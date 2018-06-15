#!/bin/bash
logsfilepath=`find $HOME -mtime +20 -name "*.log"`;

echo `ls -lh $logsfilepath`;

exit;