/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.client.render.OverlayVertexConsumer
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.ModelPartCommandRenderer
 *  net.minecraft.client.render.command.ModelPartCommandRenderer$Commands
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$ModelPartCommand
 *  net.minecraft.client.render.item.ItemRenderer
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.util.math.MatrixStack
 */
package net.minecraft.client.render.command;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.ModelPartCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class ModelPartCommandRenderer {
    private final MatrixStack matrices = new MatrixStack();

    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers, OutlineVertexConsumerProvider outlineVertexConsumerProvider, VertexConsumerProvider.Immediate immediate) {
        Commands commands = queue.getModelPartCommands();
        for (Map.Entry entry : commands.modelPartCommands.entrySet()) {
            RenderLayer renderLayer = (RenderLayer)entry.getKey();
            List list = (List)entry.getValue();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            for (OrderedRenderCommandQueueImpl.ModelPartCommand modelPartCommand : list) {
                VertexConsumer vertexConsumer3;
                VertexConsumer vertexConsumer2 = modelPartCommand.sprite() != null ? (modelPartCommand.hasGlint() ? modelPartCommand.sprite().getTextureSpecificVertexConsumer(ItemRenderer.getItemGlintConsumer((VertexConsumerProvider)vertexConsumers, (RenderLayer)renderLayer, (boolean)modelPartCommand.sheeted(), (boolean)true)) : modelPartCommand.sprite().getTextureSpecificVertexConsumer(vertexConsumer)) : (modelPartCommand.hasGlint() ? ItemRenderer.getItemGlintConsumer((VertexConsumerProvider)vertexConsumers, (RenderLayer)renderLayer, (boolean)modelPartCommand.sheeted(), (boolean)true) : vertexConsumer);
                this.matrices.peek().copy(modelPartCommand.matricesEntry());
                modelPartCommand.modelPart().render(this.matrices, vertexConsumer2, modelPartCommand.lightCoords(), modelPartCommand.overlayCoords(), modelPartCommand.tintedColor());
                if (modelPartCommand.outlineColor() != 0 && (renderLayer.getAffectedOutline().isPresent() || renderLayer.isOutline())) {
                    outlineVertexConsumerProvider.setColor(modelPartCommand.outlineColor());
                    vertexConsumer3 = outlineVertexConsumerProvider.getBuffer(renderLayer);
                    modelPartCommand.modelPart().render(this.matrices, modelPartCommand.sprite() == null ? vertexConsumer3 : modelPartCommand.sprite().getTextureSpecificVertexConsumer(vertexConsumer3), modelPartCommand.lightCoords(), modelPartCommand.overlayCoords(), modelPartCommand.tintedColor());
                }
                if (modelPartCommand.crumblingOverlay() == null) continue;
                vertexConsumer3 = new OverlayVertexConsumer(immediate.getBuffer((RenderLayer)ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(modelPartCommand.crumblingOverlay().progress())), modelPartCommand.crumblingOverlay().cameraMatricesEntry(), 1.0f);
                modelPartCommand.modelPart().render(this.matrices, vertexConsumer3, modelPartCommand.lightCoords(), modelPartCommand.overlayCoords(), modelPartCommand.tintedColor());
            }
        }
    }
}

