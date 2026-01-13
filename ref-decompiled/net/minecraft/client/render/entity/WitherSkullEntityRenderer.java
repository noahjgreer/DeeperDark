/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.WitherSkullEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SkullEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.WitherSkullEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.WitherSkullEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.WitherSkullEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class WitherSkullEntityRenderer
extends EntityRenderer<WitherSkullEntity, WitherSkullEntityRenderState> {
    private static final Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/wither/wither_invulnerable.png");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/wither/wither.png");
    private final SkullEntityModel model;

    public WitherSkullEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new SkullEntityModel(context.getPart(EntityModelLayers.WITHER_SKULL));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 35).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    protected int getBlockLight(WitherSkullEntity witherSkullEntity, BlockPos blockPos) {
        return 15;
    }

    public void render(WitherSkullEntityRenderState witherSkullEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        orderedRenderCommandQueue.submitModel((Model)this.model, (Object)witherSkullEntityRenderState.skullState, matrixStack, this.model.getLayer(this.getTexture(witherSkullEntityRenderState)), witherSkullEntityRenderState.light, OverlayTexture.DEFAULT_UV, witherSkullEntityRenderState.outlineColor, null);
        matrixStack.pop();
        super.render((EntityRenderState)witherSkullEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    private Identifier getTexture(WitherSkullEntityRenderState state) {
        return state.charged ? INVULNERABLE_TEXTURE : TEXTURE;
    }

    public WitherSkullEntityRenderState createRenderState() {
        return new WitherSkullEntityRenderState();
    }

    public void updateRenderState(WitherSkullEntity witherSkullEntity, WitherSkullEntityRenderState witherSkullEntityRenderState, float f) {
        super.updateRenderState((Entity)witherSkullEntity, (EntityRenderState)witherSkullEntityRenderState, f);
        witherSkullEntityRenderState.charged = witherSkullEntity.isCharged();
        witherSkullEntityRenderState.skullState.poweredTicks = 0.0f;
        witherSkullEntityRenderState.skullState.yaw = witherSkullEntity.getLerpedYaw(f);
        witherSkullEntityRenderState.skullState.pitch = witherSkullEntity.getLerpedPitch(f);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

