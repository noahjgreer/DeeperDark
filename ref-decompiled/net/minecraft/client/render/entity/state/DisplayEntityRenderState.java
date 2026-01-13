/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.DisplayEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class DisplayEntityRenderState
extends EntityRenderState {
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable DisplayEntity.RenderState displayRenderState;
    public float lerpProgress;
    public float yaw;
    public float pitch;
    public float cameraYaw;
    public float cameraPitch;

    public abstract boolean canRender();
}

