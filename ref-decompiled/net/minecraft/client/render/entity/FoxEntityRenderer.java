/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.FoxEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.FoxEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FoxEntityRenderState
 *  net.minecraft.client.render.entity.state.ItemHolderEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.FoxEntity
 *  net.minecraft.entity.passive.FoxEntity$Variant
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class FoxEntityRenderer
extends AgeableMobEntityRenderer<FoxEntity, FoxEntityRenderState, FoxEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/fox/fox.png");
    private static final Identifier SLEEPING_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fox/fox_sleep.png");
    private static final Identifier SNOW_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fox/snow_fox.png");
    private static final Identifier SLEEPING_SNOW_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fox/snow_fox_sleep.png");

    public FoxEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new FoxEntityModel(context.getPart(EntityModelLayers.FOX)), (EntityModel)new FoxEntityModel(context.getPart(EntityModelLayers.FOX_BABY)), 0.4f);
        this.addFeature((FeatureRenderer)new FoxHeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    protected void setupTransforms(FoxEntityRenderState foxEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)foxEntityRenderState, matrixStack, f, g);
        if (foxEntityRenderState.chasing || foxEntityRenderState.walking) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-foxEntityRenderState.pitch));
        }
    }

    public Identifier getTexture(FoxEntityRenderState foxEntityRenderState) {
        if (foxEntityRenderState.type == FoxEntity.Variant.RED) {
            return foxEntityRenderState.sleeping ? SLEEPING_TEXTURE : TEXTURE;
        }
        return foxEntityRenderState.sleeping ? SLEEPING_SNOW_TEXTURE : SNOW_TEXTURE;
    }

    public FoxEntityRenderState createRenderState() {
        return new FoxEntityRenderState();
    }

    public void updateRenderState(FoxEntity foxEntity, FoxEntityRenderState foxEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)foxEntity, (LivingEntityRenderState)foxEntityRenderState, f);
        ItemHolderEntityRenderState.update((LivingEntity)foxEntity, (ItemHolderEntityRenderState)foxEntityRenderState, (ItemModelManager)this.itemModelResolver);
        foxEntityRenderState.headRoll = foxEntity.getHeadRoll(f);
        foxEntityRenderState.inSneakingPose = foxEntity.isInSneakingPose();
        foxEntityRenderState.bodyRotationHeightOffset = foxEntity.getBodyRotationHeightOffset(f);
        foxEntityRenderState.sleeping = foxEntity.isSleeping();
        foxEntityRenderState.sitting = foxEntity.isSitting();
        foxEntityRenderState.walking = foxEntity.isWalking();
        foxEntityRenderState.chasing = foxEntity.isChasing();
        foxEntityRenderState.type = foxEntity.getVariant();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FoxEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

