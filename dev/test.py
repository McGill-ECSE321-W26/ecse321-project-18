#! /usr/bin/env python3

import sys, requests

def print_help():
    print("Help:\n\t- ./test.py gen\n\t- ./test.py del")

if len(sys.argv) != 2:
    print_help()
    exit(1)

BASE = "http://localhost:8080/fashionstore"

if sys.argv[1] == "gen":
    response = requests.post(BASE + "/dev/test")
    if response.status_code == 201:
        print(f"Successfully generated data.\nStatus code: {response.status_code}")
    else:
        print(f"Error in generating data.\nStatus code: {response.status_code}")
elif sys.argv[1] == "demo":
    response = requests.post(BASE + "/dev/demo")
    if response.status_code == 201:
        print(f"Successfully generated data.\nStatus code: {response.status_code}")
    else:
        print(f"Error in generating data.\nStatus code: {response.status_code}")
elif sys.argv[1] == "del":
    response = requests.delete(BASE + "/dev/test")
    if response.status_code == 204:
        print(f"Successfully deleted data.\nStatus code: {response.status_code}")
    else:
        print(f"Error in deleting data.\nStatus code: {response.status_code}")
else:
    print_help()
