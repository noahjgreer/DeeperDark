/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.CustomCommandRenderer
 *  net.minecraft.client.render.command.CustomCommandRenderer$Commands
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$CustomCommand
 */
package net.minecraft.client.render.command;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.CustomCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;

@Environment(value=EnvType.CLIENT)
public class CustomCommandRenderer {
    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers) {
        Commands commands = queue.getCustomCommands();
        for (Map.Entry entry : commands.customCommands.entrySet()) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer((RenderLayer)entry.getKey());
            for (OrderedRenderCommandQueueImpl.CustomCommand customCommand : (List)entry.getValue()) {
                customCommand.customRenderer().render(customCommand.matricesEntry(), vertexConsumer);
            }
        }
    }
}

