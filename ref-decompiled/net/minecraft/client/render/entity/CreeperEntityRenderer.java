/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.CreeperEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.CreeperEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.CreeperEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.CreeperEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CreeperEntityRenderer
extends MobEntityRenderer<CreeperEntity, CreeperEntityRenderState, CreeperEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/creeper/creeper.png");

    public CreeperEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new CreeperEntityModel(context.getPart(EntityModelLayers.CREEPER)), 0.5f);
        this.addFeature((FeatureRenderer)new CreeperChargeFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    protected void scale(CreeperEntityRenderState creeperEntityRenderState, MatrixStack matrixStack) {
        float f = creeperEntityRenderState.fuseTime;
        float g = 1.0f + MathHelper.sin((double)(f * 100.0f)) * f * 0.01f;
        f = MathHelper.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float h = (1.0f + f * 0.4f) * g;
        float i = (1.0f + f * 0.1f) / g;
        matrixStack.scale(h, i, h);
    }

    protected float getAnimationCounter(CreeperEntityRenderState creeperEntityRenderState) {
        float f = creeperEntityRenderState.fuseTime;
        if ((int)(f * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return MathHelper.clamp((float)f, (float)0.5f, (float)1.0f);
    }

    public Identifier getTexture(CreeperEntityRenderState creeperEntityRenderState) {
        return TEXTURE;
    }

    public CreeperEntityRenderState createRenderState() {
        return new CreeperEntityRenderState();
    }

    public void updateRenderState(CreeperEntity creeperEntity, CreeperEntityRenderState creeperEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)creeperEntity, (LivingEntityRenderState)creeperEntityRenderState, f);
        creeperEntityRenderState.fuseTime = creeperEntity.getLerpedFuseTime(f);
        creeperEntityRenderState.charged = creeperEntity.isCharged();
    }

    protected /* synthetic */ float getAnimationCounter(LivingEntityRenderState state) {
        return this.getAnimationCounter((CreeperEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

