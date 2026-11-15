# 한국관광공사 TourAPI(KorService2)에서 오늘 이후 축제를 받아 festival_info 에 저장(upsert)한다.
# 실행: python collect_festivals.py          -> 한 번 수집
#       python collect_festivals.py --loop   -> 바로 한 번 + 매일 새벽 4시 반복 (docker-compose festival-collector)
import os
import sys
import time
from datetime import date, datetime, timedelta
from urllib.parse import quote_plus

import requests
from dotenv import load_dotenv
from sqlalchemy import create_engine, text

load_dotenv()

BASE = "https://apis.data.go.kr/B551011/KorService2"
COMMON = {"MobileOS": "ETC", "MobileApp": "gajago", "_type": "json"}
RUN_HOUR = 4

UPSERT = text("""
INSERT INTO festival_info (contentid, title, addr1, zipcode, eventstartdate, eventenddate,
                           image, tel, fee, eventhomepage, overview, lat, lng)
VALUES (:contentid, :title, :addr1, :zipcode, :eventstartdate, :eventenddate,
        :image, :tel, :fee, :eventhomepage, :overview, :lat, :lng)
ON DUPLICATE KEY UPDATE title=VALUES(title), addr1=VALUES(addr1), zipcode=VALUES(zipcode),
  eventstartdate=VALUES(eventstartdate), eventenddate=VALUES(eventenddate), image=VALUES(image),
  tel=VALUES(tel), fee=COALESCE(VALUES(fee), fee),
  eventhomepage=COALESCE(VALUES(eventhomepage), eventhomepage),
  overview=COALESCE(VALUES(overview), overview), lat=VALUES(lat), lng=VALUES(lng)
""")


def to_date(s):
    return datetime.strptime(s, "%Y%m%d").date() if s and len(s) == 8 and s.isdigit() else None


def to_int(s):
    return int(s) if s and s.isdigit() else None


def to_float(s):
    try:
        return float(s) if s else None
    except ValueError:
        return None


assert to_date("20261119") == date(2026, 11, 19) and to_date("") is None and to_date("2026") is None
assert to_int("06234") == 6234 and to_int("") is None and to_int("12-3") is None
assert to_float("126.97") == 126.97 and to_float("") is None and to_float("x") is None


def call(op, **params):
    r = requests.get(f"{BASE}/{op}", params={**COMMON, "serviceKey": os.environ["TOUR_API_KEY"], **params}, timeout=30)
    r.raise_for_status()
    body = r.json()["response"]["body"]
    items = body["items"]["item"] if body.get("items") else []  # 결과 0건이면 items 가 "" 로 옴
    return items, int(body.get("totalCount", 0))


def first(op, **params):
    try:
        items, _ = call(op, **params)
        return items[0] if items else {}
    except Exception as e:  # 상세 1건 실패로 전체 수집이 멈추지 않게
        print(f"[festival] {op} {params.get('contentId')} 실패: {e}", flush=True)
        return {}


def collect():
    today = date.today().strftime("%Y%m%d")
    festivals, page = [], 1
    while True:
        items, total = call("searchFestival2", eventStartDate=today, numOfRows=100, pageNo=page)
        festivals += items
        if not items or len(festivals) >= total:
            break
        page += 1

    # 상세(소개·요금·홈페이지)는 처음 보는 행사만 요청: 재시작·매일 갱신 때 TourAPI 호출을 목록 몇 번으로 줄임
    with engine.connect() as conn:
        known = {r[0] for r in conn.execute(text("SELECT contentid FROM festival_info WHERE overview IS NOT NULL"))}

    rows, fetched = [], 0
    for f in festivals:
        cid = f["contentid"]
        if int(cid) in known:
            common, intro = {}, {}
        else:
            common = first("detailCommon2", contentId=cid)
            intro = first("detailIntro2", contentId=cid, contentTypeId=f.get("contenttypeid", "15"))
            fetched += 1
        rows.append({
            "contentid": int(cid),
            "title": f.get("title"),
            "addr1": f.get("addr1"),
            "zipcode": to_int(f.get("zipcode")),
            "eventstartdate": to_date(f.get("eventstartdate")),
            "eventenddate": to_date(f.get("eventenddate")),
            "image": f.get("firstimage") or None,
            "tel": f.get("tel") or None,
            "fee": intro.get("usetimefestival") or None,
            "eventhomepage": intro.get("eventhomepage") or common.get("homepage") or None,
            "overview": common.get("overview") or None,
            "lat": to_float(f.get("mapy")),  # TourAPI: mapy=위도, mapx=경도
            "lng": to_float(f.get("mapx")),
        })

    if rows:
        with engine.begin() as conn:
            conn.execute(UPSERT, rows)
    print(f"[festival] {datetime.now():%Y-%m-%d %H:%M} 저장 {len(rows)}건 (새 행사 상세 요청 {fetched}건)", flush=True)


def seconds_until_next_run():
    now = datetime.now()
    nxt = now.replace(hour=RUN_HOUR, minute=0, second=0, microsecond=0)
    if nxt <= now:
        nxt += timedelta(days=1)
    return (nxt - now).total_seconds()


engine = create_engine(
    f"mysql+pymysql://{os.getenv('DB_USER')}:{quote_plus(os.getenv('DB_PASSWORD', ''))}"
    f"@{os.getenv('DB_HOST')}:{os.getenv('DB_PORT', '3306')}/{os.getenv('DB_NAME')}?charset=utf8mb4"
)

if __name__ == "__main__":
    failures = 0
    while True:
        try:
            collect()
            failures = 0
        except Exception as e:
            failures += 1
            print(f"[festival] 수집 실패 ({failures}회): {e}", flush=True)
        if "--loop" not in sys.argv:
            break
        # 첫 부팅 땐 MySQL이 healthy 후에도 잠깐 재시작해 연결이 실패함 → 10분 뒤 재시도 (3번까지, API 한도 아끼기)
        time.sleep(600 if 0 < failures <= 3 else seconds_until_next_run())
