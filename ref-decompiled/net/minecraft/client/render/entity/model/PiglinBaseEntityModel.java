package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PiglinBaseEntityModel extends BipedEntityModel {
   private static final String LEFT_SLEEVE = "left_sleeve";
   private static final String RIGHT_SLEEVE = "right_sleeve";
   private static final String LEFT_PANTS = "left_pants";
   private static final String RIGHT_PANTS = "right_pants";
   public final ModelPart leftSleeve;
   public final ModelPart rightSleeve;
   public final ModelPart leftPants;
   public final ModelPart rightPants;
   public final ModelPart jacket;
   public final ModelPart rightEar;
   public final ModelPart leftEar;

   public PiglinBaseEntityModel(ModelPart modelPart) {
      super(modelPart, RenderLayer::getEntityTranslucent);
      this.leftSleeve = this.leftArm.getChild("left_sleeve");
      this.rightSleeve = this.rightArm.getChild("right_sleeve");
      this.leftPants = this.leftLeg.getChild("left_pants");
      this.rightPants = this.rightLeg.getChild("right_pants");
      this.jacket = this.body.getChild("jacket");
      this.rightEar = this.head.getChild("right_ear");
      this.leftEar = this.head.getChild("left_ear");
   }

   public static ModelData getModelData(Dilation dilation) {
      ModelData modelData = PlayerEntityModel.getTexturedModelData(dilation, false);
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), ModelTransform.NONE);
      ModelPartData modelPartData2 = getModelPartData(dilation, modelData);
      modelPartData2.addChild("hat");
      return modelData;
   }

   public static ModelPartData getModelPartData(Dilation dilation, ModelData playerModelData) {
      ModelPartData modelPartData = playerModelData.getRoot();
      ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, dilation).uv(31, 1).cuboid(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, dilation).uv(2, 4).cuboid(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, dilation).uv(2, 0).cuboid(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, dilation), ModelTransform.NONE);
      modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(51, 6).cuboid(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, dilation), ModelTransform.of(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, -0.5235988F));
      modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(39, 6).cuboid(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, dilation), ModelTransform.of(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, 0.5235988F));
      return modelPartData2;
   }

   public void setAngles(BipedEntityRenderState bipedEntityRenderState) {
      super.setAngles(bipedEntityRenderState);
      float f = bipedEntityRenderState.limbSwingAnimationProgress;
      float g = bipedEntityRenderState.limbSwingAmplitude;
      float h = 0.5235988F;
      float i = bipedEntityRenderState.age * 0.1F + f * 0.5F;
      float j = 0.08F + g * 0.4F;
      this.leftEar.roll = -0.5235988F - MathHelper.cos(i * 1.2F) * j;
      this.rightEar.roll = 0.5235988F + MathHelper.cos(i) * j;
   }

   public void setVisible(boolean visible) {
      super.setVisible(visible);
      this.leftSleeve.visible = visible;
      this.rightSleeve.visible = visible;
      this.leftPants.visible = visible;
      this.rightPants.visible = visible;
      this.jacket.visible = visible;
   }
}
