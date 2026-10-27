# Container Items

Boxes (flimsy 3, sturdy 6, reinforced 9 slots), item vaults (1/3/9 item types), shulker boxes
and ender chests can be used without placing them.

## Ways to open

- **Right-click in air** (`ContainerItemUseInHandMixin`): opens the held item. Returns `CONSUME`
  on both sides so the client doesn't go on to use the offhand item too.
- **Open key over an inventory slot** (`ContainerScreenOpenMixin` → `OpenContainerItemPayload`
  → `Deeperdark.openContainerFromInventory`). The server replies with `OpenFromScreenPayload` so
  Escape returns to the inventory screen.
- **Open key over a slot of the open menu** (`OpenNestedContainerPayload` →
  `openContainerFromCurrentMenu`): a container inside a chest, an ender chest, or an
  item-opened container. Closing the child reopens an item-opened parent.
- **Right-click a container item with a cursor stack** inserts it (`BoxItem`, `VaultItem`,
  `ShulkerBoxItemInsertMixin`); the client draws a green "+" where this would work.

## How an open item is tracked

`ItemBackedContainer` (boxes, shulkers), `ItemBackedVaultEntity` (vaults) and
`OpenMarkedContainer` (ender chest, contents live in the player's ender inventory) stamp a
random id under `deeperdark_open` in the item's `CUSTOM_DATA`. The menu finds the item by that
id, so the player may move it between slots or hold it on the cursor while it is open. Every
change is written back to the item immediately.

While a nested child is open, the parent item keeps its `deeperdark_open` marker and also gets
`deeperdark_nested_from`; the child's `stopOpen` queues the parent in
`PENDING_PARENT_REOPENS`, which `ContainerCloseIdCheckMixin` drains only for a real client
close packet (anything else queued is dropped as stale).

Markers left behind (item dropped, moved away or the server stopped while open) are stripped
each tick from the inventory of any player who has no menu open.

## Safety rules

Each rule closes an item loss or duplication:

- **An open item never goes into a container.** `ContainerItemUtil.isOpen` is checked by every
  right-click insert (both the target and the cursor) and by vault `canAccept`. Otherwise a
  parent box could be deposited into its own nested vault and both would vanish.
- **Slots ask the container.** Vanilla `Slot.mayPlace` returns true without consulting
  `Container.canPlaceItem`, so the reinforced box (dispenser layout) uses `ReinforcedBoxMenu`
  with `BoxMenu.BoxSlot`, or it could be put inside itself.
- **Nested opening only from storage that outlives the menu**: block entities, entities (chest
  minecarts/boats), double chests, the ender inventory, and top-level item-opened containers.
  Crafting grids/results, anvil inputs etc. are emptied or orphaned on close, so a box opened
  from a crafting result would be a phantom copy. Horse/llama inventories are excluded too; their
  storage isn't distance-checked. Nesting two levels deep is refused.
- **A nested child is valid only while its parent is** (`parentStorage.stillValid`). Without this
  a player could drop the parent item, keep taking items out of the child, and pick the parent
  back up with the old contents; or walk away from the chest until its chunk unloads.
- **Vault display slots ignore hotbar swaps and drag-splitting.** They show copies and ignore
  `setByPlayer`, so vanilla's swap and drag code would dupe or delete items.
- **Pick-block from a vault** edits its `vault_entries`; a vault's `CONTAINER` is only a capped
  tooltip preview.

## What each container accepts

- Shulker box: anything except shulker boxes.
- Box: no shulker boxes or boxes (`BoxBlockEntity.canPlaceItem`, which hoppers also respect).
- Vault: no vaults or shulker boxes.
- Item-opened boxes and shulkers refuse every container item (`ItemBackedContainer.canPlaceItem`),
  stricter than the placed blocks.
