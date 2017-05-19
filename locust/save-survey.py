from locust import HttpLocust, TaskSet, task
from random import choice, randint
import random, string
token = 1
subjects = []


def save_student(l):
        global token
        nombre =  ''.join(random.choice(string.lowercase) for i in range(7))
        legajo = randint(0,20000)
        email = ''.join(random.choice(string.lowercase) for i in range(7)) + '@' + ''.join(random.choice(string.lowercase) for i in range(4)) + '.' + ''.join(random.choice(string.lowercase) for i in range(3))
        student = {
            'name': nombre,
            'legajo' : legajo,
            'email': email
        }
        response = l.client.post(
                        "director/student",
                        data=None,
                        json=student,
                        headers={
                            "X-App-Name":"heroku",
                            "X-Secure-Key" : "p81lS7JOYsovb41zV41492q6AtKTHfey",
                            "Content-Type":"application/json",
                            "X-Director-Token":"estaesmiclave"
                        },
                        )

        print "Preview; Response status code:", response.status_code
        token = response.text
        print "Text: ", response.text

def subjects(l):
    global subjects
    year = '201701'
    response = l.client.get(
                    "public/subjects/"+year,
                    data=None,
                    json=None,
                    headers={
                        "X-App-Name":"heroku",
                        "X-Secure-Key" : "p81lS7JOYsovb41zV41492q6AtKTHfey",
                        "Content-Type":"application/json"
                    },
                    )
    subjects = response.text


def save_survey(l):
    



class UserTasks(TaskSet):
    tasks = [save_student, subjects]



class WebsiteUser(HttpLocust):
    host = "http://127.0.0.1:8089"
    min_wait = 2000
    max_wait = 5000
    task_set = UserTasks
