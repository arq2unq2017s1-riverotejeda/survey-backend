#!/usr/bin/env python
# -*- coding: utf-8 -*-

from locust import HttpLocust, TaskSet, task
import random, json


class DirectorBehavior(TaskSet):

    headers = {'director_token': "X-Director-Token", 'app_name': "X-App-Name", 'secure_key': "X-Secure-Key"}
    headers_values = {'director_token': "marina2017", 'app_name': "dev-test", 'secure_key': "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"}
    names = ['battery', 'correct', 'horse', 'staple']

    def on_start(self):
        """ Create a director before any task in order to use the director token """
        response = self.client.request(
            method="POST",
            url="/private/director",
            headers={
                        self.headers['app_name']: self.headers_values['app_name'],
                        self.headers['secure_key']: self.headers_values['secure_key']
                    },
            data=str(json.dumps({
                "name": "Marina",
                "last_name": "Rivero",
                "email": "marina.rivero@unq.com",
                "token": "marina2017"
            })))
        print "Create; Response content:", response.content
        # self.headers_values['director_token'] = response.content

        # save one student for test
        student_response = self.client.request(
            method="POST",
            url="/director/student",
            headers={
                self.headers['director_token']: self.headers_values['director_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            },
            data=str(json.dumps({
                "name": "student_test",
                "legajo": "0001",
                "email": "student_test@unq.com"
            })))
        print "Student successfully saved: ", student_response.status_code
        print "Student successfully saved with token: ", student_response.content
        student_token = student_response.content

        # save survey for test
        survey_response = self.client.request(
            method="POST",
            url="/student/survey",
            headers={
                "X-Student-Token": student_token,
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            },
            data=str(json.dumps({
                "student_name": "student_test",
                "legajo": "0001",
                "token": student_token,
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
        print "A survey was saved with status: ", survey_response.status_code

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
                "name": "student"+str(random.randrange(0, 1001, 2)),
                "legajo": random.randrange(1000, 5001, 2),
                "email": "student"+str(random.randrange(0, 101, 2))+"@unq.com"
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
