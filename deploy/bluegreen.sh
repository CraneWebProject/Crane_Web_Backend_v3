#!/bin/sh
# Eureka 기반 블루그린 배포
#
# 사용법: sh deploy/bluegreen.sh <서비스명> <앱 포트> <호스트 포트> <이미지> [docker run 추가 인자...]
# 필요 환경변수: EUREKA_URL (예: http://host:8761/eureka/)
#
# 순서: 새 색 컨테이너 기동 → Eureka UP 확인(헬스체크) → 구 인스턴스 OUT_OF_SERVICE 후 대기 → 구 컨테이너 정리
# 호스트 포트: blue = <호스트 포트>, green = <호스트 포트> + 10000
# 게이트웨이는 lb://(Eureka)로 라우팅하므로, Eureka 등록 상태를 바꾸는 것만으로 트래픽이 전환된다.
set -eu

SERVICE=$1
APP_PORT=$2
HOST_PORT=$3
IMAGE=$4
shift 4

APP=$(echo "$SERVICE" | tr '[:lower:]' '[:upper:]') # Eureka 앱 이름은 대문자
EUREKA="${EUREKA_URL%/}"
HEALTH_TIMEOUT=${HEALTH_TIMEOUT:-120} # 기동이 느린 환경이면 늘린다
# ponytail: 고정 대기. Eureka 서버 응답 캐시(30s) + 게이트웨이 레지스트리 갱신(30s) + LoadBalancer 캐시(35s)의 근사치.
# 게이트웨이 쪽 캐시 주기를 줄이면 이 값도 줄일 수 있다.
DRAIN_SECONDS=${DRAIN_SECONDS:-90}

running() { docker ps --format '{{.Names}}' | grep -qx "$1"; }

if running "$SERVICE-blue"; then
    OLD_NAME=$SERVICE-blue; NEW=green
elif running "$SERVICE-green"; then
    OLD_NAME=$SERVICE-green; NEW=blue
elif running "$SERVICE"; then
    OLD_NAME=$SERVICE; NEW=green # 최초 전환: 색 없는 기존 컨테이너가 기본 포트를 쓰고 있음
else
    OLD_NAME=""; NEW=blue
fi
NEW_NAME=$SERVICE-$NEW
if [ "$NEW" = blue ]; then NEW_PORT=$HOST_PORT; else NEW_PORT=$((HOST_PORT + 10000)); fi

echo "[1/4] 새 컨테이너 기동: $NEW_NAME (host port $NEW_PORT)"
docker rm -f "$NEW_NAME" >/dev/null 2>&1 || true # 이전 실패 배포의 잔여 컨테이너
# 호스트 포트는 127.0.0.1 에만 연다. 서비스는 게이트웨이가 넣어 주는 X-Authenticated-User 헤더를 신뢰하므로
# 외부에서 직접 접근하면 인증을 우회할 수 있다. 게이트웨이는 Eureka 에 등록된 컨테이너 IP 로 접근하므로 라우팅에는 영향이 없다.
docker run -d --name "$NEW_NAME" \
    -p "127.0.0.1:$NEW_PORT:$APP_PORT" \
    --restart unless-stopped \
    --memory 512m --cpus 1.0 \
    -e JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=60.0 \
    -e EUREKA_INSTANCE_INSTANCEID="$NEW_NAME" \
    "$@" "$IMAGE"

echo "[2/4] 헬스체크: Eureka 에 $NEW_NAME 이 UP 으로 등록될 때까지 대기 (최대 ${HEALTH_TIMEOUT}s)"
waited=0
until curl -fs -H 'Accept: application/json' "$EUREKA/apps/$APP/$NEW_NAME" | grep -q '"status":"UP"'; do
    if [ "$waited" -ge "$HEALTH_TIMEOUT" ]; then
        echo "헬스체크 실패: 새 컨테이너를 제거하고 기존 컨테이너를 유지합니다."
        docker logs --tail 50 "$NEW_NAME" || true
        docker rm -f "$NEW_NAME"
        exit 1
    fi
    sleep 5
    waited=$((waited + 5))
done

if [ -n "$OLD_NAME" ]; then
    echo "[3/4] 트래픽 전환: $NEW_NAME 을 제외한 $APP 인스턴스를 OUT_OF_SERVICE 로 변경 후 ${DRAIN_SECONDS}s 대기"
    for id in $(curl -fs -H 'Accept: application/json' "$EUREKA/apps/$APP" | grep -o '"instanceId":"[^"]*"' | cut -d'"' -f4); do
        [ "$id" = "$NEW_NAME" ] || curl -fs -X PUT "$EUREKA/apps/$APP/$id/status?value=OUT_OF_SERVICE"
    done
    sleep "$DRAIN_SECONDS"

    echo "[4/4] 구 컨테이너 정리: $OLD_NAME"
    docker stop "$OLD_NAME"
    docker rm "$OLD_NAME"
fi

echo "배포 완료: $NEW_NAME (host port $NEW_PORT)"
