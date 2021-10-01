#!/bin/bash

# see https://help.sonatype.com/display/NXRM3/REST+and+Integration+API

name=createRawRepo
jsonFile=${name}.js
user=$1
pass=$2
repo=$3


echo "user=$user"
echo "pass=$pass"
echo "repo=$repo"
printf "Deleting previous $name script...\n\n"

curl -k -v -X DELETE -u $user:$pass "https://nrm.cohorte.tech/service/siesta/rest/v1/script/$name"

printf "Uploading $name script. It contains: $jsonFile\n\n"

curl -k -v -u $user:$pass --header "Content-Type: application/json" 'https://nrm.cohorte.tech/service/siesta/rest/v1/script/' -d @$jsonFile

printf "Running $name script..."

curl -k -v -X POST -u $user:$pass --header "Content-Type: text/plain" "https://nrm.cohorte.tech/service/siesta/rest/v1/script/$name/run" -d $repo
