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


# MOD_OG_20200325
echo
echo "********************************** build_jars ($DEPLOY) **********************************"
echo

mvn clean install $DEPLOY -X -P build_jars -DP2_LOCAL_REPO=$P2_LOCAL_REPO
if test $? -ne 0 ; then
exit
fi


echo
echo "********************************** end **********************************"

#eof
