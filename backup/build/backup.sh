#!/bin/bash

current_dir=$(cd $(dirname $0);pwd)

java -DrootDir=${current_dir} -Dlog4j.configuration=file:${current_dir}/conf/log4j.properties -jar ${current_dir}/simplebackup-backup-3.0.0-RELEASE-jar-with-dependencies.jar
