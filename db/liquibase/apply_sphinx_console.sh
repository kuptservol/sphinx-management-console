#!/bin/bash
export NLS_LANG=AMERICAN_AMERICA.AL32UTF8

log()
{
    echo "[$(date +%F\ %T)] $@"
}

logn()
{
    echo -n "[$(date +%F\ %T)] $@"
}

log "+-----------------+"
log "|    sphinx-console    |"
log "|     UPDATE      |"
log "+-----------------+"

# fill variables. begin
DEFAULT_URL="jdbc:mysql://localhost:3306"
DEFAULT_USR="sphinx-console"

if [ _$1 == "_" ]; then # url not specified
    logn "Enter url for database (default is $DEFAULT_URL):"
    read URL
    if [ _$URL == "_" ]; then
        URL=$DEFAULT_URL;
    fi
else
    URL=$1;
fi

if [ _$2 == "_" ]; then # password not specified
    logn "Enter password for database superuser:"
    read -s EDB_PWD
else
    EDB_PWD=$2;
fi

if [ _$3 == "_" ]; then # user not specified
    logn "Enter superuser name (default is $DEFAULT_USR):"
    read EDB_USR
    if [ _$EDB_USR == "_" ]; then
        EDB_USR=$DEFAULT_USR;
    fi
else
    EDB_USR=$3;
fi

EDB_PARAMS=$4" "$5
# fill variables. end

log "Running UPDATESQL using url: $URL"

./liquibase --driver=com.mysql.jdbc.Driver --username=$EDB_USR --changeLogFile=initial_sphinx-console.xml --url="$URL/sphinx-console?useUnicode=true&characterEncoding=UTF-8" --password=$EDB_PWD update $EDB_PARAMS
if [ $? -ne 0 ]
then
exit 1
fi

./liquibase --driver=com.mysql.jdbc.Driver --username=$EDB_USR --changeLogFile=prerun_sphinx-console.xml --url="$URL/sphinx-console?useUnicode=true&characterEncoding=UTF-8" --password=$EDB_PWD update $EDB_PARAMS
if [ $? -ne 0 ]
then
exit 1
fi

./liquibase --driver=com.mysql.jdbc.Driver --username=$EDB_USR --changeLogFile=master_sphinx-console.xml --url="$URL/sphinx-console?useUnicode=true&characterEncoding=UTF-8" --password=$EDB_PWD update $EDB_PARAMS
if [ $? -ne 0 ]
then
exit 1
fi

./liquibase --driver=com.mysql.jdbc.Driver --username=$EDB_USR --changeLogFile=postrun_sphinx-console.xml --url="$URL/sphinx-console?useUnicode=true&characterEncoding=UTF-8" --password=$EDB_PWD update $EDB_PARAMS
if [ $? -ne 0 ]
then
exit 1
fi

