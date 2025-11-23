# Clear the result file (create empty file or overwrite existing)
$resultFile = "result.txt"
"" | Set-Content -Path $resultFile

# Directories to exclude
$excludeDirs = @(".gradle", ".idea", ".kotlin", "build")

# Extensions to include
$extensions = @(
    ".ts",
    ".kt",
    ".kts",
    ".properties",
    ".bat",
    ".toml",
    ".yml",
    ".test",
    ".local"
)

# Collect matching files, excluding node_modules, dist, generated, and result.txt itself
$files = Get-ChildItem -Path . -Recurse -File | Where-Object {
    $fullPath = $_.FullName

    # exclude result.txt
    if ($_.Name -eq $resultFile) {
        return $false
    }

    # skip files inside excluded directories
    foreach ($dir in $excludeDirs) {
        if ($fullPath -like "*\${dir}\*") {
            return $false
        }
    }

    # match extension or Dockerfile
    ($extensions -contains $_.Extension) -or ($_.Name -eq "Dockerfile")
}

foreach ($file in $files) {
    # Header
    Add-Content -Path $resultFile -Value "===== FILE: $($file.FullName) ====="

    # File contents
    Get-Content -Path $file.FullName | Add-Content -Path $resultFile

    # Separator (blank lines)
    Add-Content -Path $resultFile -Value "`r`n`r`n"
}

Write-Host "Done! Results saved to $resultFile"
