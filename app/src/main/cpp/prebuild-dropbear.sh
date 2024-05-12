#!/bin/bash

cd dropbear/src || { echo "Must be executed from the 'cpp' directory"; exit 1; }

# -nt 	Compares modification time of two files and determines which one is newer.
[ default_options.h -nt default_options_guard.h ] && ./ifndef_wrapper.sh < default_options.h > default_options_guard.h

cd ../..

