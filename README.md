# gajaGo-server

## 프로젝트 소개
- 외국인 관광객을 위한 여행 지원 서비스

### 기능
- 알러지 정보를 고려한 음식 추천
- AI 기반 교통 경로 추천
- 축제 및 행사 정보 제공

## 시스템 아키텍처
React Native
↓
Spring Boot
↓
FastAPI
↓
Gemini API / Tmap API

## 기술 스택

### Backend
- Spring Boot
- JPA
- MySQL
- Spring Security

### AI Server
- FastAPI
- Gemini API

### Frontend
- React Native

### Infra
- Docker
- Docker Compose

## 주요 기능

### 교통 챗봇

- Gemini를 활용한 출발지/목적지 추출
- Tmap POI API를 통한 위치 검증
- Tmap 대중교통 API 기반 경로 탐색
- 사용자 상황을 고려한 경로 추천

### 알러지 음식 필터링

- 음식-재료-알러지 구조 설계
- 알러지별 재료 매핑 테이블 구축
- 섭취 가능한 음식 목록 제공

### 축제/행사 정보

- 한국관광공사 API 활용
- 데이터 수집 및 DB 저장
- 조건별 조회 기능 제공

## 서버 실행 방법
### 사전 준비
1. Docker Desktop : 각 OS(Windows/Mac/Linux)에 맞는 버전을 설치하고 실행
2. Git : 소스 코드 클론을 위해 설치 되어 있어야 함

### 실행
1. 프로젝트 코드 가져오기
터미널을 열고, 프로젝트를 저장할 폴더로 이동한 뒤 아래 명령어 입력
git clone https://github.com/Dankook-gajaGO/gajaGo-server.git

2. 환경변수 (.env) 설정
프로젝트 루트 폴더(가장 상위 폴더)에 .env 파일을 생성하고, 서버 실행에 필요한 API키 및 데이터베이스 설정 정보를 입력

3. 서버 실행
도커 데스크톱이 켜져 있는 상태에서, 터미널에 다음 명령어를 입력
docker-compose up --build
성공적으로 실행되면, 터미널에 스프링 부트 로고와 함께 Started... 로그가 출력됨

4. "http://localhost:8080/swagger-ui.html"에 접속하면 API들 테스트 가능

5. 실행 및 종료 방법
Docker Desktop에서 종료 버튼 누르면 서버 종료됩니다.
다음에 실행할 때 Docker Desktop에서 시작 버튼 눌러서 시작 가능하고 위의 방법에서 3번 으로 하셔도 됩니다.

### 추가 사항
#### 네트워크 및 보안 설정(API 연동 시 참고)
- CORS 설정 : 현재 서버는 모든 출처에서의 요청을 허용 중
- 안드로이드 앱에서 API 호출 시, localhost 대신 본인 컴퓨터의 사설 IP 주소를 사용해야 함
- 노트북에서 명령 프롬프트(CMD)를 열고 ipconfig를 쳐서 나오는 IPv4주소(내 컴퓨터의 IP 주소)를 복사 → 앱 코드 내 API 주소를 http://[아까 나온 주소]:8080으로 수정하면 연결 가능
- 연결 오류 시 노트북과 안드로이드 앱이 동일한 와이파이에 연결되었는지 확인 필요
