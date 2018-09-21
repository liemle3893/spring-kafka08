SHELL=bash

# Pre-processing

INFER:=$(shell command -v infer 2> /dev/null)

# Tasks
clean:
	sh gradlew -q clean
.PHONY: build
build:
	sh gradlew -q assemble
deploy: clean build
	sh gradlew -q :zk_kafka:bintrayUpload --info
analyze: clean
ifndef INFER
	$(echo "Please install Infer (http://fbinfer.com/) for better analytic result.")
	sh gradlew check
else
	# sh gradlew will cause error.
	infer -o build/reports/infer -- ./gradlew build
endif

