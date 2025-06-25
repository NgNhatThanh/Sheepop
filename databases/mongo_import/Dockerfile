FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm i
COPY . .
CMD ["node", "app.js"]