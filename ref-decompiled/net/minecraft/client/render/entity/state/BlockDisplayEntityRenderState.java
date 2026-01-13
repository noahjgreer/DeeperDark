/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.BlockDisplayEntityRenderState
 *  net.minecraft.client.render.entity.state.DisplayEntityRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockDisplayEntityRenderState
extends DisplayEntityRenderState {
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable DisplayEntity.BlockDisplayEntity.Data data;

    public boolean canRender() {
        return this.data != null;
    }
}

