#!/bin/bash
# =============================================
# local_build.sh
# chmod +x local_build.sh
#
# 📌 로컬 빌드 전용 스크립트
# 1) Gradle로 JAR 빌드
# 2) Docker 이미지 빌드 (linux/amd64)
# 3) Docker 이미지 tar 파일 생성
#
# ▶ 실제 실행되는 명령어
#    ./gradlew clean bootJar
#    docker buildx build --platform linux/amd64 -t server-base-api:1.0.0 --load .
#    docker tag server-base-api:1.0.0 server-base-api:latest   # 선택
#    docker save -o server-base-api-1.0.0.tar server-base-api:1.0.0
# =============================================

set -e  # 에러 발생 시 즉시 종료

# =============================
# 환경 변수 (Makefile과 동일)
# =============================
APP_NAME="server-base-api"
VERSION="1.0.0"
IMAGE_NAME="${APP_NAME}:${VERSION}"
IMAGE_TAR="${APP_NAME}-${VERSION}.tar"

# =============================
# 색상 출력
# =============================
GREEN="\033[0;32m"
RED="\033[0;31m"
NC="\033[0m"

# =============================
# Step 1. Gradle 빌드
# =============================
echo -e "${GREEN}📌 Step 1/3: Gradle Build - bootJar 실행 중...${NC}"
./gradlew clean bootJar

echo -e "${GREEN}✅ JAR 빌드 완료: build/libs/*.jar${NC}"
echo ""

# =============================
# Step 2. Docker 이미지 빌드
# =============================
echo -e "${GREEN}📌 Step 2/3: Docker 이미지 빌드 중...${NC}"
echo -e "${GREEN}   - 이미지 이름: ${IMAGE_NAME}${NC}"
echo ""

docker buildx build --platform linux/amd64 -t "${IMAGE_NAME}" --load .

# 선택: latest 태그 추가 가능
# docker tag "${IMAGE_NAME}" "${APP_NAME}:latest"

echo -e "${GREEN}🎉 Docker 이미지 빌드 완료!${NC}"
echo ""

# =============================
# Step 3. Docker 이미지 tar 파일로 저장
# =============================
echo -e "${GREEN}📌 Step 3/3: Docker 이미지 tar 파일 생성 중...${NC}"
echo -e "${GREEN}   - 출력 파일: ${IMAGE_TAR}${NC}"

docker save -o "${IMAGE_TAR}" "${IMAGE_NAME}"

echo ""
echo -e "${GREEN}🎉 완료! 다음 파일이 생성되었습니다:${NC}"
echo -e "${GREEN}   - Docker Image: ${IMAGE_NAME}${NC}"
echo -e "${GREEN}   - TAR 파일: ${IMAGE_TAR}${NC}"
echo ""
