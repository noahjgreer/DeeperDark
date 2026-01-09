package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public abstract class EntityModel extends Model {
   public static final float field_52908 = -1.501F;

   protected EntityModel(ModelPart root) {
      this(root, RenderLayer::getEntityCutoutNoCull);
   }

   protected EntityModel(ModelPart modelPart, Function function) {
      super(modelPart, function);
   }

   public void setAngles(EntityRenderState state) {
      this.resetTransforms();
   }
}
