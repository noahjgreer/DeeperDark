package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public record EntityFlagsPredicate(Optional isOnGround, Optional isOnFire, Optional isSneaking, Optional isSprinting, Optional isSwimming, Optional isFlying, Optional isBaby) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("is_on_ground").forGetter(EntityFlagsPredicate::isOnGround), Codec.BOOL.optionalFieldOf("is_on_fire").forGetter(EntityFlagsPredicate::isOnFire), Codec.BOOL.optionalFieldOf("is_sneaking").forGetter(EntityFlagsPredicate::isSneaking), Codec.BOOL.optionalFieldOf("is_sprinting").forGetter(EntityFlagsPredicate::isSprinting), Codec.BOOL.optionalFieldOf("is_swimming").forGetter(EntityFlagsPredicate::isSwimming), Codec.BOOL.optionalFieldOf("is_flying").forGetter(EntityFlagsPredicate::isFlying), Codec.BOOL.optionalFieldOf("is_baby").forGetter(EntityFlagsPredicate::isBaby)).apply(instance, EntityFlagsPredicate::new);
   });

   public EntityFlagsPredicate(Optional optional, Optional optional2, Optional optional3, Optional optional4, Optional optional5, Optional optional6, Optional optional7) {
      this.isOnGround = optional;
      this.isOnFire = optional2;
      this.isSneaking = optional3;
      this.isSprinting = optional4;
      this.isSwimming = optional5;
      this.isFlying = optional6;
      this.isBaby = optional7;
   }

   public boolean test(Entity entity) {
      if (this.isOnGround.isPresent() && entity.isOnGround() != (Boolean)this.isOnGround.get()) {
         return false;
      } else if (this.isOnFire.isPresent() && entity.isOnFire() != (Boolean)this.isOnFire.get()) {
         return false;
      } else if (this.isSneaking.isPresent() && entity.isInSneakingPose() != (Boolean)this.isSneaking.get()) {
         return false;
      } else if (this.isSprinting.isPresent() && entity.isSprinting() != (Boolean)this.isSprinting.get()) {
         return false;
      } else if (this.isSwimming.isPresent() && entity.isSwimming() != (Boolean)this.isSwimming.get()) {
         return false;
      } else {
         if (this.isFlying.isPresent()) {
            boolean var10000;
            label54: {
               label53: {
                  if (entity instanceof LivingEntity) {
                     LivingEntity livingEntity = (LivingEntity)entity;
                     if (livingEntity.isGliding()) {
                        break label53;
                     }

                     if (livingEntity instanceof PlayerEntity) {
                        PlayerEntity playerEntity = (PlayerEntity)livingEntity;
                        if (playerEntity.getAbilities().flying) {
                           break label53;
                        }
                     }
                  }

                  var10000 = false;
                  break label54;
               }

               var10000 = true;
            }

            boolean bl = var10000;
            if (bl != (Boolean)this.isFlying.get()) {
               return false;
            }
         }

         if (this.isBaby.isPresent() && entity instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            if (livingEntity2.isBaby() != (Boolean)this.isBaby.get()) {
               return false;
            }
         }

         return true;
      }
   }

   public Optional isOnGround() {
      return this.isOnGround;
   }

   public Optional isOnFire() {
      return this.isOnFire;
   }

   public Optional isSneaking() {
      return this.isSneaking;
   }

   public Optional isSprinting() {
      return this.isSprinting;
   }

   public Optional isSwimming() {
      return this.isSwimming;
   }

   public Optional isFlying() {
      return this.isFlying;
   }

   public Optional isBaby() {
      return this.isBaby;
   }

   public static class Builder {
      private Optional isOnGround = Optional.empty();
      private Optional isOnFire = Optional.empty();
      private Optional isSneaking = Optional.empty();
      private Optional isSprinting = Optional.empty();
      private Optional isSwimming = Optional.empty();
      private Optional isFlying = Optional.empty();
      private Optional isBaby = Optional.empty();

      public static Builder create() {
         return new Builder();
      }

      public Builder onGround(Boolean onGround) {
         this.isOnGround = Optional.of(onGround);
         return this;
      }

      public Builder onFire(Boolean onFire) {
         this.isOnFire = Optional.of(onFire);
         return this;
      }

      public Builder sneaking(Boolean sneaking) {
         this.isSneaking = Optional.of(sneaking);
         return this;
      }

      public Builder sprinting(Boolean sprinting) {
         this.isSprinting = Optional.of(sprinting);
         return this;
      }

      public Builder swimming(Boolean swimming) {
         this.isSwimming = Optional.of(swimming);
         return this;
      }

      public Builder flying(Boolean flying) {
         this.isFlying = Optional.of(flying);
         return this;
      }

      public Builder isBaby(Boolean isBaby) {
         this.isBaby = Optional.of(isBaby);
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnGround, this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isFlying, this.isBaby);
      }
   }
}
