# Ladders

Ladder tweaks, all behind the `ladder_tweaks` config toggle.

## What players get

- **Stacking.** A ladder placed on the top or bottom face of a ladder copies its facing, even with
  no wall behind it.
- **Hanging.** A ladder survives without a wall if a same-facing ladder is directly above or below
  it, as long as some ladder in the connected column is on a sturdy wall. Remove the anchor and
  the whole column pops off.
- **Lowering from a ledge.** Using a ladder on the *front* face of any ladder in a column, or on
  the *top* face while the player's hitbox fills the space above it, places the new ladder under
  the bottom of the column instead. Repeating the click keeps extending the column down. It stops
  (the click fails) once the space below the column can't be replaced (ground, another block, a
  ladder facing another way, or the bottom of the world). Water is replaced and the ladder is
  waterlogged.
- **Back-side climbing.** A ladder can be climbed from behind, with step sounds.

## How it's built

- `LadderBlockMixin` handles placement facing (`getStateForPlacement` for UP/DOWN clicks on a
  ladder), hanging survival (`canSurvive`), and the column anchor check (`updateShape`). The
  anchor check walks the entire connected column with no cap, because a ladder lowered from a
  ledge is anchored only at its very top. Columns are bounded by world height.
- `BlockItemLadderMixin` handles lowering. It hooks `BlockItem.updatePlacementContext`, the same
  hook vanilla scaffolding uses to redirect placement, and returns a context aimed below the
  column. That context reports the clicked face as `DOWN`, so `LadderBlockMixin`'s
  `getStateForPlacement` copies the facing of the ladder above. Changing that UP/DOWN branch
  breaks lowering.
- `LivingEntityMixin` handles back-side climbing (`onClimbable`) and its step sounds.

## Design decisions

- **Sneaking does not opt out.** Players sneak to hold still on a ladder, which is exactly when
  they'd lower it, so sneaking keeps the same behavior.
- **Front click replaces a vanilla case.** In vanilla, clicking a ladder's front could attach a
  new ladder to a side wall in a narrow shaft. That click now lowers the column instead.

## Known limits

- Like scaffolding, the placed ladder can be far from the clicked block. Reach checks, adventure
  mode `can_place_on` and protection mods that only check the clicked position won't see the
  real target.
- The "standing above" test uses each side's own player hitbox. At the very edge of the space
  the client can predict differently from the server, and the server corrects it.
