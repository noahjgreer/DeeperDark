package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer implements BlockEntityRenderer {
   private final ChestBlockModel singleChest;
   private final ChestBlockModel doubleChestLeft;
   private final ChestBlockModel doubleChestRight;
   private final boolean christmas = isAroundChristmas();

   public ChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
      this.singleChest = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
      this.doubleChestLeft = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
      this.doubleChestRight = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));
   }

   public static boolean isAroundChristmas() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
   }

   public void render(BlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
      World world = entity.getWorld();
      boolean bl = world != null;
      BlockState blockState = bl ? entity.getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
      ChestType chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? (ChestType)blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
      Block block = blockState.getBlock();
      if (block instanceof AbstractChestBlock abstractChestBlock) {
         boolean bl2 = chestType != ChestType.SINGLE;
         matrices.push();
         float f = ((Direction)blockState.get(ChestBlock.FACING)).getPositiveHorizontalDegrees();
         matrices.translate(0.5F, 0.5F, 0.5F);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
         matrices.translate(-0.5F, -0.5F, -0.5F);
         DoubleBlockProperties.PropertySource propertySource;
         if (bl) {
            propertySource = abstractChestBlock.getBlockEntitySource(blockState, world, entity.getPos(), true);
         } else {
            propertySource = DoubleBlockProperties.PropertyRetriever::getFallback;
         }

         float g = ((Float2FloatFunction)propertySource.apply(ChestBlock.getAnimationProgressRetriever((LidOpenable)entity))).get(tickProgress);
         g = 1.0F - g;
         g = 1.0F - g * g * g;
         int i = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).applyAsInt(light);
         SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTextureId(entity, chestType, this.christmas);
         VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
         if (bl2) {
            if (chestType == ChestType.LEFT) {
               this.render(matrices, vertexConsumer, this.doubleChestLeft, g, i, overlay);
            } else {
               this.render(matrices, vertexConsumer, this.doubleChestRight, g, i, overlay);
            }
         } else {
            this.render(matrices, vertexConsumer, this.singleChest, g, i, overlay);
         }

         matrices.pop();
      }
   }

   private void render(MatrixStack matrices, VertexConsumer vertices, ChestBlockModel model, float animationProgress, int light, int overlay) {
      model.setLockAndLidPitch(animationProgress);
      model.render(matrices, vertices, light, overlay);
   }
}
