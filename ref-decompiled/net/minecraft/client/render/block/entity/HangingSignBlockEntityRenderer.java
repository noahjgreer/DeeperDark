package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class HangingSignBlockEntityRenderer extends AbstractSignBlockEntityRenderer {
   private static final String PLANK = "plank";
   private static final String V_CHAINS = "vChains";
   private static final String NORMAL_CHAINS = "normalChains";
   private static final String CHAIN_L1 = "chainL1";
   private static final String CHAIN_L2 = "chainL2";
   private static final String CHAIN_R1 = "chainR1";
   private static final String CHAIN_R2 = "chainR2";
   private static final String BOARD = "board";
   public static final float MODEL_SCALE = 1.0F;
   private static final float TEXT_SCALE = 0.9F;
   private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, -0.3199999928474426, 0.0729999989271164);
   private final Map models;

   public HangingSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
      super(context);
      Stream stream = WoodType.stream().flatMap((woodType) -> {
         return Arrays.stream(HangingSignBlockEntityRenderer.AttachmentType.values()).map((attachmentType) -> {
            return new Variant(woodType, attachmentType);
         });
      });
      this.models = (Map)stream.collect(ImmutableMap.toImmutableMap((variant) -> {
         return variant;
      }, (variant) -> {
         return createModel(context.getLoadedEntityModels(), variant.woodType, variant.attachmentType);
      }));
   }

   public static Model createModel(LoadedEntityModels models, WoodType woodType, AttachmentType attachmentType) {
      return new Model.SinglePartModel(models.getModelPart(EntityModelLayers.createHangingSign(woodType, attachmentType)), RenderLayer::getEntityCutoutNoCull);
   }

   protected float getSignScale() {
      return 1.0F;
   }

   protected float getTextScale() {
      return 0.9F;
   }

   public static void setAngles(MatrixStack matrices, float blockRotationDegrees) {
      matrices.translate(0.5, 0.9375, 0.5);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockRotationDegrees));
      matrices.translate(0.0F, -0.3125F, 0.0F);
   }

   protected void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state) {
      setAngles(matrices, blockRotationDegrees);
   }

   protected Model getModel(BlockState state, WoodType woodType) {
      AttachmentType attachmentType = HangingSignBlockEntityRenderer.AttachmentType.from(state);
      return (Model)this.models.get(new Variant(woodType, attachmentType));
   }

   protected SpriteIdentifier getTextureId(WoodType woodType) {
      return TexturedRenderLayers.getHangingSignTextureId(woodType);
   }

   protected Vec3d getTextOffset() {
      return TEXT_OFFSET;
   }

   public static void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Model model, SpriteIdentifier texture) {
      matrices.push();
      setAngles(matrices, 0.0F);
      matrices.scale(1.0F, -1.0F, -1.0F);
      Objects.requireNonNull(model);
      VertexConsumer vertexConsumer = texture.getVertexConsumer(vertexConsumers, model::getLayer);
      model.render(matrices, vertexConsumer, light, overlay);
      matrices.pop();
   }

   public static TexturedModelData getTexturedModelData(AttachmentType attachmentType) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("board", ModelPartBuilder.create().uv(0, 12).cuboid(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), ModelTransform.NONE);
      if (attachmentType == HangingSignBlockEntityRenderer.AttachmentType.WALL) {
         modelPartData.addChild("plank", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), ModelTransform.NONE);
      }

      if (attachmentType == HangingSignBlockEntityRenderer.AttachmentType.WALL || attachmentType == HangingSignBlockEntityRenderer.AttachmentType.CEILING) {
         ModelPartData modelPartData2 = modelPartData.addChild("normalChains", ModelPartBuilder.create(), ModelTransform.NONE);
         modelPartData2.addChild("chainL1", ModelPartBuilder.create().uv(0, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), ModelTransform.of(-5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
         modelPartData2.addChild("chainL2", ModelPartBuilder.create().uv(6, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), ModelTransform.of(-5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
         modelPartData2.addChild("chainR1", ModelPartBuilder.create().uv(0, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), ModelTransform.of(5.0F, -6.0F, 0.0F, 0.0F, -0.7853982F, 0.0F));
         modelPartData2.addChild("chainR2", ModelPartBuilder.create().uv(6, 6).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), ModelTransform.of(5.0F, -6.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));
      }

      if (attachmentType == HangingSignBlockEntityRenderer.AttachmentType.CEILING_MIDDLE) {
         modelPartData.addChild("vChains", ModelPartBuilder.create().uv(14, 6).cuboid(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), ModelTransform.NONE);
      }

      return TexturedModelData.of(modelData, 64, 32);
   }

   @Environment(EnvType.CLIENT)
   public static enum AttachmentType implements StringIdentifiable {
      WALL("wall"),
      CEILING("ceiling"),
      CEILING_MIDDLE("ceiling_middle");

      private final String id;

      private AttachmentType(final String id) {
         this.id = id;
      }

      public static AttachmentType from(BlockState state) {
         if (state.getBlock() instanceof HangingSignBlock) {
            return (Boolean)state.get(Properties.ATTACHED) ? CEILING_MIDDLE : CEILING;
         } else {
            return WALL;
         }
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static AttachmentType[] method_65243() {
         return new AttachmentType[]{WALL, CEILING, CEILING_MIDDLE};
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Variant(WoodType woodType, AttachmentType attachmentType) {
      final WoodType woodType;
      final AttachmentType attachmentType;

      public Variant(WoodType woodType, AttachmentType attachmentType) {
         this.woodType = woodType;
         this.attachmentType = attachmentType;
      }

      public WoodType woodType() {
         return this.woodType;
      }

      public AttachmentType attachmentType() {
         return this.attachmentType;
      }
   }
}
