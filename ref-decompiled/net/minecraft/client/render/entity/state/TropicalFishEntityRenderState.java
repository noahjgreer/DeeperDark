package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderState extends LivingEntityRenderState {
   public TropicalFishEntity.Pattern variety;
   public int baseColor;
   public int patternColor;

   public TropicalFishEntityRenderState() {
      this.variety = TropicalFishEntity.Pattern.FLOPPER;
      this.baseColor = -1;
      this.patternColor = -1;
   }
}
