#!/bin/bash

# $1 is expected to be one of: query, update
curl -G http://localhost:8080/$1 --data-urlencode $1="$2"