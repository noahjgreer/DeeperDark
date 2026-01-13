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
import net.minecraft.text.Text;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.LabelCommand(Matrix4f matricesEntry, float x, float y, Text text, int lightCoords, int color, int backgroundColor, double distanceToCameraSq) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.LabelCommand.class, "pose;x;y;text;lightCoords;color;backgroundColor;distanceToCameraSq", "matricesEntry", "x", "y", "text", "lightCoords", "color", "backgroundColor", "distanceToCameraSq"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.LabelCommand.class, "pose;x;y;text;lightCoords;color;backgroundColor;distanceToCameraSq", "matricesEntry", "x", "y", "text", "lightCoords", "color", "backgroundColor", "distanceToCameraSq"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.LabelCommand.class, "pose;x;y;text;lightCoords;color;backgroundColor;distanceToCameraSq", "matricesEntry", "x", "y", "text", "lightCoords", "color", "backgroundColor", "distanceToCameraSq"}, this, object);
    }
}
