#!/bin/bash

NAME=sphinx-console-agent
PIDFILE=/var/run/$NAME.pid
sphinx-console_path=/opt/sphinx-console
RUNAS=sphinx-console

printf "Stopping $NAME... "
PID=`cat $PIDFILE`
if [ -f $PIDFILE ]; then
	kill -KILL $PID
	echo "killed $PID"
	rm -f $PIDFILE
	#echo "sphinx-console agent stop:"  $(date +%m/%d/%Y'  '%X'  '%Z'('%:z')') $'\n'  >> $sphinx-console_path/logs/agentStartStop.log
    MSG="sphinx-console agent stop:  $(date +%m/%d/%Y'  '%X'  '%Z'('%:z')')"
    sudo -u $RUNAS tee -a $sphinx-console_path/logs/agentStartStop.log <<<$MSG>/dev/null
else
	echo "$PIDFILE not found"
fi