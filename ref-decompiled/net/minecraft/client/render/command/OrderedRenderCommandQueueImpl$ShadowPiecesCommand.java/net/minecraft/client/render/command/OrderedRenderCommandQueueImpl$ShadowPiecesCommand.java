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
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.ShadowPiecesCommand(Matrix4f matricesEntry, float radius, List<EntityRenderState.ShadowPiece> pieces) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.ShadowPiecesCommand.class, "pose;radius;pieces", "matricesEntry", "radius", "pieces"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.ShadowPiecesCommand.class, "pose;radius;pieces", "matricesEntry", "radius", "pieces"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.ShadowPiecesCommand.class, "pose;radius;pieces", "matricesEntry", "radius", "pieces"}, this, object);
    }
}
