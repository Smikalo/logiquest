# Fehler ignorieren, um das Skript nicht zu stoppen
$ErrorActionPreference = "SilentlyContinue"
Set-Location $PSScriptRoot

Write-Host "[1/4] Killing Logitech processes..." -ForegroundColor Red
taskkill /F /IM logioptionsplus_agent.exe /T
taskkill /F /IM logioptionsplus_appbroker.exe /T
taskkill /F /IM logioptionsplus_updater.exe /T
taskkill /F /IM logioptionsplus.exe /T
taskkill /F /IM LogiPluginService.exe /T

# Warten, bis Windows die Dateien freigibt
Start-Sleep -Seconds 3

$pluginFile = "LogiQuest.lplug4"

if (Test-Path $pluginFile) {
    Write-Host "[2/4] Manual Installation..." -ForegroundColor Cyan

    # Zielordner für Plugins (Windows Standard)
    $targetDir = "$env:LOCALAPPDATA\Logi\LogiPluginService\Plugins\LogiQuest"
    
    Write-Host "   Target: $targetDir"
    
    # Alten Ordner löschen
    if (Test-Path $targetDir) { 
        Remove-Item $targetDir -Recurse -Force 
    }
    New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
    
    # Trick: .lplug4 ist eigentlich ein ZIP. Wir benennen es um.
    $zipFile = "temp_install.zip"
    Copy-Item $pluginFile $zipFile -Force
    
    # Entpacken direkt in den Zielordner
    Expand-Archive -Path $zipFile -DestinationPath $targetDir -Force
    Remove-Item $zipFile
    
    Write-Host "   [OK] Plugin files copied." -ForegroundColor Green
    
    # Logi Options+ neu starten
    Write-Host "[3/4] Restarting Logi Options+..." -ForegroundColor Magenta
    $launcher = "C:\Program Files\LogiOptionsPlus\logioptionsplus.exe"
    
    if (Test-Path $launcher) { 
        Start-Process $launcher 
        Write-Host "[4/4] Done! Please wait for the app to load." -ForegroundColor Green
    } else {
        Write-Host "[!] Could not auto-start. Please open Logi Options+ manually." -ForegroundColor Yellow
    }
    
} else {
    Write-Host "[ERROR] LogiQuest.lplug4 not found. Run force_install.ps1 first." -ForegroundColor Red
}