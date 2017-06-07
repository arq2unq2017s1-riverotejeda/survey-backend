FROM hakobera/locust

ADD . /test
ENV SCENARIO_FILE /test/locustfile.py
ENV LOCUST_MODE=standalone
ENV TARGET_URL=http://app:9090/
