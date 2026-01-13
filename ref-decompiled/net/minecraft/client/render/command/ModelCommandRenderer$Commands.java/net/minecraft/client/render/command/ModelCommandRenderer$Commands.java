/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public static class ModelCommandRenderer.Commands {
    final Map<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelCommand<?>>> opaqueModelCommands = new HashMap();
    final List<OrderedRenderCommandQueueImpl.BlendedModelCommand<?>> blendedModelCommands = new ArrayList();
    private final Set<RenderLayer> usedModelRenderLayers = new ObjectOpenHashSet();

    public void add(RenderLayer renderLayer2, OrderedRenderCommandQueueImpl.ModelCommand<?> modelCommand) {
        if (renderLayer2.getRenderPipeline().getBlendFunction().isEmpty()) {
            this.opaqueModelCommands.computeIfAbsent(renderLayer2, renderLayer -> new ArrayList()).add(modelCommand);
        } else {
            Vector3f vector3f = modelCommand.matricesEntry().getPositionMatrix().transformPosition(new Vector3f());
            this.blendedModelCommands.add(new OrderedRenderCommandQueueImpl.BlendedModelCommand(modelCommand, renderLayer2, vector3f));
        }
    }

    public void clear() {
        this.blendedModelCommands.clear();
        for (Map.Entry<RenderLayer, List<OrderedRenderCommandQueueImpl.ModelCommand<?>>> entry : this.opaqueModelCommands.entrySet()) {
            List<OrderedRenderCommandQueueImpl.ModelCommand<?>> list = entry.getValue();
            if (list.isEmpty()) continue;
            this.usedModelRenderLayers.add(entry.getKey());
            list.clear();
        }
    }

    public void nextFrame() {
        this.opaqueModelCommands.keySet().removeIf(renderLayer -> !this.usedModelRenderLayers.contains(renderLayer));
        this.usedModelRenderLayers.clear();
    }
}
