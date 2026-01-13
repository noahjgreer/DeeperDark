/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.ParrotEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.ParrotEntityModel
 *  net.minecraft.client.render.entity.model.ParrotEntityModel$Pose
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.state.ParrotEntityRenderState
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.passive.ParrotEntity$Variant
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;

@Environment(value=EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer
extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final ParrotEntityModel model;

    public ShoulderParrotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new ParrotEntityModel(loader.getModelPart(EntityModelLayers.PARROT));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        ParrotEntity.Variant variant2;
        ParrotEntity.Variant variant = playerEntityRenderState.leftShoulderParrotVariant;
        if (variant != null) {
            this.render(matrixStack, orderedRenderCommandQueue, i, playerEntityRenderState, variant, f, g, true);
        }
        if ((variant2 = playerEntityRenderState.rightShoulderParrotVariant) != null) {
            this.render(matrixStack, orderedRenderCommandQueue, i, playerEntityRenderState, variant2, f, g, false);
        }
    }

    private void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, ParrotEntity.Variant parrotVariant, float headYaw, float headPitch, boolean left) {
        matrices.push();
        matrices.translate(left ? 0.4f : -0.4f, state.isInSneakingPose ? -1.3f : -1.5f, 0.0f);
        ParrotEntityRenderState parrotEntityRenderState = new ParrotEntityRenderState();
        parrotEntityRenderState.parrotPose = ParrotEntityModel.Pose.ON_SHOULDER;
        parrotEntityRenderState.age = state.age;
        parrotEntityRenderState.limbSwingAnimationProgress = state.limbSwingAnimationProgress;
        parrotEntityRenderState.limbSwingAmplitude = state.limbSwingAmplitude;
        parrotEntityRenderState.relativeHeadYaw = headYaw;
        parrotEntityRenderState.pitch = headPitch;
        queue.submitModel((Model)this.model, (Object)parrotEntityRenderState, matrices, this.model.getLayer(ParrotEntityRenderer.getTexture((ParrotEntity.Variant)parrotVariant)), light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
        matrices.pop();
    }
}

