package net.minecraft.entity.projectile.thrown;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class LingeringPotionEntity extends PotionEntity {
   public LingeringPotionEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public LingeringPotionEntity(World world, LivingEntity owner, ItemStack stack) {
      super(EntityType.LINGERING_POTION, world, owner, stack);
   }

   public LingeringPotionEntity(World world, double x, double y, double z, ItemStack stack) {
      super(EntityType.LINGERING_POTION, world, x, y, z, stack);
   }

   protected Item getDefaultItem() {
      return Items.LINGERING_POTION;
   }

   public void spawnAreaEffectCloud(ServerWorld world, ItemStack stack, HitResult hitResult) {
      AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
      Entity var6 = this.getOwner();
      if (var6 instanceof LivingEntity livingEntity) {
         areaEffectCloudEntity.setOwner(livingEntity);
      }

      areaEffectCloudEntity.setRadius(3.0F);
      areaEffectCloudEntity.setRadiusOnUse(-0.5F);
      areaEffectCloudEntity.setDuration(600);
      areaEffectCloudEntity.setWaitTime(10);
      areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
      areaEffectCloudEntity.copyComponentsFrom(stack);
      world.spawnEntity(areaEffectCloudEntity);
   }
}
