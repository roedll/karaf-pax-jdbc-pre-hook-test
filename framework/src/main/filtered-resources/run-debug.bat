@ECHO OFF

SET WD=%CD%
SET HD=%~dp0
SET PARAMS=%*

SET KARAF_DEBUG=TRUE

CALL "%HD%run.bat" %PARAMS%
