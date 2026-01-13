/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.DolphinEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.DolphinEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.DolphinEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ItemHolderEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.DolphinEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityRenderer
extends AgeableMobEntityRenderer<DolphinEntity, DolphinEntityRenderState, DolphinEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/dolphin.png");

    public DolphinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN)), (EntityModel)new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN_BABY)), 0.7f);
        this.addFeature((FeatureRenderer)new DolphinHeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    public Identifier getTexture(DolphinEntityRenderState dolphinEntityRenderState) {
        return TEXTURE;
    }

    public DolphinEntityRenderState createRenderState() {
        return new DolphinEntityRenderState();
    }

    public void updateRenderState(DolphinEntity dolphinEntity, DolphinEntityRenderState dolphinEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)dolphinEntity, (LivingEntityRenderState)dolphinEntityRenderState, f);
        ItemHolderEntityRenderState.update((LivingEntity)dolphinEntity, (ItemHolderEntityRenderState)dolphinEntityRenderState, (ItemModelManager)this.itemModelResolver);
        dolphinEntityRenderState.moving = dolphinEntity.getVelocity().horizontalLengthSquared() > 1.0E-7;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((DolphinEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

