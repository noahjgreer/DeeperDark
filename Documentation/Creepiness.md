# Creepiness

Recreates the things that made Alpha/Beta-era Minecraft feel haunted: sounds of someone else nearby,
and blocks that come back after you mined them. The research behind it is in
`Ideas/OldMinecraftCreepiness.md`. Code: `net.noahsarch.deeperdark.creepiness`.

## How it works

`CreepinessManager` keeps one `HauntState` per online player. Every 6000–18000 ticks (5–15 minutes,
the pre-20w12a cave-sound interval) it picks a random enabled `Haunt` and plays it. A haunt returns
false when the surroundings don't suit it, for example a cave sound on the surface, and the manager
then tries the others in random order. If none fits, it tries again 200 ticks later. Nothing rolls
in the Nether, because vanilla never played cave ambience there. Spectators are skipped, and so is
anyone the Creature is chasing: the chase silences the world, and haunts would break that silence.

Every effect is sent to **one player only**, as a `ClientboundSoundPacket` or
`ClientboundBlockUpdatePacket` on that player's connection. A friend standing next to them hears and
sees nothing, so the player can't confirm it was real. Because it's all packets, it works without
the mod on the client, and the server world is never changed.

Multi-step effects (mining, footsteps, doors) queue their sounds in `HauntState` with the server
tick each one is due. Queued sounds and the follower are cut off when the player changes level or a
chase starts, so they never play at stale coordinates or break the chase's silence.

| Haunt | What the player gets |
| --- | --- |
| `cave_sounds` | The old cave-sound placement: a vanilla `ambient.cave` sound from an air block on the ±4 grid around the player, with sky light 0 and block light at most a random 0–8, at a random pitch of 0.8–1.0. Only fires in the dark, so underground, or anywhere in the End as in vanilla. |
| `mining` | 1–3 rounds of hits and a break from a full block with an exposed face 4–10 blocks away, using that block's own `SoundType`. Stone and logs are preferred over dirt, so a forest sounds like chopping and a cave like digging. |
| `footsteps` | 3–4 steps walking past, 7–11 blocks away, on whatever ground is there. |
| `doors` | A door, chest or barrel within 12 blocks opens and then closes. Only the sound plays; the block doesn't move. |
| `phantom_mobs` | A zombie, skeleton, spider or (rarely) creeper-fuse sound from inside a solid block beside or below the player, pitched down as if muffled. |
| `follower` | For up to a minute, the player's own footsteps are replayed along their path 10 ticks late, so 2–3 blocks behind them. Because it replays the path, it takes a step after the player stops. It ends after 6–14 steps. |
| `ghost_blocks` | The block the player most recently mined (within the last 5 minutes, 3–24 blocks away, still air) reappears on their client for 5–15 seconds. Mining or placing into it is corrected by vanilla's normal block-update replies. Blocks with block entities and non-occluding blocks are never recorded. |

## Commands and config

All under `/dd creepiness` and op only:

- `/dd creepiness` shows whether it's enabled and how many ticks until your next haunt.
- `/dd creepiness trigger <haunt> [player]` plays one haunt now, ignoring the timer and the toggles.
  It fails if nothing nearby suits that haunt.
- `/dd creepiness config <setting> [value]` covers `enabled`, `min_delay_ticks`, `max_delay_ticks` and
  one toggle per haunt key above.

The settings are stored in the `creepiness` section of `deeperdark.yaml`. A changed delay applies
from each player's next roll.

## Known limits

- Vanilla's own mood-based cave ambience still plays. The old-style cave sounds are added on top of
  it and don't replace it.
- Timers and the last mined block are kept in memory only. They reset on relog and server restart.
- The follower uses server-side positions, so it can sound slightly off when the player is lagging.
- Not built from the ideas list: dropped footsteps (this needs a client-side hook), darker
  lighting and a black lower sky, and the Far Lands. Void particles already exist in `VoidParticleHandler`.
