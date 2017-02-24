#!/bin/bash

echo ""
echo "Setting dummy bot..............."
sh  ./dka.sh setbot "dummy"
echo "[OK]"

echo ""
echo "Showing answer to query........."
curl -G "http://localhost:8080/query" --data-urlencode query="$1" 

echo ""
echo "Printing planner problem........"
curl -G "http://localhost:8080/planner/problem" --data-urlencode query="$1"

echo ""
echo "Looking for a possible plan....."
time curl -G "http://localhost:8080/planner/plan" --data-urlencode query="$1"

echo ""
echo "Done!"
