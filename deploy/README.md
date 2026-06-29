# 2호점 백엔드 배포 (무결성 기반 스크립트)

수동 SSH 절차를 대체하는 검증 가능한 배포 스크립트. 1호점용 stale `Jenkinsfile` 은 사용하지 않는다.

## 빌드↔릴리스 분리

| 단계 | 스크립트 | 실행 위치 | 역할 |
|---|---|---|---|
| 빌드 | `build-backend.sh` | dev 서버(3.38.203.50) | worktree 격리 클린 빌드 + 평문 자격증명 가드 + sha256 + `release.manifest` |
| 릴리스 | `release-backend.sh` | 대상 호스트 | sha256 검증 → 백업 → 스왑 → 재시작 → MANIFEST 출처 확인 |

빌드 산출물은 `Git-Commit`/`Build-Time` 이 jar MANIFEST 에 박혀 있어, git 체크아웃이 없는 운영기에서도 출처 커밋을 식별할 수 있다.

## dev 배포

```bash
# dev 서버에서
deploy/build-backend.sh origin/dev /tmp/ji9-backend-release
deploy/release-backend.sh /tmp/ji9-backend-release dev
```

## prod 배포 (2-hop)

```bash
# 1) dev 서버에서 빌드 (배포 대상 커밋 지정)
deploy/build-backend.sh <release-commit> /tmp/ji9-backend-release

# 2) 산출물을 운영기로 복사 (로컬 경유)
#    /tmp/ji9-backend-release/{pos.jar,pos.jar.sha256,release.manifest} → 운영기 동일 경로

# 3) 운영기(10.83.254.178)에서 릴리스
deploy/release-backend.sh /tmp/ji9-backend-release prod
```

## 검증·롤백

- 배포 출처 확인: `unzip -p <jar> META-INF/MANIFEST.MF | grep -E 'Git-Commit|Build-Time'`
- 롤백: `release-backend.sh` 종료 시 출력되는 `rollback-<TS>-backend-<env>/` 백업으로 복원.

## TODO

- 어드민/모바일(vue) 배포 스크립트화: `vite build --mode dev-server`(dev) / `vite build`(prod) 구분, `rsync --delete`, baked URL 검증 자동화.
- 런타임 시크릿 SSM 이관(별도 과제) 후 빌드/배포 환경변수 주입 경로 갱신.
