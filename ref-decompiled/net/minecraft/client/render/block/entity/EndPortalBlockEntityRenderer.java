/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.EndPortalBlockEntity
 *  net.minecraft.client.render.block.entity.AbstractEndPortalBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.EndPortalBlockEntityRenderState
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.block.entity.AbstractEndPortalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.EndPortalBlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class EndPortalBlockEntityRenderer
extends AbstractEndPortalBlockEntityRenderer<EndPortalBlockEntity, EndPortalBlockEntityRenderState> {
    public EndPortalBlockEntityRenderState createRenderState() {
        return new EndPortalBlockEntityRenderState();
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

