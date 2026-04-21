#!/usr/bin/env python3
"""
Migrate DeeperDark mod from Yarn 1.21.11 naming to Mojang 26.1.2 naming.
Run from the project root: python migrate_yarn_to_mojang.py
"""

import os
import re

SRC_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "main", "java")

# ── FULL FQN REPLACEMENTS ──────────────────────────────────────────────────────
# Applied as simple string replacements (longest/most-specific first)
# These handle both import lines and inline fully-qualified references
FQN_REPLACEMENTS = [
    # Server
    ("net.minecraft.server.network.ServerPlayerEntity", "net.minecraft.server.level.ServerPlayer"),
    ("net.minecraft.server.world.ServerWorld",          "net.minecraft.server.level.ServerLevel"),
    ("net.minecraft.server.command.ServerCommandSource","net.minecraft.commands.CommandSourceStack"),
    ("net.minecraft.server.command.CommandManager",     "net.minecraft.commands.Commands"),
    ("net.minecraft.server.MinecraftServer",            "net.minecraft.server.MinecraftServer"),  # stays

    # Network packets
    ("net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket",          "net.minecraft.network.protocol.game.ClientboundSoundPacket"),
    ("net.minecraft.network.packet.s2c.play.StopSoundS2CPacket",          "net.minecraft.network.protocol.game.ClientboundStopSoundPacket"),
    ("net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket","net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket"),
    ("net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket",      "net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket"),
    ("net.minecraft.network.packet.s2c.play.ParticleS2CPacket",            "net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket"),

    # Specific entity classes (before generic package sweeps)
    ("net.minecraft.entity.mob.MobEntity",              "net.minecraft.world.entity.Mob"),
    ("net.minecraft.entity.mob.PathAwareEntity",        "net.minecraft.world.entity.PathfinderMob"),
    ("net.minecraft.entity.mob.CreeperEntity",          "net.minecraft.world.entity.monster.Creeper"),
    ("net.minecraft.entity.mob.WitchEntity",            "net.minecraft.world.entity.monster.Witch"),
    ("net.minecraft.entity.mob.WardenEntity",           "net.minecraft.world.entity.monster.warden.Warden"),
    ("net.minecraft.entity.mob.AbstractSkeletonEntity", "net.minecraft.world.entity.monster.skeleton.AbstractSkeleton"),
    ("net.minecraft.entity.mob.SkeletonEntity",         "net.minecraft.world.entity.monster.skeleton.Skeleton"),
    ("net.minecraft.entity.mob.ZombieEntity",           "net.minecraft.world.entity.monster.zombie.Zombie"),
    ("net.minecraft.entity.mob.ZombieVillagerEntity",   "net.minecraft.world.entity.monster.zombie.ZombieVillager"),
    ("net.minecraft.entity.mob.EndermanEntity",         "net.minecraft.world.entity.monster.EnderMan"),
    ("net.minecraft.entity.mob.AbstractPiglinEntity",   "net.minecraft.world.entity.monster.piglin.AbstractPiglin"),
    ("net.minecraft.entity.mob.PillagerEntity",         "net.minecraft.world.entity.monster.Pillager"),
    ("net.minecraft.entity.mob.SlimeEntity",            "net.minecraft.world.entity.monster.Slime"),
    ("net.minecraft.entity.mob.SpiderEntity",           "net.minecraft.world.entity.monster.Spider"),
    ("net.minecraft.entity.mob.BlazeEntity",            "net.minecraft.world.entity.monster.Blaze"),
    ("net.minecraft.entity.mob.PhantomEntity",          "net.minecraft.world.entity.monster.Phantom"),
    ("net.minecraft.entity.mob.DrownedEntity",          "net.minecraft.world.entity.monster.Drowned"),
    ("net.minecraft.entity.passive.MerchantEntity",     "net.minecraft.world.entity.npc.ClientSideMerchant"),
    ("net.minecraft.entity.passive.VillagerEntity",     "net.minecraft.world.entity.npc.villager.Villager"),
    ("net.minecraft.entity.passive.WolfEntity",         "net.minecraft.world.entity.animal.wolf.Wolf"),
    ("net.minecraft.entity.passive.CatEntity",          "net.minecraft.world.entity.animal.feline.Cat"),
    ("net.minecraft.entity.passive.OcelotEntity",       "net.minecraft.world.entity.animal.feline.Ocelot"),
    ("net.minecraft.entity.passive.SquidEntity",        "net.minecraft.world.entity.animal.squid.Squid"),
    ("net.minecraft.entity.passive.GlowSquidEntity",    "net.minecraft.world.entity.animal.squid.GlowSquid"),
    ("net.minecraft.entity.passive.TameableEntity",     "net.minecraft.world.entity.TamableAnimal"),
    ("net.minecraft.entity.passive.AnimalEntity",       "net.minecraft.world.entity.animal.Animal"),
    ("net.minecraft.entity.passive.AgeableEntity",      "net.minecraft.world.entity.AgeableMob"),
    # Display entities (qualified inner class first)
    ("net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity", "net.minecraft.world.entity.Display.ItemDisplay"),
    ("net.minecraft.entity.decoration.DisplayEntity.BlockDisplayEntity","net.minecraft.world.entity.Display.BlockDisplay"),
    ("net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity", "net.minecraft.world.entity.Display.TextDisplay"),
    ("net.minecraft.entity.decoration.DisplayEntity",   "net.minecraft.world.entity.Display"),
    ("net.minecraft.entity.decoration.ArmorStandEntity","net.minecraft.world.entity.decoration.ArmorStand"),
    ("net.minecraft.entity.decoration.ItemFrameEntity", "net.minecraft.world.entity.decoration.ItemFrame"),
    # Player entity
    ("net.minecraft.entity.player.PlayerEntity",        "net.minecraft.world.entity.player.Player"),
    # Entity effects
    ("net.minecraft.entity.effect.StatusEffectInstance","net.minecraft.world.effect.MobEffectInstance"),
    ("net.minecraft.entity.effect.StatusEffect",        "net.minecraft.world.effect.MobEffect"),
    ("net.minecraft.entity.effect.StatusEffects",       "net.minecraft.world.effect.MobEffects"),
    # Area effect cloud
    ("net.minecraft.entity.AreaEffectCloudEntity",      "net.minecraft.world.entity.AreaEffectCloud"),
    ("net.minecraft.entity.ExperienceOrbEntity",        "net.minecraft.world.entity.ExperienceOrb"),
    ("net.minecraft.entity.FishingBobberEntity",        "net.minecraft.world.entity.projectile.FishingHook"),
    ("net.minecraft.entity.ItemEntity",                 "net.minecraft.world.entity.item.ItemEntity"),
    ("net.minecraft.entity.LivingEntity",               "net.minecraft.world.entity.LivingEntity"),
    ("net.minecraft.entity.EquipmentSlot",              "net.minecraft.world.entity.EquipmentSlot"),
    ("net.minecraft.entity.EntityType",                 "net.minecraft.world.entity.EntityType"),
    ("net.minecraft.entity.SpawnReason",                "net.minecraft.world.entity.MobSpawnType"),
    ("net.minecraft.entity.damage.DamageSource",        "net.minecraft.world.damagesource.DamageSource"),
    ("net.minecraft.entity.damage.DamageType",          "net.minecraft.world.damagesource.DamageType"),
    ("net.minecraft.entity.damage.",                    "net.minecraft.world.damagesource."),
    # Generic entity AI sweeps
    ("net.minecraft.entity.ai.goal.",   "net.minecraft.world.entity.ai.goal."),
    ("net.minecraft.entity.ai.brain.",  "net.minecraft.world.entity.ai.behavior."),
    ("net.minecraft.entity.ai.",        "net.minecraft.world.entity.ai."),
    # Generic entity sweeps (these must come AFTER specific ones)
    ("net.minecraft.entity.mob.",       "net.minecraft.world.entity.monster."),
    ("net.minecraft.entity.passive.",   "net.minecraft.world.entity.animal."),
    ("net.minecraft.entity.boss.",      "net.minecraft.world.entity.boss."),
    ("net.minecraft.entity.projectile.","net.minecraft.world.entity.projectile."),
    ("net.minecraft.entity.decoration.","net.minecraft.world.entity.decoration."),
    ("net.minecraft.entity.player.",    "net.minecraft.world.entity.player."),
    ("net.minecraft.entity.vehicle.",   "net.minecraft.world.entity.vehicle."),
    ("net.minecraft.entity.",           "net.minecraft.world.entity."),

    # Blocks & state
    ("net.minecraft.block.BlockState",  "net.minecraft.world.level.block.state.BlockState"),
    ("net.minecraft.block.entity.",     "net.minecraft.world.level.block.entity."),
    ("net.minecraft.state.property.",   "net.minecraft.world.level.block.state.properties."),
    ("net.minecraft.state.",            "net.minecraft.world.level.block.state."),
    ("net.minecraft.block.PillarBlock", "net.minecraft.world.level.block.RotatedPillarBlock"),
    ("net.minecraft.block.",            "net.minecraft.world.level.block."),

    # Items & enchantments
    ("net.minecraft.item.ItemPlacementContext", "net.minecraft.world.item.context.UseOnContext"),
    ("net.minecraft.item.ItemUsage",            "net.minecraft.world.item.ItemUtils"),
    ("net.minecraft.item.",                     "net.minecraft.world.item."),
    ("net.minecraft.enchantment.",              "net.minecraft.world.item.enchantment."),
    ("net.minecraft.potion.",                   "net.minecraft.world.item.alchemy."),
    ("net.minecraft.component.",                "net.minecraft.core.component."),

    # World & generation
    ("net.minecraft.world.biome.source.BiomeSource",    "net.minecraft.world.level.biome.BiomeSource"),
    ("net.minecraft.world.biome.source.",               "net.minecraft.world.level.biome."),
    ("net.minecraft.world.biome.Biome",                 "net.minecraft.world.level.biome.Biome"),
    ("net.minecraft.world.biome.BiomeKeys",             "net.minecraft.world.level.biome.Biomes"),
    ("net.minecraft.world.biome.",                      "net.minecraft.world.level.biome."),
    ("net.minecraft.world.gen.chunk.ChunkGenerator",    "net.minecraft.world.level.chunk.ChunkGenerator"),
    ("net.minecraft.world.gen.chunk.Blender",           "net.minecraft.world.level.levelgen.blending.Blender"),
    ("net.minecraft.world.gen.chunk.VerticalBlockSample","net.minecraft.world.level.levelgen.VerticalAnchorProvider"),
    ("net.minecraft.world.gen.chunk.",                  "net.minecraft.world.level.levelgen."),
    ("net.minecraft.world.gen.noise.NoiseConfig",       "net.minecraft.world.level.levelgen.NoiseSettings"),
    ("net.minecraft.world.gen.noise.",                  "net.minecraft.world.level.levelgen."),
    ("net.minecraft.world.gen.feature.TreeConfiguredFeatures","net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration"),
    ("net.minecraft.world.gen.feature.ConfiguredFeature","net.minecraft.world.level.levelgen.feature.ConfiguredFeature"),
    ("net.minecraft.world.gen.feature.",                "net.minecraft.world.level.levelgen.feature."),
    ("net.minecraft.world.gen.",                        "net.minecraft.world.level.levelgen."),
    ("net.minecraft.world.chunk.",                      "net.minecraft.world.level.chunk."),
    ("net.minecraft.world.ChunkRegion",                 "net.minecraft.world.level.LevelChunk"),
    ("net.minecraft.world.HeightLimitView",             "net.minecraft.world.level.LevelHeightAccessor"),
    ("net.minecraft.world.Heightmap",                   "net.minecraft.world.level.levelgen.Heightmap"),
    ("net.minecraft.world.WorldView",                   "net.minecraft.world.level.LevelReader"),
    ("net.minecraft.world.WorldAccess",                 "net.minecraft.world.level.LevelAccessor"),
    ("net.minecraft.world.StructureWorldAccess",        "net.minecraft.world.level.LevelAccessor"),
    ("net.minecraft.world.World",                       "net.minecraft.world.level.Level"),

    # Registry
    ("net.minecraft.registry.entry.RegistryEntry",      "net.minecraft.core.Holder"),
    ("net.minecraft.registry.RegistryKey",               "net.minecraft.resources.ResourceKey"),
    ("net.minecraft.registry.RegistryKeys",              "net.minecraft.core.registries.Registries"),
    ("net.minecraft.registry.Registries",                "net.minecraft.core.registries.BuiltInRegistries"),
    ("net.minecraft.registry.Registry",                  "net.minecraft.core.Registry"),

    # Util / math
    ("net.minecraft.util.math.BlockPos",                "net.minecraft.core.BlockPos"),
    ("net.minecraft.util.math.Direction",               "net.minecraft.core.Direction"),
    ("net.minecraft.util.math.Vec3d",                   "net.minecraft.world.phys.Vec3"),
    ("net.minecraft.util.math.Vec3i",                   "net.minecraft.core.Vec3i"),
    ("net.minecraft.util.math.Box",                     "net.minecraft.world.phys.AABB"),
    ("net.minecraft.util.math.ChunkPos",                "net.minecraft.world.level.ChunkPos"),
    ("net.minecraft.util.math.MathHelper",              "net.minecraft.util.Mth"),
    ("net.minecraft.util.math.random.Random",           "net.minecraft.util.RandomSource"),
    ("net.minecraft.util.math.",                        "net.minecraft.util.math."),
    ("net.minecraft.util.BlockRotation",                "net.minecraft.world.level.block.Rotation"),
    ("net.minecraft.util.Identifier",                   "net.minecraft.resources.Identifier"),
    ("net.minecraft.util.ActionResult",                 "net.minecraft.world.InteractionResult"),
    ("net.minecraft.util.Hand",                         "net.minecraft.world.InteractionHand"),
    ("net.minecraft.util.Formatting",                   "net.minecraft.ChatFormatting"),
    ("net.minecraft.util.hit.EntityHitResult",          "net.minecraft.world.phys.EntityHitResult"),
    ("net.minecraft.util.hit.BlockHitResult",           "net.minecraft.world.phys.BlockHitResult"),
    ("net.minecraft.util.hit.HitResult",                "net.minecraft.world.phys.HitResult"),
    ("net.minecraft.util.hit.",                         "net.minecraft.world.phys."),

    # Sound
    ("net.minecraft.sound.SoundEvents",                 "net.minecraft.sounds.SoundEvents"),
    ("net.minecraft.sound.SoundEvent",                  "net.minecraft.sounds.SoundEvent"),
    ("net.minecraft.sound.SoundCategory",               "net.minecraft.sounds.SoundSource"),
    ("net.minecraft.sound.BlockSoundGroup",             "net.minecraft.world.level.block.SoundType"),
    ("net.minecraft.sound.",                            "net.minecraft.sounds."),

    # Text / chat
    ("net.minecraft.text.Text",                         "net.minecraft.network.chat.Component"),
    ("net.minecraft.text.MutableText",                  "net.minecraft.network.chat.MutableComponent"),
    ("net.minecraft.text.",                             "net.minecraft.network.chat."),

    # NBT
    ("net.minecraft.nbt.NbtCompound",                   "net.minecraft.nbt.CompoundTag"),
    ("net.minecraft.nbt.NbtList",                       "net.minecraft.nbt.ListTag"),
    ("net.minecraft.nbt.NbtElement",                    "net.minecraft.nbt.Tag"),
    ("net.minecraft.nbt.NbtString",                     "net.minecraft.nbt.StringTag"),
    ("net.minecraft.nbt.NbtInt",                        "net.minecraft.nbt.IntTag"),
    ("net.minecraft.nbt.NbtFloat",                      "net.minecraft.nbt.FloatTag"),
    ("net.minecraft.nbt.NbtDouble",                     "net.minecraft.nbt.DoubleTag"),
    ("net.minecraft.nbt.NbtByte",                       "net.minecraft.nbt.ByteTag"),
    ("net.minecraft.nbt.NbtLong",                       "net.minecraft.nbt.LongTag"),
    ("net.minecraft.nbt.NbtShort",                      "net.minecraft.nbt.ShortTag"),

    # Particles
    ("net.minecraft.particle.BlockStateParticleEffect","net.minecraft.core.particles.BlockParticleOption"),
    ("net.minecraft.particle.ParticleTypes",            "net.minecraft.core.particles.ParticleTypes"),
    ("net.minecraft.particle.",                         "net.minecraft.core.particles."),

    # Structure
    ("net.minecraft.structure.processor.StructureProcessor",    "net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor"),
    ("net.minecraft.structure.processor.StructureProcessorType","net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType"),
    ("net.minecraft.structure.processor.",              "net.minecraft.world.level.levelgen.structure.templatesystem."),
    ("net.minecraft.structure.StructureTemplate",       "net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate"),
    ("net.minecraft.structure.StructureTemplateManager","net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager"),
    ("net.minecraft.structure.StructurePlacementData",  "net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings"),
    ("net.minecraft.structure.",                        "net.minecraft.world.level.levelgen.structure."),

    # Village / trading
    ("net.minecraft.village.TradeOffer",                "net.minecraft.world.item.trading.MerchantOffer"),
    ("net.minecraft.village.TradeOffers",               "net.minecraft.world.item.trading.VillagerTrades"),
    ("net.minecraft.village.TradedItem",                "net.minecraft.world.item.trading.ItemCost"),
    ("net.minecraft.village.",                          "net.minecraft.world.item.trading."),

    # Commands (remaining)
    ("net.minecraft.command.CommandSource",             "net.minecraft.commands.SharedSuggestionProvider"),
    ("net.minecraft.command.",                          "net.minecraft.commands."),
    ("net.minecraft.server.command.",                   "net.minecraft.server.commands."),
]

# ── SHORT CLASS NAME RENAMES ──────────────────────────────────────────────────
# Applied as whole-word substitutions AFTER FQN processing
# Format: (old_short_name, new_short_name)
# Only rename when the class was actually imported/referenced
# These are safe to do globally because they're unambiguous enough
SHORT_NAME_RENAMES = [
    ("ServerPlayerEntity",          "ServerPlayer"),
    ("ServerWorld",                 "ServerLevel"),
    # NBT renames
    ("NbtCompound",                 "CompoundTag"),
    ("NbtList",                     "ListTag"),
    ("NbtElement",                  "Tag"),
    ("NbtString",                   "StringTag"),
    ("NbtInt",                      "IntTag"),
    ("NbtFloat",                    "FloatTag"),
    ("NbtDouble",                   "DoubleTag"),
    ("NbtByte",                     "ByteTag"),
    ("NbtLong",                     "LongTag"),
    ("NbtShort",                    "ShortTag"),
    # Entity renames
    ("CreeperEntity",               "Creeper"),
    ("WitchEntity",                 "Witch"),
    ("WardenEntity",                "Warden"),
    ("AbstractSkeletonEntity",      "AbstractSkeleton"),
    ("SkeletonEntity",              "Skeleton"),
    ("ZombieEntity",                "Zombie"),
    ("ZombieVillagerEntity",        "ZombieVillager"),
    ("EndermanEntity",              "EnderMan"),
    ("WolfEntity",                  "Wolf"),
    ("OcelotEntity",                "Ocelot"),
    ("SquidEntity",                 "Squid"),
    ("GlowSquidEntity",             "GlowSquid"),
    ("VillagerEntity",              "Villager"),
    ("AreaEffectCloudEntity",       "AreaEffectCloud"),
    ("ExperienceOrbEntity",         "ExperienceOrb"),
    ("FishingBobberEntity",         "FishingHook"),
    ("MobEntity",                   "Mob"),
    ("PathAwareEntity",             "PathfinderMob"),
    ("PlayerEntity",                "Player"),
    ("TameableEntity",              "TamableAnimal"),
    ("AnimalEntity",                "Animal"),
    ("AgeableEntity",               "AgeableMob"),
    ("CatEntity",                   "Cat"),
    ("SlimeEntity",                 "Slime"),
    ("SpiderEntity",                "Spider"),
    ("BlazeEntity",                 "Blaze"),
    ("PhantomEntity",               "Phantom"),
    ("DrownedEntity",               "Drowned"),
    ("PillagerEntity",              "Pillager"),
    ("TameableEntity",              "TamableAnimal"),
    # DisplayEntity (inner class variant first)
    ("DisplayEntity.ItemDisplayEntity", "Display.ItemDisplay"),
    ("DisplayEntity.BlockDisplayEntity","Display.BlockDisplay"),
    ("DisplayEntity.TextDisplayEntity", "Display.TextDisplay"),
    ("DisplayEntity",               "Display"),
    # Status effects
    ("StatusEffectInstance",        "MobEffectInstance"),
    ("StatusEffect",                "MobEffect"),
    ("StatusEffects",               "MobEffects"),
    # Sound
    ("SoundCategory",               "SoundSource"),
    ("BlockSoundGroup",             "SoundType"),
    # Text
    ("MutableText",                 "MutableComponent"),
    # Packets
    ("PlaySoundS2CPacket",          "ClientboundSoundPacket"),
    ("StopSoundS2CPacket",          "ClientboundStopSoundPacket"),
    ("EntityVelocityUpdateS2CPacket","ClientboundSetEntityMotionPacket"),
    ("EntityPositionS2CPacket",     "ClientboundEntityPositionSyncPacket"),
    ("ParticleS2CPacket",           "ClientboundLevelParticlesPacket"),
    # Registry
    ("RegistryKey",                 "ResourceKey"),
    ("RegistryEntry",               "Holder"),
    # Util
    ("BlockRotation",               "Rotation"),
    ("ActionResult",                "InteractionResult"),
    ("MathHelper",                  "Mth"),
    # Items
    ("ItemPlacementContext",        "UseOnContext"),
    # World
    ("HeightLimitView",             "LevelHeightAccessor"),
    ("WorldView",                   "LevelReader"),
    ("WorldAccess",                 "LevelAccessor"),
    ("StructureWorldAccess",        "LevelAccessor"),
    ("ChunkGenerator",              "ChunkGenerator"),  # stays
    # Village
    ("TradeOffer",                  "MerchantOffer"),
    ("TradeOffers",                 "VillagerTrades"),
    ("TradedItem",                  "ItemCost"),
    # Misc
    ("PillarBlock",                 "RotatedPillarBlock"),
    ("BiomeKeys",                   "Biomes"),
    ("StructurePlacementData",      "StructurePlaceSettings"),
    ("StructureProcessorType",      "StructureProcessorType"),  # stays (just package changes)
    ("ServerCommandSource",         "CommandSourceStack"),
]

def word_replace(text: str, old: str, new: str) -> str:
    """Replace old with new using word boundaries."""
    pattern = r'\b' + re.escape(old) + r'\b'
    return re.sub(pattern, new, text)

def process_file(path: str) -> bool:
    """Process a single .java file. Returns True if the file was modified."""
    with open(path, 'r', encoding='utf-8') as f:
        original = f.read()

    content = original

    # ── Step 1: FQN replacements ──────────────────────────────────────────────
    for old, new in FQN_REPLACEMENTS:
        if old in content:
            content = content.replace(old, new)

    # ── Step 2: Short class name renames ─────────────────────────────────────
    for old_short, new_short in SHORT_NAME_RENAMES:
        if old_short != new_short and re.search(r'\b' + re.escape(old_short) + r'\b', content):
            content = word_replace(content, old_short, new_short)

    if content != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    modified = 0
    total = 0
    for root, dirs, files in os.walk(SRC_DIR):
        # Skip build directories
        dirs[:] = [d for d in dirs if d not in ('build', '.gradle', 'out')]
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(root, fname)
            total += 1
            if process_file(path):
                modified += 1
                rel = os.path.relpath(path, SRC_DIR)
                print(f"  Updated: {rel}")

    print(f"\nDone: {modified}/{total} files modified.")

if __name__ == '__main__':
    main()
