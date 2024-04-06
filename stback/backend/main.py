from fastapi import FastAPI
from qhugpt import generate_text
from pydantic import BaseModel

app = FastAPI()

class ChatRequest(BaseModel):
    username: str
    password: str
    prompt: str


@app.get('/hello')
def hello_world():
    return 'Hello World!'

@app.post('/chat')
def chat(prompt: ChatRequest):
    if prompt.username == 'nomodeset' and prompt.password == '123456789':
        return generate_text(prompt.prompt)
    else:
        return 'Please login or re-login.'
    

