#!/usr/bin/env python3
"""
Generate stonecutter recipes for converting logs to wood, stripped logs, and stripped wood.
Run this script to regenerate all log-related stonecutter recipes.
"""

import json
import os

# Output directory for recipes
RECIPE_DIR = "src/main/resources/data/minecraft/recipe"

# Log types and their variants
# Format: (log_name, wood_name, stripped_log_name, stripped_wood_name)
OVERWORLD_LOGS = [
    ("oak_log", "oak_wood", "stripped_oak_log", "stripped_oak_wood"),
    ("spruce_log", "spruce_wood", "stripped_spruce_log", "stripped_spruce_wood"),
    ("birch_log", "birch_wood", "stripped_birch_log", "stripped_birch_wood"),
    ("jungle_log", "jungle_wood", "stripped_jungle_log", "stripped_jungle_wood"),
    ("acacia_log", "acacia_wood", "stripped_acacia_log", "stripped_acacia_wood"),
    ("dark_oak_log", "dark_oak_wood", "stripped_dark_oak_log", "stripped_dark_oak_wood"),
    ("mangrove_log", "mangrove_wood", "stripped_mangrove_log", "stripped_mangrove_wood"),
    ("cherry_log", "cherry_wood", "stripped_cherry_log", "stripped_cherry_wood"),
    ("pale_oak_log", "pale_oak_wood", "stripped_pale_oak_log", "stripped_pale_oak_wood"),
]

# Nether stems use "stem" and "hyphae" instead of "log" and "wood"
NETHER_STEMS = [
    ("crimson_stem", "crimson_hyphae", "stripped_crimson_stem", "stripped_crimson_hyphae"),
    ("warped_stem", "warped_hyphae", "stripped_warped_stem", "stripped_warped_hyphae"),
]

# Bamboo block (special case)
BAMBOO = [
    ("bamboo_block", None, "stripped_bamboo_block", None),  # No wood variant for bamboo
]


def create_stonecutting_recipe(input_item: str, output_item: str, count: int = 1) -> dict:
    """Create a stonecutting recipe JSON structure."""
    return {
        "type": "minecraft:stonecutting",
        "ingredient": f"minecraft:{input_item}",
        "result": {
            "id": f"minecraft:{output_item}",
            "count": count
        }
    }


def generate_recipe_file(recipe: dict, filename: str):
    """Write a recipe to a JSON file."""
    filepath = os.path.join(RECIPE_DIR, filename)
    with open(filepath, 'w') as f:
        json.dump(recipe, f, indent=2)
    print(f"Generated: {filename}")


def generate_log_recipes(log_variants: tuple):
    """Generate all stonecutter recipes for a log type."""
    log, wood, stripped_log, stripped_wood = log_variants

    recipes = []

    # Log -> Wood (1:1)
    if wood:
        recipes.append((log, wood, f"{log}_to_{wood}_stonecutting.json"))

    # Log -> Stripped Log (1:1)
    if stripped_log:
        recipes.append((log, stripped_log, f"{log}_to_{stripped_log}_stonecutting.json"))

    # Log -> Stripped Wood (1:1)
    if stripped_wood:
        recipes.append((log, stripped_wood, f"{log}_to_{stripped_wood}_stonecutting.json"))

    # Wood -> Stripped Wood (1:1) - if both exist
    if wood and stripped_wood:
        recipes.append((wood, stripped_wood, f"{wood}_to_{stripped_wood}_stonecutting.json"))

    # Stripped Log -> Stripped Wood (1:1) - if both exist
    if stripped_log and stripped_wood:
        recipes.append((stripped_log, stripped_wood, f"{stripped_log}_to_{stripped_wood}_stonecutting.json"))

    return recipes


def main():
    # Ensure output directory exists
    os.makedirs(RECIPE_DIR, exist_ok=True)

    all_recipes = []

    # Generate recipes for all log types
    for log_variants in OVERWORLD_LOGS + NETHER_STEMS + BAMBOO:
        all_recipes.extend(generate_log_recipes(log_variants))

    # Write all recipe files
    for input_item, output_item, filename in all_recipes:
        recipe = create_stonecutting_recipe(input_item, output_item)
        generate_recipe_file(recipe, filename)

    print(f"\nTotal recipes generated: {len(all_recipes)}")


if __name__ == "__main__":
    main()
