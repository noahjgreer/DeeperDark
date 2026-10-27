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
  `openContainerFromCurrentMenu`): a container inside a chest, an ender chest, a donkey/llama,
  or an item-opened container. Closing the child reopens an item-opened parent.
- **Right-click a container item with a cursor stack** inserts it (`BoxItem`, `VaultItem`,
  `ShulkerBoxItemInsertMixin`); the client draws a green "+" where this would work.

## How an open item is tracked

`ItemBackedContainer` (boxes, shulkers), `ItemBackedVaultEntity` (vaults) and
`OpenMarkedContainer` (ender chest, contents live in the player's ender inventory) stamp a
random id under `deeperdark_open` in the item's `CUSTOM_DATA`. The menu finds the item by that
id, so the player may move it between slots or hold it on the cursor while it is open. Every
change is written back to the item immediately.

While a nested child is open, the parent item keeps its `deeperdark_open` marker (so the
parent's menu stays valid, see below) and also gets `deeperdark_nested_from`; the child's
`stopOpen` queues the parent in `PENDING_PARENT_REOPENS`, which `ContainerCloseIdCheckMixin` drains only for a real client
close packet (anything else queued is dropped as stale).

Markers left behind (item dropped, moved away or the server stopped while open) are stripped
each tick from the inventory of any player who has no menu open.

## What each container accepts

One rule, `ContainerItemUtil.canStore(tier, stack)`, whether the container is placed or opened
as an item, for menu slots, right-click inserts and hoppers into boxes and vaults (vanilla's
hopper check for placed shulker boxes already refuses exactly shulker boxes). Containers are tiered
shulker box 3 > vault 2 > box 1 > anything else 0, and a container only holds lower tiers:

- Shulker box: anything except shulker boxes.
- Vault: no vaults or shulker boxes.
- Box: no boxes, vaults or shulker boxes.
- Ender chest: anything not open (an ender chest item carries no contents itself).

The strict ordering keeps nesting finite; a box↔vault or box↔bundle loop could grow NBT without
limit. For the same reason boxes, like shulker boxes and vaults, don't fit in bundles
(`canFitInsideContainerItems`).

Slots check the rule themselves (`BoxMenu.BoxSlot`, `ShulkerBoxSlotMixin`, vault `canAccept`)
because vanilla `Slot.mayPlace` never asks the container, and so the client, whose container is
a plain `SimpleContainer`, predicts rejections instead of flickering. The reinforced box uses
`BoxMenu` with its own menu type and the vanilla dispenser texture for that reason.

## Safety rules

Each rule closes an item loss or duplication:

- **An open item never goes into a container.** `canStore` refuses items carrying
  `deeperdark_open`, and `SlotOpenContainerMixin` keeps them out of every non-inventory plain
  slot (e.g. the item-opened ender chest's own grid). Otherwise a container could be put inside
  itself, or a parent box into its own nested vault, and vanish.
- **Nested opening only from storage that outlives the menu**: block entities, entities (chest
  minecarts/boats), double chests, the ender inventory, mount inventories (donkey, llama), and
  top-level item-opened containers. Crafting grids/results, anvil inputs etc. are emptied or
  orphaned on close, so a box opened from a crafting result would be a phantom copy. Nesting two
  levels deep is refused.
- **A nested child is valid only while the menu it was opened from is** (that menu's
  `stillValid`, still callable after it closed). It carries vanilla's distance, alive and
  block-entity-still-there checks, so dropping the parent item, walking away from the chest or
  donkey, or the chunk unloading closes the child before its edits can go nowhere (otherwise:
  take items out, pick the parent back up with the old contents).
- **Vault display slots ignore hotbar swaps and drag-splitting.** They show copies and ignore
  `setByPlayer`, so vanilla's swap and drag code would dupe or delete items.
- **Pick-block from a vault** edits its `vault_entries`; a vault's `CONTAINER` is only a capped
  tooltip preview.
