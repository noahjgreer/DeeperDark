package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class HorseEntityModel extends AbstractHorseEntityModel {
   public HorseEntityModel(ModelPart modelPart) {
      super(modelPart);
   }
}
