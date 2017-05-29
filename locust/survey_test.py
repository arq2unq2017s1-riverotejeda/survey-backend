#!/usr/bin/env python
# -*- coding: utf-8 -*-

from locust import HttpLocust, TaskSet, task
import random, json


class StudentBehavior(TaskSet):
    # To change environment configuration
    # For docker uncomment this lines
    # app_name = "docker"
    # secure_key = "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu"

    # For local uncomment this lines
    app_name = "dev-test"
    secure_key = "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"

    headers = {'student_token': "X-Student-Token", 'app_name': "X-App-Name", 'secure_key': "X-Secure-Key"}
    headers_values = {'student_token': "", 'app_name': app_name, 'secure_key': secure_key}
    director_token = "marcia2017"

    # This value depenends on initialdata script
    student_token = "AVz5I8P2/xvUWkONfIDS4UzqNslqGZ+ucln5QAFsnU4="
    headers_values['student_token'] = student_token

    @task(1)
    def get_student(self):
        response = self.client.request(
            method="GET",
            url="/student/0002",
            headers={
                self.headers['student_token']: self.headers_values['student_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            }
        )
        print "Finish getting student with status code:", response.status_code

    @task(2)
    def save_survey(self):
        survey_response = self.client.request(
            method="POST",
            url="/student/survey",
            headers={
                self.headers['student_token']: self.headers_values['student_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            },
            data=str(json.dumps({
                "student_name": "student_test",
                "legajo": "0002",
                "token": self.headers_values['student_token'],
                "selected_subjects": [
                    {
                        "subject": "Matematica 1",
                        "status": "APPROVED"

                    },
                    {
                        "subject": "Matematica 2",
                        "status": "APPROVED"
                    },
                    {
                        "subject": "Sistemas distribuidos",
                        "status": "APPROVED"
                    },
                    {
                        "subject": "Arquitectura 1",
                        "status": "C1"
                    },
                    {
                        "subject": "Logica y programación",
                        "status": "APPROVED"
                    },
                    {
                        "subject": "Teoria de la computación",
                        "status": "C1"
                    },
                    {
                        "subject": "Probabilidad y estadistica",
                        "status": "NOT_YET"
                    },
                    {
                        "subject": "Lenguajes formales y automatas",
                        "status": "BAD_SCHEDULE"
                    }],
                "schoolYear": "201701"
            })))
        print "Finish saving survey with status code:", survey_response.status_code

    @task(3)
    def get_survey(self):
        response = self.client.request(
            method="GET",
            url="/student/survey/0002/201701",
            headers={
                self.headers['student_token']: self.headers_values['student_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            }
        )
        print "Finish getting survey with status code:", response.status_code

    @task(4)
    def get_subjects(self):
        response = self.client.request(
            method="GET",
            url="/public/subjects/201701",
            headers={
                self.headers['student_token']: self.headers_values['student_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            }
        )
        print "Finish getting subjects with status code:", response.status_code

    # Besides this is a task from director test, its necessary to generate many subjects for task 4
    @task(5)
    def save_subject(self):
        response = self.client.request(
            method="POST",
            url="/director/subject",
            headers={
                "X-Director-Token": self.director_token,
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            },
            data=str(json.dumps({
                "name": "Caracteristicas de lenguajes",
                "school_year": "201701",
                "divisions": [
                    {
                        "comision": "C1",
                        "weekdays": ["Martes de 19 a 22"],
                        "quota": "35"
                    }
                ],
                "group": "avanzada"
            })))
        print "Finish saving subject with status code:", response.status_code


class DirectorBehavior(TaskSet):
    # To change environment configuration
    # For docker uncomment this lines
    # secure_key = "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu"
    # app_name = "docker"

    # For local uncomment this lines
    secure_key = "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"
    app_name = "dev-test"

    headers = {'director_token': "X-Director-Token", 'app_name': "X-App-Name", 'secure_key': "X-Secure-Key"}
    headers_values = {'director_token': "marcia2017", 'app_name': app_name,
                      'secure_key': secure_key}

    tasks = {StudentBehavior: 4}

    @task(1)
    def save_student(self):
        response = self.client.request(
            method="POST",
            url="/director/student",
            headers={
                self.headers['director_token']: self.headers_values['director_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            },
            data=str(json.dumps({
                "name": "student" + str(random.randrange(0, 1001, 2)),
                "legajo": random.randrange(1000, 5001, 2),
                "email": "student" + str(random.randrange(0, 101, 2)) + "@unq.com"
            })))
        print "Finish saving student with status code:", response.status_code

    @task(2)
    def get_survey(self):
        response = self.client.request(
            method="GET",
            url="/director/survey/0001/201701",
            headers={
                self.headers['director_token']: self.headers_values['director_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            }
        )
        print "Finish getting survey with status code:", response.status_code

    @task(3)
    def save_subject(self):
        response = self.client.request(
            method="POST",
            url="/director/subject",
            headers={
                self.headers['director_token']: self.headers_values['director_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            },
            data=str(json.dumps({
                "name": "Caracteristicas de lenguajes",
                "school_year": "201701",
                "divisions": [
                    {
                        "comision": "C1",
                        "weekdays": ["Martes de 19 a 22"],
                        "quota": "35"
                    }
                ],
                "group": "avanzada"
            })))
        print "Finish saving subject with status code:", response.status_code


class DirectorSurvey(HttpLocust):
    task_set = DirectorBehavior
    host = "http://localhost:9090"
    min_wait = 5000
    max_wait = 9000
