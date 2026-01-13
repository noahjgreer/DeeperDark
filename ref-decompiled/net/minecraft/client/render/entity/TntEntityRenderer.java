/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.TntEntityRenderer
 *  net.minecraft.client.render.entity.TntMinecartEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.TntEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.TntEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class TntEntityRenderer
extends EntityRenderer<TntEntity, TntEntityRenderState> {
    public TntEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
    }

    public void render(TntEntityRenderState tntEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.translate(0.0f, 0.5f, 0.0f);
        float f = tntEntityRenderState.fuse;
        if (tntEntityRenderState.fuse < 10.0f) {
            float g = 1.0f - tntEntityRenderState.fuse / 10.0f;
            g = MathHelper.clamp((float)g, (float)0.0f, (float)1.0f);
            g *= g;
            g *= g;
            float h = 1.0f + g * 0.3f;
            matrixStack.scale(h, h, h);
        }
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrixStack.translate(-0.5f, -0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        if (tntEntityRenderState.blockState != null) {
            TntMinecartEntityRenderer.renderFlashingBlock((BlockState)tntEntityRenderState.blockState, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)tntEntityRenderState.light, ((int)f / 5 % 2 == 0 ? 1 : 0) != 0, (int)tntEntityRenderState.outlineColor);
        }
        matrixStack.pop();
        super.render((EntityRenderState)tntEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public TntEntityRenderState createRenderState() {
        return new TntEntityRenderState();
    }

    public void updateRenderState(TntEntity tntEntity, TntEntityRenderState tntEntityRenderState, float f) {
        super.updateRenderState((Entity)tntEntity, (EntityRenderState)tntEntityRenderState, f);
        tntEntityRenderState.fuse = (float)tntEntity.getFuse() - f + 1.0f;
        tntEntityRenderState.blockState = tntEntity.getBlockState();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

