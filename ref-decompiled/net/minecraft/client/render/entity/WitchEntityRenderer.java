/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.WitchEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.WitchEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ItemHolderEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.WitchEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.WitchEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
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
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitchEntityRenderer
extends MobEntityRenderer<WitchEntity, WitchEntityRenderState, WitchEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/witch.png");

    public WitchEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new WitchEntityModel(context.getPart(EntityModelLayers.WITCH)), 0.5f);
        this.addFeature((FeatureRenderer)new WitchHeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    public Identifier getTexture(WitchEntityRenderState witchEntityRenderState) {
        return TEXTURE;
    }

    public WitchEntityRenderState createRenderState() {
        return new WitchEntityRenderState();
    }

    public void updateRenderState(WitchEntity witchEntity, WitchEntityRenderState witchEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)witchEntity, (LivingEntityRenderState)witchEntityRenderState, f);
        ItemHolderEntityRenderState.update((LivingEntity)witchEntity, (ItemHolderEntityRenderState)witchEntityRenderState, (ItemModelManager)this.itemModelResolver);
        witchEntityRenderState.id = witchEntity.getId();
        ItemStack itemStack = witchEntity.getMainHandStack();
        witchEntityRenderState.holdingItem = !itemStack.isEmpty();
        witchEntityRenderState.holdingPotion = itemStack.isOf(Items.POTION);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WitchEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

