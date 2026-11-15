#!/usr/bin/env bash
# EC2(Ubuntu 24.04, t3.small)에서 실행: 처음 설치 + 이후 업데이트 모두 이 스크립트 하나로.
#   사용: bash setup-ec2.sh
# 미리 할 일: 노트북에서 .env 를 서버 홈(~/.env)으로 올려 두기 (deploy/README.md 참고)
set -euo pipefail

BRANCH="${BRANCH:-main}"
REPO="https://github.com/Dankook-gajaGO/gajaGo-server.git"
DIR="$HOME/gajaGo-server"

# 1) 메모리 2GB라 Spring 빌드 중 멈추지 않게 스왑 2GB (한 번만)
if ! swapon --show | grep -q /swapfile; then
  echo "[1/5] 스왑 2GB 만드는 중"
  sudo fallocate -l 2G /swapfile
  sudo chmod 600 /swapfile
  sudo mkswap /swapfile >/dev/null
  sudo swapon /swapfile
  echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab >/dev/null
fi

# 2) Docker·Git (우분투 공식 패키지)
if ! command -v docker >/dev/null || ! docker compose version >/dev/null 2>&1; then
  echo "[2/5] Docker 설치 중"
  sudo apt-get update -y
  sudo apt-get install -y docker.io docker-compose-v2 git
  sudo systemctl enable --now docker
fi

# 3) 코드 받기 (있으면 최신으로)
echo "[3/5] 코드 받는 중 ($BRANCH)"
if [ -d "$DIR/.git" ]; then
  git -C "$DIR" fetch origin
  git -C "$DIR" checkout "$BRANCH"
  git -C "$DIR" pull --ff-only origin "$BRANCH"
else
  git clone -b "$BRANCH" "$REPO" "$DIR"
fi

# 4) 비밀값(.env): 홈에 올려 둔 파일을 옮김. 깃에는 절대 없음
if [ -f "$HOME/.env" ]; then
  mv "$HOME/.env" "$DIR/.env"
fi
if [ ! -f "$DIR/.env" ]; then
  echo "!! .env 가 없어요. 노트북에서 scp 로 ~/.env 에 올린 뒤 다시 실행하세요." >&2
  exit 1
fi
chmod 600 "$DIR/.env"

# 5) 실행 (처음엔 빌드 때문에 5~10분)
echo "[4/5] 컨테이너 빌드·실행 중 (처음엔 5~10분)"
cd "$DIR"
sudo docker compose up -d --build

echo "[5/5] 서버 응답 기다리는 중"
for _ in $(seq 1 60); do
  if curl -fs http://localhost:8080/api/exchange-rates >/dev/null; then
    IP=$(curl -fs --max-time 3 https://checkip.amazonaws.com || echo "<EC2 퍼블릭 IP>")
    echo "완료! 앱 주소: http://$IP:8080"
    sudo docker compose ps
    exit 0
  fi
  sleep 5
done
echo "!! 5분 안에 응답이 없어요. 로그 확인: sudo docker compose logs --tail 50 spring-server" >&2
exit 1
