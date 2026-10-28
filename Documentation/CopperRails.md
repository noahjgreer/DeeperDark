# Copper Rails

`deeperdark:copper_rail` is a plain `RailBlock` (vanilla rail properties, copper sounds) that slows
minecarts. Recipe: vanilla rail's shape with copper ingots, 16 rails. It is in `#minecraft:rails`
(block and item), which vanilla uses to decide that a minecart is on track at all.

## Drag

`CopperRailDrag` multiplies horizontal speed by 0.995 per tick while the cart's current rail is
copper, on top of vanilla's natural slowdown (0.997 ridden). A ridden cart coasts a bit over a
third as far as on iron rails. Powered rails still boost carts to the normal top speed.

The drag wraps the `applyNaturalSlowdown` call in both `OldMinecartBehavior.moveAlongTrack` and
`NewMinecartBehavior.calculateTrackSpeed` (experimental minecart movement), one mixin each.
Changing `AbstractMinecart.applyNaturalSlowdown` itself would miss chest and hopper carts,
because `AbstractMinecartContainer` overrides it without calling super.

## Known limits

- Copper rails don't oxidize or wax.
