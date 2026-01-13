/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class CodEntityRenderer
extends MobEntityRenderer<CodEntity, LivingEntityRenderState, CodEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/cod.png");

    public CodEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CodEntityModel(context.getPart(EntityModelLayers.COD)), 0.3f);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    protected void setupTransforms(LivingEntityRenderState state, MatrixStack matrices, float bodyYaw, float baseHeight) {
        super.setupTransforms(state, matrices, bodyYaw, baseHeight);
        float f = 4.3f * MathHelper.sin(0.6f * state.age);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f));
        if (!state.touchingWater) {
            matrices.translate(0.1f, 0.1f, -0.1f);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
