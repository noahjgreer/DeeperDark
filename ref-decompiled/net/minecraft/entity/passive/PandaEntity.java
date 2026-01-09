package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PandaEntity extends AnimalEntity {
   private static final TrackedData ASK_FOR_BAMBOO_TICKS;
   private static final TrackedData SNEEZE_PROGRESS;
   private static final TrackedData EATING_TICKS;
   private static final TrackedData MAIN_GENE;
   private static final TrackedData HIDDEN_GENE;
   private static final TrackedData PANDA_FLAGS;
   static final TargetPredicate ASK_FOR_BAMBOO_TARGET;
   private static final EntityDimensions BABY_BASE_DIMENSIONS;
   private static final int SNEEZING_FLAG = 2;
   private static final int PLAYING_FLAG = 4;
   private static final int SITTING_FLAG = 8;
   private static final int LYING_ON_BACK_FLAG = 16;
   private static final int EATING_ANIMATION_INTERVAL = 5;
   public static final int MAIN_GENE_MUTATION_CHANCE = 32;
   private static final int HIDDEN_GENE_MUTATION_CHANCE = 32;
   boolean shouldGetRevenge;
   boolean shouldAttack;
   public int playingTicks;
   private Vec3d playingJump;
   private float sittingAnimationProgress;
   private float lastSittingAnimationProgress;
   private float lieOnBackAnimationProgress;
   private float lastLieOnBackAnimationProgress;
   private float rollOverAnimationProgress;
   private float lastRollOverAnimationProgress;
   LookAtEntityGoal lookAtPlayerGoal;

   public PandaEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.moveControl = new PandaMoveControl(this);
      if (!this.isBaby()) {
         this.setCanPickUpLoot(true);
      }

   }

   protected boolean canDispenserEquipSlot(EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND && this.canPickUpLoot();
   }

   public int getAskForBambooTicks() {
      return (Integer)this.dataTracker.get(ASK_FOR_BAMBOO_TICKS);
   }

   public void setAskForBambooTicks(int askForBambooTicks) {
      this.dataTracker.set(ASK_FOR_BAMBOO_TICKS, askForBambooTicks);
   }

   public boolean isSneezing() {
      return this.hasPandaFlag(2);
   }

   public boolean isSitting() {
      return this.hasPandaFlag(8);
   }

   public void setSitting(boolean sitting) {
      this.setPandaFlag(8, sitting);
   }

   public boolean isLyingOnBack() {
      return this.hasPandaFlag(16);
   }

   public void setLyingOnBack(boolean lyingOnBack) {
      this.setPandaFlag(16, lyingOnBack);
   }

   public boolean isEating() {
      return (Integer)this.dataTracker.get(EATING_TICKS) > 0;
   }

   public void setEating(boolean eating) {
      this.dataTracker.set(EATING_TICKS, eating ? 1 : 0);
   }

   private int getEatingTicks() {
      return (Integer)this.dataTracker.get(EATING_TICKS);
   }

   private void setEatingTicks(int eatingTicks) {
      this.dataTracker.set(EATING_TICKS, eatingTicks);
   }

   public void setSneezing(boolean sneezing) {
      this.setPandaFlag(2, sneezing);
      if (!sneezing) {
         this.setSneezeProgress(0);
      }

   }

   public int getSneezeProgress() {
      return (Integer)this.dataTracker.get(SNEEZE_PROGRESS);
   }

   public void setSneezeProgress(int sneezeProgress) {
      this.dataTracker.set(SNEEZE_PROGRESS, sneezeProgress);
   }

   public Gene getMainGene() {
      return PandaEntity.Gene.byId((Byte)this.dataTracker.get(MAIN_GENE));
   }

   public void setMainGene(Gene gene) {
      if (gene.getId() > 6) {
         gene = PandaEntity.Gene.createRandom(this.random);
      }

      this.dataTracker.set(MAIN_GENE, (byte)gene.getId());
   }

   public Gene getHiddenGene() {
      return PandaEntity.Gene.byId((Byte)this.dataTracker.get(HIDDEN_GENE));
   }

   public void setHiddenGene(Gene gene) {
      if (gene.getId() > 6) {
         gene = PandaEntity.Gene.createRandom(this.random);
      }

      this.dataTracker.set(HIDDEN_GENE, (byte)gene.getId());
   }

   public boolean isPlaying() {
      return this.hasPandaFlag(4);
   }

   public void setPlaying(boolean playing) {
      this.setPandaFlag(4, playing);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(ASK_FOR_BAMBOO_TICKS, 0);
      builder.add(SNEEZE_PROGRESS, 0);
      builder.add(MAIN_GENE, (byte)0);
      builder.add(HIDDEN_GENE, (byte)0);
      builder.add(PANDA_FLAGS, (byte)0);
      builder.add(EATING_TICKS, 0);
   }

   private boolean hasPandaFlag(int bitmask) {
      return ((Byte)this.dataTracker.get(PANDA_FLAGS) & bitmask) != 0;
   }

   private void setPandaFlag(int mask, boolean value) {
      byte b = (Byte)this.dataTracker.get(PANDA_FLAGS);
      if (value) {
         this.dataTracker.set(PANDA_FLAGS, (byte)(b | mask));
      } else {
         this.dataTracker.set(PANDA_FLAGS, (byte)(b & ~mask));
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("MainGene", PandaEntity.Gene.CODEC, this.getMainGene());
      view.put("HiddenGene", PandaEntity.Gene.CODEC, this.getHiddenGene());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setMainGene((Gene)view.read("MainGene", PandaEntity.Gene.CODEC).orElse(PandaEntity.Gene.NORMAL));
      this.setHiddenGene((Gene)view.read("HiddenGene", PandaEntity.Gene.CODEC).orElse(PandaEntity.Gene.NORMAL));
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      PandaEntity pandaEntity = (PandaEntity)EntityType.PANDA.create(world, SpawnReason.BREEDING);
      if (pandaEntity != null) {
         if (entity instanceof PandaEntity) {
            PandaEntity pandaEntity2 = (PandaEntity)entity;
            pandaEntity.initGenes(this, pandaEntity2);
         }

         pandaEntity.resetAttributes();
      }

      return pandaEntity;
   }

   protected void initGoals() {
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(2, new PandaEscapeDangerGoal(this, 2.0));
      this.goalSelector.add(2, new PandaMateGoal(this, 1.0));
      this.goalSelector.add(3, new AttackGoal(this, 1.2000000476837158, true));
      this.goalSelector.add(4, new TemptGoal(this, 1.0, (stack) -> {
         return stack.isIn(ItemTags.PANDA_FOOD);
      }, false));
      this.goalSelector.add(6, new PandaFleeGoal(this, PlayerEntity.class, 8.0F, 2.0, 2.0));
      this.goalSelector.add(6, new PandaFleeGoal(this, HostileEntity.class, 4.0F, 2.0, 2.0));
      this.goalSelector.add(7, new PickUpFoodGoal());
      this.goalSelector.add(8, new LieOnBackGoal(this));
      this.goalSelector.add(8, new SneezeGoal(this));
      this.lookAtPlayerGoal = new LookAtEntityGoal(this, PlayerEntity.class, 6.0F);
      this.goalSelector.add(9, this.lookAtPlayerGoal);
      this.goalSelector.add(10, new LookAroundGoal(this));
      this.goalSelector.add(12, new PlayGoal(this));
      this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
      this.goalSelector.add(14, new WanderAroundFarGoal(this, 1.0));
      this.targetSelector.add(1, (new PandaRevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
   }

   public static DefaultAttributeContainer.Builder createPandaAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.15000000596046448).add(EntityAttributes.ATTACK_DAMAGE, 6.0);
   }

   public Gene getProductGene() {
      return PandaEntity.Gene.getProductGene(this.getMainGene(), this.getHiddenGene());
   }

   public boolean isLazy() {
      return this.getProductGene() == PandaEntity.Gene.LAZY;
   }

   public boolean isWorried() {
      return this.getProductGene() == PandaEntity.Gene.WORRIED;
   }

   public boolean isPlayful() {
      return this.getProductGene() == PandaEntity.Gene.PLAYFUL;
   }

   public boolean isBrown() {
      return this.getProductGene() == PandaEntity.Gene.BROWN;
   }

   public boolean isWeak() {
      return this.getProductGene() == PandaEntity.Gene.WEAK;
   }

   public boolean isAttacking() {
      return this.getProductGene() == PandaEntity.Gene.AGGRESSIVE;
   }

   public boolean canBeLeashed() {
      return false;
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      if (!this.isAttacking()) {
         this.shouldAttack = true;
      }

      return super.tryAttack(world, target);
   }

   public void playAttackSound() {
      this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      if (this.isWorried()) {
         if (this.getWorld().isThundering() && !this.isTouchingWater()) {
            this.setSitting(true);
            this.setEating(false);
         } else if (!this.isEating()) {
            this.setSitting(false);
         }
      }

      LivingEntity livingEntity = this.getTarget();
      if (livingEntity == null) {
         this.shouldGetRevenge = false;
         this.shouldAttack = false;
      }

      if (this.getAskForBambooTicks() > 0) {
         if (livingEntity != null) {
            this.lookAtEntity(livingEntity, 90.0F, 90.0F);
         }

         if (this.getAskForBambooTicks() == 29 || this.getAskForBambooTicks() == 14) {
            this.playSound(SoundEvents.ENTITY_PANDA_CANT_BREED, 1.0F, 1.0F);
         }

         this.setAskForBambooTicks(this.getAskForBambooTicks() - 1);
      }

      if (this.isSneezing()) {
         this.setSneezeProgress(this.getSneezeProgress() + 1);
         if (this.getSneezeProgress() > 20) {
            this.setSneezing(false);
            this.sneeze();
         } else if (this.getSneezeProgress() == 1) {
            this.playSound(SoundEvents.ENTITY_PANDA_PRE_SNEEZE, 1.0F, 1.0F);
         }
      }

      if (this.isPlaying()) {
         this.updatePlaying();
      } else {
         this.playingTicks = 0;
      }

      if (this.isSitting()) {
         this.setPitch(0.0F);
      }

      this.updateSittingAnimation();
      this.updateEatingAnimation();
      this.updateLieOnBackAnimation();
      this.updateRollOverAnimation();
   }

   public boolean isScaredByThunderstorm() {
      return this.isWorried() && this.getWorld().isThundering();
   }

   private void updateEatingAnimation() {
      if (!this.isEating() && this.isSitting() && !this.isScaredByThunderstorm() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
         this.setEating(true);
      } else if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || !this.isSitting()) {
         this.setEating(false);
      }

      if (this.isEating()) {
         this.playEatingAnimation();
         if (!this.getWorld().isClient && this.getEatingTicks() > 80 && this.random.nextInt(20) == 1) {
            if (this.getEatingTicks() > 100 && this.getEquippedStack(EquipmentSlot.MAINHAND).isIn(ItemTags.PANDA_EATS_FROM_GROUND)) {
               if (!this.getWorld().isClient) {
                  this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                  this.emitGameEvent(GameEvent.EAT);
               }

               this.setSitting(false);
            }

            this.setEating(false);
            return;
         }

         this.setEatingTicks(this.getEatingTicks() + 1);
      }

   }

   private void playEatingAnimation() {
      if (this.getEatingTicks() % 5 == 0) {
         this.playSound(SoundEvents.ENTITY_PANDA_EAT, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

         for(int i = 0; i < 6; ++i) {
            Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
            vec3d = vec3d.rotateX(-this.getPitch() * 0.017453292F);
            vec3d = vec3d.rotateY(-this.getYaw() * 0.017453292F);
            double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
            vec3d2 = vec3d2.rotateY(-this.bodyYaw * 0.017453292F);
            vec3d2 = vec3d2.add(this.getX(), this.getEyeY() + 1.0, this.getZ());
            this.getWorld().addParticleClient(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getEquippedStack(EquipmentSlot.MAINHAND)), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
         }
      }

   }

   private void updateSittingAnimation() {
      this.lastSittingAnimationProgress = this.sittingAnimationProgress;
      if (this.isSitting()) {
         this.sittingAnimationProgress = Math.min(1.0F, this.sittingAnimationProgress + 0.15F);
      } else {
         this.sittingAnimationProgress = Math.max(0.0F, this.sittingAnimationProgress - 0.19F);
      }

   }

   private void updateLieOnBackAnimation() {
      this.lastLieOnBackAnimationProgress = this.lieOnBackAnimationProgress;
      if (this.isLyingOnBack()) {
         this.lieOnBackAnimationProgress = Math.min(1.0F, this.lieOnBackAnimationProgress + 0.15F);
      } else {
         this.lieOnBackAnimationProgress = Math.max(0.0F, this.lieOnBackAnimationProgress - 0.19F);
      }

   }

   private void updateRollOverAnimation() {
      this.lastRollOverAnimationProgress = this.rollOverAnimationProgress;
      if (this.isPlaying()) {
         this.rollOverAnimationProgress = Math.min(1.0F, this.rollOverAnimationProgress + 0.15F);
      } else {
         this.rollOverAnimationProgress = Math.max(0.0F, this.rollOverAnimationProgress - 0.19F);
      }

   }

   public float getSittingAnimationProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastSittingAnimationProgress, this.sittingAnimationProgress);
   }

   public float getLieOnBackAnimationProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastLieOnBackAnimationProgress, this.lieOnBackAnimationProgress);
   }

   public float getRollOverAnimationProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastRollOverAnimationProgress, this.rollOverAnimationProgress);
   }

   private void updatePlaying() {
      ++this.playingTicks;
      if (this.playingTicks > 32) {
         this.setPlaying(false);
      } else {
         if (!this.getWorld().isClient) {
            Vec3d vec3d = this.getVelocity();
            if (this.playingTicks == 1) {
               float f = this.getYaw() * 0.017453292F;
               float g = this.isBaby() ? 0.1F : 0.2F;
               this.playingJump = new Vec3d(vec3d.x + (double)(-MathHelper.sin(f) * g), 0.0, vec3d.z + (double)(MathHelper.cos(f) * g));
               this.setVelocity(this.playingJump.add(0.0, 0.27, 0.0));
            } else if ((float)this.playingTicks != 7.0F && (float)this.playingTicks != 15.0F && (float)this.playingTicks != 23.0F) {
               this.setVelocity(this.playingJump.x, vec3d.y, this.playingJump.z);
            } else {
               this.setVelocity(0.0, this.isOnGround() ? 0.27 : vec3d.y, 0.0);
            }
         }

      }
   }

   private void sneeze() {
      Vec3d vec3d = this.getVelocity();
      World world = this.getWorld();
      world.addParticleClient(ParticleTypes.SNEEZE, this.getX() - (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(this.bodyYaw * 0.017453292F), this.getEyeY() - 0.10000000149011612, this.getZ() + (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(this.bodyYaw * 0.017453292F), vec3d.x, 0.0, vec3d.z);
      this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0F, 1.0F);
      List list = world.getNonSpectatingEntities(PandaEntity.class, this.getBoundingBox().expand(10.0));
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         PandaEntity pandaEntity = (PandaEntity)var4.next();
         if (!pandaEntity.isBaby() && pandaEntity.isOnGround() && !pandaEntity.isTouchingWater() && pandaEntity.isIdle()) {
            pandaEntity.jump();
         }
      }

      World var7 = this.getWorld();
      if (var7 instanceof ServerWorld serverWorld) {
         if (serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.forEachGiftedItem(serverWorld, LootTables.PANDA_SNEEZE_GAMEPLAY, this::dropStack);
         }
      }

   }

   protected void loot(ServerWorld world, ItemEntity itemEntity) {
      if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && canEatFromGround(itemEntity)) {
         this.triggerItemPickedUpByEntityCriteria(itemEntity);
         ItemStack itemStack = itemEntity.getStack();
         this.equipStack(EquipmentSlot.MAINHAND, itemStack);
         this.setDropGuaranteed(EquipmentSlot.MAINHAND);
         this.sendPickup(itemEntity, itemStack.getCount());
         itemEntity.discard();
      }

   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      this.setSitting(false);
      return super.damage(world, source, amount);
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Random random = world.getRandom();
      this.setMainGene(PandaEntity.Gene.createRandom(random));
      this.setHiddenGene(PandaEntity.Gene.createRandom(random));
      this.resetAttributes();
      if (entityData == null) {
         entityData = new PassiveEntity.PassiveData(0.2F);
      }

      return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
   }

   public void initGenes(PandaEntity mother, @Nullable PandaEntity father) {
      if (father == null) {
         if (this.random.nextBoolean()) {
            this.setMainGene(mother.getRandomGene());
            this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
         } else {
            this.setMainGene(PandaEntity.Gene.createRandom(this.random));
            this.setHiddenGene(mother.getRandomGene());
         }
      } else if (this.random.nextBoolean()) {
         this.setMainGene(mother.getRandomGene());
         this.setHiddenGene(father.getRandomGene());
      } else {
         this.setMainGene(father.getRandomGene());
         this.setHiddenGene(mother.getRandomGene());
      }

      if (this.random.nextInt(32) == 0) {
         this.setMainGene(PandaEntity.Gene.createRandom(this.random));
      }

      if (this.random.nextInt(32) == 0) {
         this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
      }

   }

   private Gene getRandomGene() {
      return this.random.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
   }

   public void resetAttributes() {
      if (this.isWeak()) {
         this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
      }

      if (this.isLazy()) {
         this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.07000000029802322);
      }

   }

   void stop() {
      if (!this.isTouchingWater()) {
         this.setForwardSpeed(0.0F);
         this.getNavigation().stop();
         this.setSitting(true);
      }

   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (this.isScaredByThunderstorm()) {
         return ActionResult.PASS;
      } else if (this.isLyingOnBack()) {
         this.setLyingOnBack(false);
         return ActionResult.SUCCESS;
      } else if (this.isBreedingItem(itemStack)) {
         if (this.getTarget() != null) {
            this.shouldGetRevenge = true;
         }

         if (this.isBaby()) {
            this.eat(player, hand, itemStack);
            this.growUp((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
         } else if (!this.getWorld().isClient && this.getBreedingAge() == 0 && this.canEat()) {
            this.eat(player, hand, itemStack);
            this.lovePlayer(player);
         } else {
            World var5 = this.getWorld();
            if (!(var5 instanceof ServerWorld)) {
               return ActionResult.PASS;
            }

            ServerWorld serverWorld = (ServerWorld)var5;
            if (this.isSitting() || this.isTouchingWater()) {
               return ActionResult.PASS;
            }

            this.stop();
            this.setEating(true);
            ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack2.isEmpty() && !player.isInCreativeMode()) {
               this.dropStack(serverWorld, itemStack2);
            }

            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(itemStack.getItem(), 1));
            this.eat(player, hand, itemStack);
         }

         return ActionResult.SUCCESS_SERVER;
      } else {
         return ActionResult.PASS;
      }
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isAttacking()) {
         return SoundEvents.ENTITY_PANDA_AGGRESSIVE_AMBIENT;
      } else {
         return this.isWorried() ? SoundEvents.ENTITY_PANDA_WORRIED_AMBIENT : SoundEvents.ENTITY_PANDA_AMBIENT;
      }
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_PANDA_STEP, 0.15F, 1.0F);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.PANDA_FOOD);
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PANDA_DEATH;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_PANDA_HURT;
   }

   public boolean isIdle() {
      return !this.isLyingOnBack() && !this.isScaredByThunderstorm() && !this.isEating() && !this.isPlaying() && !this.isSitting();
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   private static boolean canEatFromGround(ItemEntity itemEntity) {
      return itemEntity.getStack().isIn(ItemTags.PANDA_EATS_FROM_GROUND) && itemEntity.isAlive() && !itemEntity.cannotPickup();
   }

   static {
      ASK_FOR_BAMBOO_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
      SNEEZE_PROGRESS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
      EATING_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
      MAIN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
      HIDDEN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
      PANDA_FLAGS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
      ASK_FOR_BAMBOO_TARGET = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0);
      BABY_BASE_DIMENSIONS = EntityType.PANDA.getDimensions().scaled(0.5F).withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, 0.40625F, 0.0F));
   }

   private static class PandaMoveControl extends MoveControl {
      private final PandaEntity panda;

      public PandaMoveControl(PandaEntity panda) {
         super(panda);
         this.panda = panda;
      }

      public void tick() {
         if (this.panda.isIdle()) {
            super.tick();
         }
      }
   }

   public static enum Gene implements StringIdentifiable {
      NORMAL(0, "normal", false),
      LAZY(1, "lazy", false),
      WORRIED(2, "worried", false),
      PLAYFUL(3, "playful", false),
      BROWN(4, "brown", true),
      WEAK(5, "weak", true),
      AGGRESSIVE(6, "aggressive", false);

      public static final Codec CODEC = StringIdentifiable.createCodec(Gene::values);
      private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(Gene::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      private static final int field_30350 = 6;
      private final int id;
      private final String name;
      private final boolean recessive;

      private Gene(final int id, final String name, final boolean recessive) {
         this.id = id;
         this.name = name;
         this.recessive = recessive;
      }

      public int getId() {
         return this.id;
      }

      public String asString() {
         return this.name;
      }

      public boolean isRecessive() {
         return this.recessive;
      }

      static Gene getProductGene(Gene mainGene, Gene hiddenGene) {
         if (mainGene.isRecessive()) {
            return mainGene == hiddenGene ? mainGene : NORMAL;
         } else {
            return mainGene;
         }
      }

      public static Gene byId(int id) {
         return (Gene)BY_ID.apply(id);
      }

      public static Gene createRandom(Random random) {
         int i = random.nextInt(16);
         if (i == 0) {
            return LAZY;
         } else if (i == 1) {
            return WORRIED;
         } else if (i == 2) {
            return PLAYFUL;
         } else if (i == 4) {
            return AGGRESSIVE;
         } else if (i < 9) {
            return WEAK;
         } else {
            return i < 11 ? BROWN : NORMAL;
         }
      }

      // $FF: synthetic method
      private static Gene[] method_36642() {
         return new Gene[]{NORMAL, LAZY, WORRIED, PLAYFUL, BROWN, WEAK, AGGRESSIVE};
      }
   }

   private static class PandaEscapeDangerGoal extends EscapeDangerGoal {
      private final PandaEntity panda;

      public PandaEscapeDangerGoal(PandaEntity panda, double speed) {
         super(panda, speed, (TagKey)DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES);
         this.panda = panda;
      }

      public boolean shouldContinue() {
         if (this.panda.isSitting()) {
            this.panda.getNavigation().stop();
            return false;
         } else {
            return super.shouldContinue();
         }
      }
   }

   static class PandaMateGoal extends AnimalMateGoal {
      private final PandaEntity panda;
      private int nextAskPlayerForBambooAge;

      public PandaMateGoal(PandaEntity panda, double chance) {
         super(panda, chance);
         this.panda = panda;
      }

      public boolean canStart() {
         if (super.canStart() && this.panda.getAskForBambooTicks() == 0) {
            if (!this.isBambooClose()) {
               if (this.nextAskPlayerForBambooAge <= this.panda.age) {
                  this.panda.setAskForBambooTicks(32);
                  this.nextAskPlayerForBambooAge = this.panda.age + 600;
                  if (this.panda.canActVoluntarily()) {
                     PlayerEntity playerEntity = this.world.getClosestPlayer(PandaEntity.ASK_FOR_BAMBOO_TARGET, this.panda);
                     this.panda.lookAtPlayerGoal.setTarget(playerEntity);
                  }
               }

               return false;
            } else {
               return true;
            }
         } else {
            return false;
         }
      }

      private boolean isBambooClose() {
         BlockPos blockPos = this.panda.getBlockPos();
         BlockPos.Mutable mutable = new BlockPos.Mutable();

         for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 8; ++j) {
               for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                  for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                     mutable.set((Vec3i)blockPos, k, i, l);
                     if (this.world.getBlockState(mutable).isOf(Blocks.BAMBOO)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   private static class AttackGoal extends MeleeAttackGoal {
      private final PandaEntity panda;

      public AttackGoal(PandaEntity panda, double speed, boolean pauseWhenMobIdle) {
         super(panda, speed, pauseWhenMobIdle);
         this.panda = panda;
      }

      public boolean canStart() {
         return this.panda.isIdle() && super.canStart();
      }
   }

   private static class PandaFleeGoal extends FleeEntityGoal {
      private final PandaEntity panda;

      public PandaFleeGoal(PandaEntity panda, Class fleeFromType, float distance, double slowSpeed, double fastSpeed) {
         Predicate var10006 = EntityPredicates.EXCEPT_SPECTATOR;
         Objects.requireNonNull(var10006);
         super(panda, fleeFromType, distance, slowSpeed, fastSpeed, var10006::test);
         this.panda = panda;
      }

      public boolean canStart() {
         return this.panda.isWorried() && this.panda.isIdle() && super.canStart();
      }
   }

   class PickUpFoodGoal extends Goal {
      private int startAge;

      public PickUpFoodGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         if (this.startAge <= PandaEntity.this.age && !PandaEntity.this.isBaby() && !PandaEntity.this.isTouchingWater() && PandaEntity.this.isIdle() && PandaEntity.this.getAskForBambooTicks() <= 0) {
            if (!PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
               return true;
            } else {
               return !PandaEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), PandaEntity::canEatFromGround).isEmpty();
            }
         } else {
            return false;
         }
      }

      public boolean shouldContinue() {
         if (!PandaEntity.this.isTouchingWater() && (PandaEntity.this.isLazy() || PandaEntity.this.random.nextInt(toGoalTicks(600)) != 1)) {
            return PandaEntity.this.random.nextInt(toGoalTicks(2000)) != 1;
         } else {
            return false;
         }
      }

      public void tick() {
         if (!PandaEntity.this.isSitting() && !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            PandaEntity.this.stop();
         }

      }

      public void start() {
         if (PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            List list = PandaEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PandaEntity::canEatFromGround);
            if (!list.isEmpty()) {
               PandaEntity.this.getNavigation().startMovingTo((Entity)list.getFirst(), 1.2000000476837158);
            }
         } else {
            PandaEntity.this.stop();
         }

         this.startAge = 0;
      }

      public void stop() {
         ItemStack itemStack = PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
         if (!itemStack.isEmpty()) {
            PandaEntity.this.dropStack(castToServerWorld(PandaEntity.this.getWorld()), itemStack);
            PandaEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            int i = PandaEntity.this.isLazy() ? PandaEntity.this.random.nextInt(50) + 10 : PandaEntity.this.random.nextInt(150) + 10;
            this.startAge = PandaEntity.this.age + i * 20;
         }

         PandaEntity.this.setSitting(false);
      }
   }

   static class LieOnBackGoal extends Goal {
      private final PandaEntity panda;
      private int nextLieOnBackAge;

      public LieOnBackGoal(PandaEntity panda) {
         this.panda = panda;
      }

      public boolean canStart() {
         return this.nextLieOnBackAge < this.panda.age && this.panda.isLazy() && this.panda.isIdle() && this.panda.random.nextInt(toGoalTicks(400)) == 1;
      }

      public boolean shouldContinue() {
         if (!this.panda.isTouchingWater() && (this.panda.isLazy() || this.panda.random.nextInt(toGoalTicks(600)) != 1)) {
            return this.panda.random.nextInt(toGoalTicks(2000)) != 1;
         } else {
            return false;
         }
      }

      public void start() {
         this.panda.setLyingOnBack(true);
         this.nextLieOnBackAge = 0;
      }

      public void stop() {
         this.panda.setLyingOnBack(false);
         this.nextLieOnBackAge = this.panda.age + 200;
      }
   }

   static class SneezeGoal extends Goal {
      private final PandaEntity panda;

      public SneezeGoal(PandaEntity panda) {
         this.panda = panda;
      }

      public boolean canStart() {
         if (this.panda.isBaby() && this.panda.isIdle()) {
            if (this.panda.isWeak() && this.panda.random.nextInt(toGoalTicks(500)) == 1) {
               return true;
            } else {
               return this.panda.random.nextInt(toGoalTicks(6000)) == 1;
            }
         } else {
            return false;
         }
      }

      public boolean shouldContinue() {
         return false;
      }

      public void start() {
         this.panda.setSneezing(true);
      }
   }

   private static class LookAtEntityGoal extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
      private final PandaEntity panda;

      public LookAtEntityGoal(PandaEntity panda, Class targetType, float range) {
         super(panda, targetType, range);
         this.panda = panda;
      }

      public void setTarget(LivingEntity target) {
         this.target = target;
      }

      public boolean shouldContinue() {
         return this.target != null && super.shouldContinue();
      }

      public boolean canStart() {
         if (this.mob.getRandom().nextFloat() >= this.chance) {
            return false;
         } else {
            if (this.target == null) {
               ServerWorld serverWorld = getServerWorld(this.mob);
               if (this.targetType == PlayerEntity.class) {
                  this.target = serverWorld.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
               } else {
                  this.target = serverWorld.getClosestEntity(this.mob.getWorld().getEntitiesByClass(this.targetType, this.mob.getBoundingBox().expand((double)this.range, 3.0, (double)this.range), (livingEntity) -> {
                     return true;
                  }), this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
               }
            }

            return this.panda.isIdle() && this.target != null;
         }
      }

      public void tick() {
         if (this.target != null) {
            super.tick();
         }

      }
   }

   private static class PlayGoal extends Goal {
      private final PandaEntity panda;

      public PlayGoal(PandaEntity panda) {
         this.panda = panda;
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
      }

      public boolean canStart() {
         if ((this.panda.isBaby() || this.panda.isPlayful()) && this.panda.isOnGround()) {
            if (!this.panda.isIdle()) {
               return false;
            } else {
               float f = this.panda.getYaw() * 0.017453292F;
               float g = -MathHelper.sin(f);
               float h = MathHelper.cos(f);
               int i = (double)Math.abs(g) > 0.5 ? MathHelper.sign((double)g) : 0;
               int j = (double)Math.abs(h) > 0.5 ? MathHelper.sign((double)h) : 0;
               if (this.panda.getWorld().getBlockState(this.panda.getBlockPos().add(i, -1, j)).isAir()) {
                  return true;
               } else if (this.panda.isPlayful() && this.panda.random.nextInt(toGoalTicks(60)) == 1) {
                  return true;
               } else {
                  return this.panda.random.nextInt(toGoalTicks(500)) == 1;
               }
            }
         } else {
            return false;
         }
      }

      public boolean shouldContinue() {
         return false;
      }

      public void start() {
         this.panda.setPlaying(true);
      }

      public boolean canStop() {
         return false;
      }
   }

   static class PandaRevengeGoal extends RevengeGoal {
      private final PandaEntity panda;

      public PandaRevengeGoal(PandaEntity panda, Class... noRevengeTypes) {
         super(panda, noRevengeTypes);
         this.panda = panda;
      }

      public boolean shouldContinue() {
         if (!this.panda.shouldGetRevenge && !this.panda.shouldAttack) {
            return super.shouldContinue();
         } else {
            this.panda.setTarget((LivingEntity)null);
            return false;
         }
      }

      protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
         if (mob instanceof PandaEntity && mob.isAttacking()) {
            mob.setTarget(target);
         }

      }
   }
}
