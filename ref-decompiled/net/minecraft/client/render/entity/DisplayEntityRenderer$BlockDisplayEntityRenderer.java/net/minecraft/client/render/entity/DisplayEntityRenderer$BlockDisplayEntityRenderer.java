/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public static class DisplayEntityRenderer.BlockDisplayEntityRenderer
extends DisplayEntityRenderer<DisplayEntity.BlockDisplayEntity, DisplayEntity.BlockDisplayEntity.Data, BlockDisplayEntityRenderState> {
    protected DisplayEntityRenderer.BlockDisplayEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public BlockDisplayEntityRenderState createRenderState() {
        return new BlockDisplayEntityRenderState();
    }

    @Override
    public void updateRenderState(DisplayEntity.BlockDisplayEntity blockDisplayEntity, BlockDisplayEntityRenderState blockDisplayEntityRenderState, float f) {
        super.updateRenderState(blockDisplayEntity, blockDisplayEntityRenderState, f);
        blockDisplayEntityRenderState.data = blockDisplayEntity.getData();
    }

    @Override
    public void render(BlockDisplayEntityRenderState blockDisplayEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, float f) {
        orderedRenderCommandQueue.submitBlock(matrixStack, blockDisplayEntityRenderState.data.blockState(), i, OverlayTexture.DEFAULT_UV, blockDisplayEntityRenderState.outlineColor);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return super.getShadowRadius((DisplayEntityRenderState)state);
    }

    @Override
    protected /* synthetic */ int getBlockLight(Entity entity, BlockPos pos) {
        return super.getBlockLight((DisplayEntity)entity, pos);
    }

    @Override
    protected /* synthetic */ int getSkyLight(Entity entity, BlockPos pos) {
        return super.getSkyLight((DisplayEntity)entity, pos);
    }
}
