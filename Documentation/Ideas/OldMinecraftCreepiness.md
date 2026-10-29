# Old Minecraft creepiness: research and feature ideas

A list of the bugs and removed features that made Alpha/Beta-era Minecraft feel creepy, with ideas for
recreating each in DeeperDark. This is a research and idea list, not a system doc. Once something here
is built, it gets its own doc in `Documentation/`.

Community legends (Herobrine and other creepypastas) are out of scope for now. Herobrine may come later.

Each entry is tagged with how much to trust it:

- **[Verified]**: confirmed against the Minecraft Wiki or a Mojang bug report during research.
- **[Memory]**: widely known, but not re-checked against a source this time.

DeeperDark already ports **void fog** (`VoidFogMixin`), and has the **Creature** entity (driven by
`CreatureManager`) if a feature needs a scripted entity.

---

## Design decisions

- **Everywhere, not just underground.** The "someone else is here" sounds play on the surface too, and
  they match the surroundings: grass and dirt in a plains, wood in a forest, stone in a cave.
- **Old frequency.** Roll once every **6000–18000 ticks (5–15 min)** per player, like the pre-20w12a cave
  sounds. Rarity is what made them work.
- **Per player.** Each sound plays to one player only (`ServerPlayer` packet, not a level-wide sound).
  The friend standing next to you heard nothing, so you can't confirm it was real. That is scarier
  than a shared scare.

---

## 1. Real bugs and mechanics that felt haunted

### Cave ambience: the "footsteps and digging" you didn't make  [Verified]
The 13 original cave sounds (Alpha v1.0.3) include things that sound like **footsteps on gravel, someone
digging stone, creaking, and distant groans**. Hearing "someone mining" in an empty world was real.

How the old (pre-20w12a) algorithm worked:
- It rolled every **6000–18000 ticks**.
- It picked one point from a 3×3×3 grid of offsets **(−4, 0, +4)** around the player's feet. That point
  had to be air, not the feet position, with **zero sky light** and block light ≤ a random 0–8.
- The sound played **at that point**, with a 16-block attenuation, so it came from a specific direction
  a few blocks away. That is "something in the wall next to me."
- Pitch was random between **0.8× and 1.0×**. This was removed in 20w10a.

Modern Java uses a "mood" percentage instead. Each tick it samples one random block in a 17×17×17 cube,
dark blocks raise mood, and at 100% a sound plays. In full darkness that works out to about one sound
every 5 minutes.

**Idea:** two parts.
1. **Restore the old cave-sound behaviour:** the close, directional placement and the random pitch,
   using the vanilla cave sounds. This keeps the old darkness rule, so it only fires underground.
2. **Add "presence" sounds everywhere:** on the same 5–15 minute roll, pick a nearby air position next to a
   real block, and play that block's own `SoundType` (break, hit or step) there. The sound then matches
   the environment for free: grass blocks in a plains, logs in a forest, stone in a cave. To make
   forests sound like wood, prefer logs and stone over grass when both are in range. The variations:
   - a short run of hits, then a break (someone mining);
   - three or four footsteps walking past;
   - a single door or chest sound if one is nearby.

### Missing and phantom footsteps  [Verified]
MC-1133 (in the game since Alpha, fixed in 23w18a): near block edges the game thought you were airborne,
so **your own footsteps would cut out**. Mixed in with the cave sounds, you would hear steps that didn't
match your walking.

**Idea:** two effects.
- **Dropped steps.** Very rarely, skip a few of the player's own footstep sounds.
- **The Follower.** Sometimes, while the player walks, play footsteps that match the block under them,
  about half a second late, from a few blocks behind. When the player stops, the steps take one extra
  step and then stop. This works everywhere, using the same per-player roll.

### Mob sounds through solid rock  [Memory]
Sound was never occluded. Zombie groans, skeleton rattles and creeper hisses from unlit caves below came
straight through the stone, so a quiet house could fill with moaning from nowhere.

**Idea:** This is already vanilla behaviour. To push it further, now and then play a hostile mob's idle
sound from a solid block below or beside the player, with no mob there.

### Ghost blocks and rubberbanding  [Verified]
Ghost blocks come from client–server desync. A block you mined comes back, or a block you placed
vanishes. On a lagging server, blocks "reappearing" after you had cleared them looked like someone
undoing your work.

**Idea:** A deliberate, rare effect. Send one client a block update putting back a block it recently
mined, then correct it a few seconds later or when they touch it. The server state never changes, so
nothing is lost or duplicated.

### The Far Lands  [Verified]
An integer overflow in terrain noise created towering, spongy walls of terrain around **±12,550,824**,
with dark tunnels inside, broken lighting and heavy lag. The "Farther Lands" sat at around ±1,004,065,924.
They were fixed in Java Beta 1.8. 26.3 Pre-Release 1 re-added them behind an `ENABLE_FARLANDS` debug
property (Nether and End only, in vanilla bounds). That is worth checking in `ref-decompiled/26.3`.

**Idea:** A rare Far-Lands-style biome or structure. Alternatively, an "edge of the world" area in the
Deeper Dark, a slice of broken noise terrain.

### Void fog and void particles  [Memory]
In Beta 1.8 through 1.7, the view fogged in toward black as you got near bedrock, and small dark
particles drifted up out of the void. **DeeperDark already ports the fog part** (`VoidFogMixin`).

**Idea:** Add the depth particles back as well.

### Darker old lighting and a black lower sky  [Memory]
Before Beta 1.8 the lighting curve was harsher, and unlit caves were close to pitch black. The lower
half of the sky turned dark when you were below about sea level, so looking out of a ravine felt like
looking into nothing.

**Idea:** A lighting tweak with a steeper falloff at light levels 0–3, and a black lower sky
underground.

---

## 3. Suggested build order

1. **Presence sounds plus restored cave-sound behaviour.** One per-player scheduler, a 5–15 minute roll,
   and block-matched sounds everywhere. This is the core of the feature, and the other sound effects
   reuse it.
2. **The Follower and dropped footsteps.** These hang off the same scheduler.
3. **Phantom mob sounds through rock.**
4. **Ghost-block reappearance.**
5. **Void particles** and **darker lighting / black lower sky.** These go with the existing void fog.
7. **Far Lands terrain.** This is the biggest job, and needs the 26.3 debug code checked first.

Open question: should the presence sounds also fire in the Nether and End? Vanilla cave ambience
plays in the End but not the Nether.

---

## Sources

- [Minecraft Wiki: Ambience](https://minecraft.wiki/w/Ambience), cave sound history and both mood algorithms
- [PC Gamer: Minecraft fixes an 11-year-old bug](https://pcgamer.com/latest-minecraft-update-fixes-an-11-year-old-bug), MC-1133 footsteps
- [Minecraft Wiki: Client–server desync](https://minecraft.wiki/w/Client%E2%80%93server_desync), ghost blocks
- [Minecraft Wiki: Far Lands](https://minecraft.wiki/w/Far_Lands)