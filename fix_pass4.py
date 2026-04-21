#!/usr/bin/env python3
"""
Fifth-pass fix script for DeeperDark 26.1.2 migration.
"""

import os
import re

SRC_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "main", "java")

FQN_REPLACEMENTS = [
    # Structure class packages (singular → plural 'structures' subpackage)
    ("net.minecraft.world.level.levelgen.structure.JigsawStructure",
     "net.minecraft.world.level.levelgen.structure.structures.JigsawStructure"),
    ("net.minecraft.world.level.levelgen.structure.StrongholdGenerator",
     "net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure"),
    ("net.minecraft.world.level.levelgen.structure.WoodlandMansionStructure",
     "net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure"),

    # StructurePiecesCollector → StructurePiecesBuilder
    ("net.minecraft.world.level.levelgen.structure.StructurePiecesCollector",
     "net.minecraft.world.level.levelgen.structure.StructurePiecesBuilder"),

    # Attributes
    ("net.minecraft.world.entity.ai.attributes.DefaultAttributeContainer",
     "net.minecraft.world.entity.ai.attributes.AttributeSupplier"),

    # SpawnHelper → NaturalSpawner
    ("net.minecraft.world.SpawnHelper",                "net.minecraft.world.level.NaturalSpawner"),
    ("net.minecraft.world.level.biome.SpawnSettings",  "net.minecraft.world.level.biome.MobSpawnSettings"),

    # StructureAccessor → StructureManager
    ("net.minecraft.world.level.levelgen.StructureAccessor", "net.minecraft.world.level.StructureManager"),

    # TestableWorld → LevelSimulatedReader
    ("net.minecraft.world.TestableWorld",              "net.minecraft.world.level.LevelSimulatedReader"),

    # TreeFeatureConfig → TreeConfiguration
    ("net.minecraft.world.level.levelgen.feature.TreeFeatureConfig",
     "net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration"),

    # Network
    ("net.minecraft.network.PacketByteBuf",            "net.minecraft.network.FriendlyByteBuf"),
    ("net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket",
     "net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket"),
    ("net.minecraft.network.packet.Packet",            "net.minecraft.network.protocol.Packet"),
    ("net.minecraft.network.RegistryByteBuf",          "net.minecraft.network.RegistryFriendlyByteBuf"),

    # VanillaBiomeParameters → OverworldBiomeBuilder
    ("net.minecraft.world.level.biome.util.VanillaBiomeParameters",
     "net.minecraft.world.level.biome.OverworldBiomeBuilder"),

    # Raider
    ("net.minecraft.world.entity.raid.RaiderEntity",   "net.minecraft.world.entity.raid.Raider"),

    # Item components
    ("net.minecraft.world.item.component.LoreComponent", "net.minecraft.world.item.component.ItemLore"),
    ("net.minecraft.world.item.component.TooltipDisplayComponent",
     "net.minecraft.world.item.component.TooltipDisplay"),

    # TranslatableTextContent
    ("net.minecraft.network.chat.TranslatableTextContent",
     "net.minecraft.network.chat.contents.TranslatableContents"),

    # ShapeContext → CollisionContext
    ("net.minecraft.world.phys.shapes.ShapeContext",   "net.minecraft.world.phys.shapes.CollisionContext"),

    # ServerCommonNetworkHandler
    ("net.minecraft.server.network.ServerCommonNetworkHandler",
     "net.minecraft.server.network.ServerCommonPacketListenerImpl"),

    # TeleportTarget → TeleportTransition
    ("net.minecraft.world.TeleportTarget",             "net.minecraft.world.level.portal.TeleportTransition"),

    # PotionEntity → ThrownSplashPotion
    ("net.minecraft.world.entity.projectile.throwableitemprojectile.PotionEntity",
     "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion"),

    # PacketCodec → StreamCodec
    ("net.minecraft.network.codec.PacketCodec",        "net.minecraft.network.codec.StreamCodec"),

    # VillagerTrades in wrong package (fix import if it crept in)
    ("net.minecraft.world.level.biome.util.VanillaBiomeParameters",
     "net.minecraft.world.level.biome.OverworldBiomeBuilder"),
]

SHORT_REPLACEMENTS = [
    # Structure renames
    (r'\bStrongholdGenerator\b',           "StrongholdStructure"),
    (r'\bStructurePiecesCollector\b',      "StructurePiecesBuilder"),

    # Attributes
    (r'\bDefaultAttributeContainer\b',     "AttributeSupplier"),

    # Spawn
    (r'\bSpawnHelper\b',                   "NaturalSpawner"),
    (r'\bSpawnSettings\.SpawnEntry\b',     "MobSpawnSettings.SpawnerData"),
    (r'\bSpawnSettings\b',                 "MobSpawnSettings"),
    (r'\bStructureAccessor\b',             "StructureManager"),
    (r'\bTestableWorld\b',                 "LevelSimulatedReader"),
    (r'\bTreeFeatureConfig\b',             "TreeConfiguration"),
    (r'\bPacketByteBuf\b',                 "FriendlyByteBuf"),
    (r'\bUpdateStructureBlockC2SPacket\b', "ServerboundSetStructureBlockPacket"),
    (r'\bVanillaBiomeParameters\b',        "OverworldBiomeBuilder"),
    (r'\bRaiderEntity\b',                  "Raider"),
    (r'\bLoreComponent\b',                 "ItemLore"),
    (r'\bTooltipDisplayComponent\b',       "TooltipDisplay"),
    (r'\bTranslatableTextContent\b',       "TranslatableContents"),
    (r'\bShapeContext\b',                  "CollisionContext"),
    (r'\bServerCommonNetworkHandler\b',    "ServerCommonPacketListenerImpl"),
    (r'\bTeleportTarget\b',                "TeleportTransition"),
    (r'\bPotionEntity\b',                  "ThrownSplashPotion"),
    (r'\bRegistryByteBuf\b',               "RegistryFriendlyByteBuf"),
    # PacketCodec → StreamCodec  (but don't replace "PacketCodec" if already "StreamCodec")
    (r'\bPacketCodec\b',                   "StreamCodec"),

    # Remaining Text → Component
    (r'\bText\b',                          "Component"),

    # pieceGenerator → generatePieces (StrongholdStructure mixin method)
    # (skip - might cause issues)
]

CONDITIONAL_IMPORTS = {
    "AttributeSupplier":           "net.minecraft.world.entity.ai.attributes.AttributeSupplier",
    "NaturalSpawner":              "net.minecraft.world.level.NaturalSpawner",
    "MobSpawnSettings":            "net.minecraft.world.level.biome.MobSpawnSettings",
    "StructureManager":            "net.minecraft.world.level.StructureManager",
    "LevelSimulatedReader":        "net.minecraft.world.level.LevelSimulatedReader",
    "TreeConfiguration":           "net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration",
    "FriendlyByteBuf":             "net.minecraft.network.FriendlyByteBuf",
    "ServerboundSetStructureBlockPacket": "net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket",
    "OverworldBiomeBuilder":       "net.minecraft.world.level.biome.OverworldBiomeBuilder",
    "Raider":                      "net.minecraft.world.entity.raid.Raider",
    "ItemLore":                    "net.minecraft.world.item.component.ItemLore",
    "TooltipDisplay":              "net.minecraft.world.item.component.TooltipDisplay",
    "TranslatableContents":        "net.minecraft.network.chat.contents.TranslatableContents",
    "CollisionContext":            "net.minecraft.world.phys.shapes.CollisionContext",
    "ServerCommonPacketListenerImpl": "net.minecraft.server.network.ServerCommonPacketListenerImpl",
    "TeleportTransition":          "net.minecraft.world.level.portal.TeleportTransition",
    "ThrownSplashPotion":          "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion",
    "RegistryFriendlyByteBuf":     "net.minecraft.network.RegistryFriendlyByteBuf",
    "StreamCodec":                 "net.minecraft.network.codec.StreamCodec",
    "StructurePiecesBuilder":      "net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder",
    "StrongholdStructure":         "net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure",
    "JigsawStructure":             "net.minecraft.world.level.levelgen.structure.structures.JigsawStructure",
    "WoodlandMansionStructure":    "net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure",
}

STALE_IMPORTS = [
    "import net.minecraft.world.entity.ai.attributes.DefaultAttributeContainer;",
    "import net.minecraft.world.SpawnHelper;",
    "import net.minecraft.world.level.biome.SpawnSettings;",
    "import net.minecraft.world.level.levelgen.StructureAccessor;",
    "import net.minecraft.world.TestableWorld;",
    "import net.minecraft.world.level.levelgen.feature.TreeFeatureConfig;",
    "import net.minecraft.network.PacketByteBuf;",
    "import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;",
    "import net.minecraft.network.RegistryByteBuf;",
    "import net.minecraft.world.level.biome.util.VanillaBiomeParameters;",
    "import net.minecraft.world.entity.raid.RaiderEntity;",
    "import net.minecraft.world.item.component.LoreComponent;",
    "import net.minecraft.world.item.component.TooltipDisplayComponent;",
    "import net.minecraft.network.chat.TranslatableTextContent;",
    "import net.minecraft.world.phys.shapes.ShapeContext;",
    "import net.minecraft.server.network.ServerCommonNetworkHandler;",
    "import net.minecraft.world.TeleportTarget;",
    "import net.minecraft.world.level.levelgen.structure.JigsawStructure;",
    "import net.minecraft.world.level.levelgen.structure.StrongholdGenerator;",
    "import net.minecraft.world.level.levelgen.structure.WoodlandMansionStructure;",
    "import net.minecraft.world.level.levelgen.structure.StructurePiecesCollector;",
    "import net.minecraft.network.codec.PacketCodec;",
]


def add_missing_imports(content):
    lines = content.split('\n')
    last_import_line = -1
    package_line = -1
    for i, line in enumerate(lines):
        stripped = line.strip()
        if stripped.startswith('package '):
            package_line = i
        if stripped.startswith('import '):
            last_import_line = i
    insert_after = last_import_line if last_import_line >= 0 else package_line
    if insert_after < 0:
        return content
    existing_imports = set()
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('import '):
            existing_imports.add(stripped)
    new_imports = []
    for short_name, fqn in CONDITIONAL_IMPORTS.items():
        import_stmt = f"import {fqn};"
        if re.search(r'\b' + re.escape(short_name) + r'\b', content):
            if import_stmt not in existing_imports:
                pkg = fqn.rsplit('.', 1)[0]
                wildcard = f"import {pkg}.*;"
                if wildcard not in existing_imports:
                    new_imports.append(import_stmt)
    if not new_imports:
        return content
    lines.insert(insert_after + 1, '\n'.join(sorted(set(new_imports))))
    return '\n'.join(lines)


def remove_stale_imports(content):
    lines = content.split('\n')
    result = []
    for line in lines:
        stripped = line.strip()
        if any(stripped == stale.strip() for stale in STALE_IMPORTS):
            continue
        result.append(line)
    return '\n'.join(result)


def deduplicate_imports(content):
    lines = content.split('\n')
    seen = set()
    result = []
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('import '):
            if stripped in seen:
                continue
            seen.add(stripped)
        result.append(line)
    return '\n'.join(result)


def fix_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()
    content = original
    for old, new in FQN_REPLACEMENTS:
        content = content.replace(old, new)
    for pattern, replacement in SHORT_REPLACEMENTS:
        content = re.sub(pattern, replacement, content)
    content = remove_stale_imports(content)
    content = add_missing_imports(content)
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
