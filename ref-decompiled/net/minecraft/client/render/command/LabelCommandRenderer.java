/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.LabelCommandRenderer
 *  net.minecraft.client.render.command.LabelCommandRenderer$Commands
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$LabelCommand
 */
package net.minecraft.client.render.command;

import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.LabelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;

@Environment(value=EnvType.CLIENT)
public class LabelCommandRenderer {
    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers, TextRenderer renderer) {
        Commands commands = queue.getLabelCommands();
        commands.seethroughLabels.sort(Comparator.comparing(OrderedRenderCommandQueueImpl.LabelCommand::distanceToCameraSq).reversed());
        for (OrderedRenderCommandQueueImpl.LabelCommand labelCommand : commands.seethroughLabels) {
            renderer.draw(labelCommand.text(), labelCommand.x(), labelCommand.y(), labelCommand.color(), false, labelCommand.matricesEntry(), (VertexConsumerProvider)vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, labelCommand.backgroundColor(), labelCommand.lightCoords());
        }
        for (OrderedRenderCommandQueueImpl.LabelCommand labelCommand : commands.normalLabels) {
            renderer.draw(labelCommand.text(), labelCommand.x(), labelCommand.y(), labelCommand.color(), false, labelCommand.matricesEntry(), (VertexConsumerProvider)vertexConsumers, TextRenderer.TextLayerType.NORMAL, labelCommand.backgroundColor(), labelCommand.lightCoords());
        }
    }
}

