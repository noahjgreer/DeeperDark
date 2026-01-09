package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record EntityDebugInfo(boolean missing, double serverEntityX, double serverEntityY, double serverEntityZ, double deltaMovementX, double deltaMovementY, double deltaMovementZ, float eyeHeight, @Nullable EntityHitboxAndView hitboxes) {
   public EntityDebugInfo(boolean missing) {
      this(missing, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0F, (EntityHitboxAndView)null);
   }

   public EntityDebugInfo(boolean bl, double d, double e, double f, double g, double h, double i, float j, @Nullable EntityHitboxAndView entityHitboxAndView) {
      this.missing = bl;
      this.serverEntityX = d;
      this.serverEntityY = e;
      this.serverEntityZ = f;
      this.deltaMovementX = g;
      this.deltaMovementY = h;
      this.deltaMovementZ = i;
      this.eyeHeight = j;
      this.hitboxes = entityHitboxAndView;
   }

   public boolean missing() {
      return this.missing;
   }

   public double serverEntityX() {
      return this.serverEntityX;
   }

   public double serverEntityY() {
      return this.serverEntityY;
   }

   public double serverEntityZ() {
      return this.serverEntityZ;
   }

   public double deltaMovementX() {
      return this.deltaMovementX;
   }

   public double deltaMovementY() {
      return this.deltaMovementY;
   }

   public double deltaMovementZ() {
      return this.deltaMovementZ;
   }

   public float eyeHeight() {
      return this.eyeHeight;
   }

   @Nullable
   public EntityHitboxAndView hitboxes() {
      return this.hitboxes;
   }
}
