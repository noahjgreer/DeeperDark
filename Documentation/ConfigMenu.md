# Config menu

Typing `/dd` with no arguments opens an in-game settings screen for all of DeeperDark's config.
The `/dd ...` subcommands still work unchanged and stay the main tool for server consoles and
quick edits. The menu edits the same `config/deeperdark.yaml` on the server.

## What each player sees

- **Everyone:** *Player Sounds*: what you hear (hear player sounds on/off, volume) and your own
  sound profile (chat/death/join/hurt sound, pitch, pitch variation).
- **Operators** (permission level 2), additionally: every server category (Border, World, Mobs,
  Fortune, Explosions, Moss Growth, Combat, Leaf Decay, Item Magnets, The Creature, Creepiness), every
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
   rebroadcasts `VoidFogSyncPacket` / `AllIngredientsConsumableSyncPacket` if those changed.

Only diffs are sent, so two operators editing different settings at once don't overwrite each
other. Adding a player only creates the profile on the server once one of its fields changes.

Wire keys:

| Key                | Meaning                                         | Who may set it          |
| ------------------ | ----------------------------------------------- | ----------------------- |
| `KEY`              | a `ConfigOptions.CATEGORIES` server option      | operators               |
| `self.KEY`         | a `ConfigOptions.LISTENER` option for the sender | the sender              |
| `player.NAME.KEY`  | a `ConfigOptions.PROFILE` field of NAME's profile | NAME, or any operator  |

## Adding a setting

1. Add the field to `DeeperDarkConfig.ConfigInstance` (or a nested config).
2. Add one line to the right category in `config/ConfigOptions.java` with its range. The default
   comes from a freshly constructed config object automatically.
3. Add `deeperdark.config.option.KEY` and `KEY.desc` to `en_us.json`.

A new category also needs `deeperdark.config.category.ID` and an icon item.

The commands are separate hand-written trees (`DeeperDarkCommands`, `CreatureCommands`,
`CreepinessCommands`, `PlayerSoundsCommands`) and are **not** generated from `ConfigOptions`, so a
setting that should also be on the command line must be added there too. Keep ranges in sync.

## Known limits

- Faces use the skin from the tab list, so offline players show the default skin for their name.
- Profiles can be added in the menu but not deleted (use the YAML file).
- If a value already on disk is out of range, its row is red and Done stays disabled until it's
  fixed, even if the row is in a category you aren't looking at. Search for its key to find it.
- `unloadedActivity*` settings aren't in the menu: nothing in the mod reads them.
- Auto-updater settings aren't in the menu: they're read by each client from its own local config,
  so editing the server's copy would do nothing.
