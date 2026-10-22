# Quality-of-life recipes

A large set of data-only recipes so players aren't blocked by needing one exact material, plus
reversals, unpacking and a few renewability recipes. Ideas come from Vanilla Tweaks (crafting
tweaks), Quark, Crafting QOL, Crafting Refinements and More Recipes.

## Where it lives

| What | Where |
| --- | --- |
| Generator | `tools/generate_qol_recipes.py` |
| Conflict checker | `tools/check_recipe_conflicts.py <ver>=<fabric-convention-tags-v2 jar> ...` |
| New recipes | `data/deeperdark/recipe/<category>/` |
| Edited vanilla recipes | `data/minecraft/recipe/<vanilla name>.json` (same id overrides vanilla) |
| Stone tags | `data/minecraft/tags/item/stone_{crafting,tool}_materials.json` (hand-written) |

All recipe files above are generated except the tags and the mod's own `*_stairs.json` yields.
Don't hand-edit generated files; change the script and rerun it. Before writing, it deletes its
category folders and the overrides listed in `tools/generate_qol_recipes.manifest`. It refuses
to overwrite any other file, which protects the hand-written overrides in the same folder. It
reads vanilla recipes from `ref-decompiled/Minecraft/<ver>/assets/data`, which is gitignored, so
both scripts need the local decompile (`DecompiledSources.md`). Fabric datagen was not used,
because the mod has no datagen setup and the generated JSON is plain data. It derives
slab, stairs, wall, carpet, pane, bars and trapdoor families from the vanilla recipes, so a new
version picks up new families automatically. A file goes to the shared `resources/` when every
version has its items and the same vanilla recipe (ignoring `category`, which 26.3 dropped).
Otherwise it goes to `resources-<ver>/`. Overrides that change nothing are rejected.

Run the checker after any recipe change. It overlays the mod's recipes on vanilla, resolves item
tags (vanilla, each version's Fabric `c:` jar, and the mod's), and lists every pair of crafting or cooking recipes
that the same grid or input could match, including mirrored shaped recipes. It also fails on
unknown items and tags. The 14 pairs it reports today are the old `minecraft:*_blasting` files
duplicating vanilla's `*_from_blasting` recipes.

## What's in it

- **Any stone.** `stone_crafting_materials` gains stone, mossy cobblestone, deepslate, andesite, diorite,
  granite, tuff, calcite, dripstone block, basalt, smooth basalt, end stone, and cinnabar and
  sulfur where they exist. `stone_tool_materials` includes that tag. Together they cover the furnace, stone tools (and their anvil repair) and the
  brewing stand. Lever, piston, dispenser, dropper, observer, repeater, comparator, stonecutter
  and the coast/sentry/vex trim copies are switched to `#minecraft:stone_crafting_materials`.
- **Any glass.** Beacon, daylight detector, end crystal and glass bottle take
  `#c:glass_blocks/cheap` (clear plus stained, not tinted).
- **Other substitutes.** Sticky piston: honey bottle for slime ball (the bottle is returned).
  Jack o'lantern: any torch. Book and quill: black dye for ink sac. Concrete powder: red sand.
- **Dyeing.** Terracotta, stained glass, stained panes and candles re-dye from any color.
  Concrete powder re-dyes (8 + dye) and smelts or blasts into stained glass. Shulker boxes craft
  pre-dyed. Green = blue + yellow, brown = red + green, coal/charcoal → black dye. Red sand and
  red sandstone come from 8 sand or sandstone + red dye.
- **Yields.** Stairs give 8 (vanilla and mod stairs), wooden trapdoors give 6.
- **Back to blocks.** 2 slabs side by side → 1 block (vertical is taken by chiseled blocks),
  4 stairs in a square → 3 blocks, wall → block, 3 carpets → 2 wool/moss, 8 panes or bars in a
  ring → 3 glass/ingots. Each is lossless or loses at most what the stonecutter gives.
- **Unpacking.** Packed/blue ice, honeycomb, glowstone, amethyst, prismarine (and bricks), bricks,
  nether bricks, magma, melon, dripstone, sandstone, skeleton skull → bones, weathered/waxed
  copper → ingots, and potent sulfur and sulfur blocks on 26.3. Bamboo blocks are left out
  because vanilla already turns them into planks. Wool → string already exists as the shears
  recipe.
- **No silk touch.** Grass (dirt + seeds), mycelium (dirt + both mushrooms), podzol (dirt + leaf
  litter), nylium (netherrack + fungus or roots), mushroom blocks and stem (2×2), coral blocks
  (2×2 coral or fans, live or dead), ice (2×2 snow blocks).
- **Shortcuts.** 8 logs → 4 chests, 2 logs → 16 sticks. Paper, bread and shulker box are
  shapeless (2×2). Dropper + bow, or dropper inside a bow shape → dispenser. Copper powered
  rails. Raw ore blocks blast in 600 ticks. Chest/furnace/hopper/TNT minecarts craft in one
  step. 4 flint → gravel, 3 gravel → flint. Basalt + coal → blackstone (polished too). Saplings
  smoke into dead bushes.
- **Bulk (×9 from blocks).** Cauldron, minecart, bucket, iron trapdoor, iron chain, weighted
  pressure plates.
- **Loot gear.** Copper/iron/gold/diamond horse armor (leather pattern), nautilus armor (horse
  pattern around a nautilus shell), chainmail from iron chains (about 1.2 ingots per chain, so
  chainmail never undercuts iron or copper armor).
- **Renewable.** Calcite (diorite + bone meal), tuff (andesite + diorite → 2), gilded blackstone
  (blackstone + 5 gold nuggets; Fortune III always breaks it into 2-5 nuggets, so it can't make
  gold), spore blossom (moss + 2 pink petals), sculk (9 veins), soul soil
  (soul sand + dirt → 2), cinnabar on 26.3 (netherrack/redstone checker → 2).

## Known limits

- No recipe advancements, like the mod's other recipes. A recipe shows in the recipe book once a
  player has crafted it.
- 8 wooden stairs burn for 33% longer than the 6 planks they cost.
- The old per-wood stonecutting files in `data/minecraft/recipe` don't cover poplar on 26.3.
