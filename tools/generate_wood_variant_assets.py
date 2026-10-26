#!/usr/bin/env python3
"""
Generate assets, recipes, loot tables, tags and lang entries for WoodVariantBlocks: per wood type a
plank wall, a bark wall, a stripped bark wall, and (stripped) end-grain blocks.

Run from the project root, then rerun tools/generate_qol_recipes.py (it derives wall -> block
recipes from the bark walls' shaped recipes):
    python tools/generate_wood_variant_assets.py
"""

import json
import os

# wood, bark block (wall source), log block (texture/ingredient), log tag
WOODS = [
    ("oak", "oak_wood", "oak_log", "oak_logs"),
    ("spruce", "spruce_wood", "spruce_log", "spruce_logs"),
    ("birch", "birch_wood", "birch_log", "birch_logs"),
    ("jungle", "jungle_wood", "jungle_log", "jungle_logs"),
    ("acacia", "acacia_wood", "acacia_log", "acacia_logs"),
    ("dark_oak", "dark_oak_wood", "dark_oak_log", "dark_oak_logs"),
    ("mangrove", "mangrove_wood", "mangrove_log", "mangrove_logs"),
    ("cherry", "cherry_wood", "cherry_log", "cherry_logs"),
    ("pale_oak", "pale_oak_wood", "pale_oak_log", "pale_oak_logs"),
    ("poplar", "poplar_wood", "poplar_log", "poplar_logs"),
    ("bamboo", "bamboo_block", "bamboo_block", "bamboo_blocks"),
    ("crimson", "crimson_hyphae", "crimson_stem", "crimson_stems"),
    ("warped", "warped_hyphae", "warped_stem", "warped_stems"),
]
VERSION_ONLY = {"poplar": "26.3"}  # wood -> the only version that has it
NETHER = {"crimson", "warped"}
VERSIONS = ["26.1.2", "26.3"]

SRC = "src/main"


def res(version=None):
    return f"{SRC}/resources-{version}" if version else f"{SRC}/resources"


def write(path, data, indent=2, newline=True):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", newline="\n") as f:
        json.dump(data, f, indent=indent)
        if newline:
            f.write("\n")


def mc(name):
    return f"minecraft:{name}"


def dd(name):
    return f"deeperdark:{name}"


def title(name):
    return " ".join(w.capitalize() for w in name.split("_"))


def wall_blockstate(name):
    parts = [{"when": {"up": "true"}, "apply": {"model": dd(f"block/{name}_post")}}]
    for height, suffix in (("low", "side"), ("tall", "side_tall")):
        for side, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
            apply = {"model": dd(f"block/{name}_{suffix}"), "uvlock": True}
            if y:
                apply["y"] = y
            parts.append({"when": {side: height}, "apply": apply})
    return {"multipart": parts}


def wall_assets(assets, name, texture):
    write(f"{assets}/blockstates/{name}.json", wall_blockstate(name))
    for suffix, parent in (("post", "template_wall_post"), ("side", "template_wall_side"),
                           ("side_tall", "template_wall_side_tall"), ("inventory", "wall_inventory")):
        write(f"{assets}/models/block/{name}_{suffix}.json",
              {"parent": mc(f"block/{parent}"), "textures": {"wall": mc(f"block/{texture}")}})
    write(f"{assets}/items/{name}.json", {"model": {"type": "minecraft:model", "model": dd(f"block/{name}_inventory")}})


def end_grain_assets(assets, name, texture):
    model = {"model": dd(f"block/{name}")}
    write(f"{assets}/blockstates/{name}.json", {"variants": {f"axis={a}": model for a in "xyz"}})
    write(f"{assets}/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": mc(f"block/{texture}")}})
    write(f"{assets}/items/{name}.json", {"model": {"type": "minecraft:model", "model": dd(f"block/{name}")}})


def stonecutting(ingredients, result):
    return {"type": "minecraft:stonecutting", "ingredient": ingredients, "result": {"id": dd(result), "count": 1}}


def wall_shaped(ingredients, result):
    return {"type": "minecraft:crafting_shaped", "category": "building", "key": {"#": ingredients},
            "pattern": ["###", "###"], "result": {"id": dd(result), "count": 6}}


def loot_table(name, version):
    entry = {"type": "minecraft:item", "name": dd(name)}
    if version == "26.3":
        pool = {"rolls": 1, "entries": [entry], "condition": {"type": "minecraft:survives_explosion"}}
    else:
        pool = {"rolls": 1, "entries": [entry], "conditions": [{"condition": "minecraft:survives_explosion"}]}
    return {"type": "minecraft:block", "pools": [pool]}


def add_to_tag(path, ids, optional):
    indent, newline = 2, True
    if os.path.exists(path):  # keep a hand-written file's formatting
        text = open(path).read()
        tag = json.loads(text)
        second_line = text.split("\n")[1]
        indent, newline = len(second_line) - len(second_line.lstrip()), text.endswith("\n")
    else:
        tag = {"replace": False, "values": []}
    present = {v if isinstance(v, str) else v["id"] for v in tag["values"]}
    for i in ids:
        if i not in present:
            tag["values"].append({"id": i, "required": False} if optional else i)
    write(path, tag, indent, newline)


lang = {}
walls, axe, leaf_support, non_flammable = [], [], [], []
for wood, bark, log, log_tag in WOODS:
    version = VERSION_ONLY.get(wood)
    root = res(version)
    assets = f"{root}/assets/deeperdark"
    recipes = f"{root}/data/deeperdark/recipe"
    optional = version is not None

    plain = [mc(log)] if log == bark else [mc(log), mc(bark)]
    stripped = [mc(f"stripped_{x[len('minecraft:'):]}") for x in plain]
    blocks = {
        f"{wood}_wall": f"{wood}_planks",
        f"{bark}_wall": log,
        f"stripped_{bark}_wall": f"stripped_{log}",
        f"{wood}_end_grain": f"{log}_top",
        f"stripped_{wood}_end_grain": f"stripped_{log}_top",
    }
    for name, texture in blocks.items():
        (wall_assets if name.endswith("_wall") else end_grain_assets)(assets, name, texture)
        lang[f"block.deeperdark.{name}"] = title(name)
        for v in [version] if version else VERSIONS:
            write(f"{res(v)}/data/deeperdark/loot_table/blocks/{name}.json", loot_table(name, v))
        axe.append((dd(name), optional))
        if name.endswith("_wall"):
            walls.append((dd(name), optional))

    write(f"{recipes}/{wood}_wall_from_stonecutter.json", stonecutting(mc(f"{wood}_planks"), f"{wood}_wall"))
    # Walls come from the bark block only: from logs, the wall -> bark block recipe would undercut 4 logs -> 3 wood.
    write(f"{recipes}/{bark}_wall.json", wall_shaped(mc(bark), f"{bark}_wall"))
    write(f"{recipes}/{bark}_wall_from_stonecutter.json", stonecutting(mc(bark), f"{bark}_wall"))
    write(f"{recipes}/stripped_{bark}_wall.json", wall_shaped(mc(f"stripped_{bark}"), f"stripped_{bark}_wall"))
    write(f"{recipes}/stripped_{bark}_wall_from_stonecutter.json",
          stonecutting(mc(f"stripped_{bark}"), f"stripped_{bark}_wall"))
    write(f"{recipes}/{wood}_end_grain_from_stonecutter.json", stonecutting(plain, f"{wood}_end_grain"))
    write(f"{recipes}/stripped_{wood}_end_grain_from_stonecutter.json", stonecutting(stripped, f"stripped_{wood}_end_grain"))

    # Item log tags only (planks, charcoal, fuel): in the block log tags, trees, creaking hearts and
    # worldgen would treat it as a natural log.
    end_grains = [dd(f"{wood}_end_grain"), dd(f"stripped_{wood}_end_grain")]
    add_to_tag(f"{root}/data/minecraft/tags/item/{log_tag}.json", end_grains, False)
    leaf_support += [(i, optional) for i in end_grains]
    if wood in NETHER:
        non_flammable += end_grains

tags = f"{res()}/data/minecraft/tags"
for path, entries in ((f"{tags}/block/walls.json", walls), (f"{tags}/block/mineable/axe.json", axe),
                      (f"{tags}/block/prevents_nearby_leaf_decay.json", leaf_support),
                      (f"{res()}/data/deeperdark/tags/block/not_mineable_with_pickaxe.json", walls)):
    for optional in (False, True):
        add_to_tag(path, [i for i, o in entries if o == optional], optional)
add_to_tag(f"{tags}/item/non_flammable_wood.json", non_flammable, False)

lang_path = f"{res()}/assets/deeperdark/lang/en_us.json"
with open(lang_path, encoding="utf-8") as f:
    existing = json.load(f)
existing.update(lang)
with open(lang_path, "w", encoding="utf-8", newline="\n") as f:
    json.dump(existing, f, indent=2, ensure_ascii=False)

print(f"{len(lang)} blocks")
