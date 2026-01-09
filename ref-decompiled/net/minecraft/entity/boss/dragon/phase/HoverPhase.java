package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class HoverPhase extends AbstractPhase {
   @Nullable
   private Vec3d target;

   public HoverPhase(EnderDragonEntity enderDragonEntity) {
      super(enderDragonEntity);
   }

   public void serverTick(ServerWorld world) {
      if (this.target == null) {
         this.target = this.dragon.getPos();
      }

   }

   public boolean isSittingOrHovering() {
      return true;
   }

   public void beginPhase() {
      this.target = null;
   }

   public float getMaxYAcceleration() {
      return 1.0F;
   }

   @Nullable
   public Vec3d getPathTarget() {
      return this.target;
   }

   public PhaseType getType() {
      return PhaseType.HOVER;
   }
}
