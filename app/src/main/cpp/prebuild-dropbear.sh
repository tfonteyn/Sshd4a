#!/bin/bash

cd dropbear || { echo "Must be executed from the 'cpp' directory"; exit 1; }

[ default_options.h -nt default_options_guard.h ] && ifndef_wrapper.sh < default_options.h > default_options_guard.h
