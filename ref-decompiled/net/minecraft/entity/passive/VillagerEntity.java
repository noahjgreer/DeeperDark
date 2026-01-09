package net.minecraft.entity.passive;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerGossipType;
import net.minecraft.village.VillagerGossips;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class VillagerEntity extends MerchantEntity implements InteractionObserver, VillagerDataContainer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final TrackedData VILLAGER_DATA;
   public static final int field_30602 = 12;
   public static final Map ITEM_FOOD_VALUES;
   private static final int field_30604 = 2;
   private static final int field_30605 = 10;
   private static final int field_30606 = 1200;
   private static final int field_30607 = 24000;
   private static final int field_30609 = 10;
   private static final int field_30610 = 5;
   private static final long field_30611 = 24000L;
   @VisibleForTesting
   public static final float field_30603 = 0.5F;
   private static final int field_57709 = 0;
   private static final byte field_57710 = 0;
   private static final int field_57711 = 0;
   private static final int field_57712 = 0;
   private static final int field_57713 = 0;
   private static final boolean DEFAULT_NATURAL = false;
   private int levelUpTimer;
   private boolean levelingUp;
   @Nullable
   private PlayerEntity lastCustomer;
   private boolean field_30612;
   private int foodLevel;
   private final VillagerGossips gossip;
   private long gossipStartTime;
   private long lastGossipDecayTime;
   private int experience;
   private long lastRestockTime;
   private int restocksToday;
   private long lastRestockCheckTime;
   private boolean natural;
   private static final ImmutableList MEMORY_MODULES;
   private static final ImmutableList SENSORS;
   public static final Map POINTS_OF_INTEREST;

   public VillagerEntity(EntityType entityType, World world) {
      this(entityType, world, VillagerType.PLAINS);
   }

   public VillagerEntity(EntityType entityType, World world, RegistryKey type) {
      this(entityType, world, (RegistryEntry)world.getRegistryManager().getEntryOrThrow(type));
   }

   public VillagerEntity(EntityType entityType, World world, RegistryEntry type) {
      super(entityType, world);
      this.foodLevel = 0;
      this.gossip = new VillagerGossips();
      this.lastGossipDecayTime = 0L;
      this.experience = 0;
      this.lastRestockTime = 0L;
      this.restocksToday = 0;
      this.natural = false;
      this.getNavigation().setCanOpenDoors(true);
      this.getNavigation().setCanSwim(true);
      this.getNavigation().setMaxFollowRange(48.0F);
      this.setCanPickUpLoot(true);
      this.setVillagerData(this.getVillagerData().withType(type).withProfession(world.getRegistryManager(), VillagerProfession.NONE));
   }

   public Brain getBrain() {
      return super.getBrain();
   }

   protected Brain.Profile createBrainProfile() {
      return Brain.createProfile(MEMORY_MODULES, SENSORS);
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      Brain brain = this.createBrainProfile().deserialize(dynamic);
      this.initBrain(brain);
      return brain;
   }

   public void reinitializeBrain(ServerWorld world) {
      Brain brain = this.getBrain();
      brain.stopAllTasks(world, this);
      this.brain = brain.copy();
      this.initBrain(this.getBrain());
   }

   private void initBrain(Brain brain) {
      RegistryEntry registryEntry = this.getVillagerData().profession();
      if (this.isBaby()) {
         brain.setSchedule(Schedule.VILLAGER_BABY);
         brain.setTaskList(Activity.PLAY, VillagerTaskListProvider.createPlayTasks(0.5F));
      } else {
         brain.setSchedule(Schedule.VILLAGER_DEFAULT);
         brain.setTaskList(Activity.WORK, VillagerTaskListProvider.createWorkTasks(registryEntry, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
      }

      brain.setTaskList(Activity.CORE, VillagerTaskListProvider.createCoreTasks(registryEntry, 0.5F));
      brain.setTaskList(Activity.MEET, VillagerTaskListProvider.createMeetTasks(registryEntry, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT)));
      brain.setTaskList(Activity.REST, VillagerTaskListProvider.createRestTasks(registryEntry, 0.5F));
      brain.setTaskList(Activity.IDLE, VillagerTaskListProvider.createIdleTasks(registryEntry, 0.5F));
      brain.setTaskList(Activity.PANIC, VillagerTaskListProvider.createPanicTasks(registryEntry, 0.5F));
      brain.setTaskList(Activity.PRE_RAID, VillagerTaskListProvider.createPreRaidTasks(registryEntry, 0.5F));
      brain.setTaskList(Activity.RAID, VillagerTaskListProvider.createRaidTasks(registryEntry, 0.5F));
      brain.setTaskList(Activity.HIDE, VillagerTaskListProvider.createHideTasks(registryEntry, 0.5F));
      brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
      brain.setDefaultActivity(Activity.IDLE);
      brain.doExclusively(Activity.IDLE);
      brain.refreshActivities(this.getWorld().getTimeOfDay(), this.getWorld().getTime());
   }

   protected void onGrowUp() {
      super.onGrowUp();
      if (this.getWorld() instanceof ServerWorld) {
         this.reinitializeBrain((ServerWorld)this.getWorld());
      }

   }

   public static DefaultAttributeContainer.Builder createVillagerAttributes() {
      return MobEntity.createMobAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.5);
   }

   public boolean isNatural() {
      return this.natural;
   }

   protected void mobTick(ServerWorld world) {
      Profiler profiler = Profilers.get();
      profiler.push("villagerBrain");
      this.getBrain().tick(world, this);
      profiler.pop();
      if (this.natural) {
         this.natural = false;
      }

      if (!this.hasCustomer() && this.levelUpTimer > 0) {
         --this.levelUpTimer;
         if (this.levelUpTimer <= 0) {
            if (this.levelingUp) {
               this.levelUp();
               this.levelingUp = false;
            }

            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
         }
      }

      if (this.lastCustomer != null) {
         world.handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
         world.sendEntityStatus(this, (byte)14);
         this.lastCustomer = null;
      }

      if (!this.isAiDisabled() && this.random.nextInt(100) == 0) {
         Raid raid = world.getRaidAt(this.getBlockPos());
         if (raid != null && raid.isActive() && !raid.isFinished()) {
            world.sendEntityStatus(this, (byte)42);
         }
      }

      if (this.getVillagerData().profession().matchesKey(VillagerProfession.NONE) && this.hasCustomer()) {
         this.resetCustomer();
      }

      super.mobTick(world);
   }

   public void tick() {
      super.tick();
      if (this.getHeadRollingTimeLeft() > 0) {
         this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
      }

      this.decayGossip();
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (!itemStack.isOf(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
         if (this.isBaby()) {
            this.sayNo();
            return ActionResult.SUCCESS;
         } else {
            if (!this.getWorld().isClient) {
               boolean bl = this.getOffers().isEmpty();
               if (hand == Hand.MAIN_HAND) {
                  if (bl) {
                     this.sayNo();
                  }

                  player.incrementStat(Stats.TALKED_TO_VILLAGER);
               }

               if (bl) {
                  return ActionResult.CONSUME;
               }

               this.beginTradeWith(player);
            }

            return ActionResult.SUCCESS;
         }
      } else {
         return super.interactMob(player, hand);
      }
   }

   private void sayNo() {
      this.setHeadRollingTimeLeft(40);
      if (!this.getWorld().isClient()) {
         this.playSound(SoundEvents.ENTITY_VILLAGER_NO);
      }

   }

   private void beginTradeWith(PlayerEntity customer) {
      this.prepareOffersFor(customer);
      this.setCustomer(customer);
      this.sendOffers(customer, this.getDisplayName(), this.getVillagerData().level());
   }

   public void setCustomer(@Nullable PlayerEntity customer) {
      boolean bl = this.getCustomer() != null && customer == null;
      super.setCustomer(customer);
      if (bl) {
         this.resetCustomer();
      }

   }

   protected void resetCustomer() {
      super.resetCustomer();
      this.clearSpecialPrices();
   }

   private void clearSpecialPrices() {
      if (!this.getWorld().isClient()) {
         Iterator var1 = this.getOffers().iterator();

         while(var1.hasNext()) {
            TradeOffer tradeOffer = (TradeOffer)var1.next();
            tradeOffer.clearSpecialPrice();
         }

      }
   }

   public boolean canRefreshTrades() {
      return true;
   }

   public void restock() {
      this.updateDemandBonus();
      Iterator var1 = this.getOffers().iterator();

      while(var1.hasNext()) {
         TradeOffer tradeOffer = (TradeOffer)var1.next();
         tradeOffer.resetUses();
      }

      this.sendOffersToCustomer();
      this.lastRestockTime = this.getWorld().getTime();
      ++this.restocksToday;
   }

   private void sendOffersToCustomer() {
      TradeOfferList tradeOfferList = this.getOffers();
      PlayerEntity playerEntity = this.getCustomer();
      if (playerEntity != null && !tradeOfferList.isEmpty()) {
         playerEntity.sendTradeOffers(playerEntity.currentScreenHandler.syncId, tradeOfferList, this.getVillagerData().level(), this.getExperience(), this.isLeveledMerchant(), this.canRefreshTrades());
      }

   }

   private boolean needsRestock() {
      Iterator var1 = this.getOffers().iterator();

      TradeOffer tradeOffer;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         tradeOffer = (TradeOffer)var1.next();
      } while(!tradeOffer.hasBeenUsed());

      return true;
   }

   private boolean canRestock() {
      return this.restocksToday == 0 || this.restocksToday < 2 && this.getWorld().getTime() > this.lastRestockTime + 2400L;
   }

   public boolean shouldRestock() {
      long l = this.lastRestockTime + 12000L;
      long m = this.getWorld().getTime();
      boolean bl = m > l;
      long n = this.getWorld().getTimeOfDay();
      if (this.lastRestockCheckTime > 0L) {
         long o = this.lastRestockCheckTime / 24000L;
         long p = n / 24000L;
         bl |= p > o;
      }

      this.lastRestockCheckTime = n;
      if (bl) {
         this.lastRestockTime = m;
         this.clearDailyRestockCount();
      }

      return this.canRestock() && this.needsRestock();
   }

   private void restockAndUpdateDemandBonus() {
      int i = 2 - this.restocksToday;
      if (i > 0) {
         Iterator var2 = this.getOffers().iterator();

         while(var2.hasNext()) {
            TradeOffer tradeOffer = (TradeOffer)var2.next();
            tradeOffer.resetUses();
         }
      }

      for(int j = 0; j < i; ++j) {
         this.updateDemandBonus();
      }

      this.sendOffersToCustomer();
   }

   private void updateDemandBonus() {
      Iterator var1 = this.getOffers().iterator();

      while(var1.hasNext()) {
         TradeOffer tradeOffer = (TradeOffer)var1.next();
         tradeOffer.updateDemandBonus();
      }

   }

   private void prepareOffersFor(PlayerEntity player) {
      int i = this.getReputation(player);
      if (i != 0) {
         Iterator var3 = this.getOffers().iterator();

         while(var3.hasNext()) {
            TradeOffer tradeOffer = (TradeOffer)var3.next();
            tradeOffer.increaseSpecialPrice(-MathHelper.floor((float)i * tradeOffer.getPriceMultiplier()));
         }
      }

      if (player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
         StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
         int j = statusEffectInstance.getAmplifier();
         Iterator var5 = this.getOffers().iterator();

         while(var5.hasNext()) {
            TradeOffer tradeOffer2 = (TradeOffer)var5.next();
            double d = 0.3 + 0.0625 * (double)j;
            int k = (int)Math.floor(d * (double)tradeOffer2.getOriginalFirstBuyItem().getCount());
            tradeOffer2.increaseSpecialPrice(-Math.max(k, 1));
         }
      }

   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VILLAGER_DATA, createVillagerData());
   }

   public static VillagerData createVillagerData() {
      return new VillagerData(Registries.VILLAGER_TYPE.getOrThrow(VillagerType.PLAINS), Registries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE), 1);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("VillagerData", VillagerData.CODEC, this.getVillagerData());
      view.putByte("FoodLevel", (byte)this.foodLevel);
      view.put("Gossips", VillagerGossips.CODEC, this.gossip);
      view.putInt("Xp", this.experience);
      view.putLong("LastRestock", this.lastRestockTime);
      view.putLong("LastGossipDecay", this.lastGossipDecayTime);
      view.putInt("RestocksToday", this.restocksToday);
      if (this.natural) {
         view.putBoolean("AssignProfessionWhenSpawned", true);
      }

   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.dataTracker.set(VILLAGER_DATA, (VillagerData)view.read("VillagerData", VillagerData.CODEC).orElseGet(VillagerEntity::createVillagerData));
      this.foodLevel = view.getByte("FoodLevel", (byte)0);
      this.gossip.clear();
      Optional var10000 = view.read("Gossips", VillagerGossips.CODEC);
      VillagerGossips var10001 = this.gossip;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::add);
      this.experience = view.getInt("Xp", 0);
      this.lastRestockTime = view.getLong("LastRestock", 0L);
      this.lastGossipDecayTime = view.getLong("LastGossipDecay", 0L);
      if (this.getWorld() instanceof ServerWorld) {
         this.reinitializeBrain((ServerWorld)this.getWorld());
      }

      this.restocksToday = view.getInt("RestocksToday", 0);
      this.natural = view.getBoolean("AssignProfessionWhenSpawned", false);
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return null;
      } else {
         return this.hasCustomer() ? SoundEvents.ENTITY_VILLAGER_TRADE : SoundEvents.ENTITY_VILLAGER_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_VILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VILLAGER_DEATH;
   }

   public void playWorkSound() {
      this.playSound(((VillagerProfession)this.getVillagerData().profession().value()).workSound());
   }

   public void setVillagerData(VillagerData villagerData) {
      VillagerData villagerData2 = this.getVillagerData();
      if (!villagerData2.profession().equals(villagerData.profession())) {
         this.offers = null;
      }

      this.dataTracker.set(VILLAGER_DATA, villagerData);
   }

   public VillagerData getVillagerData() {
      return (VillagerData)this.dataTracker.get(VILLAGER_DATA);
   }

   protected void afterUsing(TradeOffer offer) {
      int i = 3 + this.random.nextInt(4);
      this.experience += offer.getMerchantExperience();
      this.lastCustomer = this.getCustomer();
      if (this.canLevelUp()) {
         this.levelUpTimer = 40;
         this.levelingUp = true;
         i += 5;
      }

      if (offer.shouldRewardPlayerExperience()) {
         this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ(), i));
      }

   }

   public void setAttacker(@Nullable LivingEntity attacker) {
      if (attacker != null && this.getWorld() instanceof ServerWorld) {
         ((ServerWorld)this.getWorld()).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
         if (this.isAlive() && attacker instanceof PlayerEntity) {
            this.getWorld().sendEntityStatus(this, (byte)13);
         }
      }

      super.setAttacker(attacker);
   }

   public void onDeath(DamageSource damageSource) {
      LOGGER.info("Villager {} died, message: '{}'", this, damageSource.getDeathMessage(this).getString());
      Entity entity = damageSource.getAttacker();
      if (entity != null) {
         this.notifyDeath(entity);
      }

      this.releaseAllTickets();
      super.onDeath(damageSource);
   }

   private void releaseAllTickets() {
      this.releaseTicketFor(MemoryModuleType.HOME);
      this.releaseTicketFor(MemoryModuleType.JOB_SITE);
      this.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
      this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
   }

   private void notifyDeath(Entity killer) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         Optional optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
         if (!optional.isEmpty()) {
            LivingTargetCache var10000 = (LivingTargetCache)optional.get();
            Objects.requireNonNull(InteractionObserver.class);
            var10000.iterate(InteractionObserver.class::isInstance).forEach((observer) -> {
               serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver)observer);
            });
         }
      }
   }

   public void releaseTicketFor(MemoryModuleType pos) {
      if (this.getWorld() instanceof ServerWorld) {
         MinecraftServer minecraftServer = ((ServerWorld)this.getWorld()).getServer();
         this.brain.getOptionalRegisteredMemory(pos).ifPresent((posx) -> {
            ServerWorld serverWorld = minecraftServer.getWorld(posx.dimension());
            if (serverWorld != null) {
               PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
               Optional optional = pointOfInterestStorage.getType(posx.pos());
               BiPredicate biPredicate = (BiPredicate)POINTS_OF_INTEREST.get(pos);
               if (optional.isPresent() && biPredicate.test(this, (RegistryEntry)optional.get())) {
                  pointOfInterestStorage.releaseTicket(posx.pos());
                  DebugInfoSender.sendPointOfInterest(serverWorld, posx.pos());
               }

            }
         });
      }
   }

   public boolean isReadyToBreed() {
      return this.foodLevel + this.getAvailableFood() >= 12 && !this.isSleeping() && this.getBreedingAge() == 0;
   }

   private boolean canEatFood() {
      return this.foodLevel < 12;
   }

   private void consumeAvailableFood() {
      if (this.canEatFood() && this.getAvailableFood() != 0) {
         for(int i = 0; i < this.getInventory().size(); ++i) {
            ItemStack itemStack = this.getInventory().getStack(i);
            if (!itemStack.isEmpty()) {
               Integer integer = (Integer)ITEM_FOOD_VALUES.get(itemStack.getItem());
               if (integer != null) {
                  int j = itemStack.getCount();

                  for(int k = j; k > 0; --k) {
                     this.foodLevel += integer;
                     this.getInventory().removeStack(i, 1);
                     if (!this.canEatFood()) {
                        return;
                     }
                  }
               }
            }
         }

      }
   }

   public int getReputation(PlayerEntity player) {
      return this.gossip.getReputationFor(player.getUuid(), (gossipType) -> {
         return true;
      });
   }

   private void depleteFood(int amount) {
      this.foodLevel -= amount;
   }

   public void eatForBreeding() {
      this.consumeAvailableFood();
      this.depleteFood(12);
   }

   public void setOffers(TradeOfferList offers) {
      this.offers = offers;
   }

   private boolean canLevelUp() {
      int i = this.getVillagerData().level();
      return VillagerData.canLevelUp(i) && this.experience >= VillagerData.getUpperLevelExperience(i);
   }

   private void levelUp() {
      this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().level() + 1));
      this.fillRecipes();
   }

   protected Text getDefaultName() {
      return ((VillagerProfession)this.getVillagerData().profession().value()).id();
   }

   public void handleStatus(byte status) {
      if (status == 12) {
         this.produceParticles(ParticleTypes.HEART);
      } else if (status == 13) {
         this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
      } else if (status == 14) {
         this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
      } else if (status == 42) {
         this.produceParticles(ParticleTypes.SPLASH);
      } else {
         super.handleStatus(status);
      }

   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      if (spawnReason == SpawnReason.BREEDING) {
         this.setVillagerData(this.getVillagerData().withProfession(world.getRegistryManager(), VillagerProfession.NONE));
      }

      if (spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_ITEM_USE || SpawnReason.isAnySpawner(spawnReason) || spawnReason == SpawnReason.DISPENSER) {
         this.setVillagerData(this.getVillagerData().withType(world.getRegistryManager(), VillagerType.forBiome(world.getBiome(this.getBlockPos()))));
      }

      if (spawnReason == SpawnReason.STRUCTURE) {
         this.natural = true;
      }

      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   @Nullable
   public VillagerEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      double d = this.random.nextDouble();
      Object registryEntry;
      if (d < 0.5) {
         registryEntry = serverWorld.getRegistryManager().getEntryOrThrow(VillagerType.forBiome(serverWorld.getBiome(this.getBlockPos())));
      } else if (d < 0.75) {
         registryEntry = this.getVillagerData().type();
      } else {
         registryEntry = ((VillagerEntity)passiveEntity).getVillagerData().type();
      }

      VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, serverWorld, (RegistryEntry)registryEntry);
      villagerEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.BREEDING, (EntityData)null);
      return villagerEntity;
   }

   public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
      if (world.getDifficulty() != Difficulty.PEACEFUL) {
         LOGGER.info("Villager {} was struck by lightning {}.", this, lightning);
         WitchEntity witchEntity = (WitchEntity)this.convertTo(EntityType.WITCH, EntityConversionContext.create(this, false, false), (witch) -> {
            witch.initialize(world, world.getLocalDifficulty(witch.getBlockPos()), SpawnReason.CONVERSION, (EntityData)null);
            witch.setPersistent();
            this.releaseAllTickets();
         });
         if (witchEntity == null) {
            super.onStruckByLightning(world, lightning);
         }
      } else {
         super.onStruckByLightning(world, lightning);
      }

   }

   protected void loot(ServerWorld world, ItemEntity itemEntity) {
      InventoryOwner.pickUpItem(world, this, this, itemEntity);
   }

   public boolean canGather(ServerWorld world, ItemStack stack) {
      Item item = stack.getItem();
      return (stack.isIn(ItemTags.VILLAGER_PICKS_UP) || ((VillagerProfession)this.getVillagerData().profession().value()).gatherableItems().contains(item)) && this.getInventory().canInsert(stack);
   }

   public boolean canShareFoodForBreeding() {
      return this.getAvailableFood() >= 24;
   }

   public boolean needsFoodForBreeding() {
      return this.getAvailableFood() < 12;
   }

   private int getAvailableFood() {
      SimpleInventory simpleInventory = this.getInventory();
      return ITEM_FOOD_VALUES.entrySet().stream().mapToInt((item) -> {
         return simpleInventory.count((Item)item.getKey()) * (Integer)item.getValue();
      }).sum();
   }

   public boolean hasSeedToPlant() {
      return this.getInventory().containsAny((stack) -> {
         return stack.isIn(ItemTags.VILLAGER_PLANTABLE_SEEDS);
      });
   }

   protected void fillRecipes() {
      VillagerData villagerData = this.getVillagerData();
      RegistryKey registryKey = (RegistryKey)villagerData.profession().getKey().orElse((Object)null);
      if (registryKey != null) {
         Int2ObjectMap int2ObjectMap2;
         if (this.getWorld().getEnabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
            Int2ObjectMap int2ObjectMap = (Int2ObjectMap)TradeOffers.REBALANCED_PROFESSION_TO_LEVELED_TRADE.get(registryKey);
            int2ObjectMap2 = int2ObjectMap != null ? int2ObjectMap : (Int2ObjectMap)TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(registryKey);
         } else {
            int2ObjectMap2 = (Int2ObjectMap)TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(registryKey);
         }

         if (int2ObjectMap2 != null && !int2ObjectMap2.isEmpty()) {
            TradeOffers.Factory[] factorys = (TradeOffers.Factory[])int2ObjectMap2.get(villagerData.level());
            if (factorys != null) {
               TradeOfferList tradeOfferList = this.getOffers();
               this.fillRecipesFromPool(tradeOfferList, factorys, 2);
            }
         }
      }
   }

   public void talkWithVillager(ServerWorld world, VillagerEntity villager, long time) {
      if ((time < this.gossipStartTime || time >= this.gossipStartTime + 1200L) && (time < villager.gossipStartTime || time >= villager.gossipStartTime + 1200L)) {
         this.gossip.shareGossipFrom(villager.gossip, this.random, 10);
         this.gossipStartTime = time;
         villager.gossipStartTime = time;
         this.summonGolem(world, time, 5);
      }
   }

   private void decayGossip() {
      long l = this.getWorld().getTime();
      if (this.lastGossipDecayTime == 0L) {
         this.lastGossipDecayTime = l;
      } else if (l >= this.lastGossipDecayTime + 24000L) {
         this.gossip.decay();
         this.lastGossipDecayTime = l;
      }
   }

   public void summonGolem(ServerWorld world, long time, int requiredCount) {
      if (this.canSummonGolem(time)) {
         Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
         List list = world.getNonSpectatingEntities(VillagerEntity.class, box);
         List list2 = list.stream().filter((villager) -> {
            return villager.canSummonGolem(time);
         }).limit(5L).toList();
         if (list2.size() >= requiredCount) {
            if (!LargeEntitySpawnHelper.trySpawnAt(EntityType.IRON_GOLEM, SpawnReason.MOB_SUMMONED, world, this.getBlockPos(), 10, 8, 6, LargeEntitySpawnHelper.Requirements.IRON_GOLEM, false).isEmpty()) {
               list.forEach(GolemLastSeenSensor::rememberIronGolem);
            }
         }
      }
   }

   public boolean canSummonGolem(long time) {
      if (!this.hasRecentlySlept(this.getWorld().getTime())) {
         return false;
      } else {
         return !this.brain.hasMemoryModule(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
      }
   }

   public void onInteractionWith(EntityInteraction interaction, Entity entity) {
      if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
         this.gossip.startGossip(entity.getUuid(), VillagerGossipType.MAJOR_POSITIVE, 20);
         this.gossip.startGossip(entity.getUuid(), VillagerGossipType.MINOR_POSITIVE, 25);
      } else if (interaction == EntityInteraction.TRADE) {
         this.gossip.startGossip(entity.getUuid(), VillagerGossipType.TRADING, 2);
      } else if (interaction == EntityInteraction.VILLAGER_HURT) {
         this.gossip.startGossip(entity.getUuid(), VillagerGossipType.MINOR_NEGATIVE, 25);
      } else if (interaction == EntityInteraction.VILLAGER_KILLED) {
         this.gossip.startGossip(entity.getUuid(), VillagerGossipType.MAJOR_NEGATIVE, 25);
      }

   }

   public int getExperience() {
      return this.experience;
   }

   public void setExperience(int experience) {
      this.experience = experience;
   }

   private void clearDailyRestockCount() {
      this.restockAndUpdateDemandBonus();
      this.restocksToday = 0;
   }

   public VillagerGossips getGossip() {
      return this.gossip;
   }

   public void readGossipData(VillagerGossips gossips) {
      this.gossip.add(gossips);
   }

   protected void sendAiDebugData() {
      super.sendAiDebugData();
      DebugInfoSender.sendBrainDebugData(this);
   }

   public void sleep(BlockPos pos) {
      super.sleep(pos);
      this.brain.remember(MemoryModuleType.LAST_SLEPT, (Object)this.getWorld().getTime());
      this.brain.forget(MemoryModuleType.WALK_TARGET);
      this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }

   public void wakeUp() {
      super.wakeUp();
      this.brain.remember(MemoryModuleType.LAST_WOKEN, (Object)this.getWorld().getTime());
   }

   private boolean hasRecentlySlept(long worldTime) {
      Optional optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.LAST_SLEPT);
      return optional.filter((lastSlept) -> {
         return worldTime - lastSlept < 24000L;
      }).isPresent();
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.VILLAGER_VARIANT ? castComponentValue(type, this.getVillagerData().type()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.VILLAGER_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.VILLAGER_VARIANT) {
         RegistryEntry registryEntry = (RegistryEntry)castComponentValue(DataComponentTypes.VILLAGER_VARIANT, value);
         this.setVillagerData(this.getVillagerData().withType(registryEntry));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
      ITEM_FOOD_VALUES = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
      MEMORY_MODULES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY});
      SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
      POINTS_OF_INTEREST = ImmutableMap.of(MemoryModuleType.HOME, (villager, registryEntry) -> {
         return registryEntry.matchesKey(PointOfInterestTypes.HOME);
      }, MemoryModuleType.JOB_SITE, (villager, registryEntry) -> {
         return ((VillagerProfession)villager.getVillagerData().profession().value()).heldWorkstation().test(registryEntry);
      }, MemoryModuleType.POTENTIAL_JOB_SITE, (villager, registryEntry) -> {
         return VillagerProfession.IS_ACQUIRABLE_JOB_SITE.test(registryEntry);
      }, MemoryModuleType.MEETING_POINT, (villager, registryEntry) -> {
         return registryEntry.matchesKey(PointOfInterestTypes.MEETING);
      });
   }
}
