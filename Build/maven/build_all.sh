#!/bin/bash

echo
echo "********************************** init **********************************"
echo

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export P2_LOCAL_REPO=`pwd`/p2-repo/ 

echo "P2_LOCAL_REPO=$P2_LOCAL_REPO"

echo
echo "********************************** cleanup **********************************"
echo

mvn clean install test -P cleanup -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi

echo
echo "********************************** build_bundles **********************************"
echo

mvn clean install -P build_bundles -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi

echo
echo "********************************** construct_p2_repo **********************************"
echo

mvn clean install test -P construct_p2_repo -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi
echo
echo "********************************** construct_update_site **********************************"
echo

mvn install -P construct_site -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi

echo
echo "********************************** construct_update_site **********************************"
echo

mvn install -P update_site -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi
echo
echo "********************************** end **********************************"

#eof
