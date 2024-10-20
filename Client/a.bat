@echo off
setlocal enabledelayedexpansion

set "root_dir=%cd%"

for /f "delims=" %%f in ('dir /b /a-d /s "%root_dir%\*.*"') do (
  set "file_size=%%~zf"
  if !file_size! gtr 100000000 (
    echo %%~dpnf
  )
)
