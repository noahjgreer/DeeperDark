# ItemSwapper Compatibility

[ItemSwapper](https://github.com/tr7zw/ItemSwapper) (client mod, source in `ref-decompiled/Mods/ItemSwapper`)
builds its swap wheels and lists from resource-pack JSON. DeeperDark adds its items to ItemSwapper's
default palettes purely through resources: no code, no dependency, and the files do nothing when
ItemSwapper isn't installed.

## Files

- `src/main/resources/assets/itemswapper/itemgroups/v2/deeperdark/*.json`: one file per target palette
  or list. `paletteModification` / `listModification` with `target` set to an ItemSwapper default
  such as `itemswapper:v2/wood/oak` and `addItems` listing our item ids.
- `src/main/resources-26.3/.../deeperdark/wood_poplar.json`: poplar only exists on 26.3.

They must be in the `itemswapper` namespace (the loader ignores every other namespace) and under the
`v2/` path. The `deeperdark/` subfolder keeps their ids from overriding ItemSwapper's own files.

## Mapping

Wood variants go into their wood's palette, stone/cobblestone brick/sandstone/deepslate/basalt/end stone/purpur/quartz/resin
blocks into both their material palette and the matching `block_sets/*` palette, coloured glass doors into
their `colors/*` palette. Collars join the armor list, item magnets the tools list, boxes the shulker list,
milk bottles the potions list, copper rails the rail palette. Storage blocks sit beside their source item in `crafting/*`.

## Known limits

- Targets are ItemSwapper's internal paths; if it renames a palette, our modification is silently ignored.
  Check against the reference tree when updating ItemSwapper.
- Unknown item ids are skipped with a log warning, so a removed DeeperDark item only causes log noise.
- New items aren't picked up automatically: add them to the matching file here.
- Vaults and Creature items are not mapped.
