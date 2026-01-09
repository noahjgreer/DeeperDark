package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer implements BlockEntityRenderer {
   private final Model bedHead;
   private final Model bedFoot;

   public BedBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
      this(ctx.getLoadedEntityModels());
   }

   public BedBlockEntityRenderer(LoadedEntityModels models) {
      this.bedHead = new Model.SinglePartModel(models.getModelPart(EntityModelLayers.BED_HEAD), RenderLayer::getEntitySolid);
      this.bedFoot = new Model.SinglePartModel(models.getModelPart(EntityModelLayers.BED_FOOT), RenderLayer::getEntitySolid);
   }

   public static TexturedModelData getHeadTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), ModelTransform.NONE);
      modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(50, 6).cuboid(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F), ModelTransform.rotation(1.5707964F, 0.0F, 1.5707964F));
      modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(50, 18).cuboid(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F), ModelTransform.rotation(1.5707964F, 0.0F, 3.1415927F));
      return TexturedModelData.of(modelData, 64, 64);
   }

   public static TexturedModelData getFootTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), ModelTransform.NONE);
      modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(50, 0).cuboid(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), ModelTransform.rotation(1.5707964F, 0.0F, 0.0F));
      modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(50, 12).cuboid(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F), ModelTransform.rotation(1.5707964F, 0.0F, 4.712389F));
      return TexturedModelData.of(modelData, 64, 64);
   }

   public void render(BedBlockEntity bedBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
      World world = bedBlockEntity.getWorld();
      if (world != null) {
         SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getBedTextureId(bedBlockEntity.getColor());
         BlockState blockState = bedBlockEntity.getCachedState();
         DoubleBlockProperties.PropertySource propertySource = DoubleBlockProperties.toPropertySource(BlockEntityType.BED, BedBlock::getBedPart, BedBlock::getOppositePartDirection, ChestBlock.FACING, blockState, world, bedBlockEntity.getPos(), (worldx, pos) -> {
            return false;
         });
         int k = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).get(i);
         this.renderPart(matrixStack, vertexConsumerProvider, blockState.get(BedBlock.PART) == BedPart.HEAD ? this.bedHead : this.bedFoot, (Direction)blockState.get(BedBlock.FACING), spriteIdentifier, k, j, false);
      }

   }

   public void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, SpriteIdentifier textureId) {
      this.renderPart(matrices, vertexConsumers, this.bedHead, Direction.SOUTH, textureId, light, overlay, false);
      this.renderPart(matrices, vertexConsumers, this.bedFoot, Direction.SOUTH, textureId, light, overlay, true);
   }

   private void renderPart(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Model model, Direction direction, SpriteIdentifier sprite, int light, int overlay, boolean isFoot) {
      matrices.push();
      setTransforms(matrices, isFoot, direction);
      VertexConsumer vertexConsumer = sprite.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
      model.render(matrices, vertexConsumer, light, overlay);
      matrices.pop();
   }

   private static void setTransforms(MatrixStack matrices, boolean isFoot, Direction direction) {
      matrices.translate(0.0F, 0.5625F, isFoot ? -1.0F : 0.0F);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
      matrices.translate(0.5F, 0.5F, 0.5F);
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F + direction.getPositiveHorizontalDegrees()));
      matrices.translate(-0.5F, -0.5F, -0.5F);
   }

   public void collectVertices(Set vertices) {
      MatrixStack matrixStack = new MatrixStack();
      setTransforms(matrixStack, false, Direction.SOUTH);
      this.bedHead.getRootPart().collectVertices(matrixStack, vertices);
      matrixStack.loadIdentity();
      setTransforms(matrixStack, true, Direction.SOUTH);
      this.bedFoot.getRootPart().collectVertices(matrixStack, vertices);
   }
}
