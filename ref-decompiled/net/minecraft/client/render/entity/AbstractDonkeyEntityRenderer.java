/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer
 *  net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer$Type
 *  net.minecraft.client.render.entity.AbstractHorseEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.DonkeyEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.HorseSaddleEntityModel
 *  net.minecraft.client.render.entity.state.DonkeyEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingHorseEntityRenderState
 *  net.minecraft.entity.passive.AbstractDonkeyEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractDonkeyEntityRenderer;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.HorseSaddleEntityModel;
import net.minecraft.client.render.entity.state.DonkeyEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AbstractDonkeyEntityRenderer<T extends AbstractDonkeyEntity>
extends AbstractHorseEntityRenderer<T, DonkeyEntityRenderState, DonkeyEntityModel> {
    private final Identifier texture;

    public AbstractDonkeyEntityRenderer(EntityRendererFactory.Context context, Type type) {
        super(context, (EntityModel)new DonkeyEntityModel(context.getPart(type.adultModelLayer)), (EntityModel)new DonkeyEntityModel(context.getPart(type.babyModelLayer)));
        this.texture = type.texture;
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), type.saddleLayerType, state -> state.saddleStack, (EntityModel)new HorseSaddleEntityModel(context.getPart(type.adultSaddleModelLayer)), (EntityModel)new HorseSaddleEntityModel(context.getPart(type.babySaddleModelLayer))));
    }

    public Identifier getTexture(DonkeyEntityRenderState donkeyEntityRenderState) {
        return this.texture;
    }

    public DonkeyEntityRenderState createRenderState() {
        return new DonkeyEntityRenderState();
    }

    public void updateRenderState(T abstractDonkeyEntity, DonkeyEntityRenderState donkeyEntityRenderState, float f) {
        super.updateRenderState(abstractDonkeyEntity, (LivingHorseEntityRenderState)donkeyEntityRenderState, f);
        donkeyEntityRenderState.hasChest = abstractDonkeyEntity.hasChest();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((DonkeyEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

