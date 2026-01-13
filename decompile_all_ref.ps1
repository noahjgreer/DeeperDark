# Decompile all class files from ref directory using CFR
# This processes each class file individually with proper error handling

$refDir = "F:\DeeperDark\ref"
$outputDir = "F:\DeeperDark\ref-decompiled"
$cfrJar = "F:\DeeperDark\cfr.jar"

Write-Host "=== Minecraft Class File Decompiler ===" -ForegroundColor Cyan
Write-Host ""

# Get all class files
Write-Host "Scanning for .class files..." -ForegroundColor Yellow
$classFiles = Get-ChildItem -Path $refDir -Filter "*.class" -Recurse -File
$totalFiles = $classFiles.Count

Write-Host "Found $totalFiles .class files" -ForegroundColor Green
Write-Host ""

if ($totalFiles -eq 0) {
    Write-Host "ERROR: No .class files found!" -ForegroundColor Red
    exit 1
}

# Start decompilation
Write-Host "Starting decompilation (this will take several minutes)..." -ForegroundColor Cyan
Write-Host ""

$startTime = Get-Date
$successful = 0
$failed = 0
$processed = 0

foreach ($classFile in $classFiles) {
    $processed++

    # CFR automatically recreates the full package path from the class file
    # So we just need to point it to the base output directory
    $relativePath = $classFile.FullName.Substring($refDir.Length + 1)
    $javaFileName = [System.IO.Path]::ChangeExtension($relativePath, ".java")
    $expectedOutput = Join-Path $outputDir $javaFileName

    # Decompile using CFR - it will recreate the package structure automatically
    try {
        $result = & java -jar $cfrJar $classFile.FullName --outputdir $outputDir --silent true 2>&1

        if (Test-Path $expectedOutput) {
            $successful++
        } else {
            $failed++
        }
    } catch {
        $failed++
    }

    # Progress update every 250 files
    if ($processed % 250 -eq 0) {
        $percent = [math]::Round(($processed / $totalFiles) * 100, 1)
        $elapsed = ((Get-Date) - $startTime).TotalMinutes
        $rate = $processed / $elapsed
        $remaining = ($totalFiles - $processed) / $rate

        Write-Host "[$percent%] Processed: $processed / $totalFiles | Success: $successful | Failed: $failed | ETA: $([math]::Round($remaining, 1))min" -ForegroundColor Cyan
    }
}

$endTime = Get-Date
$duration = $endTime - $startTime

Write-Host ""
Write-Host "=== Decompilation Complete ===" -ForegroundColor Green
Write-Host "Total files: $totalFiles" -ForegroundColor White
Write-Host "Successful: $successful" -ForegroundColor Green
Write-Host "Failed: $failed" -ForegroundColor $(if ($failed -gt 0) { "Yellow" } else { "Green" })
Write-Host "Time: $([math]::Round($duration.TotalMinutes, 1)) minutes" -ForegroundColor White

# Create summary
$summary = @"
Minecraft Decompilation Summary
================================
Date: $(Get-Date)

Source Directory: $refDir
Output Directory: $outputDir

Results:
- Total .class files: $totalFiles
- Successfully decompiled: $successful
- Failed: $failed
- Success rate: $([math]::Round(($successful / $totalFiles) * 100, 1))%
- Time taken: $([math]::Round($duration.TotalMinutes, 1)) minutes

These are decompiled Minecraft class files from the ref directory,
converted to readable Java source code for reference purposes.
"@

Set-Content -Path (Join-Path $outputDir "DECOMPILATION_SUMMARY.txt") -Value $summary

Write-Host ""
Write-Host "Output directory: $outputDir" -ForegroundColor Cyan
Write-Host "Summary file created: DECOMPILATION_SUMMARY.txt" -ForegroundColor Cyan

