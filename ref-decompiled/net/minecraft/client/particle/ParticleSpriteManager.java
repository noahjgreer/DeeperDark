/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AshParticle$Factory
 *  net.minecraft.client.particle.BlockDustParticle$CrumbleFactory
 *  net.minecraft.client.particle.BlockDustParticle$DustPillarFactory
 *  net.minecraft.client.particle.BlockDustParticle$Factory
 *  net.minecraft.client.particle.BlockFallingDustParticle$Factory
 *  net.minecraft.client.particle.BlockLeakParticle$DrippingDripstoneLavaFactory
 *  net.minecraft.client.particle.BlockLeakParticle$DrippingDripstoneWaterFactory
 *  net.minecraft.client.particle.BlockLeakParticle$DrippingHoneyFactory
 *  net.minecraft.client.particle.BlockLeakParticle$DrippingLavaFactory
 *  net.minecraft.client.particle.BlockLeakParticle$DrippingObsidianTearFactory
 *  net.minecraft.client.particle.BlockLeakParticle$DrippingWaterFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingDripstoneLavaFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingDripstoneWaterFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingHoneyFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingLavaFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingNectarFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingObsidianTearFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingSporeBlossomFactory
 *  net.minecraft.client.particle.BlockLeakParticle$FallingWaterFactory
 *  net.minecraft.client.particle.BlockLeakParticle$LandingHoneyFactory
 *  net.minecraft.client.particle.BlockLeakParticle$LandingLavaFactory
 *  net.minecraft.client.particle.BlockLeakParticle$LandingObsidianTearFactory
 *  net.minecraft.client.particle.BlockMarkerParticle$Factory
 *  net.minecraft.client.particle.BubbleColumnUpParticle$Factory
 *  net.minecraft.client.particle.BubblePopParticle$Factory
 *  net.minecraft.client.particle.CampfireSmokeParticle$CosySmokeFactory
 *  net.minecraft.client.particle.CampfireSmokeParticle$SignalSmokeFactory
 *  net.minecraft.client.particle.CloudParticle$CloudFactory
 *  net.minecraft.client.particle.CloudParticle$SneezeFactory
 *  net.minecraft.client.particle.ConnectionParticle$EnchantFactory
 *  net.minecraft.client.particle.ConnectionParticle$NautilusFactory
 *  net.minecraft.client.particle.ConnectionParticle$VaultConnectionFactory
 *  net.minecraft.client.particle.CrackParticle$CobwebFactory
 *  net.minecraft.client.particle.CrackParticle$ItemFactory
 *  net.minecraft.client.particle.CrackParticle$SlimeballFactory
 *  net.minecraft.client.particle.CrackParticle$SnowballFactory
 *  net.minecraft.client.particle.CurrentDownParticle$Factory
 *  net.minecraft.client.particle.DamageParticle$DefaultFactory
 *  net.minecraft.client.particle.DamageParticle$EnchantedHitFactory
 *  net.minecraft.client.particle.DamageParticle$Factory
 *  net.minecraft.client.particle.DragonBreathParticle$Factory
 *  net.minecraft.client.particle.DustColorTransitionParticle$Factory
 *  net.minecraft.client.particle.DustPlumeParticle$Factory
 *  net.minecraft.client.particle.ElderGuardianParticle$Factory
 *  net.minecraft.client.particle.EmotionParticle$AngryVillagerFactory
 *  net.minecraft.client.particle.EmotionParticle$HeartFactory
 *  net.minecraft.client.particle.EndRodParticle$Factory
 *  net.minecraft.client.particle.ExplosionEmitterParticle$Factory
 *  net.minecraft.client.particle.ExplosionLargeParticle$Factory
 *  net.minecraft.client.particle.ExplosionSmokeParticle$Factory
 *  net.minecraft.client.particle.FireSmokeParticle$Factory
 *  net.minecraft.client.particle.FireflyParticle$Factory
 *  net.minecraft.client.particle.FireworksSparkParticle$ExplosionFactory
 *  net.minecraft.client.particle.FireworksSparkParticle$FlashFactory
 *  net.minecraft.client.particle.FishingParticle$Factory
 *  net.minecraft.client.particle.FlameParticle$Factory
 *  net.minecraft.client.particle.FlameParticle$SmallFactory
 *  net.minecraft.client.particle.GlowParticle$ElectricSparkFactory
 *  net.minecraft.client.particle.GlowParticle$GlowFactory
 *  net.minecraft.client.particle.GlowParticle$ScrapeFactory
 *  net.minecraft.client.particle.GlowParticle$WaxOffFactory
 *  net.minecraft.client.particle.GlowParticle$WaxOnFactory
 *  net.minecraft.client.particle.GustEmitterParticle$Factory
 *  net.minecraft.client.particle.GustParticle$Factory
 *  net.minecraft.client.particle.GustParticle$SmallGustFactory
 *  net.minecraft.client.particle.LargeFireSmokeParticle$Factory
 *  net.minecraft.client.particle.LavaEmberParticle$Factory
 *  net.minecraft.client.particle.LeavesParticle$CherryLeavesFactory
 *  net.minecraft.client.particle.LeavesParticle$PaleOakLeavesFactory
 *  net.minecraft.client.particle.LeavesParticle$TintedLeavesFactory
 *  net.minecraft.client.particle.NoteParticle$Factory
 *  net.minecraft.client.particle.OminousSpawningParticle$Factory
 *  net.minecraft.client.particle.ParticleFactory
 *  net.minecraft.client.particle.ParticleSpriteManager
 *  net.minecraft.client.particle.ParticleSpriteManager$ReloadResult
 *  net.minecraft.client.particle.ParticleSpriteManager$SimpleSpriteProvider
 *  net.minecraft.client.particle.ParticleSpriteManager$SpriteAwareFactory
 *  net.minecraft.client.particle.ParticleTextureData
 *  net.minecraft.client.particle.PortalParticle$Factory
 *  net.minecraft.client.particle.RainSplashParticle$Factory
 *  net.minecraft.client.particle.RedDustParticle$Factory
 *  net.minecraft.client.particle.ReversePortalParticle$Factory
 *  net.minecraft.client.particle.SculkChargeParticle$Factory
 *  net.minecraft.client.particle.SculkChargePopParticle$Factory
 *  net.minecraft.client.particle.ShriekParticle$Factory
 *  net.minecraft.client.particle.SnowflakeParticle$Factory
 *  net.minecraft.client.particle.SonicBoomParticle$Factory
 *  net.minecraft.client.particle.SoulParticle$Factory
 *  net.minecraft.client.particle.SoulParticle$SculkSoulFactory
 *  net.minecraft.client.particle.SpellParticle$DefaultFactory
 *  net.minecraft.client.particle.SpellParticle$EntityFactory
 *  net.minecraft.client.particle.SpellParticle$InstantFactory
 *  net.minecraft.client.particle.SpellParticle$WitchFactory
 *  net.minecraft.client.particle.SpitParticle$Factory
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.particle.SquidInkParticle$Factory
 *  net.minecraft.client.particle.SquidInkParticle$GlowSquidInkFactory
 *  net.minecraft.client.particle.SuspendParticle$DolphinFactory
 *  net.minecraft.client.particle.SuspendParticle$EggCrackFactory
 *  net.minecraft.client.particle.SuspendParticle$Factory
 *  net.minecraft.client.particle.SuspendParticle$HappyVillagerFactory
 *  net.minecraft.client.particle.SuspendParticle$MyceliumFactory
 *  net.minecraft.client.particle.SweepAttackParticle$Factory
 *  net.minecraft.client.particle.TotemParticle$Factory
 *  net.minecraft.client.particle.TrailParticle$Factory
 *  net.minecraft.client.particle.TrialSpawnerDetectionParticle$Factory
 *  net.minecraft.client.particle.VibrationParticle$Factory
 *  net.minecraft.client.particle.WaterBubbleParticle$Factory
 *  net.minecraft.client.particle.WaterSplashParticle$SplashFactory
 *  net.minecraft.client.particle.WaterSuspendParticle$CrimsonSporeFactory
 *  net.minecraft.client.particle.WaterSuspendParticle$SporeBlossomAirFactory
 *  net.minecraft.client.particle.WaterSuspendParticle$UnderwaterFactory
 *  net.minecraft.client.particle.WaterSuspendParticle$WarpedSporeFactory
 *  net.minecraft.client.particle.WhiteAshParticle$Factory
 *  net.minecraft.client.particle.WhiteSmokeParticle$Factory
 *  net.minecraft.client.texture.AtlasManager
 *  net.minecraft.client.texture.AtlasManager$Stitch
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteLoader$StitchResult
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleType
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.Registries
 *  net.minecraft.resource.Resource
 *  net.minecraft.resource.ResourceFinder
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.ResourceReloader
 *  net.minecraft.resource.ResourceReloader$Store
 *  net.minecraft.resource.ResourceReloader$Synchronizer
 *  net.minecraft.util.Atlases
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.JsonHelper
 *  net.minecraft.util.Util
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.util.profiler.Profilers
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.particle;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.BlockFallingDustParticle;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.BlockMarkerParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.CloudParticle;
import net.minecraft.client.particle.ConnectionParticle;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.CurrentDownParticle;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.DustColorTransitionParticle;
import net.minecraft.client.particle.DustPlumeParticle;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.particle.EmotionParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplosionEmitterParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.ExplosionSmokeParticle;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.FireflyParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.FishingParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.GustEmitterParticle;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.client.particle.LargeFireSmokeParticle;
import net.minecraft.client.particle.LavaEmberParticle;
import net.minecraft.client.particle.LeavesParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.OminousSpawningParticle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleSpriteManager;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.ReversePortalParticle;
import net.minecraft.client.particle.SculkChargeParticle;
import net.minecraft.client.particle.SculkChargePopParticle;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.TrailParticle;
import net.minecraft.client.particle.TrialSpawnerDetectionParticle;
import net.minecraft.client.particle.VibrationParticle;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.particle.WhiteAshParticle;
import net.minecraft.client.particle.WhiteSmokeParticle;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ParticleSpriteManager
implements ResourceReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder PARTICLE_RESOURCE_FINDER = ResourceFinder.json((String)"particles");
    private final Map<Identifier, SimpleSpriteProvider> spriteAwareParticleFactories = Maps.newHashMap();
    private final Int2ObjectMap<ParticleFactory<?>> particleFactories = new Int2ObjectOpenHashMap();
    private @Nullable Runnable onPreparedTask;

    public ParticleSpriteManager() {
        this.init();
    }

    public void setOnPreparedTask(Runnable onPreparedTask) {
        this.onPreparedTask = onPreparedTask;
    }

    private void init() {
        this.register((ParticleType)ParticleTypes.ANGRY_VILLAGER, EmotionParticle.AngryVillagerFactory::new);
        this.register(ParticleTypes.BLOCK_MARKER, (ParticleFactory)new BlockMarkerParticle.Factory());
        this.register(ParticleTypes.BLOCK, (ParticleFactory)new BlockDustParticle.Factory());
        this.register((ParticleType)ParticleTypes.BUBBLE, WaterBubbleParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosySmokeFactory::new);
        this.register((ParticleType)ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalSmokeFactory::new);
        this.register((ParticleType)ParticleTypes.CLOUD, CloudParticle.CloudFactory::new);
        this.register((ParticleType)ParticleTypes.COMPOSTER, SuspendParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.COPPER_FIRE_FLAME, FlameParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.CRIT, DamageParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.DAMAGE_INDICATOR, DamageParticle.DefaultFactory::new);
        this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.DOLPHIN, SuspendParticle.DolphinFactory::new);
        this.register((ParticleType)ParticleTypes.DRIPPING_LAVA, BlockLeakParticle.DrippingLavaFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_LAVA, BlockLeakParticle.FallingLavaFactory::new);
        this.register((ParticleType)ParticleTypes.LANDING_LAVA, BlockLeakParticle.LandingLavaFactory::new);
        this.register((ParticleType)ParticleTypes.DRIPPING_WATER, BlockLeakParticle.DrippingWaterFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_WATER, BlockLeakParticle.FallingWaterFactory::new);
        this.register(ParticleTypes.DUST, RedDustParticle.Factory::new);
        this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Factory::new);
        this.register(ParticleTypes.EFFECT, SpellParticle.InstantFactory::new);
        this.register((ParticleType)ParticleTypes.ELDER_GUARDIAN, (ParticleFactory)new ElderGuardianParticle.Factory());
        this.register((ParticleType)ParticleTypes.ENCHANTED_HIT, DamageParticle.EnchantedHitFactory::new);
        this.register((ParticleType)ParticleTypes.ENCHANT, ConnectionParticle.EnchantFactory::new);
        this.register((ParticleType)ParticleTypes.END_ROD, EndRodParticle.Factory::new);
        this.register(ParticleTypes.ENTITY_EFFECT, SpellParticle.EntityFactory::new);
        this.register((ParticleType)ParticleTypes.EXPLOSION_EMITTER, (ParticleFactory)new ExplosionEmitterParticle.Factory());
        this.register((ParticleType)ParticleTypes.EXPLOSION, ExplosionLargeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SONIC_BOOM, SonicBoomParticle.Factory::new);
        this.register(ParticleTypes.FALLING_DUST, BlockFallingDustParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.GUST, GustParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SMALL_GUST, GustParticle.SmallGustFactory::new);
        this.register((ParticleType)ParticleTypes.GUST_EMITTER_LARGE, (ParticleFactory)new GustEmitterParticle.Factory(3.0, 7, 0));
        this.register((ParticleType)ParticleTypes.GUST_EMITTER_SMALL, (ParticleFactory)new GustEmitterParticle.Factory(1.0, 3, 2));
        this.register((ParticleType)ParticleTypes.FIREWORK, FireworksSparkParticle.ExplosionFactory::new);
        this.register((ParticleType)ParticleTypes.FISHING, FishingParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.FLAME, FlameParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.INFESTED, SpellParticle.DefaultFactory::new);
        this.register((ParticleType)ParticleTypes.SCULK_SOUL, SoulParticle.SculkSoulFactory::new);
        this.register(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SOUL, SoulParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Factory::new);
        this.register(ParticleTypes.FLASH, FireworksSparkParticle.FlashFactory::new);
        this.register((ParticleType)ParticleTypes.HAPPY_VILLAGER, SuspendParticle.HappyVillagerFactory::new);
        this.register((ParticleType)ParticleTypes.HEART, EmotionParticle.HeartFactory::new);
        this.register(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
        this.register(ParticleTypes.ITEM, (ParticleFactory)new CrackParticle.ItemFactory());
        this.register((ParticleType)ParticleTypes.ITEM_SLIME, (ParticleFactory)new CrackParticle.SlimeballFactory());
        this.register((ParticleType)ParticleTypes.ITEM_COBWEB, (ParticleFactory)new CrackParticle.CobwebFactory());
        this.register((ParticleType)ParticleTypes.ITEM_SNOWBALL, (ParticleFactory)new CrackParticle.SnowballFactory());
        this.register((ParticleType)ParticleTypes.LARGE_SMOKE, LargeFireSmokeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.LAVA, LavaEmberParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.MYCELIUM, SuspendParticle.MyceliumFactory::new);
        this.register((ParticleType)ParticleTypes.NAUTILUS, ConnectionParticle.NautilusFactory::new);
        this.register((ParticleType)ParticleTypes.NOTE, NoteParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.POOF, ExplosionSmokeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.PORTAL, PortalParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.RAIN, RainSplashParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SMOKE, FireSmokeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.WHITE_SMOKE, WhiteSmokeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
        this.register((ParticleType)ParticleTypes.SNOWFLAKE, SnowflakeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SPIT, SpitParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.UNDERWATER, WaterSuspendParticle.UnderwaterFactory::new);
        this.register((ParticleType)ParticleTypes.SPLASH, WaterSplashParticle.SplashFactory::new);
        this.register((ParticleType)ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
        this.register((ParticleType)ParticleTypes.DRIPPING_HONEY, BlockLeakParticle.DrippingHoneyFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_HONEY, BlockLeakParticle.FallingHoneyFactory::new);
        this.register((ParticleType)ParticleTypes.LANDING_HONEY, BlockLeakParticle.LandingHoneyFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_NECTAR, BlockLeakParticle.FallingNectarFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_SPORE_BLOSSOM, BlockLeakParticle.FallingSporeBlossomFactory::new);
        this.register((ParticleType)ParticleTypes.SPORE_BLOSSOM_AIR, WaterSuspendParticle.SporeBlossomAirFactory::new);
        this.register((ParticleType)ParticleTypes.ASH, AshParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.CRIMSON_SPORE, WaterSuspendParticle.CrimsonSporeFactory::new);
        this.register((ParticleType)ParticleTypes.WARPED_SPORE, WaterSuspendParticle.WarpedSporeFactory::new);
        this.register((ParticleType)ParticleTypes.DRIPPING_OBSIDIAN_TEAR, BlockLeakParticle.DrippingObsidianTearFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_OBSIDIAN_TEAR, BlockLeakParticle.FallingObsidianTearFactory::new);
        this.register((ParticleType)ParticleTypes.LANDING_OBSIDIAN_TEAR, BlockLeakParticle.LandingObsidianTearFactory::new);
        this.register((ParticleType)ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.WHITE_ASH, WhiteAshParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.SMALL_FLAME, FlameParticle.SmallFactory::new);
        this.register((ParticleType)ParticleTypes.DRIPPING_DRIPSTONE_WATER, BlockLeakParticle.DrippingDripstoneWaterFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_DRIPSTONE_WATER, BlockLeakParticle.FallingDripstoneWaterFactory::new);
        this.register((ParticleType)ParticleTypes.CHERRY_LEAVES, LeavesParticle.CherryLeavesFactory::new);
        this.register((ParticleType)ParticleTypes.PALE_OAK_LEAVES, LeavesParticle.PaleOakLeavesFactory::new);
        this.register(ParticleTypes.TINTED_LEAVES, LeavesParticle.TintedLeavesFactory::new);
        this.register((ParticleType)ParticleTypes.DRIPPING_DRIPSTONE_LAVA, BlockLeakParticle.DrippingDripstoneLavaFactory::new);
        this.register((ParticleType)ParticleTypes.FALLING_DRIPSTONE_LAVA, BlockLeakParticle.FallingDripstoneLavaFactory::new);
        this.register(ParticleTypes.VIBRATION, VibrationParticle.Factory::new);
        this.register(ParticleTypes.TRAIL, TrailParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowSquidInkFactory::new);
        this.register((ParticleType)ParticleTypes.GLOW, GlowParticle.GlowFactory::new);
        this.register((ParticleType)ParticleTypes.WAX_ON, GlowParticle.WaxOnFactory::new);
        this.register((ParticleType)ParticleTypes.WAX_OFF, GlowParticle.WaxOffFactory::new);
        this.register((ParticleType)ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkFactory::new);
        this.register((ParticleType)ParticleTypes.SCRAPE, GlowParticle.ScrapeFactory::new);
        this.register(ParticleTypes.SHRIEK, ShriekParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.EGG_CRACK, SuspendParticle.EggCrackFactory::new);
        this.register((ParticleType)ParticleTypes.DUST_PLUME, DustPlumeParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.TRIAL_SPAWNER_DETECTION, TrialSpawnerDetectionParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS, TrialSpawnerDetectionParticle.Factory::new);
        this.register((ParticleType)ParticleTypes.VAULT_CONNECTION, ConnectionParticle.VaultConnectionFactory::new);
        this.register(ParticleTypes.DUST_PILLAR, (ParticleFactory)new BlockDustParticle.DustPillarFactory());
        this.register((ParticleType)ParticleTypes.RAID_OMEN, SpellParticle.DefaultFactory::new);
        this.register((ParticleType)ParticleTypes.TRIAL_OMEN, SpellParticle.DefaultFactory::new);
        this.register((ParticleType)ParticleTypes.OMINOUS_SPAWNING, OminousSpawningParticle.Factory::new);
        this.register(ParticleTypes.BLOCK_CRUMBLE, (ParticleFactory)new BlockDustParticle.CrumbleFactory());
        this.register((ParticleType)ParticleTypes.FIREFLY, FireflyParticle.Factory::new);
    }

    private <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
        this.particleFactories.put(Registries.PARTICLE_TYPE.getRawId(type), factory);
    }

    private <T extends ParticleEffect> void register(ParticleType<T> type, SpriteAwareFactory<T> factory) {
        SimpleSpriteProvider simpleSpriteProvider = new SimpleSpriteProvider();
        this.spriteAwareParticleFactories.put(Registries.PARTICLE_TYPE.getId(type), simpleSpriteProvider);
        this.particleFactories.put(Registries.PARTICLE_TYPE.getRawId(type), (Object)factory.create((SpriteProvider)simpleSpriteProvider));
    }

    public CompletableFuture<Void> reload(ResourceReloader.Store store, Executor executor, ResourceReloader.Synchronizer synchronizer, Executor executor2) {
        ResourceManager resourceManager = store.getResourceManager();
        CompletionStage completableFuture = CompletableFuture.supplyAsync(() -> PARTICLE_RESOURCE_FINDER.findResources(resourceManager), executor).thenCompose(resources -> {
            ArrayList list = new ArrayList(resources.size());
            resources.forEach((resourceId, resource) -> {
                Identifier identifier = PARTICLE_RESOURCE_FINDER.toResourceId(resourceId);
                list.add(CompletableFuture.supplyAsync(() -> {
                    return new ReloadResult(identifier, this.load(identifier, resource));
                }, executor));
            });
            return Util.combineSafe(list);
        });
        CompletableFuture completableFuture2 = ((AtlasManager.Stitch)store.getOrThrow(AtlasManager.stitchKey)).getPreparations(Atlases.PARTICLES);
        return ((CompletableFuture)CompletableFuture.allOf(new CompletableFuture[]{completableFuture, completableFuture2}).thenCompose(arg_0 -> ((ResourceReloader.Synchronizer)synchronizer).whenPrepared(arg_0))).thenAcceptAsync(arg_0 -> this.method_74298(completableFuture2, (CompletableFuture)completableFuture, arg_0), executor2);
    }

    private Optional<List<Identifier>> load(Identifier id, Resource resource) {
        Optional<List<Identifier>> optional;
        block9: {
            if (!this.spriteAwareParticleFactories.containsKey(id)) {
                LOGGER.debug("Redundant texture list for particle: {}", (Object)id);
                return Optional.empty();
            }
            BufferedReader reader = resource.getReader();
            try {
                ParticleTextureData particleTextureData = ParticleTextureData.load((JsonObject)JsonHelper.deserialize((Reader)reader));
                optional = Optional.of(particleTextureData.getTextureList());
                if (reader == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException iOException) {
                    throw new IllegalStateException("Failed to load description for particle " + String.valueOf(id), iOException);
                }
            }
            ((Reader)reader).close();
        }
        return optional;
    }

    public Int2ObjectMap<ParticleFactory<?>> getParticleFactories() {
        return this.particleFactories;
    }

    private /* synthetic */ void method_74298(CompletableFuture completableFuture, CompletableFuture completableFuture2, Void void_) {
        if (this.onPreparedTask != null) {
            this.onPreparedTask.run();
        }
        Profiler profiler = Profilers.get();
        profiler.push("upload");
        SpriteLoader.StitchResult stitchResult = (SpriteLoader.StitchResult)completableFuture.join();
        profiler.swap("bindSpriteSets");
        HashSet set = new HashSet();
        Sprite sprite = stitchResult.missing();
        ((List)completableFuture2.join()).forEach(reloadResult -> {
            Optional optional = reloadResult.sprites();
            if (optional.isEmpty()) {
                return;
            }
            ArrayList<Sprite> list = new ArrayList<Sprite>();
            for (Identifier identifier : (List)optional.get()) {
                Sprite sprite2 = stitchResult.getSprite(identifier);
                if (sprite2 == null) {
                    set.add(identifier);
                    list.add(sprite);
                    continue;
                }
                list.add(sprite2);
            }
            if (list.isEmpty()) {
                list.add(sprite);
            }
            ((SimpleSpriteProvider)this.spriteAwareParticleFactories.get(reloadResult.id())).setSprites(list);
        });
        if (!set.isEmpty()) {
            LOGGER.warn("Missing particle sprites: {}", (Object)set.stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")));
        }
        profiler.pop();
    }
}

