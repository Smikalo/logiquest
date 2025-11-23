# Pfade setzen
$root = $PSScriptRoot
Set-Location $root

Write-Host "[INFO] Building..." -ForegroundColor Yellow
dotnet build --configuration Release

# Ordner definieren
$distDir = Join-Path $root "dist"
$metaDir = Join-Path $distDir "metadata"
$dllSource = Join-Path $root "bin\Release\net8.0\LogiQuestPlugin.dll"
$yamlPath = Join-Path $metaDir "LoupedeckPackage.yaml"
$iconPath = Join-Path $metaDir "Icon256x256.png"

# 1. Ordner säubern und erstellen
if (Test-Path $distDir) { Remove-Item $distDir -Recurse -Force; Start-Sleep -Milliseconds 200 }
New-Item -ItemType Directory -Path $metaDir -Force | Out-Null

# 2. DLL ins Root von dist kopieren
if (Test-Path $dllSource) {
    Copy-Item $dllSource "$distDir\"
} else {
    Write-Host "[ERROR] DLL not found. Build first." -ForegroundColor Red; exit
}

# 3. YAML erstellen (Format angepasst an ExampleStocksPlugin)
# WICHTIG: 'type: plugin4' und 'name' statt 'pluginName'
$yamlContent = @"
type: plugin4
name: LogiQuest
displayName: "LogiQuest Battle"
description: "Hackathon Plugin"
version: 1.0.0
author: "Hackathon Team"
pluginFileName: LogiQuestPlugin.dll
pluginFolderWin: .
supportedDevices:
  - LoupedeckCtFamily
minimumLoupedeckVersion: 6.0
license: MIT
"@

# Encoding UTF8 ohne BOM erzwingen
$enc = New-Object System.Text.UTF8Encoding $False
[System.IO.File]::WriteAllText($yamlPath, $yamlContent, $enc)

# 4. Gültiges Icon erstellen
$base64Png = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+ip1sAAAAASUVORK5CYII="
[System.IO.File]::WriteAllBytes($iconPath, [System.Convert]::FromBase64String($base64Png))

# 5. Packen
Write-Host "[INFO] Packing..." -ForegroundColor Cyan
LogiPluginTool pack "$distDir" "LogiQuest.lplug4"

# 6. Installieren
if (Test-Path "LogiQuest.lplug4") {
    Write-Host "[INFO] Installing..." -ForegroundColor Green
    LogiPluginTool install "LogiQuest.lplug4"
    Write-Host "`n[DONE] Restart Logi Options+ now!" -ForegroundColor Magenta
} else {
    Write-Host "[ERROR] Pack failed." -ForegroundColor Red
}