package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PiglinBruteEntity extends AbstractPiglinEntity {
   private static final int MAX_HEALTH = 50;
   private static final float MOVEMENT_SPEED = 0.35F;
   private static final int ATTACK_DAMAGE = 7;
   private static final double FOLLOW_RANGE = 12.0;
   protected static final ImmutableList SENSOR_TYPES;
   protected static final ImmutableList MEMORY_MODULE_TYPES;

   public PiglinBruteEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.experiencePoints = 20;
   }

   public static DefaultAttributeContainer.Builder createPiglinBruteAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 50.0).add(EntityAttributes.MOVEMENT_SPEED, 0.3499999940395355).add(EntityAttributes.ATTACK_DAMAGE, 7.0).add(EntityAttributes.FOLLOW_RANGE, 12.0);
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      PiglinBruteBrain.setCurrentPosAsHome(this);
      this.initEquipment(world.getRandom(), difficulty);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
      this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
   }

   protected Brain.Profile createBrainProfile() {
      return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return PiglinBruteBrain.create(this, this.createBrainProfile().deserialize(dynamic));
   }

   public Brain getBrain() {
      return super.getBrain();
   }

   public boolean canHunt() {
      return false;
   }

   public boolean canGather(ServerWorld world, ItemStack stack) {
      return stack.isOf(Items.GOLDEN_AXE) ? super.canGather(world, stack) : false;
   }

   protected void mobTick(ServerWorld world) {
      Profiler profiler = Profilers.get();
      profiler.push("piglinBruteBrain");
      this.getBrain().tick(world, this);
      profiler.pop();
      PiglinBruteBrain.tick(this);
      PiglinBruteBrain.playSoundRandomly(this);
      super.mobTick(world);
   }

   public PiglinActivity getActivity() {
      return this.isAttacking() && this.isHoldingTool() ? PiglinActivity.ATTACKING_WITH_MELEE_WEAPON : PiglinActivity.DEFAULT;
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      boolean bl = super.damage(world, source, amount);
      if (bl) {
         Entity var6 = source.getAttacker();
         if (var6 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)var6;
            PiglinBruteBrain.tryRevenge(world, this, livingEntity);
         }
      }

      return bl;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PIGLIN_BRUTE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_PIGLIN_BRUTE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PIGLIN_BRUTE_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
   }

   protected void playAngrySound() {
      this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_ANGRY);
   }

   protected void playZombificationSound() {
      this.playSound(SoundEvents.ENTITY_PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED);
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_BRUTE_SPECIFIC_SENSOR);
      MEMORY_MODULE_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, new MemoryModuleType[]{MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.HOME});
   }
}
