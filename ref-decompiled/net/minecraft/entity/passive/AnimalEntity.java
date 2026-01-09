package net.minecraft.entity.passive;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class AnimalEntity extends PassiveEntity {
   protected static final int BREEDING_COOLDOWN = 6000;
   private static final int DEFAULT_LOVE_TICKS = 0;
   private int loveTicks = 0;
   @Nullable
   private LazyEntityReference lovingPlayer;

   protected AnimalEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0F);
      this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
   }

   public static DefaultAttributeContainer.Builder createAnimalAttributes() {
      return MobEntity.createMobAttributes().add(EntityAttributes.TEMPT_RANGE, 10.0);
   }

   protected void mobTick(ServerWorld world) {
      if (this.getBreedingAge() != 0) {
         this.loveTicks = 0;
      }

      super.mobTick(world);
   }

   public void tickMovement() {
      super.tickMovement();
      if (this.getBreedingAge() != 0) {
         this.loveTicks = 0;
      }

      if (this.loveTicks > 0) {
         --this.loveTicks;
         if (this.loveTicks % 10 == 0) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticleClient(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
         }
      }

   }

   protected void applyDamage(ServerWorld world, DamageSource source, float amount) {
      this.resetLoveTicks();
      super.applyDamage(world, source, amount);
   }

   public float getPathfindingFavor(BlockPos pos, WorldView world) {
      return world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK) ? 10.0F : world.getPhototaxisFavor(pos);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("InLove", this.loveTicks);
      LazyEntityReference.writeData(this.lovingPlayer, view, "LoveCause");
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.loveTicks = view.getInt("InLove", 0);
      this.lovingPlayer = LazyEntityReference.fromData(view, "LoveCause");
   }

   public static boolean isValidNaturalSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      boolean bl = SpawnReason.isTrialSpawner(spawnReason) || isLightLevelValidForNaturalSpawn(world, pos);
      return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON) && bl;
   }

   protected static boolean isLightLevelValidForNaturalSpawn(BlockRenderView world, BlockPos pos) {
      return world.getBaseLightLevel(pos, 0) > 8;
   }

   public int getMinAmbientSoundDelay() {
      return 120;
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return false;
   }

   protected int getExperienceToDrop(ServerWorld world) {
      return 1 + this.random.nextInt(3);
   }

   public abstract boolean isBreedingItem(ItemStack stack);

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (this.isBreedingItem(itemStack)) {
         int i = this.getBreedingAge();
         if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            if (i == 0 && this.canEat()) {
               this.eat(player, hand, itemStack);
               this.lovePlayer(serverPlayerEntity);
               this.playEatSound();
               return ActionResult.SUCCESS_SERVER;
            }
         }

         if (this.isBaby()) {
            this.eat(player, hand, itemStack);
            this.growUp(toGrowUpAge(-i), true);
            this.playEatSound();
            return ActionResult.SUCCESS;
         }

         if (this.getWorld().isClient) {
            return ActionResult.CONSUME;
         }
      }

      return super.interactMob(player, hand);
   }

   protected void playEatSound() {
   }

   protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
      int i = stack.getCount();
      UseRemainderComponent useRemainderComponent = (UseRemainderComponent)stack.get(DataComponentTypes.USE_REMAINDER);
      stack.decrementUnlessCreative(1, player);
      if (useRemainderComponent != null) {
         boolean var10003 = player.isInCreativeMode();
         Objects.requireNonNull(player);
         ItemStack itemStack = useRemainderComponent.convert(stack, i, var10003, player::giveOrDropStack);
         player.setStackInHand(hand, itemStack);
      }

   }

   public boolean canEat() {
      return this.loveTicks <= 0;
   }

   public void lovePlayer(@Nullable PlayerEntity player) {
      this.loveTicks = 600;
      if (player instanceof ServerPlayerEntity serverPlayerEntity) {
         this.lovingPlayer = new LazyEntityReference(serverPlayerEntity);
      }

      this.getWorld().sendEntityStatus(this, (byte)18);
   }

   public void setLoveTicks(int loveTicks) {
      this.loveTicks = loveTicks;
   }

   public int getLoveTicks() {
      return this.loveTicks;
   }

   @Nullable
   public ServerPlayerEntity getLovingPlayer() {
      LazyEntityReference var10000 = this.lovingPlayer;
      World var10001 = this.getWorld();
      Objects.requireNonNull(var10001);
      return (ServerPlayerEntity)LazyEntityReference.resolve(var10000, var10001::getPlayerByUuid, ServerPlayerEntity.class);
   }

   public boolean isInLove() {
      return this.loveTicks > 0;
   }

   public void resetLoveTicks() {
      this.loveTicks = 0;
   }

   public boolean canBreedWith(AnimalEntity other) {
      if (other == this) {
         return false;
      } else if (other.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && other.isInLove();
      }
   }

   public void breed(ServerWorld world, AnimalEntity other) {
      PassiveEntity passiveEntity = this.createChild(world, other);
      if (passiveEntity != null) {
         passiveEntity.setBaby(true);
         passiveEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
         this.breed(world, other, passiveEntity);
         world.spawnEntityAndPassengers(passiveEntity);
      }
   }

   public void breed(ServerWorld world, AnimalEntity other, @Nullable PassiveEntity baby) {
      Optional.ofNullable(this.getLovingPlayer()).or(() -> {
         return Optional.ofNullable(other.getLovingPlayer());
      }).ifPresent((player) -> {
         player.incrementStat(Stats.ANIMALS_BRED);
         Criteria.BRED_ANIMALS.trigger(player, this, other, baby);
      });
      this.setBreedingAge(6000);
      other.setBreedingAge(6000);
      this.resetLoveTicks();
      other.resetLoveTicks();
      world.sendEntityStatus(this, (byte)18);
      if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
         world.spawnEntity(new ExperienceOrbEntity(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
      }

   }

   public void handleStatus(byte status) {
      if (status == 18) {
         for(int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticleClient(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
         }
      } else {
         super.handleStatus(status);
      }

   }
}
