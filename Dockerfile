FROM gradle:8.12.0-jdk21 AS build

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN rm -rf server
RUN gradle createServer --build-cache

FROM openjdk:21-slim AS production
ENV EULA true

WORKDIR /usr/src/app

RUN useradd -m -u 1000 -o -s /bin/bash -d /usr/src/app paper
RUN chown -R 1000 /usr/src/app
USER 1000

COPY --chown=1000 --chmod=774 --from=build /usr/src/app/server /usr/src/app
RUN rm -rf cache

CMD ["java", "-Xms4G", "-Xmx4G", "-jar", "paper.jar", "--nogui"]