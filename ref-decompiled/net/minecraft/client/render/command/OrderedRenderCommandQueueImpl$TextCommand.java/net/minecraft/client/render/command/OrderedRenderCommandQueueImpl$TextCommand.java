/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.render.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.TextCommand(Matrix4f matricesEntry, float x, float y, OrderedText text, boolean dropShadow, TextRenderer.TextLayerType layerType, int lightCoords, int color, int backgroundColor, int outlineColor) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.TextCommand.class, "pose;x;y;string;dropShadow;displayMode;lightCoords;color;backgroundColor;outlineColor", "matricesEntry", "x", "y", "text", "dropShadow", "layerType", "lightCoords", "color", "backgroundColor", "outlineColor"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.TextCommand.class, "pose;x;y;string;dropShadow;displayMode;lightCoords;color;backgroundColor;outlineColor", "matricesEntry", "x", "y", "text", "dropShadow", "layerType", "lightCoords", "color", "backgroundColor", "outlineColor"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.TextCommand.class, "pose;x;y;string;dropShadow;displayMode;lightCoords;color;backgroundColor;outlineColor", "matricesEntry", "x", "y", "text", "dropShadow", "layerType", "lightCoords", "color", "backgroundColor", "outlineColor"}, this, object);
    }
}
