/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.WindChargeEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.WindChargeEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.projectile.AbstractWindChargeEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WindChargeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WindChargeEntityRenderer
extends EntityRenderer<AbstractWindChargeEntity, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/projectiles/wind_charge.png");
    private final WindChargeEntityModel model;

    public WindChargeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new WindChargeEntityModel(context.getPart(EntityModelLayers.WIND_CHARGE));
    }

    public void render(EntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        queue.submitModel((Model)this.model, (Object)renderState, matrices, RenderLayers.breezeWind((Identifier)TEXTURE, (float)(this.getXOffset(renderState.age) % 1.0f), (float)0.0f), renderState.light, OverlayTexture.DEFAULT_UV, renderState.outlineColor, null);
        super.render(renderState, matrices, queue, cameraState);
    }

    protected float getXOffset(float tickProgress) {
        return tickProgress * 0.03f;
    }

    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

