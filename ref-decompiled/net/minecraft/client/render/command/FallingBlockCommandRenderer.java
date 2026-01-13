/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.BlockRenderLayers
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.block.BlockModelRenderer
 *  net.minecraft.client.render.block.BlockRenderManager
 *  net.minecraft.client.render.block.MovingBlockRenderState
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.FallingBlockCommandRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$BlockCommand
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$BlockStateModelCommand
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$MovingBlockCommand
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockRenderView
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.command;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public class FallingBlockCommandRenderer {
    private final MatrixStack matrices = new MatrixStack();

    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers, BlockRenderManager blockRenderManager, OutlineVertexConsumerProvider outlineVertexConsumers) {
        for (OrderedRenderCommandQueueImpl.MovingBlockCommand movingBlockCommand : queue.getMovingBlockCommands()) {
            MovingBlockRenderState movingBlockRenderState = movingBlockCommand.movingBlockRenderState();
            BlockState blockState = movingBlockRenderState.blockState;
            List list = blockRenderManager.getModel(blockState).getParts(Random.create((long)blockState.getRenderingSeed(movingBlockRenderState.fallingBlockPos)));
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiplyPositionMatrix((Matrix4fc)movingBlockCommand.matricesEntry());
            blockRenderManager.getModelRenderer().render((BlockRenderView)movingBlockRenderState, list, blockState, movingBlockRenderState.entityBlockPos, matrixStack, vertexConsumers.getBuffer(BlockRenderLayers.getMovingBlockLayer((BlockState)blockState)), false, OverlayTexture.DEFAULT_UV);
        }
        for (OrderedRenderCommandQueueImpl.BlockCommand blockCommand : queue.getBlockCommands()) {
            this.matrices.push();
            this.matrices.peek().copy(blockCommand.matricesEntry());
            blockRenderManager.renderBlockAsEntity(blockCommand.state(), this.matrices, (VertexConsumerProvider)vertexConsumers, blockCommand.lightCoords(), blockCommand.overlayCoords());
            if (blockCommand.outlineColor() != 0) {
                outlineVertexConsumers.setColor(blockCommand.outlineColor());
                blockRenderManager.renderBlockAsEntity(blockCommand.state(), this.matrices, (VertexConsumerProvider)outlineVertexConsumers, blockCommand.lightCoords(), blockCommand.overlayCoords());
            }
            this.matrices.pop();
        }
        for (OrderedRenderCommandQueueImpl.BlockStateModelCommand blockStateModelCommand : queue.getBlockStateModelCommands()) {
            BlockModelRenderer.render((MatrixStack.Entry)blockStateModelCommand.matricesEntry(), (VertexConsumer)vertexConsumers.getBuffer(blockStateModelCommand.renderLayer()), (BlockStateModel)blockStateModelCommand.model(), (float)blockStateModelCommand.r(), (float)blockStateModelCommand.g(), (float)blockStateModelCommand.b(), (int)blockStateModelCommand.lightCoords(), (int)blockStateModelCommand.overlayCoords());
            if (blockStateModelCommand.outlineColor() == 0) continue;
            outlineVertexConsumers.setColor(blockStateModelCommand.outlineColor());
            BlockModelRenderer.render((MatrixStack.Entry)blockStateModelCommand.matricesEntry(), (VertexConsumer)outlineVertexConsumers.getBuffer(blockStateModelCommand.renderLayer()), (BlockStateModel)blockStateModelCommand.model(), (float)blockStateModelCommand.r(), (float)blockStateModelCommand.g(), (float)blockStateModelCommand.b(), (int)blockStateModelCommand.lightCoords(), (int)blockStateModelCommand.overlayCoords());
        }
    }
}

