package net.minecraft.entity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElytraFlightController;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerWaypointHandler;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.waypoint.ServerWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class LivingEntity extends Entity implements Attackable, ServerWaypoint {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String ACTIVE_EFFECTS_KEY = "active_effects";
   public static final String ATTRIBUTES_KEY = "attributes";
   public static final String SLEEPING_POS_KEY = "sleeping_pos";
   public static final String EQUIPMENT_KEY = "equipment";
   public static final String BRAIN_KEY = "Brain";
   public static final String FALL_FLYING_KEY = "FallFlying";
   public static final String HURT_TIME_KEY = "HurtTime";
   public static final String DEATH_TIME_KEY = "DeathTime";
   public static final String HURT_BY_TIMESTAMP_KEY = "HurtByTimestamp";
   public static final String HEALTH_KEY = "Health";
   private static final Identifier POWDER_SNOW_SPEED_MODIFIER_ID = Identifier.ofVanilla("powder_snow");
   private static final Identifier SPRINTING_SPEED_MODIFIER_ID = Identifier.ofVanilla("sprinting");
   private static final EntityAttributeModifier SPRINTING_SPEED_BOOST;
   public static final int EQUIPMENT_SLOT_ID = 98;
   public static final int field_30072 = 100;
   public static final int field_48827 = 105;
   public static final int field_55952 = 106;
   public static final int GLOWING_FLAG = 6;
   public static final int field_30074 = 100;
   private static final int field_30078 = 40;
   public static final double field_30075 = 0.003;
   public static final double GRAVITY = 0.08;
   public static final int DEATH_TICKS = 20;
   protected static final float field_56256 = 0.98F;
   private static final int field_30080 = 10;
   private static final int field_30081 = 2;
   public static final float field_44874 = 0.42F;
   private static final double MAX_ENTITY_VIEWING_DISTANCE = 128.0;
   protected static final int USING_ITEM_FLAG = 1;
   protected static final int OFF_HAND_ACTIVE_FLAG = 2;
   protected static final int USING_RIPTIDE_FLAG = 4;
   protected static final TrackedData LIVING_FLAGS;
   private static final TrackedData HEALTH;
   private static final TrackedData POTION_SWIRLS;
   private static final TrackedData POTION_SWIRLS_AMBIENT;
   private static final TrackedData STUCK_ARROW_COUNT;
   private static final TrackedData STINGER_COUNT;
   private static final TrackedData SLEEPING_POSITION;
   private static final int field_49793 = 15;
   protected static final EntityDimensions SLEEPING_DIMENSIONS;
   public static final float BABY_SCALE_FACTOR = 0.5F;
   public static final float field_47756 = 0.5F;
   public static final Predicate NOT_WEARING_GAZE_DISGUISE_PREDICATE;
   private static final Dynamic BRAIN;
   private final AttributeContainer attributes;
   private final DamageTracker damageTracker = new DamageTracker(this);
   private final Map activeStatusEffects = Maps.newHashMap();
   private final Map lastEquipmentStacks = Util.mapEnum(EquipmentSlot.class, (slot) -> {
      return ItemStack.EMPTY;
   });
   public boolean handSwinging;
   private boolean noDrag = false;
   public Hand preferredHand;
   public int handSwingTicks;
   public int stuckArrowTimer;
   public int stuckStingerTimer;
   public int hurtTime;
   public int maxHurtTime;
   public int deathTime;
   public float lastHandSwingProgress;
   public float handSwingProgress;
   protected int lastAttackedTicks;
   public final LimbAnimator limbAnimator = new LimbAnimator();
   public final int defaultMaxHealth = 20;
   public float bodyYaw;
   public float lastBodyYaw;
   public float headYaw;
   public float lastHeadYaw;
   public final ElytraFlightController elytraFlightController = new ElytraFlightController(this);
   @Nullable
   protected LazyEntityReference attackingPlayer;
   protected int playerHitTimer;
   protected boolean dead;
   protected int despawnCounter;
   protected float lastDamageTaken;
   protected boolean jumping;
   public float sidewaysSpeed;
   public float upwardSpeed;
   public float forwardSpeed;
   protected PositionInterpolator interpolator = new PositionInterpolator(this);
   protected double serverHeadYaw;
   protected int headTrackingIncrements;
   private boolean effectsChanged = true;
   @Nullable
   private LazyEntityReference attackerReference;
   private int lastAttackedTime;
   @Nullable
   private LivingEntity attacking;
   private int lastAttackTime;
   private float movementSpeed;
   private int jumpingCooldown;
   private float absorptionAmount;
   protected ItemStack activeItemStack;
   protected int itemUseTimeLeft;
   protected int glidingTicks;
   private BlockPos lastBlockPos;
   private Optional climbingPos;
   @Nullable
   private DamageSource lastDamageSource;
   private long lastDamageTime;
   protected int riptideTicks;
   protected float riptideAttackDamage;
   @Nullable
   protected ItemStack riptideStack;
   private float leaningPitch;
   private float lastLeaningPitch;
   protected Brain brain;
   private boolean experienceDroppingDisabled;
   private final EnumMap locationBasedEnchantmentEffects;
   protected final EntityEquipment equipment;
   private Waypoint.Config waypointConfig;

   protected LivingEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.activeItemStack = ItemStack.EMPTY;
      this.climbingPos = Optional.empty();
      this.locationBasedEnchantmentEffects = new EnumMap(EquipmentSlot.class);
      this.waypointConfig = new Waypoint.Config();
      this.attributes = new AttributeContainer(DefaultAttributeRegistry.get(entityType));
      this.setHealth(this.getMaxHealth());
      this.equipment = this.createEquipment();
      this.intersectionChecked = true;
      this.refreshPosition();
      this.setYaw((float)(Math.random() * 6.2831854820251465));
      this.headYaw = this.getYaw();
      this.brain = this.deserializeBrain(BRAIN);
   }

   @Contract(
      pure = true
   )
   protected EntityEquipment createEquipment() {
      return new EntityEquipment();
   }

   public Brain getBrain() {
      return this.brain;
   }

   protected Brain.Profile createBrainProfile() {
      return Brain.createProfile(ImmutableList.of(), ImmutableList.of());
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return this.createBrainProfile().deserialize(dynamic);
   }

   public void kill(ServerWorld world) {
      this.damage(world, this.getDamageSources().genericKill(), Float.MAX_VALUE);
   }

   public boolean canTarget(EntityType type) {
      return true;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(LIVING_FLAGS, (byte)0);
      builder.add(POTION_SWIRLS, List.of());
      builder.add(POTION_SWIRLS_AMBIENT, false);
      builder.add(STUCK_ARROW_COUNT, 0);
      builder.add(STINGER_COUNT, 0);
      builder.add(HEALTH, 1.0F);
      builder.add(SLEEPING_POSITION, Optional.empty());
   }

   public static DefaultAttributeContainer.Builder createLivingAttributes() {
      return DefaultAttributeContainer.builder().add(EntityAttributes.MAX_HEALTH).add(EntityAttributes.KNOCKBACK_RESISTANCE).add(EntityAttributes.MOVEMENT_SPEED).add(EntityAttributes.ARMOR).add(EntityAttributes.ARMOR_TOUGHNESS).add(EntityAttributes.MAX_ABSORPTION).add(EntityAttributes.STEP_HEIGHT).add(EntityAttributes.SCALE).add(EntityAttributes.GRAVITY).add(EntityAttributes.SAFE_FALL_DISTANCE).add(EntityAttributes.FALL_DAMAGE_MULTIPLIER).add(EntityAttributes.JUMP_STRENGTH).add(EntityAttributes.OXYGEN_BONUS).add(EntityAttributes.BURNING_TIME).add(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE).add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY).add(EntityAttributes.MOVEMENT_EFFICIENCY).add(EntityAttributes.ATTACK_KNOCKBACK).add(EntityAttributes.CAMERA_DISTANCE).add(EntityAttributes.WAYPOINT_TRANSMIT_RANGE);
   }

   protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
      if (!this.isTouchingWater()) {
         this.checkWaterState();
      }

      World var7 = this.getWorld();
      if (var7 instanceof ServerWorld serverWorld) {
         if (onGround && this.fallDistance > 0.0) {
            this.applyMovementEffects(serverWorld, landedPosition);
            double d = (double)Math.max(0, MathHelper.floor(this.getUnsafeFallDistance(this.fallDistance)));
            if (d > 0.0 && !state.isAir()) {
               double e = this.getX();
               double f = this.getY();
               double g = this.getZ();
               BlockPos blockPos = this.getBlockPos();
               double h;
               if (landedPosition.getX() != blockPos.getX() || landedPosition.getZ() != blockPos.getZ()) {
                  h = e - (double)landedPosition.getX() - 0.5;
                  double i = g - (double)landedPosition.getZ() - 0.5;
                  double j = Math.max(Math.abs(h), Math.abs(i));
                  e = (double)landedPosition.getX() + 0.5 + h / j * 0.5;
                  g = (double)landedPosition.getZ() + 0.5 + i / j * 0.5;
               }

               h = Math.min(0.20000000298023224 + d / 15.0, 2.5);
               int k = (int)(150.0 * h);
               serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), e, f, g, k, 0.0, 0.0, 0.0, 0.15000000596046448);
            }
         }
      }

      super.fall(heightDifference, onGround, state, landedPosition);
      if (onGround) {
         this.climbingPos = Optional.empty();
      }

   }

   public boolean canBreatheInWater() {
      return this.getType().isIn(EntityTypeTags.CAN_BREATHE_UNDER_WATER);
   }

   public float getLeaningPitch(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastLeaningPitch, this.leaningPitch);
   }

   public boolean hasLandedInFluid() {
      return this.getVelocity().getY() < 9.999999747378752E-6 && this.isInFluid();
   }

   public void baseTick() {
      this.lastHandSwingProgress = this.handSwingProgress;
      if (this.firstUpdate) {
         this.getSleepingPosition().ifPresent(this::setPositionInBed);
      }

      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         EnchantmentHelper.onTick(serverWorld, this);
      }

      super.baseTick();
      Profiler profiler = Profilers.get();
      profiler.push("livingEntityBaseTick");
      if (this.isFireImmune() || this.getWorld().isClient) {
         this.extinguish();
      }

      if (this.isAlive()) {
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld2 = (ServerWorld)var3;
            boolean bl = this instanceof PlayerEntity;
            if (this.isInsideWall()) {
               this.damage(serverWorld2, this.getDamageSources().inWall(), 1.0F);
            } else if (bl && !serverWorld2.getWorldBorder().contains(this.getBoundingBox())) {
               double d = serverWorld2.getWorldBorder().getDistanceInsideBorder(this) + serverWorld2.getWorldBorder().getSafeZone();
               if (d < 0.0) {
                  double e = serverWorld2.getWorldBorder().getDamagePerBlock();
                  if (e > 0.0) {
                     this.damage(serverWorld2, this.getDamageSources().outsideBorder(), (float)Math.max(1, MathHelper.floor(-d * e)));
                  }
               }
            }

            if (this.isSubmergedIn(FluidTags.WATER) && !serverWorld2.getBlockState(BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ())).isOf(Blocks.BUBBLE_COLUMN)) {
               boolean bl2 = !this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(this) && (!bl || !((PlayerEntity)this).getAbilities().invulnerable);
               if (bl2) {
                  this.setAir(this.getNextAirUnderwater(this.getAir()));
                  if (this.getAir() == -20) {
                     this.setAir(0);
                     serverWorld2.sendEntityStatus(this, (byte)67);
                     this.damage(serverWorld2, this.getDamageSources().drown(), 2.0F);
                  }
               } else if (this.getAir() < this.getMaxAir()) {
                  this.setAir(this.getNextAirOnLand(this.getAir()));
               }

               if (this.hasVehicle() && this.getVehicle() != null && this.getVehicle().shouldDismountUnderwater()) {
                  this.stopRiding();
               }
            } else if (this.getAir() < this.getMaxAir()) {
               this.setAir(this.getNextAirOnLand(this.getAir()));
            }

            BlockPos blockPos = this.getBlockPos();
            if (!Objects.equal(this.lastBlockPos, blockPos)) {
               this.lastBlockPos = blockPos;
               this.applyMovementEffects(serverWorld2, blockPos);
            }
         }
      }

      if (this.hurtTime > 0) {
         --this.hurtTime;
      }

      if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
         --this.timeUntilRegen;
      }

      if (this.isDead() && this.getWorld().shouldUpdatePostDeath(this)) {
         this.updatePostDeath();
      }

      if (this.playerHitTimer > 0) {
         --this.playerHitTimer;
      } else {
         this.attackingPlayer = null;
      }

      if (this.attacking != null && !this.attacking.isAlive()) {
         this.attacking = null;
      }

      LivingEntity livingEntity = this.getAttacker();
      if (livingEntity != null) {
         if (!livingEntity.isAlive()) {
            this.setAttacker((LivingEntity)null);
         } else if (this.age - this.lastAttackedTime > 100) {
            this.setAttacker((LivingEntity)null);
         }
      }

      this.tickStatusEffects();
      this.lastHeadYaw = this.headYaw;
      this.lastBodyYaw = this.bodyYaw;
      this.lastYaw = this.getYaw();
      this.lastPitch = this.getPitch();
      profiler.pop();
   }

   protected float getVelocityMultiplier() {
      return MathHelper.lerp((float)this.getAttributeValue(EntityAttributes.MOVEMENT_EFFICIENCY), super.getVelocityMultiplier(), 1.0F);
   }

   public float getLuck() {
      return 0.0F;
   }

   protected void removePowderSnowSlow() {
      EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
      if (entityAttributeInstance != null) {
         if (entityAttributeInstance.getModifier(POWDER_SNOW_SPEED_MODIFIER_ID) != null) {
            entityAttributeInstance.removeModifier(POWDER_SNOW_SPEED_MODIFIER_ID);
         }

      }
   }

   protected void addPowderSnowSlowIfNeeded() {
      if (!this.getLandingBlockState().isAir()) {
         int i = this.getFrozenTicks();
         if (i > 0) {
            EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            if (entityAttributeInstance == null) {
               return;
            }

            float f = -0.05F * this.getFreezingScale();
            entityAttributeInstance.addTemporaryModifier(new EntityAttributeModifier(POWDER_SNOW_SPEED_MODIFIER_ID, (double)f, EntityAttributeModifier.Operation.ADD_VALUE));
         }
      }

   }

   protected void applyMovementEffects(ServerWorld world, BlockPos pos) {
      EnchantmentHelper.applyLocationBasedEffects(world, this);
   }

   public boolean isBaby() {
      return false;
   }

   public float getScaleFactor() {
      return this.isBaby() ? 0.5F : 1.0F;
   }

   public final float getScale() {
      AttributeContainer attributeContainer = this.getAttributes();
      return attributeContainer == null ? 1.0F : this.clampScale((float)attributeContainer.getValue(EntityAttributes.SCALE));
   }

   protected float clampScale(float scale) {
      return scale;
   }

   public boolean shouldSwimInFluids() {
      return true;
   }

   protected void updatePostDeath() {
      ++this.deathTime;
      if (this.deathTime >= 20 && !this.getWorld().isClient() && !this.isRemoved()) {
         this.getWorld().sendEntityStatus(this, (byte)60);
         this.remove(Entity.RemovalReason.KILLED);
      }

   }

   public boolean shouldDropExperience() {
      return !this.isBaby();
   }

   protected boolean shouldDropLoot() {
      return !this.isBaby();
   }

   protected int getNextAirUnderwater(int air) {
      EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.OXYGEN_BONUS);
      double d;
      if (entityAttributeInstance != null) {
         d = entityAttributeInstance.getValue();
      } else {
         d = 0.0;
      }

      return d > 0.0 && this.random.nextDouble() >= 1.0 / (d + 1.0) ? air : air - 1;
   }

   protected int getNextAirOnLand(int air) {
      return Math.min(air + 4, this.getMaxAir());
   }

   public final int getExperienceToDrop(ServerWorld world, @Nullable Entity attacker) {
      return EnchantmentHelper.getMobExperience(world, attacker, this, this.getExperienceToDrop(world));
   }

   protected int getExperienceToDrop(ServerWorld world) {
      return 0;
   }

   protected boolean shouldAlwaysDropExperience() {
      return false;
   }

   @Nullable
   public LivingEntity getAttacker() {
      return (LivingEntity)LazyEntityReference.resolve(this.attackerReference, this.getWorld(), LivingEntity.class);
   }

   @Nullable
   public PlayerEntity getAttackingPlayer() {
      return (PlayerEntity)LazyEntityReference.resolve(this.attackingPlayer, this.getWorld(), PlayerEntity.class);
   }

   public LivingEntity getLastAttacker() {
      return this.getAttacker();
   }

   public int getLastAttackedTime() {
      return this.lastAttackedTime;
   }

   public void setAttacking(PlayerEntity attackingPlayer, int playerHitTimer) {
      this.setAttacking(new LazyEntityReference(attackingPlayer), playerHitTimer);
   }

   public void setAttacking(UUID attackingPlayer, int playerHitTimer) {
      this.setAttacking(new LazyEntityReference(attackingPlayer), playerHitTimer);
   }

   private void setAttacking(LazyEntityReference attackingPlayer, int playerHitTimer) {
      this.attackingPlayer = attackingPlayer;
      this.playerHitTimer = playerHitTimer;
   }

   public void setAttacker(@Nullable LivingEntity attacker) {
      this.attackerReference = attacker != null ? new LazyEntityReference(attacker) : null;
      this.lastAttackedTime = this.age;
   }

   @Nullable
   public LivingEntity getAttacking() {
      return this.attacking;
   }

   public int getLastAttackTime() {
      return this.lastAttackTime;
   }

   public void onAttacking(Entity target) {
      if (target instanceof LivingEntity) {
         this.attacking = (LivingEntity)target;
      } else {
         this.attacking = null;
      }

      this.lastAttackTime = this.age;
   }

   public int getDespawnCounter() {
      return this.despawnCounter;
   }

   public void setDespawnCounter(int despawnCounter) {
      this.despawnCounter = despawnCounter;
   }

   public boolean hasNoDrag() {
      return this.noDrag;
   }

   public void setNoDrag(boolean noDrag) {
      this.noDrag = noDrag;
   }

   protected boolean isArmorSlot(EquipmentSlot slot) {
      return true;
   }

   public void onEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
      if (!this.getWorld().isClient() && !this.isSpectator()) {
         if (!ItemStack.areItemsAndComponentsEqual(oldStack, newStack) && !this.firstUpdate) {
            EquippableComponent equippableComponent = (EquippableComponent)newStack.get(DataComponentTypes.EQUIPPABLE);
            if (!this.isSilent() && equippableComponent != null && slot == equippableComponent.slot()) {
               this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (RegistryEntry)this.getEquipSound(slot, newStack, equippableComponent), this.getSoundCategory(), 1.0F, 1.0F, this.random.nextLong());
            }

            if (this.isArmorSlot(slot)) {
               this.emitGameEvent(equippableComponent != null ? GameEvent.EQUIP : GameEvent.UNEQUIP);
            }

         }
      }
   }

   protected RegistryEntry getEquipSound(EquipmentSlot slot, ItemStack stack, EquippableComponent equippableComponent) {
      return equippableComponent.equipSound();
   }

   public void remove(Entity.RemovalReason reason) {
      if (reason == Entity.RemovalReason.KILLED || reason == Entity.RemovalReason.DISCARDED) {
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            this.onRemoval(serverWorld, reason);
         }
      }

      super.remove(reason);
      this.brain.forgetAll();
   }

   public void onRemove(Entity.RemovalReason reason) {
      super.onRemove(reason);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         serverWorld.getWaypointHandler().onUntrack((ServerWaypoint)this);
      }

   }

   protected void onRemoval(ServerWorld world, Entity.RemovalReason reason) {
      Iterator var3 = this.getStatusEffects().iterator();

      while(var3.hasNext()) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var3.next();
         statusEffectInstance.onEntityRemoval(world, this, reason);
      }

      this.activeStatusEffects.clear();
   }

   protected void writeCustomData(WriteView view) {
      view.putFloat("Health", this.getHealth());
      view.putShort("HurtTime", (short)this.hurtTime);
      view.putInt("HurtByTimestamp", this.lastAttackedTime);
      view.putShort("DeathTime", (short)this.deathTime);
      view.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
      view.put("attributes", EntityAttributeInstance.Packed.LIST_CODEC, this.getAttributes().pack());
      if (!this.activeStatusEffects.isEmpty()) {
         view.put("active_effects", StatusEffectInstance.CODEC.listOf(), List.copyOf(this.activeStatusEffects.values()));
      }

      view.putBoolean("FallFlying", this.isGliding());
      this.getSleepingPosition().ifPresent((pos) -> {
         view.put("sleeping_pos", BlockPos.CODEC, pos);
      });
      DataResult dataResult = this.brain.encode(NbtOps.INSTANCE).map((nbtElement) -> {
         return new Dynamic(NbtOps.INSTANCE, nbtElement);
      });
      Logger var10001 = LOGGER;
      java.util.Objects.requireNonNull(var10001);
      dataResult.resultOrPartial(var10001::error).ifPresent((brain) -> {
         view.put("Brain", Codec.PASSTHROUGH, brain);
      });
      if (this.attackingPlayer != null) {
         this.attackingPlayer.writeData(view, "last_hurt_by_player");
         view.putInt("last_hurt_by_player_memory_time", this.playerHitTimer);
      }

      if (this.attackerReference != null) {
         this.attackerReference.writeData(view, "last_hurt_by_mob");
         view.putInt("ticks_since_last_hurt_by_mob", this.age - this.lastAttackedTime);
      }

      if (!this.equipment.isEmpty()) {
         view.put("equipment", EntityEquipment.CODEC, this.equipment);
      }

      if (this.waypointConfig.hasCustomStyle()) {
         view.put("locator_bar_icon", Waypoint.Config.CODEC, this.waypointConfig);
      }

   }

   @Nullable
   public ItemEntity dropItem(ItemStack stack, boolean dropAtSelf, boolean retainOwnership) {
      if (stack.isEmpty()) {
         return null;
      } else if (this.getWorld().isClient) {
         this.swingHand(Hand.MAIN_HAND);
         return null;
      } else {
         ItemEntity itemEntity = this.createItemEntity(stack, dropAtSelf, retainOwnership);
         if (itemEntity != null) {
            this.getWorld().spawnEntity(itemEntity);
         }

         return itemEntity;
      }
   }

   protected void readCustomData(ReadView view) {
      this.setAbsorptionAmountUnclamped(view.getFloat("AbsorptionAmount", 0.0F));
      if (this.getWorld() != null && !this.getWorld().isClient) {
         Optional var10000 = view.read("attributes", EntityAttributeInstance.Packed.LIST_CODEC);
         AttributeContainer var10001 = this.getAttributes();
         java.util.Objects.requireNonNull(var10001);
         var10000.ifPresent(var10001::unpack);
      }

      List list = (List)view.read("active_effects", StatusEffectInstance.CODEC.listOf()).orElse(List.of());
      this.activeStatusEffects.clear();
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var3.next();
         this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
      }

      this.setHealth(view.getFloat("Health", this.getMaxHealth()));
      this.hurtTime = view.getShort("HurtTime", (short)0);
      this.deathTime = view.getShort("DeathTime", (short)0);
      this.lastAttackedTime = view.getInt("HurtByTimestamp", 0);
      view.getOptionalString("Team").ifPresent((team) -> {
         Scoreboard scoreboard = this.getWorld().getScoreboard();
         Team team2 = scoreboard.getTeam(team);
         boolean bl = team2 != null && scoreboard.addScoreHolderToTeam(this.getUuidAsString(), team2);
         if (!bl) {
            LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", team);
         }

      });
      this.setFlag(7, view.getBoolean("FallFlying", false));
      view.read("sleeping_pos", BlockPos.CODEC).ifPresentOrElse((pos) -> {
         this.setSleepingPosition(pos);
         this.dataTracker.set(POSE, EntityPose.SLEEPING);
         if (!this.firstUpdate) {
            this.setPositionInBed(pos);
         }

      }, this::clearSleepingPosition);
      view.read("Brain", Codec.PASSTHROUGH).ifPresent((brain) -> {
         this.brain = this.deserializeBrain(brain);
      });
      this.attackingPlayer = LazyEntityReference.fromData(view, "last_hurt_by_player");
      this.playerHitTimer = view.getInt("last_hurt_by_player_memory_time", 0);
      this.attackerReference = LazyEntityReference.fromData(view, "last_hurt_by_mob");
      this.lastAttackedTime = view.getInt("ticks_since_last_hurt_by_mob", 0) + this.age;
      this.equipment.copyFrom((EntityEquipment)view.read("equipment", EntityEquipment.CODEC).orElseGet(EntityEquipment::new));
      this.waypointConfig = (Waypoint.Config)view.read("locator_bar_icon", Waypoint.Config.CODEC).orElseGet(Waypoint.Config::new);
   }

   protected void tickStatusEffects() {
      World var2 = this.getWorld();
      Iterator iterator;
      if (var2 instanceof ServerWorld serverWorld) {
         iterator = this.activeStatusEffects.keySet().iterator();

         try {
            while(iterator.hasNext()) {
               RegistryEntry registryEntry = (RegistryEntry)iterator.next();
               StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(registryEntry);
               if (!statusEffectInstance.update(serverWorld, this, () -> {
                  this.onStatusEffectUpgraded(statusEffectInstance, true, (Entity)null);
               })) {
                  iterator.remove();
                  this.onStatusEffectsRemoved(List.of(statusEffectInstance));
               } else if (statusEffectInstance.getDuration() % 600 == 0) {
                  this.onStatusEffectUpgraded(statusEffectInstance, false, (Entity)null);
               }
            }
         } catch (ConcurrentModificationException var6) {
         }

         if (this.effectsChanged) {
            this.updatePotionVisibility();
            this.updateGlowing();
            this.effectsChanged = false;
         }
      } else {
         iterator = this.activeStatusEffects.values().iterator();

         while(iterator.hasNext()) {
            StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)iterator.next();
            statusEffectInstance2.tickClient();
         }

         List list = (List)this.dataTracker.get(POTION_SWIRLS);
         if (!list.isEmpty()) {
            boolean bl = (Boolean)this.dataTracker.get(POTION_SWIRLS_AMBIENT);
            int i = this.isInvisible() ? 15 : 4;
            int j = bl ? 5 : 1;
            if (this.random.nextInt(i * j) == 0) {
               this.getWorld().addParticleClient((ParticleEffect)Util.getRandom(list, this.random), this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 1.0, 1.0, 1.0);
            }
         }
      }

   }

   protected void updatePotionVisibility() {
      if (this.activeStatusEffects.isEmpty()) {
         this.clearPotionSwirls();
         this.setInvisible(false);
      } else {
         this.setInvisible(this.hasStatusEffect(StatusEffects.INVISIBILITY));
         this.updatePotionSwirls();
      }
   }

   private void updatePotionSwirls() {
      List list = this.activeStatusEffects.values().stream().filter(StatusEffectInstance::shouldShowParticles).map(StatusEffectInstance::createParticle).toList();
      this.dataTracker.set(POTION_SWIRLS, list);
      this.dataTracker.set(POTION_SWIRLS_AMBIENT, containsOnlyAmbientEffects(this.activeStatusEffects.values()));
   }

   private void updateGlowing() {
      boolean bl = this.isGlowing();
      if (this.getFlag(6) != bl) {
         this.setFlag(6, bl);
      }

   }

   public double getAttackDistanceScalingFactor(@Nullable Entity entity) {
      double d = 1.0;
      if (this.isSneaky()) {
         d *= 0.8;
      }

      if (this.isInvisible()) {
         float f = this.getArmorVisibility();
         if (f < 0.1F) {
            f = 0.1F;
         }

         d *= 0.7 * (double)f;
      }

      if (entity != null) {
         ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
         EntityType entityType = entity.getType();
         if (entityType == EntityType.SKELETON && itemStack.isOf(Items.SKELETON_SKULL) || entityType == EntityType.ZOMBIE && itemStack.isOf(Items.ZOMBIE_HEAD) || entityType == EntityType.PIGLIN && itemStack.isOf(Items.PIGLIN_HEAD) || entityType == EntityType.PIGLIN_BRUTE && itemStack.isOf(Items.PIGLIN_HEAD) || entityType == EntityType.CREEPER && itemStack.isOf(Items.CREEPER_HEAD)) {
            d *= 0.5;
         }
      }

      return d;
   }

   public boolean canTarget(LivingEntity target) {
      return target instanceof PlayerEntity && this.getWorld().getDifficulty() == Difficulty.PEACEFUL ? false : target.canTakeDamage();
   }

   public boolean canTakeDamage() {
      return !this.isInvulnerable() && this.isPartOfGame();
   }

   public boolean isPartOfGame() {
      return !this.isSpectator() && this.isAlive();
   }

   public static boolean containsOnlyAmbientEffects(Collection effects) {
      Iterator var1 = effects.iterator();

      StatusEffectInstance statusEffectInstance;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         statusEffectInstance = (StatusEffectInstance)var1.next();
      } while(!statusEffectInstance.shouldShowParticles() || statusEffectInstance.isAmbient());

      return false;
   }

   protected void clearPotionSwirls() {
      this.dataTracker.set(POTION_SWIRLS, List.of());
   }

   public boolean clearStatusEffects() {
      if (this.getWorld().isClient) {
         return false;
      } else if (this.activeStatusEffects.isEmpty()) {
         return false;
      } else {
         Map map = Maps.newHashMap(this.activeStatusEffects);
         this.activeStatusEffects.clear();
         this.onStatusEffectsRemoved(map.values());
         return true;
      }
   }

   public Collection getStatusEffects() {
      return this.activeStatusEffects.values();
   }

   public Map getActiveStatusEffects() {
      return this.activeStatusEffects;
   }

   public boolean hasStatusEffect(RegistryEntry effect) {
      return this.activeStatusEffects.containsKey(effect);
   }

   @Nullable
   public StatusEffectInstance getStatusEffect(RegistryEntry effect) {
      return (StatusEffectInstance)this.activeStatusEffects.get(effect);
   }

   public float getEffectFadeFactor(RegistryEntry effect, float tickProgress) {
      StatusEffectInstance statusEffectInstance = this.getStatusEffect(effect);
      return statusEffectInstance != null ? statusEffectInstance.getFadeFactor(this, tickProgress) : 0.0F;
   }

   public final boolean addStatusEffect(StatusEffectInstance effect) {
      return this.addStatusEffect(effect, (Entity)null);
   }

   public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
      if (!this.canHaveStatusEffect(effect)) {
         return false;
      } else {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(effect.getEffectType());
         boolean bl = false;
         if (statusEffectInstance == null) {
            this.activeStatusEffects.put(effect.getEffectType(), effect);
            this.onStatusEffectApplied(effect, source);
            bl = true;
            effect.playApplySound(this);
         } else if (statusEffectInstance.upgrade(effect)) {
            this.onStatusEffectUpgraded(statusEffectInstance, true, source);
            bl = true;
         }

         effect.onApplied(this);
         return bl;
      }
   }

   public boolean canHaveStatusEffect(StatusEffectInstance effect) {
      if (this.getType().isIn(EntityTypeTags.IMMUNE_TO_INFESTED)) {
         return !effect.equals(StatusEffects.INFESTED);
      } else if (this.getType().isIn(EntityTypeTags.IMMUNE_TO_OOZING)) {
         return !effect.equals(StatusEffects.OOZING);
      } else if (!this.getType().isIn(EntityTypeTags.IGNORES_POISON_AND_REGEN)) {
         return true;
      } else {
         return !effect.equals(StatusEffects.REGENERATION) && !effect.equals(StatusEffects.POISON);
      }
   }

   public void setStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
      if (this.canHaveStatusEffect(effect)) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.put(effect.getEffectType(), effect);
         if (statusEffectInstance == null) {
            this.onStatusEffectApplied(effect, source);
         } else {
            effect.copyFadingFrom(statusEffectInstance);
            this.onStatusEffectUpgraded(effect, true, source);
         }

      }
   }

   public boolean hasInvertedHealingAndHarm() {
      return this.getType().isIn(EntityTypeTags.INVERTED_HEALING_AND_HARM);
   }

   @Nullable
   public final StatusEffectInstance removeStatusEffectInternal(RegistryEntry effect) {
      return (StatusEffectInstance)this.activeStatusEffects.remove(effect);
   }

   public boolean removeStatusEffect(RegistryEntry effect) {
      StatusEffectInstance statusEffectInstance = this.removeStatusEffectInternal(effect);
      if (statusEffectInstance != null) {
         this.onStatusEffectsRemoved(List.of(statusEffectInstance));
         return true;
      } else {
         return false;
      }
   }

   protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {
      if (!this.getWorld().isClient) {
         this.effectsChanged = true;
         ((StatusEffect)effect.getEffectType().value()).onApplied(this.getAttributes(), effect.getAmplifier());
         this.sendEffectToControllingPlayer(effect);
      }

   }

   public void sendEffectToControllingPlayer(StatusEffectInstance effect) {
      Iterator var2 = this.getPassengerList().iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect, false));
         }
      }

   }

   protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
      if (!this.getWorld().isClient) {
         this.effectsChanged = true;
         if (reapplyEffect) {
            StatusEffect statusEffect = (StatusEffect)effect.getEffectType().value();
            statusEffect.onRemoved(this.getAttributes());
            statusEffect.onApplied(this.getAttributes(), effect.getAmplifier());
            this.updateAttributes();
         }

         this.sendEffectToControllingPlayer(effect);
      }
   }

   protected void onStatusEffectsRemoved(Collection effects) {
      if (!this.getWorld().isClient) {
         this.effectsChanged = true;
         Iterator var2 = effects.iterator();

         while(var2.hasNext()) {
            StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var2.next();
            ((StatusEffect)statusEffectInstance.getEffectType().value()).onRemoved(this.getAttributes());
            Iterator var4 = this.getPassengerList().iterator();

            while(var4.hasNext()) {
               Entity entity = (Entity)var4.next();
               if (entity instanceof ServerPlayerEntity) {
                  ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                  serverPlayerEntity.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getId(), statusEffectInstance.getEffectType()));
               }
            }
         }

         this.updateAttributes();
      }
   }

   private void updateAttributes() {
      Set set = this.getAttributes().getPendingUpdate();
      Iterator var2 = set.iterator();

      while(var2.hasNext()) {
         EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)var2.next();
         this.updateAttribute(entityAttributeInstance.getAttribute());
      }

      set.clear();
   }

   protected void updateAttribute(RegistryEntry attribute) {
      float f;
      if (attribute.matches(EntityAttributes.MAX_HEALTH)) {
         f = this.getMaxHealth();
         if (this.getHealth() > f) {
            this.setHealth(f);
         }
      } else if (attribute.matches(EntityAttributes.MAX_ABSORPTION)) {
         f = this.getMaxAbsorption();
         if (this.getAbsorptionAmount() > f) {
            this.setAbsorptionAmount(f);
         }
      } else if (attribute.matches(EntityAttributes.SCALE)) {
         this.calculateDimensions();
      } else if (attribute.matches(EntityAttributes.WAYPOINT_TRANSMIT_RANGE)) {
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            ServerWaypointHandler serverWaypointHandler = serverWorld.getWaypointHandler();
            if (this.attributes.getValue(attribute) > 0.0) {
               serverWaypointHandler.onTrack((ServerWaypoint)this);
            } else {
               serverWaypointHandler.onUntrack((ServerWaypoint)this);
            }
         }
      }

   }

   public void heal(float amount) {
      float f = this.getHealth();
      if (f > 0.0F) {
         this.setHealth(f + amount);
      }

   }

   public float getHealth() {
      return (Float)this.dataTracker.get(HEALTH);
   }

   public void setHealth(float health) {
      this.dataTracker.set(HEALTH, MathHelper.clamp(health, 0.0F, this.getMaxHealth()));
   }

   public boolean isDead() {
      return this.getHealth() <= 0.0F;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (this.isInvulnerableTo(world, source)) {
         return false;
      } else if (this.isDead()) {
         return false;
      } else if (source.isIn(DamageTypeTags.IS_FIRE) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
         return false;
      } else {
         if (this.isSleeping()) {
            this.wakeUp();
         }

         this.despawnCounter = 0;
         if (amount < 0.0F) {
            amount = 0.0F;
         }

         float f = amount;
         float g = this.getDamageBlockedAmount(world, source, amount);
         amount -= g;
         boolean bl = g > 0.0F;
         if (source.isIn(DamageTypeTags.IS_FREEZING) && this.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
            amount *= 5.0F;
         }

         if (source.isIn(DamageTypeTags.DAMAGES_HELMET) && !this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            this.damageHelmet(source, amount);
            amount *= 0.75F;
         }

         if (Float.isNaN(amount) || Float.isInfinite(amount)) {
            amount = Float.MAX_VALUE;
         }

         boolean bl2 = true;
         if ((float)this.timeUntilRegen > 10.0F && !source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) {
            if (amount <= this.lastDamageTaken) {
               return false;
            }

            this.applyDamage(world, source, amount - this.lastDamageTaken);
            this.lastDamageTaken = amount;
            bl2 = false;
         } else {
            this.lastDamageTaken = amount;
            this.timeUntilRegen = 20;
            this.applyDamage(world, source, amount);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }

         this.becomeAngry(source);
         this.setAttackingPlayer(source);
         if (bl2) {
            BlocksAttacksComponent blocksAttacksComponent = (BlocksAttacksComponent)this.getActiveItem().get(DataComponentTypes.BLOCKS_ATTACKS);
            if (bl && blocksAttacksComponent != null) {
               blocksAttacksComponent.playBlockSound(world, this);
            } else {
               world.sendEntityDamage(this, source);
            }

            if (!source.isIn(DamageTypeTags.NO_IMPACT) && (!bl || amount > 0.0F)) {
               this.scheduleVelocityUpdate();
            }

            if (!source.isIn(DamageTypeTags.NO_KNOCKBACK)) {
               double d = 0.0;
               double e = 0.0;
               Entity var14 = source.getSource();
               if (var14 instanceof ProjectileEntity) {
                  ProjectileEntity projectileEntity = (ProjectileEntity)var14;
                  DoubleDoubleImmutablePair doubleDoubleImmutablePair = projectileEntity.getKnockback(this, source);
                  d = -doubleDoubleImmutablePair.leftDouble();
                  e = -doubleDoubleImmutablePair.rightDouble();
               } else if (source.getPosition() != null) {
                  d = source.getPosition().getX() - this.getX();
                  e = source.getPosition().getZ() - this.getZ();
               }

               this.takeKnockback(0.4000000059604645, d, e);
               if (!bl) {
                  this.tiltScreen(d, e);
               }
            }
         }

         if (this.isDead()) {
            if (!this.tryUseDeathProtector(source)) {
               if (bl2) {
                  this.playSound(this.getDeathSound());
                  this.playThornsSound(source);
               }

               this.onDeath(source);
            }
         } else if (bl2) {
            this.playHurtSound(source);
            this.playThornsSound(source);
         }

         boolean bl3 = !bl || amount > 0.0F;
         if (bl3) {
            this.lastDamageSource = source;
            this.lastDamageTime = this.getWorld().getTime();
            Iterator var16 = this.getStatusEffects().iterator();

            while(var16.hasNext()) {
               StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var16.next();
               statusEffectInstance.onEntityDamage(world, this, source, amount);
            }
         }

         ServerPlayerEntity serverPlayerEntity;
         if (this instanceof ServerPlayerEntity) {
            serverPlayerEntity = (ServerPlayerEntity)this;
            Criteria.ENTITY_HURT_PLAYER.trigger(serverPlayerEntity, source, f, amount, bl);
            if (g > 0.0F && g < 3.4028235E37F) {
               serverPlayerEntity.increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
            }
         }

         Entity var17 = source.getAttacker();
         if (var17 instanceof ServerPlayerEntity) {
            serverPlayerEntity = (ServerPlayerEntity)var17;
            Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayerEntity, this, source, f, amount, bl);
         }

         return bl3;
      }
   }

   public float getDamageBlockedAmount(ServerWorld world, DamageSource source, float amount) {
      if (amount <= 0.0F) {
         return 0.0F;
      } else {
         ItemStack itemStack = this.getBlockingItem();
         if (itemStack == null) {
            return 0.0F;
         } else {
            BlocksAttacksComponent blocksAttacksComponent = (BlocksAttacksComponent)itemStack.get(DataComponentTypes.BLOCKS_ATTACKS);
            if (blocksAttacksComponent != null) {
               Optional var10000 = blocksAttacksComponent.bypassedBy();
               java.util.Objects.requireNonNull(source);
               if (!(Boolean)var10000.map(source::isIn).orElse(false)) {
                  Entity var7 = source.getSource();
                  if (var7 instanceof PersistentProjectileEntity) {
                     PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)var7;
                     if (persistentProjectileEntity.getPierceLevel() > 0) {
                        return 0.0F;
                     }
                  }

                  Vec3d vec3d = source.getPosition();
                  double d;
                  if (vec3d != null) {
                     Vec3d vec3d2 = this.getRotationVector(0.0F, this.getHeadYaw());
                     Vec3d vec3d3 = vec3d.subtract(this.getPos());
                     vec3d3 = (new Vec3d(vec3d3.x, 0.0, vec3d3.z)).normalize();
                     d = Math.acos(vec3d3.dotProduct(vec3d2));
                  } else {
                     d = 3.1415927410125732;
                  }

                  float f = blocksAttacksComponent.getDamageReductionAmount(source, amount, d);
                  blocksAttacksComponent.onShieldHit(this.getWorld(), itemStack, this, this.getActiveHand(), f);
                  if (!source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                     Entity entity = source.getSource();
                     if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        this.takeShieldHit(world, livingEntity);
                     }
                  }

                  return f;
               }
            }

            return 0.0F;
         }
      }
   }

   private void playThornsSound(DamageSource damageSource) {
      if (damageSource.isOf(DamageTypes.THORNS)) {
         SoundCategory soundCategory = this instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
         this.getWorld().playSound((Entity)null, this.getPos().x, this.getPos().y, this.getPos().z, SoundEvents.ENCHANT_THORNS_HIT, soundCategory);
      }

   }

   protected void becomeAngry(DamageSource damageSource) {
      Entity var3 = damageSource.getAttacker();
      if (var3 instanceof LivingEntity livingEntity) {
         if (!damageSource.isIn(DamageTypeTags.NO_ANGER) && (!damageSource.isOf(DamageTypes.WIND_CHARGE) || !this.getType().isIn(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE))) {
            this.setAttacker(livingEntity);
         }
      }

   }

   @Nullable
   protected PlayerEntity setAttackingPlayer(DamageSource damageSource) {
      Entity entity = damageSource.getAttacker();
      if (entity instanceof PlayerEntity playerEntity) {
         this.setAttacking((PlayerEntity)playerEntity, 100);
      } else if (entity instanceof WolfEntity wolfEntity) {
         if (wolfEntity.isTamed()) {
            if (wolfEntity.getOwnerReference() != null) {
               this.setAttacking((UUID)wolfEntity.getOwnerReference().getUuid(), 100);
            } else {
               this.attackingPlayer = null;
               this.playerHitTimer = 0;
            }
         }
      }

      return (PlayerEntity)LazyEntityReference.resolve(this.attackingPlayer, this.getWorld(), PlayerEntity.class);
   }

   protected void takeShieldHit(ServerWorld world, LivingEntity attacker) {
      attacker.knockback(this);
   }

   protected void knockback(LivingEntity target) {
      target.takeKnockback(0.5, target.getX() - this.getX(), target.getZ() - this.getZ());
   }

   private boolean tryUseDeathProtector(DamageSource source) {
      if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      } else {
         ItemStack itemStack = null;
         DeathProtectionComponent deathProtectionComponent = null;
         Hand[] var5 = Hand.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Hand hand = var5[var7];
            ItemStack itemStack2 = this.getStackInHand(hand);
            deathProtectionComponent = (DeathProtectionComponent)itemStack2.get(DataComponentTypes.DEATH_PROTECTION);
            if (deathProtectionComponent != null) {
               itemStack = itemStack2.copy();
               itemStack2.decrement(1);
               break;
            }
         }

         if (itemStack != null) {
            if (this instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
               serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
               Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
               this.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }

            this.setHealth(1.0F);
            deathProtectionComponent.applyDeathEffects(itemStack, this);
            this.getWorld().sendEntityStatus(this, (byte)35);
         }

         return deathProtectionComponent != null;
      }
   }

   @Nullable
   public DamageSource getRecentDamageSource() {
      if (this.getWorld().getTime() - this.lastDamageTime > 40L) {
         this.lastDamageSource = null;
      }

      return this.lastDamageSource;
   }

   protected void playHurtSound(DamageSource damageSource) {
      this.playSound(this.getHurtSound(damageSource));
   }

   public void playSound(@Nullable SoundEvent sound) {
      if (sound != null) {
         this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   private void playEquipmentBreakEffects(ItemStack stack) {
      if (!stack.isEmpty()) {
         RegistryEntry registryEntry = (RegistryEntry)stack.get(DataComponentTypes.BREAK_SOUND);
         if (registryEntry != null && !this.isSilent()) {
            this.getWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), (SoundEvent)registryEntry.value(), this.getSoundCategory(), 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F, false);
         }

         this.spawnItemParticles(stack, 5);
      }

   }

   public void onDeath(DamageSource damageSource) {
      if (!this.isRemoved() && !this.dead) {
         Entity entity = damageSource.getAttacker();
         LivingEntity livingEntity = this.getPrimeAdversary();
         if (livingEntity != null) {
            livingEntity.updateKilledAdvancementCriterion(this, damageSource);
         }

         if (this.isSleeping()) {
            this.wakeUp();
         }

         if (!this.getWorld().isClient && this.hasCustomName()) {
            LOGGER.info("Named entity {} died: {}", this, this.getDamageTracker().getDeathMessage().getString());
         }

         this.dead = true;
         this.getDamageTracker().update();
         World var5 = this.getWorld();
         if (var5 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var5;
            if (entity == null || entity.onKilledOther(serverWorld, this)) {
               this.emitGameEvent(GameEvent.ENTITY_DIE);
               this.drop(serverWorld, damageSource);
               this.onKilledBy(livingEntity);
            }

            this.getWorld().sendEntityStatus(this, (byte)3);
         }

         this.setPose(EntityPose.DYING);
      }
   }

   protected void onKilledBy(@Nullable LivingEntity adversary) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         boolean bl = false;
         if (adversary instanceof WitherEntity) {
            if (serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
               BlockPos blockPos = this.getBlockPos();
               BlockState blockState = Blocks.WITHER_ROSE.getDefaultState();
               if (this.getWorld().getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.getWorld(), blockPos)) {
                  this.getWorld().setBlockState(blockPos, blockState, 3);
                  bl = true;
               }
            }

            if (!bl) {
               ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
               this.getWorld().spawnEntity(itemEntity);
            }
         }

      }
   }

   protected void drop(ServerWorld world, DamageSource damageSource) {
      boolean bl = this.playerHitTimer > 0;
      if (this.shouldDropLoot() && world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
         this.dropLoot(world, damageSource, bl);
         this.dropEquipment(world, damageSource, bl);
      }

      this.dropInventory(world);
      this.dropExperience(world, damageSource.getAttacker());
   }

   protected void dropInventory(ServerWorld world) {
   }

   protected void dropExperience(ServerWorld world, @Nullable Entity attacker) {
      if (!this.isExperienceDroppingDisabled() && (this.shouldAlwaysDropExperience() || this.playerHitTimer > 0 && this.shouldDropExperience() && world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
         ExperienceOrbEntity.spawn(world, this.getPos(), this.getExperienceToDrop(world, attacker));
      }

   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
   }

   public long getLootTableSeed() {
      return 0L;
   }

   protected float getAttackKnockbackAgainst(Entity target, DamageSource damageSource) {
      float f = (float)this.getAttributeValue(EntityAttributes.ATTACK_KNOCKBACK);
      World var5 = this.getWorld();
      if (var5 instanceof ServerWorld serverWorld) {
         return EnchantmentHelper.modifyKnockback(serverWorld, this.getWeaponStack(), target, damageSource, f);
      } else {
         return f;
      }
   }

   protected void dropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer) {
      Optional optional = this.getLootTableKey();
      if (!optional.isEmpty()) {
         LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable((RegistryKey)optional.get());
         LootWorldContext.Builder builder = (new LootWorldContext.Builder(world)).add(LootContextParameters.THIS_ENTITY, this).add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.DAMAGE_SOURCE, damageSource).addOptional(LootContextParameters.ATTACKING_ENTITY, damageSource.getAttacker()).addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, damageSource.getSource());
         PlayerEntity playerEntity = this.getAttackingPlayer();
         if (causedByPlayer && playerEntity != null) {
            builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, playerEntity).luck(playerEntity.getLuck());
         }

         LootWorldContext lootWorldContext = builder.build(LootContextTypes.ENTITY);
         lootTable.generateLoot(lootWorldContext, this.getLootTableSeed(), (stack) -> {
            this.dropStack(world, stack);
         });
      }
   }

   public boolean forEachGiftedItem(ServerWorld world, RegistryKey lootTableKey, BiConsumer lootConsumer) {
      return this.forEachGeneratedItem(world, lootTableKey, (parameterSetBuilder) -> {
         return parameterSetBuilder.add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.THIS_ENTITY, this).build(LootContextTypes.GIFT);
      }, lootConsumer);
   }

   protected void forEachShearedItem(ServerWorld world, RegistryKey lootTableKey, ItemStack tool, BiConsumer lootConsumer) {
      this.forEachGeneratedItem(world, lootTableKey, (parameterSetBuilder) -> {
         return parameterSetBuilder.add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.THIS_ENTITY, this).add(LootContextParameters.TOOL, tool).build(LootContextTypes.SHEARING);
      }, lootConsumer);
   }

   protected boolean forEachGeneratedItem(ServerWorld world, RegistryKey lootTableKey, Function lootContextParametersFactory, BiConsumer lootConsumer) {
      LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(lootTableKey);
      LootWorldContext lootWorldContext = (LootWorldContext)lootContextParametersFactory.apply(new LootWorldContext.Builder(world));
      List list = lootTable.generateLoot(lootWorldContext);
      if (!list.isEmpty()) {
         list.forEach((stack) -> {
            lootConsumer.accept(world, stack);
         });
         return true;
      } else {
         return false;
      }
   }

   public void takeKnockback(double strength, double x, double z) {
      strength *= 1.0 - this.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);
      if (!(strength <= 0.0)) {
         this.velocityDirty = true;

         Vec3d vec3d;
         for(vec3d = this.getVelocity(); x * x + z * z < 9.999999747378752E-6; z = (Math.random() - Math.random()) * 0.01) {
            x = (Math.random() - Math.random()) * 0.01;
         }

         Vec3d vec3d2 = (new Vec3d(x, 0.0, z)).normalize().multiply(strength);
         this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.isOnGround() ? Math.min(0.4, vec3d.y / 2.0 + strength) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
      }
   }

   public void tiltScreen(double deltaX, double deltaZ) {
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_GENERIC_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_GENERIC_DEATH;
   }

   private SoundEvent getFallSound(int distance) {
      return distance > 4 ? this.getFallSounds().big() : this.getFallSounds().small();
   }

   public void disableExperienceDropping() {
      this.experienceDroppingDisabled = true;
   }

   public boolean isExperienceDroppingDisabled() {
      return this.experienceDroppingDisabled;
   }

   public float getDamageTiltYaw() {
      return 0.0F;
   }

   protected Box getHitbox() {
      Box box = this.getBoundingBox();
      Entity entity = this.getVehicle();
      if (entity != null) {
         Vec3d vec3d = entity.getPassengerRidingPos(this);
         return box.withMinY(Math.max(vec3d.y, box.minY));
      } else {
         return box;
      }
   }

   public Map getLocationBasedEnchantmentEffects(EquipmentSlot slot) {
      return (Map)this.locationBasedEnchantmentEffects.computeIfAbsent(slot, (equipmentSlot) -> {
         return new Reference2ObjectArrayMap();
      });
   }

   public FallSounds getFallSounds() {
      return new FallSounds(SoundEvents.ENTITY_GENERIC_SMALL_FALL, SoundEvents.ENTITY_GENERIC_BIG_FALL);
   }

   public Optional getClimbingPos() {
      return this.climbingPos;
   }

   public boolean isClimbing() {
      if (this.isSpectator()) {
         return false;
      } else {
         BlockPos blockPos = this.getBlockPos();
         BlockState blockState = this.getBlockStateAtPos();
         if (blockState.isIn(BlockTags.CLIMBABLE)) {
            this.climbingPos = Optional.of(blockPos);
            return true;
         } else if (blockState.getBlock() instanceof TrapdoorBlock && this.canEnterTrapdoor(blockPos, blockState)) {
            this.climbingPos = Optional.of(blockPos);
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean canEnterTrapdoor(BlockPos pos, BlockState state) {
      if (!(Boolean)state.get(TrapdoorBlock.OPEN)) {
         return false;
      } else {
         BlockState blockState = this.getWorld().getBlockState(pos.down());
         return blockState.isOf(Blocks.LADDER) && blockState.get(LadderBlock.FACING) == state.get(TrapdoorBlock.FACING);
      }
   }

   public boolean isAlive() {
      return !this.isRemoved() && this.getHealth() > 0.0F;
   }

   public boolean isEntityLookingAtMe(LivingEntity entity, double d, boolean bl, boolean visualShape, double... checkedYs) {
      Vec3d vec3d = entity.getRotationVec(1.0F).normalize();
      double[] var8 = checkedYs;
      int var9 = checkedYs.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         double e = var8[var10];
         Vec3d vec3d2 = new Vec3d(this.getX() - entity.getX(), e - entity.getEyeY(), this.getZ() - entity.getZ());
         double f = vec3d2.length();
         vec3d2 = vec3d2.normalize();
         double g = vec3d.dotProduct(vec3d2);
         if (g > 1.0 - d / (bl ? f : 1.0) && entity.canSee(this, visualShape ? RaycastContext.ShapeType.VISUAL : RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e)) {
            return true;
         }
      }

      return false;
   }

   public int getSafeFallDistance() {
      return this.getSafeFallDistance(0.0F);
   }

   protected final int getSafeFallDistance(float health) {
      return MathHelper.floor(health + 3.0F);
   }

   public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
      boolean bl = super.handleFallDamage(fallDistance, damagePerDistance, damageSource);
      int i = this.computeFallDamage(fallDistance, damagePerDistance);
      if (i > 0) {
         this.playSound(this.getFallSound(i), 1.0F, 1.0F);
         this.playBlockFallSound();
         this.serverDamage(damageSource, (float)i);
         return true;
      } else {
         return bl;
      }
   }

   protected int computeFallDamage(double fallDistance, float damagePerDistance) {
      if (this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
         return 0;
      } else {
         double d = this.getUnsafeFallDistance(fallDistance);
         return MathHelper.floor(d * (double)damagePerDistance * this.getAttributeValue(EntityAttributes.FALL_DAMAGE_MULTIPLIER));
      }
   }

   private double getUnsafeFallDistance(double fallDistance) {
      return fallDistance + 1.0E-6 - this.getAttributeValue(EntityAttributes.SAFE_FALL_DISTANCE);
   }

   protected void playBlockFallSound() {
      if (!this.isSilent()) {
         int i = MathHelper.floor(this.getX());
         int j = MathHelper.floor(this.getY() - 0.20000000298023224);
         int k = MathHelper.floor(this.getZ());
         BlockState blockState = this.getWorld().getBlockState(new BlockPos(i, j, k));
         if (!blockState.isAir()) {
            BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
            this.playSound(blockSoundGroup.getFallSound(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F);
         }

      }
   }

   public void animateDamage(float yaw) {
      this.maxHurtTime = 10;
      this.hurtTime = this.maxHurtTime;
   }

   public int getArmor() {
      return MathHelper.floor(this.getAttributeValue(EntityAttributes.ARMOR));
   }

   public void damageArmor(DamageSource source, float amount) {
   }

   public void damageHelmet(DamageSource source, float amount) {
   }

   protected void damageEquipment(DamageSource source, float amount, EquipmentSlot... slots) {
      if (!(amount <= 0.0F)) {
         int i = (int)Math.max(1.0F, amount / 4.0F);
         EquipmentSlot[] var5 = slots;
         int var6 = slots.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EquipmentSlot equipmentSlot = var5[var7];
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
            if (equippableComponent != null && equippableComponent.damageOnHurt() && itemStack.isDamageable() && itemStack.takesDamageFrom(source)) {
               itemStack.damage(i, this, equipmentSlot);
            }
         }

      }
   }

   protected float applyArmorToDamage(DamageSource source, float amount) {
      if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
         this.damageArmor(source, amount);
         amount = DamageUtil.getDamageLeft(this, amount, source, (float)this.getArmor(), (float)this.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS));
      }

      return amount;
   }

   protected float modifyAppliedDamage(DamageSource source, float amount) {
      if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
         return amount;
      } else {
         if (this.hasStatusEffect(StatusEffects.RESISTANCE) && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
            int i = (this.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
            int j = 25 - i;
            float f = amount * (float)j;
            float g = amount;
            amount = Math.max(f / 25.0F, 0.0F);
            float h = g - amount;
            if (h > 0.0F && h < 3.4028235E37F) {
               if (this instanceof ServerPlayerEntity) {
                  ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0F));
               } else if (source.getAttacker() instanceof ServerPlayerEntity) {
                  ((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0F));
               }
            }
         }

         if (amount <= 0.0F) {
            return 0.0F;
         } else if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
            return amount;
         } else {
            World var10 = this.getWorld();
            float k;
            if (var10 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var10;
               k = EnchantmentHelper.getProtectionAmount(serverWorld, this, source);
            } else {
               k = 0.0F;
            }

            if (k > 0.0F) {
               amount = DamageUtil.getInflictedDamage(amount, k);
            }

            return amount;
         }
      }
   }

   protected void applyDamage(ServerWorld world, DamageSource source, float amount) {
      if (!this.isInvulnerableTo(world, source)) {
         amount = this.applyArmorToDamage(source, amount);
         amount = this.modifyAppliedDamage(source, amount);
         float f = amount;
         amount = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - amount));
         float g = f - amount;
         if (g > 0.0F && g < 3.4028235E37F) {
            Entity var7 = source.getAttacker();
            if (var7 instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var7;
               serverPlayerEntity.increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(g * 10.0F));
            }
         }

         if (amount != 0.0F) {
            this.getDamageTracker().onDamage(source, amount);
            this.setHealth(this.getHealth() - amount);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - amount);
            this.emitGameEvent(GameEvent.ENTITY_DAMAGE);
         }
      }
   }

   public DamageTracker getDamageTracker() {
      return this.damageTracker;
   }

   @Nullable
   public LivingEntity getPrimeAdversary() {
      if (this.attackingPlayer != null) {
         return (LivingEntity)this.attackingPlayer.resolve(this.getWorld(), PlayerEntity.class);
      } else {
         return this.attackerReference != null ? (LivingEntity)this.attackerReference.resolve(this.getWorld(), LivingEntity.class) : null;
      }
   }

   public final float getMaxHealth() {
      return (float)this.getAttributeValue(EntityAttributes.MAX_HEALTH);
   }

   public final float getMaxAbsorption() {
      return (float)this.getAttributeValue(EntityAttributes.MAX_ABSORPTION);
   }

   public final int getStuckArrowCount() {
      return (Integer)this.dataTracker.get(STUCK_ARROW_COUNT);
   }

   public final void setStuckArrowCount(int stuckArrowCount) {
      this.dataTracker.set(STUCK_ARROW_COUNT, stuckArrowCount);
   }

   public final int getStingerCount() {
      return (Integer)this.dataTracker.get(STINGER_COUNT);
   }

   public final void setStingerCount(int stingerCount) {
      this.dataTracker.set(STINGER_COUNT, stingerCount);
   }

   private int getHandSwingDuration() {
      if (StatusEffectUtil.hasHaste(this)) {
         return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
      } else {
         return this.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
      }
   }

   public void swingHand(Hand hand) {
      this.swingHand(hand, false);
   }

   public void swingHand(Hand hand, boolean fromServerPlayer) {
      if (!this.handSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
         this.handSwingTicks = -1;
         this.handSwinging = true;
         this.preferredHand = hand;
         if (this.getWorld() instanceof ServerWorld) {
            EntityAnimationS2CPacket entityAnimationS2CPacket = new EntityAnimationS2CPacket(this, hand == Hand.MAIN_HAND ? 0 : 3);
            ServerChunkManager serverChunkManager = ((ServerWorld)this.getWorld()).getChunkManager();
            if (fromServerPlayer) {
               serverChunkManager.sendToNearbyPlayers(this, entityAnimationS2CPacket);
            } else {
               serverChunkManager.sendToOtherNearbyPlayers(this, entityAnimationS2CPacket);
            }
         }
      }

   }

   public void onDamaged(DamageSource damageSource) {
      this.limbAnimator.setSpeed(1.5F);
      this.timeUntilRegen = 20;
      this.maxHurtTime = 10;
      this.hurtTime = this.maxHurtTime;
      SoundEvent soundEvent = this.getHurtSound(damageSource);
      if (soundEvent != null) {
         this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }

      this.lastDamageSource = damageSource;
      this.lastDamageTime = this.getWorld().getTime();
   }

   public void handleStatus(byte status) {
      switch (status) {
         case 3:
            SoundEvent soundEvent = this.getDeathSound();
            if (soundEvent != null) {
               this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            if (!(this instanceof PlayerEntity)) {
               this.setHealth(0.0F);
               this.onDeath(this.getDamageSources().generic());
            }
            break;
         case 46:
            int i = true;

            for(int j = 0; j < 128; ++j) {
               double d = (double)j / 127.0;
               float f = (this.random.nextFloat() - 0.5F) * 0.2F;
               float g = (this.random.nextFloat() - 0.5F) * 0.2F;
               float h = (this.random.nextFloat() - 0.5F) * 0.2F;
               double e = MathHelper.lerp(d, this.lastX, this.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
               double k = MathHelper.lerp(d, this.lastY, this.getY()) + this.random.nextDouble() * (double)this.getHeight();
               double l = MathHelper.lerp(d, this.lastZ, this.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
               this.getWorld().addParticleClient(ParticleTypes.PORTAL, e, k, l, (double)f, (double)g, (double)h);
            }

            return;
         case 47:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.MAINHAND));
            break;
         case 48:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.OFFHAND));
            break;
         case 49:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.HEAD));
            break;
         case 50:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.CHEST));
            break;
         case 51:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.LEGS));
            break;
         case 52:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.FEET));
            break;
         case 54:
            HoneyBlock.addRichParticles(this);
            break;
         case 55:
            this.swapHandStacks();
            break;
         case 60:
            this.addDeathParticles();
            break;
         case 65:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.BODY));
            break;
         case 67:
            this.addBubbleParticles();
            break;
         case 68:
            this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.SADDLE));
            break;
         default:
            super.handleStatus(status);
      }

   }

   public void addDeathParticles() {
      for(int i = 0; i < 20; ++i) {
         double d = this.random.nextGaussian() * 0.02;
         double e = this.random.nextGaussian() * 0.02;
         double f = this.random.nextGaussian() * 0.02;
         double g = 10.0;
         this.getWorld().addParticleClient(ParticleTypes.POOF, this.getParticleX(1.0) - d * 10.0, this.getRandomBodyY() - e * 10.0, this.getParticleZ(1.0) - f * 10.0, d, e, f);
      }

   }

   private void addBubbleParticles() {
      Vec3d vec3d = this.getVelocity();

      for(int i = 0; i < 8; ++i) {
         double d = this.random.nextTriangular(0.0, 1.0);
         double e = this.random.nextTriangular(0.0, 1.0);
         double f = this.random.nextTriangular(0.0, 1.0);
         this.getWorld().addParticleClient(ParticleTypes.BUBBLE, this.getX() + d, this.getY() + e, this.getZ() + f, vec3d.x, vec3d.y, vec3d.z);
      }

   }

   private void swapHandStacks() {
      ItemStack itemStack = this.getEquippedStack(EquipmentSlot.OFFHAND);
      this.equipStack(EquipmentSlot.OFFHAND, this.getEquippedStack(EquipmentSlot.MAINHAND));
      this.equipStack(EquipmentSlot.MAINHAND, itemStack);
   }

   protected void tickInVoid() {
      this.serverDamage(this.getDamageSources().outOfWorld(), 4.0F);
   }

   protected void tickHandSwing() {
      int i = this.getHandSwingDuration();
      if (this.handSwinging) {
         ++this.handSwingTicks;
         if (this.handSwingTicks >= i) {
            this.handSwingTicks = 0;
            this.handSwinging = false;
         }
      } else {
         this.handSwingTicks = 0;
      }

      this.handSwingProgress = (float)this.handSwingTicks / (float)i;
   }

   @Nullable
   public EntityAttributeInstance getAttributeInstance(RegistryEntry attribute) {
      return this.getAttributes().getCustomInstance(attribute);
   }

   public double getAttributeValue(RegistryEntry attribute) {
      return this.getAttributes().getValue(attribute);
   }

   public double getAttributeBaseValue(RegistryEntry attribute) {
      return this.getAttributes().getBaseValue(attribute);
   }

   public AttributeContainer getAttributes() {
      return this.attributes;
   }

   public ItemStack getMainHandStack() {
      return this.getEquippedStack(EquipmentSlot.MAINHAND);
   }

   public ItemStack getOffHandStack() {
      return this.getEquippedStack(EquipmentSlot.OFFHAND);
   }

   public ItemStack getStackInArm(Arm arm) {
      return this.getMainArm() == arm ? this.getMainHandStack() : this.getOffHandStack();
   }

   @NotNull
   public ItemStack getWeaponStack() {
      return this.getMainHandStack();
   }

   public boolean isHolding(Item item) {
      return this.isHolding((stack) -> {
         return stack.isOf(item);
      });
   }

   public boolean isHolding(Predicate predicate) {
      return predicate.test(this.getMainHandStack()) || predicate.test(this.getOffHandStack());
   }

   public ItemStack getStackInHand(Hand hand) {
      if (hand == Hand.MAIN_HAND) {
         return this.getEquippedStack(EquipmentSlot.MAINHAND);
      } else if (hand == Hand.OFF_HAND) {
         return this.getEquippedStack(EquipmentSlot.OFFHAND);
      } else {
         throw new IllegalArgumentException("Invalid hand " + String.valueOf(hand));
      }
   }

   public void setStackInHand(Hand hand, ItemStack stack) {
      if (hand == Hand.MAIN_HAND) {
         this.equipStack(EquipmentSlot.MAINHAND, stack);
      } else {
         if (hand != Hand.OFF_HAND) {
            throw new IllegalArgumentException("Invalid hand " + String.valueOf(hand));
         }

         this.equipStack(EquipmentSlot.OFFHAND, stack);
      }

   }

   public boolean hasStackEquipped(EquipmentSlot slot) {
      return !this.getEquippedStack(slot).isEmpty();
   }

   public boolean canUseSlot(EquipmentSlot slot) {
      return true;
   }

   public ItemStack getEquippedStack(EquipmentSlot slot) {
      return this.equipment.get(slot);
   }

   public void equipStack(EquipmentSlot slot, ItemStack stack) {
      this.onEquipStack(slot, this.equipment.put(slot, stack), stack);
   }

   public float getArmorVisibility() {
      int i = 0;
      int j = 0;
      Iterator var3 = AttributeModifierSlot.ARMOR.iterator();

      while(var3.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var3.next();
         if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            if (!itemStack.isEmpty()) {
               ++j;
            }

            ++i;
         }
      }

      return i > 0 ? (float)j / (float)i : 0.0F;
   }

   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
      entityAttributeInstance.removeModifier(SPRINTING_SPEED_BOOST.id());
      if (sprinting) {
         entityAttributeInstance.addTemporaryModifier(SPRINTING_SPEED_BOOST);
      }

   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public float getSoundPitch() {
      return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
   }

   protected boolean isImmobile() {
      return this.isDead();
   }

   public void pushAwayFrom(Entity entity) {
      if (!this.isSleeping()) {
         super.pushAwayFrom(entity);
      }

   }

   private void onDismounted(Entity vehicle) {
      Vec3d vec3d;
      if (this.isRemoved()) {
         vec3d = this.getPos();
      } else if (!vehicle.isRemoved() && !this.getWorld().getBlockState(vehicle.getBlockPos()).isIn(BlockTags.PORTALS)) {
         vec3d = vehicle.updatePassengerForDismount(this);
      } else {
         double d = Math.max(this.getY(), vehicle.getY());
         vec3d = new Vec3d(this.getX(), d, this.getZ());
         boolean bl = this.getWidth() <= 4.0F && this.getHeight() <= 4.0F;
         if (bl) {
            double e = (double)this.getHeight() / 2.0;
            Vec3d vec3d2 = vec3d.add(0.0, e, 0.0);
            VoxelShape voxelShape = VoxelShapes.cuboid(Box.of(vec3d2, (double)this.getWidth(), (double)this.getHeight(), (double)this.getWidth()));
            vec3d = (Vec3d)this.getWorld().findClosestCollision(this, voxelShape, vec3d2, (double)this.getWidth(), (double)this.getHeight(), (double)this.getWidth()).map((pos) -> {
               return pos.add(0.0, -e, 0.0);
            }).orElse(vec3d);
         }
      }

      this.requestTeleportAndDismount(vec3d.x, vec3d.y, vec3d.z);
   }

   public boolean shouldRenderName() {
      return this.isCustomNameVisible();
   }

   protected float getJumpVelocity() {
      return this.getJumpVelocity(1.0F);
   }

   protected float getJumpVelocity(float strength) {
      return (float)this.getAttributeValue(EntityAttributes.JUMP_STRENGTH) * strength * this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier();
   }

   public float getJumpBoostVelocityModifier() {
      return this.hasStatusEffect(StatusEffects.JUMP_BOOST) ? 0.1F * ((float)this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1.0F) : 0.0F;
   }

   @VisibleForTesting
   public void jump() {
      float f = this.getJumpVelocity();
      if (!(f <= 1.0E-5F)) {
         Vec3d vec3d = this.getVelocity();
         this.setVelocity(vec3d.x, Math.max((double)f, vec3d.y), vec3d.z);
         if (this.isSprinting()) {
            float g = this.getYaw() * 0.017453292F;
            this.addVelocityInternal(new Vec3d((double)(-MathHelper.sin(g)) * 0.2, 0.0, (double)MathHelper.cos(g) * 0.2));
         }

         this.velocityDirty = true;
      }
   }

   protected void knockDownwards() {
      this.setVelocity(this.getVelocity().add(0.0, -0.03999999910593033, 0.0));
   }

   protected void swimUpward(TagKey fluid) {
      this.setVelocity(this.getVelocity().add(0.0, 0.03999999910593033, 0.0));
   }

   protected float getBaseWaterMovementSpeedMultiplier() {
      return 0.8F;
   }

   public boolean canWalkOnFluid(FluidState state) {
      return false;
   }

   protected double getGravity() {
      return this.getAttributeValue(EntityAttributes.GRAVITY);
   }

   protected double getEffectiveGravity() {
      boolean bl = this.getVelocity().y <= 0.0;
      return bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING) ? Math.min(this.getFinalGravity(), 0.01) : this.getFinalGravity();
   }

   public void travel(Vec3d movementInput) {
      FluidState fluidState = this.getWorld().getFluidState(this.getBlockPos());
      if ((this.isTouchingWater() || this.isInLava()) && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
         this.travelInFluid(movementInput);
      } else if (this.isGliding()) {
         this.travelGliding(movementInput);
      } else {
         this.travelMidAir(movementInput);
      }

   }

   protected void travelFlying(Vec3d movementInput, float speed) {
      this.travelFlying(movementInput, 0.02F, 0.02F, speed);
   }

   protected void travelFlying(Vec3d movementInput, float inWaterSpeed, float inLavaSpeed, float regularSpeed) {
      if (this.isTouchingWater()) {
         this.updateVelocity(inWaterSpeed, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         this.setVelocity(this.getVelocity().multiply(0.800000011920929));
      } else if (this.isInLava()) {
         this.updateVelocity(inLavaSpeed, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         this.setVelocity(this.getVelocity().multiply(0.5));
      } else {
         this.updateVelocity(regularSpeed, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         this.setVelocity(this.getVelocity().multiply(0.9100000262260437));
      }

   }

   private void travelMidAir(Vec3d movementInput) {
      BlockPos blockPos = this.getVelocityAffectingPos();
      float f = this.isOnGround() ? this.getWorld().getBlockState(blockPos).getBlock().getSlipperiness() : 1.0F;
      float g = f * 0.91F;
      Vec3d vec3d = this.applyMovementInput(movementInput, f);
      double d = vec3d.y;
      StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.LEVITATION);
      if (statusEffectInstance != null) {
         d += (0.05 * (double)(statusEffectInstance.getAmplifier() + 1) - vec3d.y) * 0.2;
      } else if (this.getWorld().isClient && !this.getWorld().isChunkLoaded(blockPos)) {
         if (this.getY() > (double)this.getWorld().getBottomY()) {
            d = -0.1;
         } else {
            d = 0.0;
         }
      } else {
         d -= this.getEffectiveGravity();
      }

      if (this.hasNoDrag()) {
         this.setVelocity(vec3d.x, d, vec3d.z);
      } else {
         float h = this instanceof Flutterer ? g : 0.98F;
         this.setVelocity(vec3d.x * (double)g, d * (double)h, vec3d.z * (double)g);
      }

   }

   private void travelInFluid(Vec3d movementInput) {
      boolean bl = this.getVelocity().y <= 0.0;
      double d = this.getY();
      double e = this.getEffectiveGravity();
      Vec3d vec3d2;
      if (this.isTouchingWater()) {
         float f = this.isSprinting() ? 0.9F : this.getBaseWaterMovementSpeedMultiplier();
         float g = 0.02F;
         float h = (float)this.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
         if (!this.isOnGround()) {
            h *= 0.5F;
         }

         if (h > 0.0F) {
            f += (0.54600006F - f) * h;
            g += (this.getMovementSpeed() - g) * h;
         }

         if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            f = 0.96F;
         }

         this.updateVelocity(g, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         Vec3d vec3d = this.getVelocity();
         if (this.horizontalCollision && this.isClimbing()) {
            vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
         }

         vec3d = vec3d.multiply((double)f, 0.800000011920929, (double)f);
         this.setVelocity(this.applyFluidMovingSpeed(e, bl, vec3d));
      } else {
         this.updateVelocity(0.02F, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
            this.setVelocity(this.getVelocity().multiply(0.5, 0.800000011920929, 0.5));
            vec3d2 = this.applyFluidMovingSpeed(e, bl, this.getVelocity());
            this.setVelocity(vec3d2);
         } else {
            this.setVelocity(this.getVelocity().multiply(0.5));
         }

         if (e != 0.0) {
            this.setVelocity(this.getVelocity().add(0.0, -e / 4.0, 0.0));
         }
      }

      vec3d2 = this.getVelocity();
      if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6000000238418579 - this.getY() + d, vec3d2.z)) {
         this.setVelocity(vec3d2.x, 0.30000001192092896, vec3d2.z);
      }

   }

   private void travelGliding(Vec3d movementInput) {
      if (this.isClimbing()) {
         this.travelMidAir(movementInput);
         this.stopGliding();
      } else {
         Vec3d vec3d = this.getVelocity();
         double d = vec3d.horizontalLength();
         this.setVelocity(this.calcGlidingVelocity(vec3d));
         this.move(MovementType.SELF, this.getVelocity());
         if (!this.getWorld().isClient) {
            double e = this.getVelocity().horizontalLength();
            this.checkGlidingCollision(d, e);
         }

      }
   }

   public void stopGliding() {
      this.setFlag(7, true);
      this.setFlag(7, false);
   }

   private Vec3d calcGlidingVelocity(Vec3d oldVelocity) {
      Vec3d vec3d = this.getRotationVector();
      float f = this.getPitch() * 0.017453292F;
      double d = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
      double e = oldVelocity.horizontalLength();
      double g = this.getEffectiveGravity();
      double h = MathHelper.square(Math.cos((double)f));
      oldVelocity = oldVelocity.add(0.0, g * (-1.0 + h * 0.75), 0.0);
      double i;
      if (oldVelocity.y < 0.0 && d > 0.0) {
         i = oldVelocity.y * -0.1 * h;
         oldVelocity = oldVelocity.add(vec3d.x * i / d, i, vec3d.z * i / d);
      }

      if (f < 0.0F && d > 0.0) {
         i = e * (double)(-MathHelper.sin(f)) * 0.04;
         oldVelocity = oldVelocity.add(-vec3d.x * i / d, i * 3.2, -vec3d.z * i / d);
      }

      if (d > 0.0) {
         oldVelocity = oldVelocity.add((vec3d.x / d * e - oldVelocity.x) * 0.1, 0.0, (vec3d.z / d * e - oldVelocity.z) * 0.1);
      }

      return oldVelocity.multiply(0.9900000095367432, 0.9800000190734863, 0.9900000095367432);
   }

   private void checkGlidingCollision(double oldSpeed, double newSpeed) {
      if (this.horizontalCollision) {
         double d = oldSpeed - newSpeed;
         float f = (float)(d * 10.0 - 3.0);
         if (f > 0.0F) {
            this.playSound(this.getFallSound((int)f), 1.0F, 1.0F);
            this.serverDamage(this.getDamageSources().flyIntoWall(), f);
         }
      }

   }

   private void travelControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
      Vec3d vec3d = this.getControlledMovementInput(controllingPlayer, movementInput);
      this.tickControlled(controllingPlayer, vec3d);
      if (this.canMoveVoluntarily()) {
         this.setMovementSpeed(this.getSaddledSpeed(controllingPlayer));
         this.travel(vec3d);
      } else {
         this.setVelocity(Vec3d.ZERO);
      }

   }

   protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
   }

   protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
      return movementInput;
   }

   protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
      return this.getMovementSpeed();
   }

   public void updateLimbs(boolean flutter) {
      float f = (float)MathHelper.magnitude(this.getX() - this.lastX, flutter ? this.getY() - this.lastY : 0.0, this.getZ() - this.lastZ);
      if (!this.hasVehicle() && this.isAlive()) {
         this.updateLimbs(f);
      } else {
         this.limbAnimator.reset();
      }

   }

   protected void updateLimbs(float posDelta) {
      float f = Math.min(posDelta * 4.0F, 1.0F);
      this.limbAnimator.updateLimbs(f, 0.4F, this.isBaby() ? 3.0F : 1.0F);
   }

   private Vec3d applyMovementInput(Vec3d movementInput, float slipperiness) {
      this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
      this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
      this.move(MovementType.SELF, this.getVelocity());
      Vec3d vec3d = this.getVelocity();
      if ((this.horizontalCollision || this.jumping) && (this.isClimbing() || this.wasInPowderSnow && PowderSnowBlock.canWalkOnPowderSnow(this))) {
         vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
      }

      return vec3d;
   }

   public Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion) {
      if (gravity != 0.0 && !this.isSprinting()) {
         double d;
         if (falling && Math.abs(motion.y - 0.005) >= 0.003 && Math.abs(motion.y - gravity / 16.0) < 0.003) {
            d = -0.003;
         } else {
            d = motion.y - gravity / 16.0;
         }

         return new Vec3d(motion.x, d, motion.z);
      } else {
         return motion;
      }
   }

   private Vec3d applyClimbingSpeed(Vec3d motion) {
      if (this.isClimbing()) {
         this.onLanding();
         float f = 0.15F;
         double d = MathHelper.clamp(motion.x, -0.15000000596046448, 0.15000000596046448);
         double e = MathHelper.clamp(motion.z, -0.15000000596046448, 0.15000000596046448);
         double g = Math.max(motion.y, -0.15000000596046448);
         if (g < 0.0 && !this.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder() && this instanceof PlayerEntity) {
            g = 0.0;
         }

         motion = new Vec3d(d, g, e);
      }

      return motion;
   }

   private float getMovementSpeed(float slipperiness) {
      return this.isOnGround() ? this.getMovementSpeed() * (0.21600002F / (slipperiness * slipperiness * slipperiness)) : this.getOffGroundSpeed();
   }

   protected float getOffGroundSpeed() {
      return this.getControllingPassenger() instanceof PlayerEntity ? this.getMovementSpeed() * 0.1F : 0.02F;
   }

   public float getMovementSpeed() {
      return this.movementSpeed;
   }

   public void setMovementSpeed(float movementSpeed) {
      this.movementSpeed = movementSpeed;
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      this.onAttacking(target);
      return false;
   }

   public void tick() {
      super.tick();
      this.tickActiveItemStack();
      this.updateLeaningPitch();
      if (!this.getWorld().isClient) {
         int i = this.getStuckArrowCount();
         if (i > 0) {
            if (this.stuckArrowTimer <= 0) {
               this.stuckArrowTimer = 20 * (30 - i);
            }

            --this.stuckArrowTimer;
            if (this.stuckArrowTimer <= 0) {
               this.setStuckArrowCount(i - 1);
            }
         }

         int j = this.getStingerCount();
         if (j > 0) {
            if (this.stuckStingerTimer <= 0) {
               this.stuckStingerTimer = 20 * (30 - j);
            }

            --this.stuckStingerTimer;
            if (this.stuckStingerTimer <= 0) {
               this.setStingerCount(j - 1);
            }
         }

         this.sendEquipmentChanges();
         if (this.age % 20 == 0) {
            this.getDamageTracker().update();
         }

         if (this.isSleeping() && !this.isSleepingInBed()) {
            this.wakeUp();
         }
      }

      if (!this.isRemoved()) {
         this.tickMovement();
      }

      double d = this.getX() - this.lastX;
      double e = this.getZ() - this.lastZ;
      float f = (float)(d * d + e * e);
      float g = this.bodyYaw;
      if (f > 0.0025000002F) {
         float h = (float)MathHelper.atan2(e, d) * 57.295776F - 90.0F;
         float k = MathHelper.abs(MathHelper.wrapDegrees(this.getYaw()) - h);
         if (95.0F < k && k < 265.0F) {
            g = h - 180.0F;
         } else {
            g = h;
         }
      }

      if (this.handSwingProgress > 0.0F) {
         g = this.getYaw();
      }

      Profiler profiler = Profilers.get();
      profiler.push("headTurn");
      this.turnHead(g);
      profiler.pop();
      profiler.push("rangeChecks");

      while(this.getYaw() - this.lastYaw < -180.0F) {
         this.lastYaw -= 360.0F;
      }

      while(this.getYaw() - this.lastYaw >= 180.0F) {
         this.lastYaw += 360.0F;
      }

      while(this.bodyYaw - this.lastBodyYaw < -180.0F) {
         this.lastBodyYaw -= 360.0F;
      }

      while(this.bodyYaw - this.lastBodyYaw >= 180.0F) {
         this.lastBodyYaw += 360.0F;
      }

      while(this.getPitch() - this.lastPitch < -180.0F) {
         this.lastPitch -= 360.0F;
      }

      while(this.getPitch() - this.lastPitch >= 180.0F) {
         this.lastPitch += 360.0F;
      }

      while(this.headYaw - this.lastHeadYaw < -180.0F) {
         this.lastHeadYaw -= 360.0F;
      }

      while(this.headYaw - this.lastHeadYaw >= 180.0F) {
         this.lastHeadYaw += 360.0F;
      }

      profiler.pop();
      if (this.isGliding()) {
         ++this.glidingTicks;
      } else {
         this.glidingTicks = 0;
      }

      if (this.isSleeping()) {
         this.setPitch(0.0F);
      }

      this.updateAttributes();
      this.elytraFlightController.update();
   }

   private void sendEquipmentChanges() {
      Map map = this.getEquipmentChanges();
      if (map != null) {
         this.checkHandStackSwap(map);
         if (!map.isEmpty()) {
            this.sendEquipmentChanges(map);
         }
      }

   }

   @Nullable
   private Map getEquipmentChanges() {
      Map map = null;
      Iterator var2 = EquipmentSlot.VALUES.iterator();

      ItemStack itemStack2;
      while(var2.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var2.next();
         ItemStack itemStack = (ItemStack)this.lastEquipmentStacks.get(equipmentSlot);
         itemStack2 = this.getEquippedStack(equipmentSlot);
         if (this.areItemsDifferent(itemStack, itemStack2)) {
            if (map == null) {
               map = Maps.newEnumMap(EquipmentSlot.class);
            }

            map.put(equipmentSlot, itemStack2);
            AttributeContainer attributeContainer = this.getAttributes();
            if (!itemStack.isEmpty()) {
               this.onEquipmentRemoved(itemStack, equipmentSlot, attributeContainer);
            }
         }
      }

      if (map != null) {
         var2 = map.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry entry = (Map.Entry)var2.next();
            EquipmentSlot equipmentSlot2 = (EquipmentSlot)entry.getKey();
            itemStack2 = (ItemStack)entry.getValue();
            if (!itemStack2.isEmpty() && !itemStack2.shouldBreak()) {
               itemStack2.applyAttributeModifiers(equipmentSlot2, (attribute, modifier) -> {
                  EntityAttributeInstance entityAttributeInstance = this.attributes.getCustomInstance(attribute);
                  if (entityAttributeInstance != null) {
                     entityAttributeInstance.removeModifier(modifier.id());
                     entityAttributeInstance.addTemporaryModifier(modifier);
                  }

               });
               World var7 = this.getWorld();
               if (var7 instanceof ServerWorld) {
                  ServerWorld serverWorld = (ServerWorld)var7;
                  EnchantmentHelper.applyLocationBasedEffects(serverWorld, itemStack2, this, equipmentSlot2);
               }
            }
         }
      }

      return map;
   }

   public boolean areItemsDifferent(ItemStack stack, ItemStack stack2) {
      return !ItemStack.areEqual(stack2, stack);
   }

   private void checkHandStackSwap(Map equipmentChanges) {
      ItemStack itemStack = (ItemStack)equipmentChanges.get(EquipmentSlot.MAINHAND);
      ItemStack itemStack2 = (ItemStack)equipmentChanges.get(EquipmentSlot.OFFHAND);
      if (itemStack != null && itemStack2 != null && ItemStack.areEqual(itemStack, (ItemStack)this.lastEquipmentStacks.get(EquipmentSlot.OFFHAND)) && ItemStack.areEqual(itemStack2, (ItemStack)this.lastEquipmentStacks.get(EquipmentSlot.MAINHAND))) {
         ((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(this, new EntityStatusS2CPacket(this, (byte)55));
         equipmentChanges.remove(EquipmentSlot.MAINHAND);
         equipmentChanges.remove(EquipmentSlot.OFFHAND);
         this.lastEquipmentStacks.put(EquipmentSlot.MAINHAND, itemStack.copy());
         this.lastEquipmentStacks.put(EquipmentSlot.OFFHAND, itemStack2.copy());
      }

   }

   private void sendEquipmentChanges(Map equipmentChanges) {
      List list = Lists.newArrayListWithCapacity(equipmentChanges.size());
      equipmentChanges.forEach((slot, stack) -> {
         ItemStack itemStack = stack.copy();
         list.add(Pair.of(slot, itemStack));
         this.lastEquipmentStacks.put(slot, itemStack);
      });
      ((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getId(), list));
   }

   protected void turnHead(float bodyRotation) {
      float f = MathHelper.wrapDegrees(bodyRotation - this.bodyYaw);
      this.bodyYaw += f * 0.3F;
      float g = MathHelper.wrapDegrees(this.getYaw() - this.bodyYaw);
      float h = this.getMaxRelativeHeadRotation();
      if (Math.abs(g) > h) {
         this.bodyYaw += g - (float)MathHelper.sign((double)g) * h;
      }

   }

   protected float getMaxRelativeHeadRotation() {
      return 50.0F;
   }

   public void tickMovement() {
      if (this.jumpingCooldown > 0) {
         --this.jumpingCooldown;
      }

      if (this.isInterpolating()) {
         this.getInterpolator().tick();
      } else if (!this.canMoveVoluntarily()) {
         this.setVelocity(this.getVelocity().multiply(0.98));
      }

      if (this.headTrackingIncrements > 0) {
         this.lerpHeadYaw(this.headTrackingIncrements, this.serverHeadYaw);
         --this.headTrackingIncrements;
      }

      this.equipment.tick(this);
      Vec3d vec3d = this.getVelocity();
      double d = vec3d.x;
      double e = vec3d.y;
      double f = vec3d.z;
      if (this.getType().equals(EntityType.PLAYER)) {
         if (vec3d.horizontalLengthSquared() < 9.0E-6) {
            d = 0.0;
            f = 0.0;
         }
      } else {
         if (Math.abs(vec3d.x) < 0.003) {
            d = 0.0;
         }

         if (Math.abs(vec3d.z) < 0.003) {
            f = 0.0;
         }
      }

      if (Math.abs(vec3d.y) < 0.003) {
         e = 0.0;
      }

      this.setVelocity(d, e, f);
      Profiler profiler = Profilers.get();
      profiler.push("ai");
      this.tickMovementInput();
      if (this.isImmobile()) {
         this.jumping = false;
         this.sidewaysSpeed = 0.0F;
         this.forwardSpeed = 0.0F;
      } else if (this.canActVoluntarily() && !this.getWorld().isClient) {
         profiler.push("newAi");
         this.tickNewAi();
         profiler.pop();
      }

      profiler.pop();
      profiler.push("jump");
      if (this.jumping && this.shouldSwimInFluids()) {
         double g;
         if (this.isInLava()) {
            g = this.getFluidHeight(FluidTags.LAVA);
         } else {
            g = this.getFluidHeight(FluidTags.WATER);
         }

         boolean bl = this.isTouchingWater() && g > 0.0;
         double h = this.getSwimHeight();
         if (bl && (!this.isOnGround() || g > h)) {
            this.swimUpward(FluidTags.WATER);
         } else if (!this.isInLava() || this.isOnGround() && !(g > h)) {
            if ((this.isOnGround() || bl && g <= h) && this.jumpingCooldown == 0) {
               this.jump();
               this.jumpingCooldown = 10;
            }
         } else {
            this.swimUpward(FluidTags.LAVA);
         }
      } else {
         this.jumpingCooldown = 0;
      }

      profiler.pop();
      profiler.push("travel");
      if (this.isGliding()) {
         this.tickGliding();
      }

      Box box = this.getBoundingBox();
      Vec3d vec3d2 = new Vec3d((double)this.sidewaysSpeed, (double)this.upwardSpeed, (double)this.forwardSpeed);
      if (this.hasStatusEffect(StatusEffects.SLOW_FALLING) || this.hasStatusEffect(StatusEffects.LEVITATION)) {
         this.onLanding();
      }

      label124: {
         LivingEntity var17 = this.getControllingPassenger();
         if (var17 instanceof PlayerEntity playerEntity) {
            if (this.isAlive()) {
               this.travelControlled(playerEntity, vec3d2);
               break label124;
            }
         }

         if (this.canMoveVoluntarily() && this.canActVoluntarily()) {
            this.travel(vec3d2);
         }
      }

      if (!this.getWorld().isClient() || this.isLogicalSideForUpdatingMovement()) {
         this.tickBlockCollision();
      }

      if (this.getWorld().isClient()) {
         this.updateLimbs(this instanceof Flutterer);
      }

      profiler.pop();
      World var18 = this.getWorld();
      if (var18 instanceof ServerWorld serverWorld) {
         profiler.push("freezing");
         if (!this.inPowderSnow || !this.canFreeze()) {
            this.setFrozenTicks(Math.max(0, this.getFrozenTicks() - 2));
         }

         this.removePowderSnowSlow();
         this.addPowderSnowSlowIfNeeded();
         if (this.age % 40 == 0 && this.isFrozen() && this.canFreeze()) {
            this.damage(serverWorld, this.getDamageSources().freeze(), 1.0F);
         }

         profiler.pop();
      }

      profiler.push("push");
      if (this.riptideTicks > 0) {
         --this.riptideTicks;
         this.tickRiptide(box, this.getBoundingBox());
      }

      this.tickCramming();
      profiler.pop();
      var18 = this.getWorld();
      if (var18 instanceof ServerWorld serverWorld) {
         if (this.hurtByWater() && this.isTouchingWaterOrRain()) {
            this.damage(serverWorld, this.getDamageSources().drown(), 1.0F);
         }
      }

   }

   protected void tickMovementInput() {
      this.sidewaysSpeed *= 0.98F;
      this.forwardSpeed *= 0.98F;
   }

   public boolean hurtByWater() {
      return false;
   }

   public boolean isJumping() {
      return this.jumping;
   }

   protected void tickGliding() {
      this.limitFallDistance();
      if (!this.getWorld().isClient) {
         if (!this.canGlide()) {
            this.setFlag(7, false);
            return;
         }

         int i = this.glidingTicks + 1;
         if (i % 10 == 0) {
            int j = i / 10;
            if (j % 2 == 0) {
               List list = EquipmentSlot.VALUES.stream().filter((slot) -> {
                  return canGlideWith(this.getEquippedStack(slot), slot);
               }).toList();
               EquipmentSlot equipmentSlot = (EquipmentSlot)Util.getRandom(list, this.random);
               this.getEquippedStack(equipmentSlot).damage(1, this, (EquipmentSlot)equipmentSlot);
            }

            this.emitGameEvent(GameEvent.ELYTRA_GLIDE);
         }
      }

   }

   protected boolean canGlide() {
      if (!this.isOnGround() && !this.hasVehicle() && !this.hasStatusEffect(StatusEffects.LEVITATION)) {
         Iterator var1 = EquipmentSlot.VALUES.iterator();

         EquipmentSlot equipmentSlot;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            equipmentSlot = (EquipmentSlot)var1.next();
         } while(!canGlideWith(this.getEquippedStack(equipmentSlot), equipmentSlot));

         return true;
      } else {
         return false;
      }
   }

   protected void tickNewAi() {
   }

   protected void tickCramming() {
      List list = this.getWorld().getCrammedEntities(this, this.getBoundingBox());
      if (!list.isEmpty()) {
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            int i = serverWorld.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
               int j = 0;
               Iterator var5 = list.iterator();

               while(var5.hasNext()) {
                  Entity entity = (Entity)var5.next();
                  if (!entity.hasVehicle()) {
                     ++j;
                  }
               }

               if (j > i - 1) {
                  this.damage(serverWorld, this.getDamageSources().cramming(), 6.0F);
               }
            }
         }

         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            Entity entity2 = (Entity)var7.next();
            this.pushAway(entity2);
         }

      }
   }

   protected void tickRiptide(Box a, Box b) {
      Box box = a.union(b);
      List list = this.getWorld().getOtherEntities(this, box);
      if (!list.isEmpty()) {
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Entity entity = (Entity)var5.next();
            if (entity instanceof LivingEntity) {
               this.attackLivingEntity((LivingEntity)entity);
               this.riptideTicks = 0;
               this.setVelocity(this.getVelocity().multiply(-0.2));
               break;
            }
         }
      } else if (this.horizontalCollision) {
         this.riptideTicks = 0;
      }

      if (!this.getWorld().isClient && this.riptideTicks <= 0) {
         this.setLivingFlag(4, false);
         this.riptideAttackDamage = 0.0F;
         this.riptideStack = null;
      }

   }

   protected void pushAway(Entity entity) {
      entity.pushAwayFrom(this);
   }

   protected void attackLivingEntity(LivingEntity target) {
   }

   public boolean isUsingRiptide() {
      return ((Byte)this.dataTracker.get(LIVING_FLAGS) & 4) != 0;
   }

   public void stopRiding() {
      Entity entity = this.getVehicle();
      super.stopRiding();
      if (entity != null && entity != this.getVehicle() && !this.getWorld().isClient) {
         this.onDismounted(entity);
      }

   }

   public void tickRiding() {
      super.tickRiding();
      this.onLanding();
   }

   public PositionInterpolator getInterpolator() {
      return this.interpolator;
   }

   public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
      this.serverHeadYaw = (double)yaw;
      this.headTrackingIncrements = interpolationSteps;
   }

   public void setJumping(boolean jumping) {
      this.jumping = jumping;
   }

   public void triggerItemPickedUpByEntityCriteria(ItemEntity item) {
      Entity entity = item.getOwner();
      if (entity instanceof ServerPlayerEntity) {
         Criteria.THROWN_ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayerEntity)entity, item.getStack(), this);
      }

   }

   public void sendPickup(Entity item, int count) {
      if (!item.isRemoved() && !this.getWorld().isClient && (item instanceof ItemEntity || item instanceof PersistentProjectileEntity || item instanceof ExperienceOrbEntity)) {
         ((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(item, new ItemPickupAnimationS2CPacket(item.getId(), this.getId(), count));
      }

   }

   public boolean canSee(Entity entity) {
      return this.canSee(entity, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity.getEyeY());
   }

   public boolean canSee(Entity entity, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, double entityY) {
      if (entity.getWorld() != this.getWorld()) {
         return false;
      } else {
         Vec3d vec3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
         Vec3d vec3d2 = new Vec3d(entity.getX(), entityY, entity.getZ());
         if (vec3d2.distanceTo(vec3d) > 128.0) {
            return false;
         } else {
            return this.getWorld().raycast(new RaycastContext(vec3d, vec3d2, shapeType, fluidHandling, this)).getType() == HitResult.Type.MISS;
         }
      }
   }

   public float getYaw(float tickProgress) {
      return tickProgress == 1.0F ? this.headYaw : MathHelper.lerpAngleDegrees(tickProgress, this.lastHeadYaw, this.headYaw);
   }

   public float getHandSwingProgress(float tickProgress) {
      float f = this.handSwingProgress - this.lastHandSwingProgress;
      if (f < 0.0F) {
         ++f;
      }

      return this.lastHandSwingProgress + f * tickProgress;
   }

   public boolean canHit() {
      return !this.isRemoved();
   }

   public boolean isPushable() {
      return this.isAlive() && !this.isSpectator() && !this.isClimbing();
   }

   public float getHeadYaw() {
      return this.headYaw;
   }

   public void setHeadYaw(float headYaw) {
      this.headYaw = headYaw;
   }

   public void setBodyYaw(float bodyYaw) {
      this.bodyYaw = bodyYaw;
   }

   public Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
      return positionInPortal(super.positionInPortal(portalAxis, portalRect));
   }

   public static Vec3d positionInPortal(Vec3d pos) {
      return new Vec3d(pos.x, pos.y, 0.0);
   }

   public float getAbsorptionAmount() {
      return this.absorptionAmount;
   }

   public final void setAbsorptionAmount(float absorptionAmount) {
      this.setAbsorptionAmountUnclamped(MathHelper.clamp(absorptionAmount, 0.0F, this.getMaxAbsorption()));
   }

   protected void setAbsorptionAmountUnclamped(float absorptionAmount) {
      this.absorptionAmount = absorptionAmount;
   }

   public void enterCombat() {
   }

   public void endCombat() {
   }

   protected void markEffectsDirty() {
      this.effectsChanged = true;
   }

   public abstract Arm getMainArm();

   public boolean isUsingItem() {
      return ((Byte)this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
   }

   public Hand getActiveHand() {
      return ((Byte)this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
   }

   private void tickActiveItemStack() {
      if (this.isUsingItem()) {
         if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
            this.activeItemStack = this.getStackInHand(this.getActiveHand());
            this.tickItemStackUsage(this.activeItemStack);
         } else {
            this.clearActiveItem();
         }
      }

   }

   @Nullable
   private ItemEntity createItemEntity(ItemStack stack, boolean atSelf, boolean retainOwnership) {
      if (stack.isEmpty()) {
         return null;
      } else {
         double d = this.getEyeY() - 0.30000001192092896;
         ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), d, this.getZ(), stack);
         itemEntity.setPickupDelay(40);
         if (retainOwnership) {
            itemEntity.setThrower(this);
         }

         float f;
         float g;
         if (atSelf) {
            f = this.random.nextFloat() * 0.5F;
            g = this.random.nextFloat() * 6.2831855F;
            itemEntity.setVelocity((double)(-MathHelper.sin(g) * f), 0.20000000298023224, (double)(MathHelper.cos(g) * f));
         } else {
            f = 0.3F;
            g = MathHelper.sin(this.getPitch() * 0.017453292F);
            float h = MathHelper.cos(this.getPitch() * 0.017453292F);
            float i = MathHelper.sin(this.getYaw() * 0.017453292F);
            float j = MathHelper.cos(this.getYaw() * 0.017453292F);
            float k = this.random.nextFloat() * 6.2831855F;
            float l = 0.02F * this.random.nextFloat();
            itemEntity.setVelocity((double)(-i * h * 0.3F) + Math.cos((double)k) * (double)l, (double)(-g * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(j * h * 0.3F) + Math.sin((double)k) * (double)l);
         }

         return itemEntity;
      }
   }

   protected void tickItemStackUsage(ItemStack stack) {
      stack.usageTick(this.getWorld(), this, this.getItemUseTimeLeft());
      if (--this.itemUseTimeLeft == 0 && !this.getWorld().isClient && !stack.isUsedOnRelease()) {
         this.consumeItem();
      }

   }

   private void updateLeaningPitch() {
      this.lastLeaningPitch = this.leaningPitch;
      if (this.isInSwimmingPose()) {
         this.leaningPitch = Math.min(1.0F, this.leaningPitch + 0.09F);
      } else {
         this.leaningPitch = Math.max(0.0F, this.leaningPitch - 0.09F);
      }

   }

   protected void setLivingFlag(int mask, boolean value) {
      int i = (Byte)this.dataTracker.get(LIVING_FLAGS);
      if (value) {
         i |= mask;
      } else {
         i &= ~mask;
      }

      this.dataTracker.set(LIVING_FLAGS, (byte)i);
   }

   public void setCurrentHand(Hand hand) {
      ItemStack itemStack = this.getStackInHand(hand);
      if (!itemStack.isEmpty() && !this.isUsingItem()) {
         this.activeItemStack = itemStack;
         this.itemUseTimeLeft = itemStack.getMaxUseTime(this);
         if (!this.getWorld().isClient) {
            this.setLivingFlag(1, true);
            this.setLivingFlag(2, hand == Hand.OFF_HAND);
            this.emitGameEvent(GameEvent.ITEM_INTERACT_START);
         }

      }
   }

   public void onTrackedDataSet(TrackedData data) {
      super.onTrackedDataSet(data);
      if (SLEEPING_POSITION.equals(data)) {
         if (this.getWorld().isClient) {
            this.getSleepingPosition().ifPresent(this::setPositionInBed);
         }
      } else if (LIVING_FLAGS.equals(data) && this.getWorld().isClient) {
         if (this.isUsingItem() && this.activeItemStack.isEmpty()) {
            this.activeItemStack = this.getStackInHand(this.getActiveHand());
            if (!this.activeItemStack.isEmpty()) {
               this.itemUseTimeLeft = this.activeItemStack.getMaxUseTime(this);
            }
         } else if (!this.isUsingItem() && !this.activeItemStack.isEmpty()) {
            this.activeItemStack = ItemStack.EMPTY;
            this.itemUseTimeLeft = 0;
         }
      }

   }

   public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
      super.lookAt(anchorPoint, target);
      this.lastHeadYaw = this.headYaw;
      this.bodyYaw = this.headYaw;
      this.lastBodyYaw = this.bodyYaw;
   }

   public float lerpYaw(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastBodyYaw, this.bodyYaw);
   }

   public void spawnItemParticles(ItemStack stack, int count) {
      for(int i = 0; i < count; ++i) {
         Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         vec3d = vec3d.rotateX(-this.getPitch() * 0.017453292F);
         vec3d = vec3d.rotateY(-this.getYaw() * 0.017453292F);
         double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
         Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
         vec3d2 = vec3d2.rotateX(-this.getPitch() * 0.017453292F);
         vec3d2 = vec3d2.rotateY(-this.getYaw() * 0.017453292F);
         vec3d2 = vec3d2.add(this.getX(), this.getEyeY(), this.getZ());
         this.getWorld().addParticleClient(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
      }

   }

   protected void consumeItem() {
      if (!this.getWorld().isClient || this.isUsingItem()) {
         Hand hand = this.getActiveHand();
         if (!this.activeItemStack.equals(this.getStackInHand(hand))) {
            this.stopUsingItem();
         } else {
            if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
               ItemStack itemStack = this.activeItemStack.finishUsing(this.getWorld(), this);
               if (itemStack != this.activeItemStack) {
                  this.setStackInHand(hand, itemStack);
               }

               this.clearActiveItem();
            }

         }
      }
   }

   public void giveOrDropStack(ItemStack stack) {
   }

   public ItemStack getActiveItem() {
      return this.activeItemStack;
   }

   public int getItemUseTimeLeft() {
      return this.itemUseTimeLeft;
   }

   public int getItemUseTime() {
      return this.isUsingItem() ? this.activeItemStack.getMaxUseTime(this) - this.getItemUseTimeLeft() : 0;
   }

   public void stopUsingItem() {
      ItemStack itemStack = this.getStackInHand(this.getActiveHand());
      if (!this.activeItemStack.isEmpty() && ItemStack.areItemsEqual(itemStack, this.activeItemStack)) {
         this.activeItemStack = itemStack;
         this.activeItemStack.onStoppedUsing(this.getWorld(), this, this.getItemUseTimeLeft());
         if (this.activeItemStack.isUsedOnRelease()) {
            this.tickActiveItemStack();
         }
      }

      this.clearActiveItem();
   }

   public void clearActiveItem() {
      if (!this.getWorld().isClient) {
         boolean bl = this.isUsingItem();
         this.setLivingFlag(1, false);
         if (bl) {
            this.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
         }
      }

      this.activeItemStack = ItemStack.EMPTY;
      this.itemUseTimeLeft = 0;
   }

   public boolean isBlocking() {
      return this.getBlockingItem() != null;
   }

   @Nullable
   public ItemStack getBlockingItem() {
      if (!this.isUsingItem()) {
         return null;
      } else {
         BlocksAttacksComponent blocksAttacksComponent = (BlocksAttacksComponent)this.activeItemStack.get(DataComponentTypes.BLOCKS_ATTACKS);
         if (blocksAttacksComponent != null) {
            int i = this.activeItemStack.getItem().getMaxUseTime(this.activeItemStack, this) - this.itemUseTimeLeft;
            if (i >= blocksAttacksComponent.getBlockDelayTicks()) {
               return this.activeItemStack;
            }
         }

         return null;
      }
   }

   public boolean isHoldingOntoLadder() {
      return this.isSneaking();
   }

   public boolean isGliding() {
      return this.getFlag(7);
   }

   public boolean isInSwimmingPose() {
      return super.isInSwimmingPose() || !this.isGliding() && this.isInPose(EntityPose.GLIDING);
   }

   public int getGlidingTicks() {
      return this.glidingTicks;
   }

   public boolean teleport(double x, double y, double z, boolean particleEffects) {
      double d = this.getX();
      double e = this.getY();
      double f = this.getZ();
      double g = y;
      boolean bl = false;
      BlockPos blockPos = BlockPos.ofFloored(x, y, z);
      World world = this.getWorld();
      if (world.isChunkLoaded(blockPos)) {
         boolean bl2 = false;

         while(!bl2 && blockPos.getY() > world.getBottomY()) {
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState = world.getBlockState(blockPos2);
            if (blockState.blocksMovement()) {
               bl2 = true;
            } else {
               --g;
               blockPos = blockPos2;
            }
         }

         if (bl2) {
            this.requestTeleport(x, g, z);
            if (world.isSpaceEmpty(this) && !world.containsFluid(this.getBoundingBox())) {
               bl = true;
            }
         }
      }

      if (!bl) {
         this.requestTeleport(d, e, f);
         return false;
      } else {
         if (particleEffects) {
            world.sendEntityStatus(this, (byte)46);
         }

         if (this instanceof PathAwareEntity) {
            PathAwareEntity pathAwareEntity = (PathAwareEntity)this;
            pathAwareEntity.getNavigation().stop();
         }

         return true;
      }
   }

   public boolean isAffectedBySplashPotions() {
      return !this.isDead();
   }

   public boolean isMobOrPlayer() {
      return true;
   }

   public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
   }

   public boolean canPickUpLoot() {
      return false;
   }

   public final EntityDimensions getDimensions(EntityPose pose) {
      return pose == EntityPose.SLEEPING ? SLEEPING_DIMENSIONS : this.getBaseDimensions(pose).scaled(this.getScale());
   }

   protected EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.getType().getDimensions().scaled(this.getScaleFactor());
   }

   public ImmutableList getPoses() {
      return ImmutableList.of(EntityPose.STANDING);
   }

   public Box getBoundingBox(EntityPose pose) {
      EntityDimensions entityDimensions = this.getDimensions(pose);
      return new Box((double)(-entityDimensions.width() / 2.0F), 0.0, (double)(-entityDimensions.width() / 2.0F), (double)(entityDimensions.width() / 2.0F), (double)entityDimensions.height(), (double)(entityDimensions.width() / 2.0F));
   }

   protected boolean wouldNotSuffocateInPose(EntityPose pose) {
      Box box = this.getDimensions(pose).getBoxAt(this.getPos());
      return this.getWorld().isBlockSpaceEmpty(this, box);
   }

   public boolean canUsePortals(boolean allowVehicles) {
      return super.canUsePortals(allowVehicles) && !this.isSleeping();
   }

   public Optional getSleepingPosition() {
      return (Optional)this.dataTracker.get(SLEEPING_POSITION);
   }

   public void setSleepingPosition(BlockPos pos) {
      this.dataTracker.set(SLEEPING_POSITION, Optional.of(pos));
   }

   public void clearSleepingPosition() {
      this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
   }

   public boolean isSleeping() {
      return this.getSleepingPosition().isPresent();
   }

   public void sleep(BlockPos pos) {
      if (this.hasVehicle()) {
         this.stopRiding();
      }

      BlockState blockState = this.getWorld().getBlockState(pos);
      if (blockState.getBlock() instanceof BedBlock) {
         this.getWorld().setBlockState(pos, (BlockState)blockState.with(BedBlock.OCCUPIED, true), 3);
      }

      this.setPose(EntityPose.SLEEPING);
      this.setPositionInBed(pos);
      this.setSleepingPosition(pos);
      this.setVelocity(Vec3d.ZERO);
      this.velocityDirty = true;
   }

   private void setPositionInBed(BlockPos pos) {
      this.setPosition((double)pos.getX() + 0.5, (double)pos.getY() + 0.6875, (double)pos.getZ() + 0.5);
   }

   private boolean isSleepingInBed() {
      return (Boolean)this.getSleepingPosition().map((pos) -> {
         return this.getWorld().getBlockState(pos).getBlock() instanceof BedBlock;
      }).orElse(false);
   }

   public void wakeUp() {
      Optional var10000 = this.getSleepingPosition();
      World var10001 = this.getWorld();
      java.util.Objects.requireNonNull(var10001);
      var10000.filter(var10001::isChunkLoaded).ifPresent((pos) -> {
         BlockState blockState = this.getWorld().getBlockState(pos);
         if (blockState.getBlock() instanceof BedBlock) {
            Direction direction = (Direction)blockState.get(BedBlock.FACING);
            this.getWorld().setBlockState(pos, (BlockState)blockState.with(BedBlock.OCCUPIED, false), 3);
            Vec3d vec3d = (Vec3d)BedBlock.findWakeUpPosition(this.getType(), this.getWorld(), pos, direction, this.getYaw()).orElseGet(() -> {
               BlockPos blockPos2 = pos.up();
               return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
            });
            Vec3d vec3d2 = Vec3d.ofBottomCenter(pos).subtract(vec3d).normalize();
            float f = (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d2.z, vec3d2.x) * 57.2957763671875 - 90.0);
            this.setPosition(vec3d.x, vec3d.y, vec3d.z);
            this.setYaw(f);
            this.setPitch(0.0F);
         }

      });
      Vec3d vec3d = this.getPos();
      this.setPose(EntityPose.STANDING);
      this.setPosition(vec3d.x, vec3d.y, vec3d.z);
      this.clearSleepingPosition();
   }

   @Nullable
   public Direction getSleepingDirection() {
      BlockPos blockPos = (BlockPos)this.getSleepingPosition().orElse((Object)null);
      return blockPos != null ? BedBlock.getDirection(this.getWorld(), blockPos) : null;
   }

   public boolean isInsideWall() {
      return !this.isSleeping() && super.isInsideWall();
   }

   public ItemStack getProjectileType(ItemStack stack) {
      return ItemStack.EMPTY;
   }

   private static byte getEquipmentBreakStatus(EquipmentSlot slot) {
      byte var10000;
      switch (slot) {
         case MAINHAND:
            var10000 = 47;
            break;
         case OFFHAND:
            var10000 = 48;
            break;
         case HEAD:
            var10000 = 49;
            break;
         case CHEST:
            var10000 = 50;
            break;
         case FEET:
            var10000 = 52;
            break;
         case LEGS:
            var10000 = 51;
            break;
         case BODY:
            var10000 = 65;
            break;
         case SADDLE:
            var10000 = 68;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void sendEquipmentBreakStatus(Item item, EquipmentSlot slot) {
      this.getWorld().sendEntityStatus(this, getEquipmentBreakStatus(slot));
      this.onEquipmentRemoved(this.getEquippedStack(slot), slot, this.attributes);
   }

   private void onEquipmentRemoved(ItemStack removedEquipment, EquipmentSlot slot, AttributeContainer container) {
      removedEquipment.applyAttributeModifiers(slot, (attribute, modifier) -> {
         EntityAttributeInstance entityAttributeInstance = container.getCustomInstance(attribute);
         if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(modifier);
         }

      });
      EnchantmentHelper.removeLocationBasedEffects(removedEquipment, this, slot);
   }

   public static EquipmentSlot getSlotForHand(Hand hand) {
      return hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
   }

   public final boolean canEquipFromDispenser(ItemStack stack) {
      if (this.isAlive() && !this.isSpectator()) {
         EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
         if (equippableComponent != null && equippableComponent.dispensable()) {
            EquipmentSlot equipmentSlot = equippableComponent.slot();
            if (this.canUseSlot(equipmentSlot) && equippableComponent.allows(this.getType())) {
               return this.getEquippedStack(equipmentSlot).isEmpty() && this.canDispenserEquipSlot(equipmentSlot);
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canDispenserEquipSlot(EquipmentSlot slot) {
      return true;
   }

   public final EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
      EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
      return equippableComponent != null && this.canUseSlot(equippableComponent.slot()) ? equippableComponent.slot() : EquipmentSlot.MAINHAND;
   }

   public final boolean canEquip(ItemStack stack, EquipmentSlot slot) {
      EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
      if (equippableComponent == null) {
         return slot == EquipmentSlot.MAINHAND && this.canUseSlot(EquipmentSlot.MAINHAND);
      } else {
         return slot == equippableComponent.slot() && this.canUseSlot(equippableComponent.slot()) && equippableComponent.allows(this.getType());
      }
   }

   private static StackReference getStackReference(LivingEntity entity, EquipmentSlot slot) {
      return slot != EquipmentSlot.HEAD && slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND ? StackReference.of(entity, slot, (stack) -> {
         return stack.isEmpty() || entity.getPreferredEquipmentSlot(stack) == slot;
      }) : StackReference.of(entity, slot);
   }

   @Nullable
   private static EquipmentSlot getEquipmentSlot(int slotId) {
      if (slotId == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
         return EquipmentSlot.HEAD;
      } else if (slotId == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
         return EquipmentSlot.CHEST;
      } else if (slotId == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
         return EquipmentSlot.LEGS;
      } else if (slotId == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
         return EquipmentSlot.FEET;
      } else if (slotId == 98) {
         return EquipmentSlot.MAINHAND;
      } else if (slotId == 99) {
         return EquipmentSlot.OFFHAND;
      } else if (slotId == 105) {
         return EquipmentSlot.BODY;
      } else {
         return slotId == 106 ? EquipmentSlot.SADDLE : null;
      }
   }

   public StackReference getStackReference(int mappedIndex) {
      EquipmentSlot equipmentSlot = getEquipmentSlot(mappedIndex);
      return equipmentSlot != null ? getStackReference(this, equipmentSlot) : super.getStackReference(mappedIndex);
   }

   public boolean canFreeze() {
      if (this.isSpectator()) {
         return false;
      } else {
         Iterator var1 = AttributeModifierSlot.ARMOR.iterator();

         EquipmentSlot equipmentSlot;
         do {
            if (!var1.hasNext()) {
               return super.canFreeze();
            }

            equipmentSlot = (EquipmentSlot)var1.next();
         } while(!this.getEquippedStack(equipmentSlot).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES));

         return false;
      }
   }

   public boolean isGlowing() {
      return !this.getWorld().isClient() && this.hasStatusEffect(StatusEffects.GLOWING) || super.isGlowing();
   }

   public float getBodyYaw() {
      return this.bodyYaw;
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      double d = packet.getX();
      double e = packet.getY();
      double f = packet.getZ();
      float g = packet.getYaw();
      float h = packet.getPitch();
      this.updateTrackedPosition(d, e, f);
      this.bodyYaw = packet.getHeadYaw();
      this.headYaw = packet.getHeadYaw();
      this.lastBodyYaw = this.bodyYaw;
      this.lastHeadYaw = this.headYaw;
      this.setId(packet.getEntityId());
      this.setUuid(packet.getUuid());
      this.updatePositionAndAngles(d, e, f, g, h);
      this.setVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
   }

   public float getWeaponDisableBlockingForSeconds() {
      WeaponComponent weaponComponent = (WeaponComponent)this.getWeaponStack().get(DataComponentTypes.WEAPON);
      return weaponComponent != null ? weaponComponent.disableBlockingForSeconds() : 0.0F;
   }

   public float getStepHeight() {
      float f = (float)this.getAttributeValue(EntityAttributes.STEP_HEIGHT);
      return this.getControllingPassenger() instanceof PlayerEntity ? Math.max(f, 1.0F) : f;
   }

   public Vec3d getPassengerRidingPos(Entity passenger) {
      return this.getPos().add(this.getPassengerAttachmentPos(passenger, this.getDimensions(this.getPose()), this.getScale() * this.getScaleFactor()));
   }

   protected void lerpHeadYaw(int headTrackingIncrements, double serverHeadYaw) {
      this.headYaw = (float)MathHelper.lerpAngleDegrees(1.0 / (double)headTrackingIncrements, (double)this.headYaw, serverHeadYaw);
   }

   public void setOnFireForTicks(int ticks) {
      super.setOnFireForTicks(MathHelper.ceil((double)ticks * this.getAttributeValue(EntityAttributes.BURNING_TIME)));
   }

   public boolean isInCreativeMode() {
      return false;
   }

   public boolean isInvulnerableTo(ServerWorld world, DamageSource source) {
      return this.isAlwaysInvulnerableTo(source) || EnchantmentHelper.isInvulnerableTo(world, this, source);
   }

   public static boolean canGlideWith(ItemStack stack, EquipmentSlot slot) {
      if (!stack.contains(DataComponentTypes.GLIDER)) {
         return false;
      } else {
         EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
         return equippableComponent != null && slot == equippableComponent.slot() && !stack.willBreakNextUse();
      }
   }

   @VisibleForTesting
   public int getPlayerHitTimer() {
      return this.playerHitTimer;
   }

   public boolean hasWaypoint() {
      return this.getAttributeValue(EntityAttributes.WAYPOINT_TRANSMIT_RANGE) > 0.0;
   }

   public Optional createTracker(ServerPlayerEntity receiver) {
      if (!this.firstUpdate && receiver != this) {
         if (ServerWaypoint.cannotReceive(this, receiver)) {
            return Optional.empty();
         } else {
            Waypoint.Config config = this.waypointConfig.withTeamColorOf(this);
            if (ServerWaypoint.shouldUseAzimuth(this, receiver)) {
               return Optional.of(new ServerWaypoint.AzimuthWaypointTracker(this, config, receiver));
            } else {
               return !ServerWaypoint.canReceive(this.getChunkPos(), receiver) ? Optional.of(new ServerWaypoint.ChunkWaypointTracker(this, config, receiver)) : Optional.of(new ServerWaypoint.PositionalWaypointTracker(this, config, receiver));
            }
         }
      } else {
         return Optional.empty();
      }
   }

   public Waypoint.Config getWaypointConfig() {
      return this.waypointConfig;
   }

   static {
      SPRINTING_SPEED_BOOST = new EntityAttributeModifier(SPRINTING_SPEED_MODIFIER_ID, 0.30000001192092896, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
      LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
      HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
      POTION_SWIRLS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.PARTICLE_LIST);
      POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      STUCK_ARROW_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
      STINGER_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
      SLEEPING_POSITION = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
      SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2F, 0.2F).withEyeHeight(0.2F);
      NOT_WEARING_GAZE_DISGUISE_PREDICATE = (entity) -> {
         if (entity instanceof PlayerEntity playerEntity) {
            ItemStack var2 = playerEntity.getEquippedStack(EquipmentSlot.HEAD);
            return !var2.isIn(ItemTags.GAZE_DISGUISE_EQUIPMENT);
         } else {
            return true;
         }
      };
      BRAIN = new Dynamic(JavaOps.INSTANCE, Map.of("memories", Map.of()));
   }

   public static record FallSounds(SoundEvent small, SoundEvent big) {
      public FallSounds(SoundEvent soundEvent, SoundEvent soundEvent2) {
         this.small = soundEvent;
         this.big = soundEvent2;
      }

      public SoundEvent small() {
         return this.small;
      }

      public SoundEvent big() {
         return this.big;
      }
   }
}
