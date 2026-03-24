.PHONY: help build stop gen del report

SHELL := /bin/bash

help:
	@echo "Usage:"
	@echo -e "\tmake help    - Print the help menu."
	@echo -e "\tmake build   - Build and start Docker containers in background."
	@echo -e "\tmake stop    - Stop Docker containers in background."
	@echo -e "\tmake gen     - Generate test data (requires the server to be running in 'dev' mode)."
	@echo -e "\tmake del     - Delete test data (requires the server to be running in 'dev' mode)."
	@echo -e "\tmake report  - Serve backend reports (test/checkstyle/spotbugs/pmd/jacoco) on localhost:8000."

build: stop
	docker compose up --build -d

stop:
	docker compose down

gen:
	bash ./dev/test.sh gen

del:
	bash ./dev/test.sh del

report:
	@echo "Serving reports at http://localhost:8000"
	python3 -m http.server 8000 -d ./backend/build/reports
