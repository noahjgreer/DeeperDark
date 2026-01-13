# Changes Summary - Fortune III & Universal Fortune Nerf

## What Changed

### 1. Added Fortune III Configuration
- Added `fortune3DropChance` field to `DeeperDarkConfig.java` (default: 0.75 = 75% chance)
- Now you can configure Fortune I, II, and III separately
- Fortune III defaults to 75% chance to drop an extra item (instead of vanilla's much higher drop rates)

### 2. Universal Fortune Nerf
**Previous behavior**: Only affected the `OreDrops` formula (mainly ores)

**New behavior**: Affects ALL Fortune-based drops including:
- ✓ Ores (diamond, coal, redstone, lapis, emerald, copper, quartz, etc.)
- ✓ Crops (wheat, carrots, potatoes, beetroot, nether wart, sweet berries, etc.)
- ✓ Gravel → Flint drops
- ✓ Glowstone dust
- ✓ Sea lanterns (prismarine crystals)
- ✓ Melon slices
- ✓ And any other block that uses Fortune

**How it works**: The mixin now intercepts ALL `ApplyBonusLootFunction` calls, regardless of formula type (OreDrops, UniformBonusCount, BinomialWithBonusCount). This ensures consistent Fortune behavior across the entire game.

## Configuration

### Default Values
```yaml
customFortuneEnabled: true
fortune1DropChance: 0.25  # 25% chance for extra drop
fortune2DropChance: 0.50  # 50% chance for extra drop
fortune3DropChance: 0.75  # 75% chance for extra drop
fortuneMaxDrops: 2        # Never more than 2x base drops
```

### Vanilla Comparison
**Vanilla Fortune III on Diamond Ore**:
- Can drop 1, 2, 3, or 4 diamonds
- Average: ~2.2 diamonds per ore
- Maximum: 4 diamonds

**Custom Fortune III with Default Config**:
- Drops 1 or 2 diamonds (75% chance for 2)
- Average: 1.75 diamonds per ore
- Maximum: 2 diamonds (enforced cap)

## Files Modified

1. **DeeperDarkConfig.java**
   - Added `fortune3DropChance` field
   - Updated config header comments

2. **ApplyBonusLootFunctionOreDropsMixin.java**
   - Removed OreDrops-specific check (now applies to ALL formulas)
   - Added separate Fortune III handling
   - Simplified code by removing formula type checking

3. **ANVIL_FORTUNE_BALANCE.md**
   - Updated documentation to reflect Fortune III
   - Added note about universal Fortune nerf
   - Updated examples and testing instructions

## Testing Tips

1. **Test with different blocks**:
   - Diamond ore (should drop max 2)
   - Coal ore (should drop max 2)
   - Wheat (should drop max 2 wheat)
   - Gravel (check flint drop chance)

2. **Compare Fortune levels**:
   - Fortune I: Should get bonus ~25% of the time
   - Fortune II: Should get bonus ~50% of the time
   - Fortune III: Should get bonus ~75% of the time

3. **Verify the cap**:
   - No matter what, you should NEVER see more than 2 drops from a single block
   - This is the key balance change from vanilla

## Why This Approach?

By removing the OreDrops formula check, we ensure that:
1. **Consistency**: All Fortune drops behave the same way
2. **Balance**: No exploits through specific Fortune formulas
3. **Simplicity**: One system to understand and configure
4. **Server-side**: Still fully server-side, no client mod needed

## Notes

- The Fortune nerf is MUCH more aggressive than vanilla
- Fortune III with 75% bonus chance is still significantly nerfed from vanilla
- You can disable this entirely by setting `customFortuneEnabled: false`
- All changes take effect after server restart
- Configuration is in `config/deeperdark.yaml`

