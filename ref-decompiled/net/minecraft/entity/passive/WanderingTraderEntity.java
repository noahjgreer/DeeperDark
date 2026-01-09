package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.Iterator;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HoldInHandsGoal;
import net.minecraft.entity.ai.goal.LookAtCustomerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.StopAndLookAtEntityGoal;
import net.minecraft.entity.ai.goal.StopFollowingCustomerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class WanderingTraderEntity extends MerchantEntity implements ConsumableComponent.ConsumableSoundProvider {
   private static final int DEFAULT_DESPAWN_DELAY = 0;
   @Nullable
   private BlockPos wanderTarget;
   private int despawnDelay = 0;

   public WanderingTraderEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initGoals() {
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(0, new HoldInHandsGoal(this, PotionContentsComponent.createStack(Items.POTION, Potions.INVISIBILITY), SoundEvents.ENTITY_WANDERING_TRADER_DISAPPEARED, (wanderingTrader) -> {
         return this.getWorld().isNight() && !wanderingTrader.isInvisible();
      }));
      this.goalSelector.add(0, new HoldInHandsGoal(this, new ItemStack(Items.MILK_BUCKET), SoundEvents.ENTITY_WANDERING_TRADER_REAPPEARED, (wanderingTrader) -> {
         return this.getWorld().isDay() && wanderingTrader.isInvisible();
      }));
      this.goalSelector.add(1, new StopFollowingCustomerGoal(this));
      this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.5, 0.5));
      this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.5, 0.5));
      this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5, 0.5));
      this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.5, 0.5));
      this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.5, 0.5));
      this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5, 0.5));
      this.goalSelector.add(1, new FleeEntityGoal(this, ZoglinEntity.class, 10.0F, 0.5, 0.5));
      this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5));
      this.goalSelector.add(1, new LookAtCustomerGoal(this));
      this.goalSelector.add(2, new WanderToTargetGoal(this, 2.0, 0.35));
      this.goalSelector.add(4, new GoToWalkTargetGoal(this, 0.35));
      this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.35));
      this.goalSelector.add(9, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return null;
   }

   public boolean isLeveledMerchant() {
      return false;
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (!itemStack.isOf(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.hasCustomer() && !this.isBaby()) {
         if (hand == Hand.MAIN_HAND) {
            player.incrementStat(Stats.TALKED_TO_VILLAGER);
         }

         if (!this.getWorld().isClient) {
            if (this.getOffers().isEmpty()) {
               return ActionResult.CONSUME;
            }

            this.setCustomer(player);
            this.sendOffers(player, this.getDisplayName(), 1);
         }

         return ActionResult.SUCCESS;
      } else {
         return super.interactMob(player, hand);
      }
   }

   protected void fillRecipes() {
      TradeOfferList tradeOfferList = this.getOffers();
      Iterator var2 = TradeOffers.WANDERING_TRADER_TRADES.iterator();

      while(var2.hasNext()) {
         Pair pair = (Pair)var2.next();
         TradeOffers.Factory[] factorys = (TradeOffers.Factory[])pair.getLeft();
         this.fillRecipesFromPool(tradeOfferList, factorys, (Integer)pair.getRight());
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("DespawnDelay", this.despawnDelay);
      view.putNullable("wander_target", BlockPos.CODEC, this.wanderTarget);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.despawnDelay = view.getInt("DespawnDelay", 0);
      this.wanderTarget = (BlockPos)view.read("wander_target", BlockPos.CODEC).orElse((Object)null);
      this.setBreedingAge(Math.max(0, this.getBreedingAge()));
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return false;
   }

   protected void afterUsing(TradeOffer offer) {
      if (offer.shouldRewardPlayerExperience()) {
         int i = 3 + this.random.nextInt(4);
         this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ(), i));
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.hasCustomer() ? SoundEvents.ENTITY_WANDERING_TRADER_TRADE : SoundEvents.ENTITY_WANDERING_TRADER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_WANDERING_TRADER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WANDERING_TRADER_DEATH;
   }

   public SoundEvent getConsumeSound(ItemStack stack) {
      return stack.isOf(Items.MILK_BUCKET) ? SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK : SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION;
   }

   protected SoundEvent getTradingSound(boolean sold) {
      return sold ? SoundEvents.ENTITY_WANDERING_TRADER_YES : SoundEvents.ENTITY_WANDERING_TRADER_NO;
   }

   public SoundEvent getYesSound() {
      return SoundEvents.ENTITY_WANDERING_TRADER_YES;
   }

   public void setDespawnDelay(int despawnDelay) {
      this.despawnDelay = despawnDelay;
   }

   public int getDespawnDelay() {
      return this.despawnDelay;
   }

   public void tickMovement() {
      super.tickMovement();
      if (!this.getWorld().isClient) {
         this.tickDespawnDelay();
      }

   }

   private void tickDespawnDelay() {
      if (this.despawnDelay > 0 && !this.hasCustomer() && --this.despawnDelay == 0) {
         this.discard();
      }

   }

   public void setWanderTarget(@Nullable BlockPos wanderTarget) {
      this.wanderTarget = wanderTarget;
   }

   @Nullable
   BlockPos getWanderTarget() {
      return this.wanderTarget;
   }

   private class WanderToTargetGoal extends Goal {
      final WanderingTraderEntity trader;
      final double proximityDistance;
      final double speed;

      WanderToTargetGoal(final WanderingTraderEntity trader, final double proximityDistance, final double speed) {
         this.trader = trader;
         this.proximityDistance = proximityDistance;
         this.speed = speed;
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public void stop() {
         this.trader.setWanderTarget((BlockPos)null);
         WanderingTraderEntity.this.navigation.stop();
      }

      public boolean canStart() {
         BlockPos blockPos = this.trader.getWanderTarget();
         return blockPos != null && this.isTooFarFrom(blockPos, this.proximityDistance);
      }

      public void tick() {
         BlockPos blockPos = this.trader.getWanderTarget();
         if (blockPos != null && WanderingTraderEntity.this.navigation.isIdle()) {
            if (this.isTooFarFrom(blockPos, 10.0)) {
               Vec3d vec3d = (new Vec3d((double)blockPos.getX() - this.trader.getX(), (double)blockPos.getY() - this.trader.getY(), (double)blockPos.getZ() - this.trader.getZ())).normalize();
               Vec3d vec3d2 = vec3d.multiply(10.0).add(this.trader.getX(), this.trader.getY(), this.trader.getZ());
               WanderingTraderEntity.this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            } else {
               WanderingTraderEntity.this.navigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.speed);
            }
         }

      }

      private boolean isTooFarFrom(BlockPos pos, double proximityDistance) {
         return !pos.isWithinDistance(this.trader.getPos(), proximityDistance);
      }
   }
}
