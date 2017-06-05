from locust import HttpLocust, TaskSet, task
from random import choice
import random, string




def save_subj(self):

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
        response = self.client.post(
                        "/director/subject",
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



class UserTasks(TaskSet):
    # To change environment configuration
    # For docker uncomment this lines
    #secure_key = "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu"
    #app_name = "docker"
    # For local uncomment this lines
    secure_key = "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"
    app_name = "dev-test"

    headers = {'director_token': "X-Director-Token", 'app_name': "X-App-Name", 'secure_key': "X-Secure-Key"}
    headers_values = {'director_token': "estaesmiclave", 'app_name': app_name,
                      'secure_key': secure_key}
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
