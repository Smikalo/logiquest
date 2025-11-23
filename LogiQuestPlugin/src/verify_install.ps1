$pluginDir = "$env:LOCALAPPDATA\Logi\LogiPluginService\Plugins\LogiQuest"

Write-Host "🔍 Checking Installation at: $pluginDir" -ForegroundColor Cyan

if (Test-Path $pluginDir) {
    $dll = "$pluginDir\LogiQuestPlugin.dll"
    $yaml = "$pluginDir\metadata\LoupedeckPackage.yaml"
    
    if (Test-Path $dll) { Write-Host "✅ DLL Found." -ForegroundColor Green } 
    else { Write-Host "❌ DLL MISSING!" -ForegroundColor Red }
    
    if (Test-Path $yaml) { 
        Write-Host "✅ YAML Found." -ForegroundColor Green 
        Get-Content $yaml | ForEach-Object { Write-Host "   $_" -ForegroundColor Gray }
    } 
    else { Write-Host "❌ YAML MISSING!" -ForegroundColor Red }
    
} else {
    Write-Host "❌ Plugin folder does not exist!" -ForegroundColor Red
}

# Check if the Service process is running
$service = Get-Process -Name "LogiPluginService" -ErrorAction SilentlyContinue
if ($service) {
    Write-Host "✅ LogiPluginService is running (PID: $($service.Id))" -ForegroundColor Green
} else {
    Write-Host "F"
}