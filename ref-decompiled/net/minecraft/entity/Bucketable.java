package net.minecraft.entity;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface Bucketable {
   boolean isFromBucket();

   void setFromBucket(boolean fromBucket);

   void copyDataToStack(ItemStack stack);

   void copyDataFromNbt(NbtCompound nbt);

   ItemStack getBucketItem();

   SoundEvent getBucketFillSound();

   /** @deprecated */
   @Deprecated
   static void copyDataToStack(MobEntity entity, ItemStack stack) {
      stack.copy(DataComponentTypes.CUSTOM_NAME, entity);
      NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, (nbtCompound) -> {
         if (entity.isAiDisabled()) {
            nbtCompound.putBoolean("NoAI", entity.isAiDisabled());
         }

         if (entity.isSilent()) {
            nbtCompound.putBoolean("Silent", entity.isSilent());
         }

         if (entity.hasNoGravity()) {
            nbtCompound.putBoolean("NoGravity", entity.hasNoGravity());
         }

         if (entity.isGlowingLocal()) {
            nbtCompound.putBoolean("Glowing", entity.isGlowingLocal());
         }

         if (entity.isInvulnerable()) {
            nbtCompound.putBoolean("Invulnerable", entity.isInvulnerable());
         }

         nbtCompound.putFloat("Health", entity.getHealth());
      });
   }

   /** @deprecated */
   @Deprecated
   static void copyDataFromNbt(MobEntity entity, NbtCompound nbt) {
      Optional var10000 = nbt.getBoolean("NoAI");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setAiDisabled);
      var10000 = nbt.getBoolean("Silent");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setSilent);
      var10000 = nbt.getBoolean("NoGravity");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setNoGravity);
      var10000 = nbt.getBoolean("Glowing");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setGlowing);
      var10000 = nbt.getBoolean("Invulnerable");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setInvulnerable);
      var10000 = nbt.getFloat("Health");
      Objects.requireNonNull(entity);
      var10000.ifPresent(entity::setHealth);
   }

   static Optional tryBucket(PlayerEntity player, Hand hand, LivingEntity entity) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.getItem() == Items.WATER_BUCKET && entity.isAlive()) {
         entity.playSound(((Bucketable)entity).getBucketFillSound(), 1.0F, 1.0F);
         ItemStack itemStack2 = ((Bucketable)entity).getBucketItem();
         ((Bucketable)entity).copyDataToStack(itemStack2);
         ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack, player, itemStack2, false);
         player.setStackInHand(hand, itemStack3);
         World world = entity.getWorld();
         if (!world.isClient) {
            Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)player, itemStack2);
         }

         entity.discard();
         return Optional.of(ActionResult.SUCCESS);
      } else {
         return Optional.empty();
      }
   }
}
