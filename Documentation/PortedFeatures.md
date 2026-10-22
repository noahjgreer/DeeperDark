# Ported features

Ports of behaviour from other versions/mods live in `net.noahsarch.deeperdark.ported`, are
registered from `Deeperdark#onInitialize`, and are toggleable via `DeeperDarkConfig`.

## SwordBlockingPort (1.8 sword blocking)

In 26.1.2 blocking is entirely component-driven: `Item#use`, `Item#getUseAnimation`,
`Item#getUseDuration` and `LivingEntity#getItemBlockingWith` all key off
`DataComponents.BLOCKS_ATTACKS`, and `ShieldItem` is an empty class. So to make an item block,
attach a `BlocksAttacks` component (here via Fabric's `DefaultItemComponentEvents.MODIFY`) rather
than overriding methods. `LivingEntity#getItemBlockingWith` is the single authoritative gate:
returning null from it retracts blocking for damage, pose, AI and knockback at once.

Item tags are NOT bound when default components are assembled, so `ItemTags.SWORDS` cannot be used
in a `DefaultItemComponentEvents` predicate. Match on the component fingerprint left by
`ToolMaterial.applySwordProperties` instead.
