#!/usr/bin/env bash
# =============================================================================
# 2호점 백엔드 클린 빌드 + 무결성 산출 (dev 서버 3.38.203.50 에서 실행)
# -----------------------------------------------------------------------------
# - origin 의 지정 커밋을 git worktree 로 격리 체크아웃해 빌드 (작업트리 WIP 유출 방지)
# - gradle 이 verifyNoPlaintextCredentials 가드를 dependsOn 으로 실행 (평문 자격증명 차단)
# - 산출 jar 의 MANIFEST(Git-Commit/Build-Time) 확인 + sha256 + release.manifest 기록
# - dev / prod 양쪽 릴리스가 공용으로 쓰는 "신뢰 가능한 산출물"을 만든다.
#
# 사용:  deploy/build-backend.sh [GIT_REF] [OUT_DIR]
#   GIT_REF  배포 대상 커밋/브랜치 (기본: origin/dev)
#   OUT_DIR  산출 디렉토리           (기본: /tmp/ji9-backend-release)
#
# 산출:  $OUT_DIR/{pos.jar, pos.jar.sha256, release.manifest}
# =============================================================================
set -euo pipefail

REPO_DIR="${REPO_DIR:-$HOME/2nd-app/pos_back_new}"
GIT_REF="${1:-origin/dev}"
OUT_DIR="${2:-/tmp/ji9-backend-release}"
JAR_NAME="pos_back_new-0.0.1-SNAPSHOT.jar"

# 호스트 JDK 빌드: git 이 있어야 MANIFEST Git-Commit 캡처됨.
# /home/ubuntu/.gradle-ms 는 과거 docker(root) 빌드로 root 소유 → 호스트 빌드는 기본 ~/.gradle 사용.
unset GRADLE_USER_HOME

cd "$REPO_DIR"
git fetch origin --quiet
COMMIT="$(git rev-parse --short=12 "$GIT_REF")"
echo "[build] REPO=$REPO_DIR  REF=$GIT_REF ($COMMIT)"

WT="$(mktemp -d /tmp/ji9-build-XXXXXX)"
cleanup() {
  git -C "$REPO_DIR" worktree remove --force "$WT" 2>/dev/null || true
  git -C "$REPO_DIR" worktree prune
}
trap cleanup EXIT

git worktree add --detach "$WT" "$GIT_REF"
cd "$WT"

# 클린 빌드 (가드 미통과 시 여기서 실패)
./gradlew clean bootJar -x test --no-daemon --console=plain

ART="$WT/build/libs/$JAR_NAME"
[ -f "$ART" ] || { echo "[ERROR] jar 산출 실패: $ART"; exit 1; }

# 추적성 검증 — MANIFEST 커밋이 빌드 ref 와 일치하고 worktree 가 clean 인지
MF_COMMIT="$(unzip -p "$ART" META-INF/MANIFEST.MF | sed -n 's/^Git-Commit: //p'  | tr -d '\r')"
MF_STATE="$(unzip -p "$ART"  META-INF/MANIFEST.MF | sed -n 's/^Git-State: //p'   | tr -d '\r')"
MF_TIME="$(unzip -p "$ART"   META-INF/MANIFEST.MF | sed -n 's/^Build-Time: //p'  | tr -d '\r')"
[ "$MF_COMMIT" = "$COMMIT" ] || echo "[WARN] MANIFEST Git-Commit($MF_COMMIT) != ref($COMMIT)"
[ "$MF_STATE"  = "clean" ]   || echo "[WARN] Git-State=$MF_STATE (worktree 비청결?)"

mkdir -p "$OUT_DIR"
cp "$ART" "$OUT_DIR/pos.jar"
( cd "$OUT_DIR" && sha256sum pos.jar > pos.jar.sha256 )
SHA="$(awk '{print $1}' "$OUT_DIR/pos.jar.sha256")"

cat > "$OUT_DIR/release.manifest" <<EOF
artifact=pos.jar
git_commit=$MF_COMMIT
git_state=$MF_STATE
git_ref=$GIT_REF
build_time=$MF_TIME
sha256=$SHA
built_by=$(whoami)@$(hostname)
EOF

echo "[OK] 무결성 산출 완료 → $OUT_DIR"
cat "$OUT_DIR/release.manifest"
