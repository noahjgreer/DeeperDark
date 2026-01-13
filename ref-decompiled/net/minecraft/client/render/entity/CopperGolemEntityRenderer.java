/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Oxidizable$OxidationLevel
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.CopperGolemEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.CopperGolemHeadBlockFeatureRenderer
 *  net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.CopperGolemEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.ArmedEntityRenderState
 *  net.minecraft.client.render.entity.state.CopperGolemEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BlockStateComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.CopperGolemEntity
 *  net.minecraft.entity.passive.CopperGolemOxidationLevels
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Oxidizable;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CopperGolemHeadBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.CopperGolemEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.CopperGolemEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.entity.passive.CopperGolemOxidationLevels;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CopperGolemEntityRenderer
extends MobEntityRenderer<CopperGolemEntity, CopperGolemEntityRenderState, CopperGolemEntityModel> {
    public CopperGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new CopperGolemEntityModel(context.getPart(EntityModelLayers.COPPER_GOLEM)), 0.5f);
        this.addFeature((FeatureRenderer)new EmissiveFeatureRenderer((FeatureRendererContext)this, CopperGolemEntityRenderer.getEyeTextureGetter(), (state, tickProgress) -> 1.0f, (EntityModel)new CopperGolemEntityModel(context.getPart(EntityModelLayers.COPPER_GOLEM)), RenderLayers::eyes, false));
        this.addFeature((FeatureRenderer)new HeldItemFeatureRenderer((FeatureRendererContext)this));
        this.addFeature((FeatureRenderer)new CopperGolemHeadBlockFeatureRenderer((FeatureRendererContext)this, state -> state.headBlockItemStack, arg_0 -> ((CopperGolemEntityModel)((CopperGolemEntityModel)this.model)).transformMatricesForBlock(arg_0)));
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getPlayerSkinCache()));
    }

    public Identifier getTexture(CopperGolemEntityRenderState copperGolemEntityRenderState) {
        return CopperGolemOxidationLevels.get((Oxidizable.OxidationLevel)copperGolemEntityRenderState.oxidationLevel).texture();
    }

    private static Function<CopperGolemEntityRenderState, Identifier> getEyeTextureGetter() {
        return state -> CopperGolemOxidationLevels.get((Oxidizable.OxidationLevel)state.oxidationLevel).eyeTexture();
    }

    public CopperGolemEntityRenderState createRenderState() {
        return new CopperGolemEntityRenderState();
    }

    public void updateRenderState(CopperGolemEntity copperGolemEntity, CopperGolemEntityRenderState copperGolemEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)copperGolemEntity, (LivingEntityRenderState)copperGolemEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState((LivingEntity)copperGolemEntity, (ArmedEntityRenderState)copperGolemEntityRenderState, (ItemModelManager)this.itemModelResolver, (float)f);
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
            BlockStateComponent blockStateComponent = (BlockStateComponent)stack.getOrDefault(DataComponentTypes.BLOCK_STATE, (Object)BlockStateComponent.DEFAULT);
            return Optional.of(blockStateComponent.applyToState(blockItem.getBlock().getDefaultState()));
        });
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CopperGolemEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

