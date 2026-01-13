/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EndermanEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer
 *  net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EndermanEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.EndermanEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.EndermanEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityRenderer
extends MobEntityRenderer<EndermanEntity, EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/enderman/enderman.png");
    private final Random random = Random.create();

    public EndermanEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new EndermanEntityModel(context.getPart(EntityModelLayers.ENDERMAN)), 0.5f);
        this.addFeature((FeatureRenderer)new EndermanEyesFeatureRenderer((FeatureRendererContext)this));
        this.addFeature((FeatureRenderer)new EndermanBlockFeatureRenderer((FeatureRendererContext)this));
    }

    public Vec3d getPositionOffset(EndermanEntityRenderState endermanEntityRenderState) {
        Vec3d vec3d = super.getPositionOffset((EntityRenderState)endermanEntityRenderState);
        if (endermanEntityRenderState.angry) {
            double d = 0.02 * (double)endermanEntityRenderState.baseScale;
            return vec3d.add(this.random.nextGaussian() * d, 0.0, this.random.nextGaussian() * d);
        }
        return vec3d;
    }

    public Identifier getTexture(EndermanEntityRenderState endermanEntityRenderState) {
        return TEXTURE;
    }

    public EndermanEntityRenderState createRenderState() {
        return new EndermanEntityRenderState();
    }

    public void updateRenderState(EndermanEntity endermanEntity, EndermanEntityRenderState endermanEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)endermanEntity, (LivingEntityRenderState)endermanEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState((LivingEntity)endermanEntity, (BipedEntityRenderState)endermanEntityRenderState, (float)f, (ItemModelManager)this.itemModelResolver);
        endermanEntityRenderState.angry = endermanEntity.isAngry();
        endermanEntityRenderState.carriedBlock = endermanEntity.getCarriedBlock();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

