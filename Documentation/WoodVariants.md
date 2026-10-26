# Wood variants and fence/wall connections

## Blocks

`block/WoodVariantBlocks` registers five blocks for every wood type (oak, spruce, birch, jungle,
acacia, dark oak, mangrove, cherry, pale oak, bamboo, crimson, warped, and poplar on 26.3 only):

"Log family" means the log, wood, stripped log, stripped wood and both end grains of one wood type.

| Block (English name) | Texture | Recipes |
| --- | --- | --- |
| `<wood>_wall` (Oak Plank Wall) | planks | stonecutter: planks → 1, any log-family block → 4 (bamboo 2) |
| `<bark>_wall` (Oak Log Wall; ids `oak_wood_wall`, `crimson_hyphae_wall`, `bamboo_block_wall`) | log side | 6 logs/wood `###/###` → 6; stonecutter: log, wood, end grain |
| `stripped_<bark>_wall` (Stripped Oak Log Wall) | stripped log side | 6 stripped logs/wood → 6; stonecutter: any log-family block |
| `<wood>_end_grain` (Oak Log End Grain) | log top on all six faces | stonecutter: log, wood |
| `stripped_<wood>_end_grain` (Stripped Oak Log End Grain) | stripped log top | stonecutter: any log-family block |

Every stonecutter recipe goes straight from its input to the result, with no stripping step first.
Each one trades one log's worth for one log's worth. That matches the mod's existing log → wood/stripped
stonecutting (`data/minecraft/recipe/*_stonecutting.json`), so nothing undercuts vanilla's
4 logs → 3 wood more than that already does. End grain also cuts back into the log and wood, and either end grain
into the stripped log and wood. Stripping can't be undone, so stripped blocks never cut into
unstripped ones.

Plank walls have no crafting recipe because `###/###` of planks is the vanilla trapdoor. The QoL
generator derives a wall → wood recipe from each bark wall's shaped recipe.

English names say what the block is made of. The ids keep their original names (`oak_wall`, `oak_wood_wall`), so
already placed blocks survive.

In the Building Blocks tab, end grain follows the wood's stripped wood and the walls follow its
fence gate.

Each block copies the properties of its vanilla fence (walls) or log (end grain), which gives it
the right strength, sound, map color and lava ignition. End grain is a `RotatedPillarBlock` because
the log's map color reads the `axis` property. Every axis uses the same `cube_all` model. Wood that isn't from the Nether is flammable like
fences and logs (Fabric `FlammableBlockRegistry`) and burns for 300 ticks. Fuel is registered with
Fabric `FuelValueEvents` on 26.1.2, and with the `cookingFuel` item property on 26.3, where fuel is an
item component.

End grain is in its wood's *item* log tag (`#minecraft:oak_logs`, `#crimson_stems`,
`#bamboo_blocks`, …), so it crafts into planks, smelts into charcoal and is fuel. Nether end grain
is in `#non_flammable_wood`, so it isn't fuel. It is deliberately left out of the *block* log tags,
where trees would grow through it, creaking hearts would accept it, and worldgen would treat it as
a natural log. `#prevents_nearby_leaf_decay` keeps leaves next to it alive instead.

The walls are in `#minecraft:walls` because gates, bars, pathfinding and the connection logic read
that tag. Vanilla's `mineable/pickaxe` contains all of `#walls`, and a tag can't exclude entries.
`ToolMixin` therefore skips the `mineable/pickaxe` rule in `Tool.getMiningSpeed` for blocks in
`#deeperdark:not_mineable_with_pickaxe`, so wooden walls mine fast only with an axe. Drops are
unaffected, because the walls don't require a tool.

## Generated files

`tools/generate_wood_variant_assets.py` writes the blockstates, models, item definitions, recipes,
loot tables (one per version, as the formats differ), tag entries and `en_us` names. Poplar files
go to `resources-26.3/`, and its entries in shared tags are `"required": false`. Rerun
`tools/generate_qol_recipes.py` and the conflict checker after running it (`QolRecipes.md`).

## Fence/wall connections

`FenceBlockMixin` and `WallBlockMixin` add to each `connectsTo` so any fence connects to anything in
`#walls` and any wall to anything in `#fences`. This covers all fences and walls, including other mods'.
A tag-only approach was rejected: putting walls in `#fences` would also let leads attach to walls and
change how mobs path around them.

## Known limits

- Axes don't strip bark walls or end grain. Use the stonecutter on stripped wood or logs.
- Other locales fall back to English names.
