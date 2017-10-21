#!/bin/bash

echo
echo "********************************** init **********************************"
echo

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export P2_LOCAL_REPO=`pwd`/p2-repo/ 



echo
echo "********************************** build_bundles **********************************"
echo

mvn clean install -U -P build_bundles -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi

echo
echo "********************************** end **********************************"

#eof
