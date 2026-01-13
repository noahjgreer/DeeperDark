# Decompile all .class files from ref folder to ref-decompiled folder
# Uses CFR decompiler

$refDir = "F:\DeeperDark\ref"
$outputDir = "F:\DeeperDark\ref-decompiled"
$cfrJar = "F:\DeeperDark\cfr.jar"

Write-Host "Starting decompilation process..." -ForegroundColor Green

# Clean output directory
if (Test-Path $outputDir) {
    Write-Host "Cleaning existing ref-decompiled directory..." -ForegroundColor Yellow
    Remove-Item -Path $outputDir -Recurse -Force
}
New-Item -Path $outputDir -ItemType Directory -Force | Out-Null

# Get all .class files
Write-Host "Finding all .class files..." -ForegroundColor Cyan
$classFiles = Get-ChildItem -Path $refDir -Filter "*.class" -Recurse -File
$totalFiles = $classFiles.Count
Write-Host "Found $totalFiles .class files to decompile" -ForegroundColor Cyan

if ($totalFiles -eq 0) {
    Write-Host "No .class files found in $refDir" -ForegroundColor Red
    exit 1
}

# Decompile using CFR
Write-Host "Decompiling all classes using CFR..." -ForegroundColor Green
Write-Host "This may take several minutes..." -ForegroundColor Yellow

$startTime = Get-Date

# Process each class file individually to ensure all are decompiled
$processed = 0
$successful = 0
$failed = 0

foreach ($classFile in $classFiles) {
    $processed++

    # Show progress every 100 files
    if ($processed % 100 -eq 0) {
        $percent = [math]::Round(($processed / $totalFiles) * 100, 1)
        Write-Host "Progress: $processed / $totalFiles ($percent%) - Success: $successful, Failed: $failed" -ForegroundColor Cyan
    }

    # Calculate relative path and output path
    $relativePath = $classFile.FullName.Substring($refDir.Length + 1)
    $javaFileName = [System.IO.Path]::GetFileNameWithoutExtension($classFile.Name) + ".java"
    $relativeDir = Split-Path -Parent $relativePath
    $outputSubDir = Join-Path $outputDir $relativeDir

    # Create output directory if it doesn't exist
    if (-not (Test-Path $outputSubDir)) {
        New-Item -Path $outputSubDir -ItemType Directory -Force | Out-Null
    }

    # Decompile the class file
    try {
        # Run CFR on this specific class file
        # Output goes to the appropriate subdirectory
        $result = java -jar $cfrJar $classFile.FullName --outputdir $outputSubDir 2>&1

        # Check if the java file was created
        $expectedOutput = Join-Path $outputSubDir $javaFileName
        if (Test-Path $expectedOutput) {
            $successful++
        } else {
            $failed++
            if ($failed -le 10) {
                Write-Host "Failed to decompile: $relativePath" -ForegroundColor Red
            }
        }
    } catch {
        $failed++
        if ($failed -le 10) {
            Write-Host "Error decompiling $relativePath : $_" -ForegroundColor Red
        }
    }
}

$endTime = Get-Date
$duration = $endTime - $startTime

# Count decompiled files
$javaFiles = Get-ChildItem -Path $outputDir -Filter "*.java" -Recurse -File
$decompiled = $javaFiles.Count

Write-Host ""
Write-Host "Decompilation complete!" -ForegroundColor Green
Write-Host "Total .class files: $totalFiles" -ForegroundColor White
Write-Host "Successfully decompiled: $successful" -ForegroundColor Green
Write-Host "Failed to decompile: $failed" -ForegroundColor $(if ($failed -gt 0) { "Yellow" } else { "Green" })
Write-Host "Total .java files found: $decompiled" -ForegroundColor White
Write-Host "Time taken: $([math]::Round($duration.TotalSeconds, 1)) seconds" -ForegroundColor White

# Create summary file
$summary = @"
Decompilation Summary
Generated: $(Get-Date)

Source: $refDir
Output: $outputDir

Total .class files found: $totalFiles
Successfully decompiled: $successful
Failed to decompile: $failed
Total .java files created: $decompiled
Time taken: $([math]::Round($duration.TotalSeconds, 1)) seconds

"@

Set-Content -Path (Join-Path $outputDir "summary.txt") -Value $summary
Write-Host ""
Write-Host "Summary written to $outputDir\summary.txt" -ForegroundColor Green
Write-Host "Done! Decompiled files are in: $outputDir" -ForegroundColor Green

