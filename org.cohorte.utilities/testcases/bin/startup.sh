#!/bin/bash
# -----------------------------------------------------
# this command shell file is used by the CTestOSServer 
# class to launch a server (eg. CTestAppServerTest)
# -----------------------------------------------------

clear
echo "-----"

# retreive the "UserDir" from the first passed argument
# -----------------------------------------------------
#
# the user dir must be the directory of the "org.cohorte.utilities" project !
# eg. 
# ${git_work_tree}/cohorte-utilities/org.cohorte.utilities

wUserDir=$1
if [[ ${wUserDir} == '' ]]
then
  echo "UserDir init: no argument 1 : pwd used"
  wUserDir=`pwd`;
else
  echo "UserDir init: argument 1 passed : argument 1 used"
fi

echo "UserDir: ${wUserDir}"
echo "-----"

java \
  -showversion 	\
  -Djava.util.logging.SimpleFormatter.format="%1$tY/%1$tm/%1$td; %1$tH-%1$tM-%1$tS.%1$tL;%5$s%6$s%n" \
  -cp "${wUserDir}/bin" \
  tests.CTestAppServerTest

#eof
