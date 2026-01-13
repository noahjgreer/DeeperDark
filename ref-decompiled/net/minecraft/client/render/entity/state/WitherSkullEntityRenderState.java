/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel$SkullModelState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.WitherSkullEntityRenderState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(value=EnvType.CLIENT)
public class WitherSkullEntityRenderState
extends EntityRenderState {
    public boolean charged;
    public final SkullBlockEntityModel.SkullModelState skullState = new SkullBlockEntityModel.SkullModelState();
}

