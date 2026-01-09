package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentDropChances;
import net.minecraft.entity.EquipmentHolder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class MobEntity extends LivingEntity implements EquipmentHolder, Leashable, Targeter {
   private static final TrackedData MOB_FLAGS;
   private static final int AI_DISABLED_FLAG = 1;
   private static final int LEFT_HANDED_FLAG = 2;
   private static final int ATTACKING_FLAG = 4;
   protected static final int MINIMUM_DROPPED_EXPERIENCE_PER_EQUIPMENT = 1;
   private static final Vec3i ITEM_PICK_UP_RANGE_EXPANDER;
   private static final List EQUIPMENT_INIT_ORDER;
   public static final float BASE_SPAWN_EQUIPMENT_CHANCE = 0.15F;
   public static final float DEFAULT_CAN_PICKUP_LOOT_CHANCE = 0.55F;
   public static final float BASE_ENCHANTED_ARMOR_CHANCE = 0.5F;
   public static final float BASE_ENCHANTED_MAIN_HAND_EQUIPMENT_CHANCE = 0.25F;
   public static final int field_35039 = 2;
   private static final double ATTACK_RANGE;
   private static final boolean DEFAULT_CAN_PICK_UP_LOOT = false;
   private static final boolean DEFAULT_PERSISTENCE_REQUIRED = false;
   private static final boolean DEFAULT_LEFT_HANDED = false;
   private static final boolean DEFAULT_NO_AI = false;
   protected static final Identifier RANDOM_SPAWN_BONUS_MODIFIER_ID;
   public static final String DROP_CHANCES_KEY = "drop_chances";
   public static final String LEFT_HANDED_KEY = "LeftHanded";
   public static final String CAN_PICK_UP_LOOT_KEY = "CanPickUpLoot";
   public static final String NO_AI_KEY = "NoAI";
   public int ambientSoundChance;
   protected int experiencePoints;
   protected LookControl lookControl;
   protected MoveControl moveControl;
   protected JumpControl jumpControl;
   private final BodyControl bodyControl;
   protected EntityNavigation navigation;
   protected final GoalSelector goalSelector;
   protected final GoalSelector targetSelector;
   @Nullable
   private LivingEntity target;
   private final MobVisibilityCache visibilityCache;
   private EquipmentDropChances equipmentDropChances;
   private boolean canPickUpLoot;
   private boolean persistent;
   private final Map pathfindingPenalties;
   private Optional lootTable;
   private long lootTableSeed;
   @Nullable
   private Leashable.LeashData leashData;
   private BlockPos positionTarget;
   private int positionTargetRange;

   protected MobEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.equipmentDropChances = EquipmentDropChances.DEFAULT;
      this.canPickUpLoot = false;
      this.persistent = false;
      this.pathfindingPenalties = Maps.newEnumMap(PathNodeType.class);
      this.lootTable = Optional.empty();
      this.positionTarget = BlockPos.ORIGIN;
      this.positionTargetRange = -1;
      this.goalSelector = new GoalSelector();
      this.targetSelector = new GoalSelector();
      this.lookControl = new LookControl(this);
      this.moveControl = new MoveControl(this);
      this.jumpControl = new JumpControl(this);
      this.bodyControl = this.createBodyControl();
      this.navigation = this.createNavigation(world);
      this.visibilityCache = new MobVisibilityCache(this);
      if (world instanceof ServerWorld) {
         this.initGoals();
      }

   }

   protected void initGoals() {
   }

   public static DefaultAttributeContainer.Builder createMobAttributes() {
      return LivingEntity.createLivingAttributes().add(EntityAttributes.FOLLOW_RANGE, 16.0);
   }

   protected EntityNavigation createNavigation(World world) {
      return new MobNavigation(this, world);
   }

   protected boolean movesIndependently() {
      return false;
   }

   public float getPathfindingPenalty(PathNodeType nodeType) {
      MobEntity mobEntity2;
      label17: {
         Entity var4 = this.getControllingVehicle();
         if (var4 instanceof MobEntity mobEntity) {
            if (mobEntity.movesIndependently()) {
               mobEntity2 = mobEntity;
               break label17;
            }
         }

         mobEntity2 = this;
      }

      Float float_ = (Float)mobEntity2.pathfindingPenalties.get(nodeType);
      return float_ == null ? nodeType.getDefaultPenalty() : float_;
   }

   public void setPathfindingPenalty(PathNodeType nodeType, float penalty) {
      this.pathfindingPenalties.put(nodeType, penalty);
   }

   public void onStartPathfinding() {
   }

   public void onFinishPathfinding() {
   }

   protected BodyControl createBodyControl() {
      return new BodyControl(this);
   }

   public LookControl getLookControl() {
      return this.lookControl;
   }

   public MoveControl getMoveControl() {
      Entity var2 = this.getControllingVehicle();
      if (var2 instanceof MobEntity mobEntity) {
         return mobEntity.getMoveControl();
      } else {
         return this.moveControl;
      }
   }

   public JumpControl getJumpControl() {
      return this.jumpControl;
   }

   public EntityNavigation getNavigation() {
      Entity var2 = this.getControllingVehicle();
      if (var2 instanceof MobEntity mobEntity) {
         return mobEntity.getNavigation();
      } else {
         return this.navigation;
      }
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      Entity entity = this.getFirstPassenger();
      MobEntity var10000;
      if (!this.isAiDisabled() && entity instanceof MobEntity mobEntity) {
         if (entity.shouldControlVehicles()) {
            var10000 = mobEntity;
            return var10000;
         }
      }

      var10000 = null;
      return var10000;
   }

   public MobVisibilityCache getVisibilityCache() {
      return this.visibilityCache;
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.target;
   }

   @Nullable
   protected final LivingEntity getTargetInBrain() {
      return (LivingEntity)this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object)null);
   }

   public void setTarget(@Nullable LivingEntity target) {
      this.target = target;
   }

   public boolean canTarget(EntityType type) {
      return type != EntityType.GHAST;
   }

   public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
      return false;
   }

   public void onEatingGrass() {
      this.emitGameEvent(GameEvent.EAT);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(MOB_FLAGS, (byte)0);
   }

   public int getMinAmbientSoundDelay() {
      return 80;
   }

   public void playAmbientSound() {
      this.playSound(this.getAmbientSound());
   }

   public void baseTick() {
      super.baseTick();
      Profiler profiler = Profilers.get();
      profiler.push("mobBaseTick");
      if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
         this.resetSoundDelay();
         this.playAmbientSound();
      }

      profiler.pop();
   }

   protected void playHurtSound(DamageSource damageSource) {
      this.resetSoundDelay();
      super.playHurtSound(damageSource);
   }

   private void resetSoundDelay() {
      this.ambientSoundChance = -this.getMinAmbientSoundDelay();
   }

   protected int getExperienceToDrop(ServerWorld world) {
      if (this.experiencePoints > 0) {
         int i = this.experiencePoints;
         Iterator var3 = EquipmentSlot.VALUES.iterator();

         while(var3.hasNext()) {
            EquipmentSlot equipmentSlot = (EquipmentSlot)var3.next();
            if (equipmentSlot.increasesDroppedExperience()) {
               ItemStack itemStack = this.getEquippedStack(equipmentSlot);
               if (!itemStack.isEmpty() && this.equipmentDropChances.get(equipmentSlot) <= 1.0F) {
                  i += 1 + this.random.nextInt(3);
               }
            }
         }

         return i;
      } else {
         return this.experiencePoints;
      }
   }

   public void playSpawnEffects() {
      if (this.getWorld().isClient) {
         this.addDeathParticles();
      } else {
         this.getWorld().sendEntityStatus(this, (byte)20);
      }

   }

   public void handleStatus(byte status) {
      if (status == 20) {
         this.playSpawnEffects();
      } else {
         super.handleStatus(status);
      }

   }

   public void tick() {
      super.tick();
      if (!this.getWorld().isClient && this.age % 5 == 0) {
         this.updateGoalControls();
      }

   }

   protected void updateGoalControls() {
      boolean bl = !(this.getControllingPassenger() instanceof MobEntity);
      boolean bl2 = !(this.getVehicle() instanceof AbstractBoatEntity);
      this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
      this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
      this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
   }

   protected void turnHead(float bodyRotation) {
      this.bodyControl.tick();
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("CanPickUpLoot", this.canPickUpLoot());
      view.putBoolean("PersistenceRequired", this.persistent);
      if (!this.equipmentDropChances.equals(EquipmentDropChances.DEFAULT)) {
         view.put("drop_chances", EquipmentDropChances.CODEC, this.equipmentDropChances);
      }

      this.writeLeashData(view, this.leashData);
      if (this.hasPositionTarget()) {
         view.putInt("home_radius", this.positionTargetRange);
         view.put("home_pos", BlockPos.CODEC, this.positionTarget);
      }

      view.putBoolean("LeftHanded", this.isLeftHanded());
      this.lootTable.ifPresent((registryKey) -> {
         view.put("DeathLootTable", LootTable.TABLE_KEY, registryKey);
      });
      if (this.lootTableSeed != 0L) {
         view.putLong("DeathLootTableSeed", this.lootTableSeed);
      }

      if (this.isAiDisabled()) {
         view.putBoolean("NoAI", this.isAiDisabled());
      }

   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setCanPickUpLoot(view.getBoolean("CanPickUpLoot", false));
      this.persistent = view.getBoolean("PersistenceRequired", false);
      this.equipmentDropChances = (EquipmentDropChances)view.read("drop_chances", EquipmentDropChances.CODEC).orElse(EquipmentDropChances.DEFAULT);
      this.readLeashData(view);
      this.positionTargetRange = view.getInt("home_radius", -1);
      if (this.positionTargetRange >= 0) {
         this.positionTarget = (BlockPos)view.read("home_pos", BlockPos.CODEC).orElse(BlockPos.ORIGIN);
      }

      this.setLeftHanded(view.getBoolean("LeftHanded", false));
      this.lootTable = view.read("DeathLootTable", LootTable.TABLE_KEY);
      this.lootTableSeed = view.getLong("DeathLootTableSeed", 0L);
      this.setAiDisabled(view.getBoolean("NoAI", false));
   }

   protected void dropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer) {
      super.dropLoot(world, damageSource, causedByPlayer);
      this.lootTable = Optional.empty();
   }

   public final Optional getLootTableKey() {
      return this.lootTable.isPresent() ? this.lootTable : super.getLootTableKey();
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setForwardSpeed(float forwardSpeed) {
      this.forwardSpeed = forwardSpeed;
   }

   public void setUpwardSpeed(float upwardSpeed) {
      this.upwardSpeed = upwardSpeed;
   }

   public void setSidewaysSpeed(float sidewaysSpeed) {
      this.sidewaysSpeed = sidewaysSpeed;
   }

   public void setMovementSpeed(float movementSpeed) {
      super.setMovementSpeed(movementSpeed);
      this.setForwardSpeed(movementSpeed);
   }

   public void stopMovement() {
      this.getNavigation().stop();
      this.setSidewaysSpeed(0.0F);
      this.setUpwardSpeed(0.0F);
      this.setMovementSpeed(0.0F);
      this.setVelocity(0.0, 0.0, 0.0);
      this.resetLeashMomentum();
   }

   public void tickMovement() {
      super.tickMovement();
      Profiler profiler = Profilers.get();
      profiler.push("looting");
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         if (this.canPickUpLoot() && this.isAlive() && !this.dead && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            Vec3i vec3i = this.getItemPickUpRangeExpander();
            List list = this.getWorld().getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ()));
            Iterator var5 = list.iterator();

            while(var5.hasNext()) {
               ItemEntity itemEntity = (ItemEntity)var5.next();
               if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.canGather(serverWorld, itemEntity.getStack())) {
                  this.loot(serverWorld, itemEntity);
               }
            }
         }
      }

      profiler.pop();
   }

   protected Vec3i getItemPickUpRangeExpander() {
      return ITEM_PICK_UP_RANGE_EXPANDER;
   }

   protected void loot(ServerWorld world, ItemEntity itemEntity) {
      ItemStack itemStack = itemEntity.getStack();
      ItemStack itemStack2 = this.tryEquip(world, itemStack.copy());
      if (!itemStack2.isEmpty()) {
         this.triggerItemPickedUpByEntityCriteria(itemEntity);
         this.sendPickup(itemEntity, itemStack2.getCount());
         itemStack.decrement(itemStack2.getCount());
         if (itemStack.isEmpty()) {
            itemEntity.discard();
         }
      }

   }

   public ItemStack tryEquip(ServerWorld world, ItemStack stack) {
      EquipmentSlot equipmentSlot = this.getPreferredEquipmentSlot(stack);
      if (!this.canEquip(stack, equipmentSlot)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStack = this.getEquippedStack(equipmentSlot);
         boolean bl = this.prefersNewEquipment(stack, itemStack, equipmentSlot);
         if (equipmentSlot.isArmorSlot() && !bl) {
            equipmentSlot = EquipmentSlot.MAINHAND;
            itemStack = this.getEquippedStack(equipmentSlot);
            bl = itemStack.isEmpty();
         }

         if (bl && this.canPickupItem(stack)) {
            double d = (double)this.equipmentDropChances.get(equipmentSlot);
            if (!itemStack.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1F, 0.0F) < d) {
               this.dropStack(world, itemStack);
            }

            ItemStack itemStack2 = equipmentSlot.split(stack);
            this.equipLootStack(equipmentSlot, itemStack2);
            return itemStack2;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   protected void equipLootStack(EquipmentSlot slot, ItemStack stack) {
      this.equipStack(slot, stack);
      this.setDropGuaranteed(slot);
      this.persistent = true;
   }

   protected boolean canRemoveSaddle(PlayerEntity player) {
      return !this.hasPassengers();
   }

   public void setDropGuaranteed(EquipmentSlot slot) {
      this.equipmentDropChances = this.equipmentDropChances.withGuaranteed(slot);
   }

   protected boolean prefersNewEquipment(ItemStack newStack, ItemStack currentStack, EquipmentSlot slot) {
      if (currentStack.isEmpty()) {
         return true;
      } else if (slot.isArmorSlot()) {
         return this.prefersNewArmor(newStack, currentStack, slot);
      } else {
         return slot == EquipmentSlot.MAINHAND ? this.prefersNewWeapon(newStack, currentStack, slot) : false;
      }
   }

   private boolean prefersNewArmor(ItemStack newStack, ItemStack currentStack, EquipmentSlot slot) {
      if (EnchantmentHelper.hasAnyEnchantmentsWith(currentStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)) {
         return false;
      } else {
         double d = this.getAttributeValueWithStack(newStack, EntityAttributes.ARMOR, slot);
         double e = this.getAttributeValueWithStack(currentStack, EntityAttributes.ARMOR, slot);
         double f = this.getAttributeValueWithStack(newStack, EntityAttributes.ARMOR_TOUGHNESS, slot);
         double g = this.getAttributeValueWithStack(currentStack, EntityAttributes.ARMOR_TOUGHNESS, slot);
         if (d != e) {
            return d > e;
         } else if (f != g) {
            return f > g;
         } else {
            return this.prefersNewDamageableItem(newStack, currentStack);
         }
      }
   }

   private boolean prefersNewWeapon(ItemStack newStack, ItemStack currentStack, EquipmentSlot slot) {
      TagKey tagKey = this.getPreferredWeapons();
      if (tagKey != null) {
         if (currentStack.isIn(tagKey) && !newStack.isIn(tagKey)) {
            return false;
         }

         if (!currentStack.isIn(tagKey) && newStack.isIn(tagKey)) {
            return true;
         }
      }

      double d = this.getAttributeValueWithStack(newStack, EntityAttributes.ATTACK_DAMAGE, slot);
      double e = this.getAttributeValueWithStack(currentStack, EntityAttributes.ATTACK_DAMAGE, slot);
      if (d != e) {
         return d > e;
      } else {
         return this.prefersNewDamageableItem(newStack, currentStack);
      }
   }

   private double getAttributeValueWithStack(ItemStack stack, RegistryEntry attribute, EquipmentSlot slot) {
      double d = this.getAttributes().hasAttribute(attribute) ? this.getAttributeBaseValue(attribute) : 0.0;
      AttributeModifiersComponent attributeModifiersComponent = (AttributeModifiersComponent)stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
      return attributeModifiersComponent.applyOperations(d, slot);
   }

   public boolean prefersNewDamageableItem(ItemStack newStack, ItemStack oldStack) {
      Set set = ((ItemEnchantmentsComponent)oldStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)).getEnchantmentEntries();
      Set set2 = ((ItemEnchantmentsComponent)newStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)).getEnchantmentEntries();
      if (set2.size() != set.size()) {
         return set2.size() > set.size();
      } else {
         int i = newStack.getDamage();
         int j = oldStack.getDamage();
         if (i != j) {
            return i < j;
         } else {
            return newStack.contains(DataComponentTypes.CUSTOM_NAME) && !oldStack.contains(DataComponentTypes.CUSTOM_NAME);
         }
      }
   }

   public boolean canPickupItem(ItemStack stack) {
      return true;
   }

   public boolean canGather(ServerWorld world, ItemStack stack) {
      return this.canPickupItem(stack);
   }

   @Nullable
   public TagKey getPreferredWeapons() {
      return null;
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return true;
   }

   public boolean cannotDespawn() {
      return this.hasVehicle();
   }

   protected boolean isDisallowedInPeaceful() {
      return false;
   }

   public void checkDespawn() {
      if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
         this.discard();
      } else if (!this.isPersistent() && !this.cannotDespawn()) {
         Entity entity = this.getWorld().getClosestPlayer(this, -1.0);
         if (entity != null) {
            double d = entity.squaredDistanceTo((Entity)this);
            int i = this.getType().getSpawnGroup().getImmediateDespawnRange();
            int j = i * i;
            if (d > (double)j && this.canImmediatelyDespawn(d)) {
               this.discard();
            }

            int k = this.getType().getSpawnGroup().getDespawnStartRange();
            int l = k * k;
            if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > (double)l && this.canImmediatelyDespawn(d)) {
               this.discard();
            } else if (d < (double)l) {
               this.despawnCounter = 0;
            }
         }

      } else {
         this.despawnCounter = 0;
      }
   }

   protected final void tickNewAi() {
      ++this.despawnCounter;
      Profiler profiler = Profilers.get();
      profiler.push("sensing");
      this.visibilityCache.clear();
      profiler.pop();
      int i = this.age + this.getId();
      if (i % 2 != 0 && this.age > 1) {
         profiler.push("targetSelector");
         this.targetSelector.tickGoals(false);
         profiler.pop();
         profiler.push("goalSelector");
         this.goalSelector.tickGoals(false);
         profiler.pop();
      } else {
         profiler.push("targetSelector");
         this.targetSelector.tick();
         profiler.pop();
         profiler.push("goalSelector");
         this.goalSelector.tick();
         profiler.pop();
      }

      profiler.push("navigation");
      this.navigation.tick();
      profiler.pop();
      profiler.push("mob tick");
      this.mobTick((ServerWorld)this.getWorld());
      profiler.pop();
      profiler.push("controls");
      profiler.push("move");
      this.moveControl.tick();
      profiler.swap("look");
      this.lookControl.tick();
      profiler.swap("jump");
      this.jumpControl.tick();
      profiler.pop();
      profiler.pop();
      this.sendAiDebugData();
   }

   protected void sendAiDebugData() {
      DebugInfoSender.sendGoalSelector(this.getWorld(), this, this.goalSelector);
   }

   protected void mobTick(ServerWorld world) {
   }

   public int getMaxLookPitchChange() {
      return 40;
   }

   public int getMaxHeadRotation() {
      return 75;
   }

   protected void clampHeadYaw() {
      float f = (float)this.getMaxHeadRotation();
      float g = this.getHeadYaw();
      float h = MathHelper.wrapDegrees(this.bodyYaw - g);
      float i = MathHelper.clamp(MathHelper.wrapDegrees(this.bodyYaw - g), -f, f);
      float j = g + h - i;
      this.setHeadYaw(j);
   }

   public int getMaxLookYawChange() {
      return 10;
   }

   public void lookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
      double d = targetEntity.getX() - this.getX();
      double e = targetEntity.getZ() - this.getZ();
      double f;
      if (targetEntity instanceof LivingEntity livingEntity) {
         f = livingEntity.getEyeY() - this.getEyeY();
      } else {
         f = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - this.getEyeY();
      }

      double g = Math.sqrt(d * d + e * e);
      float h = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0F;
      float i = (float)(-(MathHelper.atan2(f, g) * 57.2957763671875));
      this.setPitch(this.changeAngle(this.getPitch(), i, maxPitchChange));
      this.setYaw(this.changeAngle(this.getYaw(), h, maxYawChange));
   }

   private float changeAngle(float from, float to, float max) {
      float f = MathHelper.wrapDegrees(to - from);
      if (f > max) {
         f = max;
      }

      if (f < -max) {
         f = -max;
      }

      return from + f;
   }

   public static boolean canMobSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      BlockPos blockPos = pos.down();
      return SpawnReason.isAnySpawner(spawnReason) || world.getBlockState(blockPos).allowsSpawning(world, blockPos, type);
   }

   public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
      return true;
   }

   public boolean canSpawn(WorldView world) {
      return !world.containsFluid(this.getBoundingBox()) && world.doesNotIntersectEntities(this);
   }

   public int getLimitPerChunk() {
      return 4;
   }

   public boolean spawnsTooManyForEachTry(int count) {
      return false;
   }

   public int getSafeFallDistance() {
      if (this.getTarget() == null) {
         return this.getSafeFallDistance(0.0F);
      } else {
         int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         i -= (3 - this.getWorld().getDifficulty().getId()) * 4;
         if (i < 0) {
            i = 0;
         }

         return this.getSafeFallDistance((float)i);
      }
   }

   public ItemStack getBodyArmor() {
      return this.getEquippedStack(EquipmentSlot.BODY);
   }

   public boolean hasSaddleEquipped() {
      return this.isWearing(EquipmentSlot.SADDLE);
   }

   public boolean isWearingBodyArmor() {
      return this.isWearing(EquipmentSlot.BODY);
   }

   private boolean isWearing(EquipmentSlot slot) {
      return this.hasStackEquipped(slot) && this.canEquip(this.getEquippedStack(slot), slot);
   }

   public void equipBodyArmor(ItemStack stack) {
      this.equipLootStack(EquipmentSlot.BODY, stack);
   }

   public Inventory createEquipmentInventory(final EquipmentSlot slot) {
      return new SingleStackInventory() {
         public ItemStack getStack() {
            return MobEntity.this.getEquippedStack(slot);
         }

         public void setStack(ItemStack stack) {
            MobEntity.this.equipStack(slot, stack);
            if (!stack.isEmpty()) {
               MobEntity.this.setDropGuaranteed(slot);
               MobEntity.this.setPersistent();
            }

         }

         public void markDirty() {
         }

         public boolean canPlayerUse(PlayerEntity player) {
            return player.getVehicle() == MobEntity.this || player.canInteractWithEntity(MobEntity.this, 4.0);
         }
      };
   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
      super.dropEquipment(world, source, causedByPlayer);
      Iterator var4 = EquipmentSlot.VALUES.iterator();

      while(true) {
         EquipmentSlot equipmentSlot;
         ItemStack itemStack;
         float f;
         boolean bl;
         do {
            do {
               do {
                  do {
                     if (!var4.hasNext()) {
                        return;
                     }

                     equipmentSlot = (EquipmentSlot)var4.next();
                     itemStack = this.getEquippedStack(equipmentSlot);
                     f = this.equipmentDropChances.get(equipmentSlot);
                  } while(f == 0.0F);

                  bl = this.equipmentDropChances.dropsExactly(equipmentSlot);
                  Entity var11 = source.getAttacker();
                  if (var11 instanceof LivingEntity livingEntity) {
                     World var12 = this.getWorld();
                     if (var12 instanceof ServerWorld serverWorld) {
                        f = EnchantmentHelper.getEquipmentDropChance(serverWorld, livingEntity, source, f);
                     }
                  }
               } while(itemStack.isEmpty());
            } while(EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_EQUIPMENT_DROP));
         } while(!causedByPlayer && !bl);

         if (this.random.nextFloat() < f) {
            if (!bl && itemStack.isDamageable()) {
               itemStack.setDamage(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
            }

            this.dropStack(world, itemStack);
            this.equipStack(equipmentSlot, ItemStack.EMPTY);
         }
      }
   }

   public EquipmentDropChances getEquipmentDropChances() {
      return this.equipmentDropChances;
   }

   public void dropAllForeignEquipment(ServerWorld world) {
      this.dropForeignEquipment(world, (stack) -> {
         return true;
      });
   }

   public Set dropForeignEquipment(ServerWorld world, Predicate dropPredicate) {
      Set set = new HashSet();
      Iterator var4 = EquipmentSlot.VALUES.iterator();

      while(var4.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var4.next();
         ItemStack itemStack = this.getEquippedStack(equipmentSlot);
         if (!itemStack.isEmpty()) {
            if (!dropPredicate.test(itemStack)) {
               set.add(equipmentSlot);
            } else if (this.equipmentDropChances.dropsExactly(equipmentSlot)) {
               this.equipStack(equipmentSlot, ItemStack.EMPTY);
               this.dropStack(world, itemStack);
            }
         }
      }

      return set;
   }

   private LootWorldContext createEquipmentLootParameters(ServerWorld world) {
      return (new LootWorldContext.Builder(world)).add(LootContextParameters.ORIGIN, this.getPos()).add(LootContextParameters.THIS_ENTITY, this).build(LootContextTypes.EQUIPMENT);
   }

   public void setEquipmentFromTable(EquipmentTable equipmentTable) {
      this.setEquipmentFromTable(equipmentTable.lootTable(), equipmentTable.slotDropChances());
   }

   public void setEquipmentFromTable(RegistryKey lootTable, Map slotDropChances) {
      World var4 = this.getWorld();
      if (var4 instanceof ServerWorld serverWorld) {
         this.setEquipmentFromTable(lootTable, this.createEquipmentLootParameters(serverWorld), slotDropChances);
      }

   }

   protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
      if (random.nextFloat() < 0.15F * localDifficulty.getClampedLocalDifficulty()) {
         int i = random.nextInt(2);
         float f = this.getWorld().getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
         if (random.nextFloat() < 0.095F) {
            ++i;
         }

         if (random.nextFloat() < 0.095F) {
            ++i;
         }

         if (random.nextFloat() < 0.095F) {
            ++i;
         }

         boolean bl = true;
         Iterator var6 = EQUIPMENT_INIT_ORDER.iterator();

         while(var6.hasNext()) {
            EquipmentSlot equipmentSlot = (EquipmentSlot)var6.next();
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            if (!bl && random.nextFloat() < f) {
               break;
            }

            bl = false;
            if (itemStack.isEmpty()) {
               Item item = getEquipmentForSlot(equipmentSlot, i);
               if (item != null) {
                  this.equipStack(equipmentSlot, new ItemStack(item));
               }
            }
         }
      }

   }

   @Nullable
   public static Item getEquipmentForSlot(EquipmentSlot equipmentSlot, int equipmentLevel) {
      switch (equipmentSlot) {
         case HEAD:
            if (equipmentLevel == 0) {
               return Items.LEATHER_HELMET;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_HELMET;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_HELMET;
            } else if (equipmentLevel == 3) {
               return Items.IRON_HELMET;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_HELMET;
            }
         case CHEST:
            if (equipmentLevel == 0) {
               return Items.LEATHER_CHESTPLATE;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_CHESTPLATE;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_CHESTPLATE;
            } else if (equipmentLevel == 3) {
               return Items.IRON_CHESTPLATE;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_CHESTPLATE;
            }
         case LEGS:
            if (equipmentLevel == 0) {
               return Items.LEATHER_LEGGINGS;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_LEGGINGS;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_LEGGINGS;
            } else if (equipmentLevel == 3) {
               return Items.IRON_LEGGINGS;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_LEGGINGS;
            }
         case FEET:
            if (equipmentLevel == 0) {
               return Items.LEATHER_BOOTS;
            } else if (equipmentLevel == 1) {
               return Items.GOLDEN_BOOTS;
            } else if (equipmentLevel == 2) {
               return Items.CHAINMAIL_BOOTS;
            } else if (equipmentLevel == 3) {
               return Items.IRON_BOOTS;
            } else if (equipmentLevel == 4) {
               return Items.DIAMOND_BOOTS;
            }
         default:
            return null;
      }
   }

   protected void updateEnchantments(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty) {
      this.enchantMainHandItem(world, random, localDifficulty);
      Iterator var4 = EquipmentSlot.VALUES.iterator();

      while(var4.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var4.next();
         if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            this.enchantEquipment(world, random, equipmentSlot, localDifficulty);
         }
      }

   }

   protected void enchantMainHandItem(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty) {
      this.enchantEquipment(world, EquipmentSlot.MAINHAND, random, 0.25F, localDifficulty);
   }

   protected void enchantEquipment(ServerWorldAccess world, Random random, EquipmentSlot slot, LocalDifficulty localDifficulty) {
      this.enchantEquipment(world, slot, random, 0.5F, localDifficulty);
   }

   private void enchantEquipment(ServerWorldAccess world, EquipmentSlot slot, Random random, float power, LocalDifficulty localDifficulty) {
      ItemStack itemStack = this.getEquippedStack(slot);
      if (!itemStack.isEmpty() && random.nextFloat() < power * localDifficulty.getClampedLocalDifficulty()) {
         EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(), EnchantmentProviders.MOB_SPAWN_EQUIPMENT, localDifficulty, random);
         this.equipStack(slot, itemStack);
      }

   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Random random = world.getRandom();
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE));
      if (!entityAttributeInstance.hasModifier(RANDOM_SPAWN_BONUS_MODIFIER_ID)) {
         entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(RANDOM_SPAWN_BONUS_MODIFIER_ID, random.nextTriangular(0.0, 0.11485000000000001), EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
      }

      this.setLeftHanded(random.nextFloat() < 0.05F);
      return entityData;
   }

   public void setPersistent() {
      this.persistent = true;
   }

   public void setEquipmentDropChance(EquipmentSlot slot, float dropChance) {
      this.equipmentDropChances = this.equipmentDropChances.withChance(slot, dropChance);
   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean canPickUpLoot) {
      this.canPickUpLoot = canPickUpLoot;
   }

   protected boolean canDispenserEquipSlot(EquipmentSlot slot) {
      return this.canPickUpLoot();
   }

   public boolean isPersistent() {
      return this.persistent;
   }

   public final ActionResult interact(PlayerEntity player, Hand hand) {
      if (!this.isAlive()) {
         return ActionResult.PASS;
      } else {
         ActionResult actionResult = this.interactWithItem(player, hand);
         if (actionResult.isAccepted()) {
            this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
            return actionResult;
         } else {
            ActionResult actionResult2 = super.interact(player, hand);
            if (actionResult2 != ActionResult.PASS) {
               return actionResult2;
            } else {
               actionResult = this.interactMob(player, hand);
               if (actionResult.isAccepted()) {
                  this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
                  return actionResult;
               } else {
                  return ActionResult.PASS;
               }
            }
         }
      }
   }

   private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isOf(Items.NAME_TAG)) {
         ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
         if (actionResult.isAccepted()) {
            return actionResult;
         }
      }

      if (itemStack.getItem() instanceof SpawnEggItem) {
         if (this.getWorld() instanceof ServerWorld) {
            SpawnEggItem spawnEggItem = (SpawnEggItem)itemStack.getItem();
            Optional optional = spawnEggItem.spawnBaby(player, this, this.getType(), (ServerWorld)this.getWorld(), this.getPos(), itemStack);
            optional.ifPresent((entity) -> {
               this.onPlayerSpawnedChild(player, entity);
            });
            if (optional.isEmpty()) {
               return ActionResult.PASS;
            }
         }

         return ActionResult.SUCCESS_SERVER;
      } else {
         return ActionResult.PASS;
      }
   }

   protected void onPlayerSpawnedChild(PlayerEntity player, MobEntity child) {
   }

   protected ActionResult interactMob(PlayerEntity player, Hand hand) {
      return ActionResult.PASS;
   }

   public boolean isInPositionTargetRange() {
      return this.isInPositionTargetRange(this.getBlockPos());
   }

   public boolean isInPositionTargetRange(BlockPos pos) {
      if (this.positionTargetRange == -1) {
         return true;
      } else {
         return this.positionTarget.getSquaredDistance(pos) < (double)(this.positionTargetRange * this.positionTargetRange);
      }
   }

   public boolean isInPositionTargetRange(Vec3d pos) {
      if (this.positionTargetRange == -1) {
         return true;
      } else {
         return this.positionTarget.getSquaredDistance(pos) < (double)(this.positionTargetRange * this.positionTargetRange);
      }
   }

   public void setPositionTarget(BlockPos target, int range) {
      this.positionTarget = target;
      this.positionTargetRange = range;
   }

   public BlockPos getPositionTarget() {
      return this.positionTarget;
   }

   public int getPositionTargetRange() {
      return this.positionTargetRange;
   }

   public void clearPositionTarget() {
      this.positionTargetRange = -1;
   }

   public boolean hasPositionTarget() {
      return this.positionTargetRange != -1;
   }

   @Nullable
   public MobEntity convertTo(EntityType entityType, EntityConversionContext context, SpawnReason reason, EntityConversionContext.Finalizer finalizer) {
      if (this.isRemoved()) {
         return null;
      } else {
         MobEntity mobEntity = (MobEntity)entityType.create(this.getWorld(), reason);
         if (mobEntity == null) {
            return null;
         } else {
            context.type().setUpNewEntity(this, mobEntity, context);
            finalizer.finalizeConversion(mobEntity);
            World var7 = this.getWorld();
            if (var7 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var7;
               serverWorld.spawnEntity(mobEntity);
            }

            if (context.type().shouldDiscardOldEntity()) {
               this.discard();
            }

            return mobEntity;
         }
      }
   }

   @Nullable
   public MobEntity convertTo(EntityType entityType, EntityConversionContext context, EntityConversionContext.Finalizer finalizer) {
      return this.convertTo(entityType, context, SpawnReason.CONVERSION, finalizer);
   }

   @Nullable
   public Leashable.LeashData getLeashData() {
      return this.leashData;
   }

   private void resetLeashMomentum() {
      if (this.leashData != null) {
         this.leashData.momentum = 0.0;
      }

   }

   public void setLeashData(@Nullable Leashable.LeashData leashData) {
      this.leashData = leashData;
   }

   public void onLeashRemoved() {
      if (this.getLeashData() == null) {
         this.clearPositionTarget();
      }

   }

   public void snapLongLeash() {
      Leashable.super.snapLongLeash();
      this.goalSelector.disableControl(Goal.Control.MOVE);
   }

   public boolean canBeLeashed() {
      return !(this instanceof Monster);
   }

   public boolean startRiding(Entity entity, boolean force) {
      boolean bl = super.startRiding(entity, force);
      if (bl && this.isLeashed()) {
         this.detachLeash();
      }

      return bl;
   }

   public boolean canActVoluntarily() {
      return super.canActVoluntarily() && !this.isAiDisabled();
   }

   public void setAiDisabled(boolean aiDisabled) {
      byte b = (Byte)this.dataTracker.get(MOB_FLAGS);
      this.dataTracker.set(MOB_FLAGS, aiDisabled ? (byte)(b | 1) : (byte)(b & -2));
   }

   public void setLeftHanded(boolean leftHanded) {
      byte b = (Byte)this.dataTracker.get(MOB_FLAGS);
      this.dataTracker.set(MOB_FLAGS, leftHanded ? (byte)(b | 2) : (byte)(b & -3));
   }

   public void setAttacking(boolean attacking) {
      byte b = (Byte)this.dataTracker.get(MOB_FLAGS);
      this.dataTracker.set(MOB_FLAGS, attacking ? (byte)(b | 4) : (byte)(b & -5));
   }

   public boolean isAiDisabled() {
      return ((Byte)this.dataTracker.get(MOB_FLAGS) & 1) != 0;
   }

   public boolean isLeftHanded() {
      return ((Byte)this.dataTracker.get(MOB_FLAGS) & 2) != 0;
   }

   public boolean isAttacking() {
      return ((Byte)this.dataTracker.get(MOB_FLAGS) & 4) != 0;
   }

   public void setBaby(boolean baby) {
   }

   public Arm getMainArm() {
      return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
   }

   public boolean isInAttackRange(LivingEntity entity) {
      return this.getAttackBox().intersects(entity.getHitbox());
   }

   protected Box getAttackBox() {
      Entity entity = this.getVehicle();
      Box box3;
      if (entity != null) {
         Box box = entity.getBoundingBox();
         Box box2 = this.getBoundingBox();
         box3 = new Box(Math.min(box2.minX, box.minX), box2.minY, Math.min(box2.minZ, box.minZ), Math.max(box2.maxX, box.maxX), box2.maxY, Math.max(box2.maxZ, box.maxZ));
      } else {
         box3 = this.getBoundingBox();
      }

      return box3.expand(ATTACK_RANGE, 0.0, ATTACK_RANGE);
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      float f = (float)this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
      ItemStack itemStack = this.getWeaponStack();
      DamageSource damageSource = (DamageSource)Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().mobAttack(this));
      f = EnchantmentHelper.getDamage(world, itemStack, target, damageSource, f);
      f += itemStack.getItem().getBonusAttackDamage(target, f, damageSource);
      boolean bl = target.damage(world, damageSource, f);
      if (bl) {
         float g = this.getAttackKnockbackAgainst(target, damageSource);
         LivingEntity livingEntity;
         if (g > 0.0F && target instanceof LivingEntity) {
            livingEntity = (LivingEntity)target;
            livingEntity.takeKnockback((double)(g * 0.5F), (double)MathHelper.sin(this.getYaw() * 0.017453292F), (double)(-MathHelper.cos(this.getYaw() * 0.017453292F)));
            this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
         }

         if (target instanceof LivingEntity) {
            livingEntity = (LivingEntity)target;
            itemStack.postHit(livingEntity, this);
         }

         EnchantmentHelper.onTargetDamaged(world, target, damageSource);
         this.onAttacking(target);
         this.playAttackSound();
      }

      return bl;
   }

   protected void playAttackSound() {
   }

   protected boolean isAffectedByDaylight() {
      if (this.getWorld().isDay() && !this.getWorld().isClient) {
         float f = this.getBrightnessAtEyes();
         BlockPos blockPos = BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ());
         boolean bl = this.isTouchingWaterOrRain() || this.inPowderSnow || this.wasInPowderSnow;
         if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !bl && this.getWorld().isSkyVisible(blockPos)) {
            return true;
         }
      }

      return false;
   }

   protected void swimUpward(TagKey fluid) {
      if (this.getNavigation().canSwim()) {
         super.swimUpward(fluid);
      } else {
         this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
      }

   }

   @VisibleForTesting
   public void clearGoalsAndTasks() {
      this.clearGoals((goal) -> {
         return true;
      });
      this.getBrain().clear();
   }

   public void clearGoals(Predicate predicate) {
      this.goalSelector.clear(predicate);
   }

   protected void removeFromDimension() {
      super.removeFromDimension();
      Iterator var1 = EquipmentSlot.VALUES.iterator();

      while(var1.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var1.next();
         ItemStack itemStack = this.getEquippedStack(equipmentSlot);
         if (!itemStack.isEmpty()) {
            itemStack.setCount(0);
         }
      }

   }

   @Nullable
   public ItemStack getPickBlockStack() {
      SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(this.getType());
      return spawnEggItem == null ? null : new ItemStack(spawnEggItem);
   }

   protected void updateAttribute(RegistryEntry attribute) {
      super.updateAttribute(attribute);
      if (attribute.matches(EntityAttributes.FOLLOW_RANGE) || attribute.matches(EntityAttributes.TEMPT_RANGE)) {
         this.getNavigation().updateRange();
      }

   }

   static {
      MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
      ITEM_PICK_UP_RANGE_EXPANDER = new Vec3i(1, 0, 1);
      EQUIPMENT_INIT_ORDER = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
      ATTACK_RANGE = Math.sqrt(2.0399999618530273) - 0.6000000238418579;
      RANDOM_SPAWN_BONUS_MODIFIER_ID = Identifier.ofVanilla("random_spawn_bonus");
   }
}
