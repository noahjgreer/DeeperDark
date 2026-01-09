package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class SplashPotionItem extends ThrowablePotionItem {
   public SplashPotionItem(Item.Settings settings) {
      super(settings);
   }

   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      world.playSound((Entity)null, user.getX(), user.getY(), user.getZ(), (SoundEvent)SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
      return super.use(world, user, hand);
   }

   protected PotionEntity createEntity(ServerWorld world, LivingEntity user, ItemStack stack) {
      return new SplashPotionEntity(world, user, stack);
   }

   protected PotionEntity createEntity(World world, Position pos, ItemStack stack) {
      return new SplashPotionEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
   }
}
