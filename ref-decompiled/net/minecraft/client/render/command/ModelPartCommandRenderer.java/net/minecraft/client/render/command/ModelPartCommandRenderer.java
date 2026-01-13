/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.command;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class ModelPartCommandRenderer {
    private final MatrixStack matrices = new MatrixStack();

    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers, OutlineVertexConsumerProvider outlineVertexConsumerProvider, VertexConsumerProvider.Immediate immediate) {
        Commands commands = queue.getModelPartCommands();
        for (Map.Entry<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelPartCommand>> entry : commands.modelPartCommands.entrySet()) {
            RenderLayer renderLayer = entry.getKey();
            List<OrderedRenderCommandQueueImpl.ModelPartCommand> list = entry.getValue();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            for (OrderedRenderCommandQueueImpl.ModelPartCommand modelPartCommand : list) {
                VertexConsumer vertexConsumer3;
                VertexConsumer vertexConsumer2 = modelPartCommand.sprite() != null ? (modelPartCommand.hasGlint() ? modelPartCommand.sprite().getTextureSpecificVertexConsumer(ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, modelPartCommand.sheeted(), true)) : modelPartCommand.sprite().getTextureSpecificVertexConsumer(vertexConsumer)) : (modelPartCommand.hasGlint() ? ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, modelPartCommand.sheeted(), true) : vertexConsumer);
                this.matrices.peek().copy(modelPartCommand.matricesEntry());
                modelPartCommand.modelPart().render(this.matrices, vertexConsumer2, modelPartCommand.lightCoords(), modelPartCommand.overlayCoords(), modelPartCommand.tintedColor());
                if (modelPartCommand.outlineColor() != 0 && (renderLayer.getAffectedOutline().isPresent() || renderLayer.isOutline())) {
                    outlineVertexConsumerProvider.setColor(modelPartCommand.outlineColor());
                    vertexConsumer3 = outlineVertexConsumerProvider.getBuffer(renderLayer);
                    modelPartCommand.modelPart().render(this.matrices, modelPartCommand.sprite() == null ? vertexConsumer3 : modelPartCommand.sprite().getTextureSpecificVertexConsumer(vertexConsumer3), modelPartCommand.lightCoords(), modelPartCommand.overlayCoords(), modelPartCommand.tintedColor());
                }
                if (modelPartCommand.crumblingOverlay() == null) continue;
                vertexConsumer3 = new OverlayVertexConsumer(immediate.getBuffer(ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(modelPartCommand.crumblingOverlay().progress())), modelPartCommand.crumblingOverlay().cameraMatricesEntry(), 1.0f);
                modelPartCommand.modelPart().render(this.matrices, vertexConsumer3, modelPartCommand.lightCoords(), modelPartCommand.overlayCoords(), modelPartCommand.tintedColor());
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Commands {
        final Map<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelPartCommand>> modelPartCommands = new HashMap<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelPartCommand>>();
        private final Set<RenderLayer> modelPartLayers = new ObjectOpenHashSet();

        public void add(RenderLayer renderLayer2, OrderedRenderCommandQueueImpl.ModelPartCommand command) {
            this.modelPartCommands.computeIfAbsent(renderLayer2, renderLayer -> new ArrayList()).add(command);
        }

        public void clear() {
            for (Map.Entry<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelPartCommand>> entry : this.modelPartCommands.entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                this.modelPartLayers.add(entry.getKey());
                entry.getValue().clear();
            }
        }

        public void nextFrame() {
            this.modelPartCommands.keySet().removeIf(renderLayer -> !this.modelPartLayers.contains(renderLayer));
            this.modelPartLayers.clear();
        }
    }
}
