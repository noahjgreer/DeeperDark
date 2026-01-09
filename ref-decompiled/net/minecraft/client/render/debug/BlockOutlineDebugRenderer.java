package net.minecraft.client.render.debug;

import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class BlockOutlineDebugRenderer implements DebugRenderer.Renderer {
   private final MinecraftClient client;

   public BlockOutlineDebugRenderer(MinecraftClient client) {
      this.client = client;
   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      Matrix4f matrix4f = matrices.peek().getPositionMatrix();
      BlockView blockView = this.client.player.getWorld();
      BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
      Iterator var12 = BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6)).iterator();

      while(true) {
         BlockPos blockPos2;
         BlockState blockState;
         do {
            if (!var12.hasNext()) {
               return;
            }

            blockPos2 = (BlockPos)var12.next();
            blockState = blockView.getBlockState(blockPos2);
         } while(blockState.isOf(Blocks.AIR));

         VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);
         Iterator var16 = voxelShape.getBoundingBoxes().iterator();

         while(var16.hasNext()) {
            Box box = (Box)var16.next();
            Box box2 = box.offset(blockPos2).expand(0.002);
            float f = (float)(box2.minX - cameraX);
            float g = (float)(box2.minY - cameraY);
            float h = (float)(box2.minZ - cameraZ);
            float i = (float)(box2.maxX - cameraX);
            float j = (float)(box2.maxY - cameraY);
            float k = (float)(box2.maxZ - cameraZ);
            int l = -2130771968;
            VertexConsumer vertexConsumer;
            if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.WEST)) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
               vertexConsumer.vertex(matrix4f, f, g, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, g, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, j, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, j, k).color(-2130771968);
            }

            if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH)) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
               vertexConsumer.vertex(matrix4f, f, j, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, g, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, j, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, g, k).color(-2130771968);
            }

            if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.EAST)) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
               vertexConsumer.vertex(matrix4f, i, g, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, g, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, j, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, j, h).color(-2130771968);
            }

            if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.NORTH)) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
               vertexConsumer.vertex(matrix4f, i, j, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, g, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, j, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, g, h).color(-2130771968);
            }

            if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.DOWN)) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
               vertexConsumer.vertex(matrix4f, f, g, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, g, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, g, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, g, k).color(-2130771968);
            }

            if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.UP)) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
               vertexConsumer.vertex(matrix4f, f, j, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, f, j, k).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, j, h).color(-2130771968);
               vertexConsumer.vertex(matrix4f, i, j, k).color(-2130771968);
            }
         }
      }
   }
}
