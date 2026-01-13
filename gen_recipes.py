import os
import json

woods = ["oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped"]
output_dir = "F:/DeeperDark/src/main/resources/data/minecraft/recipe"
os.makedirs(output_dir, exist_ok=True)

for wood in woods:
    stairs_file = f"{output_dir}/{wood}_stairs_from_{wood}_planks_stonecutting.json"
    slab_file = f"{output_dir}/{wood}_slab_from_{wood}_planks_stonecutting.json"

    with open(stairs_file, "w") as f:
        json.dump({
            "type": "minecraft:stonecutting",
            "ingredient": {
                "item": f"minecraft:{wood}_planks"
            },
            "result": {
                "count": 1,
                "id": f"minecraft:{wood}_stairs"
            }
        }, f, indent=2)

    with open(slab_file, "w") as f:
        json.dump({
            "type": "minecraft:stonecutting",
            "ingredient": {
                "item": f"minecraft:{wood}_planks"
            },
            "result": {
                "count": 2,
                "id": f"minecraft:{wood}_slab"
            }
        }, f, indent=2)

    print(f"Generated {stairs_file} and {slab_file}")

