package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ColorLerper;
import net.minecraft.util.DyeColor;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderState extends LivingEntityRenderState {
   public float neckAngle;
   public float headAngle;
   public boolean sheared;
   public DyeColor color;
   public int id;

   public SheepEntityRenderState() {
      this.color = DyeColor.WHITE;
   }

   public int getRgbColor() {
      return this.isJeb() ? ColorLerper.lerpColor(ColorLerper.Type.SHEEP, this.age) : ColorLerper.Type.SHEEP.getArgb(this.color);
   }

   public boolean isJeb() {
      return this.customName != null && "jeb_".equals(this.customName.getString());
   }
}
