import requests, json
from random import choice, randint
import random, string


url = 'http://localhost:9090/private/director'
headers = {'X-App-Name': 'docker', 'X-Secure-Key': 'rtidn2sDxg1U2u0xGWR9vkPPP33lGflu'}

#Guardo el director
json = {
    'name': 'Gabriela',
    'last_name': 'Arevalo',
    'email': 'ga@unq.com',
    'token': 'estaesmiclave'
}

# POST with form-encoded data
r = requests.post(url, headers=headers,json=json )


#Guardo las materias
url = 'http://localhost:9090/director/subject'
headers = {'X-App-Name': 'docker', 'X-Secure-Key': 'rtidn2sDxg1U2u0xGWR9vkPPP33lGflu', 'X-Director-Token' : 'estaesmiclave'}


for x in range(0,28):
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
    r = requests.post(url, headers=headers,json=materia )

