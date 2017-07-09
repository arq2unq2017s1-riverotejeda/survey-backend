#!/bin/bash
mongo 172.18.0.2:27017/unq --eval "db.dropDatabase()"
echo "======= Initialize data ========"
python initialdata_save-survey.py
echo "======= Finishing initialize data ======"
echo "======= Starting locust test ======="
locust --host=http://localhost:80 -f save-survey_test.py
echo "======= Finishing locust test ======"
