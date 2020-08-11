#!/bin/bash
echo "Running Kaizen Auto ML Pipepline Deployment Initialization. This will swap the currently running model on spark with the desired one"
modelVersion=$1
targetModelFolder=/opt/omni/.model_repository/v$modelVersion
sparkModelDeploymentFolder=/opt/omni/models
if [ -d "$targetModelFolder" ]
then
    echo "Removing old (currently running) models used by the spark cluster"
    cmd=`\rm -rf $sparkModelDeploymentFolder/*`
    echo "Removing old (currently running) models used by the spark cluster command output=$cmd"
    echo "Copy the target models to the spark repository"
    cmd=`\cp -rf $targetModelFolder/* $sparkModelDeploymentFolder`
    echo "Copy the target models to the spark repository command output=$cmd"
else
    echo "Directory $targetModelFolder does not exist. So we complain"
    exit 1
fi
