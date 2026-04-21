#!/usr/bin/env python3
"""
Fourth-pass fix script for DeeperDark 26.1.2 migration.
Handles remaining class/package renames after fix_pass2.py.
"""

import os
import re

SRC_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "main", "java")

FQN_REPLACEMENTS = [
    # GameRules package
    ("net.minecraft.world.rule.GameRules",               "net.minecraft.world.level.gamerules.GameRules"),

    # Criteria (advancements)
    ("net.minecraft.advancement.criterion.Criteria",     "net.minecraft.advancements.CriteriaTriggers"),

    # GameEvent package
    ("net.minecraft.world.event.GameEvent",              "net.minecraft.world.level.gameevent.GameEvent"),

    # Network - chat packet
    ("net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket", "net.minecraft.network.protocol.game.ServerboundChatPacket"),

    # Server network handlers
    ("net.minecraft.server.network.ServerPlayNetworkHandler", "net.minecraft.server.network.ServerGamePacketListenerImpl"),
    ("net.minecraft.network.ClientConnection",            "net.minecraft.network.Connection"),
    ("net.minecraft.server.PlayerManager",                "net.minecraft.server.players.PlayerList"),
    ("net.minecraft.server.network.ConnectedClientData", "net.minecraft.server.network.CommonListenerCookie"),

    # Piston
    ("net.minecraft.world.level.block.piston.PistonHandler", "net.minecraft.world.level.block.piston.PistonStructureResolver"),

    # Lightning
    ("net.minecraft.world.entity.LightningEntity",       "net.minecraft.world.entity.LightningBolt"),

    # PotionContents
    ("net.minecraft.world.item.component.PotionContentsComponent", "net.minecraft.world.item.alchemy.PotionContents"),

    # Inventory types
    ("net.minecraft.world.inventory.ScreenHandlerType",  "net.minecraft.world.inventory.MenuType"),
    ("net.minecraft.world.inventory.ForgingSlotsManager","net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition"),

    # Trading/Villager - wrong package
    ("net.minecraft.world.item.trading.MerchantOfferList","net.minecraft.world.item.trading.MerchantOffers"),
    ("net.minecraft.world.item.trading.VillagerData",    "net.minecraft.world.entity.npc.villager.VillagerData"),
    ("net.minecraft.world.item.trading.VillagerProfession","net.minecraft.world.entity.npc.villager.VillagerProfession"),

    # Fluid
    ("net.minecraft.world.level.material.FlowableFluid", "net.minecraft.world.level.material.FlowingFluid"),
    ("net.minecraft.world.level.block.FluidBlock",       "net.minecraft.world.level.block.LiquidBlock"),

    # EntityPose
    ("net.minecraft.world.entity.EntityPose",            "net.minecraft.world.entity.Pose"),

    # Structure piece
    ("net.minecraft.world.level.levelgen.structure.SimpleStructurePiece", "net.minecraft.world.level.levelgen.structure.TemplateStructurePiece"),

    # GlassBottleItem → BottleItem
    ("net.minecraft.world.item.GlassBottleItem",         "net.minecraft.world.item.BottleItem"),

    # StringHelper
    ("net.minecraft.util.StringHelper",                  "net.minecraft.util.StringUtil"),

    # BiomeCoords → QuartPos
    ("net.minecraft.world.level.biome.BiomeCoords",      "net.minecraft.core.QuartPos"),

    # Block state Properties → BlockStateProperties
    ("net.minecraft.world.level.block.state.properties.Properties",
     "net.minecraft.world.level.block.state.properties.BlockStateProperties"),

    # MerchantEntity → AbstractVillager
    ("net.minecraft.entity.MerchantEntity",              "net.minecraft.world.entity.npc.villager.AbstractVillager"),

    # Spawn data
    ("net.minecraft.world.entity.EntityData",            "net.minecraft.world.entity.SpawnGroupData"),

    # Difficulty
    ("net.minecraft.world.LocalDifficulty",              "net.minecraft.world.DifficultyInstance"),

    # ServerLevelAccessor
    ("net.minecraft.world.ServerWorldAccess",            "net.minecraft.world.level.ServerLevelAccessor"),

    # AttributeModifier
    ("net.minecraft.world.entity.ai.attributes.EntityAttributeModifier",
     "net.minecraft.world.entity.ai.attributes.AttributeModifier"),
]

SHORT_REPLACEMENTS = [
    # Class renames
    (r'\bCriteria\.', "CriteriaTriggers."),
    (r'\bChatMessageC2SPacket\b',      "ServerboundChatPacket"),
    (r'\bServerPlayNetworkHandler\b',  "ServerGamePacketListenerImpl"),
    (r'\bClientConnection\b',          "Connection"),
    (r'\bPlayerManager\b',             "PlayerList"),
    (r'\bConnectedClientData\b',       "CommonListenerCookie"),
    (r'\bPistonHandler\b',             "PistonStructureResolver"),
    (r'\bLightningEntity\b',           "LightningBolt"),
    (r'\bPotionContentsComponent\b',   "PotionContents"),
    (r'\bScreenHandlerType\b',         "MenuType"),
    (r'\bForgingSlotsManager\b',       "ItemCombinerMenuSlotDefinition"),
    (r'\bMerchantOfferList\b',         "MerchantOffers"),
    (r'\bFlowableFluid\b',             "FlowingFluid"),
    (r'\bFluidBlock\b',                "LiquidBlock"),
    (r'\bEntityPose\b',                "Pose"),
    (r'\bSimpleStructurePiece\b',      "TemplateStructurePiece"),
    (r'\bGlassBottleItem\b',           "BottleItem"),
    (r'\bStringHelper\b',              "StringUtil"),
    (r'\bBiomeCoords\b',               "QuartPos"),
    (r'\bMerchantEntity\b',            "AbstractVillager"),
    (r'\bEntityAttributeModifier\b',   "AttributeModifier"),
    (r'\bLocalDifficulty\b',           "DifficultyInstance"),
    (r'\bServerWorldAccess\b',         "ServerLevelAccessor"),
    (r'\bEntityData\b',                "SpawnGroupData"),

    # World → Level (unqualified type usage)
    (r'\bWorld\b',                     "Level"),

    # Hand → InteractionHand
    (r'\bHand\b',                      "InteractionHand"),

    # ItemUsageContext / ItemUtilsContext → UseOnContext
    (r'\bItemUsageContext\b',          "UseOnContext"),
    (r'\bItemUtilsContext\b',          "UseOnContext"),

    # Random (MC context) → RandomSource
    (r'\bRandom\b',                    "RandomSource"),
]

CONDITIONAL_IMPORTS = {
    "GameRules":                  "net.minecraft.world.level.gamerules.GameRules",
    "CriteriaTriggers":           "net.minecraft.advancements.CriteriaTriggers",
    "GameEvent":                  "net.minecraft.world.level.gameevent.GameEvent",
    "ServerboundChatPacket":      "net.minecraft.network.protocol.game.ServerboundChatPacket",
    "ServerGamePacketListenerImpl":"net.minecraft.server.network.ServerGamePacketListenerImpl",
    "Connection":                 "net.minecraft.network.Connection",
    "PlayerList":                 "net.minecraft.server.players.PlayerList",
    "CommonListenerCookie":       "net.minecraft.server.network.CommonListenerCookie",
    "PistonStructureResolver":    "net.minecraft.world.level.block.piston.PistonStructureResolver",
    "LightningBolt":              "net.minecraft.world.entity.LightningBolt",
    "PotionContents":             "net.minecraft.world.item.alchemy.PotionContents",
    "MenuType":                   "net.minecraft.world.inventory.MenuType",
    "ItemCombinerMenuSlotDefinition":"net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition",
    "MerchantOffers":             "net.minecraft.world.item.trading.MerchantOffers",
    "VillagerData":               "net.minecraft.world.entity.npc.villager.VillagerData",
    "VillagerProfession":         "net.minecraft.world.entity.npc.villager.VillagerProfession",
    "FlowingFluid":               "net.minecraft.world.level.material.FlowingFluid",
    "LiquidBlock":                "net.minecraft.world.level.block.LiquidBlock",
    "Pose":                       "net.minecraft.world.entity.Pose",
    "TemplateStructurePiece":     "net.minecraft.world.level.levelgen.structure.TemplateStructurePiece",
    "BottleItem":                 "net.minecraft.world.item.BottleItem",
    "StringUtil":                 "net.minecraft.util.StringUtil",
    "QuartPos":                   "net.minecraft.core.QuartPos",
    "BlockStateProperties":       "net.minecraft.world.level.block.state.properties.BlockStateProperties",
    "AbstractVillager":           "net.minecraft.world.entity.npc.villager.AbstractVillager",
    "SpawnGroupData":             "net.minecraft.world.entity.SpawnGroupData",
    "DifficultyInstance":         "net.minecraft.world.DifficultyInstance",
    "ServerLevelAccessor":        "net.minecraft.world.level.ServerLevelAccessor",
    "AttributeModifier":          "net.minecraft.world.entity.ai.attributes.AttributeModifier",
    "UseOnContext":               "net.minecraft.world.item.context.UseOnContext",
    "InteractionHand":            "net.minecraft.world.InteractionHand",
    "RandomSource":               "net.minecraft.util.RandomSource",
}

STALE_IMPORTS = [
    "import net.minecraft.world.rule.GameRules;",
    "import net.minecraft.advancement.criterion.Criteria;",
    "import net.minecraft.world.event.GameEvent;",
    "import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;",
    "import net.minecraft.server.network.ServerPlayNetworkHandler;",
    "import net.minecraft.network.ClientConnection;",
    "import net.minecraft.server.PlayerManager;",
    "import net.minecraft.server.network.ConnectedClientData;",
    "import net.minecraft.world.level.block.piston.PistonHandler;",
    "import net.minecraft.world.entity.LightningEntity;",
    "import net.minecraft.world.item.component.PotionContentsComponent;",
    "import net.minecraft.world.inventory.ScreenHandlerType;",
    "import net.minecraft.world.inventory.ForgingSlotsManager;",
    "import net.minecraft.world.item.trading.MerchantOfferList;",
    "import net.minecraft.world.item.trading.VillagerData;",
    "import net.minecraft.world.item.trading.VillagerProfession;",
    "import net.minecraft.world.level.material.FlowableFluid;",
    "import net.minecraft.world.level.block.FluidBlock;",
    "import net.minecraft.world.entity.EntityPose;",
    "import net.minecraft.world.level.levelgen.structure.SimpleStructurePiece;",
    "import net.minecraft.world.item.GlassBottleItem;",
    "import net.minecraft.util.StringHelper;",
    "import net.minecraft.world.level.biome.BiomeCoords;",
    "import net.minecraft.world.level.block.state.properties.Properties;",
    "import net.minecraft.entity.MerchantEntity;",
    "import net.minecraft.world.entity.EntityData;",
    "import net.minecraft.world.LocalDifficulty;",
    "import net.minecraft.world.ServerWorldAccess;",
    "import net.minecraft.world.entity.ai.attributes.EntityAttributeModifier;",
    "import net.minecraft.world.World;",
    "import net.minecraft.world.item.ItemUsageContext;",
    "import net.minecraft.world.item.ItemUtilsContext;",
    "import java.util.Random;",
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
