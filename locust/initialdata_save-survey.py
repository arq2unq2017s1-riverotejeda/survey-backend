#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random
import string

import json
import requests

# To change environment configuration
# For docker uncomment this lines
# secure_key = "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu"
# app_name = "docker"

# For local uncomment this lines
secure_key = "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"
app_name = "dev-test"

url = 'http://localhost:9090/private/director'
headers = {'X-App-Name': app_name, 'X-Secure-Key': secure_key}

#Guardo el director
director = {
    'name': 'Gabriela',
    'last_name': 'Arevalo',
    'email': 'ga@unq.com',
    'token': 'estaesmiclave'
}

# POST with form-encoded data
r = requests.post(url, headers=headers, json=director)
print "Creating director with status: ", r.status_code


#Guardo las materias
url = 'http://localhost:9090/director/subject'
headers = {'X-App-Name': app_name, 'X-Secure-Key': secure_key, 'X-Director-Token': 'estaesmiclave'}


for x in range(0,28):
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
    r = requests.post(url, headers=headers, json=materia)
    print "Creating subject ", nombre
    print "Creating subject with status:", r.content


""" Create a director before any task in order to use the director token """
response = requests.post(
    url="http://localhost:9090/private/director",
    headers={
        'X-App-Name': app_name,
        'X-Secure-Key': secure_key
    },
    json={
        "name": "Marcia",
        "last_name": "Tejeda",
        "email": "marcia.tejeda@unq.com",
        "token": "marcia2017"
    })

print "Creating director with status:", response.status_code

# save one student for test
student_response = requests.post(
    url="http://localhost:9090/director/student",
    headers={
        "X-Director-Token": "marcia2017",
        'X-App-Name': app_name,
        'X-Secure-Key': secure_key
    },
    json={
        "name": "student_dummy",
        "legajo": "0002",
        "email": "student_dummy@unq.com"
    })
print "Student saved status: ", student_response.status_code

# save survey for test

survey_response = requests.post(
    url="http://localhost:9090/student/survey",
    headers={
        "X-Student-Token": "AVz5I8P2/xvUWkONfIDS4UzqNslqGZ+ucln5QAFsnU4=",  # this only works with student_dummy user
        'X-App-Name': app_name,
        'X-Secure-Key': secure_key
    },
    json={
        "student_name": "student_dummy",
        "legajo": "0002",
        "token": "AVz5I8P2/xvUWkONfIDS4UzqNslqGZ+ucln5QAFsnU4=",  # this only works with student_dummy user
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
    })
print "Dummy survey saved status: ", survey_response.status_code
