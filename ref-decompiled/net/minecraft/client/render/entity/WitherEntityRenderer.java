/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.WitherEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.WitherEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.WitherEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.boss.WitherEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityRenderer
extends MobEntityRenderer<WitherEntity, WitherEntityRenderState, WitherEntityModel> {
    private static final Identifier INVULNERABLE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/wither/wither_invulnerable.png");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/wither/wither.png");

    public WitherEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new WitherEntityModel(context.getPart(EntityModelLayers.WITHER)), 1.0f);
        this.addFeature((FeatureRenderer)new WitherArmorFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    protected int getBlockLight(WitherEntity witherEntity, BlockPos blockPos) {
        return 15;
    }

    public Identifier getTexture(WitherEntityRenderState witherEntityRenderState) {
        int i = MathHelper.floor((float)witherEntityRenderState.invulnerableTimer);
        if (i <= 0 || i <= 80 && i / 5 % 2 == 1) {
            return TEXTURE;
        }
        return INVULNERABLE_TEXTURE;
    }

    public WitherEntityRenderState createRenderState() {
        return new WitherEntityRenderState();
    }

    protected void scale(WitherEntityRenderState witherEntityRenderState, MatrixStack matrixStack) {
        float f = 2.0f;
        if (witherEntityRenderState.invulnerableTimer > 0.0f) {
            f -= witherEntityRenderState.invulnerableTimer / 220.0f * 0.5f;
        }
        matrixStack.scale(f, f, f);
    }

    public void updateRenderState(WitherEntity witherEntity, WitherEntityRenderState witherEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)witherEntity, (LivingEntityRenderState)witherEntityRenderState, f);
        int i = witherEntity.getInvulnerableTimer();
        witherEntityRenderState.invulnerableTimer = i > 0 ? (float)i - f : 0.0f;
        System.arraycopy(witherEntity.getSideHeadPitches(), 0, witherEntityRenderState.sideHeadPitches, 0, witherEntityRenderState.sideHeadPitches.length);
        System.arraycopy(witherEntity.getSideHeadYaws(), 0, witherEntityRenderState.sideHeadYaws, 0, witherEntityRenderState.sideHeadYaws.length);
        witherEntityRenderState.armored = witherEntity.isArmored();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WitherEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

