$location = "F:\DeeperDark\src\main\resources\data\minecraft\recipe"
if (!(Test-Path $location)) {
    Write-Output "Path not found: $location"
    exit
}
Set-Location $location

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

foreach ($wood in $woods) {
    foreach ($recipe in $recipes) {
        $itemName = "minecraft:${wood}_planks"
        $resultId = "minecraft:${wood}$($recipe.suffix)"
        $count = $recipe.count

        $content = "{`r`n  `"type`": `"minecraft:stonecutting`",`r`n  `"ingredient`": `"$itemName`",`r`n  `"result`": {`r`n    `"id`": `"$resultId`",`r`n    `"count`": $count`r`n  }`r`n}"

        $filename = "${wood}_$($recipe.name)_from_${wood}_planks_stonecutting.json"

        Set-Content -Path $filename -Value $content -Force
    }
}
Write-Output "Done generating recipes."

