package net.minecraft.entity.projectile.thrown;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class EggEntity extends ThrownItemEntity {
   private static final EntityDimensions EMPTY_DIMENSIONS = EntityDimensions.fixed(0.0F, 0.0F);

   public EggEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public EggEntity(World world, LivingEntity owner, ItemStack stack) {
      super(EntityType.EGG, owner, world, stack);
   }

   public EggEntity(World world, double x, double y, double z, ItemStack stack) {
      super(EntityType.EGG, x, y, z, world, stack);
   }

   public void handleStatus(byte status) {
      if (status == 3) {
         double d = 0.08;

         for(int i = 0; i < 8; ++i) {
            this.getWorld().addParticleClient(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
         }
      }

   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      entityHitResult.getEntity().serverDamage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      if (!this.getWorld().isClient) {
         if (this.random.nextInt(8) == 0) {
            int i = 1;
            if (this.random.nextInt(32) == 0) {
               i = 4;
            }

            for(int j = 0; j < i; ++j) {
               ChickenEntity chickenEntity = (ChickenEntity)EntityType.CHICKEN.create(this.getWorld(), SpawnReason.TRIGGERED);
               if (chickenEntity != null) {
                  chickenEntity.setBreedingAge(-24000);
                  chickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                  Optional var10000 = Optional.ofNullable((LazyRegistryEntryReference)this.getStack().get(DataComponentTypes.CHICKEN_VARIANT)).flatMap((variant) -> {
                     return variant.resolveEntry(this.getRegistryManager());
                  });
                  Objects.requireNonNull(chickenEntity);
                  var10000.ifPresent(chickenEntity::setVariant);
                  if (!chickenEntity.recalculateDimensions(EMPTY_DIMENSIONS)) {
                     break;
                  }

                  this.getWorld().spawnEntity(chickenEntity);
               }
            }
         }

         this.getWorld().sendEntityStatus(this, (byte)3);
         this.discard();
      }

   }

   protected Item getDefaultItem() {
      return Items.EGG;
   }
}
