package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.SquidEntityRenderState;

@Environment(EnvType.CLIENT)
public class SquidEntityModel extends EntityModel {
   public static final ModelTransformer BABY_TRANSFORMER = ModelTransformer.scaling(0.5F);
   private final ModelPart[] tentacles = new ModelPart[8];

   public SquidEntityModel(ModelPart modelPart) {
      super(modelPart);
      Arrays.setAll(this.tentacles, (i) -> {
         return modelPart.getChild(getTentacleName(i));
      });
   }

   private static String getTentacleName(int index) {
      return "tentacle" + index;
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      Dilation dilation = new Dilation(0.02F);
      int i = true;
      modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, dilation), ModelTransform.origin(0.0F, 8.0F, 0.0F));
      int j = true;
      ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);

      for(int k = 0; k < 8; ++k) {
         double d = (double)k * Math.PI * 2.0 / 8.0;
         float f = (float)Math.cos(d) * 5.0F;
         float g = 15.0F;
         float h = (float)Math.sin(d) * 5.0F;
         d = (double)k * Math.PI * -2.0 / 8.0 + 1.5707963267948966;
         float l = (float)d;
         modelPartData.addChild(getTentacleName(k), modelPartBuilder, ModelTransform.of(f, 15.0F, h, 0.0F, l, 0.0F));
      }

      return TexturedModelData.of(modelData, 64, 32);
   }

   public void setAngles(SquidEntityRenderState squidEntityRenderState) {
      super.setAngles(squidEntityRenderState);
      ModelPart[] var2 = this.tentacles;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ModelPart modelPart = var2[var4];
         modelPart.pitch = squidEntityRenderState.tentacleAngle;
      }

   }
}
