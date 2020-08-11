#!/bin/bash
echo "Running Kaizen Auto ML Pipepline Model Training Cancellation task. This will kill the process associated with the model training task"
processId=$1
cmd=`kill -9 $processId`
echo "kill command output=$cmd"
