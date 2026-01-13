# Generate Minecraft source code using Fabric Loom and Yarn mappings
# This uses Gradle's genSources task which properly decompiles and maps the code

Write-Host "Generating Minecraft sources using Fabric Loom + Yarn mappings..." -ForegroundColor Green
Write-Host ""

$startTime = Get-Date

# Run Gradle genSources task
Write-Host "Running: gradlew genSources" -ForegroundColor Cyan
Write-Host "This will download, decompile, and map Minecraft sources..." -ForegroundColor Yellow
Write-Host "This may take several minutes on first run..." -ForegroundColor Yellow
Write-Host ""

try {
    # Run gradlew genSources which will:
    # 1. Download Minecraft jar
    # 2. Apply Yarn mappings
    # 3. Decompile to readable Java source
    # 4. Cache in .gradle directory

    $process = Start-Process -FilePath ".\gradlew.bat" -ArgumentList "genSources", "--refresh-dependencies" -Wait -NoNewWindow -PassThru

    if ($process.ExitCode -eq 0) {
        $endTime = Get-Date
        $duration = $endTime - $startTime

        Write-Host ""
        Write-Host "Successfully generated Minecraft sources!" -ForegroundColor Green
        Write-Host "Time taken: $([math]::Round($duration.TotalMinutes, 1)) minutes" -ForegroundColor White
        Write-Host ""

        # Find the generated sources location
        $gradleCache = "$env:USERPROFILE\.gradle\caches\fabric-loom"

        Write-Host "Mapped sources are cached in Gradle's fabric-loom cache:" -ForegroundColor Cyan
        Write-Host "$gradleCache" -ForegroundColor White
        Write-Host ""

        # Try to find and copy the sources to ref-decompiled
        Write-Host "Searching for generated sources..." -ForegroundColor Cyan

        $minecraftVersion = "1.21.11"
        $yarnMappings = "1.21.11+build.4"

        # Look for the sources jar
        $possiblePaths = @(
            "$gradleCache\*\$minecraftVersion\$yarnMappings\minecraft-*-sources.jar",
            "$gradleCache\minecraftMaven\net\minecraft\*\$minecraftVersion\*-sources.jar",
            "$env:USERPROFILE\.gradle\caches\fabric-loom\minecraftMaven\net\minecraft\minecraft\$minecraftVersion\*-sources.jar"
        )

        $sourcesJar = $null
        foreach ($pattern in $possiblePaths) {
            $found = Get-ChildItem -Path $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($found) {
                $sourcesJar = $found.FullName
                break
            }
        }

        if ($sourcesJar -and (Test-Path $sourcesJar)) {
            Write-Host "Found sources jar: $sourcesJar" -ForegroundColor Green
            Write-Host ""
            Write-Host "Extracting to ref-decompiled..." -ForegroundColor Cyan

            $outputDir = "F:\DeeperDark\ref-decompiled"

            # Clean output directory
            if (Test-Path $outputDir) {
                Remove-Item -Path $outputDir -Recurse -Force
            }
            New-Item -Path $outputDir -ItemType Directory -Force | Out-Null

            # Extract the jar
            Add-Type -AssemblyName System.IO.Compression.FileSystem
            [System.IO.Compression.ZipFile]::ExtractToDirectory($sourcesJar, $outputDir)

            # Count extracted files
            $javaFiles = Get-ChildItem -Path $outputDir -Filter "*.java" -Recurse -File
            $totalFiles = $javaFiles.Count

            Write-Host ""
            Write-Host "Extraction complete!" -ForegroundColor Green
            Write-Host "Total .java files extracted: $totalFiles" -ForegroundColor White
            Write-Host "Location: $outputDir" -ForegroundColor White

            # Create summary
            $summary = @"
Minecraft Sources Generation Summary
Generated: $(Get-Date)

Minecraft Version: $minecraftVersion
Yarn Mappings: $yarnMappings
Source Jar: $sourcesJar
Output: $outputDir

Total .java files: $totalFiles
Time taken: $([math]::Round($duration.TotalMinutes, 1)) minutes

These sources are decompiled Minecraft code with Yarn mappings applied,
making them readable and usable for mod development.
"@

            Set-Content -Path (Join-Path $outputDir "summary.txt") -Value $summary
            Write-Host ""
            Write-Host "Done! Mapped Minecraft sources are in: $outputDir" -ForegroundColor Green

        } else {
            Write-Host "Could not automatically locate sources jar." -ForegroundColor Yellow
            Write-Host ""
            Write-Host "The sources have been generated and are in Gradle's cache." -ForegroundColor Cyan
            Write-Host "You can find them in your IDE (IntelliJ/Eclipse) by:" -ForegroundColor Cyan
            Write-Host "  1. Refresh the Gradle project" -ForegroundColor White
            Write-Host "  2. Navigate to External Libraries" -ForegroundColor White
            Write-Host "  3. Look for 'net.minecraft:minecraft' sources" -ForegroundColor White
            Write-Host ""
            Write-Host "To manually search for sources, check:" -ForegroundColor Cyan
            Write-Host "  $gradleCache" -ForegroundColor White
        }

    } else {
        Write-Host ""
        Write-Host "Gradle task failed with exit code: $($process.ExitCode)" -ForegroundColor Red
        Write-Host "Check the output above for errors." -ForegroundColor Red
        exit 1
    }

} catch {
    Write-Host ""
    Write-Host "Error generating sources: $_" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

