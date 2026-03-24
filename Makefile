.PHONY: build test delete report

build: stop
	docker compose up --build --watch

stop:
	docker compose down

gen:
	./dev/test.sh gen

del:
	./dev/test.sh del

report:
	python3 -m http.server 8000 -d ./backend/build/reports
