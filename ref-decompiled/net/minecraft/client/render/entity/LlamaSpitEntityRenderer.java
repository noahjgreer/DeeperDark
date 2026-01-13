/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LlamaSpitEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LlamaSpitEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LlamaSpitEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.LlamaSpitEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LlamaSpitEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityRenderer
extends EntityRenderer<LlamaSpitEntity, LlamaSpitEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/llama/spit.png");
    private final LlamaSpitEntityModel model;

    public LlamaSpitEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new LlamaSpitEntityModel(context.getPart(EntityModelLayers.LLAMA_SPIT));
    }

    public void render(LlamaSpitEntityRenderState llamaSpitEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.translate(0.0f, 0.15f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(llamaSpitEntityRenderState.yaw - 90.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(llamaSpitEntityRenderState.pitch));
        orderedRenderCommandQueue.submitModel((Model)this.model, (Object)llamaSpitEntityRenderState, matrixStack, this.model.getLayer(TEXTURE), llamaSpitEntityRenderState.light, OverlayTexture.DEFAULT_UV, llamaSpitEntityRenderState.outlineColor, null);
        matrixStack.pop();
        super.render((EntityRenderState)llamaSpitEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public LlamaSpitEntityRenderState createRenderState() {
        return new LlamaSpitEntityRenderState();
    }

    public void updateRenderState(LlamaSpitEntity llamaSpitEntity, LlamaSpitEntityRenderState llamaSpitEntityRenderState, float f) {
        super.updateRenderState((Entity)llamaSpitEntity, (EntityRenderState)llamaSpitEntityRenderState, f);
        llamaSpitEntityRenderState.pitch = llamaSpitEntity.getLerpedPitch(f);
        llamaSpitEntityRenderState.yaw = llamaSpitEntity.getLerpedYaw(f);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

