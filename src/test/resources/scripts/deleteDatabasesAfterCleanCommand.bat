@echo off
setlocal

REM Change directory to the script’s own location
cd /d "%~dp0"

del ..\databases\*.db

endlocal