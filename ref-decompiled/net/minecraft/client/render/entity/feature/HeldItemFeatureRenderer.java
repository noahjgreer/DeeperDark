/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelWithArms
 *  net.minecraft.client.render.entity.state.ArmedEntityRenderState
 *  net.minecraft.client.render.entity.state.Lancing
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Arm
 *  net.minecraft.util.SwingAnimationType
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class HeldItemFeatureRenderer<S extends ArmedEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    public HeldItemFeatureRenderer(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S armedEntityRenderState, float f, float g) {
        this.renderItem(armedEntityRenderState, ((ArmedEntityRenderState)armedEntityRenderState).rightHandItemState, ((ArmedEntityRenderState)armedEntityRenderState).rightHandItem, Arm.RIGHT, matrixStack, orderedRenderCommandQueue, i);
        this.renderItem(armedEntityRenderState, ((ArmedEntityRenderState)armedEntityRenderState).leftHandItemState, ((ArmedEntityRenderState)armedEntityRenderState).leftHandItem, Arm.LEFT, matrixStack, orderedRenderCommandQueue, i);
    }

    protected void renderItem(S entityState, ItemRenderState itemState, ItemStack stack, Arm arm, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        float f;
        if (itemState.isEmpty()) {
            return;
        }
        matrices.push();
        ((ModelWithArms)this.getContextModel()).setArmAngle(entityState, arm, matrices);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        boolean bl = arm == Arm.LEFT;
        matrices.translate((float)(bl ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        if (((ArmedEntityRenderState)entityState).handSwingProgress > 0.0f && ((ArmedEntityRenderState)entityState).mainArm == arm && ((ArmedEntityRenderState)entityState).swingAnimationType == SwingAnimationType.STAB) {
            Lancing.method_75395(entityState, (MatrixStack)matrices);
        }
        if ((f = entityState.getItemUseTime(arm)) != 0.0f) {
            (arm == Arm.RIGHT ? ((ArmedEntityRenderState)entityState).rightArmPose : ((ArmedEntityRenderState)entityState).leftArmPose).method_75382(entityState, matrices, f, arm, stack);
        }
        itemState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, ((ArmedEntityRenderState)entityState).outlineColor);
        matrices.pop();
    }
}

