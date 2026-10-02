@echo off
title Library Management System

cd /d "%~dp0"

call "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" compile exec:java -Dexec.mainClass="com.hms.Main"

echo.
echo ========================================
echo Program finished.
echo ========================================
pause