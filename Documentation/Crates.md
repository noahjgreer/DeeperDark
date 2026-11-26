# Crates

A crate stores many copies of **one item** whose NBT differs: bows and armor at different
durability, potions, enchanted books. Item vaults only stack identical items, so a mob farm's
bows would each take a vault entry. Crafted from 4 iron ingots, 4 planks and an item frame.

## Rules

- **Lock.** The first item in claims the crate for that exact item (`stack.is(item)`). Any variant
  of it goes in. An emptied crate accepts anything again.
- **Entries.** Identical variants share one entry with a count (`VaultBlockEntity.VaultEntry`,
  reused). The crate holds at most `MAX_ENTRIES` (128) distinct entries. Every distinct variant
  carries its own NBT, so the cap bounds both the chunk data and the crate item.
- **Order.** Entries are kept sorted best first: more enchantment levels (stored ones for books),
  then more durability left. Ties keep insertion order. The menu shows this order. Hoppers take
  from the end, so they drain the worst items first.
- **Nesting.** Crates are tier 2 like vaults (`ContainerItemUtil.tier`): they go in shulker boxes
  but not in vaults, boxes or other crates, and hold only tier 0–1 items. They can't go in bundles
  either, since a crate of bundles holding crates would nest without limit.
- **Breaking** keeps the contents on the item (`copy_components` loot), like vaults.
- **Sounds** are the barrel's: its open/close sounds and the wood block sounds.

## Auto-merge

A per-crate toggle (grindstone button below the scrollbar, saved on the crate and its item).
While it's on, an item that is damaged and otherwise identical to the default item (no
enchantments, name, trim, dye...) is combined with the crate's other such item using vanilla's
crafting-grid repair math: both durabilities plus 5% of the max. That's the only case where grid
repair loses nothing, so enchanted gear is never touched. Once repaired to full, the item stacks
with the crate's undamaged ones. A farm's junk bows collapse into one entry.

Invariant while on: at most one repairable entry, with count 1. Any second one merges into it.
Merging therefore always frees the partner's entry, so a full crate can still accept a merge.
Turning the toggle on pulls the repairable entries out and reinserts them to restore the
invariant. Items taken out of the menu that don't fit the inventory go back via `putBack`, which
never merges.

## Hoppers

The crate isn't a vanilla `Container`. Vanilla hoppers expect `getItem` to return a live stack
and restore it after a failed transfer, and a crate entry isn't a stack. Instead `CrateStorage`
implements Fabric's transactional `Storage<ItemVariant>` and is registered on
`ItemStorage.SIDED`. Fabric's hopper mixin uses it whenever the target has no `Container`, so
hoppers, hopper minecarts and pipe mods that use the Transfer API insert and extract. Snapshots
are deep copies of `CrateContents`. Views look their entry up by variant on each call, because an
aborted transaction swaps in new entry objects.

Comparators and droppers don't interact with crates, since both need a `Container`.

## Menu

`CrateMenu` shows a 9×6 window over the entries. The row is server state and changes through
`clickMenuButton` (`SCROLL_BUTTON + row`). Button 0 toggles auto-merge. Display slots hold copies
with the entry's **full** count, in a `SimpleContainer` whose stack limit is lifted. Otherwise
`SimpleContainer.setItem` clamps the synced count to the item's max stack size. The server
handles every click on those slots itself (the client doesn't predict them):

- Left/right click with an item: deposit all/one.
- Left/right click empty-handed: take a stack/half a stack.
- Shift-click: entry → inventory, or inventory → crate.
- Hotbar swap, drag, throw and double-click collect are ignored on crate slots, because vanilla's
  versions write through the slot and would dupe or delete.

`CrateScreen` draws counts of 1000 and up as `1k`/`1M` and shows the exact count in the tooltip.
Its texture `textures/gui/container/crate.png` is vanilla's `generic_54` widened by 19px, with
the creative tab's scrollbar track.

## Labels

Every face not against a solid block shows a blank map (vanilla's `map_background.png`, as drawn
for a held map) with the crate's best entry on it, in the item-frame (`FIXED`) pose
(`CrateBlockEntityRenderer`). The crate is opaque, so each label takes its light from the block in
front of it. Clients never get the contents: `getUpdateTag` carries only that one item (`shown`),
and `setChanged` sends an update only when the best entry changes. The tag is written even for an
empty crate, so emptying a crate clears its labels.

## Known limits

- The crate can't yet be opened or filled as an item (vault-style right-click in air, open key,
  cursor insert).
- The creative-mode break drops nothing, like vaults.
