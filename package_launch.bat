REM Build package
call mvn package -Dmaven.test.skip

REM start server
heroku local web -f Procfile.win