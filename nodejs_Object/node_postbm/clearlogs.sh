#!/bin/sh
echo "============删除20天之后的日志文件=============";
alias ll='ls -lhG';
logsfile="${HOME}/";

files=$(find $logsfile -mtime +20 -name "*.log");
echo $files;
if [ ${#files} -eq 0 ];
then 
    echo "not is remove files!";
else
    ll ${files};
fi
