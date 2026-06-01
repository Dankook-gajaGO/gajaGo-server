# gajaGo-server

사전 준비
1. Docker Desktop : 각 OS(Windows/Mac/Linux)에 맞는 버전을 설치하고 실행
2. Git : 소스 코드 클론을 위해 설치 되어 있어야 함

실행 방법
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
