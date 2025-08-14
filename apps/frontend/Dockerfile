FROM node:24-alpine

WORKDIR /app

ENV NODE_ENV=production

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --chown=nextjs:nodejs apps/frontend/.next/standalone ./
COPY --chown=nextjs:nodejs apps/frontend/public ./public
COPY --chown=nextjs:nodejs apps/frontend/.next/static ./.next/static
COPY --chown=nextjs:nodejs apps/frontend/package.json ./package.json
COPY --chown=nextjs:nodejs apps/frontend/server.js ./server.js

USER nextjs

EXPOSE 3000
ENV PORT=3000

CMD ["node", "server.js"]