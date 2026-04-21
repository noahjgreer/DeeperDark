#!/usr/bin/env python3
"""
Third-pass fix script for DeeperDark 26.1.2 migration.
Handles remaining package/class renames after fix_remaining_issues.py.
"""

import os
import re

SRC_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "main", "java")

FQN_REPLACEMENTS = [
    # Loot package
    ("net.minecraft.loot.context.LootContextParameters", "net.minecraft.world.level.storage.loot.parameters.LootContextParams"),
    ("net.minecraft.loot.context.LootWorldContext",      "net.minecraft.world.level.storage.loot.LootParams"),
    ("net.minecraft.loot.context.LootContext",           "net.minecraft.world.level.storage.loot.LootContext"),
    ("net.minecraft.loot.function.ApplyBonusLootFunction","net.minecraft.world.level.storage.loot.functions.ApplyBonusCount"),
    ("net.minecraft.loot.condition.MatchToolLootCondition","net.minecraft.world.level.storage.loot.predicates.MatchTool"),
    ("net.minecraft.loot.context.",                      "net.minecraft.world.level.storage.loot."),
    ("net.minecraft.loot.function.",                     "net.minecraft.world.level.storage.loot.functions."),
    ("net.minecraft.loot.condition.",                    "net.minecraft.world.level.storage.loot.predicates."),
    ("net.minecraft.loot.",                              "net.minecraft.world.level.storage.loot."),

    # Recipe package
    ("net.minecraft.recipe.",                            "net.minecraft.world.item.crafting."),

    # Collection utilities
    ("net.minecraft.util.collection.DefaultedList",      "net.minecraft.core.NonNullList"),

    # Consumable component types
    ("net.minecraft.core.component.type.ConsumableComponent",  "net.minecraft.world.item.component.Consumable"),
    ("net.minecraft.core.component.type.ConsumableComponents", "net.minecraft.world.item.component.Consumables"),
    ("net.minecraft.core.component.type.",                     "net.minecraft.world.item.component."),

    # Consume effects
    ("net.minecraft.world.item.consume.ApplyEffectsConsumeEffect", "net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect"),
    ("net.minecraft.world.item.consume.",                          "net.minecraft.world.item.consume_effects."),

    # Thrown projectile package rename
    ("net.minecraft.world.entity.projectile.thrown.ExperienceBottleEntity","net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle"),
    ("net.minecraft.world.entity.projectile.thrown.ThrownItemEntity",      "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile"),
    ("net.minecraft.world.entity.projectile.thrown.",                      "net.minecraft.world.entity.projectile.throwableitemprojectile."),

    # Explosion package
    ("net.minecraft.world.explosion.ExplosionImpl", "net.minecraft.world.level.ServerExplosion"),
    ("net.minecraft.world.explosion.Explosion",     "net.minecraft.world.level.Explosion"),
    ("net.minecraft.world.explosion.",              "net.minecraft.world.level."),

    # Entity attributes package
    ("net.minecraft.world.entity.attribute.EntityAttributes", "net.minecraft.world.entity.ai.attributes.Attributes"),
    ("net.minecraft.world.entity.attribute.",                 "net.minecraft.world.entity.ai.attributes."),

    # World dimension package
    ("net.minecraft.world.dimension.DimensionType", "net.minecraft.world.level.dimension.DimensionType"),
    ("net.minecraft.world.dimension.NetherPortal",  "net.minecraft.world.level.portal.PortalShape"),
    ("net.minecraft.world.dimension.",              "net.minecraft.world.level.dimension."),

    # Block enums package
    ("net.minecraft.world.level.block.enums.NoteBlockInstrument", "net.minecraft.world.level.block.state.properties.NoteBlockInstrument"),
    ("net.minecraft.world.level.block.enums.",                    "net.minecraft.world.level.block.state.properties."),

    # Inventory slot sub-package - should just be in inventory
    ("net.minecraft.world.inventory.slot.ResultSlot", "net.minecraft.world.inventory.ResultSlot"),
    ("net.minecraft.world.inventory.slot.",           "net.minecraft.world.inventory."),

    # Item predicate
    ("net.minecraft.predicate.item.ItemPredicate", "net.minecraft.advancements.criterion.ItemPredicate"),
    ("net.minecraft.predicate.",                   "net.minecraft.advancements."),

    # BiomeAccess -> BiomeManager (biome access was merged into BiomeManager)
    ("net.minecraft.world.level.biome.BiomeAccess", "net.minecraft.world.level.biome.BiomeManager"),

    # MultiNoiseUtil.MultiNoiseSampler -> Climate.Sampler
    ("net.minecraft.world.level.biome.util.MultiNoiseUtil", "net.minecraft.world.level.biome.Climate"),

    # OctavePerlinNoiseSampler -> PerlinNoise
    ("net.minecraft.util.noise.OctavePerlinNoiseSampler", "net.minecraft.world.level.levelgen.synth.PerlinNoise"),

    # NoiseChunkGenerator -> NoiseBasedChunkGenerator, ChunkGeneratorSettings -> NoiseGeneratorSettings
    ("net.minecraft.world.level.chunk.ChunkGeneratorSettings", "net.minecraft.world.level.levelgen.NoiseGeneratorSettings"),
    ("net.minecraft.world.level.levelgen.AquiferSampler",      "net.minecraft.world.level.levelgen.Aquifer"),
    ("net.minecraft.world.level.levelgen.NoiseChunkGenerator", "net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator"),

    # BlockView (Yarn) -> BlockGetter (Mojang)
    ("net.minecraft.world.BlockView", "net.minecraft.world.level.BlockGetter"),

    # NetherPortal Accessor target class fix
    ("net.minecraft.world.level.portal.PortalShape", "net.minecraft.world.level.portal.PortalShape"),  # already correct
]

SHORT_REPLACEMENTS = [
    # Loot class renames
    (r'\bApplyBonusLootFunction\b',       "ApplyBonusCount"),
    (r'\bMatchToolLootCondition\b',       "MatchTool"),
    (r'\bLootContextParameters\b',        "LootContextParams"),
    (r'\bLootWorldContext\b',             "LootParams"),

    # Recipe
    # (most recipe classes have same short names in new package)

    # Collection
    (r'\bDefaultedList\b',                "NonNullList"),

    # Consumable
    (r'\bConsumableComponent\b',          "Consumable"),
    (r'\bConsumableComponents\b',         "Consumables"),
    (r'\bApplyEffectsConsumeEffect\b',    "ApplyStatusEffectsConsumeEffect"),

    # Projectile thrown
    (r'\bExperienceBottleEntity\b',       "ThrownExperienceBottle"),
    (r'\bThrownItemEntity\b',             "ThrowableItemProjectile"),

    # Explosion
    (r'\bExplosionImpl\b',                "ServerExplosion"),

    # Attributes
    (r'\bEntityAttributes\b',             "Attributes"),

    # Portal
    (r'\bNetherPortal\b',                 "PortalShape"),

    # Block enums
    # NoteBlockInstrument stays the same short name

    # Biome
    (r'\bBiomeAccess\.Storage\b',         "BiomeManager.NoiseBiomeSource"),
    (r'\bBiomeAccess\b',                  "BiomeManager"),
    (r'\bMultiNoiseUtil\.MultiNoiseSampler\b', "Climate.Sampler"),
    (r'\bMultiNoiseUtil\b',               "Climate"),

    # Worldgen
    (r'\bOctavePerlinNoiseSampler\b',     "PerlinNoise"),
    (r'\bNoiseChunkGenerator\b',          "NoiseBasedChunkGenerator"),
    (r'\bChunkGeneratorSettings\b',       "NoiseGeneratorSettings"),
    (r'\bAquiferSampler\.FluidLevelSampler\b', "Aquifer.FluidPicker"),
    (r'\bAquiferSampler\.FluidLevel\b',   "Aquifer.FluidStatus"),
    (r'\bAquiferSampler\b',               "Aquifer"),

    # BlockView -> BlockGetter
    (r'\bBlockView\b',                    "BlockGetter"),

    # MultiNoiseBiomeSource.getBiome -> getNoiseBiome
    # The method rename is in the mixin injection point (method name string)
    (r'"getBiome"\b',                     '"getNoiseBiome"'),
]

CONDITIONAL_IMPORTS = {
    "LootContextParams":         "net.minecraft.world.level.storage.loot.parameters.LootContextParams",
    "LootParams":                "net.minecraft.world.level.storage.loot.LootParams",
    "LootContext":               "net.minecraft.world.level.storage.loot.LootContext",
    "ApplyBonusCount":           "net.minecraft.world.level.storage.loot.functions.ApplyBonusCount",
    "MatchTool":                 "net.minecraft.world.level.storage.loot.predicates.MatchTool",
    "NonNullList":               "net.minecraft.core.NonNullList",
    "Consumable":                "net.minecraft.world.item.component.Consumable",
    "Consumables":               "net.minecraft.world.item.component.Consumables",
    "ApplyStatusEffectsConsumeEffect": "net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect",
    "ThrownExperienceBottle":    "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle",
    "ThrowableItemProjectile":   "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile",
    "ServerExplosion":           "net.minecraft.world.level.ServerExplosion",
    "Explosion":                 "net.minecraft.world.level.Explosion",
    "Attributes":                "net.minecraft.world.entity.ai.attributes.Attributes",
    "PortalShape":               "net.minecraft.world.level.portal.PortalShape",
    "DimensionType":             "net.minecraft.world.level.dimension.DimensionType",
    "BiomeManager":              "net.minecraft.world.level.biome.BiomeManager",
    "Climate":                   "net.minecraft.world.level.biome.Climate",
    "PerlinNoise":               "net.minecraft.world.level.levelgen.synth.PerlinNoise",
    "NoiseBasedChunkGenerator":  "net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator",
    "NoiseGeneratorSettings":    "net.minecraft.world.level.levelgen.NoiseGeneratorSettings",
    "Aquifer":                   "net.minecraft.world.level.levelgen.Aquifer",
    "BlockGetter":               "net.minecraft.world.level.BlockGetter",
    "ItemPredicate":             "net.minecraft.advancements.criterion.ItemPredicate",
    "NoteBlockInstrument":       "net.minecraft.world.level.block.state.properties.NoteBlockInstrument",
}

STALE_IMPORTS = [
    "import net.minecraft.world.level.biome.util.MultiNoiseUtil;",
    "import net.minecraft.world.level.chunk.ChunkGeneratorSettings;",
    "import net.minecraft.world.level.levelgen.AquiferSampler;",
    "import net.minecraft.util.noise.OctavePerlinNoiseSampler;",
    "import net.minecraft.world.BlockView;",
    "import net.minecraft.world.dimension.NetherPortal;",
    "import net.minecraft.world.dimension.DimensionType;",
    "import net.minecraft.world.entity.attribute.EntityAttributes;",
    "import net.minecraft.world.explosion.Explosion;",
    "import net.minecraft.world.explosion.ExplosionImpl;",
    "import net.minecraft.loot.context.LootContextParameters;",
    "import net.minecraft.loot.context.LootContext;",
    "import net.minecraft.loot.context.LootWorldContext;",
    "import net.minecraft.loot.function.ApplyBonusLootFunction;",
    "import net.minecraft.loot.condition.MatchToolLootCondition;",
    "import net.minecraft.util.collection.DefaultedList;",
    "import net.minecraft.core.component.type.ConsumableComponent;",
    "import net.minecraft.core.component.type.ConsumableComponents;",
    "import net.minecraft.world.item.consume.ApplyEffectsConsumeEffect;",
    "import net.minecraft.world.entity.projectile.thrown.ExperienceBottleEntity;",
    "import net.minecraft.world.entity.projectile.thrown.ThrownItemEntity;",
    "import net.minecraft.predicate.item.ItemPredicate;",
    "import net.minecraft.world.level.block.enums.NoteBlockInstrument;",
    "import net.minecraft.world.inventory.slot.ResultSlot;",
    "import net.minecraft.world.level.biome.BiomeAccess;",
    "import net.minecraft.recipe.*;",
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
