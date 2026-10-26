# Wood variants and fence/wall connections

## Blocks

`block/WoodVariantBlocks` registers five blocks for every wood type (oak, spruce, birch, jungle,
acacia, dark oak, mangrove, cherry, pale oak, bamboo, crimson, warped, and poplar on 26.3 only):

| Block | Texture | Recipes |
| --- | --- | --- |
| `<wood>_wall` | planks | stonecutter: planks |
| `<bark>_wall` (`oak_wood_wall`, `crimson_hyphae_wall`, `bamboo_block_wall`) | log side | 6 wood `###/###` → 6, stonecutter |
| `stripped_<bark>_wall` | stripped log side | same, from stripped wood |
| `<wood>_end_grain` | log top on all six faces | stonecutter: log or wood |
| `stripped_<wood>_end_grain` | stripped log top | stonecutter: stripped log or wood |

Plank walls have no crafting recipe because `###/###` of planks is the vanilla trapdoor.
The QoL generator derives a wall → wood recipe from each bark wall's shaped recipe. Bark walls
don't take logs, because log → wall → wood would undercut vanilla's 4 logs → 3 wood.

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
