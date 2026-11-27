JAVAC   ?= javac
JAVA    ?= java
JAVADOC ?= javadoc

SRC_DIR := src
OUT_DIR := out
DOC_DIR := docs

SOURCES := $(wildcard $(SRC_DIR)/*.java \
                      $(SRC_DIR)/exception/*.java \
                      $(SRC_DIR)/model/*.java \
                      $(SRC_DIR)/payment/*.java \
                      $(SRC_DIR)/service/*.java \
                      $(SRC_DIR)/view/*.java)

.PHONY: all run javadoc clean

all:
	$(JAVAC) -Xlint:all -d $(OUT_DIR) -sourcepath $(SRC_DIR) $(SOURCES)

run: all
	$(JAVA) -cp $(OUT_DIR) Main

javadoc:
	$(JAVADOC) -d $(DOC_DIR) -author -version -sourcepath $(SRC_DIR) $(SOURCES)

clean:
	rm -rf $(OUT_DIR) $(DOC_DIR)
