#!/bin/bash

echo "======= Initialize data ========"
python initialdata_save-survey.py
echo "======= Finishing initialize data ======"
echo "======= Starting locust test ======="
locust -f survey_test.py
echo "======= Finishing locust test ======"
