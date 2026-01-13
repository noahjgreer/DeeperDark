/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.AbstractMinecartEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.TntMinecartEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.MinecartEntityRenderState
 *  net.minecraft.client.render.entity.state.TntMinecartEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.vehicle.AbstractMinecartEntity
 *  net.minecraft.entity.vehicle.TntMinecartEntity
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.client.render.entity.state.TntMinecartEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TntMinecartEntityRenderer
extends AbstractMinecartEntityRenderer<TntMinecartEntity, TntMinecartEntityRenderState> {
    public TntMinecartEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.TNT_MINECART);
    }

    protected void renderBlock(TntMinecartEntityRenderState tntMinecartEntityRenderState, BlockState blockState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i) {
        float f = tntMinecartEntityRenderState.fuseTicks;
        if (f > -1.0f && f < 10.0f) {
            float g = 1.0f - f / 10.0f;
            g = MathHelper.clamp((float)g, (float)0.0f, (float)1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            matrixStack.scale(h, h, h);
        }
        TntMinecartEntityRenderer.renderFlashingBlock((BlockState)blockState, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (f > -1.0f && (int)f / 5 % 2 == 0 ? 1 : 0) != 0, (int)tntMinecartEntityRenderState.outlineColor);
    }

    public static void renderFlashingBlock(BlockState state, MatrixStack matrices, OrderedRenderCommandQueue queue, int i, boolean bl, int j) {
        int k = bl ? OverlayTexture.packUv((int)OverlayTexture.getU((float)1.0f), (int)10) : OverlayTexture.DEFAULT_UV;
        queue.submitBlock(matrices, state, i, k, j);
    }

    public TntMinecartEntityRenderState createRenderState() {
        return new TntMinecartEntityRenderState();
    }

    public void updateRenderState(TntMinecartEntity tntMinecartEntity, TntMinecartEntityRenderState tntMinecartEntityRenderState, float f) {
        super.updateRenderState((AbstractMinecartEntity)tntMinecartEntity, (MinecartEntityRenderState)tntMinecartEntityRenderState, f);
        tntMinecartEntityRenderState.fuseTicks = tntMinecartEntity.getFuseTicks() > -1 ? (float)tntMinecartEntity.getFuseTicks() - f + 1.0f : -1.0f;
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

