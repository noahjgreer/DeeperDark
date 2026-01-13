/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CopperGolemHeadBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.CopperGolemEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.CopperGolemEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.entity.passive.CopperGolemOxidationLevels;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CopperGolemEntityRenderer
extends MobEntityRenderer<CopperGolemEntity, CopperGolemEntityRenderState, CopperGolemEntityModel> {
    public CopperGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CopperGolemEntityModel(context.getPart(EntityModelLayers.COPPER_GOLEM)), 0.5f);
        this.addFeature(new EmissiveFeatureRenderer<CopperGolemEntityRenderState, CopperGolemEntityModel>(this, CopperGolemEntityRenderer.getEyeTextureGetter(), (state, tickProgress) -> 1.0f, new CopperGolemEntityModel(context.getPart(EntityModelLayers.COPPER_GOLEM)), RenderLayers::eyes, false));
        this.addFeature(new HeldItemFeatureRenderer<CopperGolemEntityRenderState, CopperGolemEntityModel>(this));
        this.addFeature(new CopperGolemHeadBlockFeatureRenderer<CopperGolemEntityRenderState, CopperGolemEntityModel>(this, state -> state.headBlockItemStack, ((CopperGolemEntityModel)this.model)::transformMatricesForBlock));
        this.addFeature(new HeadFeatureRenderer<CopperGolemEntityRenderState, CopperGolemEntityModel>(this, context.getEntityModels(), context.getPlayerSkinCache()));
    }

    @Override
    public Identifier getTexture(CopperGolemEntityRenderState copperGolemEntityRenderState) {
        return CopperGolemOxidationLevels.get(copperGolemEntityRenderState.oxidationLevel).texture();
    }

    private static Function<CopperGolemEntityRenderState, Identifier> getEyeTextureGetter() {
        return state -> CopperGolemOxidationLevels.get(state.oxidationLevel).eyeTexture();
    }

    @Override
    public CopperGolemEntityRenderState createRenderState() {
        return new CopperGolemEntityRenderState();
    }

    @Override
    public void updateRenderState(CopperGolemEntity copperGolemEntity, CopperGolemEntityRenderState copperGolemEntityRenderState, float f) {
        super.updateRenderState(copperGolemEntity, copperGolemEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(copperGolemEntity, copperGolemEntityRenderState, this.itemModelResolver, f);
        copperGolemEntityRenderState.oxidationLevel = copperGolemEntity.getOxidationLevel();
        copperGolemEntityRenderState.copperGolemState = copperGolemEntity.getState();
        copperGolemEntityRenderState.spinHeadAnimationState.copyFrom(copperGolemEntity.getSpinHeadAnimationState());
        copperGolemEntityRenderState.gettingItemAnimationState.copyFrom(copperGolemEntity.getGettingItemAnimationState());
        copperGolemEntityRenderState.gettingNoItemAnimationState.copyFrom(copperGolemEntity.getGettingNoItemAnimationState());
        copperGolemEntityRenderState.droppingItemAnimationState.copyFrom(copperGolemEntity.getDroppingItemAnimationState());
        copperGolemEntityRenderState.droppingNoItemAnimationState.copyFrom(copperGolemEntity.getDroppingNoItemAnimationState());
        copperGolemEntityRenderState.headBlockItemStack = Optional.of(copperGolemEntity.getEquippedStack(CopperGolemEntity.POPPY_SLOT)).flatMap(stack -> {
            Item item = stack.getItem();
            if (!(item instanceof BlockItem)) {
                return Optional.empty();
            }
            BlockItem blockItem = (BlockItem)item;
            BlockStateComponent blockStateComponent = stack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
            return Optional.of(blockStateComponent.applyToState(blockItem.getBlock().getDefaultState()));
        });
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CopperGolemEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
