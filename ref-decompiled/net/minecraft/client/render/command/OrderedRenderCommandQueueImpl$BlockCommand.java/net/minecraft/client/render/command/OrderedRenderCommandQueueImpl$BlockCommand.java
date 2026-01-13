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
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.BlockCommand(MatrixStack.Entry matricesEntry, BlockState state, int lightCoords, int overlayCoords, int outlineColor) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlockCommand.class, "pose;state;lightCoords;overlayCoords;outlineColor", "matricesEntry", "state", "lightCoords", "overlayCoords", "outlineColor"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlockCommand.class, "pose;state;lightCoords;overlayCoords;outlineColor", "matricesEntry", "state", "lightCoords", "overlayCoords", "outlineColor"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlockCommand.class, "pose;state;lightCoords;overlayCoords;outlineColor", "matricesEntry", "state", "lightCoords", "overlayCoords", "outlineColor"}, this, object);
    }
}
