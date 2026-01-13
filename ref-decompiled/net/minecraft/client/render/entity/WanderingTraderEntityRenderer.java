/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.WanderingTraderEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.VillagerResemblingModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ItemHolderEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.VillagerEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.WanderingTraderEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WanderingTraderEntityRenderer
extends MobEntityRenderer<WanderingTraderEntity, VillagerEntityRenderState, VillagerResemblingModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/wandering_trader.png");

    public WanderingTraderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new VillagerResemblingModel(context.getPart(EntityModelLayers.WANDERING_TRADER)), 0.5f);
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getPlayerSkinCache()));
        this.addFeature((FeatureRenderer)new VillagerHeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
        return TEXTURE;
    }

    public VillagerEntityRenderState createRenderState() {
        return new VillagerEntityRenderState();
    }

    public void updateRenderState(WanderingTraderEntity wanderingTraderEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)wanderingTraderEntity, (LivingEntityRenderState)villagerEntityRenderState, f);
        ItemHolderEntityRenderState.update((LivingEntity)wanderingTraderEntity, (ItemHolderEntityRenderState)villagerEntityRenderState, (ItemModelManager)this.itemModelResolver);
        villagerEntityRenderState.headRolling = wanderingTraderEntity.getHeadRollingTimeLeft() > 0;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((VillagerEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

