# Extract and decompile Minecraft sources from Fabric Loom cache
# This uses the merged-intermediary jar that was created by genSources

$outputDir = "F:\DeeperDark\ref-decompiled"
$gradleCache = "$env:USERPROFILE\.gradle\caches\fabric-loom"
$minecraftVersion = "1.21.11"
$yarnVersion = "1.21.11+build.4"

Write-Host "Extracting Minecraft sources from Loom cache..." -ForegroundColor Green
Write-Host ""

# Find the merged jar
$mergedJarPath = "$gradleCache\minecraftMaven\net\minecraft\minecraft-merged-intermediary\$minecraftVersion-net.fabricmc.yarn.1_21_11.$yarnVersion-v2"
$mergedJar = Get-ChildItem -Path $mergedJarPath -Filter "*.jar" -File | Where-Object { $_.Name -notmatch "backup" } | Select-Object -First 1

if (-not $mergedJar) {
    Write-Host "Could not find merged Minecraft jar!" -ForegroundColor Red
    Write-Host "Expected location: $mergedJarPath" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Searching for any merged jar..." -ForegroundColor Cyan
    $mergedJar = Get-ChildItem -Path "$gradleCache\minecraftMaven\net\minecraft" -Recurse -Filter "*merged*$minecraftVersion*.jar" -File |
        Where-Object { $_.Name -notmatch "backup" } |
        Select-Object -First 1
}

if (-not $mergedJar) {
    Write-Host "Error: No merged Minecraft jar found in Gradle cache!" -ForegroundColor Red
    Write-Host "Please run 'gradlew genSources' first" -ForegroundColor Yellow
    exit 1
}

Write-Host "Found merged jar: $($mergedJar.FullName)" -ForegroundColor Green
Write-Host ""

# Clean and create output directory
if (Test-Path $outputDir) {
    Write-Host "Cleaning existing ref-decompiled directory..." -ForegroundColor Yellow
    Remove-Item -Path $outputDir -Recurse -Force
}
New-Item -Path $outputDir -ItemType Directory -Force | Out-Null

Write-Host "Extracting jar contents to ref-decompiled..." -ForegroundColor Cyan
Write-Host "This may take a minute..." -ForegroundColor Yellow

try {
    # Extract the jar using .NET
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::ExtractToDirectory($mergedJar.FullName, $outputDir)

    Write-Host ""
    Write-Host "Extraction complete!" -ForegroundColor Green

    # Count files
    $classFiles = Get-ChildItem -Path $outputDir -Filter "*.class" -Recurse -File
    $totalClasses = $classFiles.Count

    Write-Host "Extracted $totalClasses .class files" -ForegroundColor White
    Write-Host ""

    # Now decompile using CFR (which handles mapped code better than raw obfuscated code)
    Write-Host "Decompiling classes using CFR..." -ForegroundColor Cyan
    Write-Host "This will take several minutes..." -ForegroundColor Yellow
    Write-Host ""

    $cfrJar = "F:\DeeperDark\cfr.jar"
    $startTime = Get-Date

    # Decompile each class file individually
    $processed = 0
    $successful = 0

    foreach ($classFile in $classFiles) {
        $processed++

        # Show progress every 500 files
        if ($processed % 500 -eq 0) {
            $percent = [math]::Round(($processed / $totalClasses) * 100, 1)
            $elapsed = ((Get-Date) - $startTime).TotalMinutes
            $rate = $processed / $elapsed
            $remaining = ($totalClasses - $processed) / $rate
            Write-Host "Progress: $processed / $totalClasses ($percent%) - Elapsed: $([math]::Round($elapsed, 1))min - ETA: $([math]::Round($remaining, 1))min" -ForegroundColor Cyan
        }

        # Get the directory for this class file
        $classDir = Split-Path -Parent $classFile.FullName

        # Decompile in place
        try {
            java -jar $cfrJar $classFile.FullName --outputdir $classDir --silent true 2>&1 | Out-Null
            $successful++
        } catch {
            # Ignore errors
        }
    }

    $endTime = Get-Date
    $duration = $endTime - $startTime

    # Remove class files
    Write-Host ""
    Write-Host "Cleaning up .class files..." -ForegroundColor Cyan
    Get-ChildItem -Path $outputDir -Filter "*.class" -Recurse -File | Remove-Item -Force

    # Count Java files
    $javaFiles = Get-ChildItem -Path $outputDir -Filter "*.java" -Recurse -File
    $totalJava = $javaFiles.Count

    Write-Host ""
    Write-Host "Decompilation complete!" -ForegroundColor Green
    Write-Host "Total .class files: $totalClasses" -ForegroundColor White
    Write-Host "Total .java files: $totalJava" -ForegroundColor White
    Write-Host "Time taken: $([math]::Round($duration.TotalMinutes, 1)) minutes" -ForegroundColor White

    # Create summary
    $summary = @"
Minecraft Sources Extraction Summary
Generated: $(Get-Date)

Minecraft Version: $minecraftVersion
Yarn Mappings: $yarnVersion
Source Jar: $($mergedJar.FullName)
Output: $outputDir

Total .class files: $totalClasses
Total .java files: $totalJava
Time taken: $([math]::Round($duration.TotalMinutes, 1)) minutes

These sources are decompiled from the Yarn-mapped Minecraft jar,
making them readable for mod development reference.
"@

    Set-Content -Path (Join-Path $outputDir "summary.txt") -Value $summary

    Write-Host ""
    Write-Host "Done! Mapped Minecraft sources are in: $outputDir" -ForegroundColor Green

} catch {
    Write-Host ""
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

