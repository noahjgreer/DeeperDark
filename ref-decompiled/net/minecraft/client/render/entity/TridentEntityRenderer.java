/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.TridentEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.TridentEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.TridentEntityRenderState
 *  net.minecraft.client.render.item.ItemRenderer
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.TridentEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Unit
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class TridentEntityRenderer
extends EntityRenderer<TridentEntity, TridentEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/trident.png");
    private final TridentEntityModel model;

    public TridentEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new TridentEntityModel(context.getPart(EntityModelLayers.TRIDENT));
    }

    public void render(TridentEntityRenderState tridentEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(tridentEntityRenderState.yaw - 90.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(tridentEntityRenderState.pitch + 90.0f));
        List list = ItemRenderer.getGlintRenderLayers((RenderLayer)this.model.getLayer(TEXTURE), (boolean)false, (boolean)tridentEntityRenderState.enchanted);
        for (int i = 0; i < list.size(); ++i) {
            orderedRenderCommandQueue.getBatchingQueue(i).submitModel((Model)this.model, (Object)Unit.INSTANCE, matrixStack, (RenderLayer)list.get(i), tridentEntityRenderState.light, OverlayTexture.DEFAULT_UV, -1, null, tridentEntityRenderState.outlineColor, null);
        }
        matrixStack.pop();
        super.render((EntityRenderState)tridentEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public TridentEntityRenderState createRenderState() {
        return new TridentEntityRenderState();
    }

    public void updateRenderState(TridentEntity tridentEntity, TridentEntityRenderState tridentEntityRenderState, float f) {
        super.updateRenderState((Entity)tridentEntity, (EntityRenderState)tridentEntityRenderState, f);
        tridentEntityRenderState.yaw = tridentEntity.getLerpedYaw(f);
        tridentEntityRenderState.pitch = tridentEntity.getLerpedPitch(f);
        tridentEntityRenderState.enchanted = tridentEntity.isEnchanted();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

