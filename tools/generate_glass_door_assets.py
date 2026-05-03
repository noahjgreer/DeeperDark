#!/usr/bin/env python3
"""
Generate blockstate, block model, and item model JSON files for all glass door variants.

Run from the project root:
    python tools/generate_glass_door_assets.py
"""

import json
import os

COLORS = [
    "white", "orange", "magenta", "light_blue", "yellow", "lime",
    "pink", "gray", "light_gray", "cyan", "purple", "blue",
    "brown", "green", "red", "black",
]

BLOCK_VARIANTS = [
    "bottom_left", "bottom_left_open",
    "bottom_right", "bottom_right_open",
    "top_left", "top_left_open",
    "top_right", "top_right_open",
]

ASSETS = "src/main/resources/assets/deeperdark"
BLOCKSTATES_DIR = f"{ASSETS}/blockstates"
MODELS_BLOCK_DIR = f"{ASSETS}/models/block"
MODELS_ITEM_DIR = f"{ASSETS}/models/item"
ITEMS_DIR = f"{ASSETS}/items"

DATA = "src/main/resources/data/deeperdark"
LOOT_TABLES_DIR = f"{DATA}/loot_table/blocks"
RECIPES_DIR = f"{DATA}/recipe"

# Vanilla stained glass item IDs (ingredient for crafting recipes)
STAINED_GLASS_ITEMS = {
    "white":      "minecraft:white_stained_glass",
    "orange":     "minecraft:orange_stained_glass",
    "magenta":    "minecraft:magenta_stained_glass",
    "light_blue": "minecraft:light_blue_stained_glass",
    "yellow":     "minecraft:yellow_stained_glass",
    "lime":       "minecraft:lime_stained_glass",
    "pink":       "minecraft:pink_stained_glass",
    "gray":       "minecraft:gray_stained_glass",
    "light_gray": "minecraft:light_gray_stained_glass",
    "cyan":       "minecraft:cyan_stained_glass",
    "purple":     "minecraft:purple_stained_glass",
    "blue":       "minecraft:blue_stained_glass",
    "brown":      "minecraft:brown_stained_glass",
    "green":      "minecraft:green_stained_glass",
    "red":        "minecraft:red_stained_glass",
    "black":      "minecraft:black_stained_glass",
}


def write_json(path, data):
    with open(path, "w") as f:
        json.dump(data, f, indent=2)
    print(f"  wrote {path}")


def fix_glass_door_blockstate():
    """Fix glass_door.json: swap minecraft: namespace to deeperdark:."""
    path = f"{BLOCKSTATES_DIR}/glass_door.json"
    with open(path) as f:
        data = json.load(f)

    for entry in data["variants"].values():
        entry["model"] = entry["model"].replace(
            "minecraft:block/glass_door",
            "deeperdark:block/glass_door",
        )

    write_json(path, data)
    return data


def generate_stained_blockstate(base_data, color):
    """Clone the base blockstate, redirecting all model refs to the stained variant."""
    prefix = f"{color}_stained_glass_door"
    variants = {}
    for state, entry in base_data["variants"].items():
        new_entry = {"model": entry["model"].replace("deeperdark:block/glass_door", f"deeperdark:block/{prefix}")}
        if "y" in entry:
            new_entry["y"] = entry["y"]
        variants[state] = new_entry
    return {"variants": variants}


def generate_block_model(block_name, variant):
    """One of the 8 directional/open-state models for a door variant."""
    return {
        "parent": f"minecraft:block/door_{variant}",
        "textures": {
            "bottom": f"deeperdark:block/{block_name}_bottom",
            "top": f"deeperdark:block/{block_name}_top",
        },
    }


def generate_item_model(item_name):
    return {
        "parent": "minecraft:item/generated",
        "textures": {"layer0": f"deeperdark:item/{item_name}"},
    }


def generate_loot_table(block_id):
    """Silk-touch-only loot table, matching vanilla glass behaviour."""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "bonus_rolls": 0.0,
                "conditions": [
                    {
                        "condition": "minecraft:match_tool",
                        "predicate": {
                            "predicates": {
                                "minecraft:enchantments": [
                                    {
                                        "enchantments": "minecraft:silk_touch",
                                        "levels": {"min": 1},
                                    }
                                ]
                            }
                        },
                    }
                ],
                "entries": [{"type": "minecraft:item", "name": block_id}],
                "rolls": 1.0,
            }
        ],
        "random_sequence": block_id.replace(":", ":blocks/"),
    }


def generate_recipe(block_id, ingredient_id):
    """Shaped 2×3 recipe yielding 3 doors (vanilla door pattern)."""
    return {
        "type": "minecraft:crafting_shaped",
        "category": "building",
        "group": "glass_door",
        "key": {"#": ingredient_id},
        "pattern": ["##", "##", "##"],
        "result": {"count": 3, "id": block_id},
    }


def generate_dye_recipe():
    """Special recipe definition — all logic lives in DyedGlassDoorRecipe.java."""
    return {"type": "deeperdark:crafting_dyed_glass_door"}


def generate_item_definition(item_name):
    """Item definition file (items/ folder, distinct from models/item/)."""
    return {
        "model": {
            "type": "minecraft:model",
            "model": f"deeperdark:item/{item_name}",
        }
    }


def main():
    print("=== Fixing base glass_door blockstate ===")
    base_data = fix_glass_door_blockstate()

    print("\n=== Generating base glass_door item model + definition + data ===")
    write_json(f"{MODELS_ITEM_DIR}/glass_door.json", generate_item_model("glass_door"))
    write_json(f"{ITEMS_DIR}/glass_door.json", generate_item_definition("glass_door"))
    write_json(f"{LOOT_TABLES_DIR}/glass_door.json", generate_loot_table("deeperdark:glass_door"))
    write_json(f"{RECIPES_DIR}/glass_door.json", generate_recipe("deeperdark:glass_door", "minecraft:glass"))

    print("\n=== Generating dye recipe (one-time) ===")
    write_json(f"{RECIPES_DIR}/dyed_glass_door.json", generate_dye_recipe())

    for color in COLORS:
        block_name = f"{color}_stained_glass_door"
        block_id = f"deeperdark:{block_name}"
        print(f"\n=== {block_name} ===")

        # blockstate
        write_json(
            f"{BLOCKSTATES_DIR}/{block_name}.json",
            generate_stained_blockstate(base_data, color),
        )

        # 8 block models
        for variant in BLOCK_VARIANTS:
            write_json(
                f"{MODELS_BLOCK_DIR}/{block_name}_{variant}.json",
                generate_block_model(block_name, variant),
            )

        # item model
        write_json(f"{MODELS_ITEM_DIR}/{block_name}.json", generate_item_model(block_name))

        # item definition
        write_json(f"{ITEMS_DIR}/{block_name}.json", generate_item_definition(block_name))

        # loot table
        write_json(f"{LOOT_TABLES_DIR}/{block_name}.json", generate_loot_table(block_id))

        # crafting recipe
        write_json(
            f"{RECIPES_DIR}/{block_name}.json",
            generate_recipe(block_id, STAINED_GLASS_ITEMS[color]),
        )

    print("\nDone.")


if __name__ == "__main__":
    main()
