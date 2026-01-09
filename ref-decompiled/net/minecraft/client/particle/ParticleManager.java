package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.atlas.Atlases;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleGroup;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ParticleManager implements ResourceReloader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceFinder FINDER = ResourceFinder.json("particles");
   private static final int MAX_PARTICLE_COUNT = 16384;
   private static final List PARTICLE_TEXTURE_SHEETS;
   protected ClientWorld world;
   private final Map particles = Maps.newIdentityHashMap();
   private final Queue newEmitterParticles = Queues.newArrayDeque();
   private final Random random = Random.create();
   private final Int2ObjectMap factories = new Int2ObjectOpenHashMap();
   private final Queue newParticles = Queues.newArrayDeque();
   private final Map spriteAwareFactories = Maps.newHashMap();
   private final SpriteAtlasTexture particleAtlasTexture;
   private final Object2IntOpenHashMap groupCounts = new Object2IntOpenHashMap();

   public ParticleManager(ClientWorld world, TextureManager textureManager) {
      this.particleAtlasTexture = new SpriteAtlasTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
      textureManager.registerTexture(this.particleAtlasTexture.getId(), (AbstractTexture)this.particleAtlasTexture);
      this.world = world;
      this.registerDefaultFactories();
   }

   private void registerDefaultFactories() {
      this.registerFactory(ParticleTypes.ANGRY_VILLAGER, (SpriteAwareFactory)(EmotionParticle.AngryVillagerFactory::new));
      this.registerFactory(ParticleTypes.BLOCK_MARKER, (ParticleFactory)(new BlockMarkerParticle.Factory()));
      this.registerFactory(ParticleTypes.BLOCK, (ParticleFactory)(new BlockDustParticle.Factory()));
      this.registerFactory(ParticleTypes.BUBBLE, (SpriteAwareFactory)(WaterBubbleParticle.Factory::new));
      this.registerFactory(ParticleTypes.BUBBLE_COLUMN_UP, (SpriteAwareFactory)(BubbleColumnUpParticle.Factory::new));
      this.registerFactory(ParticleTypes.BUBBLE_POP, (SpriteAwareFactory)(BubblePopParticle.Factory::new));
      this.registerFactory(ParticleTypes.CAMPFIRE_COSY_SMOKE, (SpriteAwareFactory)(CampfireSmokeParticle.CosySmokeFactory::new));
      this.registerFactory(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, (SpriteAwareFactory)(CampfireSmokeParticle.SignalSmokeFactory::new));
      this.registerFactory(ParticleTypes.CLOUD, (SpriteAwareFactory)(CloudParticle.CloudFactory::new));
      this.registerFactory(ParticleTypes.COMPOSTER, (SpriteAwareFactory)(SuspendParticle.Factory::new));
      this.registerFactory(ParticleTypes.CRIT, (SpriteAwareFactory)(DamageParticle.Factory::new));
      this.registerFactory(ParticleTypes.CURRENT_DOWN, (SpriteAwareFactory)(CurrentDownParticle.Factory::new));
      this.registerFactory(ParticleTypes.DAMAGE_INDICATOR, (SpriteAwareFactory)(DamageParticle.DefaultFactory::new));
      this.registerFactory(ParticleTypes.DRAGON_BREATH, (SpriteAwareFactory)(DragonBreathParticle.Factory::new));
      this.registerFactory(ParticleTypes.DOLPHIN, (SpriteAwareFactory)(SuspendParticle.DolphinFactory::new));
      this.registerBlockLeakFactory(ParticleTypes.DRIPPING_LAVA, BlockLeakParticle::createDrippingLava);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_LAVA, BlockLeakParticle::createFallingLava);
      this.registerBlockLeakFactory(ParticleTypes.LANDING_LAVA, BlockLeakParticle::createLandingLava);
      this.registerBlockLeakFactory(ParticleTypes.DRIPPING_WATER, BlockLeakParticle::createDrippingWater);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_WATER, BlockLeakParticle::createFallingWater);
      this.registerFactory(ParticleTypes.DUST, RedDustParticle.Factory::new);
      this.registerFactory(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Factory::new);
      this.registerFactory(ParticleTypes.EFFECT, (SpriteAwareFactory)(SpellParticle.DefaultFactory::new));
      this.registerFactory(ParticleTypes.ELDER_GUARDIAN, (ParticleFactory)(new ElderGuardianAppearanceParticle.Factory()));
      this.registerFactory(ParticleTypes.ENCHANTED_HIT, (SpriteAwareFactory)(DamageParticle.EnchantedHitFactory::new));
      this.registerFactory(ParticleTypes.ENCHANT, (SpriteAwareFactory)(ConnectionParticle.EnchantFactory::new));
      this.registerFactory(ParticleTypes.END_ROD, (SpriteAwareFactory)(EndRodParticle.Factory::new));
      this.registerFactory(ParticleTypes.ENTITY_EFFECT, SpellParticle.EntityFactory::new);
      this.registerFactory(ParticleTypes.EXPLOSION_EMITTER, (ParticleFactory)(new ExplosionEmitterParticle.Factory()));
      this.registerFactory(ParticleTypes.EXPLOSION, (SpriteAwareFactory)(ExplosionLargeParticle.Factory::new));
      this.registerFactory(ParticleTypes.SONIC_BOOM, (SpriteAwareFactory)(SonicBoomParticle.Factory::new));
      this.registerFactory(ParticleTypes.FALLING_DUST, BlockFallingDustParticle.Factory::new);
      this.registerFactory(ParticleTypes.GUST, (SpriteAwareFactory)(GustParticle.Factory::new));
      this.registerFactory(ParticleTypes.SMALL_GUST, (SpriteAwareFactory)(GustParticle.SmallGustFactory::new));
      this.registerFactory(ParticleTypes.GUST_EMITTER_LARGE, (ParticleFactory)(new GustEmitterParticle.Factory(3.0, 7, 0)));
      this.registerFactory(ParticleTypes.GUST_EMITTER_SMALL, (ParticleFactory)(new GustEmitterParticle.Factory(1.0, 3, 2)));
      this.registerFactory(ParticleTypes.FIREWORK, (SpriteAwareFactory)(FireworksSparkParticle.ExplosionFactory::new));
      this.registerFactory(ParticleTypes.FISHING, (SpriteAwareFactory)(FishingParticle.Factory::new));
      this.registerFactory(ParticleTypes.FLAME, (SpriteAwareFactory)(FlameParticle.Factory::new));
      this.registerFactory(ParticleTypes.INFESTED, (SpriteAwareFactory)(SpellParticle.DefaultFactory::new));
      this.registerFactory(ParticleTypes.SCULK_SOUL, (SpriteAwareFactory)(SoulParticle.SculkSoulFactory::new));
      this.registerFactory(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Factory::new);
      this.registerFactory(ParticleTypes.SCULK_CHARGE_POP, (SpriteAwareFactory)(SculkChargePopParticle.Factory::new));
      this.registerFactory(ParticleTypes.SOUL, (SpriteAwareFactory)(SoulParticle.Factory::new));
      this.registerFactory(ParticleTypes.SOUL_FIRE_FLAME, (SpriteAwareFactory)(FlameParticle.Factory::new));
      this.registerFactory(ParticleTypes.FLASH, (SpriteAwareFactory)(FireworksSparkParticle.FlashFactory::new));
      this.registerFactory(ParticleTypes.HAPPY_VILLAGER, (SpriteAwareFactory)(SuspendParticle.HappyVillagerFactory::new));
      this.registerFactory(ParticleTypes.HEART, (SpriteAwareFactory)(EmotionParticle.HeartFactory::new));
      this.registerFactory(ParticleTypes.INSTANT_EFFECT, (SpriteAwareFactory)(SpellParticle.InstantFactory::new));
      this.registerFactory(ParticleTypes.ITEM, (ParticleFactory)(new CrackParticle.ItemFactory()));
      this.registerFactory(ParticleTypes.ITEM_SLIME, (ParticleFactory)(new CrackParticle.SlimeballFactory()));
      this.registerFactory(ParticleTypes.ITEM_COBWEB, (ParticleFactory)(new CrackParticle.CobwebFactory()));
      this.registerFactory(ParticleTypes.ITEM_SNOWBALL, (ParticleFactory)(new CrackParticle.SnowballFactory()));
      this.registerFactory(ParticleTypes.LARGE_SMOKE, (SpriteAwareFactory)(LargeFireSmokeParticle.Factory::new));
      this.registerFactory(ParticleTypes.LAVA, (SpriteAwareFactory)(LavaEmberParticle.Factory::new));
      this.registerFactory(ParticleTypes.MYCELIUM, (SpriteAwareFactory)(SuspendParticle.MyceliumFactory::new));
      this.registerFactory(ParticleTypes.NAUTILUS, (SpriteAwareFactory)(ConnectionParticle.NautilusFactory::new));
      this.registerFactory(ParticleTypes.NOTE, (SpriteAwareFactory)(NoteParticle.Factory::new));
      this.registerFactory(ParticleTypes.POOF, (SpriteAwareFactory)(ExplosionSmokeParticle.Factory::new));
      this.registerFactory(ParticleTypes.PORTAL, (SpriteAwareFactory)(PortalParticle.Factory::new));
      this.registerFactory(ParticleTypes.RAIN, (SpriteAwareFactory)(RainSplashParticle.Factory::new));
      this.registerFactory(ParticleTypes.SMOKE, (SpriteAwareFactory)(FireSmokeParticle.Factory::new));
      this.registerFactory(ParticleTypes.WHITE_SMOKE, (SpriteAwareFactory)(WhiteSmokeParticle.Factory::new));
      this.registerFactory(ParticleTypes.SNEEZE, (SpriteAwareFactory)(CloudParticle.SneezeFactory::new));
      this.registerFactory(ParticleTypes.SNOWFLAKE, (SpriteAwareFactory)(SnowflakeParticle.Factory::new));
      this.registerFactory(ParticleTypes.SPIT, (SpriteAwareFactory)(SpitParticle.Factory::new));
      this.registerFactory(ParticleTypes.SWEEP_ATTACK, (SpriteAwareFactory)(SweepAttackParticle.Factory::new));
      this.registerFactory(ParticleTypes.TOTEM_OF_UNDYING, (SpriteAwareFactory)(TotemParticle.Factory::new));
      this.registerFactory(ParticleTypes.SQUID_INK, (SpriteAwareFactory)(SquidInkParticle.Factory::new));
      this.registerFactory(ParticleTypes.UNDERWATER, (SpriteAwareFactory)(WaterSuspendParticle.UnderwaterFactory::new));
      this.registerFactory(ParticleTypes.SPLASH, (SpriteAwareFactory)(WaterSplashParticle.SplashFactory::new));
      this.registerFactory(ParticleTypes.WITCH, (SpriteAwareFactory)(SpellParticle.WitchFactory::new));
      this.registerBlockLeakFactory(ParticleTypes.DRIPPING_HONEY, BlockLeakParticle::createDrippingHoney);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_HONEY, BlockLeakParticle::createFallingHoney);
      this.registerBlockLeakFactory(ParticleTypes.LANDING_HONEY, BlockLeakParticle::createLandingHoney);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_NECTAR, BlockLeakParticle::createFallingNectar);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_SPORE_BLOSSOM, BlockLeakParticle::createFallingSporeBlossom);
      this.registerFactory(ParticleTypes.SPORE_BLOSSOM_AIR, (SpriteAwareFactory)(WaterSuspendParticle.SporeBlossomAirFactory::new));
      this.registerFactory(ParticleTypes.ASH, (SpriteAwareFactory)(AshParticle.Factory::new));
      this.registerFactory(ParticleTypes.CRIMSON_SPORE, (SpriteAwareFactory)(WaterSuspendParticle.CrimsonSporeFactory::new));
      this.registerFactory(ParticleTypes.WARPED_SPORE, (SpriteAwareFactory)(WaterSuspendParticle.WarpedSporeFactory::new));
      this.registerBlockLeakFactory(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, BlockLeakParticle::createDrippingObsidianTear);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_OBSIDIAN_TEAR, BlockLeakParticle::createFallingObsidianTear);
      this.registerBlockLeakFactory(ParticleTypes.LANDING_OBSIDIAN_TEAR, BlockLeakParticle::createLandingObsidianTear);
      this.registerFactory(ParticleTypes.REVERSE_PORTAL, (SpriteAwareFactory)(ReversePortalParticle.Factory::new));
      this.registerFactory(ParticleTypes.WHITE_ASH, (SpriteAwareFactory)(WhiteAshParticle.Factory::new));
      this.registerFactory(ParticleTypes.SMALL_FLAME, (SpriteAwareFactory)(FlameParticle.SmallFactory::new));
      this.registerBlockLeakFactory(ParticleTypes.DRIPPING_DRIPSTONE_WATER, BlockLeakParticle::createDrippingDripstoneWater);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_DRIPSTONE_WATER, BlockLeakParticle::createFallingDripstoneWater);
      this.registerFactory(ParticleTypes.CHERRY_LEAVES, (SpriteAwareFactory)(LeavesParticle.CherryLeavesFactory::new));
      this.registerFactory(ParticleTypes.PALE_OAK_LEAVES, (SpriteAwareFactory)(LeavesParticle.PaleOakLeavesFactory::new));
      this.registerFactory(ParticleTypes.TINTED_LEAVES, LeavesParticle.TintedLeavesFactory::new);
      this.registerBlockLeakFactory(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, BlockLeakParticle::createDrippingDripstoneLava);
      this.registerBlockLeakFactory(ParticleTypes.FALLING_DRIPSTONE_LAVA, BlockLeakParticle::createFallingDripstoneLava);
      this.registerFactory(ParticleTypes.VIBRATION, VibrationParticle.Factory::new);
      this.registerFactory(ParticleTypes.TRAIL, TrailParticle.Factory::new);
      this.registerFactory(ParticleTypes.GLOW_SQUID_INK, (SpriteAwareFactory)(SquidInkParticle.GlowSquidInkFactory::new));
      this.registerFactory(ParticleTypes.GLOW, (SpriteAwareFactory)(GlowParticle.GlowFactory::new));
      this.registerFactory(ParticleTypes.WAX_ON, (SpriteAwareFactory)(GlowParticle.WaxOnFactory::new));
      this.registerFactory(ParticleTypes.WAX_OFF, (SpriteAwareFactory)(GlowParticle.WaxOffFactory::new));
      this.registerFactory(ParticleTypes.ELECTRIC_SPARK, (SpriteAwareFactory)(GlowParticle.ElectricSparkFactory::new));
      this.registerFactory(ParticleTypes.SCRAPE, (SpriteAwareFactory)(GlowParticle.ScrapeFactory::new));
      this.registerFactory(ParticleTypes.SHRIEK, ShriekParticle.Factory::new);
      this.registerFactory(ParticleTypes.EGG_CRACK, (SpriteAwareFactory)(SuspendParticle.EggCrackFactory::new));
      this.registerFactory(ParticleTypes.DUST_PLUME, (SpriteAwareFactory)(DustPlumeParticle.Factory::new));
      this.registerFactory(ParticleTypes.TRIAL_SPAWNER_DETECTION, (SpriteAwareFactory)(TrialSpawnerDetectionParticle.Factory::new));
      this.registerFactory(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS, (SpriteAwareFactory)(TrialSpawnerDetectionParticle.Factory::new));
      this.registerFactory(ParticleTypes.VAULT_CONNECTION, (SpriteAwareFactory)(ConnectionParticle.VaultConnectionFactory::new));
      this.registerFactory(ParticleTypes.DUST_PILLAR, (ParticleFactory)(new BlockDustParticle.DustPillarFactory()));
      this.registerFactory(ParticleTypes.RAID_OMEN, (SpriteAwareFactory)(SpellParticle.DefaultFactory::new));
      this.registerFactory(ParticleTypes.TRIAL_OMEN, (SpriteAwareFactory)(SpellParticle.DefaultFactory::new));
      this.registerFactory(ParticleTypes.OMINOUS_SPAWNING, (SpriteAwareFactory)(OminousSpawningParticle.Factory::new));
      this.registerFactory(ParticleTypes.BLOCK_CRUMBLE, (ParticleFactory)(new BlockDustParticle.CrumbleFactory()));
      this.registerFactory(ParticleTypes.FIREFLY, (SpriteAwareFactory)(FireflyParticle.Factory::new));
   }

   private void registerFactory(ParticleType type, ParticleFactory factory) {
      this.factories.put(Registries.PARTICLE_TYPE.getRawId(type), factory);
   }

   private void registerBlockLeakFactory(ParticleType type, ParticleFactory.BlockLeakParticleFactory factory) {
      this.registerFactory(type, (spriteBillboardParticle) -> {
         return (type, world, x, y, z, velocityX, velocityY, velocityZ) -> {
            SpriteBillboardParticle spriteBillboardParticlex = factory.createParticle(type, world, x, y, z, velocityX, velocityY, velocityZ);
            if (spriteBillboardParticlex != null) {
               spriteBillboardParticlex.setSprite(spriteBillboardParticle);
            }

            return spriteBillboardParticlex;
         };
      });
   }

   private void registerFactory(ParticleType type, SpriteAwareFactory factory) {
      SimpleSpriteProvider simpleSpriteProvider = new SimpleSpriteProvider();
      this.spriteAwareFactories.put(Registries.PARTICLE_TYPE.getId(type), simpleSpriteProvider);
      this.factories.put(Registries.PARTICLE_TYPE.getRawId(type), factory.create(simpleSpriteProvider));
   }

   public CompletableFuture reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
      CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
         return FINDER.findResources(resourceManager);
      }, executor).thenCompose((particles) -> {
         List list = new ArrayList(particles.size());
         particles.forEach((id, resource) -> {
            Identifier identifier = FINDER.toResourceId(id);
            list.add(CompletableFuture.supplyAsync(() -> {
               @Environment(EnvType.CLIENT)
               record ReloadResult(Identifier id, Optional sprites) {
                  ReloadResult(Identifier identifier, Optional optional) {
                     this.id = identifier;
                     this.sprites = optional;
                  }

                  public Identifier id() {
                     return this.id;
                  }

                  public Optional sprites() {
                     return this.sprites;
                  }
               }

               return new ReloadResult(identifier, this.loadTextureList(identifier, resource));
            }, executor));
         });
         return Util.combineSafe(list);
      });
      CompletableFuture completableFuture2 = SpriteLoader.fromAtlas(this.particleAtlasTexture).load(resourceManager, Atlases.PARTICLES, 0, executor).thenCompose(SpriteLoader.StitchResult::whenComplete);
      CompletableFuture var10000 = CompletableFuture.allOf(completableFuture2, completableFuture);
      Objects.requireNonNull(synchronizer);
      return var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((void_) -> {
         this.clearParticles();
         Profiler profiler = Profilers.get();
         profiler.push("upload");
         SpriteLoader.StitchResult stitchResult = (SpriteLoader.StitchResult)completableFuture2.join();
         this.particleAtlasTexture.upload(stitchResult);
         profiler.swap("bindSpriteSets");
         Set set = new HashSet();
         Sprite sprite = stitchResult.missing();
         ((List)completableFuture.join()).forEach((result) -> {
            Optional optional = result.sprites();
            if (!optional.isEmpty()) {
               List list = new ArrayList();
               Iterator var7 = ((List)optional.get()).iterator();

               while(var7.hasNext()) {
                  Identifier identifier = (Identifier)var7.next();
                  Sprite sprite2 = (Sprite)stitchResult.regions().get(identifier);
                  if (sprite2 == null) {
                     set.add(identifier);
                     list.add(sprite);
                  } else {
                     list.add(sprite2);
                  }
               }

               if (list.isEmpty()) {
                  list.add(sprite);
               }

               ((SimpleSpriteProvider)this.spriteAwareFactories.get(result.id())).setSprites(list);
            }
         });
         if (!set.isEmpty()) {
            LOGGER.warn("Missing particle sprites: {}", set.stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")));
         }

         profiler.pop();
      }, executor2);
   }

   public void clearAtlas() {
      this.particleAtlasTexture.clear();
   }

   private Optional loadTextureList(Identifier id, Resource resource) {
      if (!this.spriteAwareFactories.containsKey(id)) {
         LOGGER.debug("Redundant texture list for particle: {}", id);
         return Optional.empty();
      } else {
         try {
            Reader reader = resource.getReader();

            Optional var5;
            try {
               ParticleTextureData particleTextureData = ParticleTextureData.load(JsonHelper.deserialize((Reader)reader));
               var5 = Optional.of(particleTextureData.getTextureList());
            } catch (Throwable var7) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (reader != null) {
               reader.close();
            }

            return var5;
         } catch (IOException var8) {
            throw new IllegalStateException("Failed to load description for particle " + String.valueOf(id), var8);
         }
      }
   }

   public void addEmitter(Entity entity, ParticleEffect parameters) {
      this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters));
   }

   public void addEmitter(Entity entity, ParticleEffect parameters, int maxAge) {
      this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters, maxAge));
   }

   @Nullable
   public Particle addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      Particle particle = this.createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
      if (particle != null) {
         this.addParticle(particle);
         return particle;
      } else {
         return null;
      }
   }

   @Nullable
   private Particle createParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      ParticleFactory particleFactory = (ParticleFactory)this.factories.get(Registries.PARTICLE_TYPE.getRawId(parameters.getType()));
      return particleFactory == null ? null : particleFactory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
   }

   public void addParticle(Particle particle) {
      Optional optional = particle.getGroup();
      if (optional.isPresent()) {
         if (this.canAdd((ParticleGroup)optional.get())) {
            this.newParticles.add(particle);
            this.addTo((ParticleGroup)optional.get(), 1);
         }
      } else {
         this.newParticles.add(particle);
      }

   }

   public void tick() {
      this.particles.forEach((sheet, queue) -> {
         Profilers.get().push(sheet.toString());
         this.tickParticles(queue);
         Profilers.get().pop();
      });
      if (!this.newEmitterParticles.isEmpty()) {
         List list = Lists.newArrayList();
         Iterator var2 = this.newEmitterParticles.iterator();

         while(var2.hasNext()) {
            EmitterParticle emitterParticle = (EmitterParticle)var2.next();
            emitterParticle.tick();
            if (!emitterParticle.isAlive()) {
               list.add(emitterParticle);
            }
         }

         this.newEmitterParticles.removeAll(list);
      }

      Particle particle;
      if (!this.newParticles.isEmpty()) {
         while((particle = (Particle)this.newParticles.poll()) != null) {
            ((Queue)this.particles.computeIfAbsent(particle.getType(), (sheet) -> {
               return EvictingQueue.create(16384);
            })).add(particle);
         }
      }

   }

   private void tickParticles(Collection particles) {
      if (!particles.isEmpty()) {
         Iterator iterator = particles.iterator();

         while(iterator.hasNext()) {
            Particle particle = (Particle)iterator.next();
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               particle.getGroup().ifPresent((group) -> {
                  this.addTo(group, -1);
               });
               iterator.remove();
            }
         }
      }

   }

   private void addTo(ParticleGroup group, int count) {
      this.groupCounts.addTo(group, count);
   }

   private void tickParticle(Particle particle) {
      try {
         particle.tick();
      } catch (Throwable var5) {
         CrashReport crashReport = CrashReport.create(var5, "Ticking Particle");
         CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
         Objects.requireNonNull(particle);
         crashReportSection.add("Particle", particle::toString);
         ParticleTextureSheet var10002 = particle.getType();
         Objects.requireNonNull(var10002);
         crashReportSection.add("Particle Type", var10002::toString);
         throw new CrashException(crashReport);
      }
   }

   public void renderParticles(Camera camera, float tickProgress, VertexConsumerProvider.Immediate vertexConsumers) {
      Iterator var4 = PARTICLE_TEXTURE_SHEETS.iterator();

      while(var4.hasNext()) {
         ParticleTextureSheet particleTextureSheet = (ParticleTextureSheet)var4.next();
         Queue queue = (Queue)this.particles.get(particleTextureSheet);
         if (queue != null && !queue.isEmpty()) {
            renderParticles(camera, tickProgress, vertexConsumers, particleTextureSheet, queue);
         }
      }

      Queue queue2 = (Queue)this.particles.get(ParticleTextureSheet.CUSTOM);
      if (queue2 != null && !queue2.isEmpty()) {
         renderCustomParticles(camera, tickProgress, vertexConsumers, queue2);
      }

      vertexConsumers.draw();
   }

   private static void renderParticles(Camera camera, float tickProgress, VertexConsumerProvider.Immediate vertexConsumers, ParticleTextureSheet sheet, Queue particles) {
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer((RenderLayer)Objects.requireNonNull(sheet.renderType()));
      Iterator var6 = particles.iterator();

      while(var6.hasNext()) {
         Particle particle = (Particle)var6.next();

         try {
            particle.render(vertexConsumer, camera, tickProgress);
         } catch (Throwable var11) {
            CrashReport crashReport = CrashReport.create(var11, "Rendering Particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
            Objects.requireNonNull(particle);
            crashReportSection.add("Particle", particle::toString);
            Objects.requireNonNull(sheet);
            crashReportSection.add("Particle Type", sheet::toString);
            throw new CrashException(crashReport);
         }
      }

   }

   private static void renderCustomParticles(Camera camera, float tickProgress, VertexConsumerProvider.Immediate vertexConsumers, Queue particles) {
      MatrixStack matrixStack = new MatrixStack();
      Iterator var5 = particles.iterator();

      while(var5.hasNext()) {
         Particle particle = (Particle)var5.next();

         try {
            particle.renderCustom(matrixStack, vertexConsumers, camera, tickProgress);
         } catch (Throwable var10) {
            CrashReport crashReport = CrashReport.create(var10, "Rendering Particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
            Objects.requireNonNull(particle);
            crashReportSection.add("Particle", particle::toString);
            crashReportSection.add("Particle Type", (Object)"Custom");
            throw new CrashException(crashReport);
         }
      }

   }

   public void setWorld(@Nullable ClientWorld world) {
      this.world = world;
      this.clearParticles();
      this.newEmitterParticles.clear();
   }

   public void addBlockBreakParticles(BlockPos pos, BlockState state) {
      if (!state.isAir() && state.hasBlockBreakParticles()) {
         VoxelShape voxelShape = state.getOutlineShape(this.world, pos);
         double d = 0.25;
         voxelShape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double d = Math.min(1.0, maxX - minX);
            double e = Math.min(1.0, maxY - minY);
            double f = Math.min(1.0, maxZ - minZ);
            int i = Math.max(2, MathHelper.ceil(d / 0.25));
            int j = Math.max(2, MathHelper.ceil(e / 0.25));
            int k = Math.max(2, MathHelper.ceil(f / 0.25));

            for(int l = 0; l < i; ++l) {
               for(int m = 0; m < j; ++m) {
                  for(int n = 0; n < k; ++n) {
                     double g = ((double)l + 0.5) / (double)i;
                     double h = ((double)m + 0.5) / (double)j;
                     double o = ((double)n + 0.5) / (double)k;
                     double p = g * d + minX;
                     double q = h * e + minY;
                     double r = o * f + minZ;
                     this.addParticle(new BlockDustParticle(this.world, (double)pos.getX() + p, (double)pos.getY() + q, (double)pos.getZ() + r, g - 0.5, h - 0.5, o - 0.5, state, pos));
                  }
               }
            }

         });
      }
   }

   public void addBlockBreakingParticles(BlockPos pos, Direction direction) {
      BlockState blockState = this.world.getBlockState(pos);
      if (blockState.getRenderType() != BlockRenderType.INVISIBLE && blockState.hasBlockBreakParticles()) {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();
         float f = 0.1F;
         Box box = blockState.getOutlineShape(this.world, pos).getBoundingBox();
         double d = (double)i + this.random.nextDouble() * (box.maxX - box.minX - 0.20000000298023224) + 0.10000000149011612 + box.minX;
         double e = (double)j + this.random.nextDouble() * (box.maxY - box.minY - 0.20000000298023224) + 0.10000000149011612 + box.minY;
         double g = (double)k + this.random.nextDouble() * (box.maxZ - box.minZ - 0.20000000298023224) + 0.10000000149011612 + box.minZ;
         if (direction == Direction.DOWN) {
            e = (double)j + box.minY - 0.10000000149011612;
         }

         if (direction == Direction.UP) {
            e = (double)j + box.maxY + 0.10000000149011612;
         }

         if (direction == Direction.NORTH) {
            g = (double)k + box.minZ - 0.10000000149011612;
         }

         if (direction == Direction.SOUTH) {
            g = (double)k + box.maxZ + 0.10000000149011612;
         }

         if (direction == Direction.WEST) {
            d = (double)i + box.minX - 0.10000000149011612;
         }

         if (direction == Direction.EAST) {
            d = (double)i + box.maxX + 0.10000000149011612;
         }

         this.addParticle((new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState, pos)).move(0.2F).scale(0.6F));
      }
   }

   public String getDebugString() {
      return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
   }

   private boolean canAdd(ParticleGroup group) {
      return this.groupCounts.getInt(group) < group.getMaxCount();
   }

   private void clearParticles() {
      this.particles.clear();
      this.newParticles.clear();
      this.newEmitterParticles.clear();
      this.groupCounts.clear();
   }

   static {
      PARTICLE_TEXTURE_SHEETS = List.of(ParticleTextureSheet.TERRAIN_SHEET, ParticleTextureSheet.PARTICLE_SHEET_OPAQUE, ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT);
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   interface SpriteAwareFactory {
      ParticleFactory create(SpriteProvider spriteProvider);
   }

   @Environment(EnvType.CLIENT)
   static class SimpleSpriteProvider implements SpriteProvider {
      private List sprites;

      public Sprite getSprite(int age, int maxAge) {
         return (Sprite)this.sprites.get(age * (this.sprites.size() - 1) / maxAge);
      }

      public Sprite getSprite(Random random) {
         return (Sprite)this.sprites.get(random.nextInt(this.sprites.size()));
      }

      public void setSprites(List sprites) {
         this.sprites = ImmutableList.copyOf(sprites);
      }
   }
}
