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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.BlockStateModelCommand(MatrixStack.Entry matricesEntry, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int lightCoords, int overlayCoords, int outlineColor) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlockStateModelCommand.class, "pose;renderType;model;r;g;b;lightCoords;overlayCoords;outlineColor", "matricesEntry", "renderLayer", "model", "r", "g", "b", "lightCoords", "overlayCoords", "outlineColor"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlockStateModelCommand.class, "pose;renderType;model;r;g;b;lightCoords;overlayCoords;outlineColor", "matricesEntry", "renderLayer", "model", "r", "g", "b", "lightCoords", "overlayCoords", "outlineColor"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlockStateModelCommand.class, "pose;renderType;model;r;g;b;lightCoords;overlayCoords;outlineColor", "matricesEntry", "renderLayer", "model", "r", "g", "b", "lightCoords", "overlayCoords", "outlineColor"}, this, object);
    }
}
