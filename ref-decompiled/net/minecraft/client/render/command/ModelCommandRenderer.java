/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.client.render.OverlayVertexConsumer
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.ModelCommandRenderer
 *  net.minecraft.client.render.command.ModelCommandRenderer$Commands
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$BlendedModelCommand
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$ModelCommand
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.util.math.MatrixStack
 */
package net.minecraft.client.render.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class ModelCommandRenderer {
    private final MatrixStack matrices = new MatrixStack();

    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers, OutlineVertexConsumerProvider outlineVertexConsumers, VertexConsumerProvider.Immediate crumblingOverlayVertexConsumers) {
        Commands commands = queue.getModelCommands();
        this.renderAll(vertexConsumers, outlineVertexConsumers, commands.opaqueModelCommands, crumblingOverlayVertexConsumers);
        commands.blendedModelCommands.sort(Comparator.comparingDouble(modelCommand -> -modelCommand.position().lengthSquared()));
        this.renderAllBlended(vertexConsumers, outlineVertexConsumers, commands.blendedModelCommands, crumblingOverlayVertexConsumers);
    }

    private void renderAllBlended(VertexConsumerProvider.Immediate vertexConsumers, OutlineVertexConsumerProvider outlineVertexConsumers, List<OrderedRenderCommandQueueImpl.BlendedModelCommand<?>> blendedModelCommands, VertexConsumerProvider.Immediate crumblingOverlayVertexConsumers) {
        for (OrderedRenderCommandQueueImpl.BlendedModelCommand<?> blendedModelCommand : blendedModelCommands) {
            this.render(blendedModelCommand.model(), blendedModelCommand.renderType(), vertexConsumers.getBuffer(blendedModelCommand.renderType()), outlineVertexConsumers, crumblingOverlayVertexConsumers);
        }
    }

    private void renderAll(VertexConsumerProvider.Immediate vertexConsumers, OutlineVertexConsumerProvider outlineVertexConsumers, Map<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelCommand<?>>> modelCommands, VertexConsumerProvider.Immediate crumblingOverlayVertexConsumers) {
        Collection<Map.Entry<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelCommand<?>>>> iterable;
        if (SharedConstants.SHUFFLE_MODELS) {
            ArrayList list = new ArrayList(modelCommands.entrySet());
            Collections.shuffle(list);
            iterable = list;
        } else {
            iterable = modelCommands.entrySet();
        }
        for (Map.Entry entry : iterable) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer((RenderLayer)entry.getKey());
            for (OrderedRenderCommandQueueImpl.ModelCommand modelCommand : (List)entry.getValue()) {
                this.render(modelCommand, (RenderLayer)entry.getKey(), vertexConsumer, outlineVertexConsumers, crumblingOverlayVertexConsumers);
            }
        }
    }

    private <S> void render(OrderedRenderCommandQueueImpl.ModelCommand<S> model, RenderLayer renderLayer, VertexConsumer vertexConsumer, OutlineVertexConsumerProvider outlineVertexConsumers, VertexConsumerProvider.Immediate crumblingOverlayVertexConsumers) {
        VertexConsumer vertexConsumer3;
        this.matrices.push();
        this.matrices.peek().copy(model.matricesEntry());
        Model model2 = model.model();
        VertexConsumer vertexConsumer2 = model.sprite() == null ? vertexConsumer : model.sprite().getTextureSpecificVertexConsumer(vertexConsumer);
        model2.setAngles(model.state());
        model2.render(this.matrices, vertexConsumer2, model.lightCoords(), model.overlayCoords(), model.tintedColor());
        if (model.outlineColor() != 0 && (renderLayer.getAffectedOutline().isPresent() || renderLayer.isOutline())) {
            outlineVertexConsumers.setColor(model.outlineColor());
            vertexConsumer3 = outlineVertexConsumers.getBuffer(renderLayer);
            model2.render(this.matrices, model.sprite() == null ? vertexConsumer3 : model.sprite().getTextureSpecificVertexConsumer(vertexConsumer3), model.lightCoords(), model.overlayCoords(), model.tintedColor());
        }
        if (model.crumblingOverlay() != null && renderLayer.hasCrumbling()) {
            vertexConsumer3 = new OverlayVertexConsumer(crumblingOverlayVertexConsumers.getBuffer((RenderLayer)ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(model.crumblingOverlay().progress())), model.crumblingOverlay().cameraMatricesEntry(), 1.0f);
            model2.render(this.matrices, model.sprite() == null ? vertexConsumer3 : model.sprite().getTextureSpecificVertexConsumer(vertexConsumer3), model.lightCoords(), model.overlayCoords(), model.tintedColor());
        }
        this.matrices.pop();
    }
}

