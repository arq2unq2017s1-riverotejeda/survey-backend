import random
import string

import json
import requests

# To change environment configuration
# For docker uncomment this lines
secure_key = "rtidn2sDxg1U2u0xGWR9vkPPP33lGflu"
app_name = "docker"

# For local uncomment this lines
# secure_key = "y9Y4mw9v5HK5kMp5PaFn4NrAztGlP9rt"
# app_name = "dev-test"

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
