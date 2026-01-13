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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityRenderer
extends MobEntityRenderer<EndermanEntity, EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderman/enderman.png");
    private final Random random = Random.create();

    public EndermanEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EndermanEntityModel(context.getPart(EntityModelLayers.ENDERMAN)), 0.5f);
        this.addFeature(new EndermanEyesFeatureRenderer(this));
        this.addFeature(new EndermanBlockFeatureRenderer(this));
    }

    @Override
    public Vec3d getPositionOffset(EndermanEntityRenderState endermanEntityRenderState) {
        Vec3d vec3d = super.getPositionOffset(endermanEntityRenderState);
        if (endermanEntityRenderState.angry) {
            double d = 0.02 * (double)endermanEntityRenderState.baseScale;
            return vec3d.add(this.random.nextGaussian() * d, 0.0, this.random.nextGaussian() * d);
        }
        return vec3d;
    }

    @Override
    public Identifier getTexture(EndermanEntityRenderState endermanEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public EndermanEntityRenderState createRenderState() {
        return new EndermanEntityRenderState();
    }

    @Override
    public void updateRenderState(EndermanEntity endermanEntity, EndermanEntityRenderState endermanEntityRenderState, float f) {
        super.updateRenderState(endermanEntity, endermanEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState(endermanEntity, endermanEntityRenderState, f, this.itemModelResolver);
        endermanEntityRenderState.angry = endermanEntity.isAngry();
        endermanEntityRenderState.carriedBlock = endermanEntity.getCarriedBlock();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
