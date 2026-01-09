package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.BreezeWindChargeEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.LingeringPotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.ChestRaftEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.RaftEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class EntityType implements ToggleableFeature, TypeFilter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final RegistryEntry.Reference registryEntry;
   public static final Codec CODEC;
   private static final float field_30054 = 1.3964844F;
   private static final int field_42459 = 10;
   public static final EntityType ACACIA_BOAT;
   public static final EntityType ACACIA_CHEST_BOAT;
   public static final EntityType ALLAY;
   public static final EntityType AREA_EFFECT_CLOUD;
   public static final EntityType ARMADILLO;
   public static final EntityType ARMOR_STAND;
   public static final EntityType ARROW;
   public static final EntityType AXOLOTL;
   public static final EntityType BAMBOO_CHEST_RAFT;
   public static final EntityType BAMBOO_RAFT;
   public static final EntityType BAT;
   public static final EntityType BEE;
   public static final EntityType BIRCH_BOAT;
   public static final EntityType BIRCH_CHEST_BOAT;
   public static final EntityType BLAZE;
   public static final EntityType BLOCK_DISPLAY;
   public static final EntityType BOGGED;
   public static final EntityType BREEZE;
   public static final EntityType BREEZE_WIND_CHARGE;
   public static final EntityType CAMEL;
   public static final EntityType CAT;
   public static final EntityType CAVE_SPIDER;
   public static final EntityType CHERRY_BOAT;
   public static final EntityType CHERRY_CHEST_BOAT;
   public static final EntityType CHEST_MINECART;
   public static final EntityType CHICKEN;
   public static final EntityType COD;
   public static final EntityType COMMAND_BLOCK_MINECART;
   public static final EntityType COW;
   public static final EntityType CREAKING;
   public static final EntityType CREEPER;
   public static final EntityType DARK_OAK_BOAT;
   public static final EntityType DARK_OAK_CHEST_BOAT;
   public static final EntityType DOLPHIN;
   public static final EntityType DONKEY;
   public static final EntityType DRAGON_FIREBALL;
   public static final EntityType DROWNED;
   public static final EntityType EGG;
   public static final EntityType ELDER_GUARDIAN;
   public static final EntityType ENDERMAN;
   public static final EntityType ENDERMITE;
   public static final EntityType ENDER_DRAGON;
   public static final EntityType ENDER_PEARL;
   public static final EntityType END_CRYSTAL;
   public static final EntityType EVOKER;
   public static final EntityType EVOKER_FANGS;
   public static final EntityType EXPERIENCE_BOTTLE;
   public static final EntityType EXPERIENCE_ORB;
   public static final EntityType EYE_OF_ENDER;
   public static final EntityType FALLING_BLOCK;
   public static final EntityType FIREBALL;
   public static final EntityType FIREWORK_ROCKET;
   public static final EntityType FOX;
   public static final EntityType FROG;
   public static final EntityType FURNACE_MINECART;
   public static final EntityType GHAST;
   public static final EntityType HAPPY_GHAST;
   public static final EntityType GIANT;
   public static final EntityType GLOW_ITEM_FRAME;
   public static final EntityType GLOW_SQUID;
   public static final EntityType GOAT;
   public static final EntityType GUARDIAN;
   public static final EntityType HOGLIN;
   public static final EntityType HOPPER_MINECART;
   public static final EntityType HORSE;
   public static final EntityType HUSK;
   public static final EntityType ILLUSIONER;
   public static final EntityType INTERACTION;
   public static final EntityType IRON_GOLEM;
   public static final EntityType ITEM;
   public static final EntityType ITEM_DISPLAY;
   public static final EntityType ITEM_FRAME;
   public static final EntityType JUNGLE_BOAT;
   public static final EntityType JUNGLE_CHEST_BOAT;
   public static final EntityType LEASH_KNOT;
   public static final EntityType LIGHTNING_BOLT;
   public static final EntityType LLAMA;
   public static final EntityType LLAMA_SPIT;
   public static final EntityType MAGMA_CUBE;
   public static final EntityType MANGROVE_BOAT;
   public static final EntityType MANGROVE_CHEST_BOAT;
   public static final EntityType MARKER;
   public static final EntityType MINECART;
   public static final EntityType MOOSHROOM;
   public static final EntityType MULE;
   public static final EntityType OAK_BOAT;
   public static final EntityType OAK_CHEST_BOAT;
   public static final EntityType OCELOT;
   public static final EntityType OMINOUS_ITEM_SPAWNER;
   public static final EntityType PAINTING;
   public static final EntityType PALE_OAK_BOAT;
   public static final EntityType PALE_OAK_CHEST_BOAT;
   public static final EntityType PANDA;
   public static final EntityType PARROT;
   public static final EntityType PHANTOM;
   public static final EntityType PIG;
   public static final EntityType PIGLIN;
   public static final EntityType PIGLIN_BRUTE;
   public static final EntityType PILLAGER;
   public static final EntityType POLAR_BEAR;
   public static final EntityType SPLASH_POTION;
   public static final EntityType LINGERING_POTION;
   public static final EntityType PUFFERFISH;
   public static final EntityType RABBIT;
   public static final EntityType RAVAGER;
   public static final EntityType SALMON;
   public static final EntityType SHEEP;
   public static final EntityType SHULKER;
   public static final EntityType SHULKER_BULLET;
   public static final EntityType SILVERFISH;
   public static final EntityType SKELETON;
   public static final EntityType SKELETON_HORSE;
   public static final EntityType SLIME;
   public static final EntityType SMALL_FIREBALL;
   public static final EntityType SNIFFER;
   public static final EntityType SNOWBALL;
   public static final EntityType SNOW_GOLEM;
   public static final EntityType SPAWNER_MINECART;
   public static final EntityType SPECTRAL_ARROW;
   public static final EntityType SPIDER;
   public static final EntityType SPRUCE_BOAT;
   public static final EntityType SPRUCE_CHEST_BOAT;
   public static final EntityType SQUID;
   public static final EntityType STRAY;
   public static final EntityType STRIDER;
   public static final EntityType TADPOLE;
   public static final EntityType TEXT_DISPLAY;
   public static final EntityType TNT;
   public static final EntityType TNT_MINECART;
   public static final EntityType TRADER_LLAMA;
   public static final EntityType TRIDENT;
   public static final EntityType TROPICAL_FISH;
   public static final EntityType TURTLE;
   public static final EntityType VEX;
   public static final EntityType VILLAGER;
   public static final EntityType VINDICATOR;
   public static final EntityType WANDERING_TRADER;
   public static final EntityType WARDEN;
   public static final EntityType WIND_CHARGE;
   public static final EntityType WITCH;
   public static final EntityType WITHER;
   public static final EntityType WITHER_SKELETON;
   public static final EntityType WITHER_SKULL;
   public static final EntityType WOLF;
   public static final EntityType ZOGLIN;
   public static final EntityType ZOMBIE;
   public static final EntityType ZOMBIE_HORSE;
   public static final EntityType ZOMBIE_VILLAGER;
   public static final EntityType ZOMBIFIED_PIGLIN;
   public static final EntityType PLAYER;
   public static final EntityType FISHING_BOBBER;
   private static final Set POTENTIALLY_EXECUTES_COMMANDS;
   private final EntityFactory factory;
   private final SpawnGroup spawnGroup;
   private final ImmutableSet canSpawnInside;
   private final boolean saveable;
   private final boolean summonable;
   private final boolean fireImmune;
   private final boolean spawnableFarFromPlayer;
   private final int maxTrackDistance;
   private final int trackTickInterval;
   private final String translationKey;
   @Nullable
   private Text name;
   private final Optional lootTableKey;
   private final EntityDimensions dimensions;
   private final float spawnBoxScale;
   private final FeatureSet requiredFeatures;

   private static EntityType register(RegistryKey key, Builder type) {
      return (EntityType)Registry.register(Registries.ENTITY_TYPE, (RegistryKey)key, type.build(key));
   }

   private static RegistryKey keyOf(String id) {
      return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.ofVanilla(id));
   }

   private static EntityType register(String id, Builder type) {
      return register(keyOf(id), type);
   }

   public static Identifier getId(EntityType type) {
      return Registries.ENTITY_TYPE.getId(type);
   }

   public static Optional get(String id) {
      return Registries.ENTITY_TYPE.getOptionalValue(Identifier.tryParse(id));
   }

   public EntityType(EntityFactory factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet canSpawnInside, EntityDimensions dimensions, float spawnBoxScale, int maxTrackDistance, int trackTickInterval, String translationKey, Optional lootTable, FeatureSet requiredFeatures) {
      this.registryEntry = Registries.ENTITY_TYPE.createEntry(this);
      this.factory = factory;
      this.spawnGroup = spawnGroup;
      this.spawnableFarFromPlayer = spawnableFarFromPlayer;
      this.saveable = saveable;
      this.summonable = summonable;
      this.fireImmune = fireImmune;
      this.canSpawnInside = canSpawnInside;
      this.dimensions = dimensions;
      this.spawnBoxScale = spawnBoxScale;
      this.maxTrackDistance = maxTrackDistance;
      this.trackTickInterval = trackTickInterval;
      this.translationKey = translationKey;
      this.lootTableKey = lootTable;
      this.requiredFeatures = requiredFeatures;
   }

   @Nullable
   public Entity spawnFromItemStack(ServerWorld world, @Nullable ItemStack stack, @Nullable LivingEntity spawner, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY) {
      Consumer consumer;
      if (stack != null) {
         consumer = copier(world, stack, spawner);
      } else {
         consumer = (entity) -> {
         };
      }

      return this.spawn(world, consumer, pos, spawnReason, alignPosition, invertY);
   }

   public static Consumer copier(World world, ItemStack stack, @Nullable LivingEntity spawner) {
      return copier((entity) -> {
      }, world, stack, spawner);
   }

   public static Consumer copier(Consumer chained, World world, ItemStack stack, @Nullable LivingEntity spawner) {
      return nbtCopier(componentsCopier(chained, stack), world, stack, spawner);
   }

   public static Consumer componentsCopier(Consumer chained, ItemStack stack) {
      return chained.andThen((entity) -> {
         entity.copyComponentsFrom(stack);
      });
   }

   public static Consumer nbtCopier(Consumer chained, World world, ItemStack stack, @Nullable LivingEntity spawner) {
      NbtComponent nbtComponent = (NbtComponent)stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
      return !nbtComponent.isEmpty() ? chained.andThen((entity) -> {
         loadFromEntityNbt(world, spawner, entity, nbtComponent);
      }) : chained;
   }

   @Nullable
   public Entity spawn(ServerWorld world, BlockPos pos, SpawnReason reason) {
      return this.spawn(world, (Consumer)null, pos, reason, false, false);
   }

   @Nullable
   public Entity spawn(ServerWorld world, @Nullable Consumer afterConsumer, BlockPos pos, SpawnReason reason, boolean alignPosition, boolean invertY) {
      Entity entity = this.create(world, afterConsumer, pos, reason, alignPosition, invertY);
      if (entity != null) {
         world.spawnEntityAndPassengers(entity);
         if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            mobEntity.playAmbientSound();
         }
      }

      return entity;
   }

   @Nullable
   public Entity create(ServerWorld world, @Nullable Consumer afterConsumer, BlockPos pos, SpawnReason reason, boolean alignPosition, boolean invertY) {
      Entity entity = this.create(world, reason);
      if (entity == null) {
         return null;
      } else {
         double d;
         if (alignPosition) {
            entity.setPosition((double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5);
            d = getOriginY(world, pos, invertY, entity.getBoundingBox());
         } else {
            d = 0.0;
         }

         entity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY() + d, (double)pos.getZ() + 0.5, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);
         if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            mobEntity.headYaw = mobEntity.getYaw();
            mobEntity.bodyYaw = mobEntity.getYaw();
            mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), reason, (EntityData)null);
         }

         if (afterConsumer != null) {
            afterConsumer.accept(entity);
         }

         return entity;
      }
   }

   protected static double getOriginY(WorldView world, BlockPos pos, boolean invertY, Box boundingBox) {
      Box box = new Box(pos);
      if (invertY) {
         box = box.stretch(0.0, -1.0, 0.0);
      }

      Iterable iterable = world.getCollisions((Entity)null, box);
      return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, iterable, invertY ? -2.0 : -1.0);
   }

   public static void loadFromEntityNbt(World world, @Nullable LivingEntity spawner, @Nullable Entity entity, NbtComponent nbt) {
      MinecraftServer minecraftServer = world.getServer();
      if (minecraftServer != null && entity != null) {
         EntityType entityType = (EntityType)nbt.getRegistryValueOfId(minecraftServer.getRegistryManager(), RegistryKeys.ENTITY_TYPE);
         if (entity.getType() == entityType) {
            if (!world.isClient && entity.getType().canPotentiallyExecuteCommands()) {
               if (!(spawner instanceof PlayerEntity)) {
                  return;
               }

               PlayerEntity playerEntity = (PlayerEntity)spawner;
               if (!minecraftServer.getPlayerManager().isOperator(playerEntity.getGameProfile())) {
                  return;
               }
            }

            nbt.applyToEntity(entity);
         }
      }
   }

   public boolean isSaveable() {
      return this.saveable;
   }

   public boolean isSummonable() {
      return this.summonable;
   }

   public boolean isFireImmune() {
      return this.fireImmune;
   }

   public boolean isSpawnableFarFromPlayer() {
      return this.spawnableFarFromPlayer;
   }

   public SpawnGroup getSpawnGroup() {
      return this.spawnGroup;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public Text getName() {
      if (this.name == null) {
         this.name = Text.translatable(this.getTranslationKey());
      }

      return this.name;
   }

   public String toString() {
      return this.getTranslationKey();
   }

   public String getUntranslatedName() {
      int i = this.getTranslationKey().lastIndexOf(46);
      return i == -1 ? this.getTranslationKey() : this.getTranslationKey().substring(i + 1);
   }

   public Optional getLootTableKey() {
      return this.lootTableKey;
   }

   public float getWidth() {
      return this.dimensions.width();
   }

   public float getHeight() {
      return this.dimensions.height();
   }

   public FeatureSet getRequiredFeatures() {
      return this.requiredFeatures;
   }

   @Nullable
   public Entity create(World world, SpawnReason reason) {
      return !this.isEnabled(world.getEnabledFeatures()) ? null : this.factory.create(this, world);
   }

   public static Optional getEntityFromData(ReadView view, World world, SpawnReason reason) {
      return Util.ifPresentOrElse(fromData(view).map((type) -> {
         return type.create(world, reason);
      }), (entity) -> {
         entity.readData(view);
      }, () -> {
         LOGGER.warn("Skipping Entity with id {}", view.getString("id", "[invalid]"));
      });
   }

   public Box getSpawnBox(double x, double y, double z) {
      float f = this.spawnBoxScale * this.getWidth() / 2.0F;
      float g = this.spawnBoxScale * this.getHeight();
      return new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f);
   }

   public boolean isInvalidSpawn(BlockState state) {
      if (this.canSpawnInside.contains(state.getBlock())) {
         return false;
      } else if (!this.fireImmune && PathNodeMaker.isFireDamaging(state)) {
         return true;
      } else {
         return state.isOf(Blocks.WITHER_ROSE) || state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.CACTUS) || state.isOf(Blocks.POWDER_SNOW);
      }
   }

   public EntityDimensions getDimensions() {
      return this.dimensions;
   }

   public static Optional fromData(ReadView view) {
      return view.read("id", CODEC);
   }

   @Nullable
   public static Entity loadEntityWithPassengers(NbtCompound nbt, World world, SpawnReason reason, Function entityProcessor) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(LOGGER);

      Entity var5;
      try {
         var5 = loadEntityWithPassengers(NbtReadView.create(logging, world.getRegistryManager(), nbt), world, reason, entityProcessor);
      } catch (Throwable var8) {
         try {
            logging.close();
         } catch (Throwable var7) {
            var8.addSuppressed(var7);
         }

         throw var8;
      }

      logging.close();
      return var5;
   }

   @Nullable
   public static Entity loadEntityWithPassengers(ReadView view, World world, SpawnReason reason, Function entityProcessor) {
      return (Entity)loadEntityFromData(view, world, reason).map(entityProcessor).map((entity) -> {
         Iterator var5 = view.getListReadView("Passengers").iterator();

         while(var5.hasNext()) {
            ReadView readView2 = (ReadView)var5.next();
            Entity entity2 = loadEntityWithPassengers(readView2, world, reason, entityProcessor);
            if (entity2 != null) {
               entity2.startRiding(entity, true);
            }
         }

         return entity;
      }).orElse((Object)null);
   }

   public static Stream streamFromData(ReadView.ListReadView view, World world, SpawnReason reason) {
      return view.stream().mapMulti((viewx, callback) -> {
         loadEntityWithPassengers(viewx, world, reason, (entity) -> {
            callback.accept(entity);
            return entity;
         });
      });
   }

   private static Optional loadEntityFromData(ReadView view, World world, SpawnReason reason) {
      try {
         return getEntityFromData(view, world, reason);
      } catch (RuntimeException var4) {
         LOGGER.warn("Exception loading entity: ", var4);
         return Optional.empty();
      }
   }

   public int getMaxTrackDistance() {
      return this.maxTrackDistance;
   }

   public int getTrackTickInterval() {
      return this.trackTickInterval;
   }

   public boolean alwaysUpdateVelocity() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != GLOW_ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }

   public boolean isIn(TagKey tag) {
      return this.registryEntry.isIn(tag);
   }

   public boolean isIn(RegistryEntryList entityTypeEntryList) {
      return entityTypeEntryList.contains(this.registryEntry);
   }

   @Nullable
   public Entity downcast(Entity entity) {
      return entity.getType() == this ? entity : null;
   }

   public Class getBaseClass() {
      return Entity.class;
   }

   /** @deprecated */
   @Deprecated
   public RegistryEntry.Reference getRegistryEntry() {
      return this.registryEntry;
   }

   private static EntityFactory getBoatFactory(Supplier itemSupplier) {
      return (type, world) -> {
         return new BoatEntity(type, world, itemSupplier);
      };
   }

   private static EntityFactory getChestBoatFactory(Supplier itemSupplier) {
      return (type, world) -> {
         return new ChestBoatEntity(type, world, itemSupplier);
      };
   }

   private static EntityFactory getRaftFactory(Supplier itemSupplier) {
      return (type, world) -> {
         return new RaftEntity(type, world, itemSupplier);
      };
   }

   private static EntityFactory getChestRaftFactory(Supplier itemSupplier) {
      return (type, world) -> {
         return new ChestRaftEntity(type, world, itemSupplier);
      };
   }

   public boolean canPotentiallyExecuteCommands() {
      return POTENTIALLY_EXECUTES_COMMANDS.contains(this);
   }

   static {
      CODEC = Registries.ENTITY_TYPE.getCodec();
      ACACIA_BOAT = register("acacia_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.ACACIA_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      ACACIA_CHEST_BOAT = register("acacia_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.ACACIA_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      ALLAY = register("allay", EntityType.Builder.create(AllayEntity::new, SpawnGroup.CREATURE).dimensions(0.35F, 0.6F).eyeHeight(0.36F).vehicleAttachment(0.04F).maxTrackingRange(8).trackingTickInterval(2));
      AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.create(AreaEffectCloudEntity::new, SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(6.0F, 0.5F).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
      ARMADILLO = register("armadillo", EntityType.Builder.create(ArmadilloEntity::new, SpawnGroup.CREATURE).dimensions(0.7F, 0.65F).eyeHeight(0.26F).maxTrackingRange(10));
      ARMOR_STAND = register("armor_stand", EntityType.Builder.create(ArmorStandEntity::new, SpawnGroup.MISC).dimensions(0.5F, 1.975F).eyeHeight(1.7775F).maxTrackingRange(10));
      ARROW = register("arrow", EntityType.Builder.create(ArrowEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).eyeHeight(0.13F).maxTrackingRange(4).trackingTickInterval(20));
      AXOLOTL = register("axolotl", EntityType.Builder.create(AxolotlEntity::new, SpawnGroup.AXOLOTLS).dimensions(0.75F, 0.42F).eyeHeight(0.2751F).maxTrackingRange(10));
      BAMBOO_CHEST_RAFT = register("bamboo_chest_raft", EntityType.Builder.create(getChestRaftFactory(() -> {
         return Items.BAMBOO_CHEST_RAFT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      BAMBOO_RAFT = register("bamboo_raft", EntityType.Builder.create(getRaftFactory(() -> {
         return Items.BAMBOO_RAFT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      BAT = register("bat", EntityType.Builder.create(BatEntity::new, SpawnGroup.AMBIENT).dimensions(0.5F, 0.9F).eyeHeight(0.45F).maxTrackingRange(5));
      BEE = register("bee", EntityType.Builder.create(BeeEntity::new, SpawnGroup.CREATURE).dimensions(0.7F, 0.6F).eyeHeight(0.3F).maxTrackingRange(8));
      BIRCH_BOAT = register("birch_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.BIRCH_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      BIRCH_CHEST_BOAT = register("birch_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.BIRCH_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      BLAZE = register("blaze", EntityType.Builder.create(BlazeEntity::new, SpawnGroup.MONSTER).makeFireImmune().dimensions(0.6F, 1.8F).maxTrackingRange(8));
      BLOCK_DISPLAY = register("block_display", EntityType.Builder.create(DisplayEntity.BlockDisplayEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.0F, 0.0F).maxTrackingRange(10).trackingTickInterval(1));
      BOGGED = register("bogged", EntityType.Builder.create(BoggedEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.99F).eyeHeight(1.74F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      BREEZE = register("breeze", EntityType.Builder.create(BreezeEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.77F).eyeHeight(1.3452F).maxTrackingRange(10));
      BREEZE_WIND_CHARGE = register("breeze_wind_charge", EntityType.Builder.create(BreezeWindChargeEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.3125F, 0.3125F).eyeHeight(0.0F).maxTrackingRange(4).trackingTickInterval(10));
      CAMEL = register("camel", EntityType.Builder.create(CamelEntity::new, SpawnGroup.CREATURE).dimensions(1.7F, 2.375F).eyeHeight(2.275F).maxTrackingRange(10));
      CAT = register("cat", EntityType.Builder.create(CatEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.7F).eyeHeight(0.35F).passengerAttachments(0.5125F).maxTrackingRange(8));
      CAVE_SPIDER = register("cave_spider", EntityType.Builder.create(CaveSpiderEntity::new, SpawnGroup.MONSTER).dimensions(0.7F, 0.5F).eyeHeight(0.45F).maxTrackingRange(8));
      CHERRY_BOAT = register("cherry_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.CHERRY_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      CHERRY_CHEST_BOAT = register("cherry_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.CHERRY_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      CHEST_MINECART = register("chest_minecart", EntityType.Builder.create(ChestMinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));
      CHICKEN = register("chicken", EntityType.Builder.create(ChickenEntity::new, SpawnGroup.CREATURE).dimensions(0.4F, 0.7F).eyeHeight(0.644F).passengerAttachments(new Vec3d(0.0, 0.7, -0.1)).maxTrackingRange(10));
      COD = register("cod", EntityType.Builder.create(CodEntity::new, SpawnGroup.WATER_AMBIENT).dimensions(0.5F, 0.3F).eyeHeight(0.195F).maxTrackingRange(4));
      COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.create(CommandBlockMinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));
      COW = register("cow", EntityType.Builder.create(CowEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.4F).eyeHeight(1.3F).passengerAttachments(1.36875F).maxTrackingRange(10));
      CREAKING = register("creaking", EntityType.Builder.create(CreakingEntity::new, SpawnGroup.MONSTER).dimensions(0.9F, 2.7F).eyeHeight(2.3F).maxTrackingRange(8));
      CREEPER = register("creeper", EntityType.Builder.create(CreeperEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.7F).maxTrackingRange(8));
      DARK_OAK_BOAT = register("dark_oak_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.DARK_OAK_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      DARK_OAK_CHEST_BOAT = register("dark_oak_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.DARK_OAK_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      DOLPHIN = register("dolphin", EntityType.Builder.create(DolphinEntity::new, SpawnGroup.WATER_CREATURE).dimensions(0.9F, 0.6F).eyeHeight(0.3F));
      DONKEY = register("donkey", EntityType.Builder.create(DonkeyEntity::new, SpawnGroup.CREATURE).dimensions(1.3964844F, 1.5F).eyeHeight(1.425F).passengerAttachments(1.1125F).maxTrackingRange(10));
      DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.create(DragonFireballEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(1.0F, 1.0F).maxTrackingRange(4).trackingTickInterval(10));
      DROWNED = register("drowned", EntityType.Builder.create(DrownedEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.0125F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      EGG = register("egg", EntityType.Builder.create(EggEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.create(ElderGuardianEntity::new, SpawnGroup.MONSTER).dimensions(1.9975F, 1.9975F).eyeHeight(0.99875F).passengerAttachments(2.350625F).maxTrackingRange(10));
      ENDERMAN = register("enderman", EntityType.Builder.create(EndermanEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 2.9F).eyeHeight(2.55F).passengerAttachments(2.80625F).maxTrackingRange(8));
      ENDERMITE = register("endermite", EntityType.Builder.create(EndermiteEntity::new, SpawnGroup.MONSTER).dimensions(0.4F, 0.3F).eyeHeight(0.13F).passengerAttachments(0.2375F).maxTrackingRange(8));
      ENDER_DRAGON = register("ender_dragon", EntityType.Builder.create(EnderDragonEntity::new, SpawnGroup.MONSTER).makeFireImmune().dimensions(16.0F, 8.0F).passengerAttachments(3.0F).maxTrackingRange(10));
      ENDER_PEARL = register("ender_pearl", EntityType.Builder.create(EnderPearlEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      END_CRYSTAL = register("end_crystal", EntityType.Builder.create(EndCrystalEntity::new, SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(2.0F, 2.0F).maxTrackingRange(16).trackingTickInterval(Integer.MAX_VALUE));
      EVOKER = register("evoker", EntityType.Builder.create(EvokerEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).passengerAttachments(2.0F).vehicleAttachment(-0.6F).maxTrackingRange(8));
      EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.create(EvokerFangsEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.8F).maxTrackingRange(6).trackingTickInterval(2));
      EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.create(ExperienceBottleEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.create(ExperienceOrbEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).maxTrackingRange(6).trackingTickInterval(20));
      EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.create(EyeOfEnderEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(4));
      FALLING_BLOCK = register("falling_block", EntityType.Builder.create(FallingBlockEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.98F).maxTrackingRange(10).trackingTickInterval(20));
      FIREBALL = register("fireball", EntityType.Builder.create(FireballEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(1.0F, 1.0F).maxTrackingRange(4).trackingTickInterval(10));
      FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.create(FireworkRocketEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      FOX = register("fox", EntityType.Builder.create(FoxEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.7F).eyeHeight(0.4F).passengerAttachments(new Vec3d(0.0, 0.6375, -0.25)).maxTrackingRange(8).allowSpawningInside(Blocks.SWEET_BERRY_BUSH));
      FROG = register("frog", EntityType.Builder.create(FrogEntity::new, SpawnGroup.CREATURE).dimensions(0.5F, 0.5F).passengerAttachments(new Vec3d(0.0, 0.375, -0.25)).maxTrackingRange(10));
      FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.create(FurnaceMinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));
      GHAST = register("ghast", EntityType.Builder.create(GhastEntity::new, SpawnGroup.MONSTER).makeFireImmune().dimensions(4.0F, 4.0F).eyeHeight(2.6F).passengerAttachments(4.0625F).vehicleAttachment(0.5F).maxTrackingRange(10));
      HAPPY_GHAST = register("happy_ghast", EntityType.Builder.create(HappyGhastEntity::new, SpawnGroup.CREATURE).dimensions(4.0F, 4.0F).eyeHeight(2.6F).passengerAttachments(new Vec3d(0.0, 4.0, 1.7), new Vec3d(-1.7, 4.0, 0.0), new Vec3d(0.0, 4.0, -1.7), new Vec3d(1.7, 4.0, 0.0)).vehicleAttachment(0.5F).maxTrackingRange(10));
      GIANT = register("giant", EntityType.Builder.create(GiantEntity::new, SpawnGroup.MONSTER).dimensions(3.6F, 12.0F).eyeHeight(10.44F).vehicleAttachment(-3.75F).maxTrackingRange(10));
      GLOW_ITEM_FRAME = register("glow_item_frame", EntityType.Builder.create(GlowItemFrameEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).eyeHeight(0.0F).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
      GLOW_SQUID = register("glow_squid", EntityType.Builder.create(GlowSquidEntity::new, SpawnGroup.UNDERGROUND_WATER_CREATURE).dimensions(0.8F, 0.8F).eyeHeight(0.4F).maxTrackingRange(10));
      GOAT = register("goat", EntityType.Builder.create(GoatEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.3F).passengerAttachments(1.1125F).maxTrackingRange(10));
      GUARDIAN = register("guardian", EntityType.Builder.create(GuardianEntity::new, SpawnGroup.MONSTER).dimensions(0.85F, 0.85F).eyeHeight(0.425F).passengerAttachments(0.975F).maxTrackingRange(8));
      HOGLIN = register("hoglin", EntityType.Builder.create(HoglinEntity::new, SpawnGroup.MONSTER).dimensions(1.3964844F, 1.4F).passengerAttachments(1.49375F).maxTrackingRange(8));
      HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.create(HopperMinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));
      HORSE = register("horse", EntityType.Builder.create(HorseEntity::new, SpawnGroup.CREATURE).dimensions(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.44375F).maxTrackingRange(10));
      HUSK = register("husk", EntityType.Builder.create(HuskEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.075F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      ILLUSIONER = register("illusioner", EntityType.Builder.create(IllusionerEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).passengerAttachments(2.0F).vehicleAttachment(-0.6F).maxTrackingRange(8));
      INTERACTION = register("interaction", EntityType.Builder.create(InteractionEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.0F, 0.0F).maxTrackingRange(10));
      IRON_GOLEM = register("iron_golem", EntityType.Builder.create(IronGolemEntity::new, SpawnGroup.MISC).dimensions(1.4F, 2.7F).maxTrackingRange(10));
      ITEM = register("item", EntityType.Builder.create(ItemEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).eyeHeight(0.2125F).maxTrackingRange(6).trackingTickInterval(20));
      ITEM_DISPLAY = register("item_display", EntityType.Builder.create(DisplayEntity.ItemDisplayEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.0F, 0.0F).maxTrackingRange(10).trackingTickInterval(1));
      ITEM_FRAME = register("item_frame", EntityType.Builder.create(ItemFrameEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).eyeHeight(0.0F).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
      JUNGLE_BOAT = register("jungle_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.JUNGLE_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      JUNGLE_CHEST_BOAT = register("jungle_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.JUNGLE_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      LEASH_KNOT = register("leash_knot", EntityType.Builder.create(LeashKnotEntity::new, SpawnGroup.MISC).dropsNothing().disableSaving().dimensions(0.375F, 0.5F).eyeHeight(0.0625F).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
      LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.create(LightningEntity::new, SpawnGroup.MISC).dropsNothing().disableSaving().dimensions(0.0F, 0.0F).maxTrackingRange(16).trackingTickInterval(Integer.MAX_VALUE));
      LLAMA = register("llama", EntityType.Builder.create(LlamaEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.87F).eyeHeight(1.7765F).passengerAttachments(new Vec3d(0.0, 1.37, -0.3)).maxTrackingRange(10));
      LLAMA_SPIT = register("llama_spit", EntityType.Builder.create(LlamaSpitEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      MAGMA_CUBE = register("magma_cube", EntityType.Builder.create(MagmaCubeEntity::new, SpawnGroup.MONSTER).makeFireImmune().dimensions(0.52F, 0.52F).eyeHeight(0.325F).spawnBoxScale(4.0F).maxTrackingRange(8));
      MANGROVE_BOAT = register("mangrove_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.MANGROVE_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      MANGROVE_CHEST_BOAT = register("mangrove_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.MANGROVE_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      MARKER = register("marker", EntityType.Builder.create(MarkerEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.0F, 0.0F).maxTrackingRange(0));
      MINECART = register("minecart", EntityType.Builder.create(MinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));
      MOOSHROOM = register("mooshroom", EntityType.Builder.create(MooshroomEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.4F).eyeHeight(1.3F).passengerAttachments(1.36875F).maxTrackingRange(10));
      MULE = register("mule", EntityType.Builder.create(MuleEntity::new, SpawnGroup.CREATURE).dimensions(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.2125F).maxTrackingRange(8));
      OAK_BOAT = register("oak_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.OAK_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      OAK_CHEST_BOAT = register("oak_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.OAK_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      OCELOT = register("ocelot", EntityType.Builder.create(OcelotEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.7F).passengerAttachments(0.6375F).maxTrackingRange(10));
      OMINOUS_ITEM_SPAWNER = register("ominous_item_spawner", EntityType.Builder.create(OminousItemSpawnerEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(8));
      PAINTING = register("painting", EntityType.Builder.create(PaintingEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
      PALE_OAK_BOAT = register("pale_oak_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.PALE_OAK_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      PALE_OAK_CHEST_BOAT = register("pale_oak_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.PALE_OAK_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      PANDA = register("panda", EntityType.Builder.create(PandaEntity::new, SpawnGroup.CREATURE).dimensions(1.3F, 1.25F).maxTrackingRange(10));
      PARROT = register("parrot", EntityType.Builder.create(ParrotEntity::new, SpawnGroup.CREATURE).dimensions(0.5F, 0.9F).eyeHeight(0.54F).passengerAttachments(0.4625F).maxTrackingRange(8));
      PHANTOM = register("phantom", EntityType.Builder.create(PhantomEntity::new, SpawnGroup.MONSTER).dimensions(0.9F, 0.5F).eyeHeight(0.175F).passengerAttachments(0.3375F).vehicleAttachment(-0.125F).maxTrackingRange(8));
      PIG = register("pig", EntityType.Builder.create(PigEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 0.9F).passengerAttachments(0.86875F).maxTrackingRange(10));
      PIGLIN = register("piglin", EntityType.Builder.create(PiglinEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).eyeHeight(1.79F).passengerAttachments(2.0125F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      PIGLIN_BRUTE = register("piglin_brute", EntityType.Builder.create(PiglinBruteEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).eyeHeight(1.79F).passengerAttachments(2.0125F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      PILLAGER = register("pillager", EntityType.Builder.create(PillagerEntity::new, SpawnGroup.MONSTER).spawnableFarFromPlayer().dimensions(0.6F, 1.95F).passengerAttachments(2.0F).vehicleAttachment(-0.6F).maxTrackingRange(8));
      POLAR_BEAR = register("polar_bear", EntityType.Builder.create(PolarBearEntity::new, SpawnGroup.CREATURE).allowSpawningInside(Blocks.POWDER_SNOW).dimensions(1.4F, 1.4F).maxTrackingRange(10));
      SPLASH_POTION = register("splash_potion", EntityType.Builder.create(SplashPotionEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      LINGERING_POTION = register("lingering_potion", EntityType.Builder.create(LingeringPotionEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      PUFFERFISH = register("pufferfish", EntityType.Builder.create(PufferfishEntity::new, SpawnGroup.WATER_AMBIENT).dimensions(0.7F, 0.7F).eyeHeight(0.455F).maxTrackingRange(4));
      RABBIT = register("rabbit", EntityType.Builder.create(RabbitEntity::new, SpawnGroup.CREATURE).dimensions(0.4F, 0.5F).maxTrackingRange(8));
      RAVAGER = register("ravager", EntityType.Builder.create(RavagerEntity::new, SpawnGroup.MONSTER).dimensions(1.95F, 2.2F).passengerAttachments(new Vec3d(0.0, 2.2625, -0.0625)).maxTrackingRange(10));
      SALMON = register("salmon", EntityType.Builder.create(SalmonEntity::new, SpawnGroup.WATER_AMBIENT).dimensions(0.7F, 0.4F).eyeHeight(0.26F).maxTrackingRange(4));
      SHEEP = register("sheep", EntityType.Builder.create(SheepEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.3F).eyeHeight(1.235F).passengerAttachments(1.2375F).maxTrackingRange(10));
      SHULKER = register("shulker", EntityType.Builder.create(ShulkerEntity::new, SpawnGroup.MONSTER).makeFireImmune().spawnableFarFromPlayer().dimensions(1.0F, 1.0F).eyeHeight(0.5F).maxTrackingRange(10));
      SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.create(ShulkerBulletEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.3125F, 0.3125F).maxTrackingRange(8));
      SILVERFISH = register("silverfish", EntityType.Builder.create(SilverfishEntity::new, SpawnGroup.MONSTER).dimensions(0.4F, 0.3F).eyeHeight(0.13F).passengerAttachments(0.2375F).maxTrackingRange(8));
      SKELETON = register("skeleton", EntityType.Builder.create(SkeletonEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.99F).eyeHeight(1.74F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity::new, SpawnGroup.CREATURE).dimensions(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.31875F).maxTrackingRange(10));
      SLIME = register("slime", EntityType.Builder.create(SlimeEntity::new, SpawnGroup.MONSTER).dimensions(0.52F, 0.52F).eyeHeight(0.325F).spawnBoxScale(4.0F).maxTrackingRange(10));
      SMALL_FIREBALL = register("small_fireball", EntityType.Builder.create(SmallFireballEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.3125F, 0.3125F).maxTrackingRange(4).trackingTickInterval(10));
      SNIFFER = register("sniffer", EntityType.Builder.create(SnifferEntity::new, SpawnGroup.CREATURE).dimensions(1.9F, 1.75F).eyeHeight(1.05F).passengerAttachments(2.09375F).nameTagAttachment(2.05F).maxTrackingRange(10));
      SNOWBALL = register("snowball", EntityType.Builder.create(SnowballEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10));
      SNOW_GOLEM = register("snow_golem", EntityType.Builder.create(SnowGolemEntity::new, SpawnGroup.MISC).allowSpawningInside(Blocks.POWDER_SNOW).dimensions(0.7F, 1.9F).eyeHeight(1.7F).maxTrackingRange(8));
      SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.create(SpawnerMinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));
      SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.create(SpectralArrowEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).eyeHeight(0.13F).maxTrackingRange(4).trackingTickInterval(20));
      SPIDER = register("spider", EntityType.Builder.create(SpiderEntity::new, SpawnGroup.MONSTER).dimensions(1.4F, 0.9F).eyeHeight(0.65F).passengerAttachments(0.765F).maxTrackingRange(8));
      SPRUCE_BOAT = register("spruce_boat", EntityType.Builder.create(getBoatFactory(() -> {
         return Items.SPRUCE_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      SPRUCE_CHEST_BOAT = register("spruce_chest_boat", EntityType.Builder.create(getChestBoatFactory(() -> {
         return Items.SPRUCE_CHEST_BOAT;
      }), SpawnGroup.MISC).dropsNothing().dimensions(1.375F, 0.5625F).eyeHeight(0.5625F).maxTrackingRange(10));
      SQUID = register("squid", EntityType.Builder.create(SquidEntity::new, SpawnGroup.WATER_CREATURE).dimensions(0.8F, 0.8F).eyeHeight(0.4F).maxTrackingRange(8));
      STRAY = register("stray", EntityType.Builder.create(StrayEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.99F).eyeHeight(1.74F).vehicleAttachment(-0.7F).allowSpawningInside(Blocks.POWDER_SNOW).maxTrackingRange(8));
      STRIDER = register("strider", EntityType.Builder.create(StriderEntity::new, SpawnGroup.CREATURE).makeFireImmune().dimensions(0.9F, 1.7F).maxTrackingRange(10));
      TADPOLE = register("tadpole", EntityType.Builder.create(TadpoleEntity::new, SpawnGroup.CREATURE).dimensions(0.4F, 0.3F).eyeHeight(0.19500001F).maxTrackingRange(10));
      TEXT_DISPLAY = register("text_display", EntityType.Builder.create(DisplayEntity.TextDisplayEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.0F, 0.0F).maxTrackingRange(10).trackingTickInterval(1));
      TNT = register("tnt", EntityType.Builder.create(TntEntity::new, SpawnGroup.MISC).dropsNothing().makeFireImmune().dimensions(0.98F, 0.98F).eyeHeight(0.15F).maxTrackingRange(10).trackingTickInterval(10));
      TNT_MINECART = register("tnt_minecart", EntityType.Builder.create(TntMinecartEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8));
      TRADER_LLAMA = register("trader_llama", EntityType.Builder.create(TraderLlamaEntity::new, SpawnGroup.CREATURE).dimensions(0.9F, 1.87F).eyeHeight(1.7765F).passengerAttachments(new Vec3d(0.0, 1.37, -0.3)).maxTrackingRange(10));
      TRIDENT = register("trident", EntityType.Builder.create(TridentEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).eyeHeight(0.13F).maxTrackingRange(4).trackingTickInterval(20));
      TROPICAL_FISH = register("tropical_fish", EntityType.Builder.create(TropicalFishEntity::new, SpawnGroup.WATER_AMBIENT).dimensions(0.5F, 0.4F).eyeHeight(0.26F).maxTrackingRange(4));
      TURTLE = register("turtle", EntityType.Builder.create(TurtleEntity::new, SpawnGroup.CREATURE).dimensions(1.2F, 0.4F).passengerAttachments(new Vec3d(0.0, 0.55625, -0.25)).maxTrackingRange(10));
      VEX = register("vex", EntityType.Builder.create(VexEntity::new, SpawnGroup.MONSTER).makeFireImmune().dimensions(0.4F, 0.8F).eyeHeight(0.51875F).passengerAttachments(0.7375F).vehicleAttachment(0.04F).maxTrackingRange(8));
      VILLAGER = register("villager", EntityType.Builder.create(VillagerEntity::new, SpawnGroup.MISC).dimensions(0.6F, 1.95F).eyeHeight(1.62F).maxTrackingRange(10));
      VINDICATOR = register("vindicator", EntityType.Builder.create(VindicatorEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).passengerAttachments(2.0F).vehicleAttachment(-0.6F).maxTrackingRange(8));
      WANDERING_TRADER = register("wandering_trader", EntityType.Builder.create(WanderingTraderEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 1.95F).eyeHeight(1.62F).maxTrackingRange(10));
      WARDEN = register("warden", EntityType.Builder.create(WardenEntity::new, SpawnGroup.MONSTER).dimensions(0.9F, 2.9F).passengerAttachments(3.15F).attachment(EntityAttachmentType.WARDEN_CHEST, 0.0F, 1.6F, 0.0F).maxTrackingRange(16).makeFireImmune());
      WIND_CHARGE = register("wind_charge", EntityType.Builder.create(WindChargeEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.3125F, 0.3125F).eyeHeight(0.0F).maxTrackingRange(4).trackingTickInterval(10));
      WITCH = register("witch", EntityType.Builder.create(WitchEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).eyeHeight(1.62F).passengerAttachments(2.2625F).maxTrackingRange(8));
      WITHER = register("wither", EntityType.Builder.create(WitherEntity::new, SpawnGroup.MONSTER).makeFireImmune().allowSpawningInside(Blocks.WITHER_ROSE).dimensions(0.9F, 3.5F).maxTrackingRange(10));
      WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.create(WitherSkeletonEntity::new, SpawnGroup.MONSTER).makeFireImmune().allowSpawningInside(Blocks.WITHER_ROSE).dimensions(0.7F, 2.4F).eyeHeight(2.1F).vehicleAttachment(-0.875F).maxTrackingRange(8));
      WITHER_SKULL = register("wither_skull", EntityType.Builder.create(WitherSkullEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.3125F, 0.3125F).maxTrackingRange(4).trackingTickInterval(10));
      WOLF = register("wolf", EntityType.Builder.create(WolfEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.85F).eyeHeight(0.68F).passengerAttachments(new Vec3d(0.0, 0.81875, -0.0625)).maxTrackingRange(10));
      ZOGLIN = register("zoglin", EntityType.Builder.create(ZoglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().dimensions(1.3964844F, 1.4F).passengerAttachments(1.49375F).maxTrackingRange(8));
      ZOMBIE = register("zombie", EntityType.Builder.create(ZombieEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.0125F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.create(ZombieHorseEntity::new, SpawnGroup.CREATURE).dimensions(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.31875F).maxTrackingRange(10));
      ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.create(ZombieVillagerEntity::new, SpawnGroup.MONSTER).dimensions(0.6F, 1.95F).passengerAttachments(2.125F).vehicleAttachment(-0.7F).eyeHeight(1.74F).maxTrackingRange(8));
      ZOMBIFIED_PIGLIN = register("zombified_piglin", EntityType.Builder.create(ZombifiedPiglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().dimensions(0.6F, 1.95F).eyeHeight(1.79F).passengerAttachments(2.0F).vehicleAttachment(-0.7F).maxTrackingRange(8));
      PLAYER = register("player", EntityType.Builder.create(SpawnGroup.MISC).disableSaving().disableSummon().dimensions(0.6F, 1.8F).eyeHeight(1.62F).vehicleAttachment(PlayerEntity.VEHICLE_ATTACHMENT_POS).maxTrackingRange(32).trackingTickInterval(2));
      FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.create(FishingBobberEntity::new, SpawnGroup.MISC).dropsNothing().disableSaving().disableSummon().dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(5));
      POTENTIALLY_EXECUTES_COMMANDS = Set.of(FALLING_BLOCK, COMMAND_BLOCK_MINECART, SPAWNER_MINECART);
   }

   public static class Builder implements FabricEntityType.Builder {
      private final EntityFactory factory;
      private final SpawnGroup spawnGroup;
      private ImmutableSet canSpawnInside = ImmutableSet.of();
      private boolean saveable = true;
      private boolean summonable = true;
      private boolean fireImmune;
      private boolean spawnableFarFromPlayer;
      private int maxTrackingRange = 5;
      private int trackingTickInterval = 3;
      private EntityDimensions dimensions = EntityDimensions.changing(0.6F, 1.8F);
      private float spawnBoxScale = 1.0F;
      private EntityAttachments.Builder attachments = EntityAttachments.builder();
      private FeatureSet requiredFeatures;
      private RegistryKeyedValue lootTable;
      private final RegistryKeyedValue translationKey;

      private Builder(EntityFactory factory, SpawnGroup spawnGroup) {
         this.requiredFeatures = FeatureFlags.VANILLA_FEATURES;
         this.lootTable = (registryKey) -> {
            return Optional.of(RegistryKey.of(RegistryKeys.LOOT_TABLE, registryKey.getValue().withPrefixedPath("entities/")));
         };
         this.translationKey = (registryKey) -> {
            return Util.createTranslationKey("entity", registryKey.getValue());
         };
         this.factory = factory;
         this.spawnGroup = spawnGroup;
         this.spawnableFarFromPlayer = spawnGroup == SpawnGroup.CREATURE || spawnGroup == SpawnGroup.MISC;
      }

      public static Builder create(EntityFactory factory, SpawnGroup spawnGroup) {
         return new Builder(factory, spawnGroup);
      }

      public static Builder create(SpawnGroup spawnGroup) {
         return new Builder((type, world) -> {
            return null;
         }, spawnGroup);
      }

      public Builder dimensions(float width, float height) {
         this.dimensions = EntityDimensions.changing(width, height);
         return this;
      }

      public Builder spawnBoxScale(float spawnBoxScale) {
         this.spawnBoxScale = spawnBoxScale;
         return this;
      }

      public Builder eyeHeight(float eyeHeight) {
         this.dimensions = this.dimensions.withEyeHeight(eyeHeight);
         return this;
      }

      public Builder passengerAttachments(float... offsetYs) {
         float[] var2 = offsetYs;
         int var3 = offsetYs.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            float f = var2[var4];
            this.attachments = this.attachments.add(EntityAttachmentType.PASSENGER, 0.0F, f, 0.0F);
         }

         return this;
      }

      public Builder passengerAttachments(Vec3d... passengerAttachments) {
         Vec3d[] var2 = passengerAttachments;
         int var3 = passengerAttachments.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Vec3d vec3d = var2[var4];
            this.attachments = this.attachments.add(EntityAttachmentType.PASSENGER, vec3d);
         }

         return this;
      }

      public Builder vehicleAttachment(Vec3d vehicleAttachment) {
         return this.attachment(EntityAttachmentType.VEHICLE, vehicleAttachment);
      }

      public Builder vehicleAttachment(float offsetY) {
         return this.attachment(EntityAttachmentType.VEHICLE, 0.0F, -offsetY, 0.0F);
      }

      public Builder nameTagAttachment(float offsetY) {
         return this.attachment(EntityAttachmentType.NAME_TAG, 0.0F, offsetY, 0.0F);
      }

      public Builder attachment(EntityAttachmentType type, float offsetX, float offsetY, float offsetZ) {
         this.attachments = this.attachments.add(type, offsetX, offsetY, offsetZ);
         return this;
      }

      public Builder attachment(EntityAttachmentType type, Vec3d offset) {
         this.attachments = this.attachments.add(type, offset);
         return this;
      }

      public Builder disableSummon() {
         this.summonable = false;
         return this;
      }

      public Builder disableSaving() {
         this.saveable = false;
         return this;
      }

      public Builder makeFireImmune() {
         this.fireImmune = true;
         return this;
      }

      public Builder allowSpawningInside(Block... blocks) {
         this.canSpawnInside = ImmutableSet.copyOf(blocks);
         return this;
      }

      public Builder spawnableFarFromPlayer() {
         this.spawnableFarFromPlayer = true;
         return this;
      }

      public Builder maxTrackingRange(int maxTrackingRange) {
         this.maxTrackingRange = maxTrackingRange;
         return this;
      }

      public Builder trackingTickInterval(int trackingTickInterval) {
         this.trackingTickInterval = trackingTickInterval;
         return this;
      }

      public Builder requires(FeatureFlag... features) {
         this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
         return this;
      }

      public Builder dropsNothing() {
         this.lootTable = RegistryKeyedValue.fixed(Optional.empty());
         return this;
      }

      public EntityType build(RegistryKey registryKey) {
         if (this.saveable) {
            Util.getChoiceType(TypeReferences.ENTITY_TREE, registryKey.getValue().toString());
         }

         return new EntityType(this.factory, this.spawnGroup, this.saveable, this.summonable, this.fireImmune, this.spawnableFarFromPlayer, this.canSpawnInside, this.dimensions.withAttachments(this.attachments), this.spawnBoxScale, this.maxTrackingRange, this.trackingTickInterval, (String)this.translationKey.get(registryKey), (Optional)this.lootTable.get(registryKey), this.requiredFeatures);
      }
   }

   @FunctionalInterface
   public interface EntityFactory {
      @Nullable
      Entity create(EntityType type, World world);
   }
}
