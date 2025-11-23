Get-Process | Where-Object {$_.MainModule.FileName -like "*demo.exe*"} | Stop-Process -Force -ErrorAction SilentlyContinue
.\gradlew.bat :composeApp:createDistributable
.\composeApp\build\compose\binaries\main\app\demo\demo.exe
