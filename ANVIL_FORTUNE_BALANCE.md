# Anvil and Fortune Balance Modifications

## Overview
This document describes the server-side modifications made to balance anvil costs and Fortune enchantment behavior in your Minecraft mod.

## Changes Made

### 1. Configuration Updates (`DeeperDarkConfig.java`)

Added new configuration options to `ConfigInstance`:

#### Anvil Configuration
- `anvilRepairCost` (default: 0) - XP level cost for repairing items with materials (e.g., diamond + diamond pickaxe)
  - Set to 0 for free repairs
  - Can be adjusted to any desired level cost
  
- `anvilEnchantCost` (default: 5) - Flat XP level cost for any enchanting combination
  - Applies to book + tool, book + book, or combining two enchanted items
  - Provides consistent, predictable costs for enchanting

#### Fortune Enchantment Configuration
- `customFortuneEnabled` (default: true) - Toggle custom Fortune behavior on/off
  - When false, uses vanilla Minecraft Fortune behavior
  - When true, uses the custom balanced system
  
- `fortune1DropChance` (default: 0.25) - Probability (0.0-1.0) that Fortune I drops 2 items instead of 1
  - 0.25 = 25% chance
  
- `fortune2DropChance` (default: 0.50) - Probability (0.0-1.0) that Fortune II drops 2 items instead of 1
  - 0.50 = 50% chance
  - Fortune III and higher also use this same chance
  
- `fortuneMaxDrops` (default: 2) - Maximum number of items Fortune can drop
  - Prevents Fortune from ever dropping more than 2x the base amount
  - Vanilla Fortune III can drop 4+ items; this limits it

### 2. Anvil Screen Handler Mixin (`AnvilScreenHandlerMixin.java`)

**File**: `src/main/java/net/noahsarch/deeperdark/mixin/AnvilScreenHandlerMixin.java`

This mixin modifies the anvil's cost calculation logic:

**How it works:**
1. Intercepts the `updateResult()` method after vanilla calculates costs
2. Determines if the operation is a repair or enchanting:
   - **Repair**: Detected when using materials to fix item durability (e.g., diamond + pickaxe)
   - **Enchanting**: Detected when combining enchanted books or enchanted items
3. Applies configured costs based on operation type
4. Caps costs at level 39 to prevent "Too Expensive!" message

**Server-side**: This modification is fully server-side. Clients will see the correct costs without needing the mod.

### 3. Fortune Enchantment Mixin (`ApplyBonusLootFunctionOreDropsMixin.java`)

**File**: `src/main/java/net/noahsarch/deeperdark/mixin/ApplyBonusLootFunctionOreDropsMixin.java`

This mixin modifies how the Fortune enchantment affects loot drops (ores, crops, etc.):

**How it works:**
1. Intercepts the `ApplyBonusLootFunction.process()` method
2. Gets the Fortune level from the tool used
3. Applies custom probability-based drops:
   - Fortune I: rolls for chance to get +1 item (25% by default)
   - Fortune II/III: rolls for chance to get +1 item (50% by default)
4. Caps total drops at configured maximum (2x by default)

**Behavior changes from vanilla:**
- **Vanilla Fortune III**: Can drop 4+ items from a single ore
- **Custom Fortune II/III**: Maximum 2 items, with only 50% chance

**Server-side**: This modification is fully server-side. The loot table calculations happen server-side, so clients don't need the mod.

### 4. Mixin Configuration (`deeperdark.mixins.json`)

Added `ApplyBonusLootFunctionOreDropsMixin` to the mixins list.

## Configuration Examples

### Example 1: Free Repairs, Moderate Enchanting Cost
```yaml
anvilRepairCost: 0
anvilEnchantCost: 5
```

### Example 2: All Anvil Operations Cost 1 Level
```yaml
anvilRepairCost: 1
anvilEnchantCost: 1
```

### Example 3: Vanilla Fortune Behavior
```yaml
customFortuneEnabled: false
```

### Example 4: Very Rare Fortune Bonus
```yaml
customFortuneEnabled: true
fortune1DropChance: 0.10  # 10% chance
fortune2DropChance: 0.25  # 25% chance
fortune3DropChance: 0.40  # 40% chance
fortuneMaxDrops: 2
```

### Example 5: Guaranteed Fortune Bonus
```yaml
customFortuneEnabled: true
fortune1DropChance: 1.0   # 100% chance
fortune2DropChance: 1.0   # 100% chance
fortune3DropChance: 1.0   # 100% chance
fortuneMaxDrops: 2
```

## Testing Recommendations

### Testing Anvil Changes
1. **Repair Testing**:
   - Damage a diamond pickaxe
   - Place it in anvil with a diamond
   - Verify cost matches `anvilRepairCost` config value

2. **Enchanting Testing**:
   - Combine two enchanted books
   - Verify cost matches `anvilEnchantCost` config value
   - Try book + tool combination
   - Try combining two enchanted tools

### Testing Fortune Changes
1. **Fortune I Testing**:
   - Mine 100 diamond ores with Fortune I pickaxe
   - Count total diamonds obtained
   - Should average ~1.25x the number of ores with default config (25% bonus chance)

2. **Fortune II/III Testing**:
   - Mine 100 diamond ores with Fortune II or III pickaxe
   - Count total diamonds obtained
   - Should average ~1.5x the number of ores with default config (50% bonus chance)
   - Should never drop more than 2 diamonds per ore

3. **Verify Configuration**:
   - Change config values in `config/deeperdark.yaml`
   - Restart server
   - Verify new values take effect

## Technical Notes

### Why These Approaches?

**Anvil Costs**: 
- Injecting at the end of `updateResult()` allows us to override the vanilla calculation completely
- We detect operation type by examining the item stacks and repair usage counter
- This gives server operators full control over economic balance

**Fortune Modification**:
- Injecting into `ApplyBonusLootFunction.process()` lets us intercept all fortune-based drops
- We use the enchantment level from the tool to determine behavior
- The probability-based system gives more predictable and balanced drops
- Capping at 2x prevents excessive resource generation

### Compatibility Notes

- Both modifications are **fully server-side**
- Clients without the mod will see correct behavior
- Config changes require server restart to take effect
- These changes affect all items that use anvils and Fortune enchantment

## Troubleshooting

### Anvil costs not changing
- Verify config file is in `config/deeperdark.yaml`
- Check config values are properly formatted (integers for costs)
- Restart the server after config changes

### Fortune still drops too many items
- Verify `customFortuneEnabled: true` in config
- Check `fortuneMaxDrops` value (should be 2)
- Ensure you're testing with newly mined blocks (not old drops)

### "Too Expensive!" message still appears
- This shouldn't happen as costs are capped at 39
- If it does, check for conflicts with other mods
- Verify the mixin is loading (check server logs)

## Future Enhancements

Possible additions for future versions:
- Per-enchantment cost configuration
- Different Fortune behavior per block type
- Anvil cost scaling based on item type
- Experience cost vs. material cost options

