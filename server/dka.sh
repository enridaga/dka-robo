#!/bin/bash
flags=""


if [ $1 = "problem" ]; then
   curl $flags -G http://localhost:8080/planner/problem --data-urlencode query="$2"
elif [ $1 = "load" ]; then
   curl $flags -X PUT -T $2 http://localhost:8080/data -H "Content-type: application/n-quads; charset=utf-8"
else
   # $1 is expected to be one of: query, update
   curl $flags -G http://localhost:8080/$1 --data-urlencode $1="$2"
fi

