package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class CatEntityModel extends FelineEntityModel {
   public static final ModelTransformer CAT_TRANSFORMER = ModelTransformer.scaling(0.8F);

   public CatEntityModel(ModelPart modelPart) {
      super(modelPart);
   }
}
