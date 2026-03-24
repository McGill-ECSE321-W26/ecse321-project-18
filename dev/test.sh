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

URI="http://localhost:8080/fashionstore/dev/test"

case "$1" in
    "gen")
        curl -X POST "$URI"
        ;;
    "del")
        curl -X DELETE "$URI"
        ;;
    *)
        print_help
        exit 1
        ;;
esac
