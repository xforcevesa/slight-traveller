from fastapi import FastAPI
from qhugpt import generate_text
from pydantic import BaseModel
from threading import Lock

app = FastAPI()

request_lock = Lock()


class ChatRequest(BaseModel):
    username: str
    password: str
    prompt: str


@app.get('/hello')
def hello_world():
    return 'Hello World!'

@app.post('/chat')
def chat(prompt: ChatRequest):
    global request_lock
    if prompt.username == 'nomodeset' and prompt.password == '123456789':
        text = "Wait a moment"
        if not request_lock.locked():
            with request_lock:
                text = generate_text(prompt.prompt)
        return text
    else:
        return 'Please login or re-login.'
    

