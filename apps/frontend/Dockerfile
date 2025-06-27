# =========================================
# 1. 의존성 설치 스테이지 (Dependency Stage)
# =========================================
FROM node:alpine AS deps

# pnpm 설치 (npm이나 yarn을 사용하면 해당 부분 수정)
RUN npm install -g pnpm

WORKDIR /app

# 루트의 package.json, pnpm-lock.yaml 등을 먼저 복사하여
# 소스코드가 아닌 의존성만 변경되었을 때 캐시를 활용
COPY ../../pnpm-lock.yaml ./
COPY ../../package.json ./
COPY ../../turbo.json ./
COPY ./package.json ./

# 의존성 설치
RUN pnpm install --frozen-lockfile


# =========================================
# 2. 빌드 스테이지 (Builder Stage)
# =========================================
FROM node:alpine AS builder

WORKDIR /app
# 이전 스테이지에서 설치한 의존성을 그대로 복사
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# 터보레포를 사용하여 프론트엔드 앱만 빌드
RUN npm install -g turbo && turbo run build --filter=frontend


# =========================================
# 3. 최종 실행 스테이지 (Runner Stage)
# =========================================
FROM node:alpine AS runner

WORKDIR /app

# 프로덕션 환경으로 설정
ENV NODE_ENV production

# standalone 모드로 빌드된 결과물들을 builder 스테이지에서 복사
# 이 결과물에는 최소한의 파일만 포함되어 있어 이미지 크기가 매우 작아집니다.
COPY --from=builder /app/apps/frontend/public ./apps/frontend/public
COPY --from=builder --chown=nextjs:nodejs /app/apps/frontend/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/apps/frontend/.next/static ./apps/frontend/.next/static

# non-root 유저로 실행하여 보안 강화
USER nextjs

EXPOSE 3000

ENV PORT 3000

# Next.js standalone 서버 실행
CMD ["node", "apps/frontend/server.js"]