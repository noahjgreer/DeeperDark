/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.StriderEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StriderEntityRenderer
extends AgeableMobEntityRenderer<StriderEntity, StriderEntityRenderState, StriderEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider.png");
    private static final Identifier COLD_TEXTURE = Identifier.ofVanilla("textures/entity/strider/strider_cold.png");
    private static final float BABY_SHADOW_RADIUS_SCALE = 0.5f;

    public StriderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER)), new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_BABY)), 0.5f);
        this.addFeature(new SaddleFeatureRenderer<StriderEntityRenderState, StriderEntityModel, StriderEntityModel>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.STRIDER_SADDLE, state -> state.saddleStack, new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_SADDLE)), new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_BABY_SADDLE))));
    }

    @Override
    public Identifier getTexture(StriderEntityRenderState striderEntityRenderState) {
        return striderEntityRenderState.cold ? COLD_TEXTURE : TEXTURE;
    }

    @Override
    protected float getShadowRadius(StriderEntityRenderState striderEntityRenderState) {
        float f = super.getShadowRadius(striderEntityRenderState);
        if (striderEntityRenderState.baby) {
            return f * 0.5f;
        }
        return f;
    }

    @Override
    public StriderEntityRenderState createRenderState() {
        return new StriderEntityRenderState();
    }

    @Override
    public void updateRenderState(StriderEntity striderEntity, StriderEntityRenderState striderEntityRenderState, float f) {
        super.updateRenderState(striderEntity, striderEntityRenderState, f);
        striderEntityRenderState.saddleStack = striderEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        striderEntityRenderState.cold = striderEntity.isCold();
        striderEntityRenderState.hasPassengers = striderEntity.hasPassengers();
    }

    @Override
    protected boolean isShaking(StriderEntityRenderState striderEntityRenderState) {
        return super.isShaking(striderEntityRenderState) || striderEntityRenderState.cold;
    }

    @Override
    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((StriderEntityRenderState)livingEntityRenderState);
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((StriderEntityRenderState)state);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((StriderEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((StriderEntityRenderState)state);
    }
}
