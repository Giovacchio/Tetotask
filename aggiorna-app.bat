@echo off
chcp 65001 >nul
rem ============================================================
rem  TetoTask - copia la versione web aggiornata dentro l'app Android
rem  Doppio clic su questo file, poi in Android Studio premi Run.
rem ============================================================
set "SRC=%~dp0index.html"
set "DST=%~dp0android\app\src\main\assets\public\index.html"
if not exist "%SRC%" (
  echo Non trovo index.html accanto a questo file.
  pause
  exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -Command "$s=[IO.File]::ReadAllText($env:SRC); $s=$s.Replace('https://www.gstatic.com/firebasejs/9.23.0/','lib/'); [IO.File]::WriteAllText($env:DST,$s,(New-Object System.Text.UTF8Encoding $false))"
if errorlevel 1 (
  echo Qualcosa e' andato storto nella copia.
  pause
  exit /b 1
)
echo.
echo  Fatto! L'app Android ora ha l'ultima versione di TetoTask.
echo  Apri Android Studio e premi Run (il triangolo verde).
echo.
pause
