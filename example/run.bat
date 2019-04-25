@echo off
java --version > nul
if %errorlevel% neq 0 (
  echo Please install JDK before running！！！
  pause
) else (
  echo Start unpacking......
  cd /d %~dp0 && UnpackABFile.jar "%~dp0\soulbackup.ab" "%~dp0\" " "
  echo -----------------------------Soul-----------------------------
  pause
)
