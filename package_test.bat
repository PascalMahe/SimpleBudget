REM Build package
call mvn package -Dmaven.test.skip

REM start server in another window
start "Heroku" heroku local web -f Procfile.win

REM wait 15 seconds for server to boot
TIMEOUT 15

REM launch tests
call mvn test

REM kill server
taskkill /FI "WINDOWTITLE eq Heroku*"