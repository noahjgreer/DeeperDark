package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class SheepEntity extends AnimalEntity implements Shearable {
   private static final int MAX_GRASS_TIMER = 40;
   private static final TrackedData COLOR;
   private static final DyeColor DEFAULT_COLOR;
   private static final boolean DEFAULT_SHEARED = false;
   private int eatGrassTimer;
   private EatGrassGoal eatGrassGoal;

   public SheepEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initGoals() {
      this.eatGrassGoal = new EatGrassGoal(this);
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
      this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
      this.goalSelector.add(3, new TemptGoal(this, 1.1, (stack) -> {
         return stack.isIn(ItemTags.SHEEP_FOOD);
      }, false));
      this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.add(5, this.eatGrassGoal);
      this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
      this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.add(8, new LookAroundGoal(this));
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.SHEEP_FOOD);
   }

   protected void mobTick(ServerWorld world) {
      this.eatGrassTimer = this.eatGrassGoal.getTimer();
      super.mobTick(world);
   }

   public void tickMovement() {
      if (this.getWorld().isClient) {
         this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
      }

      super.tickMovement();
   }

   public static DefaultAttributeContainer.Builder createSheepAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 8.0).add(EntityAttributes.MOVEMENT_SPEED, 0.23000000417232513);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(COLOR, (byte)0);
   }

   public void handleStatus(byte status) {
      if (status == 10) {
         this.eatGrassTimer = 40;
      } else {
         super.handleStatus(status);
      }

   }

   public float getNeckAngle(float tickProgress) {
      if (this.eatGrassTimer <= 0) {
         return 0.0F;
      } else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
         return 1.0F;
      } else {
         return this.eatGrassTimer < 4 ? ((float)this.eatGrassTimer - tickProgress) / 4.0F : -((float)(this.eatGrassTimer - 40) - tickProgress) / 4.0F;
      }
   }

   public float getHeadAngle(float tickProgress) {
      if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
         float f = ((float)(this.eatGrassTimer - 4) - tickProgress) / 32.0F;
         return 0.62831855F + 0.21991149F * MathHelper.sin(f * 28.7F);
      } else {
         return this.eatGrassTimer > 0 ? 0.62831855F : this.getLerpedPitch(tickProgress) * 0.017453292F;
      }
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isOf(Items.SHEARS)) {
         World var5 = this.getWorld();
         if (var5 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var5;
            if (this.isShearable()) {
               this.sheared(serverWorld, SoundCategory.PLAYERS, itemStack);
               this.emitGameEvent(GameEvent.SHEAR, player);
               itemStack.damage(1, player, (EquipmentSlot)getSlotForHand(hand));
               return ActionResult.SUCCESS_SERVER;
            }
         }

         return ActionResult.CONSUME;
      } else {
         return super.interactMob(player, hand);
      }
   }

   public void sheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
      world.playSoundFromEntity((Entity)null, this, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
      this.forEachShearedItem(world, LootTables.SHEEP_SHEARING, shears, (worldx, stack) -> {
         for(int i = 0; i < stack.getCount(); ++i) {
            ItemEntity itemEntity = this.dropStack(worldx, stack.copyWithCount(1), 1.0F);
            if (itemEntity != null) {
               itemEntity.setVelocity(itemEntity.getVelocity().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
            }
         }

      });
      this.setSheared(true);
   }

   public boolean isShearable() {
      return this.isAlive() && !this.isSheared() && !this.isBaby();
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("Sheared", this.isSheared());
      view.put("Color", DyeColor.INDEX_CODEC, this.getColor());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setSheared(view.getBoolean("Sheared", false));
      this.setColor((DyeColor)view.read("Color", DyeColor.INDEX_CODEC).orElse(DEFAULT_COLOR));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SHEEP_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
   }

   public DyeColor getColor() {
      return DyeColor.byIndex((Byte)this.dataTracker.get(COLOR) & 15);
   }

   public void setColor(DyeColor color) {
      byte b = (Byte)this.dataTracker.get(COLOR);
      this.dataTracker.set(COLOR, (byte)(b & 240 | color.getIndex() & 15));
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.SHEEP_COLOR ? castComponentValue(type, this.getColor()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.SHEEP_COLOR);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.SHEEP_COLOR) {
         this.setColor((DyeColor)castComponentValue(DataComponentTypes.SHEEP_COLOR, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   public boolean isSheared() {
      return ((Byte)this.dataTracker.get(COLOR) & 16) != 0;
   }

   public void setSheared(boolean sheared) {
      byte b = (Byte)this.dataTracker.get(COLOR);
      if (sheared) {
         this.dataTracker.set(COLOR, (byte)(b | 16));
      } else {
         this.dataTracker.set(COLOR, (byte)(b & -17));
      }

   }

   public static DyeColor selectSpawnColor(ServerWorldAccess world, BlockPos pos) {
      RegistryEntry registryEntry = world.getBiome(pos);
      return SheepColors.select(registryEntry, world.getRandom());
   }

   @Nullable
   public SheepEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      SheepEntity sheepEntity = (SheepEntity)EntityType.SHEEP.create(serverWorld, SpawnReason.BREEDING);
      if (sheepEntity != null) {
         DyeColor dyeColor = this.getColor();
         DyeColor dyeColor2 = ((SheepEntity)passiveEntity).getColor();
         sheepEntity.setColor(DyeColor.mixColors(serverWorld, dyeColor, dyeColor2));
      }

      return sheepEntity;
   }

   public void onEatingGrass() {
      super.onEatingGrass();
      this.setSheared(false);
      if (this.isBaby()) {
         this.growUp(60);
      }

   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      this.setColor(selectSpawnColor(world, this.getBlockPos()));
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      COLOR = DataTracker.registerData(SheepEntity.class, TrackedDataHandlerRegistry.BYTE);
      DEFAULT_COLOR = DyeColor.WHITE;
   }
}
