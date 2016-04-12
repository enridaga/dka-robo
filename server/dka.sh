#!/bin/bash


curl -G http://localhost:8080/$1 --data-urlencode $1="$2"