package net.minecraft.client.render.entity.model;

import java.util.Iterator;
import java.util.Map;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel extends EntityModel {
   public static final ModelTransformer BABY_TRANSFORMER = LlamaEntityModel::transformBaby;
   private final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightChest;
   private final ModelPart leftChest;

   public LlamaEntityModel(ModelPart modelPart) {
      super(modelPart);
      this.head = modelPart.getChild("head");
      this.rightChest = modelPart.getChild("right_chest");
      this.leftChest = modelPart.getChild("left_chest");
      this.rightHindLeg = modelPart.getChild("right_hind_leg");
      this.leftHindLeg = modelPart.getChild("left_hind_leg");
      this.rightFrontLeg = modelPart.getChild("right_front_leg");
      this.leftFrontLeg = modelPart.getChild("left_front_leg");
   }

   public static TexturedModelData getTexturedModelData(Dilation dilation) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, dilation).uv(0, 14).cuboid("neck", -4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, dilation).uv(17, 0).cuboid("ear", -4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, dilation).uv(17, 0).cuboid("ear", 1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, dilation), ModelTransform.origin(0.0F, 7.0F, -6.0F));
      modelPartData.addChild("body", ModelPartBuilder.create().uv(29, 0).cuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, dilation), ModelTransform.of(0.0F, 5.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));
      modelPartData.addChild("right_chest", ModelPartBuilder.create().uv(45, 28).cuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, dilation), ModelTransform.of(-8.5F, 3.0F, 3.0F, 0.0F, 1.5707964F, 0.0F));
      modelPartData.addChild("left_chest", ModelPartBuilder.create().uv(45, 41).cuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, dilation), ModelTransform.of(5.5F, 3.0F, 3.0F, 0.0F, 1.5707964F, 0.0F));
      int i = true;
      int j = true;
      ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(29, 29).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, dilation);
      modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.origin(-3.5F, 10.0F, 6.0F));
      modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.origin(3.5F, 10.0F, 6.0F));
      modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.origin(-3.5F, 10.0F, -5.0F));
      modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.origin(3.5F, 10.0F, -5.0F));
      return TexturedModelData.of(modelData, 128, 64);
   }

   private static ModelData transformBaby(ModelData modelData) {
      float f = 2.0F;
      float g = 0.7F;
      float h = 1.1F;
      UnaryOperator unaryOperator = (modelTransform) -> {
         return modelTransform.moveOrigin(0.0F, 21.0F, 3.52F).scaled(0.71428573F, 0.64935064F, 0.7936508F);
      };
      UnaryOperator unaryOperator2 = (modelTransform) -> {
         return modelTransform.moveOrigin(0.0F, 33.0F, 0.0F).scaled(0.625F, 0.45454544F, 0.45454544F);
      };
      UnaryOperator unaryOperator3 = (modelTransform) -> {
         return modelTransform.moveOrigin(0.0F, 33.0F, 0.0F).scaled(0.45454544F, 0.41322312F, 0.45454544F);
      };
      ModelData modelData2 = new ModelData();
      Iterator var8 = modelData.getRoot().getChildren().iterator();

      while(var8.hasNext()) {
         Map.Entry entry = (Map.Entry)var8.next();
         String string = (String)entry.getKey();
         ModelPartData modelPartData = (ModelPartData)entry.getValue();
         UnaryOperator var10000;
         switch (string) {
            case "head":
               var10000 = unaryOperator;
               break;
            case "body":
               var10000 = unaryOperator2;
               break;
            default:
               var10000 = unaryOperator3;
         }

         UnaryOperator unaryOperator4 = var10000;
         modelData2.getRoot().addChild(string, modelPartData.applyTransformer(unaryOperator4));
      }

      return modelData2;
   }

   public void setAngles(LlamaEntityRenderState llamaEntityRenderState) {
      super.setAngles(llamaEntityRenderState);
      this.head.pitch = llamaEntityRenderState.pitch * 0.017453292F;
      this.head.yaw = llamaEntityRenderState.relativeHeadYaw * 0.017453292F;
      float f = llamaEntityRenderState.limbSwingAmplitude;
      float g = llamaEntityRenderState.limbSwingAnimationProgress;
      this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
      this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 1.4F * f;
      this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662F + 3.1415927F) * 1.4F * f;
      this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
      this.rightChest.visible = llamaEntityRenderState.hasChest;
      this.leftChest.visible = llamaEntityRenderState.hasChest;
   }
}
