from transformers import AutoTokenizer, AutoModel
from transformers import BitsAndBytesConfig
import torch
from vllm import LLM, SamplingParams
import os

os.environ['http_proxy']='http://127.0.0.1:8889'
os.environ['http_proxy']='http://127.0.0.1:8889'

llm = LLM(model="THUDM/chatglm3-6b", trust_remote_code=True)

def main_old():
    quantization_config = BitsAndBytesConfig(
        load_in_4bit=True,
        bnb_4bit_use_double_quant=True,
        bnb_4bit_quant_type="nf4",
        bnb_4bit_compute_dtype=torch.float16,
    )
    tokenizer = AutoTokenizer.from_pretrained("THUDM/chatglm3-6b", trust_remote_code=True)
    model = AutoModel.from_pretrained("THUDM/chatglm3-6b", torch_dtype=torch.float16,
            quantization_config=quantization_config, trust_remote_code=True)
    # tokenizer = AutoTokenizer.from_pretrained("THUDM/codegeex2-6b", trust_remote_code=True)
    # model = AutoModel.from_pretrained("THUDM/codegeex2-6b", trust_remote_code=True).half().cuda()
    model = model.eval()
    history = []
    while True:
        text = input("Type to Chat >>> ").strip()
        if len(text) == 0:
            print("Goodbye ~")
            break
        response, history = model.chat(tokenizer, text, history=history[:20])
        print(response)
        print()

def main():
    while True:
        text = input("Type to Chat >>> ")
        if len(text) == 0:
            print("Goodbye ~")
            break
        prompts = [
            f"<|user|>\n{text}\n<|user|>\n",
        ]
        sampling_params = SamplingParams(temperature=0.5, top_p=0.8, max_tokens=8192)
        outputs = llm.generate(prompts, sampling_params)

        # Print the outputs.
        for output in outputs:
            generated_text = output.outputs[0].text.strip()
            print(generated_text)
        print()

if __name__ == '__main__':
    # main_old()
    main()
