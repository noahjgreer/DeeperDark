package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;

@Environment(EnvType.CLIENT)
public class BoatEntityModel extends AbstractBoatEntityModel {
   private static final int field_52877 = 28;
   private static final int field_52878 = 32;
   private static final int field_52879 = 6;
   private static final int field_52880 = 20;
   private static final int field_52881 = 4;
   private static final String WATER_PATCH = "water_patch";
   private static final String BACK = "back";
   private static final String FRONT = "front";
   private static final String RIGHT = "right";
   private static final String LEFT = "left";

   public BoatEntityModel(ModelPart modelPart) {
      super(modelPart);
   }

   private static void addParts(ModelPartData modelPartData) {
      int i = true;
      int j = true;
      int k = true;
      modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), ModelTransform.of(0.0F, 3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F));
      modelPartData.addChild("back", ModelPartBuilder.create().uv(0, 19).cuboid(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F), ModelTransform.of(-15.0F, 4.0F, 4.0F, 0.0F, 4.712389F, 0.0F));
      modelPartData.addChild("front", ModelPartBuilder.create().uv(0, 27).cuboid(-8.0F, -7.0F, -1.0F, 16.0F, 6.0F, 2.0F), ModelTransform.of(15.0F, 4.0F, 0.0F, 0.0F, 1.5707964F, 0.0F));
      modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 35).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), ModelTransform.of(0.0F, 4.0F, -9.0F, 0.0F, 3.1415927F, 0.0F));
      modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 43).cuboid(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F), ModelTransform.origin(0.0F, 4.0F, 9.0F));
      int l = true;
      int m = true;
      int n = true;
      float f = -5.0F;
      modelPartData.addChild("left_paddle", ModelPartBuilder.create().uv(62, 0).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), ModelTransform.of(3.0F, -5.0F, 9.0F, 0.0F, 0.0F, 0.19634955F));
      modelPartData.addChild("right_paddle", ModelPartBuilder.create().uv(62, 20).cuboid(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F).cuboid(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F), ModelTransform.of(3.0F, -5.0F, -9.0F, 0.0F, 3.1415927F, 0.19634955F));
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      addParts(modelPartData);
      return TexturedModelData.of(modelData, 128, 64);
   }

   public static TexturedModelData getChestTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      addParts(modelPartData);
      modelPartData.addChild("chest_bottom", ModelPartBuilder.create().uv(0, 76).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F), ModelTransform.of(-2.0F, -5.0F, -6.0F, 0.0F, -1.5707964F, 0.0F));
      modelPartData.addChild("chest_lid", ModelPartBuilder.create().uv(0, 59).cuboid(0.0F, 0.0F, 0.0F, 12.0F, 4.0F, 12.0F), ModelTransform.of(-2.0F, -9.0F, -6.0F, 0.0F, -1.5707964F, 0.0F));
      modelPartData.addChild("chest_lock", ModelPartBuilder.create().uv(0, 59).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F), ModelTransform.of(-1.0F, -6.0F, -1.0F, 0.0F, -1.5707964F, 0.0F));
      return TexturedModelData.of(modelData, 128, 128);
   }

   public static TexturedModelData getBaseTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("water_patch", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F), ModelTransform.of(0.0F, -3.0F, 1.0F, 1.5707964F, 0.0F, 0.0F));
      return TexturedModelData.of(modelData, 0, 0);
   }
}
