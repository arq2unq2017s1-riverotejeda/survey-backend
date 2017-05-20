from locust import HttpLocust, TaskSet, task
from random import choice, randint
import random, string, json
n = 0

def save_survey(l):
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
        response = l.client.post(
                        "director/student",
                        data=None,
                        json=student,
                        headers={
                            "X-App-Name":"docker",
                            "X-Secure-Key" : "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu",
                            "Content-Type":"application/json",
                            "X-Director-Token":"estaesmiclave"
                        },
                        )

        # print "Preview; Response status code:", response.status_code
        token = response.text
        # print "Text: ", response.text


        # Traigo todas las materias
        year = '201301'
        response = l.client.get(
                        "public/subjects/"+year,
                        data=None,
                        json=None,
                        headers={
                            "X-App-Name":"docker",
                            "X-Secure-Key" : "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu",
                            "Content-Type":"application/json"
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
                'schoolYear' : year
        }

        # print encuesta
        response = l.client.post(
                        "student/survey",
                        data=None,
                        json=encuesta,
                        headers={
                            "X-App-Name":"docker",
                            "X-Secure-Key" : "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu",
                            "Content-Type":"application/json",
                            "X-Student-Token":token
                        },
                        )
        # print response.status_code




class UserTasks(TaskSet):
    tasks = [save_survey]



class WebsiteUser(HttpLocust):
    host = "http://127.0.0.1:8089"
    min_wait = 2000
    max_wait = 5000
    task_set = UserTasks
