# Quick Reference - Anvil & Fortune Balance

## Summary
Your mod now has configurable anvil costs and a drastically nerfed Fortune enchantment that applies to ALL fortune-based drops.

## Config File Location
`config/deeperdark.yaml`

## Default Configuration

### Anvil Costs
```yaml
anvilRepairCost: 0      # Free repairs with materials
anvilEnchantCost: 5     # 5 levels for any enchanting
```

### Fortune Enchantment
```yaml
customFortuneEnabled: true
fortune1DropChance: 0.25   # 25% chance for bonus drop
fortune2DropChance: 0.50   # 50% chance for bonus drop  
fortune3DropChance: 0.75   # 75% chance for bonus drop
fortuneMaxDrops: 2         # Maximum 2x drops (never more)
```

## What Fortune Affects
- ✓ **Ores**: Diamond, emerald, coal, redstone, lapis, copper, quartz, etc.
- ✓ **Crops**: Wheat, carrots, potatoes, beetroot, nether wart, sweet berries
- ✓ **Gravel**: Flint drops
- ✓ **Glowstone**: Dust drops
- ✓ **Sea Lanterns**: Prismarine crystal drops
- ✓ **Melons**: Slice drops
- ✓ **Everything else** that uses Fortune

## Quick Comparisons

### Vanilla vs Custom Fortune III on Diamond Ore
| System | Min Drops | Max Drops | Average | Cap |
|--------|-----------|-----------|---------|-----|
| Vanilla | 1 | 4 | ~2.2 | None |
| Custom (default) | 1 | 2 | 1.75 | 2 |

### Anvil Costs
| Operation | Vanilla | Custom (default) |
|-----------|---------|------------------|
| Repair with material | Varies | 0 (free) |
| Enchanting (any) | Varies | 5 (flat) |

## Testing Commands

After building and running your server, test with:

```mcfunction
# Give yourself Fortune tools
/give @s diamond_pickaxe{Enchantments:[{id:"fortune",lvl:1}]}
/give @s diamond_pickaxe{Enchantments:[{id:"fortune",lvl:2}]}
/give @s diamond_pickaxe{Enchantments:[{id:"fortune",lvl:3}]}

# Test mining 100 diamond ores and count the drops
# Fortune I: Should get ~117 diamonds (16.66% bonus)
# Fortune II: Should get ~133 diamonds (33.33% bonus)
# Fortune III: Should get ~150 diamonds (50% bonus)
```

## Disable Fortune Nerf

To use vanilla Fortune behavior:
```yaml
customFortuneEnabled: false
```

## Server-Side Only
✓ Both modifications are fully server-side
✓ Vanilla clients can connect
✓ No client-side mod required
✓ Changes apply immediately to all players

## Restart Required
After changing config values, restart the server for changes to take effect.

## Files Modified
1. `src/main/java/net/noahsarch/deeperdark/DeeperDarkConfig.java`
2. `src/main/java/net/noahsarch/deeperdark/mixin/AnvilScreenHandlerMixin.java`
3. `src/main/java/net/noahsarch/deeperdark/mixin/ApplyBonusLootFunctionOreDropsMixin.java`
4. `src/main/resources/deeperdark.mixins.json`

## Documentation
- Full documentation: `ANVIL_FORTUNE_BALANCE.md`
- Change summary: `FORTUNE_CHANGES_SUMMARY.md`

