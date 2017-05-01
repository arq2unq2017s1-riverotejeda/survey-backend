FROM java:8

RUN wget -q https://services.gradle.org/distributions/gradle-3.3-bin.zip \
    && unzip gradle-3.3-bin.zip -d /opt \
    && rm gradle-3.3-bin.zip

ENV GRADLE_HOME /opt/gradle-3.3
ENV PATH $PATH:/opt/gradle-3.3/bin

MAINTAINER unq
COPY [".", "/usr/src/survey"]
WORKDIR /usr/src/survey
RUN gradle stage
RUN java -jar -Denv=prod build/libs/*.jar
EXPOSE 9090
ENTRYPOINT ["/survey/bin/survey"]
