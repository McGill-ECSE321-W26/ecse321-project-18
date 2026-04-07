#! /usr/bin/env bash

# Function to print help
print_help() {
    echo -e "Help:\n\t- ./test.sh gen\n\t- ./test.sh del"
}

# Check if exactly one argument is provided
if [ "$#" -ne 1 ]; then
    print_help
    exit 1
fi

BASE="http://localhost:8080/fashionstore"

case "$1" in
    "gen")
        curl -X POST "${BASE}/dev/test"
        ;;
    "demo")
        curl -X POST "${BASE}/dev/demo"
        ;;
    "del")
        curl -X DELETE "${BASE}/dev/test"
        ;;
    *)
        print_help
        exit 1
        ;;
esac
