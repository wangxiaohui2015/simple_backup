#!/bin/bash

current_dir=$(cd $(dirname $0);pwd)

java -jar ${current_dir}/simplebackup-restore-2.0.0-RELEASE-jar-release.jar
