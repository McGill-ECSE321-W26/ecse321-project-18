#! /usr/bin/env python3

import base64, sys

filename = ""
print(sys.argv)
if len(sys.argv) == 1:
    filename = input("Enter filename: ")
elif len(sys.argv) == 2:
    filename = sys.argv[1]
else:
    print("Error: Invalid input")
    exit(1)

with open(filename, "rb") as f:
    encoded = base64.b64encode(f.read())
    print(f"data:image/png;base64,{encoded.decode()}")
