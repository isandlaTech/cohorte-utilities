#!/bin/bash

# User-specific bootstrapping configuration
if test -z "$COHORTE_HOME"
	then
		echo
		echo "[ERROR] the system environment's variable COHORTE_HOME, is not defined!"	
		echo
		exit
fi

# Updating PYTHONPATH 
base=$(pwd)
export PYTHONPATH=${PYTHONPATH:="$base/bin:$base/repo"}

# Run COHORTE
echo
echo "[INFO] starting COHORTE ..."
sh $COHORTE_HOME/run.sh $(pwd) $*
