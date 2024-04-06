from vllm import LLM, SamplingParams
import os

os.environ['http_proxy']='http://127.0.0.1:8889'
os.environ['http_proxy']='http://127.0.0.1:8889'

llm = LLM(model="THUDM/chatglm3-6b", trust_remote_code=True)


def generate_text(text: str):
    prompts = [
        f"<|user|>\n{text}\n<|user|>\n",
    ]
    sampling_params = SamplingParams(temperature=0.8, top_p=0.9, max_tokens=8192)
    outputs = llm.generate(prompts, sampling_params)
    return outputs[0].outputs[0].text.strip()
