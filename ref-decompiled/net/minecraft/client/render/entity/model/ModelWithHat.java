package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface ModelWithHat {
   void setHatVisible(boolean visible);

   void rotateArms(MatrixStack stack);
}
