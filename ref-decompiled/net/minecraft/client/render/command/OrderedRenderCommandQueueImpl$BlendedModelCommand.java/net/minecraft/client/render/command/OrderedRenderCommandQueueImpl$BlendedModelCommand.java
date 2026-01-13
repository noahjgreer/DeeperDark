/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.BlendedModelCommand<S>(OrderedRenderCommandQueueImpl.ModelCommand<S> model, RenderLayer renderType, Vector3f position) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlendedModelCommand.class, "modelSubmit;renderType;position", "model", "renderType", "position"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlendedModelCommand.class, "modelSubmit;renderType;position", "model", "renderType", "position"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.BlendedModelCommand.class, "modelSubmit;renderType;position", "model", "renderType", "position"}, this, object);
    }
}
