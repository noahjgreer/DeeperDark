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


def main():
    print("=== Fixing base glass_door blockstate ===")
    base_data = fix_glass_door_blockstate()

    print("\n=== Generating base glass_door item model ===")
    write_json(f"{MODELS_ITEM_DIR}/glass_door.json", generate_item_model("glass_door"))

    for color in COLORS:
        block_name = f"{color}_stained_glass_door"
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

    print("\nDone.")


if __name__ == "__main__":
    main()
