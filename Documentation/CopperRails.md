# Copper Rails

Rails made from copper ingots (vanilla rail's shape, 16 per craft) with copper sounds. Like
vanilla copper they oxidize over time (copper → exposed → weathered → oxidized), can be waxed
with honeycomb (in world or by crafting), and scraped back with an axe or by lightning. The more
oxidized a rail, the more it slows minecarts.

## Blocks

- `CopperRailBlock` is a `RailBlock` that knows its `WeatherState`. The four waxed rails use it
  directly so they never age.
- `WeatheringCopperRailBlock` adds `WeatheringCopper` and the random tick, mirroring vanilla's
  `WeatheringCopperChainBlock`.
- The stage chain and wax pairs are registered with Fabric's `OxidizableBlocksRegistry`
  (`registerNextStage` / `registerWaxable`), which vanilla's honeycomb, axe and lightning code
  read. Both methods exist on both supported versions; the collection-based
  `registerWeatheringCopperBlocks` differs between them, so it isn't used.
- Waxed rails reuse their unwaxed stage's block and item models.
- All eight are in `#minecraft:rails` (block and item), which vanilla uses to decide that a
  minecart is on track at all.

## Drag

`CopperRailBlock.applyDrag` multiplies horizontal speed per tick, on top of vanilla's natural
slowdown (0.997 for a ridden cart), while the cart's current rail is a copper rail:

| Stage      | Factor | Ridden coasting distance vs. iron rails |
| ---------- | ------ | --------------------------------------- |
| Copper     | 1.0    | same                                    |
| Exposed    | 0.995  | ~38%                                    |
| Weathered  | 0.985  | ~17%                                    |
| Oxidized   | 0.96   | ~7%                                     |

Waxing keeps the stage's drag. Powered rails still boost carts to the normal top speed.

The drag wraps the `applyNaturalSlowdown` call in both `OldMinecartBehavior.moveAlongTrack` and
`NewMinecartBehavior.calculateTrackSpeed` (experimental minecart movement), one mixin each.
Changing `AbstractMinecart.applyNaturalSlowdown` itself would miss chest and hopper carts,
because `AbstractMinecartContainer` overrides it without calling super.

## Known limits

- Waxing or scraping a rail doesn't count toward vanilla's Wax On / Wax Off advancements, whose
  block lists are hardcoded per version.
