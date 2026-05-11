# Build stage
FROM node:22-alpine AS build

WORKDIR /app

# Install dependencies based on package manifest only.
COPY package.json package-lock.json* ./
RUN npm install --production=false

# Copy source files and build static assets.
COPY . .
RUN npm run build

# Production stage
FROM nginx:stable-alpine AS production

# Remove default nginx static files and copy build output.
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port and run nginx in foreground.
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
