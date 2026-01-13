/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.command;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;

@Environment(value=EnvType.CLIENT)
public class TextCommandRenderer {
    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        for (OrderedRenderCommandQueueImpl.TextCommand textCommand : queue.getTextCommands()) {
            if (textCommand.outlineColor() == 0) {
                textRenderer.draw(textCommand.text(), textCommand.x(), textCommand.y(), textCommand.color(), textCommand.dropShadow(), textCommand.matricesEntry(), (VertexConsumerProvider)vertexConsumers, textCommand.layerType(), textCommand.backgroundColor(), textCommand.lightCoords());
                continue;
            }
            textRenderer.drawWithOutline(textCommand.text(), textCommand.x(), textCommand.y(), textCommand.color(), textCommand.outlineColor(), textCommand.matricesEntry(), vertexConsumers, textCommand.lightCoords());
        }
    }
}
