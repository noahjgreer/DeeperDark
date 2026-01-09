package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class GiantEntityModel extends AbstractZombieModel {
   public GiantEntityModel(ModelPart modelPart) {
      super(modelPart);
   }
}
