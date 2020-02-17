#!/bin/bash

SD=$(cd $(dirname $0); pwd -P)
WD="`pwd`"
SCRIPT=$(basename $0)
SCRIPT_NAME=${SCRIPT%.*}
SCRIPT_EXTENSION=${SCRIPT##*.}
SELF=$SD/$SCRIPT

export KARAF_DEBUG=TRUE

sh $SD/run.sh debug $@

