# Assets

## Item model definitions

Every new item and block needs a file in `src/main/resources/assets/deeperdark/items/` as well as
its block/item model:

```json
{
    "model": {
        "type": "minecraft:model",
        "model": "deeperdark:block/BLOCK_NAME"
    }
}
```

- Walls: point to the `_inventory` model (e.g. `deeperdark:block/stone_wall_inventory`).
- Stairs and slabs: point to the base block model (e.g. `deeperdark:block/deepslate_stairs`).
- Pillars and simple blocks: point directly to the block model.

## Text

Player-visible text goes through translatable components backed by
`assets/deeperdark/lang/en_us.json`. Other locales (`es_*`, `lol_us`) exist; when removing a key,
remove it from every locale.
