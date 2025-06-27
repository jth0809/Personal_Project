# =================================================================
# Stage 1: 의존성 설치 (Dependency Installation)
#   - CI/CD 환경에 최적화된 'npm ci'를 사용하여 안정적으로 패키지를 설치합니다.
# =================================================================
FROM node:alpine AS deps

# 알파인 리눅스에서 필요한 라이브러리 설치
RUN apk add --no-cache libc6-compat

WORKDIR /app

# 의존성 캐싱을 위해 package.json과 package-lock.json 파일들을 먼저 복사합니다.
# 이 파일들이 변경되지 않으면, 이 스테이지는 캐시되어 'npm ci' 과정을 건너뜁니다.
COPY ../../package.json ./
COPY ../../package-lock.json ./
COPY ../../turbo.json ./
COPY ./package.json ./

# 'npm ci'는 package-lock.json을 기반으로 패키지를 정확하고 빠르게 설치합니다.
RUN npm ci


# =================================================================
# Stage 2: 애플리케이션 빌드 (Application Build)
#   - 소스코드를 빌드하여 프로덕션용 결과물을 생성합니다.
# =================================================================
FROM node:alpine AS builder

WORKDIR /app

# 이전 스테이지에서 설치한 node_modules를 그대로 가져옵니다.
COPY --from=deps /app/node_modules ./node_modules
# 전체 소스코드를 복사합니다.
COPY . .

# npx를 통해 프로젝트에 설치된 turbo를 사용하여 프론트엔드 앱만 빌드합니다.
# 이렇게 하면 'command not found' 오류를 방지할 수 있습니다.
RUN npx turbo run build --filter=frontend


# =================================================================
# Stage 3: 최종 실행 이미지 (Final Production Image)
#   - 실제 서비스를 실행할 최소한의 파일만 포함하여 가볍고 안전한 이미지를 만듭니다.
# =================================================================
FROM node:alpine AS runner

WORKDIR /app

# 프로덕션 환경으로 설정합니다.
ENV NODE_ENV production

# 보안을 위해 non-root 유저를 생성합니다.
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# next.config.js의 'output: "standalone"' 설정으로 생성된 최적화된 결과물만 복사합니다.
COPY --from=builder /app/apps/frontend/public ./apps/frontend/public
COPY --from=builder --chown=nextjs:nodejs /app/apps/frontend/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/apps/frontend/.next/static ./apps/frontend/.next/static

# non-root 유저로 전환하여 컨테이너 보안을 강화합니다.
USER nextjs

# 애플리케이션이 3000번 포트를 사용함을 명시합니다.
EXPOSE 3000

# 환경변수로 포트를 설정합니다.
ENV PORT 3000

# Next.js standalone 서버를 실행합니다.
CMD ["node", "apps/frontend/server.js"]
