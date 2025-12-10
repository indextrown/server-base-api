#!/bin/bash
# =============================================
# deploy.sh
# chmod +x deploy.sh
#
# 📌 전체 자동화 스크립트 (로컬 빌드 + 원격 전송)
# 1) local_build.sh 실행
# 2) remote_build.sh 실행
#
# 매개변수 전달
#   ./deploy.sh server-base-api 1.0.0
#   ./deploy.sh           # 기본값 사용
# =============================================

set -e

APP_NAME="${1:-server-base-api}"
VERSION="${2:-1.0.0}"

GREEN="\033[0;32m"
NC="\033[0m"

echo -e "${GREEN}🚀 전체 배포 시작 (APP=${APP_NAME}, VERSION=${VERSION})${NC}"
echo ""

# =============================
# Step 1 — local_build.sh 실행
# =============================
echo -e "${GREEN}📌 Step 1/2: 로컬 빌드 실행 (local_build.sh)${NC}"
./local_build.sh
echo ""

# =============================
# Step 2 — remote_build.sh 실행
# =============================
echo -e "${GREEN}📌 Step 2/2: 원격 배포 실행 (remote_build.sh)${NC}"
./remote_build.sh "${APP_NAME}" "${VERSION}"
echo ""

echo -e "${GREEN}🎉 완료! 전체 배포 프로세스가 성공적으로 끝났습니다.${NC}"
