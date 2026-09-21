#!/bin/bash
echo "start building..."
start=$(date +%s)
#git pull origin main
mvnd clean deploy -DskipTests=true -P coverage,nexus -T3


echo "build complete and clean up the project..."
mvnd clean

end=$(date +%s)
execution_time=$((end-start))
echo "clean up complete and total cost: $execution_time seconds."
