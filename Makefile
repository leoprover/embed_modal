INSTALL_DIR ?= $(HOME)/.local/bin
BIN_NAME ?= embedlogic

build:
	mvn package
	mkdir -p bin
	cat util/exec_dummy embed/target/embed-1.0-SNAPSHOT-shaded.jar > bin/$(BIN_NAME)
	chmod +x bin/$(BIN_NAME)

all: build

install:
	install -d $(INSTALL_DIR)
	install bin/$(BIN_NAME) $(INSTALL_DIR)

clean:
	mvn clean
	rm -Rf bin
