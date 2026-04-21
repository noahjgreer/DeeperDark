#!/usr/bin/env python3
"""
Second-pass fix script for DeeperDark 26.1.2 migration.
Fixes remaining Yarn->Mojang issues not caught by the first migration pass.
"""

import os
import re

SRC_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "main", "java")

# ── FULL FQN REPLACEMENTS (import lines + inline FQN usage) ──────────────────
FQN_REPLACEMENTS = [
    # Mixin screen -> inventory package (specific classes)
    ("net.minecraft.screen.AnvilScreenHandler",         "net.minecraft.world.inventory.AnvilMenu"),
    ("net.minecraft.screen.ForgingScreenHandler",       "net.minecraft.world.inventory.ItemCombinerMenu"),
    ("net.minecraft.screen.ScreenHandlerContext",       "net.minecraft.world.inventory.ContainerLevelAccess"),
    ("net.minecraft.screen.Property",                   "net.minecraft.world.inventory.DataSlot"),
    ("net.minecraft.screen.LoomScreenHandler",          "net.minecraft.world.inventory.LoomMenu"),
    ("net.minecraft.screen.CraftingResultSlot",         "net.minecraft.world.inventory.ResultSlot"),
    ("net.minecraft.screen.AbstractFurnaceScreenHandler","net.minecraft.world.inventory.AbstractFurnaceMenu"),
    ("net.minecraft.screen.BlastFurnaceScreenHandler",  "net.minecraft.world.inventory.BlastFurnaceMenu"),
    ("net.minecraft.screen.BrewingStandScreenHandler",  "net.minecraft.world.inventory.BrewingStandMenu"),
    ("net.minecraft.screen.slot.Slot",                  "net.minecraft.world.inventory.Slot"),
    ("net.minecraft.screen.",                           "net.minecraft.world.inventory."),

    # AbstractBoatEntity -> AbstractBoat (specific first, then generic)
    ("net.minecraft.world.entity.vehicle.AbstractBoatEntity", "net.minecraft.world.entity.vehicle.boat.AbstractBoat"),
    ("net.minecraft.world.entity.vehicle.boat.AbstractBoatEntity", "net.minecraft.world.entity.vehicle.boat.AbstractBoat"),

    # Projectile arrow classes
    ("net.minecraft.world.entity.projectile.ArrowEntity",            "net.minecraft.world.entity.projectile.arrow.Arrow"),
    ("net.minecraft.world.entity.projectile.PersistentProjectileEntity","net.minecraft.world.entity.projectile.arrow.AbstractArrow"),

    # NBT/storage API: ReadView -> ValueInput, WriteView -> ValueOutput
    ("net.minecraft.storage.ReadView",  "net.minecraft.world.level.storage.ValueInput"),
    ("net.minecraft.storage.WriteView", "net.minecraft.world.level.storage.ValueOutput"),

    # Permission classes
    ("net.minecraft.commands.permission.Permission",      "net.minecraft.server.permissions.Permission"),
    ("net.minecraft.commands.permission.PermissionLevel", "net.minecraft.server.permissions.PermissionLevel"),

    # Packets still using old Yarn-style path
    ("net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket","net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket"),
    ("net.minecraft.network.packet.s2c.play.PositionFlag",              "net.minecraft.world.entity.Relative"),
    ("net.minecraft.network.packet.s2c.play.",                          "net.minecraft.network.protocol.game."),

    # Math - AffineTransformation
    ("net.minecraft.util.math.AffineTransformation", "com.mojang.math.Transformation"),
    ("net.minecraft.util.math.",                     "net.minecraft.util."),

    # DataComponentTypes -> DataComponents
    ("net.minecraft.core.component.type.ItemEnchantmentsComponent", "net.minecraft.world.item.enchantment.ItemEnchantments"),
    ("net.minecraft.core.component.DataComponentTypes",             "net.minecraft.core.component.DataComponents"),

    # PlayerInventory -> Inventory
    ("net.minecraft.world.entity.player.PlayerInventory", "net.minecraft.world.entity.player.Inventory"),

    # Fluid classes
    ("net.minecraft.fluid.FluidState", "net.minecraft.world.level.material.FluidState"),
    ("net.minecraft.fluid.Fluid",      "net.minecraft.world.level.material.Fluid"),
    ("net.minecraft.fluid.",           "net.minecraft.world.level.material."),
    ("net.minecraft.registry.tag.FluidTags", "net.minecraft.tags.FluidTags"),
    ("net.minecraft.registry.tag.BlockTags", "net.minecraft.tags.BlockTags"),
    ("net.minecraft.registry.tag.ItemTags",  "net.minecraft.tags.ItemTags"),
    ("net.minecraft.registry.tag.EntityTypeTags", "net.minecraft.tags.EntityTypeTags"),
    ("net.minecraft.registry.tag.",     "net.minecraft.tags."),

    # ResourceKeys -> Registries (import)
    ("net.minecraft.resources.ResourceKeys", "net.minecraft.core.registries.Registries"),

    # RaycastContext -> ClipContext
    ("net.minecraft.world.RaycastContext", "net.minecraft.world.level.ClipContext"),

    # EntityPosition -> PositionMoveRotation
    ("net.minecraft.world.entity.EntityPosition", "net.minecraft.world.entity.PositionMoveRotation"),

    # MobSpawnType / SpawnReason -> EntitySpawnReason
    ("net.minecraft.world.entity.MobSpawnType", "net.minecraft.world.entity.EntitySpawnReason"),
    ("net.minecraft.entity.SpawnReason",        "net.minecraft.world.entity.EntitySpawnReason"),

    # ProjectileEntity -> Projectile
    ("net.minecraft.world.entity.projectile.ProjectileEntity", "net.minecraft.world.entity.projectile.Projectile"),
]

# ── WORD-BOUNDARY SHORT NAME REPLACEMENTS ────────────────────────────────────
# Applied to the whole file after FQN substitutions.
# Must NOT replace substrings (use word boundaries).
SHORT_REPLACEMENTS = [
    # Screen handler class renames
    (r'\bAnvilScreenHandler\b',          "AnvilMenu"),
    (r'\bForgingScreenHandler\b',        "ItemCombinerMenu"),
    (r'\bScreenHandlerContext\b',        "ContainerLevelAccess"),
    (r'\bLoomScreenHandler\b',           "LoomMenu"),
    (r'\bCraftingResultSlot\b',          "ResultSlot"),
    (r'\bAbstractFurnaceScreenHandler\b',"AbstractFurnaceMenu"),
    (r'\bBlastFurnaceScreenHandler\b',   "BlastFurnaceMenu"),
    (r'\bBrewingStandScreenHandler\b',   "BrewingStandMenu"),

    # Boat
    (r'\bAbstractBoatEntity\.Location\b', "AbstractBoat.Status"),
    (r'\bAbstractBoatEntity\b',          "AbstractBoat"),

    # Arrow projectiles
    (r'\bArrowEntity\b',                 "Arrow"),
    (r'\bPersistentProjectileEntity\b',  "AbstractArrow"),
    (r'\bProjectileEntity\b',            "Projectile"),
    (r'\bFishingBobberEntity\b',         "FishingHook"),

    # NBT / storage API
    (r'\bReadView\b',                    "ValueInput"),
    (r'\bWriteView\b',                   "ValueOutput"),
    # Method: writeCustomData -> addAdditionalSaveData, readCustomData -> readAdditionalSaveData
    (r'"writeCustomData"',               '"addAdditionalSaveData"'),
    (r'"readCustomData"',                '"readAdditionalSaveData"'),
    # ValueInput uses getFloatOr not getFloat
    (r'\.getFloat\("([^"]+)",\s*([^)]+)\)',  r'.getFloatOr("\1", \2)'),

    # DataComponentTypes -> DataComponents
    (r'\bDataComponentTypes\b',          "DataComponents"),
    # ItemEnchantmentsComponent -> ItemEnchantments
    (r'\bItemEnchantmentsComponent\b',   "ItemEnchantments"),
    # PlayerInventory -> Inventory
    (r'\bPlayerInventory\b',             "Inventory"),

    # AffineTransformation -> Transformation
    (r'\bAffineTransformation\b',        "Transformation"),

    # PositionFlag -> Relative
    (r'\bPositionFlag\b',                "Relative"),

    # RegistryKeys -> Registries (code body usage)
    (r'\bRegistryKeys\b',                "Registries"),
    # ResourceKey.of -> ResourceKey.create
    (r'\bResourceKey\.of\b',             "ResourceKey.create"),

    # Identifier factory methods
    (r'\bIdentifier\.ofVanilla\b',       "Identifier.withDefaultNamespace"),
    # Identifier.of(a, b) -> Identifier.fromNamespaceAndPath(a, b)
    # (can't use simple regex; handled via a more targeted pass below)

    # Vec3d -> Vec3
    (r'\bVec3d\b',                       "Vec3"),

    # Box -> AABB (only in MC context, not com.mojang.math)
    (r'\bnew Box\b',                     "new AABB"),
    (r'\bBox box\b',                     "AABB box"),
    (r'\bBox detectionBox\b',            "AABB detectionBox"),
    (r'\bBox saplingArea\b',             "AABB saplingArea"),
    (r'\bBox\(',                         "AABB("),

    # RaycastContext -> ClipContext
    (r'\bRaycastContext\b',              "ClipContext"),
    (r'\bRaycastContext\.ShapeType\.COLLIDER\b', "ClipContext.Block.COLLIDER"),
    (r'\bRaycastContext\.FluidHandling\.NONE\b', "ClipContext.Fluid.NONE"),
    # World method raycast -> clip
    (r'\.raycast\(new ClipContext\(',    '.clip(new ClipContext('),

    # EntityPosition -> PositionMoveRotation
    (r'\bEntityPosition\b',              "PositionMoveRotation"),

    # MobSpawnType -> EntitySpawnReason
    (r'\bMobSpawnType\b',                "EntitySpawnReason"),
    (r'\bSpawnReason\b',                 "EntitySpawnReason"),

    # Text -> Component (only for Text.literal / Text.translatable usage)
    (r'\bText\.literal\b',               "Component.literal"),
    (r'\bText\.translatable\b',          "Component.translatable"),
    (r'\bText\.empty\b',                 "Component.empty"),

    # Formatting.xxx -> ChatFormatting.xxx (when not prefixed with other things)
    (r'\bFormatting\.([A-Z_]+)\b',       r'ChatFormatting.\1'),
]

# ── IMPORT ADDITIONS (for things that might be missing) ──────────────────────
# If a file uses X but doesn't import it, add the import
CONDITIONAL_IMPORTS = {
    "ClipContext":           "net.minecraft.world.level.ClipContext",
    "ValueInput":            "net.minecraft.world.level.storage.ValueInput",
    "ValueOutput":           "net.minecraft.world.level.storage.ValueOutput",
    "Relative":              "net.minecraft.world.entity.Relative",
    "PositionMoveRotation":  "net.minecraft.world.entity.PositionMoveRotation",
    "AbstractBoat":          "net.minecraft.world.entity.vehicle.boat.AbstractBoat",
    "Arrow":                 "net.minecraft.world.entity.projectile.arrow.Arrow",
    "AbstractArrow":         "net.minecraft.world.entity.projectile.arrow.AbstractArrow",
    "Transformation":        "com.mojang.math.Transformation",
    "EntitySpawnReason":     "net.minecraft.world.entity.EntitySpawnReason",
    "DataComponents":        "net.minecraft.core.component.DataComponents",
    "ItemEnchantments":      "net.minecraft.world.item.enchantment.ItemEnchantments",
    "Inventory":             "net.minecraft.world.entity.player.Inventory",
    "Registries":            "net.minecraft.core.registries.Registries",
    "AABB":                  "net.minecraft.world.phys.AABB",
    "Vec3":                  "net.minecraft.world.phys.Vec3",
    "Component":             "net.minecraft.network.chat.Component",
    "ChatFormatting":        "net.minecraft.ChatFormatting",
    "AnvilMenu":             "net.minecraft.world.inventory.AnvilMenu",
    "ItemCombinerMenu":      "net.minecraft.world.inventory.ItemCombinerMenu",
    "ContainerLevelAccess":  "net.minecraft.world.inventory.ContainerLevelAccess",
    "LoomMenu":              "net.minecraft.world.inventory.LoomMenu",
    "ResultSlot":            "net.minecraft.world.inventory.ResultSlot",
    "DataSlot":              "net.minecraft.world.inventory.DataSlot",
    "FluidState":            "net.minecraft.world.level.material.FluidState",
    "FluidTags":             "net.minecraft.tags.FluidTags",
    "Projectile":            "net.minecraft.world.entity.projectile.Projectile",
    "FishingHook":           "net.minecraft.world.entity.projectile.FishingHook",
}

# ── STALE IMPORTS TO REMOVE ───────────────────────────────────────────────────
STALE_IMPORTS = [
    "import net.minecraft.resources.ResourceKeys;",
    "import net.minecraft.world.RaycastContext;",
    "import net.minecraft.fluid.FluidState;",
    "import net.minecraft.fluid.Fluid;",
    "import net.minecraft.util.math.AffineTransformation;",
    "import net.minecraft.storage.ReadView;",
    "import net.minecraft.storage.WriteView;",
    "import net.minecraft.world.entity.vehicle.AbstractBoatEntity;",
    "import net.minecraft.world.entity.projectile.ArrowEntity;",
    "import net.minecraft.world.entity.projectile.PersistentProjectileEntity;",
    "import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;",
    "import net.minecraft.network.packet.s2c.play.PositionFlag;",
    "import net.minecraft.core.component.DataComponentTypes;",
    "import net.minecraft.core.component.type.ItemEnchantmentsComponent;",
    "import net.minecraft.world.entity.player.PlayerInventory;",
    "import net.minecraft.world.entity.MobSpawnType;",
    "import net.minecraft.world.entity.EntityPosition;",
    # Old screen package imports
    "import net.minecraft.screen.AnvilScreenHandler;",
    "import net.minecraft.screen.ForgingScreenHandler;",
    "import net.minecraft.screen.ScreenHandlerContext;",
    "import net.minecraft.screen.LoomScreenHandler;",
    "import net.minecraft.screen.CraftingResultSlot;",
    "import net.minecraft.screen.Property;",
    # Duplicate Text import if Component is already there
    "import net.minecraft.text.Text;",
    "import net.minecraft.text.MutableText;",
    "import net.minecraft.util.Formatting;",
]


def fix_identifier_of(content):
    """Replace Identifier.of(a, b) -> Identifier.fromNamespaceAndPath(a, b)
    and Identifier.of(a) -> Identifier.withDefaultNamespace(a)"""
    # Two-arg form: Identifier.of("ns", "path") -> Identifier.fromNamespaceAndPath("ns", "path")
    content = re.sub(
        r'\bIdentifier\.of\(([^,)]+),\s*([^)]+)\)',
        r'Identifier.fromNamespaceAndPath(\1, \2)',
        content
    )
    # One-arg form: Identifier.of("path") -> Identifier.withDefaultNamespace("path")
    content = re.sub(
        r'\bIdentifier\.of\(([^)]+)\)',
        r'Identifier.withDefaultNamespace(\1)',
        content
    )
    return content


def add_missing_imports(content, filename):
    """Add missing imports for newly-renamed classes."""
    lines = content.split('\n')

    # Find where to insert imports (after package declaration + existing imports)
    package_line = -1
    last_import_line = -1
    for i, line in enumerate(lines):
        stripped = line.strip()
        if stripped.startswith('package '):
            package_line = i
        if stripped.startswith('import '):
            last_import_line = i

    insert_after = last_import_line if last_import_line >= 0 else package_line
    if insert_after < 0:
        return content

    # Collect all existing imports
    existing_imports = set()
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('import '):
            existing_imports.add(stripped)

    # Determine which imports to add
    new_imports = []
    for short_name, fqn in CONDITIONAL_IMPORTS.items():
        import_stmt = f"import {fqn};"
        # Check if class is used in the file (word boundary)
        if re.search(r'\b' + re.escape(short_name) + r'\b', content):
            if import_stmt not in existing_imports:
                # Make sure we're not importing something already covered by a wildcard or different FQN
                pkg = fqn.rsplit('.', 1)[0]
                wildcard = f"import {pkg}.*;"
                if wildcard not in existing_imports:
                    new_imports.append(import_stmt)

    if not new_imports:
        return content

    # Insert new imports
    new_imports_str = '\n'.join(sorted(set(new_imports)))
    lines.insert(insert_after + 1, new_imports_str)
    return '\n'.join(lines)


def remove_stale_imports(content):
    """Remove imports that are no longer valid."""
    lines = content.split('\n')
    result = []
    for line in lines:
        stripped = line.strip()
        if any(stripped == stale.strip() for stale in STALE_IMPORTS):
            continue
        result.append(line)
    return '\n'.join(result)


def deduplicate_imports(content):
    """Remove duplicate import statements."""
    lines = content.split('\n')
    seen_imports = set()
    result = []
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('import '):
            if stripped in seen_imports:
                continue
            seen_imports.add(stripped)
        result.append(line)
    return '\n'.join(result)


def fix_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original

    # 1. Apply FQN replacements
    for old, new in FQN_REPLACEMENTS:
        content = content.replace(old, new)

    # 2. Apply short name word-boundary replacements
    for pattern, replacement in SHORT_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    # 3. Fix Identifier.of() calls
    content = fix_identifier_of(content)

    # 4. Remove stale imports
    content = remove_stale_imports(content)

    # 5. Add missing imports
    content = add_missing_imports(content, path)

    # 6. Deduplicate imports
    content = deduplicate_imports(content)

    if content != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    changed = 0
    skipped = 0
    for root, dirs, files in os.walk(SRC_DIR):
        # Skip generated data-gen files
        dirs[:] = [d for d in dirs if d not in ('generated',)]
        for filename in files:
            if not filename.endswith('.java'):
                continue
            path = os.path.join(root, filename)
            try:
                if fix_file(path):
                    print(f"  FIXED  {os.path.relpath(path, SRC_DIR)}")
                    changed += 1
                else:
                    skipped += 1
            except Exception as e:
                print(f"  ERROR  {path}: {e}")

    print(f"\nDone. {changed} files modified, {skipped} unchanged.")


if __name__ == '__main__':
    main()
