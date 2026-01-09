package net.minecraft.entity.damage;

import org.jetbrains.annotations.Nullable;

public record DamageRecord(DamageSource damageSource, float damage, @Nullable FallLocation fallLocation, float fallDistance) {
   public DamageRecord(DamageSource damageSource, float f, @Nullable FallLocation fallLocation, float g) {
      this.damageSource = damageSource;
      this.damage = f;
      this.fallLocation = fallLocation;
      this.fallDistance = g;
   }

   public DamageSource damageSource() {
      return this.damageSource;
   }

   public float damage() {
      return this.damage;
   }

   @Nullable
   public FallLocation fallLocation() {
      return this.fallLocation;
   }

   public float fallDistance() {
      return this.fallDistance;
   }
}
