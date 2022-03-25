#!/bin/bash

USERNAME=$1
PASSWORD=$2
REPO_NAME=$3

echo "USERNAME=$USERNAME"
echo "PASSWORD=$PASSWORD"
echo "REPO_NAME=$REPO_NAME"

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $DIR/target/repository

for file in ./*
do
	curl -k -v --user "$USERNAME:$PASSWORD" --upload-file ${file} https://nrm.cohorte.tech/repository/${REPO_NAME}/${file}
done;

cd $DIR/../../Build/maven/p2-repo
echo "-----------"
pwd 
echo "-----------"

for file in ./*
do
	curl -k -v --user "$USERNAME:$PASSWORD" --upload-file ${file} https://nrm.cohorte.tech/repository/${REPO_NAME}/${file}
done;

for file in plugins/*
do
	curl -k -v --user "$USERNAME:$PASSWORD" --upload-file ${file} https://nrm.cohorte.tech/repository/${REPO_NAME}/${file}
done;
