# Testing Guide for Fixed Issues

## Setup
1. Build the mod: `.\gradlew.bat build`
2. Copy the jar from `build/libs/` to your server's `mods` folder
3. Start the server/world

## Test 1: Golden Cauldron Filling & Persistence

### Steps:
1. Get a golden cauldron: `/give @s sugar[item_model="minecraft:golden_cauldron_item",item_name='{"text":"Golden Cauldron"}']`
2. Place the golden cauldron
3. Verify golden particles appear around it
4. Fill it with a water bucket
   - **Expected**: Cauldron fills with water, golden display and particles remain
   - **Previous bug**: Would revert to iron cauldron, display entity disappeared
5. Empty it with a bucket
   - **Expected**: Back to empty golden cauldron
6. Fill with lava bucket
   - **Expected**: Lava-filled golden cauldron with particles
7. Save and quit the world
8. Reopen the world
9. **Expected**: Golden cauldron is still there, with proper lighting and particles
   - **Previous bug**: Would not track properly after reload

## Test 2: Custom Block Persistence After Reload

### Steps:
1. Place several different custom blocks:
   - Golden cauldron
   - Flint block: `/give @s sugar[item_model="minecraft:flint_block",item_name='{"text":"Flint Block"}']`
   - Leather block: `/give @s sugar[item_model="minecraft:leather_block",item_name='{"text":"Leather Block"}']`
   - Gunpowder block: `/give @s sugar[item_model="minecraft:gunpowder_block",item_name='{"text":"Gunpowder Block"}']`

2. Note their positions
3. Save and quit the world
4. Reopen the world
5. **Expected**: All custom blocks are present at their original positions
6. Kill all display entities: `/kill @e[type=item_display]`
7. Wait 1-2 seconds
8. **Expected**: All custom block displays respawn automatically
   - **Previous bug**: Only blocks from current session would respawn

## Test 3: Lighting Updates Persistence

### Steps:
1. Place a custom block (e.g., flint block)
2. Place a torch next to it
   - **Expected**: Block brightens
3. Remove the torch
   - **Expected**: Block darkens
4. Place the torch again
5. Save and quit the world
6. Reopen the world
7. Remove the torch
8. **Expected**: Block darkens properly
   - **Previous bug**: Block would not react to lighting changes after reload

## Test 4: Break Sound (No Double Sound)

### Steps:
1. Place a golden cauldron
2. Break it with your fist or tool
3. **Expected**: You hear only ONE metal break sound (not two)
   - **Previous bug**: Both vanilla cauldron sound and custom metal sound played
4. Verify golden particles appear when breaking
5. Verify you receive the golden cauldron item back (not iron cauldron)

## Test 5: Different Cauldron States

### Steps:
1. Place a golden cauldron
2. Fill it partially (water bottles work for this):
   - Use a water bottle on empty cauldron (level 1)
   - Use another water bottle (level 2)
   - Use a third water bottle (level 3)
3. **Expected**: At each level, golden display and particles remain
4. Use a bucket on level 3 cauldron
5. **Expected**: Get water bucket, cauldron becomes empty golden cauldron
6. Test with lava bucket
7. **Expected**: Golden lava cauldron with particles

## Test 6: Data File Verification

### Steps:
1. Place several custom blocks
2. Stop the server
3. Navigate to your world folder: `<world>/data/`
4. Look for `deeperdark_custom_blocks.dat`
5. **Expected**: File exists and contains NBT data
6. Start server again
7. **Expected**: All custom blocks load correctly

You can inspect the NBT file with tools like NBTExplorer or by converting it to SNBT.

## Test 7: Golden Cauldron Break with Different Tools

### Steps:
1. Place golden cauldrons
2. Break them with:
   - Fist (survival mode)
   - Pickaxe
   - Creative mode
3. **Expected**: 
   - Survival with proper tool: Drop golden cauldron item
   - Survival without proper tool: No drop (if not harvestable)
   - Creative: No drop
   - All cases: Single break sound, golden particles

## Test 8: Chunk Loading/Unloading

### Steps:
1. Place custom blocks in multiple chunks
2. Walk far away (unload the chunks)
3. Return to the chunks
4. **Expected**: Custom blocks still present and functional
5. Kill display entities while near the blocks: `/kill @e[type=item_display,distance=..32]`
6. Wait a few seconds
7. **Expected**: Display entities respawn

## Expected Behavior Summary

### ✅ Fixed:
- Golden cauldrons maintain their display and particles when filled
- Custom blocks persist across world reloads
- Lighting updates work after world reload
- Single break sound (no double sound)
- Custom block tracking is saved to disk
- Display entities respawn for blocks placed before world reload

### ⚠️ Partial (Serverside Limitation):
- Vanilla break particles still appear briefly
  - This is a client-side rendering issue that cannot be fully suppressed serverside
  - **Workaround**: Consider using transparent blocks as the base (barrier, glass, etc.)

## Troubleshooting

### If blocks don't respawn after reload:
1. Check server console for errors about `deeperdark_custom_blocks.dat`
2. Verify the file exists in `<world>/data/`
3. Check file permissions

### If golden cauldron reverts to iron:
1. Check that the interaction handler is working (no errors in console)
2. Verify you're using vanilla buckets (not modded ones)
3. Try in a fresh world to rule out conflicts

### If you hear double sounds:
1. Make sure you're using the latest build
2. Check that `CustomBlockManager` sets block to AIR before playing sound
3. Look for any other mods that might be adding sounds

## Performance Notes

- The custom block tracker runs every second (20 ticks)
- With hundreds of custom blocks, this should be fine
- With thousands of custom blocks, you might notice slight tick time increase
- Monitor with `/debug start` and `/debug stop` if concerned

## Console Output to Monitor

Look for these log messages:
- `[Deeper Dark] Mod initialized!` - Confirms mod loaded
- No errors about "Failed to load custom block data"
- No errors about "Failed to save custom block data"

