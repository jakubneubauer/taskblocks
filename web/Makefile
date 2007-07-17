#
# Web Build File
#
CURRENT_VERSION=unknown

# Definitions for macro expansions
DEFS=-D CURRENT_VERSION=$(CURRENT_VERSION)

# Constructs the list of all wanted .html files from the list of
# all existing .m4.html files.
HTMLFILES=$(patsubst %.m4.html, %.html, $(wildcard *.m4.html))

# Constructs the list of existing .inc files to be used in dependencies
INCLUDES=$(wildcard *.inc)

all: $(HTMLFILES)
%.html: %.m4.html $(INCLUDES)
	m4 -D CURRENT_FILE=$@ $(DEFS) $< > $@
clean:
	rm -f $(HTMLFILES)
