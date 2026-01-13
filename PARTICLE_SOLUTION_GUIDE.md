# Particle & Sound Interception - Technical Deep Dive

## The Problem

### Current State
When breaking custom blocks, two things happen that we'd like to prevent:
1. **Double sound**: Base block break sound + custom break sound
2. **Wrong particles**: Base block particles (e.g., cobblestone texture) + custom particles (e.g., gold texture)

### What We Fixed
✅ **Double sound** - FIXED by setting block to AIR before playing custom sound

### What Remains
⚠️ **Base block particles** - Still appear because they're rendered client-side

## Why It's Hard to Fix Serverside

### Client-Side Rendering
Block break particles are rendered on the client side based on:
1. The block state at the time of breaking
2. Client-side particle spawning code
3. Happens before the server can update the block to AIR

### The Sequence
```
Player starts breaking block
  ↓
Client renders breaking animation particles (based on current block)
  ↓
Player finishes breaking
  ↓
Client sends break packet to server
  ↓
Server processes break (our event handler)
  ↓
Server sets block to AIR
  ↓
Server spawns custom particles
  ↓
Client receives updated block state & custom particles
```

The problem: Steps 1-2 happen on the client BEFORE the server can intervene.

## Solutions (In Order of Complexity)

### Solution 1: Use Transparent Base Blocks (RECOMMENDED)
**Difficulty**: Low  
**Effectiveness**: High  
**Serverside Only**: Yes

#### How it works:
- Use `Blocks.BARRIER`, `Blocks.GLASS`, or `Blocks.STRUCTURE_VOID` as the base block
- Vanilla particles will still spawn, but they'll be invisible/transparent
- Item display handles all visual aspects

#### Implementation:
```java
// In each custom block event (e.g., FlintBlockEvents.java)
// Change from:
CustomBlockManager.place(world, placePos, stack, Blocks.COBBLED_DEEPSLATE, null)

// To:
CustomBlockManager.place(world, placePos, stack, Blocks.BARRIER, null)
```

#### Pros:
- Completely serverside
- No client mod needed
- Particles become invisible
- Collision still works
- Very simple to implement

#### Cons:
- Can't use interesting block properties (redstone, etc.)
- Barrier blocks might have unintended interactions

### Solution 2: Cancel Break Particles with Mixin
**Difficulty**: Medium  
**Effectiveness**: High  
**Serverside Only**: Yes (but trickier)

#### How it works:
- Create a mixin that intercepts particle spawning on the server
- Check if the position has a custom block display entity
- Cancel particle spawning if it does

#### Implementation:
```java
@Mixin(ServerWorld.class)
public class ServerWorldParticleMixin {
    @Inject(method = "spawnParticles*", at = @At("HEAD"), cancellable = true)
    private void preventCustomBlockParticles(..., BlockPos pos, ..., CallbackInfo ci) {
        // Check if this is a custom block position
        if (isCustomBlock(pos)) {
            ci.cancel();
        }
    }
}
```

#### Pros:
- Still serverside
- More precise control
- Doesn't change base block

#### Cons:
- More complex
- Might cancel unintended particles
- Requires testing with particle-heavy mods

### Solution 3: Client-Side Mod (MOST EFFECTIVE)
**Difficulty**: High  
**Effectiveness**: Very High  
**Serverside Only**: No

#### How it works:
- Create a client-side companion mod
- Intercept client particle rendering
- Suppress vanilla break particles for custom blocks
- Server sends custom packets to identify custom blocks

#### Implementation:
Server side:
```java
// Send packet when custom block is placed
CustomBlockTrackerPacket.send(player, pos, modelId);

// Send packet when custom block is broken
// (to preemptively suppress particles)
```

Client side:
```java
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    private void suppressCustomBlockParticles(BlockPos pos, ..., CallbackInfo ci) {
        if (ClientCustomBlockTracker.isCustomBlock(pos)) {
            ci.cancel();
        }
    }
}
```

#### Pros:
- Complete control
- Perfect particle suppression
- Can add custom break animations
- Best user experience

#### Cons:
- Requires client mod
- Players must install it
- No longer fully serverside
- More maintenance

### Solution 4: Hybrid Approach
**Difficulty**: Medium  
**Effectiveness**: High  
**Serverside Only**: Optional client mod

#### How it works:
- Use transparent base blocks (Solution 1) as default
- Provide optional client mod (Solution 3) for enhanced visuals
- Server works perfectly for all players
- Players with client mod get zero vanilla particles

#### Implementation:
1. Implement Solution 1 (transparent base)
2. Create optional client mod that:
   - Detects custom blocks via display entities
   - Suppresses break particles
   - Adds enhanced break effects

#### Pros:
- Works for everyone (serverside)
- Enhanced for those with client mod
- Graceful degradation
- Best of both worlds

#### Cons:
- Two codebases to maintain
- Some complexity

## Current Implementation Details

### What We Did
In `CustomBlockManager.onBreak()`:
```java
// Set to AIR first (with flag 2 to skip updates)
world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

// Then do everything else
// Drops, particles, sounds
```

This fixes the double sound because:
- `flag 2` prevents neighbor updates
- Block is AIR before sound plays
- Vanilla break sound checks block type (now AIR)
- Only our custom sound plays

### Why Particles Still Appear
The client doesn't know the block is AIR yet when it starts the break animation. The particles are rendered immediately based on the block state the client sees.

## Recommendations

### For Your Server (Fully Serverside)
**Implement Solution 1**: Use transparent base blocks

Changes needed:
1. Edit each custom block event file
2. Change base block to `Blocks.BARRIER` or `Blocks.GLASS`
3. Test collision and interaction behavior
4. Done!

This will make particles invisible while keeping everything serverside.

### For Enhanced Experience (Optional)
**Also implement Solution 4**: Create optional client mod

Benefits:
- Server works for everyone
- Players with mod get perfect experience
- You can add fancy break animations
- Future-proof for more features

## Code Examples

### Example 1: Transparent Base Block
```java
// FlintBlockEvents.java
if (!world.isClient()) {
    // Use BARRIER instead of COBBLED_DEEPSLATE
    if (CustomBlockManager.place(world, placePos, stack, Blocks.BARRIER, null)) {
        // Still play DEEPSLATE sound for realism
        world.playSound(null, placePos, BlockSoundGroup.DEEPSLATE.getPlaceSound(), 
            SoundCategory.BLOCKS, 1f, 1f);
        
        if (!player.isCreative()) {
            stack.decrement(1);
        }
    }
}
```

### Example 2: Server-Side Particle Suppression Mixin
```java
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow public abstract Box getBlockState(BlockPos pos);
    
    @Inject(
        method = "spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I",
        at = @At("HEAD"),
        cancellable = true
    )
    private void suppressCustomBlockParticles(
        ParticleEffect particle,
        double x, double y, double z,
        int count,
        double deltaX, double deltaY, double deltaZ,
        double speed,
        CallbackInfoReturnable<Integer> cir
    ) {
        // Check if this is a block break particle
        if (particle instanceof BlockStateParticleEffect) {
            BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
            
            // Check if there's a display entity here (custom block)
            Box box = new Box(pos);
            List<?> displays = ((ServerWorld)(Object)this).getEntitiesByClass(
                ItemDisplayEntity.class, box, e -> true
            );
            
            if (!displays.isEmpty()) {
                cir.setReturnValue(0); // Cancel particles
            }
        }
    }
}
```

## Testing Particle Fixes

### Test 1: Transparent Base Block
1. Change flint block to use BARRIER base
2. Place flint block
3. Break flint block
4. Result: Should see NO cobblestone particles, only custom particles

### Test 2: Particle Mixin
1. Add ServerWorldMixin
2. Place any custom block
3. Break it
4. Console: Should log "Suppressing particles for custom block"
5. Result: No vanilla particles

## Performance Considerations

### Transparent Base Blocks
- **Performance impact**: None
- Blocks are just as efficient as any other block

### Particle Mixin
- **Performance impact**: Minimal
- Only checks on particle spawn
- Quick display entity check
- Negligible overhead

### Client Mod
- **Performance impact**: Minimal on client
- Intercepts particle rendering
- Very fast check against cached positions

## Conclusion

For your use case (fully serverside mod), **Solution 1 (transparent base blocks)** is the best approach:
- Simple to implement
- Zero performance impact  
- Solves the visible particle problem
- No client mod required
- Maintains serverside-only philosophy

The vanilla particles will still technically spawn, but players won't see them because barrier blocks are invisible.

