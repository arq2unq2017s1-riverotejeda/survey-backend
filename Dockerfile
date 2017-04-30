FROM java:8
MAINTAINER unknown
COPY [".", "/usr/src/survey"]
WORKDIR /usr/src/survey
RUN ./gradlew stage
RUN java -jar build/libs/survey-all.jar
EXPOSE 9090
ENTRYPOINT ["/survey/bin/survey"]
