@echo off
setlocal

set "INTELLIJ_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.2.1"
set "REPO=%~dp0.m2\repository"
if not exist "%REPO%" mkdir "%REPO%"

if not defined JAVA_HOME if exist "%INTELLIJ_HOME%\jbr\bin\java.exe" (
    set "JAVA_HOME=%INTELLIJ_HOME%\jbr"
)
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"

where mvn.cmd >nul 2>&1
if not errorlevel 1 (
    call mvn.cmd -Dmaven.repo.local="%REPO%" %*
    exit /b %errorlevel%
)

set "INTELLIJ_MAVEN=%INTELLIJ_HOME%\plugins\maven\lib\maven3\bin\mvn.cmd"
if not exist "%INTELLIJ_MAVEN%" (
    echo Maven was not found on PATH or in the configured IntelliJ installation.
    exit /b 1
)

call "%INTELLIJ_MAVEN%" -Dmaven.repo.local="%REPO%" %*
exit /b %errorlevel%
