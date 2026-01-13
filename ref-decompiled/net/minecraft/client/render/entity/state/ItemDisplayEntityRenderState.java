/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.DisplayEntityRenderState
 *  net.minecraft.client.render.entity.state.ItemDisplayEntityRenderState
 *  net.minecraft.client.render.item.ItemRenderState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

@Environment(value=EnvType.CLIENT)
public class ItemDisplayEntityRenderState
extends DisplayEntityRenderState {
    public final ItemRenderState itemRenderState = new ItemRenderState();

    public boolean canRender() {
        return !this.itemRenderState.isEmpty();
    }
}

