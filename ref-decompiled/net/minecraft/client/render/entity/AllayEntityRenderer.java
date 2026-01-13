/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.AllayEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.AllayEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.AllayEntityRenderState
 *  net.minecraft.client.render.entity.state.ArmedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.AllayEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AllayEntityRenderState;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class AllayEntityRenderer
extends MobEntityRenderer<AllayEntity, AllayEntityRenderState, AllayEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/allay/allay.png");

    public AllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new AllayEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4f);
        this.addFeature((FeatureRenderer)new HeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    public Identifier getTexture(AllayEntityRenderState allayEntityRenderState) {
        return TEXTURE;
    }

    public AllayEntityRenderState createRenderState() {
        return new AllayEntityRenderState();
    }

    public void updateRenderState(AllayEntity allayEntity, AllayEntityRenderState allayEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)allayEntity, (LivingEntityRenderState)allayEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState((LivingEntity)allayEntity, (ArmedEntityRenderState)allayEntityRenderState, (ItemModelManager)this.itemModelResolver, (float)f);
        allayEntityRenderState.dancing = allayEntity.isDancing();
        allayEntityRenderState.spinning = allayEntity.isSpinning();
        allayEntityRenderState.spinningAnimationTicks = allayEntity.getSpinningAnimationTicks(f);
        allayEntityRenderState.itemHoldAnimationTicks = allayEntity.getItemHoldAnimationTicks(f);
    }

    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((AllayEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

