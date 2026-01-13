/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.ItemCommandRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$ItemCommand
 *  net.minecraft.client.render.item.ItemRenderState$Glint
 *  net.minecraft.client.render.item.ItemRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 */
package net.minecraft.client.render.command;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

@Environment(value=EnvType.CLIENT)
public class ItemCommandRenderer {
    private final MatrixStack matrices = new MatrixStack();

    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers, OutlineVertexConsumerProvider outlineVertexConsumers) {
        for (OrderedRenderCommandQueueImpl.ItemCommand itemCommand : queue.getItemCommands()) {
            this.matrices.push();
            this.matrices.peek().copy(itemCommand.positionMatrix());
            ItemRenderer.renderItem((ItemDisplayContext)itemCommand.displayContext(), (MatrixStack)this.matrices, (VertexConsumerProvider)vertexConsumers, (int)itemCommand.lightCoords(), (int)itemCommand.overlayCoords(), (int[])itemCommand.tintLayers(), (List)itemCommand.quads(), (RenderLayer)itemCommand.renderLayer(), (ItemRenderState.Glint)itemCommand.glintType());
            if (itemCommand.outlineColor() != 0) {
                outlineVertexConsumers.setColor(itemCommand.outlineColor());
                ItemRenderer.renderItem((ItemDisplayContext)itemCommand.displayContext(), (MatrixStack)this.matrices, (VertexConsumerProvider)outlineVertexConsumers, (int)itemCommand.lightCoords(), (int)itemCommand.overlayCoords(), (int[])itemCommand.tintLayers(), (List)itemCommand.quads(), (RenderLayer)itemCommand.renderLayer(), (ItemRenderState.Glint)ItemRenderState.Glint.NONE);
            }
            this.matrices.pop();
        }
    }
}

