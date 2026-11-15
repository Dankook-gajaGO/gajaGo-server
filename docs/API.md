# 가자GO API 명세 (현재 기준)

처음 기획 명세(회원·음식·축제·교통 챗봇·편의시설, 8개 + 파이썬 2개)와 비교해 바뀐 점을 함께 적었습니다.

- 기본 주소: `http://<서버>:8080`
- 인증: 로그인 응답의 JWT를 `Authorization: Bearer <토큰>` 으로 보냄 (유효기간 7일)
- **공개** = 비회원도 사용 가능, **회원** = 토큰 필요 (본인 데이터만 조회·수정)
- 오류 응답: `{"message": "..."}` + 상태 코드 (400 입력 오류, 401/403 인증, 404 없음, 502 외부 API 실패)

## 1. 처음 명세 대비 바뀐 점 요약

| 구분 | 내용 |
| --- | --- |
| 유지 | 회원가입·로그인, 음식 필터, 축제 목록·상세, 교통 챗봇 2단계, 편의시설 — 주소와 기본 흐름 그대로 |
| 응답 확장 | 축제 상세에 `lat`, `lng` 추가 (지도·길찾기용) / 음식 응답에 `foods`(음식별 재료 목록) 추가 |
| 요청 확장 | 교통 챗봇 첫 요청에 현재 위치(`lat`,`lng`)와 정해진 목적지(`destName` 등) 선택 입력 추가 |
| 접근 변경 | 교통 챗봇을 비회원도 사용 가능 (회원이면 길찾기 기록 저장) |
| 신규 | 내 알러지 저장, 저장한 행사, 일정·캘린더, 길찾기 기록, 여행 가계부(지출·여행·환율) |
| 편의시설 | 카테고리에 약국·지하철역·환전소 추가 (사물함은 데이터 부정확으로 제외) |
| 인프라 | AWS RDS → 서버 1대 Docker Compose 안의 MySQL, 축제 데이터 자동 수집(매일 04:00), Gemini 모델 자동 전환 |
| 인증 | JWT 30분 → 7일, 잘못된 비밀번호·중복 아이디가 403으로 보이던 문제 수정(정확한 메시지 반환) |

## 2. 엔드포인트

### 회원
| 메서드 | 경로 | 접근 | 설명 |
| --- | --- | --- | --- |
| POST | `/api/member/signup` | 공개 | `{loginId, password, nickname, email}` → 성공 메시지. 아이디 중복이면 400 |
| POST | `/api/member/login` | 공개 | `{loginId, password}` → JWT 문자열 |
| GET | `/api/member/allergies` | 회원 | **신규** `{allergies: [..]}` 내 알러지 |
| PUT | `/api/member/allergies` | 회원 | **신규** `{allergies: [..]}` 저장 |

### 음식
| 메서드 | 경로 | 접근 | 설명 |
| --- | --- | --- | --- |
| POST | `/api/food/safeFoodList` | 공개 | `{selectedAllergies: [..]}` → `{categories: [{category, foodNames, foods: [{foodId, foodName, category, ingredients}]}]}` |

### 축제·행사
| 메서드 | 경로 | 접근 | 설명 |
| --- | --- | --- | --- |
| GET | `/api/festival/festivalList?months=N` | 공개 | 진행 중 + N개월 안에 시작하는 행사 |
| POST | `/api/festival/festivalDetail?contentId=` | 공개 | 상세 (+ **`lat`, `lng`**) |
| GET | `/api/saved-festivals` | 회원 | **신규** 저장한 행사 목록 |
| POST / DELETE | `/api/saved-festivals/{contentId}` | 회원 | **신규** 저장 / 저장 취소 |

### 일정·캘린더 (신규)
| 메서드 | 경로 | 접근 | 설명 |
| --- | --- | --- | --- |
| GET | `/api/schedules?date=YYYY-MM-DD` | 회원 | 그날 일정 |
| GET | `/api/schedules/month?year=&month=` | 회원 | 한 달 일정 (캘린더 표시) |
| POST | `/api/schedules/items` | 회원 | `{date, type(FESTIVAL·PLACE·TRANSPORT·MEMO·CUSTOM), title, memo, startTime, endTime, festivalContentId, placeName, address, lat, lng}` |
| PUT / DELETE | `/api/schedules/items/{itemId}` | 회원 | 수정 / 삭제 |

### 교통 챗봇
| 메서드 | 경로 | 접근 | 설명 |
| --- | --- | --- | --- |
| POST | `/api/transport/InitialRequest` | 공개(**변경**) | `{message, lat?, lng?, destName?, destAddress?, destLat?, destLng?}` → `{requestId, departurecandidates, destinationcandidates}`. 출발지를 말하지 않으면 현재 위치, 행사에서 길찾기면 행사 좌표를 그대로 사용 |
| POST | `/api/transport/routes` | 공개(**변경**) | `{requestId, selectedDeparture, selectedDestination}` → `{requestId, routes: [{id, reason, transfers, walkingDistance, rank, travelTime, fare, detail}]}`. 회원 요청은 본인 것만 조회 |
| GET / POST / DELETE | `/api/transport/history` | 회원 | **신규** 최근 길찾기 기록 조회 / 저장 / 전체 삭제 |

### 편의시설
| 메서드 | 경로 | 접근 | 설명 |
| --- | --- | --- | --- |
| GET | `/api/facilities/nearby?lat=&lng=&radius=3&offset=0&size=10&categories=` | 공개 | 카테고리: 은행, ATM, 편의점, 화장실, **약국, 지하철역, 환전소** (없으면 환전소 제외 전체) |

### 여행 가계부 (신규)
| 메서드 | 경로 | 접근 | 설명 |
| --- | --- | --- | --- |
| GET / POST | `/api/expenses` | 회원 | 지출 목록 / 추가 `{spentOn, title, amount, category}` |
| PUT / DELETE | `/api/expenses/{id}` | 회원 | 수정 / 삭제 (본인 것만) |
| GET / POST | `/api/trips` | 회원 | 여행 목록 / 추가 `{name, startDate, endDate}` |
| DELETE | `/api/trips/{id}` | 회원 | 삭제 (지출은 날짜로 묶여 있어 지워지지 않음) |
| GET | `/api/exchange-rates` | 공개 | 원화 기준 주요 10개 통화 환율 (open.er-api.com, 서버 12시간 캐시) |

## 3. 파이썬 서버 (내부 전용, 밖에서 접근 불가)
| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| POST | `/api/v1/extract-userinput` | `{user_input}` → Gemini로 출발지·목적지·주의사항 추출. 실패 시 502 (**변경**: 예전엔 빈 결과로 진행) |
| POST | `/api/v1/routes/{request_id}` | TMAP 경로 3개 + Gemini 추천 이유·순위 생성 후 DB 저장 |

- Gemini 모델: `GEMINI_MODELS` 순서대로 시도하고, 하루 한도 초과·혼잡(429/503)이면 다음 모델로 자동 전환
- 별도 컨테이너 `festival-collector`: TourAPI에서 축제 수집 (시작할 때 1회 + 매일 04:00)

## 4. 시스템 구성 (처음 기획 대비)
| 항목 | 처음 | 현재 |
| --- | --- | --- |
| DB | AWS RDS MySQL | Docker Compose 안의 MySQL 8.4 (처음 실행 시 테이블·음식 데이터 자동 입력) |
| 축제 데이터 | 한 번 적재 | TourAPI 자동 수집·갱신 |
| 배포 | 개발 노트북 IP | 서버 1대(EC2)에서 4개 컨테이너, 외부엔 8080만 공개 |
| 외부 API | Gemini, TMAP | Gemini, TMAP, TourAPI, 환율(open.er-api.com) |
