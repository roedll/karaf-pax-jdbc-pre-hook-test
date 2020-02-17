@ECHO OFF

SET WD=%CD%
SET HD=%~dp0
SET PARAMS=%*

SET KARAF_TITLE=Pax-JDBC-PreHook-Test
SET PAUSE=true

ECHO Starting Pax-JDBC-PreHook-Test (${project.version})...
CALL "%HD%bin\karaf.bat" %PARAMS%
