#!/bin/sh
# This script can be used as a "remote shell" command that is only
# capable of pretending to connect to "localhost".  This is useful
# for testing or for running a local copy where the sender and the
# receiver needs to use different options (e.g. --fake-super).  If
# we get a -l USER option, we try to use "sudo -u USER" to run the
# command.  Supports only the hostnames "localhost" and "lh", with
# the latter implying the --no-cd option.

user=''
do_cd=y # Default path is user's home dir (just like ssh) unless host is "lh".

while : ; do
    case "$1" in
    -l) user="$2"; shift; shift ;;
    -l*) user=`echo "$1" | sed 's/^-l//'`; shift ;;
    --no-cd) do_cd=n; shift ;;
    -*) shift ;;
    localhost) shift; break ;;
    lh) do_cd=n; shift; break ;;
    *) echo "lsh: unable to connect to host $1" 1>&2; exit 1 ;;
    esac
done

if [ "$user" ]; then
    prefix=''
    if [ $do_cd = y ]; then
	home=`perl -e "print((getpwnam('$user'))[7])"`
	prefix="cd '$home' &&"
    fi
    sudo -H -u "$user" sh -c "$prefix $*"
else
    if [ $do_cd = y ]; then
	cd || exit 1
    fi
    eval "${@}"
fi
