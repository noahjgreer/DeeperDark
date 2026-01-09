package net.minecraft.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PlayerAbilities {
   private static final boolean DEFAULT_INVULNERABLE = false;
   private static final boolean DEFAULT_FLYING = false;
   private static final boolean DEFAULT_ALLOW_FLYING = false;
   private static final boolean DEFAULT_CREATIVE_MODE = false;
   private static final boolean DEFAULT_ALLOW_MODIFY_WORLD = true;
   private static final float DEFAULT_FLY_SPEED = 0.05F;
   private static final float DEFAULT_WALK_SPEED = 0.1F;
   public boolean invulnerable;
   public boolean flying;
   public boolean allowFlying;
   public boolean creativeMode;
   public boolean allowModifyWorld = true;
   private float flySpeed = 0.05F;
   private float walkSpeed = 0.1F;

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float flySpeed) {
      this.flySpeed = flySpeed;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setWalkSpeed(float walkSpeed) {
      this.walkSpeed = walkSpeed;
   }

   public Packed pack() {
      return new Packed(this.invulnerable, this.flying, this.allowFlying, this.creativeMode, this.allowModifyWorld, this.flySpeed, this.walkSpeed);
   }

   public void unpack(Packed packed) {
      this.invulnerable = packed.invulnerable;
      this.flying = packed.flying;
      this.allowFlying = packed.mayFly;
      this.creativeMode = packed.instabuild;
      this.allowModifyWorld = packed.mayBuild;
      this.flySpeed = packed.flyingSpeed;
      this.walkSpeed = packed.walkingSpeed;
   }

   public static record Packed(boolean invulnerable, boolean flying, boolean mayFly, boolean instabuild, boolean mayBuild, float flyingSpeed, float walkingSpeed) {
      final boolean invulnerable;
      final boolean flying;
      final boolean mayFly;
      final boolean instabuild;
      final boolean mayBuild;
      final float flyingSpeed;
      final float walkingSpeed;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.BOOL.fieldOf("invulnerable").orElse(false).forGetter(Packed::invulnerable), Codec.BOOL.fieldOf("flying").orElse(false).forGetter(Packed::flying), Codec.BOOL.fieldOf("mayfly").orElse(false).forGetter(Packed::mayFly), Codec.BOOL.fieldOf("instabuild").orElse(false).forGetter(Packed::instabuild), Codec.BOOL.fieldOf("mayBuild").orElse(true).forGetter(Packed::mayBuild), Codec.FLOAT.fieldOf("flySpeed").orElse(0.05F).forGetter(Packed::flyingSpeed), Codec.FLOAT.fieldOf("walkSpeed").orElse(0.1F).forGetter(Packed::walkingSpeed)).apply(instance, Packed::new);
      });

      public Packed(boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, float f, float g) {
         this.invulnerable = bl;
         this.flying = bl2;
         this.mayFly = bl3;
         this.instabuild = bl4;
         this.mayBuild = bl5;
         this.flyingSpeed = f;
         this.walkingSpeed = g;
      }

      public boolean invulnerable() {
         return this.invulnerable;
      }

      public boolean flying() {
         return this.flying;
      }

      public boolean mayFly() {
         return this.mayFly;
      }

      public boolean instabuild() {
         return this.instabuild;
      }

      public boolean mayBuild() {
         return this.mayBuild;
      }

      public float flyingSpeed() {
         return this.flyingSpeed;
      }

      public float walkingSpeed() {
         return this.walkingSpeed;
      }
   }
}
