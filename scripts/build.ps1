<#
.SYNOPSIS
    Local build and test script for Brainrot Reel Tracker (Android).
#>

$projectDir = Split-Path -Parent $PSScriptRoot
Set-Location $projectDir

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "   Brainrot Reel Tracker - Build Helper    " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    Write-Host "[!] Java JDK is not installed or not in PATH." -ForegroundColor Yellow
    Write-Host "    To compile locally, install JDK 17+ or open this project in Android Studio." -ForegroundColor White
    Write-Host "    OR push this project to a free GitHub repo to compile automatically using GitHub Actions!" -ForegroundColor Green
    Exit 0
}

Write-Host "[+] Java found: $($javaCmd.Source)" -ForegroundColor Green

if (Test-Path "$projectDir\gradlew.bat") {
    Write-Host "[+] Running unit tests..." -ForegroundColor Cyan
    & "$projectDir\gradlew.bat" test
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[+] Unit tests PASSED!" -ForegroundColor Green
        Write-Host "[+] Compiling Debug APK..." -ForegroundColor Cyan
        & "$projectDir\gradlew.bat" assembleDebug
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[+] APK built successfully at app/build/outputs/apk/debug/app-debug.apk!" -ForegroundColor Green
        }
    } else {
        Write-Host "[-] Unit tests failed." -ForegroundColor Red
    }
}
