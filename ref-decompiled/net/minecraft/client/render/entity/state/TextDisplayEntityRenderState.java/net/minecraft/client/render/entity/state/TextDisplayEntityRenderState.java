/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
    public  @Nullable DisplayEntity.TextDisplayEntity.Data data;
    public  @Nullable DisplayEntity.TextDisplayEntity.TextLines textLines;

    @Override
    public boolean canRender() {
        return this.data != null;
    }
}
