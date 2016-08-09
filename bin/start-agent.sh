#!/bin/bash

NAME=sphinx-console-agent
RUNAS=sphinx-console
PIDFILE=/var/run/$NAME.pid

sphinx-console_path=/opt/sphinx-console
java_agent_management_port=8091
rmi_host=195.26.187.170
agent_path=/opt/sphinx-console/agent

if [ -f $PIDFILE ]; then
    PID=`cat $PIDFILE`
        if [ -z "`ps axf | grep ${PID} | grep -v grep`" ]; then
            printf "%s\n" "Process is dead but pidfile ($PIDFILE) exists. Rewriting PID..."
        else
            echo "sphinx-console agent is already running. PID: $PID"
            exit 0
        fi
fi


CMD="nohup java -Xms256m  -Xmx256m -Dlog4j.configuration=file:$sphinx-console_path/conf/agent-log4j.properties -DconfigLocation=$sphinx-console_path/conf -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dfile.encoding=UTF-8 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=$java_agent_management_port -Djava.rmi.server.hostname=$rmi_host -Dcom.sun.management.jmxremote.ssl=false -Dlog.base=$sphinx-console_path -Dfile.encoding=UTF8 -jar $agent_path/sphinx-console-coordinator-agent.jar > $sphinx-console_path/logs/agent.log.gz 2>&1 & echo \$!"
#sudo -u $RUNAS sh -c "$CMD" > "$PIDFILE"
PID=`sudo -u $RUNAS sh -c "$CMD"`
echo "$PID" > $PIDFILE


MSG="sphinx-console agent start:  $(date +%m/%d/%Y'  '%X'  '%Z'('%:z')')"
sudo -u $RUNAS tee -a $sphinx-console_path/logs/agentStartStop.log <<<$MSG>/dev/null
#echo $'\n'"sphinx-console agent start:"  $(date +%m/%d/%Y'  '%X'  '%Z'('%:z')')  >> $sphinx-console_path/logs/agentStartStop.log

if [ -z $PIDFILE ]; then
	print "Fail"
else
	PID=`cat $PIDFILE`
 	echo $PID
 	printf "Ok"$'\n'
fi
