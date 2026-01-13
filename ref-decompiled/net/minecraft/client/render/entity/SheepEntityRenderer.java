/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.SheepEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer
 *  net.minecraft.client.render.entity.feature.SheepWoolUndercoatFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SheepEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SheepEntityRenderState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.SheepEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolUndercoatFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SheepEntityRenderer
extends AgeableMobEntityRenderer<SheepEntity, SheepEntityRenderState, SheepEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/sheep/sheep.png");

    public SheepEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new SheepEntityModel(context.getPart(EntityModelLayers.SHEEP)), (EntityModel)new SheepEntityModel(context.getPart(EntityModelLayers.SHEEP_BABY)), 0.7f);
        this.addFeature((FeatureRenderer)new SheepWoolUndercoatFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
        this.addFeature((FeatureRenderer)new SheepWoolFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    public Identifier getTexture(SheepEntityRenderState sheepEntityRenderState) {
        return TEXTURE;
    }

    public SheepEntityRenderState createRenderState() {
        return new SheepEntityRenderState();
    }

    public void updateRenderState(SheepEntity sheepEntity, SheepEntityRenderState sheepEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)sheepEntity, (LivingEntityRenderState)sheepEntityRenderState, f);
        sheepEntityRenderState.headAngle = sheepEntity.getHeadAngle(f);
        sheepEntityRenderState.neckAngle = sheepEntity.getNeckAngle(f);
        sheepEntityRenderState.sheared = sheepEntity.isSheared();
        sheepEntityRenderState.color = sheepEntity.getColor();
        sheepEntityRenderState.rainbow = SheepEntityRenderer.nameEquals((Entity)sheepEntity, (String)"jeb_");
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SheepEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

