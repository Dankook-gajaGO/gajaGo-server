# food_result.csv + 알러지 매핑(팀 Notion 표) -> db/init/02_food_seed.sql
# 실행: python db/make_food_seed.py  (CSV나 매핑을 바꿨을 때만 다시 실행)
import csv
from pathlib import Path

HERE = Path(__file__).parent

# 카테고리 이름은 앱이 보내는 값(selectedAllergies)과 같아야 필터가 동작함
ALLERGY = {
    "난류": ["계란", "계란흰자", "계란노른자"],
    "우유": ["우유", "버터", "생크림", "모짜렐라치즈"],
    "밀": ["만두피", "밀가루", "냉면", "국수", "밀국수", "스파게티", "우동면", "콘프레이크", "소면", "쫄면", "칼국수", "빵가루"],
    "메밀": ["메밀"],
    "견과류": ["잣", "잣가루"],
    "대두": ["간장", "국간장", "유부", "완두콩", "된장", "청국장", "순두부", "두부", "진간장", "[쇠고기양념] 간장", "풋콩", "녹두", "[불고기양념] 간장", "순창콩된장", "[비빔양념] 간장", "두반장"],
    "갑각류": ["새우젓", "새우", "생새우", "잔새우", "꽃게"],
    "해산물": ["멸치", "어묵", "굴", "젓국", "바지락", "명란", "아귀", "미더덕", "낙지", "홍합", "조갯살", "코다리", "[멸치장국] 국멸치", "맛살조개", "대구살", "대구", "까나리액젓", "고등어", "갈치", "전복", "장어", "장어대리", "장어뼈", "참치통조림", "굴소스", "청정원어장", "참치", "꽁치", "오징어"],
    "돼지고기": ["돼기고기", "돼지고기", "햄", "프랑크소시지", "베이컨", "돼지갈비", "비엔나소시지", "순대", "다진돼지고기"],
    "쇠고기": ["안심", "육수", "쇠고기", "양지머리", "다진쇠고기", "갈비", "쇠고기육수", "장조림", "소 잡뼈", "사골", "도가니", "채썬쇠고기", "쇠고기 육수", "[쇠고기양념] 다진파", "곱창", "양"],
    "닭고기": ["닭고기", "닭", "닭봉", "닭육수"],
}


def q(s):
    return "'" + s.replace("\\", "\\\\").replace("'", "''") + "'"


rows = list(csv.DictReader(open(HERE / "food_result.csv", encoding="utf-8-sig")))
foods, ingredients = [], []
for r in rows:
    foods.append(f"({int(r['food_id'])},{q(r['food_nm'])},{q(r['nation_nm'])},{q(r['ty_nm'])})")
    for name in (x.strip() for x in r["ingredient_nm"].split(",")):
        if name:
            ingredients.append(f"({int(r['food_id'])},{q(name)})")
mapping = [f"({q(cat)},{q(name)})" for cat, names in ALLERGY.items() for name in names]

# self-check: Notion 표 기준 총 101개 재료, CSV 음식 99개
assert len(mapping) == 101, len(mapping)
assert len(foods) == 99, len(foods)

SEP = ",\n"
sql = f"""-- 자동 생성 파일: db/make_food_seed.py 로 다시 만들 것 (직접 수정 금지)
SET NAMES utf8mb4;
INSERT INTO food_info (food_id, food_nm, nation_nm, ty_nm) VALUES
{SEP.join(foods)};
INSERT INTO ingredient_info (food_id, ingredient_nm) VALUES
{SEP.join(ingredients)};
INSERT INTO allergy_mapping (allergy_category, ingredient_nm) VALUES
{SEP.join(mapping)};
"""
(HERE / "init" / "02_food_seed.sql").write_text(sql, encoding="utf-8")
print(f"foods={len(foods)} ingredients={len(ingredients)} mapping={len(mapping)}")
