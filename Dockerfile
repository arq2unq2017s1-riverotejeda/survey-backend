FROM java:8
MAINTAINER unknown
EXPOSE 9090
ADD survey.tar /
ENTRYPOINT ["/survey/bin/survey"]