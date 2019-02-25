#!/bin/bash
LOGFILES=`find ${HOME} -mtime +20 -name "*.log"`

if [ ${#LOGFILES} -eq 0 ]

then
    echo "not is remove logfiles!";
else
    # 是否需要进行备份
    # tar -jcvP -f LOGSFILES$(date +%Y-%m-%d).tar.bz2
    rm -rf ${LOGFILES}
fi