# Define all possible log locations
$candidatePaths = @(
    "$env:ProgramData\Logishrd\LogiOptionsPlus\log",
    "$env:LOCALAPPDATA\LogiOptionsPlus\log",
    "$env:APPDATA\Logishrd\LogiOptionsPlus\log"
)

$logDir = $null

# Find the first existing directory
foreach ($path in $candidatePaths) {
    if (Test-Path $path) {
        $logDir = $path
        break
    }
}

if ($logDir) {
    $latestLog = Get-ChildItem $logDir -Filter "logioptionsplus_agent*.log" | Sort-Object LastWriteTime -Descending | Select-Object -First 1

    if ($latestLog) {
        Write-Host "[LOG] Found log file: $($latestLog.FullName)" -ForegroundColor Cyan

        # Read the last 500 lines
        $content = Get-Content $latestLog.FullName -Tail 500

        $foundEvents = $false

        foreach ($line in $content) {
            # Search for key terms
            if ($line -match "Plugin" -or $line -match "10134" -or $line -match "hackathon" -or $line -match "server") {
                if ($line -match "Error" -or $line -match "Failed" -or $line -match "Exception" -or $line -match "reject") {
                    Write-Host $line -ForegroundColor Red
                } elseif ($line -match "listening") {
                    Write-Host $line -ForegroundColor Green
                } else {
                    Write-Host $line -ForegroundColor Gray
                }
                $foundEvents = $true
            }
        }

        if (-not $foundEvents) {
            Write-Host "`n[ANALYSIS] No plugin events found." -ForegroundColor Yellow
            Write-Host "This confirms that the Plugin System is NOT starting."
            Write-Host "1. Developer Mode is likely OFF."
            Write-Host "2. Or the app needs a full restart (Task Manager -> End Task)."
        }
    } else {
        Write-Host "ERROR: Log folder exists but no agent logs found." -ForegroundColor Red
    }
} else {
    Write-Host "CRITICAL ERROR: Logitech Logs not found in any standard location." -ForegroundColor Red
    Write-Host "Is Logitech Options+ installed correctly?"
}