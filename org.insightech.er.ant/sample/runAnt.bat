@echo off

set EQUINOX_LAUNCHER_JAR=c:\eclipse\4.3-2\plugins\org.eclipse.equinox.launcher_1.3.0.v20130327-1440.jar
set BUILD_FILE=./build.xml

if "%EQUINOX_LAUNCHER_JAR%" == "" goto HELP

pushd %~dp0
echo Launching equinox ...
java -jar %EQUINOX_LAUNCHER_JAR% -application org.eclipse.ant.core.antRunner -buildfile %BUILD_FILE% %1
goto end

:HELP
echo EQUINOX_LAUNCHER_JAR environment variable is not defined
goto end

:end
popd
pause
