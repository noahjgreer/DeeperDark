package net.minecraft.client.render.entity.state;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record EntityHitboxAndView(double viewX, double viewY, double viewZ, ImmutableList hitboxes) {
   public EntityHitboxAndView(double d, double e, double f, ImmutableList immutableList) {
      this.viewX = d;
      this.viewY = e;
      this.viewZ = f;
      this.hitboxes = immutableList;
   }

   public double viewX() {
      return this.viewX;
   }

   public double viewY() {
      return this.viewY;
   }

   public double viewZ() {
      return this.viewZ;
   }

   public ImmutableList hitboxes() {
      return this.hitboxes;
   }
}
