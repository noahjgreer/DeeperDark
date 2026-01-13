/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
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
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/spider/spider.png");

    public SpiderEntityRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.SPIDER);
    }

    public SpiderEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new SpiderEntityModel(ctx.getPart(layer)), 0.8f);
        this.addFeature(new SpiderEyesFeatureRenderer<SpiderEntityModel>(this));
    }

    @Override
    protected float getLyingPositionRotationDegrees() {
        return 180.0f;
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
    public void updateRenderState(T spiderEntity, LivingEntityRenderState livingEntityRenderState, float f) {
        super.updateRenderState(spiderEntity, livingEntityRenderState, f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
