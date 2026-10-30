# Config menu

Typing `/dd` with no arguments opens an in-game settings screen for all of DeeperDark's config.
Every server setting in the menu is also a command, `/dd config <key> [value]` (and
`/dd config list`), for server consoles and quick edits. Both edit the same
`config/deeperdark.yaml` on the server.

## What each player sees

- **Everyone:** *Player Sounds*: what you hear (hear player sounds on/off, volume) and your own
  sound profile (chat/death/join/hurt sound, pitch, pitch variation).
- **Operators** (permission level 2), additionally: every server category (Border, World, Mobs,
  Fortune, Explosions, Moss Growth, Combat, Leaf Decay, Players, Items & Containers, Enchanting,
  Blocks & Plants, Movement, Item Magnets, The Creature, Creepiness), every
  player's sound profile (with their face), and **Add Player** to start a profile for any name,
  online or not.

The left sidebar picks a category (item icons). The search box filters across every category the
player can see, matching the setting's name, key, description, category, or a player's name. Each
row has a Reset button, and its tooltip shows the config key, a description, the default and the
allowed range. Invalid text turns red and disables Done. Sound fields have a ▶ button that plays
the sound locally at that profile's pitch.

## How it works

Modelled on vanilla 26.1.2's in-world game rules screen (`InWorldGameRulesScreen`):

1. `/dd` → `ConfigMenu.open` sends `ConfigMenuPayload(operator, values)`: a string map holding
   only what this player may edit. If the client lacks the mod (`ServerPlayNetworking.canSend`
   fails), the command prints a hint to use the subcommands instead.
2. The client opens `ConfigScreen`. Edits stay local until Done.
3. Done sends `ConfigChangesPayload` with only the changed values. `ConfigMenu.apply` re-checks
   each key against the sender's *current* permissions, parses each value with the same
   `ConfigOption.parse` the client used, applies what passes, logs what doesn't, saves once, and
   resyncs clients if a synced setting changed (below).

Only diffs are sent, so two operators editing different settings at once don't overwrite each
other. Adding a player only creates the profile on the server once one of its fields changes.

Wire keys:

| Key                | Meaning                                         | Who may set it          |
| ------------------ | ----------------------------------------------- | ----------------------- |
| `KEY`              | a `ConfigOptions.CATEGORIES` server option      | operators               |
| `self.KEY`         | a `ConfigOptions.LISTENER` option for the sender | the sender              |
| `player.NAME.KEY`  | a `ConfigOptions.PROFILE` field of NAME's profile | NAME, or any operator  |

## Feature toggles

Nearly every DeeperDark gameplay tweak has a boolean toggle (default `true` = DeeperDark behaviour,
`false` = vanilla), checked at the tweak's own mixin or event handler. Not toggleable, on purpose:

- **Registered content** (blocks, items, recipes, gunpowder trails, stackable buckets, rotten
  flesh food, Warden knockback): fixed at startup, or the item would be useless without it.
- **Worldgen** (deep dark depth, Black Mesa, pale mansions, lava sea level, structure limits): it
  would only affect new chunks. `compact_tree_growth` is the exception: it's a sapling feature, so
  it also changes trees in newly generated chunks.
- **Infrastructure** (slot/menu fixes, accessors, chat length, structure block limits) and collar
  trinkets, which are part of the collar item.

Toggling `ladder_tweaks` off lets ladder chains with no wall behind them break on their next
block update. Toggling `player_riding` off dismounts current player riders. `armor_stand_arms`
forces `showArms()` but saves each stand's own flag, so turning it off brings back vanilla for
stands placed without arms. Stands saved before this setting existed stored `ShowArms:true` and
keep their arms. `podzol_spreading` is
checked in `randomTick`, since vanilla caches `isRandomlyTicking` per block state at startup.

## Client sync

Settings that client-side code also reads (rendering, movement, placement and interaction
prediction) are listed in `ConfigOptions.SYNCED`. `ConfigSync` sends all of them in one
`ConfigSyncPayload` on join, after a menu or command change to any of them, and after
`/dd reload`. The client writes them into its in-memory config and rebuilds the ingredient set.
When it leaves a remote server it reloads its own config file, so the server's values don't carry
over (singleplayer shares the integrated server's config object, so it skips this).

Add a key to `SYNCED` when the feature's code runs on the client too. Otherwise a server with it off
and a client with it on would mispredict: rubber-banding, ghost blocks or ghost items.

## Adding a setting

1. Add the field to `DeeperDarkConfig.ConfigInstance` (or a nested config).
2. Add one line to the right category in `config/ConfigOptions.java` with its range. The default
   comes from a freshly constructed config object automatically.
3. Add `deeperdark.config.option.KEY` and `KEY.desc` to `en_us.json`.
4. If client-side code reads it, add the key to `ConfigOptions.SYNCED`.

A new category also needs `deeperdark.config.category.ID` and an icon item.

`/dd config <key> [value]` is generated from `ConfigOptions.SERVER` (`DeeperDarkCommands.configCommand`),
with Brigadier argument ranges taken from the option, so new server settings need no command code.
`/dd border config` reuses the same generated nodes. The `/dd creature`, `/dd creepiness` and
`/dd player_sounds` trees are still hand-written (`CreatureCommands`, `CreepinessCommands`,
`PlayerSoundsCommands`), so creature and creepiness settings can be set either there or through
`/dd config`. Keep their ranges in sync with `ConfigOptions`.

## Known limits

- Faces use the skin from the tab list, so offline players show the default skin for their name.
- Profiles can be added in the menu but not deleted (use the YAML file).
- If a value already on disk is out of range, its row is red and Done stays disabled until it's
  fixed, even if the row is in a category you aren't looking at. Search for its key to find it.
- `unloadedActivity*` settings aren't in the menu: nothing in the mod reads them.
- Auto-updater settings aren't in the menu: they're read by each client from its own local config,
  so editing the server's copy would do nothing.
