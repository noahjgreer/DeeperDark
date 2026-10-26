"""Generate the QoL recipes (see Documentation/QolRecipes.md). Usage: python tools/generate_qol_recipes.py"""
import json, os, shutil, sys, copy

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
VERS = ["26.1.2", "26.3"]
REF = {v: f"{REPO}/ref-decompiled/Minecraft/{v}/assets" for v in VERS}
SHARED = f"{REPO}/src/main/resources/data"
VDIR = {v: f"{REPO}/src/main/resources-{v}/data" for v in VERS}
CATEGORIES = ["back_to_blocks", "bulk", "dyeing", "loot_gear", "renewable", "shortcuts", "silk_touch_free", "unpacking"]

# Generated vanilla overrides share data/minecraft/recipe with hand-written ones; the manifest tells them apart.
MANIFEST = f"{REPO}/tools/generate_qol_recipes.manifest"
previous = set(open(MANIFEST).read().split()) if os.path.exists(MANIFEST) else set()
for rel in previous:
    if os.path.exists(f"{REPO}/{rel}"):
        os.remove(f"{REPO}/{rel}")
for root in [SHARED, *VDIR.values()]:
    for c in CATEGORIES:
        shutil.rmtree(f"{root}/deeperdark/recipe/{c}", ignore_errors=True)
written = []
report = []

ITEMS = {v: {"minecraft:" + f[:-5] for f in os.listdir(f"{REF[v]}/assets/minecraft/items")} for v in VERS}
MOD_ITEMS = set()
for f in os.listdir(f"{REPO}/src/main/resources/assets/deeperdark/items"):
    MOD_ITEMS.add("deeperdark:" + f[:-5])
for v in VERS:
    ITEMS[v] |= MOD_ITEMS
    d = f"{REPO}/src/main/resources-{v}/assets/deeperdark/items"
    if os.path.isdir(d):
        ITEMS[v] |= {"deeperdark:" + f[:-5] for f in os.listdir(d)}

def vanilla(v, name):
    p = f"{REF[v]}/data/minecraft/recipe/{name}.json"
    return json.load(open(p)) if os.path.exists(p) else None

def vanilla_names(v):
    return sorted(f[:-5] for f in os.listdir(f"{REF[v]}/data/minecraft/recipe") if f.endswith(".json"))

def item_refs(node, key=None):
    if isinstance(node, dict):
        for k, val in node.items():
            if k in ("type", "category", "group", "pattern"):
                continue
            yield from item_refs(val, k)
    elif isinstance(node, list):
        for x in node:
            yield from item_refs(x, key)
    elif isinstance(node, str) and ":" in node and not node.startswith("#"):
        yield node

def valid_in(v, recipe):
    return all(i in ITEMS[v] for i in item_refs(recipe))

def write(path, data):
    rel = os.path.relpath(path, REPO).replace("\\", "/")
    if os.path.exists(path):
        raise SystemExit(f"refusing to overwrite hand-written {rel}")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", newline="\n") as f:
        json.dump(data, f, indent=2)
        f.write("\n")
    written.append(rel)

def place(rel, per_version):
    """per_version: {ver: json or None}. Shared when identical (ignoring category) in all versions."""
    def norm(d):
        d = copy.deepcopy(d); d.pop("category", None); return d
    present = {v: d for v, d in per_version.items() if d is not None}
    if not present:
        return
    if len(present) == len(VERS) and all(norm(d) == norm(present[VERS[0]]) for d in present.values()):
        write(f"{SHARED}/{rel}", present[VERS[0]])
    else:
        for v, d in present.items():
            write(f"{VDIR[v]}/{rel}", d)

def new(category, name, recipe):
    if not any(valid_in(v, recipe) for v in VERS):
        raise SystemExit(f"{category}/{name}: no version has all of {sorted(set(item_refs(recipe)))}")
    place(f"deeperdark/recipe/{category}/{name}.json",
          {v: (recipe if valid_in(v, recipe) else None) for v in VERS})

def override(name, fn):
    """Rewrite vanilla recipe `name` with fn(json)->json in each version that has it."""
    out = {}
    for v in VERS:
        d = vanilla(v, name)
        out[v] = fn(copy.deepcopy(d)) if d else None
        if d and out[v] == d:
            raise SystemExit(f"override of {name} changes nothing on {v}")
    if not any(out.values()):
        raise SystemExit(f"no vanilla recipe {name}")
    place(f"minecraft/recipe/{name}.json", out)

def replace_ing(old, new_):
    def walk(n):
        if isinstance(n, dict):
            return {k: (x if k in ("result", "pattern") else [walk(i) for i in x] if k == "ingredients" else walk(x))
                    for k, x in n.items()}
        if isinstance(n, list):
            if old not in n:
                return [walk(x) for x in n]
            if not isinstance(new_, list):
                raise SystemExit(f"cannot put {new_} into ingredient list {n}")  # lists hold item ids only
            return [y for x in n for y in (new_ if x == old else [x])]
        return new_ if n == old else n
    return walk

def shaped(pattern, key, result, count=1, cat="misc"):
    r = {"type": "minecraft:crafting_shaped", "category": cat, "key": key, "pattern": pattern,
         "result": {"id": result}}
    if count != 1: r["result"]["count"] = count
    return r

def shapeless(ings, result, count=1, cat="misc"):
    r = {"type": "minecraft:crafting_shapeless", "category": cat, "ingredients": ings, "result": {"id": result}}
    if count != 1: r["result"]["count"] = count
    return r

def cooking(kind, ing, result, time, xp, cat="misc"):
    return {"type": f"minecraft:{kind}", "category": cat, "cookingtime": time, "experience": xp,
            "ingredient": ing, "result": {"id": result}}

def mc(x): return "minecraft:" + x
def short(x): return x.split(":", 1)[1]

DYES = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray",
        "cyan", "purple", "blue", "brown", "green", "red", "black"]
STONE_TAG = "#minecraft:stone_crafting_materials"

# ---------------------------------------------------------------- material substitution
for name in ["lever", "piston", "dispenser", "dropper", "observer", "coast_armor_trim_smithing_template",
             "sentry_armor_trim_smithing_template", "vex_armor_trim_smithing_template"]:
    override(name, replace_ing(mc("cobblestone"), STONE_TAG))
for name in ["repeater", "comparator", "stonecutter"]:
    override(name, replace_ing(mc("stone"), STONE_TAG))
for name in ["beacon", "daylight_detector", "end_crystal", "glass_bottle"]:
    override(name, replace_ing(mc("glass"), "#c:glass_blocks/cheap"))
override("sticky_piston", replace_ing(mc("slime_ball"), [mc("slime_ball"), mc("honey_bottle")]))
override("jack_o_lantern", replace_ing(mc("torch"), [mc("torch"), mc("soul_torch"), mc("copper_torch")]))
override("writable_book", replace_ing(mc("ink_sac"), [mc("ink_sac"), mc("black_dye")]))

# ---------------------------------------------------------------- dyeing
for c in DYES:
    override(f"{c}_terracotta", replace_ing(mc("terracotta"), "#minecraft:terracotta"))
    override(f"{c}_stained_glass", replace_ing(mc("glass"), "#c:glass_blocks/cheap"))
    override(f"{c}_stained_glass_pane_from_glass_pane", replace_ing(mc("glass_pane"), "#c:glass_panes"))
    override(f"{c}_candle", replace_ing(mc("candle"), "#minecraft:candles"))
    override(f"{c}_concrete_powder", replace_ing(mc("sand"), [mc("sand"), mc("red_sand")]))
    r = shaped(["###", "#X#", "###"], {"#": "#c:concrete_powders", "X": mc(f"{c}_dye")},
               mc(f"{c}_concrete_powder"), 8, "building")
    r["group"] = "concrete_powder"
    new("dyeing", f"{c}_concrete_powder", r)
    new("dyeing", f"{c}_shulker_box",
        shapeless([mc("shulker_shell"), mc("chest"), mc("shulker_shell"), mc(f"{c}_dye")], mc(f"{c}_shulker_box")))
    for kind, t in (("smelting", 200), ("blasting", 100)):
        new("dyeing", f"{c}_stained_glass_from_{kind}_concrete_powder",
            cooking(kind, mc(f"{c}_concrete_powder"), mc(f"{c}_stained_glass"), t, 0.1, "blocks"))
new("dyeing", "green_dye_from_blue_and_yellow", shapeless([mc("blue_dye"), mc("yellow_dye")], mc("green_dye"), 2))
new("dyeing", "brown_dye_from_red_and_green", shapeless([mc("red_dye"), mc("green_dye")], mc("brown_dye"), 2))
new("dyeing", "black_dye_from_coal", shapeless([mc("coal")], mc("black_dye")))
new("dyeing", "black_dye_from_charcoal", shapeless([mc("charcoal")], mc("black_dye")))
new("dyeing", "red_sand", shaped(["###", "#X#", "###"], {"#": mc("sand"), "X": mc("red_dye")}, mc("red_sand"), 8, "building"))
new("dyeing", "red_sandstone", shaped(["###", "#X#", "###"], {"#": mc("sandstone"), "X": mc("red_dye")}, mc("red_sandstone"), 8, "building"))

# ---------------------------------------------------------------- yields + back to blocks
def single_item(key_val, result_prefix):
    """The full block a slab/stairs/wall recipe consumes, or None for tag/odd keys."""
    if isinstance(key_val, str):
        return None if key_val.startswith("#") else key_val
    if isinstance(key_val, list):
        named = [x for x in key_val if short(x).startswith(result_prefix)]
        return (named or key_val)[0]
    return None

def families(v):
    """(kind, product, block, source) from vanilla + mod shaped recipes."""
    srcs = [(n, vanilla(v, n), "vanilla") for n in vanilla_names(v)]
    for d in (f"{SHARED}/deeperdark/recipe", f"{VDIR[v]}/deeperdark/recipe"):
        if os.path.isdir(d):
            for f in sorted(os.listdir(d)):  # top level only: the mod's hand-written recipes
                if f.endswith(".json"):
                    srcs.append((f[:-5], json.load(open(f"{d}/{f}")), "mod"))
    out = []
    for n, d, src in srcs:
        if d.get("type") != "minecraft:crafting_shaped" or len(d["key"]) != 1:
            continue
        res, pat = d["result"]["id"], d["pattern"]
        kv = next(iter(d["key"].values()))
        if res.endswith("_slab") and pat == ["###"]:
            kind, prefix = "slab", short(res)[:-5]
        elif res.endswith("_stairs") and pat in (["#  ", "## ", "###"], ["#", "##", "###"]):
            kind, prefix = "stairs", short(res)[:-7]
        elif res.endswith("_wall") and pat == ["###", "###"]:
            kind, prefix = "wall", short(res)[:-5]
        elif res.endswith("_carpet") and pat == ["##"]:
            kind, prefix = "carpet", short(res)[:-7]
        elif res.endswith("pane") and pat == ["###", "###"]:
            kind, prefix = "pane", short(res)[:-5]
        elif res.endswith("_bars") and pat == ["###", "###"]:
            kind, prefix = "bars", short(res)[:-5]
        elif res.endswith("_trapdoor") and pat == ["###", "###"] and kv == single_item(kv, "") and "planks" in str(kv):
            kind, prefix = "trapdoor", None
        else:
            continue
        out.append((kind, res, single_item(kv, prefix) if prefix is not None else kv, n, src))
    return out

BACK = {  # kind: (pattern, block count)
    "slab": (["##"], 1), "stairs": (["##", "##"], 3), "carpet": (["###"], 2),
    "pane": (["###", "# #", "###"], 3), "bars": (["###", "# #", "###"], 3),
}
fams = {v: families(v) for v in VERS}
skipped = []
back = {}  # rel -> {v: recipe}
for v in VERS:
    for kind, res, block, n, src in fams[v]:
        if kind == "trapdoor":
            continue
        if block is None:
            skipped.append(f"{v} {kind} {res} (tag ingredient)")
            continue
        name = f"{short(block)}_from_{short(res)}"
        if kind == "wall":
            r = shapeless([res], block, 1, "building")
        else:
            pat, cnt = BACK[kind]
            r = shaped(pat, {"#": res}, block, cnt, "building")
        back.setdefault(f"deeperdark/recipe/back_to_blocks/{name}.json", {})[v] = r
for rel, per in back.items():
    place(rel, {v: per.get(v) for v in VERS})

for kind, cnt in (("stairs", 8), ("trapdoor", 6)):
    names = sorted({n for v in VERS for k, _, _, n, s in fams[v] if k == kind and s == "vanilla"})
    def bump(d, cnt=cnt):
        d["result"]["count"] = cnt; return d
    for n in names:
        override(n, bump)
    report.append(f"{kind} yield -> {cnt}: {len(names)} vanilla recipes")
mod_stairs = sorted({n for v in VERS for k, _, _, n, s in fams[v] if k == "stairs" and s == "mod"})
report.append("mod stairs to bump by hand: " + ", ".join(mod_stairs))

# ---------------------------------------------------------------- unpacking
for name, ing, res, cnt in [
    ("ice_from_packed_ice", "packed_ice", "ice", 9),
    ("packed_ice_from_blue_ice", "blue_ice", "packed_ice", 9),
    ("honeycomb_from_honeycomb_block", "honeycomb_block", "honeycomb", 4),
    ("glowstone_dust_from_glowstone", "glowstone", "glowstone_dust", 4),
    ("amethyst_shard_from_amethyst_block", "amethyst_block", "amethyst_shard", 4),
    ("prismarine_shard_from_prismarine", "prismarine", "prismarine_shard", 4),
    ("prismarine_shard_from_prismarine_bricks", "prismarine_bricks", "prismarine_shard", 9),
    ("brick_from_bricks", "bricks", "brick", 4),
    ("nether_brick_from_nether_bricks", "nether_bricks", "nether_brick", 4),
    ("magma_cream_from_magma_block", "magma_block", "magma_cream", 4),
    ("melon_slice_from_melon", "melon", "melon_slice", 9),
    ("pointed_dripstone_from_dripstone_block", "dripstone_block", "pointed_dripstone", 4),
    ("sand_from_sandstone", "sandstone", "sand", 4),
    ("red_sand_from_red_sandstone", "red_sandstone", "red_sand", 4),
    ("bone_from_skeleton_skull", "skeleton_skull", "bone", 9),
    ("sulfur_from_potent_sulfur", "potent_sulfur", "sulfur", 9),
    ("sulfur_spike_from_sulfur", "sulfur", "sulfur_spike", 4),
]:
    new("unpacking", name, shapeless([mc(ing)], mc(res), cnt))
new("unpacking", "copper_ingot_from_weathered_copper_blocks", shapeless(
    [[mc(p + s + "copper") for p in ("", "waxed_") for s in ("exposed_", "weathered_", "oxidized_")]],
    mc("copper_ingot"), 9))

# ---------------------------------------------------------------- no silk touch
new("silk_touch_free", "grass_block", shapeless([mc("dirt"), mc("wheat_seeds")], mc("grass_block"), 1, "building"))
new("silk_touch_free", "mycelium", shapeless([mc("dirt"), mc("red_mushroom"), mc("brown_mushroom")], mc("mycelium"), 1, "building"))
new("silk_touch_free", "podzol", shapeless([mc("dirt"), mc("leaf_litter")], mc("podzol"), 1, "building"))
for w in ("crimson", "warped"):
    new("silk_touch_free", f"{w}_nylium",
        shapeless([mc("netherrack"), [mc(f"{w}_fungus"), mc(f"{w}_roots")]], mc(f"{w}_nylium"), 1, "building"))
for m in ("red", "brown"):
    new("silk_touch_free", f"{m}_mushroom_block", shaped(["##", "##"], {"#": mc(f"{m}_mushroom")}, mc(f"{m}_mushroom_block"), 1, "building"))
new("silk_touch_free", "mushroom_stem", shaped(["RB", "BR"], {"R": mc("red_mushroom"), "B": mc("brown_mushroom")}, mc("mushroom_stem"), 1, "building"))
for c in ("tube", "brain", "bubble", "fire", "horn"):
    for dead in ("", "dead_"):
        new("silk_touch_free", f"{dead}{c}_coral_block", shaped(["##", "##"],
            {"#": [mc(f"{dead}{c}_coral"), mc(f"{dead}{c}_coral_fan")]}, mc(f"{dead}{c}_coral_block"), 1, "building"))
new("silk_touch_free", "ice", shaped(["##", "##"], {"#": mc("snow_block")}, mc("ice"), 1, "building"))

# ---------------------------------------------------------------- shortcuts
new("shortcuts", "chest_from_logs", shaped(["###", "# #", "###"], {"#": "#minecraft:logs"}, mc("chest"), 4))
new("shortcuts", "stick_from_logs", shaped(["#", "#"], {"#": "#minecraft:logs"}, mc("stick"), 16))
override("paper", lambda d: shapeless([mc("sugar_cane")] * 3, mc("paper"), 3))
override("bread", lambda d: shapeless([mc("wheat")] * 3, mc("bread")))
override("shulker_box", lambda d: shapeless([mc("shulker_shell"), mc("chest"), mc("shulker_shell")], mc("shulker_box")))
new("shortcuts", "dispenser_from_dropper_and_bow", shapeless([mc("dropper"), mc("bow")], mc("dispenser"), 1, "redstone"))
new("shortcuts", "dispenser_from_dropper_and_string", shaped([" #X", "#DX", " #X"],
    {"#": mc("stick"), "X": mc("string"), "D": mc("dropper")}, mc("dispenser"), 1, "redstone"))
new("shortcuts", "powered_rail_from_copper", shaped(["X X", "X#X", "XRX"],
    {"X": mc("copper_ingot"), "#": mc("stick"), "R": mc("redstone")}, mc("powered_rail"), 6))
for ore, xp in (("iron", 6.3), ("gold", 9.0), ("copper", 6.3)):
    r = cooking("blasting", mc(f"raw_{ore}_block"), mc(f"{ore}_block"), 600, xp)
    new("shortcuts", f"{ore}_block_from_blasting_raw_{ore}_block", r)
for x, cart in (("chest", "chest_minecart"), ("furnace", "furnace_minecart"), ("hopper", "hopper_minecart"), ("tnt", "tnt_minecart")):
    new("shortcuts", f"{cart}_from_iron", shaped(["# #", "#X#"], {"#": mc("iron_ingot"), "X": mc(x)}, mc(cart)))
new("shortcuts", "gravel_from_flint", shaped(["##", "##"], {"#": mc("flint")}, mc("gravel"), 1, "building"))
new("shortcuts", "flint_from_gravel", shapeless([mc("gravel")] * 3, mc("flint")))
new("shortcuts", "blackstone_from_basalt", shapeless([mc("basalt"), "#minecraft:coals"], mc("blackstone"), 1, "building"))
new("shortcuts", "polished_blackstone_from_polished_basalt", shapeless([mc("polished_basalt"), "#minecraft:coals"], mc("polished_blackstone"), 1, "building"))
new("shortcuts", "dead_bush_from_smoking_sapling", cooking("smoking", "#minecraft:saplings", mc("dead_bush"), 100, 0.1))

# ---------------------------------------------------------------- bulk
B = {"B": mc("iron_block")}
new("bulk", "cauldron_from_iron_blocks", shaped(["B B", "B B", "BBB"], B, mc("cauldron"), 9))
new("bulk", "minecart_from_iron_blocks", shaped(["B B", "BBB"], B, mc("minecart"), 9))
new("bulk", "bucket_from_iron_blocks", shaped(["B B", " B "], B, mc("bucket"), 9))
new("bulk", "iron_trapdoor_from_iron_blocks", shaped(["BB", "BB"], B, mc("iron_trapdoor"), 9, "redstone"))
new("bulk", "iron_chain_from_iron_block", shaped(["I", "B", "I"], {"I": mc("iron_ingot"), **B}, mc("iron_chain"), 9))
new("bulk", "heavy_weighted_pressure_plate_from_iron_blocks", shaped(["BB"], B, mc("heavy_weighted_pressure_plate"), 9, "redstone"))
new("bulk", "light_weighted_pressure_plate_from_gold_blocks", shaped(["BB"], {"B": mc("gold_block")}, mc("light_weighted_pressure_plate"), 9, "redstone"))

# ---------------------------------------------------------------- loot gear
for mat, ing in (("copper", "copper_ingot"), ("iron", "iron_ingot"), ("golden", "gold_ingot"), ("diamond", "diamond")):
    new("loot_gear", f"{mat}_horse_armor", shaped(["X X", "XXX", "X X"], {"X": mc(ing)}, mc(f"{mat}_horse_armor"), 1, "equipment"))
    new("loot_gear", f"{mat}_nautilus_armor", shaped(["X X", "XSX", "X X"], {"X": mc(ing), "S": mc("nautilus_shell")}, mc(f"{mat}_nautilus_armor"), 1, "equipment"))
for piece, pat in (("helmet", ["XXX", "X X"]), ("chestplate", ["X X", "XXX", "XXX"]),
                   ("leggings", ["XXX", "X X", "X X"]), ("boots", ["X X", "X X"])):
    new("loot_gear", f"chainmail_{piece}", shaped(pat, {"X": mc("iron_chain")}, mc(f"chainmail_{piece}"), 1, "equipment"))

# ---------------------------------------------------------------- renewable
new("renewable", "calcite", shapeless([mc("diorite"), mc("bone_meal")], mc("calcite"), 1, "building"))
new("renewable", "tuff", shapeless([mc("andesite"), mc("diorite")], mc("tuff"), 2, "building"))
# 5 nuggets: Fortune III always turns gilded blackstone into 2-5 nuggets, so it must never pay out more
new("renewable", "gilded_blackstone", shapeless([mc("blackstone")] + [mc("gold_nugget")] * 5, mc("gilded_blackstone"), 1, "building"))
new("renewable", "spore_blossom", shapeless([mc("moss_block"), mc("pink_petals"), mc("pink_petals")], mc("spore_blossom"), 1, "building"))
new("renewable", "sculk", shaped(["###", "###", "###"], {"#": mc("sculk_vein")}, mc("sculk"), 1, "building"))
new("renewable", "soul_soil", shapeless([mc("soul_sand"), mc("dirt")], mc("soul_soil"), 2, "building"))
new("renewable", "cinnabar", shaped(["NR", "RN"], {"N": mc("netherrack"), "R": mc("redstone")}, mc("cinnabar"), 2, "building"))

with open(MANIFEST, "w", newline="\n") as f:
    f.write("\n".join(sorted(w for w in written if "/minecraft/recipe/" in w)) + "\n")
print("\n".join(report))
print("skipped back-to-blocks:", *skipped, sep="\n  ")
shared = sum(1 for w in written if "/resources/data" in w)
print(f"written {len(written)} files ({shared} shared)")
for v in VERS:
    print(f"  {v}-only:", sorted(w.rsplit('/', 1)[1] for w in written if f"resources-{v}/" in w))
