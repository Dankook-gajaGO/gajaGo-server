# AWS EC2 배포 (t3.small)

서버 1대에 MySQL·챗봇·축제 수집·Spring 4개 컨테이너가 함께 돌아가요. 앱은 `http://<퍼블릭 IP>:8080` 으로 접속해요.

## 1. EC2 만들기 (AWS 콘솔, 오른쪽 위 리전 = 내 프로젝트 리전)
새 AWS 경험(소셜 로그인 가입) 계정은 **프로젝트 리전 한 곳에만** 리소스를 만들 수 있어요. 리전 확인: AWS Settings > View all projects > Overview > Additional Info > Region (이 프로젝트는 아시아 태평양(시드니) ap-southeast-2).
1. EC2 → 인스턴스 시작
2. 이름 `gajago-server`, AMI **Ubuntu Server 24.04 LTS**, 인스턴스 유형 **t3.small**
3. 키 페어: 새로 만들기 → `gajago-key`, RSA, `.pem` → 다운로드한 파일을 안전한 곳에 보관 (다시 받을 수 없음)
4. 네트워크 설정 → 편집
   - SSH(22): 소스 **내 IP**
   - 보안 그룹 규칙 추가: 사용자 지정 TCP **8080**, 소스 **0.0.0.0/0** (앱이 어디서나 접속)
5. 스토리지 **20GiB** (gp3)
6. 인스턴스 시작 → 인스턴스 목록에서 **퍼블릭 IPv4 주소** 확인
7. (권장) 탄력적 IP 할당 → 인스턴스에 연결. 안 하면 서버를 껐다 켤 때 IP가 바뀌어 APK를 다시 빌드해야 함

## 2. 노트북에서 비밀값 올리기 + 접속 (PowerShell)
```powershell
cd "C:\Users\O2\Desktop\가자 go 클로드\gajaGo-server"
scp -i C:\경로\gajago-key.pem .env ubuntu@<퍼블릭IP>:~/.env
scp -i C:\경로\gajago-key.pem deploy/setup-ec2.sh ubuntu@<퍼블릭IP>:~/
ssh -i C:\경로\gajago-key.pem ubuntu@<퍼블릭IP>
```
`.pem` 권한 오류가 나면: 파일 속성 → 보안 → 상속 사용 안 함 → 본인 계정만 읽기 남기기.

## 3. 서버에서 실행
```bash
bash setup-ec2.sh
```
끝에 `완료! 앱 주소: http://…:8080` 이 나오면 성공. 브라우저에서 `http://<IP>:8080/api/exchange-rates` 가 열리면 바깥에서도 접속 OK.

## 이후 업데이트 (깃에 새 코드 올린 뒤)
```bash
bash ~/gajaGo-server/deploy/setup-ec2.sh
```

## 자주 쓰는 명령
- 상태: `cd ~/gajaGo-server && sudo docker compose ps`
- 로그: `sudo docker compose logs --tail 100 spring-server` (python-server, festival-collector, mysql)
- 재시작: `sudo docker compose restart`

## 비용 주의
- t3.small 은 켜 두는 동안 시간당 과금 (무료 플랜 크레딧에서 차감). 발표·시연이 끝나면 **중지**하면 과금이 멈춤 (스토리지 소액 제외)
- 탄력적 IP는 인스턴스가 꺼져 있으면 소액 과금
