/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.SquidEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.SquidEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SquidEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.passive.SquidEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SquidEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer<T extends SquidEntity>
extends AgeableMobEntityRenderer<T, SquidEntityRenderState, SquidEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/squid/squid.png");

    public SquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel model, SquidEntityModel babyModel) {
        super(context, (EntityModel)model, (EntityModel)babyModel, 0.7f);
    }

    public Identifier getTexture(SquidEntityRenderState squidEntityRenderState) {
        return TEXTURE;
    }

    public SquidEntityRenderState createRenderState() {
        return new SquidEntityRenderState();
    }

    public void updateRenderState(T squidEntity, SquidEntityRenderState squidEntityRenderState, float f) {
        super.updateRenderState(squidEntity, (LivingEntityRenderState)squidEntityRenderState, f);
        squidEntityRenderState.tentacleAngle = MathHelper.lerp((float)f, (float)((SquidEntity)squidEntity).lastTentacleAngle, (float)((SquidEntity)squidEntity).tentacleAngle);
        squidEntityRenderState.tiltAngle = MathHelper.lerp((float)f, (float)((SquidEntity)squidEntity).lastTiltAngle, (float)((SquidEntity)squidEntity).tiltAngle);
        squidEntityRenderState.rollAngle = MathHelper.lerp((float)f, (float)((SquidEntity)squidEntity).lastRollAngle, (float)((SquidEntity)squidEntity).rollAngle);
    }

    protected void setupTransforms(SquidEntityRenderState squidEntityRenderState, MatrixStack matrixStack, float f, float g) {
        matrixStack.translate(0.0f, squidEntityRenderState.baby ? 0.25f : 0.5f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(squidEntityRenderState.tiltAngle));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(squidEntityRenderState.rollAngle));
        matrixStack.translate(0.0f, squidEntityRenderState.baby ? -0.6f : -1.2f, 0.0f);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SquidEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

