$woods = @("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped")
$recipes = @(
    @{suffix="_stairs"; count=1; name="stairs"},
    @{suffix="_slab"; count=2; name="slab"},
    @{suffix="_fence"; count=1; name="fence"},
    @{suffix="_fence_gate"; count=1; name="fence_gate"},
    @{suffix="_pressure_plate"; count=1; name="pressure_plate"},
    @{suffix="_button"; count=4; name="button"},
    @{suffix="_sign"; count=1; name="sign"}
)

$outputDir = "F:\DeeperDark\src\main\resources\data\minecraft\recipe"

foreach ($wood in $woods) {
    foreach ($recipe in $recipes) {
        $itemName = "minecraft:${wood}_planks"
        $resultId = "minecraft:${wood}$($recipe.suffix)"
        $count = $recipe.count

        # Using string format required by user: ingredient as string
        $json = @"
{
  "type": "minecraft:stonecutting",
  "ingredient": "$itemName",
  "result": {
    "id": "$resultId",
    "count": $count
  }
}
"@
        $filename = "${wood}_$($recipe.name)_from_${wood}_planks_stonecutting.json"
        $path = Join-Path $outputDir $filename
        $json | Out-File -FilePath $path -Encoding ASCII -Force
        Write-Host "Generated $path"
    }
}

