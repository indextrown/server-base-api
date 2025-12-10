#!/bin/bash
# =============================================
# remote_build.sh
# chmod +x remote_build.sh
#
# 📌 원격 빌드/배포 스크립트 (tar 파일 전송)
# 1) (로컬에서 이미 생성된) Docker tar 파일 서버로 전송
#
# ▶ 실제 실행되는 명령어
#    scp server-base-api-1.0.0.tar poppang-server:/home/poppang/test/
#
# 매개변수 사용법
#    ./remote_build.sh server-base-api 1.0.0
#    ./remote_build.sh                       # 기본값 사용
# =============================================

set -e  # 에러 발생 시 즉시 종료

# =============================
# 입력 파라미터 (없으면 기본값)
# =============================
APP_NAME="${1:-server-base-api}"
VERSION="${2:-1.0.0}"
IMAGE_NAME="${APP_NAME}:${VERSION}"
IMAGE_TAR="${APP_NAME}-${VERSION}.tar"

# =============================
# 서버 정보
# =============================
SSH_HOST="poppang-server"
SERVER_DIR="/home/poppang/test"

# =============================
# 색상 출력
# =============================
GREEN="\033[0;32m"
RED="\033[0;31m"
NC="\033[0m"

# =============================
# Step 1. TAR 파일 서버 전송
# =============================
echo -e "${GREEN}🚚 Step 1/1: Docker 이미지 tar 서버로 전송 중...${NC}"
echo -e "${GREEN}   - 전송 파일: ${IMAGE_TAR}${NC}"
echo -e "${GREEN}   - 서버 경로: ${SSH_HOST}:${SERVER_DIR}/${NC}"
echo ""

# tar 파일 존재 확인
if [ ! -f "${IMAGE_TAR}" ]; then
  echo -e "${RED}❌ 오류: tar 파일을 찾을 수 없습니다: ${IMAGE_TAR}${NC}"
  echo -e "${RED}   먼저 local_build.sh 를 실행하여 tar 파일을 생성하세요.${NC}"
  exit 1
fi

scp "${IMAGE_TAR}" "${SSH_HOST}:${SERVER_DIR}/"

echo -e ""
echo -e "${GREEN}🎉 TAR 파일 전송 완료!${NC}"
echo -e "${GREEN}   - 서버에 저장됨: ${SERVER_DIR}/${IMAGE_TAR}${NC}"
echo ""

# =============================
# Step 2. 서버에서 deploy-prod.sh 실행
# =============================
echo -e "${GREEN}🚀 Step 2/2: 서버에서 deploy-prod.sh 실행 중...${NC}"
ssh ${SSH_HOST} "bash ${SERVER_DIR}/deploy-prod.sh ${SERVER_DIR}/${IMAGE_TAR} ${IMAGE_NAME}"

# =============================
# (참고) 서버에서 이미지 로드 + 실행 (현재 주석 유지)
# =============================
# echo -e "${GREEN}📦 서버에서 Docker 이미지 로드 중...${NC}"
# ssh ${SSH_HOST} <<EOF
# set -e
# cd ${SERVER_DIR}
# docker load -i ${IMAGE_TAR}
# docker stop ${APP_NAME} || true
# docker rm ${APP_NAME} || true
# docker run -d -p 5010:8080 --name ${APP_NAME} ${IMAGE_NAME}
# EOF
#
# echo -e "${GREEN}🚀 서버 배포 완료${NC}"
