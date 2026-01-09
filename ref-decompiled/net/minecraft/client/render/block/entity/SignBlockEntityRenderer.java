package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer extends AbstractSignBlockEntityRenderer {
   public static final float SCALE = 0.6666667F;
   private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, 0.3333333432674408, 0.046666666865348816);
   private final Map typeToModelPair;

   public SignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
      super(ctx);
      this.typeToModelPair = (Map)WoodType.stream().collect(ImmutableMap.toImmutableMap((signType) -> {
         return signType;
      }, (signType) -> {
         return new SignModelPair(createSignModel(ctx.getLoadedEntityModels(), signType, true), createSignModel(ctx.getLoadedEntityModels(), signType, false));
      }));
   }

   protected Model getModel(BlockState state, WoodType woodType) {
      SignModelPair signModelPair = (SignModelPair)this.typeToModelPair.get(woodType);
      return state.getBlock() instanceof SignBlock ? signModelPair.standing() : signModelPair.wall();
   }

   protected SpriteIdentifier getTextureId(WoodType woodType) {
      return TexturedRenderLayers.getSignTextureId(woodType);
   }

   protected float getSignScale() {
      return 0.6666667F;
   }

   protected float getTextScale() {
      return 0.6666667F;
   }

   private static void setAngles(MatrixStack matrices, float blockRotationDegrees) {
      matrices.translate(0.5F, 0.5F, 0.5F);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockRotationDegrees));
   }

   protected void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state) {
      setAngles(matrices, blockRotationDegrees);
      if (!(state.getBlock() instanceof SignBlock)) {
         matrices.translate(0.0F, -0.3125F, -0.4375F);
      }

   }

   protected Vec3d getTextOffset() {
      return TEXT_OFFSET;
   }

   public static void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Model model, SpriteIdentifier texture) {
      matrices.push();
      setTransformsForItem(matrices);
      Objects.requireNonNull(model);
      VertexConsumer vertexConsumer = texture.getVertexConsumer(vertexConsumers, model::getLayer);
      model.render(matrices, vertexConsumer, light, overlay);
      matrices.pop();
   }

   public static void setTransformsForItem(MatrixStack matrices) {
      setAngles(matrices, 0.0F);
      matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);
   }

   public static Model createSignModel(LoadedEntityModels models, WoodType type, boolean standing) {
      EntityModelLayer entityModelLayer = standing ? EntityModelLayers.createStandingSign(type) : EntityModelLayers.createWallSign(type);
      return new Model.SinglePartModel(models.getModelPart(entityModelLayer), RenderLayer::getEntityCutoutNoCull);
   }

   public static TexturedModelData getTexturedModelData(boolean standing) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("sign", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), ModelTransform.NONE);
      if (standing) {
         modelPartData.addChild("stick", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), ModelTransform.NONE);
      }

      return TexturedModelData.of(modelData, 64, 32);
   }

   @Environment(EnvType.CLIENT)
   private static record SignModelPair(Model standing, Model wall) {
      SignModelPair(Model model, Model model2) {
         this.standing = model;
         this.wall = model2;
      }

      public Model standing() {
         return this.standing;
      }

      public Model wall() {
         return this.wall;
      }
   }
}
