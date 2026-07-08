# ---------- Build stage ----------
FROM node:20-alpine AS build
WORKDIR /app

# Instala dependencias aproveitando o cache do Docker
COPY package.json package-lock.json ./
RUN npm ci

# Builda o frontend (gera a pasta dist/)
COPY . .

# ARG injetado no build do Vite (import.meta.env.VITE_API_URL)
ARG VITE_API_URL=http://localhost:8080/api
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# ---------- Runtime stage ----------
FROM nginx:alpine

# Serve o build estatico na porta 5173
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 5173