FROM gradle:8.12.0-jdk21 AS build

WORKDIR /usr/src/app

COPY . /usr/src/app

RUN ./gradlew build --build-cache

FROM openjdk:21-slim AS production
ARG PAPER_URL
ARG PAPER_JAR_NAME
ENV EULA true

WORKDIR /usr/src/app

RUN useradd -m -u 1000 -o -s /bin/bash -d /usr/src/app paper
RUN chown 1000 /usr/src/app
USER 1000

COPY --chown=1000 --chmod=774 --from=build /usr/src/app/build/libs/conQest.jar /usr/src/app/plugins/conQest.jar
ADD --chown=1000 --chmod=774 $PAPER_URL /usr/src/app/
RUN mv $PAPER_JAR_NAME paper.jar
RUN echo eula=true > eula.txt


CMD ["java", "-Xms4G", "-Xmx4G", "-jar", "paper.jar", "--nogui"]