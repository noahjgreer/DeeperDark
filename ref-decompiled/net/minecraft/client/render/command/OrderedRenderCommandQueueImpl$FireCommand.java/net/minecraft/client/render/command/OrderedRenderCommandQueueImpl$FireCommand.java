/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 */
package net.minecraft.client.render.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.FireCommand(MatrixStack.Entry matricesEntry, EntityRenderState renderState, Quaternionf rotation) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.FireCommand.class, "pose;entityRenderState;rotation", "matricesEntry", "renderState", "rotation"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.FireCommand.class, "pose;entityRenderState;rotation", "matricesEntry", "renderState", "rotation"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.FireCommand.class, "pose;entityRenderState;rotation", "matricesEntry", "renderState", "rotation"}, this, object);
    }
}
