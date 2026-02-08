.PHONY: build report

build:
	@cd backend && ./gradlew build

cb:
	@cd backend && ./gradlew clean build

report:
	python3 -m http.server 8000 -d ./backend/build/reports
