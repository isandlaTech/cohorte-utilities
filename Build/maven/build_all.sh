#!/bin/bash

echo
echo "********************************** init **********************************"
echo


if  [ $# -gt 0 ] && [ $1 = 'deploy' ]
then
	export DEPLOY=deploy
	echo "DEPLOY     : [${DEPLOY}] Deployment in Nexus for validation"
else
	export DEPLOY=""
	echo "DEPLOY     : [${DEPLOY}] => No deployment in Nexus"
fi


export JAVA_HOME=$(/usr/libexec/java_home -v 1.8/usr/libexec/java_home -v 1.8)

echo "JAVA_HOME    : [${JAVA_HOME}]"

java -version

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "DIR          : [${DIR}]"

export P2_LOCAL_REPO=`pwd`/p2-repo/ 
echo "P2_LOCAL_REPO: [$P2_LOCAL_REPO]"

echo
echo "********************************** cleanup and build org.cohorte.utilities **********************************"
echo

cd ../../org.cohorte.utilities/
mvn clean install $DEPLOY 

cd $DIR


echo
echo "********************************** cleanup **********************************"
echo


mvn clean install test -P cleanup -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi

echo
echo "********************************** build_bundles ($DEPLOY) **********************************"
echo

mvn clean install $DEPLOY -P build_bundles -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi

# MOD_OG_20200325
echo
echo "********************************** build_jars ($DEPLOY) **********************************"
echo

#mvn clean install $DEPLOY -P build_jars -DP2_LOCAL_REPO=$P2_LOCAL_REPO
#if test $? -ne 0 ; then
#exit
#fi

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

mvn install -P update_site -DP2_LOCAL_REPO=$P2_LOCAL_REPO -DNEXUS_USER=$1 -DNEXUS_PASS=$2
if test $? -ne 0 ; then
exit
fi
echo
echo "********************************** end **********************************"

#eof
