# Supporting 26.1.2 and 26.3 from one jar

Plan for shipping one DeeperDark jar that runs on both Minecraft **26.1.2** and **26.3**. It covers
the approach, the build changes, and every compile and mixin failure found by a trial compile of
the current source against 26.3. 26.2 is not a target, but its breaking changes also apply because
26.3 comes after it.

## Facts this plan rests on

| | 26.1.2 (current) | 26.3 |
| --- | --- | --- |
| Released | 2026-03 | 2026-09-15 |
| Java | 25 | 25 |
| Obfuscated | No | No (no mappings published) |
| Fabric Loader | 0.19.2 in use | 0.19.5+ (0.20.1 latest) |
| Fabric API | 0.146.1+26.1.2 in use (0.155.3 latest) | 0.164.0+26.3 latest |
| Loom | 1.16-SNAPSHOT, old `fabric-loom` id | 1.17+, `net.fabricmc.fabric-loom` id only |
| Gradle | 9.4.0 | 9.6.0 |
| Data pack format | 101.1 | 121.0 |
| Resource pack format | 84.0 | 97.1 |

Decompiled 26.3 sources are in `ref-decompiled/Minecraft/26.3` (see `DecompiledSources.md`).

## Approach: two builds inside one jar

A single set of bytecode that adapts at runtime would need reflection or runtime-selected
mixins at about 190 call sites, including classes that extend vanilla classes whose abstract
methods changed (for example `SlipChunkGenerator`). That code would be hard to read and would get
no compile-time checking.

Instead:

1. **One source tree, compiled twice.** [Stonecutter](https://stonecutter.kikugie.dev/) (0.9.8)
   compiles the same `src/` once against 26.1.2 and once against 26.3. Where the versions differ,
   the code uses comment conditionals that Stonecutter switches on or off per version:

   ```java
   //? if >=26.3 {
   import net.minecraft.world.entity.monster.Enderman;
   //?} else
   /*import net.minecraft.world.entity.monster.EnderMan;*/
   ```

   This sketch is untested; check the exact syntax in the Stonecutter docs. Each build is checked
   by `javac` against its real Minecraft version.
2. **Bundle both builds as nested jars.** A small outer jar holds both builds under
   `META-INF/jars/`:

   ```text
   deeperdark-<ver>.jar                 (outer, mod id "deeperdark_bundle")
   ├── fabric.mod.json                  jars: [both inner jars], depends: { "deeperdark": "*" }
   └── META-INF/jars/
       ├── deeperdark-<ver>+26.1.2.jar  (mod id "deeperdark", depends minecraft ~26.1.2)
       └── deeperdark-<ver>+26.3.jar    (mod id "deeperdark", depends minecraft ~26.3)
   ```

   Fabric Loader picks the right inner jar by itself. Nested mods default to load condition
   `IF_POSSIBLE` (see `ModLoadCondition` and `ModSolver` in the loader sources), and only one
   candidate per mod id can load, so the solver loads the inner jar whose `minecraft` dependency
   is satisfied and skips the other. The outer jar's dependency on `deeperdark` turns "no inner
   jar fits" (for example on 26.2) into a clear error instead of a silent no-op.

Costs:

- Jar size roughly doubles. The jar is about 12.8 MB today, mostly assets. Shared `assets/` could
  move to the outer jar, because every mod jar is loaded as a resource pack, but that is optional.
- Mod Menu lists two entries. Hide the outer one with
  `"custom": { "modmenu": { "parent": "deeperdark" } }`.
- Players can't carry a world between versions. Vanilla can't load a 26.3 world in 26.1.2, so
  this approach adds no new limitation.

Rejected alternatives:

- **Runtime version checks plus reflection.** Rejected for the reasons in the first paragraph.
- **Pack overlays for data differences.** Fabric's `ModNioPackResources.createOverlay` does
  support overlays, but each inner jar is built for one version anyway, so a per-version resource
  directory is simpler.
- **Two separate downloads.** This is the fallback if bundling causes trouble. It is the same
  Stonecutter build without the bundling task.

## Steps

Each step should build, and each should be reviewed and committed separately.

### 1. Modernize the build on 26.1.2 alone

The new Loom plugin supports 26.1.2, so this step needs no version split. It also removes the
build workarounds for unobfuscated Minecraft.

- Switch `id 'fabric-loom'` to `id 'net.fabricmc.fabric-loom'`, use Loom 1.17.x or newer, and set
  Gradle to 9.6.0 in `gradle-wrapper.properties`.
- Delete the `mappings loom.layered { … }` line and `mappings.tiny`. The new plugin uses no
  mappings.
- Change `modImplementation` to `implementation`, and other `mod*` configurations to their plain
  equivalents.
- Delete the `generateDLIConfig` launch.cfg patch. It only existed to work around the empty-tiny
  hack.
- Change the `seasons.accesswidener` header from `named` to `official`. The trial build failed
  with "Expected official namespace" until this was done.
- Change `remapJar` to `jar` in `buildGitRelease`.
- Re-check `installDevMods`. Its namespace patching exists for the remapper, so it may become
  unnecessary. ChestTracker and jackfredlib still can't run in dev, because they use intermediary
  names.
- Bump Loader and Fabric API to the latest 26.1.2 builds, then build and play-test.

### 2. Introduce Stonecutter with only 26.1.2

- Add the Stonecutter settings plugin (repo `https://maven.kikugie.dev/releases`) with one
  version node, `26.1.2`.
- Move version numbers into per-version properties: `minecraft_version`, `fabric_version`, the
  `minecraft` dependency range in `fabric.mod.json`, and the compat `libs/` jar paths.
- Add a per-version resource directory, for example `src/main/resources-26.1.2/`, through
  `sourceSets.main.resources.srcDir`. It holds data files whose format changed and any
  version-only mixin config.
- The output jar should match what step 1 produced.

### 3. Add the 26.3 node and fix every failure

Add the `26.3` node (Fabric API `0.164.0+26.3`, Loader `>=0.19.5`) and work through the inventory
below. For each fix, decide between three options:

- **Conditional code.** Use this for renames and signature changes. It is the usual case.
- **Version-only class plus mixin config.** Use this when a whole mixin target is gone, such as
  `AxeItem`. List those mixins in a per-version mixins JSON in the version resource directory.
- **Data file in the version resource directory.** Use this when vanilla made the feature
  data-driven.

Compile errors can't catch mixin injection targets, because they are strings. Run both versions
with `-Dmixin.debug.verify=true -Dmixin.checks=true` and fix every "could not find target" in
`run/logs`.

### 4. Bundle task

Add a Gradle task, run by `build`, that takes both inner jars and writes the outer jar with its
own `fabric.mod.json`. The outer `fabric.mod.json` sets `"id": "deeperdark_bundle"`, lists both
inner jars under `"jars"`, depends on `"deeperdark": "*"`, and sets the Mod Menu parent. Point
`buildGitRelease` at the bundled jar.

### 5. Verify on both versions

Put the same bundled jar in a 26.1.2 instance and in a 26.3 instance, client and dedicated server.
Each log should show exactly one `deeperdark` loaded, at the right version. Then smoke-test the
systems touched in step 3: brewing, composting, stripping, the logo and updater screens, key
binds, worldgen in the Slip, mansions, and seasons.

### 6. Docs

Add a `Documentation/MultiVersion.md` system doc that covers how the build works and how to add a
version. Add it to the table in `CLAUDE.md`, and update `CLAUDE.md`'s first line, which says the
mod targets 26.1.2 only.

## Inventory of 26.3 failures

These come from a trial compile of the current `main` against 26.3: 186 compile errors in 66
files, plus 6 mixin targets that no longer exist. The trial ran in a scratch copy, and nothing in
the repo changed.

### Fabric API removals (26.3)

| Removed | Our use | Replacement |
| --- | --- | --- |
| `FabricPotionBrewingBuilder` | `potion/CustomBrewingRecipeHandler` | Data-driven brewing recipes in `data/<ns>/recipe/` (26.3 only) |
| `ComposterBlock.COMPOSTABLES` (vanilla map) | `Deeperdark` | `DataComponents.COMPOSTABLE` via `DefaultItemComponentEvents.MODIFY` |
| `AxeItem` (stripping moved to block transformers) | `AxeItemMixin`, `AxeItemAccessor` | `block_transformer` data plus `DataComponents.BLOCK_TRANSFORMER`, or `BlockTransformerEvents.MODIFY` / `BlockTransformerHelper` |

### Rendering: `com.mojang.blaze3d.pipeline` moved to `com.mojang.renderpearl.api.pipeline`

`RenderPipeline`, `BlendFunction`, `ColorTargetState`, and the `SourceFactor`/`DestFactor`
classes moved. Used in `intro/DeeperDarkLogoScreen` (58 errors) and `autoupdate/AutoUpdaterScreen`.
Check the new pipeline builder API in the 26.3 sources. It is more than a package rename.

Also: `PoseStack.mulPose(Quaternionf)` was removed and now takes `Matrix4fc` or `Transformation`
(`CreatureEntityRenderer`, `PrimedDynamiteRenderer`, `VaultBlockEntityRenderer`,
`VaultItemSpecialRenderer`). `AbstractEndPortalRenderer.submitSpecial` changed signature (the
Vault renderers). `GameRenderer.getMainCamera()` changed (`VoidFogDHCompatMixin`).

### Input: GLFW replaced by SDL (26.3)

`org.lwjgl.glfw` is gone. Use `InputConstants`, whose mouse button values changed. Affected:
`client/ContainerItemKeyHandler` (`InputConstants.Type.KEYSYM`), `intro/DeeperDarkLogoScreen`,
`mixin/ContainerScreenOpenMixin`, and `seasons/mixin/ItemMixin` (`InputConstants.isKeyDown`
signature). Any custom text-input widget must call
`Minecraft#onTextInputFocusChange(GuiEventListener, boolean)`. None was found, but check again
after porting.

### GUI moved out of `Minecraft` (26.2)

`Minecraft.setScreen(...)` and `Minecraft.screen` are now on `minecraft.gui` (`Gui.setScreen`).
Affected: `DeeperDarkClient`, `AutoUpdaterScreen`, `ContainerScreenOpenMixin`,
`MinecraftMusicMixin`. The mixin targets `Gui.extractCameraOverlays` (`GuiQuicksandMixin`) and
`Gui.HeartType.forPlayer` (`GuiHeartTypeQuicksandMixin`) no longer resolve.

### Registry constants split into `*Ids` / `*Types` classes (26.2)

- `EntityType.PLAYER`, `ITEM_DISPLAY`, `CREEPER`, `LIGHTNING_BOLT`, and `VILLAGER` are now on
  `EntityTypes` (keys on `EntityTypeIds`). Affected: `DeeperDarkDynamicLightsInit`,
  `EntityRidingMixin`, `FishingBobberEntityMixin`, `WitchEntityMixin`, `CustomBlockManager`,
  `CustomBlockTracker`.
- The copper `Blocks.*_COPPER` constants were replaced by `WeatheringCopperCollection<Block>`
  fields (`NoteBlockMixin`, 32 errors).
- Block codecs and the `block_type` registry were removed (26.3). Every `codec()` override with
  `simpleCodec(X::new)` must go, in `BoxBlock`, `CollarBenchBlock`, `DynamiteBlock`,
  `GunpowderTrailBlock`, `IgneousSpongeBlock`, `MilkCauldronBlock`, `QuicksandBlock`,
  `SiphonBlock`, and `VaultBlock`. That method only exists on 26.1.2, so wrap it in a conditional.

### Class and member renames

| 26.1.2 | 26.3 | Files |
| --- | --- | --- |
| `EnderMan` | `Enderman` | `CollarEvents`, `EndermanPickUpBlockMixin` |
| `RedStoneWireBlock` | `RedstoneWireBlock` | `CollarEvents`, `RedStoneWireBlockMixin` |
| `Entity.hurtMarked` | `Entity.syncVelocity` (`needsSync` is an older, different flag) | `CollarEvents`, `ItemEntityMixin`, `ItemMagnetHandler`, `PlayerLeashHandler`, `WorldBorderHandler` |
| `advancements.CriteriaTriggers` | `advancements.triggers.CriteriaTriggers` | `MilkBucketPlacementMixin`, `ShearsItemMixin` |
| `advancements.criterion.ItemPredicate` | `advancements.predicates.ItemPredicate` | `MatchToolLootConditionMixin` |
| `PushReaction.NORMAL/DESTROY/BLOCK/PUSH_ONLY/IGNORE` | `PUSH_PULL/POPPED/IMMOVEABLE/PUSH/IGNORE_ENTITY` (match by meaning) | `ModBlocks` |

### Worldgen: configured features merged into `Feature` (26.3)

`ConfiguredFeature`, `FeaturePlaceContext`, the `feature.configurations` package, and
`Registries.CONFIGURED_FEATURE` are gone. Affected: `seasons/mixin/FreezeTopLayerFeatureMixin`,
`TreeFeatureMixin`, `SaplingBlockMixin`. `StructureProcessorType` is no longer generic and
`StructureProcessor` became an interface (`PaleMansionProcessor`, `WoodlandMansionStructureMixin`).
`SlipChunkGenerator` must implement the new abstract `addDebugScreenInfo` and lost
`MinecraftServer.getStructureManager()`. Also changed: `RandomState.sampler()` and
`Noise.getValue(double,double,boolean)` (`BiomeMixin`).

Data files: `data/*/worldgen/configured_feature/` becomes `worldgen/feature/` with the `config`
wrapper removed, and some placement modifiers were renamed (see the
[26.3 changelog](https://minecraft.wiki/w/Java_Edition_26.3)). Our `deeperdark` and `minecraft`
`configured_feature` and `placed_feature` files need 26.3 copies in the version resource
directory. Also re-check `noise_settings` (surface rules became material rules), `biome`, and
`structure` JSON against the vanilla 26.3 data in `ref-decompiled/Minecraft/26.3/assets/data`.

### Other signature changes (one or two sites each)

`Inventory.placeItemBackInInventory` gained a `Prediction` argument (`CollarBenchMenu`).
`LivingEntity.drop(ItemStack, boolean)` changed (`CraftingResultSlotMixin`, `InventoryMenuMixin`,
`PlayerLeashHandler`, `DeeperDarkCommands`). `Level.playSound` overloads changed (`AxeItemMixin`,
`ShearsItemMixin`). `LootContext.getParameter` changed (`ApplyBonusLootFunctionOreDropsMixin`,
`MatchToolLootConditionMixin`). `DataComponentPatch.entrySet()` changed (`ComponentIngredient`,
`ComponentShapelessRecipe`). `ItemContainerContents.allItemsCopyStream()` changed (`Deeperdark`,
`PickBlockContainerMixin`). `BlockPos.getCenter()` changed (`CreatureManager`). Other changes:
`BlockPos.withinManhattan` (`IceBlockMixin`), `BonemealableBlock.performBonemeal`
(`FertilizableUtil`), `LightEngine.getLightBlockInto` (`PodzolBlockMixin`),
`MobEffect.isInstantenous` (`ModVillagers`), `ClockManager.getTotalTicks` (`FabricSeasons`,
`SeasonCommand`), `LevelRenderer.setSectionRangeDirty` (`FabricSeasonsClient`), and
`LevelRenderer.allChanged` (`WorldRendererMixin`, mixin target). The
`ServerboundChatPacket.write` mixin target also no longer resolves (`ServerboundChatPacketMixin`).

### Compat jars in `libs/`

Each version node needs its own compile-only jars. Find 26.3 builds of LambDynamicLights,
ChestTracker plus jackfredlib, and Stack to Nearby Chests. If a mod has no 26.3 build, drop that
compat on 26.3: put its entrypoint and mixin config in the 26.1.2-only `fabric.mod.json` and
mixin JSON. The Distant Horizons compat mixin (`VoidFogDHCompatMixin`) needs the same check.

## Sources

- [Fabric for Minecraft 26.3](https://fabricmc.net/2026/09/15/263.html)
- [Fabric for Minecraft 26.2](https://fabricmc.net/2026/06/15/262.html)
- [Fabric for Minecraft 26.1](https://fabricmc.net/2026/03/14/261.html) (new Loom plugin, no mappings)
- [Minecraft Wiki: Java Edition 26.3](https://minecraft.wiki/w/Java_Edition_26.3)
- [Stonecutter](https://stonecutter.kikugie.dev/)
- Fabric Loader 0.19.2 sources: `ModSolver`, `ModLoadCondition` (nested jar selection)
