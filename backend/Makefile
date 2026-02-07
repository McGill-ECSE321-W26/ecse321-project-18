.PHONY: build report

build:
	@./gradlew build

report:
	python3 -m http.server 8000 -d ./build/reports
