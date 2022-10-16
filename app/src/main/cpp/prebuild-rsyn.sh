#!/bin/bash

cd rsync || { echo "Must be executed from the 'cpp' directory"; exit 1; }

# only needed if "usage.c" is used

. ./mkgitver

awk -f help-from-md.awk -v hfile=help-rsync.h rsync.1.md
awk -f help-from-md.awk -v hfile=help-rsyncd.h rsync.1.md

# always needed:

awk -f daemon-parm.awk daemon-parm.txt
awk -f mkproto.awk *.c lib/compat.c daemon-parm.h
awk -f define-from-md.awk  -v hfile=default-cvsignore.h rsync.1.md
awk -f define-from-md.awk  -v hfile=default-dont-compress.h rsync.1.md

# to create the man files (not needed, just for completeness of notes)
#maybe-make-man rsync.1.md
#maybe-make-man rsync-ssl.1.md
#maybe-make-man rsyncd.conf.5.md
#maybe-make-man support/rrsync.1.md

#TODO: figure out how to actually calculate EXTRA_ROUNDING?
echo -e "#define EXTRA_ROUNDING 0\n" >rounding.h
