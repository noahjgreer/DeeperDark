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


# Closed-door y rotation per facing; an open door turns a further 90 (left hinge) or 270 (right).
CLOSED_Y = {"east": 0, "south": 90, "west": 180, "north": 270}


def generate_blockstate(block_name):
    """Every facing/half/hinge/open/centered state. Centered doors use the template_centered_* models."""
    variants = {}
    for facing, closed_y in CLOSED_Y.items():
        for half, model_half in (("lower", "bottom"), ("upper", "top")):
            for hinge in ("left", "right"):
                for is_open in (False, True):
                    for centered in (False, True):
                        y = (closed_y + (0 if not is_open else 90 if hinge == "left" else 270)) % 360
                        model = (f"deeperdark:block/{block_name}_" + ("centered_" if centered else "")
                                 + f"{model_half}_{hinge}" + ("_open" if is_open else ""))
                        entry = {"model": model}
                        if y:
                            entry["y"] = y
                        key = (f"centered={str(centered).lower()},facing={facing},half={half},"
                               f"hinge={hinge},open={str(is_open).lower()}")
                        variants[key] = entry
    return {"variants": variants}


def generate_block_model(block_name, variant, centered):
    """One of the 16 directional/open-state models for a door variant."""
    return {
        "parent": f"deeperdark:block/template_centered_door_{variant}" if centered else f"minecraft:block/door_{variant}",
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


def write_block_models(block_name):
    for variant in BLOCK_VARIANTS:
        write_json(f"{MODELS_BLOCK_DIR}/{block_name}_{variant}.json", generate_block_model(block_name, variant, False))
        write_json(f"{MODELS_BLOCK_DIR}/{block_name}_centered_{variant}.json", generate_block_model(block_name, variant, True))


def main():
    print("=== Generating base glass_door assets + recipe ===")
    write_json(f"{BLOCKSTATES_DIR}/glass_door.json", generate_blockstate("glass_door"))
    write_block_models("glass_door")
    write_json(f"{MODELS_ITEM_DIR}/glass_door.json", generate_item_model("glass_door"))
    write_json(f"{ITEMS_DIR}/glass_door.json", generate_item_definition("glass_door"))
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
            generate_blockstate(block_name),
        )

        write_block_models(block_name)

        # item model
        write_json(f"{MODELS_ITEM_DIR}/{block_name}.json", generate_item_model(block_name))

        # item definition
        write_json(f"{ITEMS_DIR}/{block_name}.json", generate_item_definition(block_name))

        # crafting recipe
        write_json(
            f"{RECIPES_DIR}/{block_name}.json",
            generate_recipe(block_id, STAINED_GLASS_ITEMS[color]),
        )

    print("\nDone.")


if __name__ == "__main__":
    main()
