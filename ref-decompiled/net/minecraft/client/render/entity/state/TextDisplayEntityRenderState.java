/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.DisplayEntityRenderState
 *  net.minecraft.client.render.entity.state.TextDisplayEntityRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextDisplayEntityRenderState
extends DisplayEntityRenderState {
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable DisplayEntity.TextDisplayEntity.Data data;
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable DisplayEntity.TextDisplayEntity.TextLines textLines;

    public boolean canRender() {
        return this.data != null;
    }
}

