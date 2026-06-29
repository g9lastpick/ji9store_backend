#!/usr/bin/env bash
# =============================================================================
# 2호점 백엔드 릴리스(무결성 검증 → 백업 → 스왑 → 재시작 → 검증) — 대상 호스트에서 실행
# -----------------------------------------------------------------------------
# build-backend.sh 산출물(pos.jar + sha256 + release.manifest)을 받아 배포한다.
#   - dev  : dev 서버(3.38.203.50)에서 build-backend.sh 직후 그대로 실행
#   - prod : dev 산출물을 운영기(10.83.254.178)로 scp 한 뒤 운영기에서 실행 (2-hop)
#
# 사용:  deploy/release-backend.sh <ART_DIR> [dev|prod]
#   ART_DIR  산출 디렉토리 (pos.jar, pos.jar.sha256, release.manifest 포함)
#   ENV      dev(기본) | prod
# =============================================================================
set -euo pipefail

ART_DIR="${1:?artifact 디렉토리 필요 (build-backend.sh 산출)}"
ENVN="${2:-dev}"

case "$ENVN" in
  dev)  UPLOAD="$HOME/2nd-app/spring-upload/pos.jar";                          CONTAINER="2nd-spring-pos";;
  prod) UPLOAD="$HOME/app/spring-upload/pos_back_new-0.0.1-SNAPSHOT.jar";      CONTAINER="spring-pos";;
  *) echo "[ERROR] ENV 는 dev|prod"; exit 1;;
esac
JAR_BASENAME="$(basename "$UPLOAD")"

# 1) 무결성 검증 — 빌드 호스트에서 만든 sha256 과 일치해야 진행
echo "[verify] sha256 검증..."
( cd "$ART_DIR" && sha256sum -c pos.jar.sha256 )
echo "--- release.manifest ---"; cat "$ART_DIR/release.manifest"; echo "-------------------------"

# 2) 백업 (현 jar + docker ps + 배포 매니페스트)
TS="$(date +%Y%m%d-%H%M%S)"
BK="$HOME/rollback-$TS-backend-$ENVN"
mkdir -p "$BK"
if [ -f "$UPLOAD" ]; then cp "$UPLOAD" "$BK/"; echo "[backup] 현 jar → $BK/$JAR_BASENAME"; fi
docker ps --format '{{.Names}}\t{{.Status}}' > "$BK/docker-ps.txt" 2>/dev/null || true
cp "$ART_DIR/release.manifest" "$BK/deployed.manifest"

# 3) 스왑 + 재시작
cp "$ART_DIR/pos.jar" "$UPLOAD"
echo "[swap] $UPLOAD 교체 완료, $CONTAINER 재시작..."
docker restart "$CONTAINER" >/dev/null

# 4) 기동 검증 (~최대 90s)
ok=0
for _ in $(seq 1 30); do
  sleep 3
  if docker logs --since 120s "$CONTAINER" 2>&1 | grep -q "Started PosApplication"; then ok=1; break; fi
done
if [ "$ok" = 1 ]; then echo "[OK] Started PosApplication 확인"; else
  echo "[WARN] 기동 로그 미확인 — 수동 점검 필요 (docker logs $CONTAINER)"; fi

# 5) 배포된 jar 출처 식별 (운영기 git 없음 → MANIFEST 로 확인)
echo "--- 배포된 jar MANIFEST ---"
docker exec "$CONTAINER" sh -c "unzip -p /app/$JAR_BASENAME META-INF/MANIFEST.MF 2>/dev/null | grep -E 'Git-Commit|Build-Time'" \
  || echo "(컨테이너에 unzip 없음 — 호스트에서: unzip -p $UPLOAD META-INF/MANIFEST.MF | grep Git-Commit)"

echo
echo "[DONE] 롤백 명령: cp '$BK/$JAR_BASENAME' '$UPLOAD' && docker restart $CONTAINER"
