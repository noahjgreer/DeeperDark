package net.minecraft.entity.projectile.thrown;

import java.util.Iterator;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SplashPotionEntity extends PotionEntity {
   public SplashPotionEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public SplashPotionEntity(World world, LivingEntity owner, ItemStack stack) {
      super(EntityType.SPLASH_POTION, world, owner, stack);
   }

   public SplashPotionEntity(World world, double x, double y, double z, ItemStack stack) {
      super(EntityType.SPLASH_POTION, world, x, y, z, stack);
   }

   protected Item getDefaultItem() {
      return Items.SPLASH_POTION;
   }

   public void spawnAreaEffectCloud(ServerWorld world, ItemStack stack, HitResult hitResult) {
      PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
      float f = (Float)stack.getOrDefault(DataComponentTypes.POTION_DURATION_SCALE, 1.0F);
      Iterable iterable = potionContentsComponent.getEffects();
      Box box = this.getBoundingBox().offset(hitResult.getPos().subtract(this.getPos()));
      Box box2 = box.expand(4.0, 2.0, 4.0);
      List list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, box2);
      float g = ProjectileUtil.getToleranceMargin(this);
      if (!list.isEmpty()) {
         Entity entity = this.getEffectCause();
         Iterator var12 = list.iterator();

         while(true) {
            LivingEntity livingEntity;
            double d;
            do {
               do {
                  if (!var12.hasNext()) {
                     return;
                  }

                  livingEntity = (LivingEntity)var12.next();
               } while(!livingEntity.isAffectedBySplashPotions());

               d = box.squaredMagnitude(livingEntity.getBoundingBox().expand((double)g));
            } while(!(d < 16.0));

            double e = 1.0 - Math.sqrt(d) / 4.0;
            Iterator var18 = iterable.iterator();

            while(var18.hasNext()) {
               StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var18.next();
               RegistryEntry registryEntry = statusEffectInstance.getEffectType();
               if (((StatusEffect)registryEntry.value()).isInstant()) {
                  ((StatusEffect)registryEntry.value()).applyInstantEffect(world, this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
               } else {
                  int i = statusEffectInstance.mapDuration((baseDuration) -> {
                     return (int)(e * (double)baseDuration * (double)f + 0.5);
                  });
                  StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(registryEntry, i, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles());
                  if (!statusEffectInstance2.isDurationBelow(20)) {
                     livingEntity.addStatusEffect(statusEffectInstance2, entity);
                  }
               }
            }
         }
      }
   }
}
