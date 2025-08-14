FROM node:24-alpine

WORKDIR /app

ENV NODE_ENV=production

RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

COPY --chown=nextjs:nodejs apps/frontend/.next/standalone ./
COPY --chown=nextjs:nodejs apps/frontend/public ./public
COPY --chown=nextjs:nodejs apps/frontend/.next/static ./.next/static

USER nextjs

EXPOSE 3000
ENV PORT=3000

CMD ["node", "server.js"]
