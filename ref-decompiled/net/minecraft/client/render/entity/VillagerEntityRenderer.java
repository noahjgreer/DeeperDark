/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.VillagerEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer$HeadTransformation
 *  net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer
 *  net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.VillagerResemblingModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ItemHolderEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.VillagerEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VillagerEntityRenderer
extends AgeableMobEntityRenderer<VillagerEntity, VillagerEntityRenderState, VillagerResemblingModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/villager/villager.png");
    public static final HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(-0.1171875f, -0.07421875f, 1.0f);

    public VillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER)), (EntityModel)new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER_BABY)), 0.5f);
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getPlayerSkinCache(), HEAD_TRANSFORMATION));
        this.addFeature((FeatureRenderer)new VillagerClothingFeatureRenderer((FeatureRendererContext)this, context.getResourceManager(), "villager", (EntityModel)new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER_NO_HAT)), (EntityModel)new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER_BABY_NO_HAT))));
        this.addFeature((FeatureRenderer)new VillagerHeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
        return TEXTURE;
    }

    protected float getShadowRadius(VillagerEntityRenderState villagerEntityRenderState) {
        float f = super.getShadowRadius((LivingEntityRenderState)villagerEntityRenderState);
        if (villagerEntityRenderState.baby) {
            return f * 0.5f;
        }
        return f;
    }

    public VillagerEntityRenderState createRenderState() {
        return new VillagerEntityRenderState();
    }

    public void updateRenderState(VillagerEntity villagerEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)villagerEntity, (LivingEntityRenderState)villagerEntityRenderState, f);
        ItemHolderEntityRenderState.update((LivingEntity)villagerEntity, (ItemHolderEntityRenderState)villagerEntityRenderState, (ItemModelManager)this.itemModelResolver);
        villagerEntityRenderState.headRolling = villagerEntity.getHeadRollingTimeLeft() > 0;
        villagerEntityRenderState.villagerData = villagerEntity.getVillagerData();
    }

    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((VillagerEntityRenderState)livingEntityRenderState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((VillagerEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((VillagerEntityRenderState)state);
    }
}

