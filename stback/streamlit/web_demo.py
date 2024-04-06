import streamlit as st
import torch
from vllm import LLM, SamplingParams
import os
from qhuglm import llm

# Streamlit app
st.title("QHU-GPT ChatBot")

# Text input for user prompt
prompt = st.text_input("Enter a prompt:", "")

# llm = LLM(model="THUDM/chatglm3-6b", trust_remote_code=True)

if 'message' not in st.session_state:
    st.session_state.message = []

def generate_text(text: str):
    prompts = [
        f"<|user|>\n{text}\n<|user|>\n",
    ]
    sampling_params = SamplingParams(temperature=0.8, top_p=0.9, max_tokens=8192)
    outputs = llm.generate(prompts, sampling_params)
    return outputs[0].outputs[0].text.strip()

if prompt:
    # st.chat_message('user').markdown(prompt, unsafe_allow_html=True)
    st.session_state.message.append({'role': 'user', 'text': prompt})
    with st.spinner("Wait a moment..."):
        text = generate_text(prompt)
    # st.chat_message('bot').markdown(text, unsafe_allow_html=True)
    st.session_state.message.append({'role': 'bot', 'text': text})
    

for message in reversed(st.session_state.message):
    with st.chat_message(message['role']):
        st.markdown(message['text'], unsafe_allow_html=True)
