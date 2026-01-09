package net.minecraft.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.dimension.PortalManager;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.waypoint.ServerWaypoint;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Entity implements DataTracked, Nameable, EntityLike, ScoreHolder, ComponentsAccess, AttachmentTarget {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ID_KEY = "id";
   public static final String UUID_KEY = "UUID";
   public static final String PASSENGERS_KEY = "Passengers";
   public static final String CUSTOM_DATA_KEY = "data";
   public static final String POS_KEY = "Pos";
   public static final String MOTION_KEY = "Motion";
   public static final String ROTATION_KEY = "Rotation";
   public static final String PORTAL_COOLDOWN_KEY = "PortalCooldown";
   public static final String NO_GRAVITY_KEY = "NoGravity";
   public static final String AIR_KEY = "Air";
   public static final String ON_GROUND_KEY = "OnGround";
   public static final String FALL_DISTANCE_KEY = "fall_distance";
   public static final String FIRE_KEY = "Fire";
   public static final String SILENT_KEY = "Silent";
   public static final String GLOWING_KEY = "Glowing";
   public static final String INVULNERABLE_KEY = "Invulnerable";
   private static final AtomicInteger CURRENT_ID = new AtomicInteger();
   public static final int field_49791 = 0;
   public static final int MAX_RIDING_COOLDOWN = 60;
   public static final int DEFAULT_PORTAL_COOLDOWN = 300;
   public static final int MAX_COMMAND_TAGS = 1024;
   private static final Codec TAG_LIST_CODEC;
   public static final float field_44870 = 0.2F;
   public static final double field_44871 = 0.500001;
   public static final double field_44872 = 0.999999;
   public static final int DEFAULT_MIN_FREEZE_DAMAGE_TICKS = 140;
   public static final int FREEZING_DAMAGE_INTERVAL = 40;
   public static final int field_49073 = 3;
   private static final ImmutableList X_THEN_Z;
   private static final ImmutableList Z_THEN_X;
   private static final Box NULL_BOX;
   private static final double SPEED_IN_WATER = 0.014;
   private static final double SPEED_IN_LAVA_IN_NETHER = 0.007;
   private static final double SPEED_IN_LAVA = 0.0023333333333333335;
   private static double renderDistanceMultiplier;
   private final EntityType type;
   private boolean alwaysSyncAbsolute;
   private int id;
   public boolean intersectionChecked;
   private ImmutableList passengerList;
   protected int ridingCooldown;
   @Nullable
   private Entity vehicle;
   private World world;
   public double lastX;
   public double lastY;
   public double lastZ;
   private Vec3d pos;
   private BlockPos blockPos;
   private ChunkPos chunkPos;
   private Vec3d velocity;
   private float yaw;
   private float pitch;
   public float lastYaw;
   public float lastPitch;
   private Box boundingBox;
   private boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean groundCollision;
   public boolean collidedSoftly;
   public boolean velocityModified;
   protected Vec3d movementMultiplier;
   @Nullable
   private RemovalReason removalReason;
   public static final float DEFAULT_FRICTION = 0.6F;
   public static final float MIN_RISING_BUBBLE_COLUMN_SPEED = 1.8F;
   public float distanceTraveled;
   public float speed;
   public double fallDistance;
   private float nextStepSoundDistance;
   public double lastRenderX;
   public double lastRenderY;
   public double lastRenderZ;
   public boolean noClip;
   protected final Random random;
   public int age;
   private int fireTicks;
   protected boolean touchingWater;
   protected Object2DoubleMap fluidHeight;
   protected boolean submergedInWater;
   private final Set submergedFluidTag;
   public int timeUntilRegen;
   protected boolean firstUpdate;
   protected final DataTracker dataTracker;
   protected static final TrackedData FLAGS;
   protected static final int ON_FIRE_FLAG_INDEX = 0;
   private static final int SNEAKING_FLAG_INDEX = 1;
   private static final int SPRINTING_FLAG_INDEX = 3;
   private static final int SWIMMING_FLAG_INDEX = 4;
   private static final int INVISIBLE_FLAG_INDEX = 5;
   protected static final int GLOWING_FLAG_INDEX = 6;
   protected static final int GLIDING_FLAG_INDEX = 7;
   private static final TrackedData AIR;
   private static final TrackedData CUSTOM_NAME;
   private static final TrackedData NAME_VISIBLE;
   private static final TrackedData SILENT;
   private static final TrackedData NO_GRAVITY;
   protected static final TrackedData POSE;
   private static final TrackedData FROZEN_TICKS;
   private EntityChangeListener changeListener;
   private final TrackedPosition trackedPosition;
   public boolean velocityDirty;
   @Nullable
   public PortalManager portalManager;
   private int portalCooldown;
   private boolean invulnerable;
   protected UUID uuid;
   protected String uuidString;
   private boolean glowing;
   private final Set commandTags;
   private final double[] pistonMovementDelta;
   private long pistonMovementTick;
   private EntityDimensions dimensions;
   private float standingEyeHeight;
   public boolean inPowderSnow;
   public boolean wasInPowderSnow;
   public Optional supportingBlockPos;
   private boolean forceUpdateSupportingBlockPos;
   private float lastChimeIntensity;
   private int lastChimeAge;
   private boolean hasVisualFire;
   @Nullable
   private BlockState stateAtPos;
   public static final int MAX_QUEUED_COLLISION_CHECKS = 100;
   private final ArrayDeque queuedCollisionChecks;
   private final List currentlyCheckedCollisions;
   private final LongSet collidedBlockPositions;
   private final EntityCollisionHandler.Impl collisionHandler;
   private NbtComponent customData;

   public Entity(EntityType type, World world) {
      this.id = CURRENT_ID.incrementAndGet();
      this.passengerList = ImmutableList.of();
      this.velocity = Vec3d.ZERO;
      this.boundingBox = NULL_BOX;
      this.movementMultiplier = Vec3d.ZERO;
      this.nextStepSoundDistance = 1.0F;
      this.random = Random.create();
      this.fluidHeight = new Object2DoubleArrayMap(2);
      this.submergedFluidTag = new HashSet();
      this.firstUpdate = true;
      this.changeListener = EntityChangeListener.NONE;
      this.trackedPosition = new TrackedPosition();
      this.uuid = MathHelper.randomUuid(this.random);
      this.uuidString = this.uuid.toString();
      this.commandTags = Sets.newHashSet();
      this.pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
      this.supportingBlockPos = Optional.empty();
      this.forceUpdateSupportingBlockPos = false;
      this.stateAtPos = null;
      this.queuedCollisionChecks = new ArrayDeque(100);
      this.currentlyCheckedCollisions = new ObjectArrayList();
      this.collidedBlockPositions = new LongOpenHashSet();
      this.collisionHandler = new EntityCollisionHandler.Impl();
      this.customData = NbtComponent.DEFAULT;
      this.type = type;
      this.world = world;
      this.dimensions = type.getDimensions();
      this.pos = Vec3d.ZERO;
      this.blockPos = BlockPos.ORIGIN;
      this.chunkPos = ChunkPos.ORIGIN;
      DataTracker.Builder builder = new DataTracker.Builder(this);
      builder.add(FLAGS, (byte)0);
      builder.add(AIR, this.getMaxAir());
      builder.add(NAME_VISIBLE, false);
      builder.add(CUSTOM_NAME, Optional.empty());
      builder.add(SILENT, false);
      builder.add(NO_GRAVITY, false);
      builder.add(POSE, EntityPose.STANDING);
      builder.add(FROZEN_TICKS, 0);
      this.initDataTracker(builder);
      this.dataTracker = builder.build();
      this.setPosition(0.0, 0.0, 0.0);
      this.standingEyeHeight = this.dimensions.eyeHeight();
   }

   public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
      VoxelShape voxelShape = state.getCollisionShape(this.getWorld(), pos, ShapeContext.of(this)).offset((Vec3i)pos);
      return VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(this.getBoundingBox()), BooleanBiFunction.AND);
   }

   public int getTeamColorValue() {
      AbstractTeam abstractTeam = this.getScoreboardTeam();
      return abstractTeam != null && abstractTeam.getColor().getColorValue() != null ? abstractTeam.getColor().getColorValue() : 16777215;
   }

   public boolean isSpectator() {
      return false;
   }

   public final void detach() {
      if (this.hasPassengers()) {
         this.removeAllPassengers();
      }

      if (this.hasVehicle()) {
         this.stopRiding();
      }

   }

   public void updateTrackedPosition(double x, double y, double z) {
      this.trackedPosition.setPos(new Vec3d(x, y, z));
   }

   public TrackedPosition getTrackedPosition() {
      return this.trackedPosition;
   }

   public EntityType getType() {
      return this.type;
   }

   public boolean shouldAlwaysSyncAbsolute() {
      return this.alwaysSyncAbsolute;
   }

   public void setAlwaysSyncAbsolute(boolean alwaysSyncAbsolute) {
      this.alwaysSyncAbsolute = alwaysSyncAbsolute;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Set getCommandTags() {
      return this.commandTags;
   }

   public boolean addCommandTag(String tag) {
      return this.commandTags.size() >= 1024 ? false : this.commandTags.add(tag);
   }

   public boolean removeCommandTag(String tag) {
      return this.commandTags.remove(tag);
   }

   public void kill(ServerWorld world) {
      this.remove(Entity.RemovalReason.KILLED);
      this.emitGameEvent(GameEvent.ENTITY_DIE);
   }

   public final void discard() {
      this.remove(Entity.RemovalReason.DISCARDED);
   }

   protected abstract void initDataTracker(DataTracker.Builder builder);

   public DataTracker getDataTracker() {
      return this.dataTracker;
   }

   public boolean equals(Object o) {
      if (o instanceof Entity) {
         return ((Entity)o).id == this.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   public void remove(RemovalReason reason) {
      this.setRemoved(reason);
   }

   public void onRemoved() {
   }

   public void onRemove(RemovalReason reason) {
   }

   public void setPose(EntityPose pose) {
      this.dataTracker.set(POSE, pose);
   }

   public EntityPose getPose() {
      return (EntityPose)this.dataTracker.get(POSE);
   }

   public boolean isInPose(EntityPose pose) {
      return this.getPose() == pose;
   }

   public boolean isInRange(Entity entity, double radius) {
      return this.getPos().isInRange(entity.getPos(), radius);
   }

   public boolean isInRange(Entity entity, double horizontalRadius, double verticalRadius) {
      double d = entity.getX() - this.getX();
      double e = entity.getY() - this.getY();
      double f = entity.getZ() - this.getZ();
      return MathHelper.squaredHypot(d, f) < MathHelper.square(horizontalRadius) && MathHelper.square(e) < MathHelper.square(verticalRadius);
   }

   protected void setRotation(float yaw, float pitch) {
      this.setYaw(yaw % 360.0F);
      this.setPitch(pitch % 360.0F);
   }

   public final void setPosition(Vec3d pos) {
      this.setPosition(pos.getX(), pos.getY(), pos.getZ());
   }

   public void setPosition(double x, double y, double z) {
      this.setPos(x, y, z);
      this.setBoundingBox(this.calculateBoundingBox());
   }

   protected final Box calculateBoundingBox() {
      return this.calculateDefaultBoundingBox(this.pos);
   }

   protected Box calculateDefaultBoundingBox(Vec3d pos) {
      return this.dimensions.getBoxAt(pos);
   }

   protected void refreshPosition() {
      this.setPosition(this.pos.x, this.pos.y, this.pos.z);
   }

   public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
      float f = (float)cursorDeltaY * 0.15F;
      float g = (float)cursorDeltaX * 0.15F;
      this.setPitch(this.getPitch() + f);
      this.setYaw(this.getYaw() + g);
      this.setPitch(MathHelper.clamp(this.getPitch(), -90.0F, 90.0F));
      this.lastPitch += f;
      this.lastYaw += g;
      this.lastPitch = MathHelper.clamp(this.lastPitch, -90.0F, 90.0F);
      if (this.vehicle != null) {
         this.vehicle.onPassengerLookAround(this);
      }

   }

   public void tick() {
      this.baseTick();
   }

   public void baseTick() {
      Profiler profiler = Profilers.get();
      profiler.push("entityBaseTick");
      this.stateAtPos = null;
      if (this.hasVehicle() && this.getVehicle().isRemoved()) {
         this.stopRiding();
      }

      if (this.ridingCooldown > 0) {
         --this.ridingCooldown;
      }

      this.tickPortalTeleportation();
      if (this.shouldSpawnSprintingParticles()) {
         this.spawnSprintingParticles();
      }

      this.wasInPowderSnow = this.inPowderSnow;
      this.inPowderSnow = false;
      this.updateWaterState();
      this.updateSubmergedInWaterState();
      this.updateSwimming();
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         if (this.fireTicks > 0) {
            if (this.isFireImmune()) {
               this.setFireTicks(this.fireTicks - 4);
            } else {
               if (this.fireTicks % 20 == 0 && !this.isInLava()) {
                  this.damage(serverWorld, this.getDamageSources().onFire(), 1.0F);
               }

               this.setFireTicks(this.fireTicks - 1);
            }
         }
      } else {
         this.extinguish();
      }

      if (this.isInLava()) {
         this.fallDistance *= 0.5;
      }

      this.attemptTickInVoid();
      if (!this.getWorld().isClient) {
         this.setOnFire(this.fireTicks > 0);
      }

      this.firstUpdate = false;
      var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         if (this instanceof Leashable) {
            Leashable.tickLeash(serverWorld, (Entity)((Leashable)this));
         }
      }

      profiler.pop();
   }

   public void setOnFire(boolean onFire) {
      this.setFlag(0, onFire || this.hasVisualFire);
   }

   public void attemptTickInVoid() {
      if (this.getY() < (double)(this.getWorld().getBottomY() - 64)) {
         this.tickInVoid();
      }

   }

   public void resetPortalCooldown() {
      this.portalCooldown = this.getDefaultPortalCooldown();
   }

   public void setPortalCooldown(int portalCooldown) {
      this.portalCooldown = portalCooldown;
   }

   public int getPortalCooldown() {
      return this.portalCooldown;
   }

   public boolean hasPortalCooldown() {
      return this.portalCooldown > 0;
   }

   protected void tickPortalCooldown() {
      if (this.hasPortalCooldown()) {
         --this.portalCooldown;
      }

   }

   public void igniteByLava() {
      if (!this.isFireImmune()) {
         this.setOnFireFor(15.0F);
      }
   }

   public void setOnFireFromLava() {
      if (!this.isFireImmune()) {
         World var2 = this.getWorld();
         if (var2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var2;
            if (this.damage(serverWorld, this.getDamageSources().lava(), 4.0F) && this.shouldPlayBurnSoundInLava() && !this.isSilent()) {
               serverWorld.playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_BURN, this.getSoundCategory(), 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }
         }

      }
   }

   protected boolean shouldPlayBurnSoundInLava() {
      return true;
   }

   public final void setOnFireFor(float seconds) {
      this.setOnFireForTicks(MathHelper.floor(seconds * 20.0F));
   }

   public void setOnFireForTicks(int ticks) {
      if (this.fireTicks < ticks) {
         this.setFireTicks(ticks);
      }

      this.defrost();
   }

   public void setFireTicks(int fireTicks) {
      this.fireTicks = fireTicks;
   }

   public int getFireTicks() {
      return this.fireTicks;
   }

   public void extinguish() {
      this.setFireTicks(0);
   }

   protected void tickInVoid() {
      this.discard();
   }

   public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
      return this.doesNotCollide(this.getBoundingBox().offset(offsetX, offsetY, offsetZ));
   }

   private boolean doesNotCollide(Box box) {
      return this.getWorld().isSpaceEmpty(this, box) && !this.getWorld().containsFluid(box);
   }

   public void setOnGround(boolean onGround) {
      this.onGround = onGround;
      this.updateSupportingBlockPos(onGround, (Vec3d)null);
   }

   public void setMovement(boolean onGround, Vec3d movement) {
      this.setMovement(onGround, this.horizontalCollision, movement);
   }

   public void setMovement(boolean onGround, boolean horizontalCollision, Vec3d movement) {
      this.onGround = onGround;
      this.horizontalCollision = horizontalCollision;
      this.updateSupportingBlockPos(onGround, movement);
   }

   public boolean isSupportedBy(BlockPos pos) {
      return this.supportingBlockPos.isPresent() && ((BlockPos)this.supportingBlockPos.get()).equals(pos);
   }

   protected void updateSupportingBlockPos(boolean onGround, @Nullable Vec3d movement) {
      if (onGround) {
         Box box = this.getBoundingBox();
         Box box2 = new Box(box.minX, box.minY - 1.0E-6, box.minZ, box.maxX, box.minY, box.maxZ);
         Optional optional = this.world.findSupportingBlockPos(this, box2);
         if (!optional.isPresent() && !this.forceUpdateSupportingBlockPos) {
            if (movement != null) {
               Box box3 = box2.offset(-movement.x, 0.0, -movement.z);
               optional = this.world.findSupportingBlockPos(this, box3);
               this.supportingBlockPos = optional;
            }
         } else {
            this.supportingBlockPos = optional;
         }

         this.forceUpdateSupportingBlockPos = optional.isEmpty();
      } else {
         this.forceUpdateSupportingBlockPos = false;
         if (this.supportingBlockPos.isPresent()) {
            this.supportingBlockPos = Optional.empty();
         }
      }

   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void move(MovementType type, Vec3d movement) {
      if (this.noClip) {
         this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
      } else {
         if (type == MovementType.PISTON) {
            movement = this.adjustMovementForPiston(movement);
            if (movement.equals(Vec3d.ZERO)) {
               return;
            }
         }

         Profiler profiler = Profilers.get();
         profiler.push("move");
         if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
            movement = movement.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
            this.setVelocity(Vec3d.ZERO);
         }

         movement = this.adjustMovementForSneaking(movement, type);
         Vec3d vec3d = this.adjustMovementForCollisions(movement);
         double d = vec3d.lengthSquared();
         if (d > 1.0E-7 || movement.lengthSquared() - d < 1.0E-7) {
            if (this.fallDistance != 0.0 && d >= 1.0) {
               BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(this.getPos(), this.getPos().add(vec3d), RaycastContext.ShapeType.FALLDAMAGE_RESETTING, RaycastContext.FluidHandling.WATER, this));
               if (blockHitResult.getType() != HitResult.Type.MISS) {
                  this.onLanding();
               }
            }

            Vec3d vec3d2 = this.getPos();
            Vec3d vec3d3 = vec3d2.add(vec3d);
            this.addQueuedCollisionChecks(new QueuedCollisionCheck(vec3d2, vec3d3, true));
            this.setPosition(vec3d3);
         }

         profiler.pop();
         profiler.push("rest");
         boolean bl = !MathHelper.approximatelyEquals(movement.x, vec3d.x);
         boolean bl2 = !MathHelper.approximatelyEquals(movement.z, vec3d.z);
         this.horizontalCollision = bl || bl2;
         if (Math.abs(movement.y) > 0.0 || this.isLogicalSideForUpdatingMovement()) {
            this.verticalCollision = movement.y != vec3d.y;
            this.groundCollision = this.verticalCollision && movement.y < 0.0;
            this.setMovement(this.groundCollision, this.horizontalCollision, vec3d);
         }

         if (this.horizontalCollision) {
            this.collidedSoftly = this.hasCollidedSoftly(vec3d);
         } else {
            this.collidedSoftly = false;
         }

         BlockPos blockPos = this.getLandingPos();
         BlockState blockState = this.getWorld().getBlockState(blockPos);
         if (this.isLogicalSideForUpdatingMovement()) {
            this.fall(vec3d.y, this.isOnGround(), blockState, blockPos);
         }

         if (this.isRemoved()) {
            profiler.pop();
         } else {
            if (this.horizontalCollision) {
               Vec3d vec3d4 = this.getVelocity();
               this.setVelocity(bl ? 0.0 : vec3d4.x, vec3d4.y, bl2 ? 0.0 : vec3d4.z);
            }

            if (this.canMoveVoluntarily()) {
               Block block = blockState.getBlock();
               if (movement.y != vec3d.y) {
                  block.onEntityLand(this.getWorld(), this);
               }
            }

            if (!this.getWorld().isClient() || this.isLogicalSideForUpdatingMovement()) {
               MoveEffect moveEffect = this.getMoveEffect();
               if (moveEffect.hasAny() && !this.hasVehicle()) {
                  this.applyMoveEffect(moveEffect, vec3d, blockPos, blockState);
               }
            }

            float f = this.getVelocityMultiplier();
            this.setVelocity(this.getVelocity().multiply((double)f, 1.0, (double)f));
            profiler.pop();
         }
      }
   }

   private void applyMoveEffect(MoveEffect moveEffect, Vec3d movement, BlockPos landingPos, BlockState landingState) {
      float f = 0.6F;
      float g = (float)(movement.length() * 0.6000000238418579);
      float h = (float)(movement.horizontalLength() * 0.6000000238418579);
      BlockPos blockPos = this.getSteppingPos();
      BlockState blockState = this.getWorld().getBlockState(blockPos);
      boolean bl = this.canClimb(blockState);
      this.distanceTraveled += bl ? g : h;
      this.speed += g;
      if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
         boolean bl2 = blockPos.equals(landingPos);
         boolean bl3 = this.stepOnBlock(landingPos, landingState, moveEffect.playsSounds(), bl2, movement);
         if (!bl2) {
            bl3 |= this.stepOnBlock(blockPos, blockState, false, moveEffect.emitsGameEvents(), movement);
         }

         if (bl3) {
            this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
         } else if (this.isTouchingWater()) {
            this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
            if (moveEffect.playsSounds()) {
               this.playSwimSound();
            }

            if (moveEffect.emitsGameEvents()) {
               this.emitGameEvent(GameEvent.SWIM);
            }
         }
      } else if (blockState.isAir()) {
         this.addAirTravelEffects();
      }

   }

   protected void tickBlockCollision() {
      this.currentlyCheckedCollisions.clear();
      this.currentlyCheckedCollisions.addAll(this.queuedCollisionChecks);
      this.queuedCollisionChecks.clear();
      if (this.currentlyCheckedCollisions.isEmpty()) {
         this.currentlyCheckedCollisions.add(new QueuedCollisionCheck(this.getLastRenderPos(), this.getPos(), false));
      } else if (((QueuedCollisionCheck)this.currentlyCheckedCollisions.getLast()).to.squaredDistanceTo(this.getPos()) > 9.999999439624929E-11) {
         this.currentlyCheckedCollisions.add(new QueuedCollisionCheck(((QueuedCollisionCheck)this.currentlyCheckedCollisions.getLast()).to, this.getPos(), false));
      }

      this.tickBlockCollisions(this.currentlyCheckedCollisions);
   }

   private void addQueuedCollisionChecks(QueuedCollisionCheck queuedCollisionCheck) {
      if (this.queuedCollisionChecks.size() >= 100) {
         QueuedCollisionCheck queuedCollisionCheck2 = (QueuedCollisionCheck)this.queuedCollisionChecks.removeFirst();
         QueuedCollisionCheck queuedCollisionCheck3 = (QueuedCollisionCheck)this.queuedCollisionChecks.removeFirst();
         QueuedCollisionCheck queuedCollisionCheck4 = new QueuedCollisionCheck(queuedCollisionCheck2.from(), queuedCollisionCheck3.to(), false);
         this.queuedCollisionChecks.addFirst(queuedCollisionCheck4);
      }

      this.queuedCollisionChecks.add(queuedCollisionCheck);
   }

   public void popQueuedCollisionCheck() {
      if (!this.queuedCollisionChecks.isEmpty()) {
         this.queuedCollisionChecks.removeLast();
      }

   }

   protected void clearQueuedCollisionChecks() {
      this.queuedCollisionChecks.clear();
   }

   public void tickBlockCollision(Vec3d lastRenderPos, Vec3d pos) {
      this.tickBlockCollisions(List.of(new QueuedCollisionCheck(lastRenderPos, pos, false)));
   }

   private void tickBlockCollisions(List checks) {
      if (this.shouldTickBlockCollision()) {
         if (this.isOnGround()) {
            BlockPos blockPos = this.getLandingPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            blockState.getBlock().onSteppedOn(this.getWorld(), blockPos, blockState, this);
         }

         boolean bl = this.isOnFire();
         boolean bl2 = this.shouldEscapePowderSnow();
         int i = this.getFireTicks();
         this.checkBlockCollisions(checks, this.collisionHandler);
         this.collisionHandler.runCallbacks(this);
         if (this.isBeingRainedOn()) {
            this.extinguish();
         }

         if (bl && !this.isOnFire() || bl2 && !this.shouldEscapePowderSnow()) {
            this.playExtinguishSound();
         }

         boolean bl3 = this.getFireTicks() > i;
         if (!this.world.isClient && !this.isOnFire() && !bl3) {
            this.setFireTicks(-this.getBurningDuration());
         }

      }
   }

   protected boolean shouldTickBlockCollision() {
      return !this.isRemoved() && !this.noClip;
   }

   private boolean canClimb(BlockState state) {
      return state.isIn(BlockTags.CLIMBABLE) || state.isOf(Blocks.POWDER_SNOW);
   }

   private boolean stepOnBlock(BlockPos pos, BlockState state, boolean playSound, boolean emitEvent, Vec3d movement) {
      if (state.isAir()) {
         return false;
      } else {
         boolean bl = this.canClimb(state);
         if ((this.isOnGround() || bl || this.isInSneakingPose() && movement.y == 0.0 || this.isOnRail()) && !this.isSwimming()) {
            if (playSound) {
               this.playStepSounds(pos, state);
            }

            if (emitEvent) {
               this.getWorld().emitGameEvent(GameEvent.STEP, this.getPos(), GameEvent.Emitter.of(this, state));
            }

            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean hasCollidedSoftly(Vec3d adjustedMovement) {
      return false;
   }

   protected void playExtinguishSound() {
      if (!this.world.isClient()) {
         this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, this.getSoundCategory(), 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }

   }

   public void extinguishWithSound() {
      if (this.isOnFire()) {
         this.playExtinguishSound();
      }

      this.extinguish();
   }

   protected void addAirTravelEffects() {
      if (this.isFlappingWings()) {
         this.addFlapEffects();
         if (this.getMoveEffect().emitsGameEvents()) {
            this.emitGameEvent(GameEvent.FLAP);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public BlockPos getLandingPos() {
      return this.getPosWithYOffset(0.2F);
   }

   public BlockPos getVelocityAffectingPos() {
      return this.getPosWithYOffset(0.500001F);
   }

   public BlockPos getSteppingPos() {
      return this.getPosWithYOffset(1.0E-5F);
   }

   protected BlockPos getPosWithYOffset(float offset) {
      if (this.supportingBlockPos.isPresent()) {
         BlockPos blockPos = (BlockPos)this.supportingBlockPos.get();
         if (!(offset > 1.0E-5F)) {
            return blockPos;
         } else {
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            return (!((double)offset <= 0.5) || !blockState.isIn(BlockTags.FENCES)) && !blockState.isIn(BlockTags.WALLS) && !(blockState.getBlock() instanceof FenceGateBlock) ? blockPos.withY(MathHelper.floor(this.pos.y - (double)offset)) : blockPos;
         }
      } else {
         int i = MathHelper.floor(this.pos.x);
         int j = MathHelper.floor(this.pos.y - (double)offset);
         int k = MathHelper.floor(this.pos.z);
         return new BlockPos(i, j, k);
      }
   }

   protected float getJumpVelocityMultiplier() {
      float f = this.getWorld().getBlockState(this.getBlockPos()).getBlock().getJumpVelocityMultiplier();
      float g = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
      return (double)f == 1.0 ? g : f;
   }

   protected float getVelocityMultiplier() {
      BlockState blockState = this.getWorld().getBlockState(this.getBlockPos());
      float f = blockState.getBlock().getVelocityMultiplier();
      if (!blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.BUBBLE_COLUMN)) {
         return (double)f == 1.0 ? this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getVelocityMultiplier() : f;
      } else {
         return f;
      }
   }

   protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
      return movement;
   }

   protected Vec3d adjustMovementForPiston(Vec3d movement) {
      if (movement.lengthSquared() <= 1.0E-7) {
         return movement;
      } else {
         long l = this.getWorld().getTime();
         if (l != this.pistonMovementTick) {
            Arrays.fill(this.pistonMovementDelta, 0.0);
            this.pistonMovementTick = l;
         }

         double d;
         if (movement.x != 0.0) {
            d = this.calculatePistonMovementFactor(Direction.Axis.X, movement.x);
            return Math.abs(d) <= 9.999999747378752E-6 ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
         } else if (movement.y != 0.0) {
            d = this.calculatePistonMovementFactor(Direction.Axis.Y, movement.y);
            return Math.abs(d) <= 9.999999747378752E-6 ? Vec3d.ZERO : new Vec3d(0.0, d, 0.0);
         } else if (movement.z != 0.0) {
            d = this.calculatePistonMovementFactor(Direction.Axis.Z, movement.z);
            return Math.abs(d) <= 9.999999747378752E-6 ? Vec3d.ZERO : new Vec3d(0.0, 0.0, d);
         } else {
            return Vec3d.ZERO;
         }
      }
   }

   private double calculatePistonMovementFactor(Direction.Axis axis, double offsetFactor) {
      int i = axis.ordinal();
      double d = MathHelper.clamp(offsetFactor + this.pistonMovementDelta[i], -0.51, 0.51);
      offsetFactor = d - this.pistonMovementDelta[i];
      this.pistonMovementDelta[i] = d;
      return offsetFactor;
   }

   private Vec3d adjustMovementForCollisions(Vec3d movement) {
      Box box = this.getBoundingBox();
      List list = this.getWorld().getEntityCollisions(this, box.stretch(movement));
      Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(this, movement, box, this.getWorld(), list);
      boolean bl = movement.x != vec3d.x;
      boolean bl2 = movement.y != vec3d.y;
      boolean bl3 = movement.z != vec3d.z;
      boolean bl4 = bl2 && movement.y < 0.0;
      if (this.getStepHeight() > 0.0F && (bl4 || this.isOnGround()) && (bl || bl3)) {
         Box box2 = bl4 ? box.offset(0.0, vec3d.y, 0.0) : box;
         Box box3 = box2.stretch(movement.x, (double)this.getStepHeight(), movement.z);
         if (!bl4) {
            box3 = box3.stretch(0.0, -9.999999747378752E-6, 0.0);
         }

         List list2 = findCollisionsForMovement(this, this.world, list, box3);
         float f = (float)vec3d.y;
         float[] fs = collectStepHeights(box2, list2, this.getStepHeight(), f);
         float[] var14 = fs;
         int var15 = fs.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            float g = var14[var16];
            Vec3d vec3d2 = adjustMovementForCollisions(new Vec3d(movement.x, (double)g, movement.z), box2, list2);
            if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
               double d = box.minY - box2.minY;
               return vec3d2.subtract(0.0, d, 0.0);
            }
         }
      }

      return vec3d;
   }

   private static float[] collectStepHeights(Box collisionBox, List collisions, float f, float stepHeight) {
      FloatSet floatSet = new FloatArraySet(4);
      Iterator var5 = collisions.iterator();

      while(var5.hasNext()) {
         VoxelShape voxelShape = (VoxelShape)var5.next();
         DoubleList doubleList = voxelShape.getPointPositions(Direction.Axis.Y);
         DoubleListIterator var8 = doubleList.iterator();

         while(var8.hasNext()) {
            double d = (Double)var8.next();
            float g = (float)(d - collisionBox.minY);
            if (!(g < 0.0F) && g != stepHeight) {
               if (g > f) {
                  break;
               }

               floatSet.add(g);
            }
         }
      }

      float[] fs = floatSet.toFloatArray();
      FloatArrays.unstableSort(fs);
      return fs;
   }

   public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List collisions) {
      List list = findCollisionsForMovement(entity, world, collisions, entityBoundingBox.stretch(movement));
      return adjustMovementForCollisions(movement, entityBoundingBox, list);
   }

   private static List findCollisionsForMovement(@Nullable Entity entity, World world, List regularCollisions, Box movingEntityBoundingBox) {
      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(regularCollisions.size() + 1);
      if (!regularCollisions.isEmpty()) {
         builder.addAll(regularCollisions);
      }

      WorldBorder worldBorder = world.getWorldBorder();
      boolean bl = entity != null && worldBorder.canCollide(entity, movingEntityBoundingBox);
      if (bl) {
         builder.add(worldBorder.asVoxelShape());
      }

      builder.addAll(world.getBlockCollisions(entity, movingEntityBoundingBox));
      return builder.build();
   }

   private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List collisions) {
      if (collisions.isEmpty()) {
         return movement;
      } else {
         Vec3d vec3d = Vec3d.ZERO;
         Iterator var4 = getAxisCheckOrder(movement).iterator();

         while(var4.hasNext()) {
            Direction.Axis axis = (Direction.Axis)var4.next();
            double d = movement.getComponentAlongAxis(axis);
            if (d != 0.0) {
               double e = VoxelShapes.calculateMaxOffset(axis, entityBoundingBox.offset(vec3d), collisions, d);
               vec3d = vec3d.withAxis(axis, e);
            }
         }

         return vec3d;
      }
   }

   private static Iterable getAxisCheckOrder(Vec3d movement) {
      return Math.abs(movement.x) < Math.abs(movement.z) ? Z_THEN_X : X_THEN_Z;
   }

   protected float calculateNextStepSoundDistance() {
      return (float)((int)this.distanceTraveled + 1);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_GENERIC_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   protected SoundEvent getHighSpeedSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   private void checkBlockCollisions(List queuedCollisionChecks, EntityCollisionHandler.Impl collisionHandler) {
      if (this.shouldTickBlockCollision()) {
         LongSet longSet = this.collidedBlockPositions;
         Iterator var4 = queuedCollisionChecks.iterator();

         while(true) {
            while(var4.hasNext()) {
               QueuedCollisionCheck queuedCollisionCheck = (QueuedCollisionCheck)var4.next();
               Vec3d vec3d = queuedCollisionCheck.from;
               Vec3d vec3d2 = queuedCollisionCheck.to().subtract(queuedCollisionCheck.from());
               if (queuedCollisionCheck.axisIndependant && vec3d2.lengthSquared() > 0.0) {
                  Iterator var8 = getAxisCheckOrder(vec3d2).iterator();

                  while(var8.hasNext()) {
                     Direction.Axis axis = (Direction.Axis)var8.next();
                     double d = vec3d2.getComponentAlongAxis(axis);
                     if (d != 0.0) {
                        Vec3d vec3d3 = vec3d.offset(axis.getPositiveDirection(), d);
                        this.checkBlockCollision(vec3d, vec3d3, collisionHandler, longSet);
                        vec3d = vec3d3;
                     }
                  }
               } else {
                  this.checkBlockCollision(queuedCollisionCheck.from(), queuedCollisionCheck.to(), collisionHandler, longSet);
               }
            }

            longSet.clear();
            return;
         }
      }
   }

   private void checkBlockCollision(Vec3d from, Vec3d to, EntityCollisionHandler.Impl collisionHandler, LongSet collidedBlockPositions) {
      Box box = this.calculateDefaultBoundingBox(to).contract(9.999999747378752E-6);
      BlockView.collectCollisionsBetween(from, to, box, (pos, version) -> {
         if (!this.isAlive()) {
            return false;
         } else {
            BlockState blockState = this.getWorld().getBlockState(pos);
            if (blockState.isAir()) {
               this.afterCollisionCheck(pos, false, false);
               return true;
            } else if (!collidedBlockPositions.add(pos.asLong())) {
               return true;
            } else {
               VoxelShape voxelShape = blockState.getInsideCollisionShape(this.getWorld(), pos, this);
               boolean bl = voxelShape == VoxelShapes.fullCube() || this.collides(from, to, voxelShape.offset(new Vec3d(pos)).getBoundingBoxes());
               if (bl) {
                  try {
                     collisionHandler.updateIfNecessary(version);
                     blockState.onEntityCollision(this.getWorld(), pos, this, collisionHandler);
                     this.onBlockCollision(blockState);
                  } catch (Throwable var14) {
                     CrashReport crashReport = CrashReport.create(var14, "Colliding entity with block");
                     CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
                     CrashReportSection.addBlockInfo(crashReportSection, this.getWorld(), pos, blockState);
                     CrashReportSection crashReportSection2 = crashReport.addElement("Entity being checked for collision");
                     this.populateCrashReport(crashReportSection2);
                     throw new CrashException(crashReport);
                  }
               }

               boolean bl2 = this.collidesWithFluid(blockState.getFluidState(), pos, from, to);
               if (bl2) {
                  collisionHandler.updateIfNecessary(version);
                  blockState.getFluidState().onEntityCollision(this.getWorld(), pos, this, collisionHandler);
               }

               this.afterCollisionCheck(pos, bl, bl2);
               return true;
            }
         }
      });
   }

   private void afterCollisionCheck(BlockPos pos, boolean blockCollision, boolean fluidCollision) {
   }

   public boolean collidesWithFluid(FluidState state, BlockPos fluidPos, Vec3d oldPos, Vec3d newPos) {
      Box box = state.getCollisionBox(this.getWorld(), fluidPos);
      return box != null && this.collides(oldPos, newPos, List.of(box));
   }

   public boolean collides(Vec3d oldPos, Vec3d newPos, List boxes) {
      Box box = this.calculateDefaultBoundingBox(oldPos);
      Vec3d vec3d = newPos.subtract(oldPos);
      return box.collides(vec3d, boxes);
   }

   protected void onBlockCollision(BlockState state) {
   }

   public BlockPos getWorldSpawnPos(ServerWorld world, BlockPos basePos) {
      BlockPos blockPos = world.getSpawnPos();
      Vec3d vec3d = blockPos.toCenterPos();
      int i = world.getWorldChunk(blockPos).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ()) + 1;
      return BlockPos.ofFloored(vec3d.x, (double)i, vec3d.z);
   }

   public void emitGameEvent(RegistryEntry event, @Nullable Entity entity) {
      this.getWorld().emitGameEvent(entity, event, this.pos);
   }

   public void emitGameEvent(RegistryEntry event) {
      this.emitGameEvent(event, this);
   }

   private void playStepSounds(BlockPos pos, BlockState state) {
      this.playStepSound(pos, state);
      if (this.shouldPlayAmethystChimeSound(state)) {
         this.playAmethystChimeSound();
      }

   }

   protected void playSwimSound() {
      Entity entity = (Entity)Objects.requireNonNullElse(this.getControllingPassenger(), this);
      float f = entity == this ? 0.35F : 0.4F;
      Vec3d vec3d = entity.getVelocity();
      float g = Math.min(1.0F, (float)Math.sqrt(vec3d.x * vec3d.x * 0.20000000298023224 + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.20000000298023224) * f);
      this.playSwimSound(g);
   }

   protected BlockPos getStepSoundPos(BlockPos pos) {
      BlockPos blockPos = pos.up();
      BlockState blockState = this.getWorld().getBlockState(blockPos);
      return !blockState.isIn(BlockTags.INSIDE_STEP_SOUND_BLOCKS) && !blockState.isIn(BlockTags.COMBINATION_STEP_SOUND_BLOCKS) ? pos : blockPos;
   }

   protected void playCombinationStepSounds(BlockState primaryState, BlockState secondaryState) {
      BlockSoundGroup blockSoundGroup = primaryState.getSoundGroup();
      this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
      this.playSecondaryStepSound(secondaryState);
   }

   protected void playSecondaryStepSound(BlockState state) {
      BlockSoundGroup blockSoundGroup = state.getSoundGroup();
      this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.05F, blockSoundGroup.getPitch() * 0.8F);
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      BlockSoundGroup blockSoundGroup = state.getSoundGroup();
      this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
   }

   private boolean shouldPlayAmethystChimeSound(BlockState state) {
      return state.isIn(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.age >= this.lastChimeAge + 20;
   }

   private void playAmethystChimeSound() {
      this.lastChimeIntensity *= (float)Math.pow(0.997, (double)(this.age - this.lastChimeAge));
      this.lastChimeIntensity = Math.min(1.0F, this.lastChimeIntensity + 0.07F);
      float f = 0.5F + this.lastChimeIntensity * this.random.nextFloat() * 1.2F;
      float g = 0.1F + this.lastChimeIntensity * 1.2F;
      this.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, g, f);
      this.lastChimeAge = this.age;
   }

   protected void playSwimSound(float volume) {
      this.playSound(this.getSwimSound(), volume, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
   }

   protected void addFlapEffects() {
   }

   protected boolean isFlappingWings() {
      return false;
   }

   public void playSound(SoundEvent sound, float volume, float pitch) {
      if (!this.isSilent()) {
         this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)sound, this.getSoundCategory(), volume, pitch);
      }

   }

   public void playSoundIfNotSilent(SoundEvent event) {
      if (!this.isSilent()) {
         this.playSound(event, 1.0F, 1.0F);
      }

   }

   public boolean isSilent() {
      return (Boolean)this.dataTracker.get(SILENT);
   }

   public void setSilent(boolean silent) {
      this.dataTracker.set(SILENT, silent);
   }

   public boolean hasNoGravity() {
      return (Boolean)this.dataTracker.get(NO_GRAVITY);
   }

   public void setNoGravity(boolean noGravity) {
      this.dataTracker.set(NO_GRAVITY, noGravity);
   }

   protected double getGravity() {
      return 0.0;
   }

   public final double getFinalGravity() {
      return this.hasNoGravity() ? 0.0 : this.getGravity();
   }

   protected void applyGravity() {
      double d = this.getFinalGravity();
      if (d != 0.0) {
         this.setVelocity(this.getVelocity().add(0.0, -d, 0.0));
      }

   }

   protected MoveEffect getMoveEffect() {
      return Entity.MoveEffect.ALL;
   }

   public boolean occludeVibrationSignals() {
      return false;
   }

   public final void handleFall(double xDifference, double yDifference, double zDifference, boolean onGround) {
      if (!this.isRegionUnloaded()) {
         this.updateSupportingBlockPos(onGround, new Vec3d(xDifference, yDifference, zDifference));
         BlockPos blockPos = this.getLandingPos();
         BlockState blockState = this.getWorld().getBlockState(blockPos);
         this.fall(yDifference, onGround, blockState, blockPos);
      }
   }

   protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
      if (!this.isTouchingWater() && heightDifference < 0.0) {
         this.fallDistance -= (double)((float)heightDifference);
      }

      if (onGround) {
         if (this.fallDistance > 0.0) {
            state.getBlock().onLandedUpon(this.getWorld(), state, landedPosition, this, this.fallDistance);
            this.getWorld().emitGameEvent(GameEvent.HIT_GROUND, this.pos, GameEvent.Emitter.of(this, (BlockState)this.supportingBlockPos.map((pos) -> {
               return this.getWorld().getBlockState(pos);
            }).orElse(state)));
         }

         this.onLanding();
      }

   }

   public boolean isFireImmune() {
      return this.getType().isFireImmune();
   }

   public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
      if (this.type.isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
         return false;
      } else {
         this.handleFallDamageForPassengers(fallDistance, damagePerDistance, damageSource);
         return false;
      }
   }

   protected void handleFallDamageForPassengers(double fallDistance, float damagePerDistance, DamageSource damageSource) {
      if (this.hasPassengers()) {
         Iterator var5 = this.getPassengerList().iterator();

         while(var5.hasNext()) {
            Entity entity = (Entity)var5.next();
            entity.handleFallDamage(fallDistance, damagePerDistance, damageSource);
         }
      }

   }

   public boolean isTouchingWater() {
      return this.touchingWater;
   }

   boolean isBeingRainedOn() {
      BlockPos blockPos = this.getBlockPos();
      return this.getWorld().hasRain(blockPos) || this.getWorld().hasRain(BlockPos.ofFloored((double)blockPos.getX(), this.getBoundingBox().maxY, (double)blockPos.getZ()));
   }

   public boolean isTouchingWaterOrRain() {
      return this.isTouchingWater() || this.isBeingRainedOn();
   }

   public boolean isInFluid() {
      return this.isTouchingWater() || this.isInLava();
   }

   public boolean isSubmergedInWater() {
      return this.submergedInWater && this.isTouchingWater();
   }

   public boolean isAtCloudHeight() {
      Optional optional = this.world.getDimension().cloudHeight();
      if (optional.isEmpty()) {
         return false;
      } else {
         int i = (Integer)optional.get();
         if (this.getY() + (double)this.getHeight() < (double)i) {
            return false;
         } else {
            int j = i + 4;
            return this.getY() <= (double)j;
         }
      }
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isTouchingWater() && !this.hasVehicle());
      } else {
         this.setSwimming(this.isSprinting() && this.isSubmergedInWater() && !this.hasVehicle() && this.getWorld().getFluidState(this.blockPos).isIn(FluidTags.WATER));
      }

   }

   protected boolean updateWaterState() {
      this.fluidHeight.clear();
      this.checkWaterState();
      double d = this.getWorld().getDimension().ultrawarm() ? 0.007 : 0.0023333333333333335;
      boolean bl = this.updateMovementInFluid(FluidTags.LAVA, d);
      return this.isTouchingWater() || bl;
   }

   void checkWaterState() {
      Entity var2 = this.getVehicle();
      if (var2 instanceof AbstractBoatEntity abstractBoatEntity) {
         if (!abstractBoatEntity.isSubmergedInWater()) {
            this.touchingWater = false;
            return;
         }
      }

      if (this.updateMovementInFluid(FluidTags.WATER, 0.014)) {
         if (!this.touchingWater && !this.firstUpdate) {
            this.onSwimmingStart();
         }

         this.onLanding();
         this.touchingWater = true;
      } else {
         this.touchingWater = false;
      }

   }

   private void updateSubmergedInWaterState() {
      this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
      this.submergedFluidTag.clear();
      double d = this.getEyeY();
      Entity entity = this.getVehicle();
      if (entity instanceof AbstractBoatEntity abstractBoatEntity) {
         if (!abstractBoatEntity.isSubmergedInWater() && abstractBoatEntity.getBoundingBox().maxY >= d && abstractBoatEntity.getBoundingBox().minY <= d) {
            return;
         }
      }

      BlockPos blockPos = BlockPos.ofFloored(this.getX(), d, this.getZ());
      FluidState fluidState = this.getWorld().getFluidState(blockPos);
      double e = (double)((float)blockPos.getY() + fluidState.getHeight(this.getWorld(), blockPos));
      if (e > d) {
         Stream var10000 = fluidState.streamTags();
         Set var10001 = this.submergedFluidTag;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
      }

   }

   protected void onSwimmingStart() {
      Entity entity = (Entity)Objects.requireNonNullElse(this.getControllingPassenger(), this);
      float f = entity == this ? 0.2F : 0.9F;
      Vec3d vec3d = entity.getVelocity();
      float g = Math.min(1.0F, (float)Math.sqrt(vec3d.x * vec3d.x * 0.20000000298023224 + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.20000000298023224) * f);
      if (g < 0.25F) {
         this.playSound(this.getSplashSound(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getHighSpeedSplashSound(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }

      float h = (float)MathHelper.floor(this.getY());

      int i;
      double d;
      double e;
      for(i = 0; (float)i < 1.0F + this.dimensions.width() * 20.0F; ++i) {
         d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         e = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         this.getWorld().addParticleClient(ParticleTypes.BUBBLE, this.getX() + d, (double)(h + 1.0F), this.getZ() + e, vec3d.x, vec3d.y - this.random.nextDouble() * 0.20000000298023224, vec3d.z);
      }

      for(i = 0; (float)i < 1.0F + this.dimensions.width() * 20.0F; ++i) {
         d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         e = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
         this.getWorld().addParticleClient(ParticleTypes.SPLASH, this.getX() + d, (double)(h + 1.0F), this.getZ() + e, vec3d.x, vec3d.y, vec3d.z);
      }

      this.emitGameEvent(GameEvent.SPLASH);
   }

   /** @deprecated */
   @Deprecated
   protected BlockState getLandingBlockState() {
      return this.getWorld().getBlockState(this.getLandingPos());
   }

   public BlockState getSteppingBlockState() {
      return this.getWorld().getBlockState(this.getSteppingPos());
   }

   public boolean shouldSpawnSprintingParticles() {
      return this.isSprinting() && !this.isTouchingWater() && !this.isSpectator() && !this.isInSneakingPose() && !this.isInLava() && this.isAlive();
   }

   protected void spawnSprintingParticles() {
      BlockPos blockPos = this.getLandingPos();
      BlockState blockState = this.getWorld().getBlockState(blockPos);
      if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
         Vec3d vec3d = this.getVelocity();
         BlockPos blockPos2 = this.getBlockPos();
         double d = this.getX() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width();
         double e = this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width();
         if (blockPos2.getX() != blockPos.getX()) {
            d = MathHelper.clamp(d, (double)blockPos.getX(), (double)blockPos.getX() + 1.0);
         }

         if (blockPos2.getZ() != blockPos.getZ()) {
            e = MathHelper.clamp(e, (double)blockPos.getZ(), (double)blockPos.getZ() + 1.0);
         }

         this.getWorld().addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, this.getY() + 0.1, e, vec3d.x * -4.0, 1.5, vec3d.z * -4.0);
      }

   }

   public boolean isSubmergedIn(TagKey fluidTag) {
      return this.submergedFluidTag.contains(fluidTag);
   }

   public boolean isInLava() {
      return !this.firstUpdate && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
   }

   public void updateVelocity(float speed, Vec3d movementInput) {
      Vec3d vec3d = movementInputToVelocity(movementInput, speed, this.getYaw());
      this.setVelocity(this.getVelocity().add(vec3d));
   }

   protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
      double d = movementInput.lengthSquared();
      if (d < 1.0E-7) {
         return Vec3d.ZERO;
      } else {
         Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
         float f = MathHelper.sin(yaw * 0.017453292F);
         float g = MathHelper.cos(yaw * 0.017453292F);
         return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
      }
   }

   /** @deprecated */
   @Deprecated
   public float getBrightnessAtEyes() {
      return this.getWorld().isPosLoaded(this.getBlockX(), this.getBlockZ()) ? this.getWorld().getBrightness(BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ())) : 0.0F;
   }

   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.updatePosition(x, y, z);
      this.setAngles(yaw, pitch);
   }

   public void setAngles(float yaw, float pitch) {
      this.setYaw(yaw % 360.0F);
      this.setPitch(MathHelper.clamp(pitch, -90.0F, 90.0F) % 360.0F);
      this.lastYaw = this.getYaw();
      this.lastPitch = this.getPitch();
   }

   public void updatePosition(double x, double y, double z) {
      double d = MathHelper.clamp(x, -3.0E7, 3.0E7);
      double e = MathHelper.clamp(z, -3.0E7, 3.0E7);
      this.lastX = d;
      this.lastY = y;
      this.lastZ = e;
      this.setPosition(d, y, e);
   }

   public void refreshPositionAfterTeleport(Vec3d pos) {
      this.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
   }

   public void refreshPositionAfterTeleport(double x, double y, double z) {
      this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
   }

   public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
      this.refreshPositionAndAngles(pos.toBottomCenterPos(), yaw, pitch);
   }

   public void refreshPositionAndAngles(Vec3d pos, float yaw, float pitch) {
      this.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch);
   }

   public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.setPos(x, y, z);
      this.setYaw(yaw);
      this.setPitch(pitch);
      this.resetPosition();
      this.refreshPosition();
   }

   public final void resetPosition() {
      this.updateLastPosition();
      this.updateLastAngles();
   }

   public final void setLastPositionAndAngles(Vec3d pos, float yaw, float pitch) {
      this.setLastPosition(pos);
      this.setLastAngles(yaw, pitch);
   }

   protected void updateLastPosition() {
      this.setLastPosition(this.pos);
   }

   public void updateLastAngles() {
      this.setLastAngles(this.getYaw(), this.getPitch());
   }

   private void setLastPosition(Vec3d pos) {
      this.lastX = this.lastRenderX = pos.x;
      this.lastY = this.lastRenderY = pos.y;
      this.lastZ = this.lastRenderZ = pos.z;
   }

   private void setLastAngles(float lastYaw, float lastPitch) {
      this.lastYaw = lastYaw;
      this.lastPitch = lastPitch;
   }

   public final Vec3d getLastRenderPos() {
      return new Vec3d(this.lastRenderX, this.lastRenderY, this.lastRenderZ);
   }

   public float distanceTo(Entity entity) {
      float f = (float)(this.getX() - entity.getX());
      float g = (float)(this.getY() - entity.getY());
      float h = (float)(this.getZ() - entity.getZ());
      return MathHelper.sqrt(f * f + g * g + h * h);
   }

   public double squaredDistanceTo(double x, double y, double z) {
      double d = this.getX() - x;
      double e = this.getY() - y;
      double f = this.getZ() - z;
      return d * d + e * e + f * f;
   }

   public double squaredDistanceTo(Entity entity) {
      return this.squaredDistanceTo(entity.getPos());
   }

   public double squaredDistanceTo(Vec3d vector) {
      double d = this.getX() - vector.x;
      double e = this.getY() - vector.y;
      double f = this.getZ() - vector.z;
      return d * d + e * e + f * f;
   }

   public void onPlayerCollision(PlayerEntity player) {
   }

   public void pushAwayFrom(Entity entity) {
      if (!this.isConnectedThroughVehicle(entity)) {
         if (!entity.noClip && !this.noClip) {
            double d = entity.getX() - this.getX();
            double e = entity.getZ() - this.getZ();
            double f = MathHelper.absMax(d, e);
            if (f >= 0.009999999776482582) {
               f = Math.sqrt(f);
               d /= f;
               e /= f;
               double g = 1.0 / f;
               if (g > 1.0) {
                  g = 1.0;
               }

               d *= g;
               e *= g;
               d *= 0.05000000074505806;
               e *= 0.05000000074505806;
               if (!this.hasPassengers() && this.isPushable()) {
                  this.addVelocity(-d, 0.0, -e);
               }

               if (!entity.hasPassengers() && entity.isPushable()) {
                  entity.addVelocity(d, 0.0, e);
               }
            }

         }
      }
   }

   public void addVelocity(Vec3d velocity) {
      this.addVelocity(velocity.x, velocity.y, velocity.z);
   }

   public void addVelocity(double deltaX, double deltaY, double deltaZ) {
      this.setVelocity(this.getVelocity().add(deltaX, deltaY, deltaZ));
      this.velocityDirty = true;
   }

   protected void scheduleVelocityUpdate() {
      this.velocityModified = true;
   }

   /** @deprecated */
   @Deprecated
   public final void serverDamage(DamageSource source, float amount) {
      World var4 = this.world;
      if (var4 instanceof ServerWorld serverWorld) {
         this.damage(serverWorld, source, amount);
      }

   }

   /** @deprecated */
   @Deprecated
   public final boolean sidedDamage(DamageSource source, float amount) {
      World var4 = this.world;
      if (var4 instanceof ServerWorld serverWorld) {
         return this.damage(serverWorld, source, amount);
      } else {
         return this.clientDamage(source);
      }
   }

   public abstract boolean damage(ServerWorld world, DamageSource source, float amount);

   public boolean clientDamage(DamageSource source) {
      return false;
   }

   public final Vec3d getRotationVec(float tickProgress) {
      return this.getRotationVector(this.getPitch(tickProgress), this.getYaw(tickProgress));
   }

   public Direction getFacing() {
      return Direction.getFacing(this.getRotationVec(1.0F));
   }

   public float getPitch(float tickProgress) {
      return this.getLerpedPitch(tickProgress);
   }

   public float getYaw(float tickProgress) {
      return this.getLerpedYaw(tickProgress);
   }

   public float getLerpedPitch(float tickProgress) {
      return tickProgress == 1.0F ? this.getPitch() : MathHelper.lerp(tickProgress, this.lastPitch, this.getPitch());
   }

   public float getLerpedYaw(float tickProgress) {
      return tickProgress == 1.0F ? this.getYaw() : MathHelper.lerpAngleDegrees(tickProgress, this.lastYaw, this.getYaw());
   }

   public final Vec3d getRotationVector(float pitch, float yaw) {
      float f = pitch * 0.017453292F;
      float g = -yaw * 0.017453292F;
      float h = MathHelper.cos(g);
      float i = MathHelper.sin(g);
      float j = MathHelper.cos(f);
      float k = MathHelper.sin(f);
      return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
   }

   public final Vec3d getOppositeRotationVector(float tickProgress) {
      return this.getOppositeRotationVector(this.getPitch(tickProgress), this.getYaw(tickProgress));
   }

   protected final Vec3d getOppositeRotationVector(float pitch, float yaw) {
      return this.getRotationVector(pitch - 90.0F, yaw);
   }

   public final Vec3d getEyePos() {
      return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
   }

   public final Vec3d getCameraPosVec(float tickProgress) {
      double d = MathHelper.lerp((double)tickProgress, this.lastX, this.getX());
      double e = MathHelper.lerp((double)tickProgress, this.lastY, this.getY()) + (double)this.getStandingEyeHeight();
      double f = MathHelper.lerp((double)tickProgress, this.lastZ, this.getZ());
      return new Vec3d(d, e, f);
   }

   public Vec3d getClientCameraPosVec(float tickProgress) {
      return this.getCameraPosVec(tickProgress);
   }

   public final Vec3d getLerpedPos(float deltaTicks) {
      double d = MathHelper.lerp((double)deltaTicks, this.lastX, this.getX());
      double e = MathHelper.lerp((double)deltaTicks, this.lastY, this.getY());
      double f = MathHelper.lerp((double)deltaTicks, this.lastZ, this.getZ());
      return new Vec3d(d, e, f);
   }

   public HitResult raycast(double maxDistance, float tickProgress, boolean includeFluids) {
      Vec3d vec3d = this.getCameraPosVec(tickProgress);
      Vec3d vec3d2 = this.getRotationVec(tickProgress);
      Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
      return this.getWorld().raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, this));
   }

   public boolean canBeHitByProjectile() {
      return this.isAlive() && this.canHit();
   }

   public boolean canHit() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void updateKilledAdvancementCriterion(Entity entityKilled, DamageSource damageSource) {
      if (entityKilled instanceof ServerPlayerEntity) {
         Criteria.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)entityKilled, this, damageSource);
      }

   }

   public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
      double d = this.getX() - cameraX;
      double e = this.getY() - cameraY;
      double f = this.getZ() - cameraZ;
      double g = d * d + e * e + f * f;
      return this.shouldRender(g);
   }

   public boolean shouldRender(double distance) {
      double d = this.getBoundingBox().getAverageSideLength();
      if (Double.isNaN(d)) {
         d = 1.0;
      }

      d *= 64.0 * renderDistanceMultiplier;
      return distance < d * d;
   }

   public boolean saveSelfData(WriteView view) {
      if (this.removalReason != null && !this.removalReason.shouldSave()) {
         return false;
      } else {
         String string = this.getSavedEntityId();
         if (string == null) {
            return false;
         } else {
            view.putString("id", string);
            this.writeData(view);
            return true;
         }
      }
   }

   public boolean saveData(WriteView view) {
      return this.hasVehicle() ? false : this.saveSelfData(view);
   }

   public void writeData(WriteView view) {
      try {
         if (this.vehicle != null) {
            view.put("Pos", Vec3d.CODEC, new Vec3d(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
         } else {
            view.put("Pos", Vec3d.CODEC, this.getPos());
         }

         view.put("Motion", Vec3d.CODEC, this.getVelocity());
         view.put("Rotation", Vec2f.CODEC, new Vec2f(this.getYaw(), this.getPitch()));
         view.putDouble("fall_distance", this.fallDistance);
         view.putShort("Fire", (short)this.fireTicks);
         view.putShort("Air", (short)this.getAir());
         view.putBoolean("OnGround", this.isOnGround());
         view.putBoolean("Invulnerable", this.invulnerable);
         view.putInt("PortalCooldown", this.portalCooldown);
         view.put("UUID", Uuids.INT_STREAM_CODEC, this.getUuid());
         view.putNullable("CustomName", TextCodecs.CODEC, this.getCustomName());
         if (this.isCustomNameVisible()) {
            view.putBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         if (this.isSilent()) {
            view.putBoolean("Silent", this.isSilent());
         }

         if (this.hasNoGravity()) {
            view.putBoolean("NoGravity", this.hasNoGravity());
         }

         if (this.glowing) {
            view.putBoolean("Glowing", true);
         }

         int i = this.getFrozenTicks();
         if (i > 0) {
            view.putInt("TicksFrozen", this.getFrozenTicks());
         }

         if (this.hasVisualFire) {
            view.putBoolean("HasVisualFire", this.hasVisualFire);
         }

         if (!this.commandTags.isEmpty()) {
            view.put("Tags", TAG_LIST_CODEC, List.copyOf(this.commandTags));
         }

         if (!this.customData.isEmpty()) {
            view.put("data", NbtComponent.CODEC, this.customData);
         }

         this.writeCustomData(view);
         if (this.hasPassengers()) {
            WriteView.ListView listView = view.getList("Passengers");
            Iterator var9 = this.getPassengerList().iterator();

            while(var9.hasNext()) {
               Entity entity = (Entity)var9.next();
               WriteView writeView = listView.add();
               if (!entity.saveSelfData(writeView)) {
                  listView.removeLast();
               }
            }

            if (listView.isEmpty()) {
               view.remove("Passengers");
            }
         }

      } catch (Throwable var7) {
         CrashReport crashReport = CrashReport.create(var7, "Saving entity NBT");
         CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
         this.populateCrashReport(crashReportSection);
         throw new CrashException(crashReport);
      }
   }

   public void readData(ReadView view) {
      try {
         Vec3d vec3d = (Vec3d)view.read("Pos", Vec3d.CODEC).orElse(Vec3d.ZERO);
         Vec3d vec3d2 = (Vec3d)view.read("Motion", Vec3d.CODEC).orElse(Vec3d.ZERO);
         Vec2f vec2f = (Vec2f)view.read("Rotation", Vec2f.CODEC).orElse(Vec2f.ZERO);
         this.setVelocity(Math.abs(vec3d2.x) > 10.0 ? 0.0 : vec3d2.x, Math.abs(vec3d2.y) > 10.0 ? 0.0 : vec3d2.y, Math.abs(vec3d2.z) > 10.0 ? 0.0 : vec3d2.z);
         this.velocityDirty = true;
         double d = 3.0000512E7;
         this.setPos(MathHelper.clamp(vec3d.x, -3.0000512E7, 3.0000512E7), MathHelper.clamp(vec3d.y, -2.0E7, 2.0E7), MathHelper.clamp(vec3d.z, -3.0000512E7, 3.0000512E7));
         this.setYaw(vec2f.x);
         this.setPitch(vec2f.y);
         this.resetPosition();
         this.setHeadYaw(this.getYaw());
         this.setBodyYaw(this.getYaw());
         this.fallDistance = view.getDouble("fall_distance", 0.0);
         this.fireTicks = view.getShort("Fire", (short)0);
         this.setAir(view.getInt("Air", this.getMaxAir()));
         this.onGround = view.getBoolean("OnGround", false);
         this.invulnerable = view.getBoolean("Invulnerable", false);
         this.portalCooldown = view.getInt("PortalCooldown", 0);
         view.read("UUID", Uuids.INT_STREAM_CODEC).ifPresent((uuid) -> {
            this.uuid = uuid;
            this.uuidString = this.uuid.toString();
         });
         if (Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ())) {
            if (Double.isFinite((double)this.getYaw()) && Double.isFinite((double)this.getPitch())) {
               this.refreshPosition();
               this.setRotation(this.getYaw(), this.getPitch());
               this.setCustomName((Text)view.read("CustomName", TextCodecs.CODEC).orElse((Object)null));
               this.setCustomNameVisible(view.getBoolean("CustomNameVisible", false));
               this.setSilent(view.getBoolean("Silent", false));
               this.setNoGravity(view.getBoolean("NoGravity", false));
               this.setGlowing(view.getBoolean("Glowing", false));
               this.setFrozenTicks(view.getInt("TicksFrozen", 0));
               this.hasVisualFire = view.getBoolean("HasVisualFire", false);
               this.customData = (NbtComponent)view.read("data", NbtComponent.CODEC).orElse(NbtComponent.DEFAULT);
               this.commandTags.clear();
               Optional var10000 = view.read("Tags", TAG_LIST_CODEC);
               Set var10001 = this.commandTags;
               Objects.requireNonNull(var10001);
               var10000.ifPresent(var10001::addAll);
               this.readCustomData(view);
               if (this.shouldSetPositionOnLoad()) {
                  this.refreshPosition();
               }

            } else {
               throw new IllegalStateException("Entity has invalid rotation");
            }
         } else {
            throw new IllegalStateException("Entity has invalid position");
         }
      } catch (Throwable var7) {
         CrashReport crashReport = CrashReport.create(var7, "Loading entity NBT");
         CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
         this.populateCrashReport(crashReportSection);
         throw new CrashException(crashReport);
      }
   }

   protected boolean shouldSetPositionOnLoad() {
      return true;
   }

   @Nullable
   protected final String getSavedEntityId() {
      EntityType entityType = this.getType();
      Identifier identifier = EntityType.getId(entityType);
      return entityType.isSaveable() && identifier != null ? identifier.toString() : null;
   }

   protected abstract void readCustomData(ReadView view);

   protected abstract void writeCustomData(WriteView view);

   @Nullable
   public ItemEntity dropItem(ServerWorld world, ItemConvertible item) {
      return this.dropItem(world, item, 0);
   }

   @Nullable
   public ItemEntity dropItem(ServerWorld world, ItemConvertible item, int offsetY) {
      return this.dropStack(world, new ItemStack(item), (float)offsetY);
   }

   @Nullable
   public ItemEntity dropStack(ServerWorld world, ItemStack stack) {
      return this.dropStack(world, stack, 0.0F);
   }

   @Nullable
   public ItemEntity dropStack(ServerWorld world, ItemStack stack, Vec3d offset) {
      if (stack.isEmpty()) {
         return null;
      } else {
         ItemEntity itemEntity = new ItemEntity(world, this.getX() + offset.x, this.getY() + offset.y, this.getZ() + offset.z, stack);
         itemEntity.setToDefaultPickupDelay();
         world.spawnEntity(itemEntity);
         return itemEntity;
      }
   }

   @Nullable
   public ItemEntity dropStack(ServerWorld world, ItemStack stack, float yOffset) {
      return this.dropStack(world, stack, new Vec3d(0.0, (double)yOffset, 0.0));
   }

   public boolean isAlive() {
      return !this.isRemoved();
   }

   public boolean isInsideWall() {
      if (this.noClip) {
         return false;
      } else {
         float f = this.dimensions.width() * 0.8F;
         Box box = Box.of(this.getEyePos(), (double)f, 1.0E-6, (double)f);
         return BlockPos.stream(box).anyMatch((pos) -> {
            BlockState blockState = this.getWorld().getBlockState(pos);
            return !blockState.isAir() && blockState.shouldSuffocate(this.getWorld(), pos) && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(this.getWorld(), pos).offset((Vec3i)pos), VoxelShapes.cuboid(box), BooleanBiFunction.AND);
         });
      }
   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      if (!this.getWorld().isClient && player.shouldCancelInteraction() && this instanceof Leashable leashable) {
         if (leashable.canBeLeashed() && this.isAlive()) {
            label83: {
               if (this instanceof LivingEntity) {
                  LivingEntity livingEntity = (LivingEntity)this;
                  if (livingEntity.isBaby()) {
                     break label83;
                  }
               }

               List list = Leashable.collectLeashablesAround(this, (leashablex) -> {
                  return leashablex.getLeashHolder() == player;
               });
               if (!list.isEmpty()) {
                  boolean bl = false;
                  Iterator var7 = list.iterator();

                  while(var7.hasNext()) {
                     Leashable leashable2 = (Leashable)var7.next();
                     if (leashable2.canBeLeashedTo(this)) {
                        leashable2.attachLeash(this, true);
                        bl = true;
                     }
                  }

                  if (bl) {
                     this.getWorld().emitGameEvent(GameEvent.ENTITY_ACTION, this.getBlockPos(), GameEvent.Emitter.of((Entity)player));
                     this.playSoundIfNotSilent(SoundEvents.ITEM_LEAD_TIED);
                     return ActionResult.SUCCESS_SERVER.noIncrementStat();
                  }
               }
            }
         }
      }

      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isOf(Items.SHEARS) && this.snipAllHeldLeashes(player)) {
         itemStack.damage(1, player, (Hand)hand);
         return ActionResult.SUCCESS;
      } else {
         if (this instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)this;
            if (itemStack.isOf(Items.SHEARS) && mobEntity.canRemoveSaddle(player) && !player.shouldCancelInteraction() && this.shearEquipment(player, hand, itemStack, mobEntity)) {
               return ActionResult.SUCCESS;
            }
         }

         if (this.isAlive() && this instanceof Leashable) {
            Leashable leashable3 = (Leashable)this;
            if (leashable3.getLeashHolder() == player) {
               if (!this.getWorld().isClient()) {
                  if (player.isInCreativeMode()) {
                     leashable3.detachLeashWithoutDrop();
                  } else {
                     leashable3.detachLeash();
                  }

                  this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
                  this.playSoundIfNotSilent(SoundEvents.ITEM_LEAD_UNTIED);
               }

               return ActionResult.SUCCESS.noIncrementStat();
            }

            ItemStack itemStack2 = player.getStackInHand(hand);
            if (itemStack2.isOf(Items.LEAD) && !(leashable3.getLeashHolder() instanceof PlayerEntity)) {
               if (!this.getWorld().isClient() && leashable3.canBeLeashedTo(player)) {
                  if (leashable3.isLeashed()) {
                     leashable3.detachLeash();
                  }

                  leashable3.attachLeash(player, true);
                  this.playSoundIfNotSilent(SoundEvents.ITEM_LEAD_TIED);
                  itemStack2.decrement(1);
               }

               return ActionResult.SUCCESS;
            }
         }

         return ActionResult.PASS;
      }
   }

   public boolean snipAllHeldLeashes(@Nullable PlayerEntity player) {
      boolean bl = this.detachAllHeldLeashes(player);
      if (bl) {
         World var4 = this.getWorld();
         if (var4 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var4;
            serverWorld.playSound((Entity)null, this.getBlockPos(), SoundEvents.ITEM_SHEARS_SNIP, player != null ? player.getSoundCategory() : this.getSoundCategory());
         }
      }

      return bl;
   }

   public boolean detachAllHeldLeashes(@Nullable PlayerEntity player) {
      List list = Leashable.collectLeashablesHeldBy(this);
      boolean bl = !list.isEmpty();
      if (this instanceof Leashable leashable) {
         if (leashable.isLeashed()) {
            leashable.detachLeash();
            bl = true;
         }
      }

      Iterator var6 = list.iterator();

      while(var6.hasNext()) {
         Leashable leashable2 = (Leashable)var6.next();
         leashable2.detachLeash();
      }

      if (bl) {
         this.emitGameEvent(GameEvent.SHEAR, player);
         return true;
      } else {
         return false;
      }
   }

   private boolean shearEquipment(PlayerEntity player, Hand hand, ItemStack shears, MobEntity entity) {
      Iterator var5 = EquipmentSlot.VALUES.iterator();

      EquipmentSlot equipmentSlot;
      ItemStack itemStack;
      EquippableComponent equippableComponent;
      do {
         do {
            do {
               if (!var5.hasNext()) {
                  return false;
               }

               equipmentSlot = (EquipmentSlot)var5.next();
               itemStack = entity.getEquippedStack(equipmentSlot);
               equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
            } while(equippableComponent == null);
         } while(!equippableComponent.canBeSheared());
      } while(EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) && !player.isCreative());

      shears.damage(1, player, (EquipmentSlot)LivingEntity.getSlotForHand(hand));
      Vec3d vec3d = this.dimensions.attachments().getPointOrDefault(EntityAttachmentType.PASSENGER);
      entity.equipLootStack(equipmentSlot, ItemStack.EMPTY);
      this.emitGameEvent(GameEvent.SHEAR, player);
      this.playSoundIfNotSilent((SoundEvent)equippableComponent.shearingSound().value());
      World var11 = this.getWorld();
      if (var11 instanceof ServerWorld serverWorld) {
         this.dropStack(serverWorld, itemStack, vec3d);
         Criteria.PLAYER_SHEARED_EQUIPMENT.trigger((ServerPlayerEntity)player, itemStack, entity);
      }

      return true;
   }

   public boolean collidesWith(Entity other) {
      return other.isCollidable(this) && !this.isConnectedThroughVehicle(other);
   }

   public boolean isCollidable(@Nullable Entity entity) {
      return false;
   }

   public void tickRiding() {
      this.setVelocity(Vec3d.ZERO);
      this.tick();
      if (this.hasVehicle()) {
         this.getVehicle().updatePassengerPosition(this);
      }
   }

   public final void updatePassengerPosition(Entity passenger) {
      if (this.hasPassenger(passenger)) {
         this.updatePassengerPosition(passenger, Entity::setPosition);
      }
   }

   protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
      Vec3d vec3d = this.getPassengerRidingPos(passenger);
      Vec3d vec3d2 = passenger.getVehicleAttachmentPos(this);
      positionUpdater.accept(passenger, vec3d.x - vec3d2.x, vec3d.y - vec3d2.y, vec3d.z - vec3d2.z);
   }

   public void onPassengerLookAround(Entity passenger) {
   }

   public Vec3d getVehicleAttachmentPos(Entity vehicle) {
      return this.getAttachments().getPoint(EntityAttachmentType.VEHICLE, 0, this.yaw);
   }

   public Vec3d getPassengerRidingPos(Entity passenger) {
      return this.getPos().add(this.getPassengerAttachmentPos(passenger, this.dimensions, 1.0F));
   }

   protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
      return getPassengerAttachmentPos(this, passenger, dimensions.attachments());
   }

   protected static Vec3d getPassengerAttachmentPos(Entity vehicle, Entity passenger, EntityAttachments attachments) {
      int i = vehicle.getPassengerList().indexOf(passenger);
      return attachments.getPointOrDefault(EntityAttachmentType.PASSENGER, i, vehicle.yaw);
   }

   public boolean startRiding(Entity entity) {
      return this.startRiding(entity, false);
   }

   public boolean isLiving() {
      return this instanceof LivingEntity;
   }

   public boolean startRiding(Entity entity, boolean force) {
      if (entity == this.vehicle) {
         return false;
      } else if (!entity.couldAcceptPassenger()) {
         return false;
      } else if (!this.getWorld().isClient() && !entity.type.isSaveable()) {
         return false;
      } else {
         for(Entity entity2 = entity; entity2.vehicle != null; entity2 = entity2.vehicle) {
            if (entity2.vehicle == this) {
               return false;
            }
         }

         if (force || this.canStartRiding(entity) && entity.canAddPassenger(this)) {
            if (this.hasVehicle()) {
               this.stopRiding();
            }

            this.setPose(EntityPose.STANDING);
            this.vehicle = entity;
            this.vehicle.addPassenger(this);
            entity.streamIntoPassengers().filter((passenger) -> {
               return passenger instanceof ServerPlayerEntity;
            }).forEach((player) -> {
               Criteria.STARTED_RIDING.trigger((ServerPlayerEntity)player);
            });
            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean canStartRiding(Entity entity) {
      return !this.isSneaking() && this.ridingCooldown <= 0;
   }

   public void removeAllPassengers() {
      for(int i = this.passengerList.size() - 1; i >= 0; --i) {
         ((Entity)this.passengerList.get(i)).stopRiding();
      }

   }

   public void dismountVehicle() {
      if (this.vehicle != null) {
         Entity entity = this.vehicle;
         this.vehicle = null;
         entity.removePassenger(this);
      }

   }

   public void stopRiding() {
      this.dismountVehicle();
   }

   protected void addPassenger(Entity passenger) {
      if (passenger.getVehicle() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (this.passengerList.isEmpty()) {
            this.passengerList = ImmutableList.of(passenger);
         } else {
            List list = Lists.newArrayList(this.passengerList);
            if (!this.getWorld().isClient && passenger instanceof PlayerEntity && !(this.getFirstPassenger() instanceof PlayerEntity)) {
               list.add(0, passenger);
            } else {
               list.add(passenger);
            }

            this.passengerList = ImmutableList.copyOf(list);
         }

         this.emitGameEvent(GameEvent.ENTITY_MOUNT, passenger);
      }
   }

   protected void removePassenger(Entity passenger) {
      if (passenger.getVehicle() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         if (this.passengerList.size() == 1 && this.passengerList.get(0) == passenger) {
            this.passengerList = ImmutableList.of();
         } else {
            this.passengerList = (ImmutableList)this.passengerList.stream().filter((entity) -> {
               return entity != passenger;
            }).collect(ImmutableList.toImmutableList());
         }

         passenger.ridingCooldown = 60;
         this.emitGameEvent(GameEvent.ENTITY_DISMOUNT, passenger);
      }
   }

   protected boolean canAddPassenger(Entity passenger) {
      return this.passengerList.isEmpty();
   }

   protected boolean couldAcceptPassenger() {
      return true;
   }

   public final boolean isInterpolating() {
      return this.getInterpolator() != null && this.getInterpolator().isInterpolating();
   }

   public final void updateTrackedPositionAndAngles(Vec3d pos, float yaw, float pitch) {
      PositionInterpolator positionInterpolator = this.getInterpolator();
      if (positionInterpolator != null) {
         positionInterpolator.refreshPositionAndAngles(pos, yaw, pitch);
      } else {
         this.setPosition(pos);
         this.setRotation(yaw, pitch);
      }

   }

   @Nullable
   public PositionInterpolator getInterpolator() {
      return null;
   }

   public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
      this.setHeadYaw(yaw);
   }

   public float getTargetingMargin() {
      return 0.0F;
   }

   public Vec3d getRotationVector() {
      return this.getRotationVector(this.getPitch(), this.getYaw());
   }

   public Vec3d getHandPosOffset(Item item) {
      if (!(this instanceof PlayerEntity playerEntity)) {
         return Vec3d.ZERO;
      } else {
         boolean bl = playerEntity.getOffHandStack().isOf(item) && !playerEntity.getMainHandStack().isOf(item);
         Arm arm = bl ? playerEntity.getMainArm().getOpposite() : playerEntity.getMainArm();
         return this.getRotationVector(0.0F, this.getYaw() + (float)(arm == Arm.RIGHT ? 80 : -80)).multiply(0.5);
      }
   }

   public Vec2f getRotationClient() {
      return new Vec2f(this.getPitch(), this.getYaw());
   }

   public Vec3d getRotationVecClient() {
      return Vec3d.fromPolar(this.getRotationClient());
   }

   public void tryUsePortal(Portal portal, BlockPos pos) {
      if (this.hasPortalCooldown()) {
         this.resetPortalCooldown();
      } else {
         if (this.portalManager != null && this.portalManager.portalMatches(portal)) {
            if (!this.portalManager.isInPortal()) {
               this.portalManager.setPortalPos(pos.toImmutable());
               this.portalManager.setInPortal(true);
            }
         } else {
            this.portalManager = new PortalManager(portal, pos.toImmutable());
         }

      }
   }

   protected void tickPortalTeleportation() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         this.tickPortalCooldown();
         if (this.portalManager != null) {
            if (this.portalManager.tick(serverWorld, this, this.canUsePortals(false))) {
               Profiler profiler = Profilers.get();
               profiler.push("portal");
               this.resetPortalCooldown();
               TeleportTarget teleportTarget = this.portalManager.createTeleportTarget(serverWorld, this);
               if (teleportTarget != null) {
                  ServerWorld serverWorld2 = teleportTarget.world();
                  if (serverWorld.getServer().isWorldAllowed(serverWorld2) && (serverWorld2.getRegistryKey() == serverWorld.getRegistryKey() || this.canTeleportBetween(serverWorld, serverWorld2))) {
                     this.teleportTo(teleportTarget);
                  }
               }

               profiler.pop();
            } else if (this.portalManager.hasExpired()) {
               this.portalManager = null;
            }

         }
      }
   }

   public int getDefaultPortalCooldown() {
      Entity entity = this.getFirstPassenger();
      return entity instanceof ServerPlayerEntity ? entity.getDefaultPortalCooldown() : 300;
   }

   public void setVelocityClient(double x, double y, double z) {
      this.setVelocity(x, y, z);
   }

   public void onDamaged(DamageSource damageSource) {
   }

   public void handleStatus(byte status) {
      switch (status) {
         case 53:
            HoneyBlock.addRegularParticles(this);
         default:
      }
   }

   public void animateDamage(float yaw) {
   }

   public boolean isOnFire() {
      boolean bl = this.getWorld() != null && this.getWorld().isClient;
      return !this.isFireImmune() && (this.fireTicks > 0 || bl && this.getFlag(0));
   }

   public boolean hasVehicle() {
      return this.getVehicle() != null;
   }

   public boolean hasPassengers() {
      return !this.passengerList.isEmpty();
   }

   public boolean shouldDismountUnderwater() {
      return this.getType().isIn(EntityTypeTags.DISMOUNTS_UNDERWATER);
   }

   public boolean shouldControlVehicles() {
      return !this.getType().isIn(EntityTypeTags.NON_CONTROLLING_RIDER);
   }

   public void setSneaking(boolean sneaking) {
      this.setFlag(1, sneaking);
   }

   public boolean isSneaking() {
      return this.getFlag(1);
   }

   public boolean bypassesSteppingEffects() {
      return this.isSneaking();
   }

   public boolean bypassesLandingEffects() {
      return this.isSneaking();
   }

   public boolean isSneaky() {
      return this.isSneaking();
   }

   public boolean isDescending() {
      return this.isSneaking();
   }

   public boolean isInSneakingPose() {
      return this.isInPose(EntityPose.CROUCHING);
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean sprinting) {
      this.setFlag(3, sprinting);
   }

   public boolean isSwimming() {
      return this.getFlag(4);
   }

   public boolean isInSwimmingPose() {
      return this.isInPose(EntityPose.SWIMMING);
   }

   public boolean isCrawling() {
      return this.isInSwimmingPose() && !this.isTouchingWater();
   }

   public void setSwimming(boolean swimming) {
      this.setFlag(4, swimming);
   }

   public final boolean isGlowingLocal() {
      return this.glowing;
   }

   public final void setGlowing(boolean glowing) {
      this.glowing = glowing;
      this.setFlag(6, this.isGlowing());
   }

   public boolean isGlowing() {
      return this.getWorld().isClient() ? this.getFlag(6) : this.glowing;
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   public boolean isInvisibleTo(PlayerEntity player) {
      if (player.isSpectator()) {
         return false;
      } else {
         AbstractTeam abstractTeam = this.getScoreboardTeam();
         return abstractTeam != null && player != null && player.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles() ? false : this.isInvisible();
      }
   }

   public boolean isOnRail() {
      return false;
   }

   public void updateEventHandler(BiConsumer callback) {
   }

   @Nullable
   public Team getScoreboardTeam() {
      return this.getWorld().getScoreboard().getScoreHolderTeam(this.getNameForScoreboard());
   }

   public final boolean isTeammate(@Nullable Entity other) {
      if (other == null) {
         return false;
      } else {
         return this == other || this.isInSameTeam(other) || other.isInSameTeam(this);
      }
   }

   protected boolean isInSameTeam(Entity other) {
      return this.isTeamPlayer(other.getScoreboardTeam());
   }

   public boolean isTeamPlayer(@Nullable AbstractTeam team) {
      return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(team) : false;
   }

   public void setInvisible(boolean invisible) {
      this.setFlag(5, invisible);
   }

   protected boolean getFlag(int index) {
      return ((Byte)this.dataTracker.get(FLAGS) & 1 << index) != 0;
   }

   protected void setFlag(int index, boolean value) {
      byte b = (Byte)this.dataTracker.get(FLAGS);
      if (value) {
         this.dataTracker.set(FLAGS, (byte)(b | 1 << index));
      } else {
         this.dataTracker.set(FLAGS, (byte)(b & ~(1 << index)));
      }

   }

   public int getMaxAir() {
      return 300;
   }

   public int getAir() {
      return (Integer)this.dataTracker.get(AIR);
   }

   public void setAir(int air) {
      this.dataTracker.set(AIR, air);
   }

   public void defrost() {
      this.setFrozenTicks(0);
   }

   public int getFrozenTicks() {
      return (Integer)this.dataTracker.get(FROZEN_TICKS);
   }

   public void setFrozenTicks(int frozenTicks) {
      this.dataTracker.set(FROZEN_TICKS, frozenTicks);
   }

   public float getFreezingScale() {
      int i = this.getMinFreezeDamageTicks();
      return (float)Math.min(this.getFrozenTicks(), i) / (float)i;
   }

   public boolean isFrozen() {
      return this.getFrozenTicks() >= this.getMinFreezeDamageTicks();
   }

   public int getMinFreezeDamageTicks() {
      return 140;
   }

   public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
      this.setFireTicks(this.fireTicks + 1);
      if (this.fireTicks == 0) {
         this.setOnFireFor(8.0F);
      }

      this.damage(world, this.getDamageSources().lightningBolt(), 5.0F);
   }

   public void onBubbleColumnSurfaceCollision(boolean drag, BlockPos pos) {
      applyBubbleColumnSurfaceEffects(this, drag, pos);
   }

   protected static void applyBubbleColumnSurfaceEffects(Entity entity, boolean drag, BlockPos pos) {
      Vec3d vec3d = entity.getVelocity();
      double d;
      if (drag) {
         d = Math.max(-0.9, vec3d.y - 0.03);
      } else {
         d = Math.min(1.8, vec3d.y + 0.1);
      }

      entity.setVelocity(vec3d.x, d, vec3d.z);
      spawnBubbleColumnParticles(entity.world, pos);
   }

   protected static void spawnBubbleColumnParticles(World world, BlockPos pos) {
      if (world instanceof ServerWorld serverWorld) {
         for(int i = 0; i < 2; ++i) {
            serverWorld.spawnParticles(ParticleTypes.SPLASH, (double)pos.getX() + world.random.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + world.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
            serverWorld.spawnParticles(ParticleTypes.BUBBLE, (double)pos.getX() + world.random.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + world.random.nextDouble(), 1, 0.0, 0.01, 0.0, 0.2);
         }
      }

   }

   public void onBubbleColumnCollision(boolean drag) {
      applyBubbleColumnEffects(this, drag);
   }

   protected static void applyBubbleColumnEffects(Entity entity, boolean drag) {
      Vec3d vec3d = entity.getVelocity();
      double d;
      if (drag) {
         d = Math.max(-0.3, vec3d.y - 0.03);
      } else {
         d = Math.min(0.7, vec3d.y + 0.06);
      }

      entity.setVelocity(vec3d.x, d, vec3d.z);
      entity.onLanding();
   }

   public boolean onKilledOther(ServerWorld world, LivingEntity other) {
      return true;
   }

   public void limitFallDistance() {
      if (this.getVelocity().getY() > -0.5 && this.fallDistance > 1.0) {
         this.fallDistance = 1.0;
      }

   }

   public void onLanding() {
      this.fallDistance = 0.0;
   }

   protected void pushOutOfBlocks(double x, double y, double z) {
      BlockPos blockPos = BlockPos.ofFloored(x, y, z);
      Vec3d vec3d = new Vec3d(x - (double)blockPos.getX(), y - (double)blockPos.getY(), z - (double)blockPos.getZ());
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Direction direction = Direction.UP;
      double d = Double.MAX_VALUE;
      Direction[] var13 = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
      int var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         Direction direction2 = var13[var15];
         mutable.set(blockPos, (Direction)direction2);
         if (!this.getWorld().getBlockState(mutable).isFullCube(this.getWorld(), mutable)) {
            double e = vec3d.getComponentAlongAxis(direction2.getAxis());
            double f = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - e : e;
            if (f < d) {
               d = f;
               direction = direction2;
            }
         }
      }

      float g = this.random.nextFloat() * 0.2F + 0.1F;
      float h = (float)direction.getDirection().offset();
      Vec3d vec3d2 = this.getVelocity().multiply(0.75);
      if (direction.getAxis() == Direction.Axis.X) {
         this.setVelocity((double)(h * g), vec3d2.y, vec3d2.z);
      } else if (direction.getAxis() == Direction.Axis.Y) {
         this.setVelocity(vec3d2.x, (double)(h * g), vec3d2.z);
      } else if (direction.getAxis() == Direction.Axis.Z) {
         this.setVelocity(vec3d2.x, vec3d2.y, (double)(h * g));
      }

   }

   public void slowMovement(BlockState state, Vec3d multiplier) {
      this.onLanding();
      this.movementMultiplier = multiplier;
   }

   private static Text removeClickEvents(Text textComponent) {
      MutableText mutableText = textComponent.copyContentOnly().setStyle(textComponent.getStyle().withClickEvent((ClickEvent)null));
      Iterator var2 = textComponent.getSiblings().iterator();

      while(var2.hasNext()) {
         Text text = (Text)var2.next();
         mutableText.append(removeClickEvents(text));
      }

      return mutableText;
   }

   public Text getName() {
      Text text = this.getCustomName();
      return text != null ? removeClickEvents(text) : this.getDefaultName();
   }

   protected Text getDefaultName() {
      return this.type.getName();
   }

   public boolean isPartOf(Entity entity) {
      return this == entity;
   }

   public float getHeadYaw() {
      return 0.0F;
   }

   public void setHeadYaw(float headYaw) {
   }

   public void setBodyYaw(float bodyYaw) {
   }

   public boolean isAttackable() {
      return true;
   }

   public boolean handleAttack(Entity attacker) {
      return false;
   }

   public String toString() {
      String string = this.getWorld() == null ? "~NULL~" : this.getWorld().toString();
      return this.removalReason != null ? String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, removed=%s]", this.getClass().getSimpleName(), this.getName().getString(), this.id, string, this.getX(), this.getY(), this.getZ(), this.removalReason) : String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.id, string, this.getX(), this.getY(), this.getZ());
   }

   protected final boolean isAlwaysInvulnerableTo(DamageSource damageSource) {
      return this.isRemoved() || this.invulnerable && !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isSourceCreativePlayer() || damageSource.isIn(DamageTypeTags.IS_FIRE) && this.isFireImmune() || damageSource.isIn(DamageTypeTags.IS_FALL) && this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean invulnerable) {
      this.invulnerable = invulnerable;
   }

   public void copyPositionAndRotation(Entity entity) {
      this.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
   }

   public void copyFrom(Entity original) {
      ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getErrorReporterContext(), LOGGER);

      try {
         NbtWriteView nbtWriteView = NbtWriteView.create(logging, original.getRegistryManager());
         original.writeData(nbtWriteView);
         this.readData(NbtReadView.create(logging, this.getRegistryManager(), nbtWriteView.getNbt()));
      } catch (Throwable var6) {
         try {
            logging.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      logging.close();
      this.portalCooldown = original.portalCooldown;
      this.portalManager = original.portalManager;
   }

   @Nullable
   public Entity teleportTo(TeleportTarget teleportTarget) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         if (!this.isRemoved()) {
            ServerWorld serverWorld2 = teleportTarget.world();
            boolean bl = serverWorld2.getRegistryKey() != serverWorld.getRegistryKey();
            if (!teleportTarget.asPassenger()) {
               this.stopRiding();
            }

            if (bl) {
               return this.teleportCrossDimension(serverWorld, serverWorld2, teleportTarget);
            }

            return this.teleportSameDimension(serverWorld, teleportTarget);
         }
      }

      return null;
   }

   private Entity teleportSameDimension(ServerWorld world, TeleportTarget teleportTarget) {
      Iterator var3 = this.getPassengerList().iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         entity.teleportTo(this.getPassengerTeleportTarget(teleportTarget, entity));
      }

      Profiler profiler = Profilers.get();
      profiler.push("teleportSameDimension");
      this.setPosition(PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives());
      if (!teleportTarget.asPassenger()) {
         this.sendTeleportPacket(teleportTarget);
      }

      teleportTarget.postTeleportTransition().onTransition(this);
      profiler.pop();
      return this;
   }

   private Entity teleportCrossDimension(ServerWorld from, ServerWorld to, TeleportTarget teleportTarget) {
      List list = this.getPassengerList();
      List list2 = new ArrayList(list.size());
      this.removeAllPassengers();
      Iterator var6 = list.iterator();

      Entity entity;
      while(var6.hasNext()) {
         entity = (Entity)var6.next();
         Entity entity2 = entity.teleportTo(this.getPassengerTeleportTarget(teleportTarget, entity));
         if (entity2 != null) {
            list2.add(entity2);
         }
      }

      Profiler profiler = Profilers.get();
      profiler.push("teleportCrossDimension");
      entity = this.getType().create(to, SpawnReason.DIMENSION_TRAVEL);
      if (entity == null) {
         profiler.pop();
         return null;
      } else {
         entity.copyFrom(this);
         this.removeFromDimension();
         entity.setPosition(PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives());
         to.onDimensionChanged(entity);
         Iterator var11 = list2.iterator();

         while(var11.hasNext()) {
            Entity entity3 = (Entity)var11.next();
            entity3.startRiding(entity, true);
         }

         to.resetIdleTimeout();
         teleportTarget.postTeleportTransition().onTransition(entity);
         this.teleportSpectatingPlayers(teleportTarget, from);
         profiler.pop();
         return entity;
      }
   }

   protected void teleportSpectatingPlayers(TeleportTarget teleportTarget, ServerWorld from) {
      List list = List.copyOf(from.getPlayers());
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
         if (serverPlayerEntity.getCameraEntity() == this) {
            serverPlayerEntity.teleportTo(teleportTarget);
            serverPlayerEntity.setCameraEntity((Entity)null);
         }
      }

   }

   private TeleportTarget getPassengerTeleportTarget(TeleportTarget teleportTarget, Entity passenger) {
      float f = teleportTarget.yaw() + (teleportTarget.relatives().contains(PositionFlag.Y_ROT) ? 0.0F : passenger.getYaw() - this.getYaw());
      float g = teleportTarget.pitch() + (teleportTarget.relatives().contains(PositionFlag.X_ROT) ? 0.0F : passenger.getPitch() - this.getPitch());
      Vec3d vec3d = passenger.getPos().subtract(this.getPos());
      Vec3d vec3d2 = teleportTarget.position().add(teleportTarget.relatives().contains(PositionFlag.X) ? 0.0 : vec3d.getX(), teleportTarget.relatives().contains(PositionFlag.Y) ? 0.0 : vec3d.getY(), teleportTarget.relatives().contains(PositionFlag.Z) ? 0.0 : vec3d.getZ());
      return teleportTarget.withPosition(vec3d2).withRotation(f, g).asPassenger();
   }

   private void sendTeleportPacket(TeleportTarget teleportTarget) {
      Entity entity = this.getControllingPassenger();
      Iterator var3 = this.getPassengersDeep().iterator();

      while(true) {
         while(true) {
            Entity entity2;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               entity2 = (Entity)var3.next();
            } while(!(entity2 instanceof ServerPlayerEntity));

            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
            if (entity != null && serverPlayerEntity.getId() == entity.getId()) {
               serverPlayerEntity.networkHandler.sendPacket(EntityPositionS2CPacket.create(this.getId(), PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives(), this.onGround));
            } else {
               serverPlayerEntity.networkHandler.sendPacket(EntityPositionS2CPacket.create(this.getId(), PlayerPosition.fromEntity(this), Set.of(), this.onGround));
            }
         }
      }
   }

   public void setPosition(PlayerPosition pos, Set flags) {
      PlayerPosition playerPosition = PlayerPosition.fromEntity(this);
      PlayerPosition playerPosition2 = PlayerPosition.apply(playerPosition, pos, flags);
      this.setPos(playerPosition2.position().x, playerPosition2.position().y, playerPosition2.position().z);
      this.setYaw(playerPosition2.yaw());
      this.setHeadYaw(playerPosition2.yaw());
      this.setPitch(playerPosition2.pitch());
      this.refreshPosition();
      this.resetPosition();
      this.setVelocity(playerPosition2.deltaMovement());
      this.clearQueuedCollisionChecks();
   }

   public void rotate(float yaw, float pitch) {
      this.setYaw(yaw);
      this.setHeadYaw(yaw);
      this.setPitch(pitch);
      this.updateLastAngles();
   }

   public void addPortalChunkTicketAt(BlockPos pos) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         serverWorld.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(pos), 3);
      }

   }

   protected void removeFromDimension() {
      this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
      if (this instanceof Leashable leashable) {
         leashable.detachLeashWithoutDrop();
      }

      if (this instanceof ServerWaypoint serverWaypoint) {
         World var3 = this.world;
         if (var3 instanceof ServerWorld serverWorld) {
            serverWorld.getWaypointHandler().onUntrack(serverWaypoint);
         }
      }

   }

   public Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
      return NetherPortal.entityPosInPortal(portalRect, portalAxis, this.getPos(), this.getDimensions(this.getPose()));
   }

   public boolean canUsePortals(boolean allowVehicles) {
      return (allowVehicles || !this.hasVehicle()) && this.isAlive();
   }

   public boolean canTeleportBetween(World from, World to) {
      if (from.getRegistryKey() == World.END && to.getRegistryKey() == World.OVERWORLD) {
         Iterator var3 = this.getPassengerList().iterator();

         while(var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            if (entity instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
               if (!serverPlayerEntity.seenCredits) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
      return max;
   }

   public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
      return true;
   }

   public int getSafeFallDistance() {
      return 3;
   }

   public boolean canAvoidTraps() {
      return false;
   }

   public void populateCrashReport(CrashReportSection section) {
      section.add("Entity Type", () -> {
         String var10000 = String.valueOf(EntityType.getId(this.getType()));
         return var10000 + " (" + this.getClass().getCanonicalName() + ")";
      });
      section.add("Entity ID", (Object)this.id);
      section.add("Entity Name", () -> {
         return this.getName().getString();
      });
      section.add("Entity's Exact location", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
      section.add("Entity's Block location", (Object)CrashReportSection.createPositionString(this.getWorld(), MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
      Vec3d vec3d = this.getVelocity();
      section.add("Entity's Momentum", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
      section.add("Entity's Passengers", () -> {
         return this.getPassengerList().toString();
      });
      section.add("Entity's Vehicle", () -> {
         return String.valueOf(this.getVehicle());
      });
   }

   public boolean doesRenderOnFire() {
      return this.isOnFire() && !this.isSpectator();
   }

   public void setUuid(UUID uuid) {
      this.uuid = uuid;
      this.uuidString = this.uuid.toString();
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getUuidAsString() {
      return this.uuidString;
   }

   public String getNameForScoreboard() {
      return this.uuidString;
   }

   public boolean isPushedByFluids() {
      return true;
   }

   public static double getRenderDistanceMultiplier() {
      return renderDistanceMultiplier;
   }

   public static void setRenderDistanceMultiplier(double value) {
      renderDistanceMultiplier = value;
   }

   public Text getDisplayName() {
      return Team.decorateName(this.getScoreboardTeam(), this.getName()).styled((style) -> {
         return style.withHoverEvent(this.getHoverEvent()).withInsertion(this.getUuidAsString());
      });
   }

   public void setCustomName(@Nullable Text name) {
      this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(name));
   }

   @Nullable
   public Text getCustomName() {
      return (Text)((Optional)this.dataTracker.get(CUSTOM_NAME)).orElse((Object)null);
   }

   public boolean hasCustomName() {
      return ((Optional)this.dataTracker.get(CUSTOM_NAME)).isPresent();
   }

   public void setCustomNameVisible(boolean visible) {
      this.dataTracker.set(NAME_VISIBLE, visible);
   }

   public boolean isCustomNameVisible() {
      return (Boolean)this.dataTracker.get(NAME_VISIBLE);
   }

   public boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set flags, float yaw, float pitch, boolean resetCamera) {
      Entity entity = this.teleportTo(new TeleportTarget(world, new Vec3d(destX, destY, destZ), Vec3d.ZERO, yaw, pitch, flags, TeleportTarget.NO_OP));
      return entity != null;
   }

   public void requestTeleportAndDismount(double destX, double destY, double destZ) {
      this.requestTeleport(destX, destY, destZ);
   }

   public void requestTeleport(double destX, double destY, double destZ) {
      if (this.getWorld() instanceof ServerWorld) {
         this.refreshPositionAndAngles(destX, destY, destZ, this.getYaw(), this.getPitch());
         this.teleportPassengers();
      }
   }

   private void teleportPassengers() {
      this.streamSelfAndPassengers().forEach((entity) -> {
         UnmodifiableIterator var1 = entity.passengerList.iterator();

         while(var1.hasNext()) {
            Entity entity2 = (Entity)var1.next();
            entity.updatePassengerPosition(entity2, Entity::refreshPositionAfterTeleport);
         }

      });
   }

   public void requestTeleportOffset(double offsetX, double offsetY, double offsetZ) {
      this.requestTeleport(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
   }

   public boolean shouldRenderName() {
      return this.isCustomNameVisible();
   }

   public void onDataTrackerUpdate(List entries) {
   }

   public void onTrackedDataSet(TrackedData data) {
      if (POSE.equals(data)) {
         this.calculateDimensions();
      }

   }

   /** @deprecated */
   @Deprecated
   protected void reinitDimensions() {
      EntityPose entityPose = this.getPose();
      EntityDimensions entityDimensions = this.getDimensions(entityPose);
      this.dimensions = entityDimensions;
      this.standingEyeHeight = entityDimensions.eyeHeight();
   }

   public void calculateDimensions() {
      EntityDimensions entityDimensions = this.dimensions;
      EntityPose entityPose = this.getPose();
      EntityDimensions entityDimensions2 = this.getDimensions(entityPose);
      this.dimensions = entityDimensions2;
      this.standingEyeHeight = entityDimensions2.eyeHeight();
      this.refreshPosition();
      boolean bl = entityDimensions2.width() <= 4.0F && entityDimensions2.height() <= 4.0F;
      if (!this.world.isClient && !this.firstUpdate && !this.noClip && bl && (entityDimensions2.width() > entityDimensions.width() || entityDimensions2.height() > entityDimensions.height()) && !(this instanceof PlayerEntity)) {
         this.recalculateDimensions(entityDimensions);
      }

   }

   public boolean recalculateDimensions(EntityDimensions previous) {
      EntityDimensions entityDimensions = this.getDimensions(this.getPose());
      Vec3d vec3d = this.getPos().add(0.0, (double)previous.height() / 2.0, 0.0);
      double d = (double)Math.max(0.0F, entityDimensions.width() - previous.width()) + 1.0E-6;
      double e = (double)Math.max(0.0F, entityDimensions.height() - previous.height()) + 1.0E-6;
      VoxelShape voxelShape = VoxelShapes.cuboid(Box.of(vec3d, d, e, d));
      Optional optional = this.world.findClosestCollision(this, voxelShape, vec3d, (double)entityDimensions.width(), (double)entityDimensions.height(), (double)entityDimensions.width());
      if (optional.isPresent()) {
         this.setPosition(((Vec3d)optional.get()).add(0.0, (double)(-entityDimensions.height()) / 2.0, 0.0));
         return true;
      } else {
         if (entityDimensions.width() > previous.width() && entityDimensions.height() > previous.height()) {
            VoxelShape voxelShape2 = VoxelShapes.cuboid(Box.of(vec3d, d, 1.0E-6, d));
            Optional optional2 = this.world.findClosestCollision(this, voxelShape2, vec3d, (double)entityDimensions.width(), (double)previous.height(), (double)entityDimensions.width());
            if (optional2.isPresent()) {
               this.setPosition(((Vec3d)optional2.get()).add(0.0, (double)(-previous.height()) / 2.0 + 1.0E-6, 0.0));
               return true;
            }
         }

         return false;
      }
   }

   public Direction getHorizontalFacing() {
      return Direction.fromHorizontalDegrees((double)this.getYaw());
   }

   public Direction getMovementDirection() {
      return this.getHorizontalFacing();
   }

   protected HoverEvent getHoverEvent() {
      return new HoverEvent.ShowEntity(new HoverEvent.EntityContent(this.getType(), this.getUuid(), this.getName()));
   }

   public boolean canBeSpectated(ServerPlayerEntity spectator) {
      return true;
   }

   public final Box getBoundingBox() {
      return this.boundingBox;
   }

   public final void setBoundingBox(Box boundingBox) {
      this.boundingBox = boundingBox;
   }

   public final float getEyeHeight(EntityPose pose) {
      return this.getDimensions(pose).eyeHeight();
   }

   public final float getStandingEyeHeight() {
      return this.standingEyeHeight;
   }

   public StackReference getStackReference(int mappedIndex) {
      return StackReference.EMPTY;
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.getWorld().getServer();
   }

   public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
      return ActionResult.PASS;
   }

   public boolean isImmuneToExplosion(Explosion explosion) {
      return false;
   }

   public void onStartedTrackingBy(ServerPlayerEntity player) {
   }

   public void onStoppedTrackingBy(ServerPlayerEntity player) {
   }

   public float applyRotation(BlockRotation rotation) {
      float f = MathHelper.wrapDegrees(this.getYaw());
      switch (rotation) {
         case CLOCKWISE_180:
            return f + 180.0F;
         case COUNTERCLOCKWISE_90:
            return f + 270.0F;
         case CLOCKWISE_90:
            return f + 90.0F;
         default:
            return f;
      }
   }

   public float applyMirror(BlockMirror mirror) {
      float f = MathHelper.wrapDegrees(this.getYaw());
      switch (mirror) {
         case FRONT_BACK:
            return -f;
         case LEFT_RIGHT:
            return 180.0F - f;
         default:
            return f;
      }
   }

   public ProjectileDeflection getProjectileDeflection(ProjectileEntity projectile) {
      return this.getType().isIn(EntityTypeTags.DEFLECTS_PROJECTILES) ? ProjectileDeflection.SIMPLE : ProjectileDeflection.NONE;
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return null;
   }

   public final boolean hasControllingPassenger() {
      return this.getControllingPassenger() != null;
   }

   public final List getPassengerList() {
      return this.passengerList;
   }

   @Nullable
   public Entity getFirstPassenger() {
      return this.passengerList.isEmpty() ? null : (Entity)this.passengerList.get(0);
   }

   public boolean hasPassenger(Entity passenger) {
      return this.passengerList.contains(passenger);
   }

   public boolean hasPassenger(Predicate predicate) {
      UnmodifiableIterator var2 = this.passengerList.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(!predicate.test(entity));

      return true;
   }

   private Stream streamIntoPassengers() {
      return this.passengerList.stream().flatMap(Entity::streamSelfAndPassengers);
   }

   public Stream streamSelfAndPassengers() {
      return Stream.concat(Stream.of(this), this.streamIntoPassengers());
   }

   public Stream streamPassengersAndSelf() {
      return Stream.concat(this.passengerList.stream().flatMap(Entity::streamPassengersAndSelf), Stream.of(this));
   }

   public Iterable getPassengersDeep() {
      return () -> {
         return this.streamIntoPassengers().iterator();
      };
   }

   public int getPlayerPassengers() {
      return (int)this.streamIntoPassengers().filter((passenger) -> {
         return passenger instanceof PlayerEntity;
      }).count();
   }

   public boolean hasPlayerRider() {
      return this.getPlayerPassengers() == 1;
   }

   public Entity getRootVehicle() {
      Entity entity;
      for(entity = this; entity.hasVehicle(); entity = entity.getVehicle()) {
      }

      return entity;
   }

   public boolean isConnectedThroughVehicle(Entity entity) {
      return this.getRootVehicle() == entity.getRootVehicle();
   }

   public boolean hasPassengerDeep(Entity passenger) {
      if (!passenger.hasVehicle()) {
         return false;
      } else {
         Entity entity = passenger.getVehicle();
         return entity == this ? true : this.hasPassengerDeep(entity);
      }
   }

   public final boolean isLogicalSideForUpdatingMovement() {
      if (this.world.isClient()) {
         return this.isControlledByMainPlayer();
      } else {
         return !this.isControlledByPlayer();
      }
   }

   protected boolean isControlledByMainPlayer() {
      LivingEntity livingEntity = this.getControllingPassenger();
      return livingEntity != null && livingEntity.isControlledByMainPlayer();
   }

   public boolean isControlledByPlayer() {
      LivingEntity livingEntity = this.getControllingPassenger();
      return livingEntity != null && livingEntity.isControlledByPlayer();
   }

   public boolean canMoveVoluntarily() {
      return this.isLogicalSideForUpdatingMovement();
   }

   public boolean canActVoluntarily() {
      return this.isLogicalSideForUpdatingMovement();
   }

   protected static Vec3d getPassengerDismountOffset(double vehicleWidth, double passengerWidth, float passengerYaw) {
      double d = (vehicleWidth + passengerWidth + 9.999999747378752E-6) / 2.0;
      float f = -MathHelper.sin(passengerYaw * 0.017453292F);
      float g = MathHelper.cos(passengerYaw * 0.017453292F);
      float h = Math.max(Math.abs(f), Math.abs(g));
      return new Vec3d((double)f * d / (double)h, 0.0, (double)g * d / (double)h);
   }

   public Vec3d updatePassengerForDismount(LivingEntity passenger) {
      return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   @Nullable
   public Entity getVehicle() {
      return this.vehicle;
   }

   @Nullable
   public Entity getControllingVehicle() {
      return this.vehicle != null && this.vehicle.getControllingPassenger() == this ? this.vehicle : null;
   }

   public PistonBehavior getPistonBehavior() {
      return PistonBehavior.NORMAL;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   protected int getBurningDuration() {
      return 0;
   }

   public ServerCommandSource getCommandSource(ServerWorld world) {
      return new ServerCommandSource(CommandOutput.DUMMY, this.getPos(), this.getRotationClient(), world, 0, this.getName().getString(), this.getDisplayName(), world.getServer(), this);
   }

   public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
      Vec3d vec3d = anchorPoint.positionAt(this);
      double d = target.x - vec3d.x;
      double e = target.y - vec3d.y;
      double f = target.z - vec3d.z;
      double g = Math.sqrt(d * d + f * f);
      this.setPitch(MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875))));
      this.setYaw(MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F));
      this.setHeadYaw(this.getYaw());
      this.lastPitch = this.getPitch();
      this.lastYaw = this.getYaw();
   }

   public float lerpYaw(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastYaw, this.yaw);
   }

   public boolean updateMovementInFluid(TagKey tag, double speed) {
      if (this.isRegionUnloaded()) {
         return false;
      } else {
         Box box = this.getBoundingBox().contract(0.001);
         int i = MathHelper.floor(box.minX);
         int j = MathHelper.ceil(box.maxX);
         int k = MathHelper.floor(box.minY);
         int l = MathHelper.ceil(box.maxY);
         int m = MathHelper.floor(box.minZ);
         int n = MathHelper.ceil(box.maxZ);
         double d = 0.0;
         boolean bl = this.isPushedByFluids();
         boolean bl2 = false;
         Vec3d vec3d = Vec3d.ZERO;
         int o = 0;
         BlockPos.Mutable mutable = new BlockPos.Mutable();

         for(int p = i; p < j; ++p) {
            for(int q = k; q < l; ++q) {
               for(int r = m; r < n; ++r) {
                  mutable.set(p, q, r);
                  FluidState fluidState = this.getWorld().getFluidState(mutable);
                  if (fluidState.isIn(tag)) {
                     double e = (double)((float)q + fluidState.getHeight(this.getWorld(), mutable));
                     if (e >= box.minY) {
                        bl2 = true;
                        d = Math.max(e - box.minY, d);
                        if (bl) {
                           Vec3d vec3d2 = fluidState.getVelocity(this.getWorld(), mutable);
                           if (d < 0.4) {
                              vec3d2 = vec3d2.multiply(d);
                           }

                           vec3d = vec3d.add(vec3d2);
                           ++o;
                        }
                     }
                  }
               }
            }
         }

         if (vec3d.length() > 0.0) {
            if (o > 0) {
               vec3d = vec3d.multiply(1.0 / (double)o);
            }

            if (!(this instanceof PlayerEntity)) {
               vec3d = vec3d.normalize();
            }

            Vec3d vec3d3 = this.getVelocity();
            vec3d = vec3d.multiply(speed);
            double f = 0.003;
            if (Math.abs(vec3d3.x) < 0.003 && Math.abs(vec3d3.z) < 0.003 && vec3d.length() < 0.0045000000000000005) {
               vec3d = vec3d.normalize().multiply(0.0045000000000000005);
            }

            this.setVelocity(this.getVelocity().add(vec3d));
         }

         this.fluidHeight.put(tag, d);
         return bl2;
      }
   }

   public boolean isRegionUnloaded() {
      Box box = this.getBoundingBox().expand(1.0);
      int i = MathHelper.floor(box.minX);
      int j = MathHelper.ceil(box.maxX);
      int k = MathHelper.floor(box.minZ);
      int l = MathHelper.ceil(box.maxZ);
      return !this.getWorld().isRegionLoaded(i, k, j, l);
   }

   public double getFluidHeight(TagKey fluid) {
      return this.fluidHeight.getDouble(fluid);
   }

   public double getSwimHeight() {
      return (double)this.getStandingEyeHeight() < 0.4 ? 0.0 : 0.4;
   }

   public final float getWidth() {
      return this.dimensions.width();
   }

   public final float getHeight() {
      return this.dimensions.height();
   }

   public Packet createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
      return new EntitySpawnS2CPacket(this, entityTrackerEntry);
   }

   public EntityDimensions getDimensions(EntityPose pose) {
      return this.type.getDimensions();
   }

   public final EntityAttachments getAttachments() {
      return this.dimensions.attachments();
   }

   public Vec3d getPos() {
      return this.pos;
   }

   public Vec3d getSyncedPos() {
      return this.getPos();
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public BlockState getBlockStateAtPos() {
      if (this.stateAtPos == null) {
         this.stateAtPos = this.getWorld().getBlockState(this.getBlockPos());
      }

      return this.stateAtPos;
   }

   public ChunkPos getChunkPos() {
      return this.chunkPos;
   }

   public Vec3d getVelocity() {
      return this.velocity;
   }

   public void setVelocity(Vec3d velocity) {
      this.velocity = velocity;
   }

   public void addVelocityInternal(Vec3d velocity) {
      this.setVelocity(this.getVelocity().add(velocity));
   }

   public void setVelocity(double x, double y, double z) {
      this.setVelocity(new Vec3d(x, y, z));
   }

   public final int getBlockX() {
      return this.blockPos.getX();
   }

   public final double getX() {
      return this.pos.x;
   }

   public double getBodyX(double widthScale) {
      return this.pos.x + (double)this.getWidth() * widthScale;
   }

   public double getParticleX(double widthScale) {
      return this.getBodyX((2.0 * this.random.nextDouble() - 1.0) * widthScale);
   }

   public final int getBlockY() {
      return this.blockPos.getY();
   }

   public final double getY() {
      return this.pos.y;
   }

   public double getBodyY(double heightScale) {
      return this.pos.y + (double)this.getHeight() * heightScale;
   }

   public double getRandomBodyY() {
      return this.getBodyY(this.random.nextDouble());
   }

   public double getEyeY() {
      return this.pos.y + (double)this.standingEyeHeight;
   }

   public final int getBlockZ() {
      return this.blockPos.getZ();
   }

   public final double getZ() {
      return this.pos.z;
   }

   public double getBodyZ(double widthScale) {
      return this.pos.z + (double)this.getWidth() * widthScale;
   }

   public double getParticleZ(double widthScale) {
      return this.getBodyZ((2.0 * this.random.nextDouble() - 1.0) * widthScale);
   }

   public final void setPos(double x, double y, double z) {
      if (this.pos.x != x || this.pos.y != y || this.pos.z != z) {
         this.pos = new Vec3d(x, y, z);
         int i = MathHelper.floor(x);
         int j = MathHelper.floor(y);
         int k = MathHelper.floor(z);
         if (i != this.blockPos.getX() || j != this.blockPos.getY() || k != this.blockPos.getZ()) {
            this.blockPos = new BlockPos(i, j, k);
            this.stateAtPos = null;
            if (ChunkSectionPos.getSectionCoord(i) != this.chunkPos.x || ChunkSectionPos.getSectionCoord(k) != this.chunkPos.z) {
               this.chunkPos = new ChunkPos(this.blockPos);
            }
         }

         this.changeListener.updateEntityPosition();
         if (!this.firstUpdate) {
            World var11 = this.world;
            if (var11 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var11;
               if (!this.isRemoved()) {
                  if (this instanceof ServerWaypoint) {
                     ServerWaypoint serverWaypoint = (ServerWaypoint)this;
                     if (serverWaypoint.hasWaypoint()) {
                        serverWorld.getWaypointHandler().onUpdate(serverWaypoint);
                     }
                  }

                  if (this instanceof ServerPlayerEntity) {
                     ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
                     if (serverPlayerEntity.canReceiveWaypoints() && serverPlayerEntity.networkHandler != null) {
                        serverWorld.getWaypointHandler().updatePlayerPos(serverPlayerEntity);
                     }
                  }
               }
            }
         }
      }

   }

   public void checkDespawn() {
   }

   public Vec3d[] getHeldQuadLeashOffsets() {
      return Leashable.createQuadLeashOffsets(this, 0.0, 0.5, 0.5, 0.0);
   }

   public boolean hasQuadLeashAttachmentPoints() {
      return false;
   }

   public void tickHeldLeash(Leashable leashedEntity) {
   }

   public void onHeldLeashUpdate(Leashable heldLeashable) {
   }

   public Vec3d getLeashPos(float tickProgress) {
      return this.getLerpedPos(tickProgress).add(0.0, (double)this.standingEyeHeight * 0.7, 0.0);
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      int i = packet.getEntityId();
      double d = packet.getX();
      double e = packet.getY();
      double f = packet.getZ();
      this.updateTrackedPosition(d, e, f);
      this.refreshPositionAndAngles(d, e, f, packet.getYaw(), packet.getPitch());
      this.setId(i);
      this.setUuid(packet.getUuid());
      Vec3d vec3d = new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
      this.setVelocity(vec3d);
   }

   @Nullable
   public ItemStack getPickBlockStack() {
      return null;
   }

   public void setInPowderSnow(boolean inPowderSnow) {
      this.inPowderSnow = inPowderSnow;
   }

   public boolean canFreeze() {
      return !this.getType().isIn(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
   }

   public boolean shouldEscapePowderSnow() {
      return this.getFrozenTicks() > 0;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getBodyYaw() {
      return this.getYaw();
   }

   public void setYaw(float yaw) {
      if (!Float.isFinite(yaw)) {
         Util.logErrorOrPause("Invalid entity rotation: " + yaw + ", discarding.");
      } else {
         this.yaw = yaw;
      }
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float pitch) {
      if (!Float.isFinite(pitch)) {
         Util.logErrorOrPause("Invalid entity rotation: " + pitch + ", discarding.");
      } else {
         this.pitch = Math.clamp(pitch % 360.0F, -90.0F, 90.0F);
      }
   }

   public boolean canSprintAsVehicle() {
      return false;
   }

   public float getStepHeight() {
      return 0.0F;
   }

   public void onExplodedBy(@Nullable Entity entity) {
   }

   public final boolean isRemoved() {
      return this.removalReason != null;
   }

   @Nullable
   public RemovalReason getRemovalReason() {
      return this.removalReason;
   }

   public final void setRemoved(RemovalReason reason) {
      if (this.removalReason == null) {
         this.removalReason = reason;
      }

      if (this.removalReason.shouldDestroy()) {
         this.stopRiding();
      }

      this.getPassengerList().forEach(Entity::stopRiding);
      this.changeListener.remove(reason);
      this.onRemove(reason);
   }

   protected void unsetRemoved() {
      this.removalReason = null;
   }

   public void setChangeListener(EntityChangeListener changeListener) {
      this.changeListener = changeListener;
   }

   public boolean shouldSave() {
      if (this.removalReason != null && !this.removalReason.shouldSave()) {
         return false;
      } else if (this.hasVehicle()) {
         return false;
      } else {
         return !this.hasPassengers() || !this.hasPlayerRider();
      }
   }

   public boolean isPlayer() {
      return false;
   }

   public boolean canModifyAt(ServerWorld world, BlockPos pos) {
      return true;
   }

   public boolean isFlyingVehicle() {
      return false;
   }

   public World getWorld() {
      return this.world;
   }

   protected void setWorld(World world) {
      this.world = world;
   }

   public DamageSources getDamageSources() {
      return this.getWorld().getDamageSources();
   }

   public DynamicRegistryManager getRegistryManager() {
      return this.getWorld().getRegistryManager();
   }

   protected void lerpPosAndRotation(int step, double x, double y, double z, double yaw, double pitch) {
      double d = 1.0 / (double)step;
      double e = MathHelper.lerp(d, this.getX(), x);
      double f = MathHelper.lerp(d, this.getY(), y);
      double g = MathHelper.lerp(d, this.getZ(), z);
      float h = (float)MathHelper.lerpAngleDegrees(d, (double)this.getYaw(), yaw);
      float i = (float)MathHelper.lerp(d, (double)this.getPitch(), pitch);
      this.setPosition(e, f, g);
      this.setRotation(h, i);
   }

   public Random getRandom() {
      return this.random;
   }

   public Vec3d getMovement() {
      LivingEntity var2 = this.getControllingPassenger();
      if (var2 instanceof PlayerEntity playerEntity) {
         if (this.isAlive()) {
            return playerEntity.getMovement();
         }
      }

      return this.getVelocity();
   }

   @Nullable
   public ItemStack getWeaponStack() {
      return null;
   }

   public Optional getLootTableKey() {
      return this.type.getLootTableKey();
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.CUSTOM_NAME);
      this.copyComponentFrom(from, DataComponentTypes.CUSTOM_DATA);
   }

   public final void copyComponentsFrom(ItemStack stack) {
      this.copyComponentsFrom((ComponentsAccess)stack.getComponents());
   }

   @Nullable
   public Object get(ComponentType type) {
      if (type == DataComponentTypes.CUSTOM_NAME) {
         return castComponentValue(type, this.getCustomName());
      } else {
         return type == DataComponentTypes.CUSTOM_DATA ? castComponentValue(type, this.customData) : null;
      }
   }

   @Nullable
   @Contract("_,!null->!null;_,_->_")
   protected static Object castComponentValue(ComponentType type, @Nullable Object value) {
      return value;
   }

   public void setComponent(ComponentType type, Object value) {
      this.setApplicableComponent(type, value);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.CUSTOM_NAME) {
         this.setCustomName((Text)castComponentValue(DataComponentTypes.CUSTOM_NAME, value));
         return true;
      } else if (type == DataComponentTypes.CUSTOM_DATA) {
         this.customData = (NbtComponent)castComponentValue(DataComponentTypes.CUSTOM_DATA, value);
         return true;
      } else {
         return false;
      }
   }

   protected boolean copyComponentFrom(ComponentsAccess from, ComponentType type) {
      Object object = from.get(type);
      return object != null ? this.setApplicableComponent(type, object) : false;
   }

   public ErrorReporter.Context getErrorReporterContext() {
      return new ErrorReporterContext(this);
   }

   static {
      TAG_LIST_CODEC = Codec.STRING.sizeLimitedListOf(1024);
      X_THEN_Z = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);
      Z_THEN_X = ImmutableList.of(Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X);
      NULL_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      renderDistanceMultiplier = 1.0;
      FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
      AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
      CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
      NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
      SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
      NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
      POSE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
      FROZEN_TICKS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
   }

   public static enum RemovalReason {
      KILLED(true, false),
      DISCARDED(true, false),
      UNLOADED_TO_CHUNK(false, true),
      UNLOADED_WITH_PLAYER(false, false),
      CHANGED_DIMENSION(false, false);

      private final boolean destroy;
      private final boolean save;

      private RemovalReason(final boolean destroy, final boolean save) {
         this.destroy = destroy;
         this.save = save;
      }

      public boolean shouldDestroy() {
         return this.destroy;
      }

      public boolean shouldSave() {
         return this.save;
      }

      // $FF: synthetic method
      private static RemovalReason[] method_36603() {
         return new RemovalReason[]{KILLED, DISCARDED, UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION};
      }
   }

   static record QueuedCollisionCheck(Vec3d from, Vec3d to, boolean axisIndependant) {
      final Vec3d from;
      final Vec3d to;
      final boolean axisIndependant;

      QueuedCollisionCheck(Vec3d vec3d, Vec3d vec3d2, boolean bl) {
         this.from = vec3d;
         this.to = vec3d2;
         this.axisIndependant = bl;
      }

      public Vec3d from() {
         return this.from;
      }

      public Vec3d to() {
         return this.to;
      }

      public boolean axisIndependant() {
         return this.axisIndependant;
      }
   }

   public static enum MoveEffect {
      NONE(false, false),
      SOUNDS(true, false),
      EVENTS(false, true),
      ALL(true, true);

      final boolean sounds;
      final boolean events;

      private MoveEffect(final boolean sounds, final boolean events) {
         this.sounds = sounds;
         this.events = events;
      }

      public boolean hasAny() {
         return this.events || this.sounds;
      }

      public boolean emitsGameEvents() {
         return this.events;
      }

      public boolean playsSounds() {
         return this.sounds;
      }

      // $FF: synthetic method
      private static MoveEffect[] method_36602() {
         return new MoveEffect[]{NONE, SOUNDS, EVENTS, ALL};
      }
   }

   @FunctionalInterface
   public interface PositionUpdater {
      void accept(Entity entity, double x, double y, double z);
   }

   static record ErrorReporterContext(Entity entity) implements ErrorReporter.Context {
      ErrorReporterContext(Entity entity) {
         this.entity = entity;
      }

      public String getName() {
         return this.entity.toString();
      }

      public Entity entity() {
         return this.entity;
      }
   }
}
