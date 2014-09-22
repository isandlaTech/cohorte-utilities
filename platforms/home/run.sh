#!/bin/bash

# Keep the current working directory
old_pwd="$(pwd)"

# Compute the path to this file
cd "$(dirname $0)"
run_home="$(pwd)"

# Compute the repository root, and normalize it
cohorte_root=${COHORTE_ROOT:="$run_home/"}
cd "$cohorte_root"
export COHORTE_ROOT="$(pwd)"
export COHORTE_HOME="$(pwd)"
cd "$old_pwd"

#echo "[INFO] COHORTE_ROOT   => $COHORTE_ROOT"
echo
echo "[INFO] COHORTE_HOME => $COHORTE_HOME"

# $1 should contain the path to COHORTE_BASE to be launched
if [ $# -eq 0 ]
	then		
		echo "[ERROR] No COHORTE BASE was supplied!"
		echo
		exit -0
fi  

# remove the supplied COHORTE_BASE from the list of parameters
export COHORTE_BASE="$1"
echo "[INFO] COHORTE_BASE => $COHORTE_BASE"
 
shift

# COHORTE node name
export COHORTE_NODE_NAME=${COHORTE_NODE_NAME:="central"}

# Forker log file
export COHORTE_LOGFILE="$COHORTE_BASE/var/forker.log"

# Default Python interpreter to use (Python 3)
PYTHON_INTERPRETER=${PYTHON_INTERPRETER:="python3"}

# Update Python path: 
#    Current path + bin path + repo path
export PYTHONPATH=$PYTHONPATH:$COHORTE_HOME/lib:$COHORTE_HOME/repo
echo "[INFO] PYTHONPATH => $PYTHONPATH"

# Remove previous environment
if [ ! -d "$COHORTE_BASE/var" ]
then
    mkdir "$COHORTE_BASE/var"
else
    rm -r $COHORTE_BASE/var/* 2> /dev/null
fi

# Run the damn thing
$PYTHON_INTERPRETER -m cohorte.boot.boot -d -v $*
