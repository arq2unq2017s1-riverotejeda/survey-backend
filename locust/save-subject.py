from locust import HttpLocust, TaskSet, task
from random import choice
import random, string




def save_subj(l):
        nombre =  ''.join(random.choice(string.lowercase) for i in range(15))
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
        response = l.client.post(
                        "director/subject",
                        data=None,
                        json=materia,
                        headers={
                            "X-App-Name":"heroku",
                            "X-Secure-Key" : "p81lS7JOYsovb41zV41492q6AtKTHfey",
                            "Content-Type":"application/json",
                            "X-Director-Token":"estaesmiclave"
                        },
                        )

        print "Preview; Response status code:", response.status_code



class UserTasks(TaskSet):
    # one can specify tasks like this
    tasks = [save_subj]



class WebsiteUser(HttpLocust):
    """
    Locust user class that does requests to the locust web server running on localhost
    """
    host = "http://127.0.0.1:8089"
    min_wait = 2000
    max_wait = 5000
    task_set = UserTasks
