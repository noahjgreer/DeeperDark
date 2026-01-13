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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TntMinecartEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends AbstractMinecartEntityRenderer<TntMinecartEntity, TntMinecartEntityRenderState> {
    public TntMinecartEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.TNT_MINECART);
    }

    @Override
    protected void renderBlock(TntMinecartEntityRenderState tntMinecartEntityRenderState, BlockState blockState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i) {
        float f = tntMinecartEntityRenderState.fuseTicks;
        if (f > -1.0f && f < 10.0f) {
            float g = 1.0f - f / 10.0f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            matrixStack.scale(h, h, h);
        }
        TntMinecartEntityRenderer.renderFlashingBlock(blockState, matrixStack, orderedRenderCommandQueue, i, f > -1.0f && (int)f / 5 % 2 == 0, tntMinecartEntityRenderState.outlineColor);
    }

    public static void renderFlashingBlock(BlockState state, MatrixStack matrices, OrderedRenderCommandQueue queue, int i, boolean bl, int j) {
        int k = bl ? OverlayTexture.packUv(OverlayTexture.getU(1.0f), 10) : OverlayTexture.DEFAULT_UV;
        queue.submitBlock(matrices, state, i, k, j);
    }

    @Override
    public TntMinecartEntityRenderState createRenderState() {
        return new TntMinecartEntityRenderState();
    }

    @Override
    public void updateRenderState(TntMinecartEntity tntMinecartEntity, TntMinecartEntityRenderState tntMinecartEntityRenderState, float f) {
        super.updateRenderState(tntMinecartEntity, tntMinecartEntityRenderState, f);
        tntMinecartEntityRenderState.fuseTicks = tntMinecartEntity.getFuseTicks() > -1 ? (float)tntMinecartEntity.getFuseTicks() - f + 1.0f : -1.0f;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
