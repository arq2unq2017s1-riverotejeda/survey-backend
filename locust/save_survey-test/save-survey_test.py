from locust import HttpLocust, TaskSet, task
from random import choice, randint
import random, string, json
n = 0


class UserBehavior(TaskSet):

    # To change environment configuration
    # For docker uncomment this lines
    secure_key = "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu"
    app_name = "docker"
    # For local uncomment this lines
    # secure_key = "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"
    # app_name = "dev-test"

    headers = {'director_token': "X-Director-Token", 'app_name': "X-App-Name", 'secure_key': "X-Secure-Key"}
    headers_values = {'director_token': "estaesmiclave", 'app_name': app_name,
                      'secure_key': secure_key}

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
                            "/director/student",
                            data=None,
                            json=student,
                            headers={
                                self.headers['app_name']: self.headers_values['app_name'],
                                self.headers['secure_key']: self.headers_values['secure_key'],
                                self.headers['director_token']: self.headers_values['director_token'],
                                "Content-Type": "application/json"
                            },
                            )

            # print "Preview; Response status code:", response.status_code
            token = response.text
            # print "Text: ", response.text


            # Traigo todas las materias
            year = '201301'
            response = self.client.get(
                            "/public/subjects/"+year,
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
            #print 'ENCODED:', data_string

            decoded = json.loads(data_string)
            #print 'DECODED:', decoded

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
                            "/student/survey",
                            data=None,
                            json=encuesta,
                            headers={
                                self.headers['app_name']: self.headers_values['app_name'],
                                self.headers['secure_key']: self.headers_values['secure_key'],
                                "Content-Type": "application/json",
                                "X-Student-Token": token
                            },
                            )
            # print response.status_code




# class UserTasks(TaskSet):
 #   tasks = [save_survey]



class WebsiteUser(HttpLocust):
    host = "http://127.0.0.1:8089"
    min_wait = 2000
    max_wait = 5000
    task_set = UserBehavior
