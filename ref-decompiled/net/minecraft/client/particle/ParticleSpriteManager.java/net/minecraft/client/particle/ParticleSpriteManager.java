/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ParticleSpriteManager
implements ResourceReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder PARTICLE_RESOURCE_FINDER = ResourceFinder.json("particles");
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
        this.register(ParticleTypes.ANGRY_VILLAGER, EmotionParticle.AngryVillagerFactory::new);
        this.register(ParticleTypes.BLOCK_MARKER, new BlockMarkerParticle.Factory());
        this.register(ParticleTypes.BLOCK, new BlockDustParticle.Factory());
        this.register(ParticleTypes.BUBBLE, WaterBubbleParticle.Factory::new);
        this.register(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Factory::new);
        this.register(ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
        this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosySmokeFactory::new);
        this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalSmokeFactory::new);
        this.register(ParticleTypes.CLOUD, CloudParticle.CloudFactory::new);
        this.register(ParticleTypes.COMPOSTER, SuspendParticle.Factory::new);
        this.register(ParticleTypes.COPPER_FIRE_FLAME, FlameParticle.Factory::new);
        this.register(ParticleTypes.CRIT, DamageParticle.Factory::new);
        this.register(ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
        this.register(ParticleTypes.DAMAGE_INDICATOR, DamageParticle.DefaultFactory::new);
        this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
        this.register(ParticleTypes.DOLPHIN, SuspendParticle.DolphinFactory::new);
        this.register(ParticleTypes.DRIPPING_LAVA, BlockLeakParticle.DrippingLavaFactory::new);
        this.register(ParticleTypes.FALLING_LAVA, BlockLeakParticle.FallingLavaFactory::new);
        this.register(ParticleTypes.LANDING_LAVA, BlockLeakParticle.LandingLavaFactory::new);
        this.register(ParticleTypes.DRIPPING_WATER, BlockLeakParticle.DrippingWaterFactory::new);
        this.register(ParticleTypes.FALLING_WATER, BlockLeakParticle.FallingWaterFactory::new);
        this.register(ParticleTypes.DUST, RedDustParticle.Factory::new);
        this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Factory::new);
        this.register(ParticleTypes.EFFECT, SpellParticle.InstantFactory::new);
        this.register(ParticleTypes.ELDER_GUARDIAN, new ElderGuardianParticle.Factory());
        this.register(ParticleTypes.ENCHANTED_HIT, DamageParticle.EnchantedHitFactory::new);
        this.register(ParticleTypes.ENCHANT, ConnectionParticle.EnchantFactory::new);
        this.register(ParticleTypes.END_ROD, EndRodParticle.Factory::new);
        this.register(ParticleTypes.ENTITY_EFFECT, SpellParticle.EntityFactory::new);
        this.register(ParticleTypes.EXPLOSION_EMITTER, new ExplosionEmitterParticle.Factory());
        this.register(ParticleTypes.EXPLOSION, ExplosionLargeParticle.Factory::new);
        this.register(ParticleTypes.SONIC_BOOM, SonicBoomParticle.Factory::new);
        this.register(ParticleTypes.FALLING_DUST, BlockFallingDustParticle.Factory::new);
        this.register(ParticleTypes.GUST, GustParticle.Factory::new);
        this.register(ParticleTypes.SMALL_GUST, GustParticle.SmallGustFactory::new);
        this.register(ParticleTypes.GUST_EMITTER_LARGE, new GustEmitterParticle.Factory(3.0, 7, 0));
        this.register(ParticleTypes.GUST_EMITTER_SMALL, new GustEmitterParticle.Factory(1.0, 3, 2));
        this.register(ParticleTypes.FIREWORK, FireworksSparkParticle.ExplosionFactory::new);
        this.register(ParticleTypes.FISHING, FishingParticle.Factory::new);
        this.register(ParticleTypes.FLAME, FlameParticle.Factory::new);
        this.register(ParticleTypes.INFESTED, SpellParticle.DefaultFactory::new);
        this.register(ParticleTypes.SCULK_SOUL, SoulParticle.SculkSoulFactory::new);
        this.register(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Factory::new);
        this.register(ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Factory::new);
        this.register(ParticleTypes.SOUL, SoulParticle.Factory::new);
        this.register(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Factory::new);
        this.register(ParticleTypes.FLASH, FireworksSparkParticle.FlashFactory::new);
        this.register(ParticleTypes.HAPPY_VILLAGER, SuspendParticle.HappyVillagerFactory::new);
        this.register(ParticleTypes.HEART, EmotionParticle.HeartFactory::new);
        this.register(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
        this.register(ParticleTypes.ITEM, new CrackParticle.ItemFactory());
        this.register(ParticleTypes.ITEM_SLIME, new CrackParticle.SlimeballFactory());
        this.register(ParticleTypes.ITEM_COBWEB, new CrackParticle.CobwebFactory());
        this.register(ParticleTypes.ITEM_SNOWBALL, new CrackParticle.SnowballFactory());
        this.register(ParticleTypes.LARGE_SMOKE, LargeFireSmokeParticle.Factory::new);
        this.register(ParticleTypes.LAVA, LavaEmberParticle.Factory::new);
        this.register(ParticleTypes.MYCELIUM, SuspendParticle.MyceliumFactory::new);
        this.register(ParticleTypes.NAUTILUS, ConnectionParticle.NautilusFactory::new);
        this.register(ParticleTypes.NOTE, NoteParticle.Factory::new);
        this.register(ParticleTypes.POOF, ExplosionSmokeParticle.Factory::new);
        this.register(ParticleTypes.PORTAL, PortalParticle.Factory::new);
        this.register(ParticleTypes.RAIN, RainSplashParticle.Factory::new);
        this.register(ParticleTypes.SMOKE, FireSmokeParticle.Factory::new);
        this.register(ParticleTypes.WHITE_SMOKE, WhiteSmokeParticle.Factory::new);
        this.register(ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
        this.register(ParticleTypes.SNOWFLAKE, SnowflakeParticle.Factory::new);
        this.register(ParticleTypes.SPIT, SpitParticle.Factory::new);
        this.register(ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
        this.register(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Factory::new);
        this.register(ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
        this.register(ParticleTypes.UNDERWATER, WaterSuspendParticle.UnderwaterFactory::new);
        this.register(ParticleTypes.SPLASH, WaterSplashParticle.SplashFactory::new);
        this.register(ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
        this.register(ParticleTypes.DRIPPING_HONEY, BlockLeakParticle.DrippingHoneyFactory::new);
        this.register(ParticleTypes.FALLING_HONEY, BlockLeakParticle.FallingHoneyFactory::new);
        this.register(ParticleTypes.LANDING_HONEY, BlockLeakParticle.LandingHoneyFactory::new);
        this.register(ParticleTypes.FALLING_NECTAR, BlockLeakParticle.FallingNectarFactory::new);
        this.register(ParticleTypes.FALLING_SPORE_BLOSSOM, BlockLeakParticle.FallingSporeBlossomFactory::new);
        this.register(ParticleTypes.SPORE_BLOSSOM_AIR, WaterSuspendParticle.SporeBlossomAirFactory::new);
        this.register(ParticleTypes.ASH, AshParticle.Factory::new);
        this.register(ParticleTypes.CRIMSON_SPORE, WaterSuspendParticle.CrimsonSporeFactory::new);
        this.register(ParticleTypes.WARPED_SPORE, WaterSuspendParticle.WarpedSporeFactory::new);
        this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, BlockLeakParticle.DrippingObsidianTearFactory::new);
        this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, BlockLeakParticle.FallingObsidianTearFactory::new);
        this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, BlockLeakParticle.LandingObsidianTearFactory::new);
        this.register(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.Factory::new);
        this.register(ParticleTypes.WHITE_ASH, WhiteAshParticle.Factory::new);
        this.register(ParticleTypes.SMALL_FLAME, FlameParticle.SmallFactory::new);
        this.register(ParticleTypes.DRIPPING_DRIPSTONE_WATER, BlockLeakParticle.DrippingDripstoneWaterFactory::new);
        this.register(ParticleTypes.FALLING_DRIPSTONE_WATER, BlockLeakParticle.FallingDripstoneWaterFactory::new);
        this.register(ParticleTypes.CHERRY_LEAVES, LeavesParticle.CherryLeavesFactory::new);
        this.register(ParticleTypes.PALE_OAK_LEAVES, LeavesParticle.PaleOakLeavesFactory::new);
        this.register(ParticleTypes.TINTED_LEAVES, LeavesParticle.TintedLeavesFactory::new);
        this.register(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, BlockLeakParticle.DrippingDripstoneLavaFactory::new);
        this.register(ParticleTypes.FALLING_DRIPSTONE_LAVA, BlockLeakParticle.FallingDripstoneLavaFactory::new);
        this.register(ParticleTypes.VIBRATION, VibrationParticle.Factory::new);
        this.register(ParticleTypes.TRAIL, TrailParticle.Factory::new);
        this.register(ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowSquidInkFactory::new);
        this.register(ParticleTypes.GLOW, GlowParticle.GlowFactory::new);
        this.register(ParticleTypes.WAX_ON, GlowParticle.WaxOnFactory::new);
        this.register(ParticleTypes.WAX_OFF, GlowParticle.WaxOffFactory::new);
        this.register(ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkFactory::new);
        this.register(ParticleTypes.SCRAPE, GlowParticle.ScrapeFactory::new);
        this.register(ParticleTypes.SHRIEK, ShriekParticle.Factory::new);
        this.register(ParticleTypes.EGG_CRACK, SuspendParticle.EggCrackFactory::new);
        this.register(ParticleTypes.DUST_PLUME, DustPlumeParticle.Factory::new);
        this.register(ParticleTypes.TRIAL_SPAWNER_DETECTION, TrialSpawnerDetectionParticle.Factory::new);
        this.register(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS, TrialSpawnerDetectionParticle.Factory::new);
        this.register(ParticleTypes.VAULT_CONNECTION, ConnectionParticle.VaultConnectionFactory::new);
        this.register(ParticleTypes.DUST_PILLAR, new BlockDustParticle.DustPillarFactory());
        this.register(ParticleTypes.RAID_OMEN, SpellParticle.DefaultFactory::new);
        this.register(ParticleTypes.TRIAL_OMEN, SpellParticle.DefaultFactory::new);
        this.register(ParticleTypes.OMINOUS_SPAWNING, OminousSpawningParticle.Factory::new);
        this.register(ParticleTypes.BLOCK_CRUMBLE, new BlockDustParticle.CrumbleFactory());
        this.register(ParticleTypes.FIREFLY, FireflyParticle.Factory::new);
    }

    private <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
        this.particleFactories.put(Registries.PARTICLE_TYPE.getRawId(type), factory);
    }

    private <T extends ParticleEffect> void register(ParticleType<T> type, SpriteAwareFactory<T> factory) {
        SimpleSpriteProvider simpleSpriteProvider = new SimpleSpriteProvider();
        this.spriteAwareParticleFactories.put(Registries.PARTICLE_TYPE.getId(type), simpleSpriteProvider);
        this.particleFactories.put(Registries.PARTICLE_TYPE.getRawId(type), factory.create(simpleSpriteProvider));
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Store store, Executor executor, ResourceReloader.Synchronizer synchronizer, Executor executor2) {
        ResourceManager resourceManager = store.getResourceManager();
        CompletionStage completableFuture = CompletableFuture.supplyAsync(() -> PARTICLE_RESOURCE_FINDER.findResources(resourceManager), executor).thenCompose(resources -> {
            ArrayList list = new ArrayList(resources.size());
            resources.forEach((resourceId, resource) -> {
                Identifier identifier = PARTICLE_RESOURCE_FINDER.toResourceId((Identifier)resourceId);
                list.add(CompletableFuture.supplyAsync(() -> {
                    @Environment(value=EnvType.CLIENT)
                    record ReloadResult(Identifier id, Optional<List<Identifier>> sprites) {
                    }
                    return new ReloadResult(identifier, this.load(identifier, (Resource)resource));
                }, executor));
            });
            return Util.combineSafe(list);
        });
        CompletableFuture<SpriteLoader.StitchResult> completableFuture2 = store.getOrThrow(AtlasManager.stitchKey).getPreparations(Atlases.PARTICLES);
        return ((CompletableFuture)CompletableFuture.allOf(new CompletableFuture[]{completableFuture, completableFuture2}).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(arg_0 -> this.method_74298(completableFuture2, (CompletableFuture)completableFuture, arg_0), executor2);
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
                ParticleTextureData particleTextureData = ParticleTextureData.load(JsonHelper.deserialize(reader));
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
            Optional<List<Identifier>> optional = reloadResult.sprites();
            if (optional.isEmpty()) {
                return;
            }
            ArrayList<Sprite> list = new ArrayList<Sprite>();
            for (Identifier identifier : optional.get()) {
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
            this.spriteAwareParticleFactories.get(reloadResult.id()).setSprites(list);
        });
        if (!set.isEmpty()) {
            LOGGER.warn("Missing particle sprites: {}", (Object)set.stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")));
        }
        profiler.pop();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface SpriteAwareFactory<T extends ParticleEffect> {
        public ParticleFactory<T> create(SpriteProvider var1);
    }

    @Environment(value=EnvType.CLIENT)
    static class SimpleSpriteProvider
    implements SpriteProvider {
        private List<Sprite> sprites;

        SimpleSpriteProvider() {
        }

        @Override
        public Sprite getSprite(int age, int maxAge) {
            return this.sprites.get(age * (this.sprites.size() - 1) / maxAge);
        }

        @Override
        public Sprite getSprite(Random random) {
            return this.sprites.get(random.nextInt(this.sprites.size()));
        }

        @Override
        public Sprite getFirst() {
            return this.sprites.getFirst();
        }

        public void setSprites(List<Sprite> sprites) {
            this.sprites = ImmutableList.copyOf(sprites);
        }
    }
}
