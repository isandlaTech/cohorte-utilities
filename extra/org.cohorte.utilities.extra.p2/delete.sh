#!/bin/bash

# see https://help.sonatype.com/display/NXRM3/REST+and+Integration+API

name=deleteRawRepo
jsonFile=${name}.js
user=$1
pass=$2
repo=$3

echo "user=$user"
echo "pass=$pass"
echo "repo=$repo"
printf "Deleting previous $name script...\n\n"

curl -v -X DELETE -u $user:$pass "http://nexus:8081/service/siesta/rest/v1/script/$name"

printf "Uploading $name script. It contains: $jsonFile\n\n"

curl -v -u $user:$pass --header "Content-Type: application/json" 'http://nexus:8081/service/siesta/rest/v1/script/' -d @$jsonFile

printf "Running $name script..."

curl -v -X POST -u $user:$pass --header "Content-Type: text/plain" "http://nexus:8081/service/siesta/rest/v1/script/$name/run" -d $repo

