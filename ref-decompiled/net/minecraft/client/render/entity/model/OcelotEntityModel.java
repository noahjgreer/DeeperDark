package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel extends FelineEntityModel {
   public OcelotEntityModel(ModelPart modelPart) {
      super(modelPart);
   }
}
