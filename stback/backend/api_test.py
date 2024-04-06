import requests


def main():
    response = requests.post(
        url='http://42.180.52.71:8999/chat',
        json=dict(
            username='nomodeset',
            password='123456789',
            prompt='Dijkstra示例代码'
        )
    )
    print(response.text)


if __name__ == '__main__':
    main()
