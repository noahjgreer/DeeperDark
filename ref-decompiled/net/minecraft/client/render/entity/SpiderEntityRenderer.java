/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.SpiderEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SpiderEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.mob.SpiderEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SpiderEntityRenderer<T extends SpiderEntity>
extends MobEntityRenderer<T, LivingEntityRenderState, SpiderEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/spider/spider.png");

    public SpiderEntityRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.SPIDER);
    }

    public SpiderEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, (EntityModel)new SpiderEntityModel(ctx.getPart(layer)), 0.8f);
        this.addFeature((FeatureRenderer)new SpiderEyesFeatureRenderer((FeatureRendererContext)this));
    }

    protected float getLyingPositionRotationDegrees() {
        return 180.0f;
    }

    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    public void updateRenderState(T spiderEntity, LivingEntityRenderState livingEntityRenderState, float f) {
        super.updateRenderState(spiderEntity, livingEntityRenderState, f);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

