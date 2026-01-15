import os
import json

# Directory containing vanilla recipes
VANILLA_RECIPE_DIR = "F:/DeeperDark/ref-decompiled/data/minecraft/recipe/"
# Output directory for modded blast furnace recipes
MOD_RECIPE_DIR = "F:/DeeperDark/src/main/resources/data/minecraft/recipe/"

# List of food item IDs (expand as needed)
FOOD_ITEMS = set([
    "apple", "bread", "carrot", "potato", "baked_potato", "beetroot", "beetroot_soup", "cake", "cookie", "cooked_beef", "cooked_chicken", "cooked_cod", "cooked_mutton", "cooked_porkchop", "cooked_rabbit", "cooked_salmon", "dried_kelp", "enchanted_golden_apple", "golden_apple", "golden_carrot", "honey_bottle", "melon_slice", "mushroom_stew", "pumpkin_pie", "rabbit_stew", "sweet_berries", "glow_berries", "suspicious_stew", "tropical_fish", "rotten_flesh", "spider_eye", "poisonous_potato"
])

# Helper to check if an item is food
def is_food(item_id):
    return item_id.replace("minecraft:", "") in FOOD_ITEMS

for filename in os.listdir(VANILLA_RECIPE_DIR):
    if not filename.endswith(".json"): continue
    path = os.path.join(VANILLA_RECIPE_DIR, filename)
    with open(path, "r") as f:
        try:
            data = json.load(f)
        except Exception:
            continue
    # Only process furnace (smelting) recipes
    if data.get("type") not in ("minecraft:smelting", "minecraft:furnace"):  # Accept both types
        continue
    result = data.get("result")
    if isinstance(result, dict):
        result_id = result.get("id")
    else:
        result_id = result
    if not result_id or is_food(result_id):
        continue
    # Clean output filename: <result>_blasting.json
    result_name = result_id.replace("minecraft:", "")
    out_filename = f"{result_name}_blasting.json"
    out_path = os.path.join(MOD_RECIPE_DIR, out_filename)
    # Generate blast furnace recipe
    blast_recipe = {
        "type": "minecraft:blasting",
        "category": data.get("category", "blocks"),
        "cookingtime": max(100, int(data.get("cookingtime", 200) // 2)),
        "experience": data.get("experience", 0.1),
        "group": data.get("group", ""),
        "ingredient": data.get("ingredient"),
        "result": {"id": result_id}
    }
    with open(out_path, "w") as out:
        json.dump(blast_recipe, out, indent=2)
    print(f"Generated {out_path}")
