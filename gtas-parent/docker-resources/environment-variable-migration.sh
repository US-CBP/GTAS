#!/bin/bash
function join { local IFS="$1"; shift; echo "$*"; }

while read -r line
do
    if [[ $line != SP_* ]] ;
    then
    	continue
    fi

    IFS='=' read -r -a array <<< "$line"

    IFS='_' read -r -a chars <<< "${array[0]}"

    unset chars[0]
    result=$(join . ${chars[@]} | awk '{print tolower($0)}')
    echo $result

    LINE_NUM=$(awk "/${result}=/{ print NR; exit }" $1)
    value=$(echo "${array[1]}")
    if [[ -z "$LINE_NUM" ]] ;
    then
      echo "$result"'='"$value" >> $1
      continue
    fi

    sed -i "$LINE_NUM"'s|.*|'"$result"'='"$value"'|' $1
done