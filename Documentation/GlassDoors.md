# Glass doors

`glass_door` and the 16 `<color>_stained_glass_door`s are `GlassDoorBlock`s (a `DoorBlock`
subclass) with their own `BlockSetType` that has glass sounds and can't be opened by wind charges.
Their assets come from `tools/generate_glass_door_assets.py` (blockstates, block/item models, item
definitions, recipes). Loot tables live in each `resources-<ver>/` and are maintained by hand,
because their format differs between versions.

## Centered in pane walls

A glass door gets the `centered=true` state when a pane sits in line with it. That means an
`IronBarsBlock` (any glass pane, iron bars) directly above the upper half, or beside either half
on either side along the door's width. It also counts if the neighbour is the other leaf of a double door and the pane is on that
leaf's far side. The check runs on placement and in `updateShape`, so the door follows panes that
are added or removed later. The double-door case looks through the other leaf instead of reading
its `centered` state. Reading the state would let two leaves keep each other centered after the
panes were gone.

- **Closed:** the door is the vanilla door moved 6.5px toward `FACING`, so it sits 3px thick in
  the middle of the block (6.5 to 9.5). The 2px pane (7 to 9) runs straight into it.
- **Open:** the same 6.5px shift. The door swings about its centre-line hinge and reaches half a
  block into the space in front. The shape goes outside the block, which works for collision
  because `BlockCollisions` checks neighbours with large shapes. The outline and hit box past
  the block edge only respond when the ray also passes through the door's own block.
- **Models:** `template_centered_door_*` are the vanilla `door_*` models with the element moved.
  They were built once from `ref-decompiled`, and they are not generated. The open ones have no
  cullfaces, because they reach into neighbouring blocks. The blockstate uses the same `y`
  rotations as uncentered doors.

Known limits:

- A row of three or more glass doors only centers the doors within two blocks of a pane.
- Doors placed before this feature existed stay uncentered until a block beside them changes.
- Nothing stops a block being placed in the space the open door swings into. The door just
  clips into it.

## Panes hugging doors

`IronBarsBlockMixin` connects a pane to a neighbouring glass door when the door is centered and
the pane is at one of the door's ends (not against its face). It does this at the return of
`getStateForPlacement` and `updateShape`, because `attachsTo` is final and doesn't know the
direction. When a pane is placed next to a door, the pane is placed first, then the door centers,
then the pane connects on the door's shape update.
