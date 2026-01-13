/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EndCrystalEntityRenderer
 *  net.minecraft.client.render.entity.EnderDragonEntityRenderer
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.EndCrystalEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.EndCrystalEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityRenderer
extends EntityRenderer<EndCrystalEntity, EndCrystalEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/end_crystal/end_crystal.png");
    private static final RenderLayer END_CRYSTAL = RenderLayers.entityCutoutNoCull((Identifier)TEXTURE);
    private final EndCrystalEntityModel model;

    public EndCrystalEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.model = new EndCrystalEntityModel(context.getPart(EntityModelLayers.END_CRYSTAL));
    }

    public void render(EndCrystalEntityRenderState endCrystalEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        orderedRenderCommandQueue.submitModel((Model)this.model, (Object)endCrystalEntityRenderState, matrixStack, END_CRYSTAL, endCrystalEntityRenderState.light, OverlayTexture.DEFAULT_UV, endCrystalEntityRenderState.outlineColor, null);
        matrixStack.pop();
        Vec3d vec3d = endCrystalEntityRenderState.beamOffset;
        if (vec3d != null) {
            float f = EndCrystalEntityRenderer.getYOffset((float)endCrystalEntityRenderState.age);
            float g = (float)vec3d.x;
            float h = (float)vec3d.y;
            float i = (float)vec3d.z;
            matrixStack.translate(vec3d);
            EnderDragonEntityRenderer.renderCrystalBeam((float)(-g), (float)(-h + f), (float)(-i), (float)endCrystalEntityRenderState.age, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)endCrystalEntityRenderState.light);
        }
        super.render((EntityRenderState)endCrystalEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public static float getYOffset(float f) {
        float g = MathHelper.sin((double)(f * 0.2f)) / 2.0f + 0.5f;
        g = (g * g + g) * 0.4f;
        return g - 1.4f;
    }

    public EndCrystalEntityRenderState createRenderState() {
        return new EndCrystalEntityRenderState();
    }

    public void updateRenderState(EndCrystalEntity endCrystalEntity, EndCrystalEntityRenderState endCrystalEntityRenderState, float f) {
        super.updateRenderState((Entity)endCrystalEntity, (EntityRenderState)endCrystalEntityRenderState, f);
        endCrystalEntityRenderState.age = (float)endCrystalEntity.endCrystalAge + f;
        endCrystalEntityRenderState.baseVisible = endCrystalEntity.shouldShowBottom();
        BlockPos blockPos = endCrystalEntity.getBeamTarget();
        endCrystalEntityRenderState.beamOffset = blockPos != null ? Vec3d.ofCenter((Vec3i)blockPos).subtract(endCrystalEntity.getLerpedPos(f)) : null;
    }

    public boolean shouldRender(EndCrystalEntity endCrystalEntity, Frustum frustum, double d, double e, double f) {
        return super.shouldRender((Entity)endCrystalEntity, frustum, d, e, f) || endCrystalEntity.getBeamTarget() != null;
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

