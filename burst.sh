#!/bin/bash 

pids=()
path="bin/"
trap ctrl_c INT

function ctrl_c() {
	for i in "${pids[@]}"
	do
		echo "kill ${i}"
		kill $i
	done
	echo "Processes killed."
	exit
}

nTest=$1
echo "[ ] Start Server"
java -cp $path jvn.coord.JvnCoordImpl & pids+=($!)
echo "[ ] Launch ${nTest} tests"
for i in $(seq 1 ${nTest}) 
do
	echo "Run ${i}"
	java -cp $path irc.IrcHeadless true & pids+=($!)
done

while true; do sleep 1; done
