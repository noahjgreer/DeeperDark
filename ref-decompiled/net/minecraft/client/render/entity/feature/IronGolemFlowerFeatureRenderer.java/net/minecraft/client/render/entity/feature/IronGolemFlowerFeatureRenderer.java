/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class IronGolemFlowerFeatureRenderer
extends FeatureRenderer<IronGolemEntityRenderState, IronGolemEntityModel> {
    public IronGolemFlowerFeatureRenderer(FeatureRendererContext<IronGolemEntityRenderState, IronGolemEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, IronGolemEntityRenderState ironGolemEntityRenderState, float f, float g) {
        if (ironGolemEntityRenderState.lookingAtVillagerTicks == 0) {
            return;
        }
        matrixStack.push();
        ModelPart modelPart = ((IronGolemEntityModel)this.getContextModel()).getRightArm();
        modelPart.applyTransform(matrixStack);
        matrixStack.translate(-1.1875f, 1.0625f, -0.9375f);
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        float h = 0.5f;
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        orderedRenderCommandQueue.submitBlock(matrixStack, Blocks.POPPY.getDefaultState(), i, OverlayTexture.DEFAULT_UV, ironGolemEntityRenderState.outlineColor);
        matrixStack.pop();
    }
}
