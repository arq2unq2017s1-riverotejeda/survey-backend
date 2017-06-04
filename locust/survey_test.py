#!/usr/bin/env python
# -*- coding: utf-8 -*-

from locust import HttpLocust, TaskSet, task
import random, json, string


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

    tasks = {StudentBehavior: 6}
    n = 0

    @task(1)
    def save_survey(self):
        global n
        n = n+1
        # Guardo estudiante
        student_name =  ''.join(random.choice(string.lowercase) for i in range(7))
        legajo = n
        # randint(0,2000000)
        email = ''.join(random.choice(string.lowercase) for i in range(7)) + '@' + ''.join(random.choice(string.lowercase) for i in range(4)) + '.' + ''.join(random.choice(string.lowercase) for i in range(3))
        student = {
            'name': student_name,
            'legajo' : legajo,
            'email': email
        }
        response = self.client.post(
            "director/student",
            data=None,
            json=student,
            headers={
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key'],
                "Content-Type": "application/json",
                self.headers['director_token']: self.headers_values['director_token']
            },
        )

        token = response.text

        # Traigo todas las materias
        year = '201301'
        response = self.client.get(
            "public/subjects/"+year,
            data=None,
            json=None,
            headers={
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key'],
                "Content-Type": "application/json"
            },
        )
        subjects = response.json()
        data_string = json.dumps(subjects)

        decoded = json.loads(data_string)

        selectedSubjects = []

        #Guardo encuesta
        for s in subjects:
            name = s["subject_name"]
            option = s["general_options"][0]
            ss = {'subject' : name, 'status' : option}
            # encode = json.dumps(s)
            selectedSubjects.append(ss)

        encuesta = {
            'student_name': student_name,
            'legajo' : legajo,
            'token' : token,
            'selected_subjects' : selectedSubjects,
            'school_year' : year
        }

        # print encuesta
        response = self.client.post(
            "student/survey",
            data=None,
            json=encuesta,
            headers={
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key'],
                "Content-Type": "application/json",
                "X-Student-Token": token
            },
        )
        print response.status_code

    @task(2)
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

    @task(3)
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

    @task(4)
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

    @task(5)
    def save_subj_random(self):
        nombre = ''.join(random.choice(string.lowercase) for i in range(15))
        materia = {
            'name': nombre,
            'divisions': [
                {
                    'comision': 'C1',
                    'weekdays': [
                        'Lunes de 18 a 22',
                        'Jueves de 9 a 12'
                    ],
                    'quota': 35
                },
                {
                    'comision': 'C2',
                    'weekdays': [
                        'Miercoles de 18 a 22',
                        'Viernes de 9 a 12'
                    ],
                    'quota': 15
                }
            ],
            'group': 'basic',
            'school_year': '201301'
        }
        response = self.client.post(
            "director/subject",
            data=None,
            json=materia,
            headers={
                self.headers['director_token']: self.headers_values['director_token'],
                self.headers['app_name']: self.headers_values['app_name'],
                self.headers['secure_key']: self.headers_values['secure_key'],
                "Content-Type": "application/json"
            },
        )

        print "Preview; Response status code:", response.status_code


class DirectorSurvey(HttpLocust):
    task_set = DirectorBehavior
    host = "http://localhost:9090"
    min_wait = 5000
    max_wait = 9000
