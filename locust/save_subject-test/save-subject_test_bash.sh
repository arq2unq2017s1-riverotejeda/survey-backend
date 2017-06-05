#!/bin/bash

echo "======= Initialize data ========"
python initialdata_save-subject.py
echo "======= Finishing initialize data ======"
echo "======= Starting locust test ======="
locust --host=http://localhost:9090 -f save-subject_test.py
echo "======= Finishing locust test ======"
