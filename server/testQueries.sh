#!/bin/bash

# paste following in your script
declare -a Spinner

Spinner=(/ - \\ \| / - \\ \| ) 
Spinnerpos=0

update_spinner()
{
    printf "\b"${Spinner[$Spinnerpos]} 
    (( Spinnerpos=(Spinnerpos +1)%8 ))
}

echo "Setting bot"
sh  ./dka.sh setbot "$2:5000"

echo "Querying..."
curl -G "http://localhost:8080/query" --data-urlencode query="$1" 

echo "Plan"
curl -G "http://localhost:8080/planner/plan" --data-urlencode query="$1"

echo ""
echo "Sending plan to robot"
curl -G "http://localhost:8080/bot/send" --data-urlencode query="$1"

# while /isbusy
busy="true"
echo "Executing..." 
while [[ "$busy" == "true" ]]
do
        busy=`curl -s -G "http://localhost:8080/bot/isbusy"`
        update_spinner #echo -n "." #"Are you busy?" $busy
done 

echo "Updated?"
curl -G "http://localhost:8080/query" --data-urlencode query="$1"

