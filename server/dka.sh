#!/bin/bash
flags=""

if [ $# -eq 0 ]
  then
    echo "No arguments supplied" && exit 1;
fi

base=http://localhost:8080

if [ $1 = "problem" ] || [ $1 = "plan" ] || [ $1 = "plan-cached" ] ; then
   curl $flags -G "$base/planner/$1" --data-urlencode query="$2"
elif [ $1 = "load" ]; then
   curl $flags -X PUT -T $2 "$base/data" -H "Content-type: application/n-quads; charset=utf-8"
elif [ $1 = "isbusy" ] || [ $1 = "wru" ] || [ $1 = "getbot" ] || [ $1 = "doing" ] ; then
   curl $flags "$base/bot/$1"
   echo ""
elif [ $1 = "abort" ] ; then
   curl $flags -X DELETE "$base/bot/abort"
elif [ $1 = "send" ] ; then
   curl $flags -G "$base/bot/$1" --data-urlencode query="$2"
elif [ $1 = "setbot" ] ; then
   curl $flags -G "$base/bot/$1" --data-urlencode address="$2"
else
   # $1 is expected to be one of: query, update
   #curl $flags -G "$base/$1" --data-urlencode $1="$2" -H "Accept:$3"
   curl $flags -G "$base/$1" --data-urlencode $1="$2"
   #echo $flags -G "$base/$1" --data-urlencode $1 = $2
fi

