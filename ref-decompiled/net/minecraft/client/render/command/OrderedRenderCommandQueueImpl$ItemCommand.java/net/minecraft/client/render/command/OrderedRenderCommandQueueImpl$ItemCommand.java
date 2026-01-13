/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.ItemCommand(MatrixStack.Entry positionMatrix, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.ItemCommand.class, "pose;displayContext;lightCoords;overlayCoords;outlineColor;tintLayers;quads;renderType;foilType", "positionMatrix", "displayContext", "lightCoords", "overlayCoords", "outlineColor", "tintLayers", "quads", "renderLayer", "glintType"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.ItemCommand.class, "pose;displayContext;lightCoords;overlayCoords;outlineColor;tintLayers;quads;renderType;foilType", "positionMatrix", "displayContext", "lightCoords", "overlayCoords", "outlineColor", "tintLayers", "quads", "renderLayer", "glintType"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.ItemCommand.class, "pose;displayContext;lightCoords;overlayCoords;outlineColor;tintLayers;quads;renderType;foilType", "positionMatrix", "displayContext", "lightCoords", "overlayCoords", "outlineColor", "tintLayers", "quads", "renderLayer", "glintType"}, this, object);
    }
}
