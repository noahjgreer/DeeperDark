# Multi-version build

One source tree builds DeeperDark for several Minecraft versions, currently **26.1.2** and
**26.3**. The release is one jar that runs on any of them.

## How it works

1. **Stonecutter** (`settings.gradle`, `stonecutter.gradle`) creates one Gradle subproject per
   version under `versions/<ver>/`. Each one compiles the shared `src/` against its own
   Minecraft version with the shared `build.gradle`. Its output goes to
   `versions/<ver>/build/libs/deeperdark-<mod_version>+<ver>.jar`.
2. **The bundle** (`bundleJar` in `stonecutter.gradle`) nests every version's jar under
   `META-INF/jars/` of `build/libs/deeperdark-<mod_version>.jar`. The outer jar is a mod named
   `deeperdark_bundle`. Fabric Loader treats nested mods as optional (`IF_POSSIBLE`) and loads
   only one mod per id. It therefore loads the inner jar whose `"minecraft": "~<ver>"`
   dependency matches the game and skips the others. The outer mod depends on its own version of
   `deeperdark`. On an unsupported Minecraft version the game then stops with a clear
   missing-dependency error instead of loading nothing, and a leftover older deeperdark jar
   can't satisfy the dependency. Mod Menu shows the outer mod under `deeperdark`
   (`custom.modmenu.parent`).
3. `./gradlew build` builds every version and then the bundle. `buildGitRelease` uploads the
   bundle.

The inner jars get a `+<ver>` version suffix so their file names and mod versions differ. A
crash report shows which build loaded.

Runtime version checks with reflection were rejected because about 190 call sites differ, and
that approach gets no compile-time checking.

## Where version differences live

From smallest to largest change:

| Kind | Where | Example |
| --- | --- | --- |
| Same-shape rename | `replacements` in `stonecutter.gradle` | `EnderMan` → `Enderman`, `.hurtMarked` → `.syncVelocity` |
| Anything else in code | `//? if >=26.3 { … //?} else { … }` comments at the use site | Signature changes, removed methods |
| Whole classes for one version | `src/main/java-<ver>/` | Mixins whose target class is gone |
| Whole resources for one version | `src/main/resources-<ver>/` | Data files whose format changed, each version's `deeperdark.versioned.mixins.json` |
| Dependency versions and compat jars | `versions/<ver>/gradle.properties` | `fabric_version`, `compat_jars`, `dev_mod_jars` |

Things to watch for when using Stonecutter comments:

- The working copy shows the **active** version, which is set in `stonecutter.gradle`. Code for
  other versions is commented out. Run `Reset active project` (it sets 26.1.2) before
  committing.
- A `//` comment inside a block that is inactive on the active version must sit inside the
  `/* */`.
- Comments placed between `//? if` and `/*` get mangled.
- `javac` checks each version, but mixin targets are strings and javac can't check them. To
  check them, run each version with `-Dmixin.debug.verify=true -Dmixin.checks=true` and look for
  "could not find target" in the log.

## Data formats

26.3 changed many data formats, and it ignores unknown keys silently. For example, a loot table
in the 26.1.2 format still loads on 26.3 but loses all its conditions. Any data file whose
format differs therefore has a copy in each `resources-<ver>/`. The 26.3 copies were built from
vanilla 26.3 data (`ref-decompiled/Minecraft/26.3/assets/data`) with the mod's edits added.
Main differences:

- `feature` replaces `configured_feature`.
- `final_density` is its own file.
- Biome spawns are an attribute.
- Loot tables use `condition`/`modifier`.
- Block states use `{"id"}`.

## Adding a version

1. Add it to `versions` in `settings.gradle`.
2. Create `versions/<ver>/gradle.properties` by copying an existing one, then update the Fabric
   API version and the compat jars.
3. Add `src/main/resources-<ver>/deeperdark.versioned.mixins.json`, even if its list is empty.
4. Decompile the version into `ref-decompiled/Minecraft/<ver>`. See `DecompiledSources.md`.
5. Set it active, compile, and fix errors with the options in the table above. Then run the
   mixin check on it.
6. Add it to `VERS` in both `tools/*recipe*.py` scripts, rerun the QoL recipe generator and the
   conflict checker. See `QolRecipes.md`.

## Known limits

- The bundle is about twice the size of a single-version jar, because each inner jar carries
  its own copy of the assets.
- ChestTracker has no 26.3 build. Its plugin compiles against the 26.1.2 jar, and on 26.3 it
  never runs.
- The shears block transformer on 26.3 copies the vanilla axe rules. Logs that other mods
  register for stripping are missed.
