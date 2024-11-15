@echo off

setlocal

set current_dir=%~dp0

set current_dir=%current_dir:~0,-1%

java -DrootDir=%current_dir% -Dlog4j.configuration=file:%current_dir%\conf\log4j.properties -jar %current_dir%\simplebackup-restore-3.0.0-RELEASE-jar-with-dependencies.jar %*

endlocal