#!/usr/bin/env python
# -*- coding: utf-8 -*-

from locust import HttpLocust, TaskSet, task
import json


class StudentBehavior(TaskSet):

    headers = {'student_token': "X-Student-Token", 'app_name': "X-App-Name", 'secure_key': "X-Secure-Key"}
    headers_values = {'student_token': "", 'app_name': "dev-test", 'secure_key': "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"}
    director_token = "marcia2017"

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
                "name": "Marcia",
                "last_name": "Tejeda",
                "email": "marcia.tejeda@unq.com",
                "token": "marcia2017"
            })))
        print "Creating director with status:", response.status_code

        # save one student for test
        student_response = self.client.request(
            method="POST",
            url="/director/student",
            headers={
                "X-Director-Token": self.director_token,
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key']
            },
            data=str(json.dumps({
                "name": "student_dummy",
                "legajo": "0002",
                "email": "student_dummy@unq.com"
            })))
        print "Student successfully saved: ", student_response.status_code
        print "Student successfully saved with token: ", student_response.content
        self.headers_values['student_token'] = student_response.content

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


class StudentSurvey(HttpLocust):
    task_set = StudentBehavior
    host = "http://localhost:9090"
    min_wait = 5000
    max_wait = 9000
