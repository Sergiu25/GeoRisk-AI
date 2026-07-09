@echo off
setlocal
set JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\jbr
set PATH=%JAVA_HOME%\bin;%PATH%
set REPO=%~dp0.m2\repository
if not exist "%REPO%" mkdir "%REPO%"
"C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1\plugins\maven\lib\maven3\bin\mvn.cmd" -Dmaven.repo.local="%REPO%" %*
