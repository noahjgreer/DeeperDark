/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer$HeadTransformation
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelWithHead
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PlayerHeldItemFeatureRenderer<S extends PlayerEntityRenderState, M extends EntityModel<S> & ModelWithHead>
extends HeldItemFeatureRenderer<S, M> {
    private static final float HEAD_YAW = -0.5235988f;
    private static final float HEAD_ROLL = 1.5707964f;

    public PlayerHeldItemFeatureRenderer(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    protected void renderItem(S playerEntityRenderState, ItemRenderState itemRenderState, ItemStack itemStack, Arm arm, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i) {
        Hand hand;
        if (itemRenderState.isEmpty()) {
            return;
        }
        Hand hand2 = hand = arm == ((PlayerEntityRenderState)playerEntityRenderState).mainArm ? Hand.MAIN_HAND : Hand.OFF_HAND;
        if (((PlayerEntityRenderState)playerEntityRenderState).isUsingItem && ((PlayerEntityRenderState)playerEntityRenderState).activeHand == hand && ((PlayerEntityRenderState)playerEntityRenderState).handSwingProgress < 1.0E-5f && !((PlayerEntityRenderState)playerEntityRenderState).spyglassState.isEmpty()) {
            this.renderSpyglass(playerEntityRenderState, arm, matrixStack, orderedRenderCommandQueue, i);
        } else {
            super.renderItem(playerEntityRenderState, itemRenderState, itemStack, arm, matrixStack, orderedRenderCommandQueue, i);
        }
    }

    private void renderSpyglass(S state, Arm arm, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        matrices.push();
        this.getContextModel().getRootPart().applyTransform(matrices);
        ModelPart modelPart = ((ModelWithHead)this.getContextModel()).getHead();
        float f = modelPart.pitch;
        modelPart.pitch = MathHelper.clamp((float)modelPart.pitch, (float)-0.5235988f, (float)1.5707964f);
        modelPart.applyTransform(matrices);
        modelPart.pitch = f;
        HeadFeatureRenderer.translate((MatrixStack)matrices, (HeadFeatureRenderer.HeadTransformation)HeadFeatureRenderer.HeadTransformation.DEFAULT);
        boolean bl = arm == Arm.LEFT;
        matrices.translate((bl ? -2.5f : 2.5f) / 16.0f, -0.0625f, 0.0f);
        ((PlayerEntityRenderState)state).spyglassState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, ((PlayerEntityRenderState)state).outlineColor);
        matrices.pop();
    }
}

