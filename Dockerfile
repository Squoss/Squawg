# sources:
# https://docs.docker.com/develop/develop-images/multistage-build/
# https://nodejs.org/en/docs/guides/nodejs-docker-webapp/


FROM node:16 as react

WORKDIR /squeng/fixadat

COPY fegui/.env ./
# COPY fegui/.npmrc ./
COPY fegui/package*.json ./
COPY fegui/tsconfig.json ./
# https://docs.npmjs.com/cli/v7/commands/npm-ci
RUN npm ci

COPY fegui/public ./public
COPY fegui/src ./src
# https://create-react-app.dev/docs/adding-custom-environment-variables#linux-macos-bash
RUN INLINE_RUNTIME_CHUNK=false npm run build


FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.2_2.13.9 as play

WORKDIR /squeng/fixadat

COPY beapi/app ./app
COPY beapi/conf ./conf
COPY beapi/project ./project
COPY beapi/public ./public
COPY beapi/reinraum ./reinraum
COPY beapi/build.sbt ./
COPY --from=react /squeng/fixadat/build ./public/build
RUN sbt stage


FROM openjdk:17-slim

WORKDIR /squeng/fixadat

COPY --from=play /squeng/fixadat/target/universal/stage ./target/universal/stage

RUN groupadd -r gruppe && useradd --no-log-init -r -g gruppe benutzer
RUN chown -R benutzer:gruppe /squeng
USER benutzer

EXPOSE 8080
CMD ["target/universal/stage/bin/fixadat", "-Dpidfile.path=play.pid", "-Dhttp.port=8080"]
