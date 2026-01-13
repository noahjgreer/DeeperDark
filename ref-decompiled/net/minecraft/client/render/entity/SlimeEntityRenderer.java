/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.SlimeEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SlimeEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SlimeEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.SlimeEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityRenderer
extends MobEntityRenderer<SlimeEntity, SlimeEntityRenderState, SlimeEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/slime/slime.png");

    public SlimeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new SlimeEntityModel(context.getPart(EntityModelLayers.SLIME)), 0.25f);
        this.addFeature((FeatureRenderer)new SlimeOverlayFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    protected float getShadowRadius(SlimeEntityRenderState slimeEntityRenderState) {
        return (float)slimeEntityRenderState.size * 0.25f;
    }

    protected void scale(SlimeEntityRenderState slimeEntityRenderState, MatrixStack matrixStack) {
        float f = 0.999f;
        matrixStack.scale(0.999f, 0.999f, 0.999f);
        matrixStack.translate(0.0f, 0.001f, 0.0f);
        float g = slimeEntityRenderState.size;
        float h = slimeEntityRenderState.stretch / (g * 0.5f + 1.0f);
        float i = 1.0f / (h + 1.0f);
        matrixStack.scale(i * g, 1.0f / i * g, i * g);
    }

    public Identifier getTexture(SlimeEntityRenderState slimeEntityRenderState) {
        return TEXTURE;
    }

    public SlimeEntityRenderState createRenderState() {
        return new SlimeEntityRenderState();
    }

    public void updateRenderState(SlimeEntity slimeEntity, SlimeEntityRenderState slimeEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)slimeEntity, (LivingEntityRenderState)slimeEntityRenderState, f);
        slimeEntityRenderState.stretch = MathHelper.lerp((float)f, (float)slimeEntity.lastStretch, (float)slimeEntity.stretch);
        slimeEntityRenderState.size = slimeEntity.getSize();
    }

    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((SlimeEntityRenderState)livingEntityRenderState);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((SlimeEntityRenderState)state);
    }
}

