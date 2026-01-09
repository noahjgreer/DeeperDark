package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPhase implements Phase {
   protected final EnderDragonEntity dragon;

   public AbstractPhase(EnderDragonEntity dragon) {
      this.dragon = dragon;
   }

   public boolean isSittingOrHovering() {
      return false;
   }

   public void clientTick() {
   }

   public void serverTick(ServerWorld world) {
   }

   public void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source, @Nullable PlayerEntity player) {
   }

   public void beginPhase() {
   }

   public void endPhase() {
   }

   public float getMaxYAcceleration() {
      return 0.6F;
   }

   @Nullable
   public Vec3d getPathTarget() {
      return null;
   }

   public float modifyDamageTaken(DamageSource damageSource, float damage) {
      return damage;
   }

   public float getYawAcceleration() {
      float f = (float)this.dragon.getVelocity().horizontalLength() + 1.0F;
      float g = Math.min(f, 40.0F);
      return 0.7F / g / f;
   }
}
