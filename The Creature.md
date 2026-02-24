# Overview
The creature is an entity that appears only within caves by default. The creature exhibits standoffish and shy behaviors, intended to invoke uncomfortability to the player. 
# Appearance
## Filetypes
The creature comes in four different variants. They are found in the mod's assets under these filenames:

1. `assets\minecraft\textures\entity\creature0.png`

2. `assets\minecraft\textures\entity\creature1.png`

3. `assets\minecraft\textures\entity\creature2.png`

4. `assets\minecraft\textures\entity\creature3.png`

Regardless of the file chosen, the png will always be in a 1:2 ratio, (usually at a resolution of 512 x 1024).

## Entity
The creature entity appears as a billboard, (no pitch, only yaw.) The creature will be 3 blocks in height, so scaled 1 + 1/3 it's default item model height, which is a 1 x 2 block or 16 x 32 pixel model. 

# Behavior
## Spawning
The creature only spawns in caves by default (below y=`52`). 

### Validity Test
Every `600` ticks, the game rolls a percentage, by default `0.5%`. Should it match, it will then pick a random player within the world. If the player's current y level is at or below the default of `52`, then the creature's begin spawning function will begin. If the player chosen is not at or below `52`, it will proceed to the next player. If none of the players on the server are below y=`52`, then the creature will wait another `600` ticks before rerunning the spawn validity test again.

### Begin Spawning
In the event that the validity test is passed, the creature will then run through a pathtracing algorithm starting from the player who passed the test should the pathtracing algorithm fail, the fallback radial algorithm will be used.

#### Creature Preferences (Criteria)
- Never stands out in the open.
- Never goes above y=`52`.
- Favors hiding behind corners or concealing blocks.
- Prefers spaces of `3` blocks in height, but never lower.
- Cannot be within `20` blocks of another creature entity.

#### Creature Pathtracing Algorithm
The pathtracing will crawl the caves or whatever enclosed corridors / space the player is in. The crawl will continue at least until it has reached the minimum crawl distance (`60` blocks by default). At which point, if it is able to keep crawling, it will do so until it either reaches a dead end, or hits the maximum distance of `120` blocks. When the minimum and maximum pathtracing points have been found, the creature will then be placed according it's preferences. The creature's overall position will be randomly selected between the `60` and `120` crawl range, but will favor areas where its preferable criteria are met. 

#### Creature Radial Placement Algorithm
Should the pathtracing algorithm not pass the test, the creature will use a fallback radial placement algorithm. Reasons the pathracing algorithm may not pass include:

- The player's space being too compact for a valid placement. (Doesn't meet the min crawl range)
- The height of the pathtraced environment being too short to meet the creature's preferences.
- Valid pathtracing distances exceed the maximum Y. 

The radial placement algorithm disregards path tracing from the player, and instead casts a radial sphere searching for nearby cave systems. The search range for the sphere must be within the crawl distance ranges. Should caves or free space be found that meet the creature's preferences, it will assess all valid locations within the radial results and pick one at random. 

In the event that the creature's placement fails both algorithms, it will be logged in the console for further inspection at a later time. Proceeding this, the creature will go back into it's validity test cycle.

## Idleing
There will likely be some time where the creature will be "waiting" for a player to get within range. Here's what it does during that time. 

### Audio
In the event that the player enters within the minimum crawl distance radius from the creature. (e.g. `60` blocks of the creature), the creature will emit the `ambience` sound. This sound will originate from the creature, and have a volume that perfectly encompasses the distance between the creature and its minimum pathtracing range. This audio will not loop unless optionally configured to do so. 

### Copper Nugget Trail
If the player is within `2/3` of the pathfinding radius to the creature (e.g. `40` blocks), then the game will pathfind a path from the creature to the player. This path cannot go through blocks, and is similar to the pathfinding algorithm used for spawning the creature. In the event that a reachable path cannot be traced successfully between the player and the creature, then the "Copper Nugget Trail" sequence will not proceed until it does.

The path will dynamically update according to the player's movement, until the following conditions are met:
- No players are looking at the path of the pending trail position

The copper nugget trail sequence is only run once per entity. Multiple players cannot activate a copper trail for the same entity. 

#### Sequence
When the conditions are met, the game will then spawn a trail of copper nuggets, each one separated by `3` blocks along the path that was drawn between the player and the creature. The nuggets will begin at the player, and will go to a configurable distance to the creature. `0` would be no distance, and the player would recieve a single nugget. `1` would be the full length, wherein the trail would lead all the way to the creature. By default, this is `0.7`, meaning that the nuggets would cover `70%` of the way to the creature, and then stop.


### Shaking
When the creature is not in attack mode, it will stand still in a cave, waiting for a player to approach it, or look at it. The creature will generally stand still.

In the event that the creature is within the line of sign of the player, and the player's view of the creature is not obstructed by blocks, then depending on how close the player's crosshair is casted to the creature's origin, the creature will shake/jitter accordingly, with direct eye-contact with the creature leading to intense shaking, moving the creature's billboard display across a `2` square block radius. 

If the creature is on the player's screen, but the player has not directly looked at the creature itself, the creature will only jitter mildly, the jitter increasing linearly depending on cursor distance to on screen creature origin. 

## Interaction
In the event that a player makes direct visual contact with the creature, or comes within `5` blocks of the creature, one of the following behaviors will be triggered.

### Default Behavior
The creature will immediately disappear.

After disappearing, the player will recieve the following status effects:
- `5` seconds of `Darkness`
- `2` seconds of `Nausea`
- `30` seconds of `Weakness`

### Chase Behavior
The chase behavior is an alternate behavior that occurs `40%` of the time. 

#### Preparation Sequence
When the chase behavior occurs, the following actions are triggered:
- The player will freeze positionally with their gaze locked at the origin of the creature. 
- Any jitter present from the creature will logarithmically fall off to 0, in a matter of `1` second.
- The `hush` sound will play.
- The player will recieve heavy blindness that lasts for `3` seconds.

#### Chase Sequence
At the end of the preparation sequence, the player will recieve darkness for 30 seconds. The creature will disappear from its original position. During this time, the Pathfinding Algorithm will search for any valid position within a `50` to `80` block chase range and move the creature to there. The creature will then persue the player with adaptive pathfinding, (adapting to updated player positions through movement). The creature will pursue the specific player that looked at it at a rate of `5` blocks per second. 

Rapid, high pitched footstep sounds will play at a loud volume from the current position of the creature. The footstep sounds will be dependant on whatever block is below the creature during playtime. 

During the chase sequence, the player's view will be nudged down continuously, wherein the pitch of the player's viewing angle is reduced by a `0.5` degrees every tick. This will force the player to run while looking at the floor.

If the player is able to evade the creature for 30 seconds, the following will occur:
- The creature will despawn, and in it's final position, a single diamond will be dropped

In the event that the player is caught, there is a 50/50 chance of either of the two happening:
- The player dies, and the chat says: `PLAYERNAME was slain by something` (present in the language file).
- The player is teleported to their spawnpoint. (e.g. their bed, or world spawn as fallback)

Regardless of the oucome, the following two events will happen upon either condition being met (being caught or escaping.):
- The server will send a stopsound packet for both the `hush` and `ambient` creature sounds.
- The player will be cleared of all status effects, and the player view pitch influence will cease.

## Side Effects 
Below are some additional side effects that have the possibility of occuring following the interaction sequence. 

### Echo
This side effect has a `100%` chance of happening by default.

Following the creature's disappearance, if a player goes within `1.5` blocks of the position where the creature once was, they will hear the `ambience` noise again, but no other effect will happen. 

After 30 seconds, this trigger zone will automatically be removed. If the trigger zone is triggered by a player, then it will be removed then as well.

### Torch Removal
This side effect has a `40%` chance of happening by default.

Following the creature's disappearance, the game scan a 20 cubic block region, centered at the origin of the creature, and replace all of the torches within that region with air. (Similar to using the fill destroy command, during this sequence, the torches will be "broken" or "dropped" where they were once placed.)

### Projectile Rejection
This side effect has a `100%` chance of happening by default.

This side effect is not related to the creature's disappearance, but the creatures presence. If a player attempts to shoot a projectile of any kind at the creature (arrow, snowball, etc.), the creature will destroy the projectile immediately upon impact with itself, and then after a `0.2` second wait, the creature will shoot a projectile of the same type at the player completely accurately.

## Despawning
If the creature does not enter into attack mode, then it will despawn after a configurable `12000` ticks.

# Audio
The audio files for the creature are stored in the following directories for their respective identifiers.
## `ambience`
- `assets\deeperdark\sounds\ambient\creature\ambience0.ogg`
- `assets\deeperdark\sounds\ambient\creature\ambience1.ogg`
- `assets\deeperdark\sounds\ambient\creature\ambience2.ogg`
- `assets\deeperdark\sounds\ambient\creature\ambience3.ogg`
- `assets\deeperdark\sounds\ambient\creature\ambience4.ogg`
- `assets\deeperdark\sounds\ambient\creature\ambience5.ogg`
- `assets\deeperdark\sounds\ambient\creature\ambience6.ogg`

## `hush`
- `assets\deeperdark\sounds\entity\creature\hush\hush0.ogg`

# Configuration
All functions of the creature system will be configurable through the Deeper Dark Configuration system, utilizing the synced yaml and command interaction system. Here are the notable configurable commands for the creature: 
```
- dd creature
| - config
| | - pathfinding_min_dist [60]
| | - pathfinding_max_dist [120]
| | - pathfinding_max_y [52]
| | - validity_frequency [600]
| | - validity_roll [0.005] (0.0 - 1.0)
| | - entity_spacing [20]
| | - trail_separation [3]
| | - trail_reach [0.7]
| | - jitter_max [2]
| | - player_distance_tolerance [5]
| | - chase_frequency [0.4]
| | - chase_path_min_dist [50]
| | - chase_path_max_dist [80]
| | - movement_speed [5]
| | - evasion_timer [600 ticks]
| | - death_frequency [0.5]
| | - echo_chance [1.0]
| | - echo_trigger_radius [1.5]
| | - torch_removal_chance [0.4]
| | - projectile_rejection_chance [1.0]
| | - projectile_rejection_delay [4 ticks]
| | - despawn_delay [12000]
| - clear (Removes all creatures in the world)
| - list (Lists all creatures and their behaviors)
|   [Creature UUID]
|   [Creature XYZ] | Sound: [Sound Played Status] | Trail: [Trail Status] | Chase: [Will Creature Chase?] | Torch Remove: [Will Creature Remove Torches?] | Echo: [Will Creature Echo?] | Projectile: [Will Creature Reject Projectiles?] | Despawn: [Ticks Until Creature Despawn] | Current Sequence: [Creature's Current Sequence] | Targeted Player: [Playername or Pending]
| - summon (X, Y, Z)
| - spawn [PlayerName] [Optional Max Distance Override]
```

# To Be Organized
- [x] Player gets blindness when it disappears or when they get closer to it and they also get weakness.
- [ ] Encounters with the creature that are not separated by deaths lead to more intense effects.
- [ ] Encoutners with the creature increase the regional difficulty.
- [x] Nausea during encounter