/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.NautilusEntityRenderer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.NautilusArmorEntityModel
 *  net.minecraft.client.render.entity.model.NautilusEntityModel
 *  net.minecraft.client.render.entity.model.NautilusSaddleEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.NautilusEntityRenderState
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.passive.AbstractNautilusEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.NautilusArmorEntityModel;
import net.minecraft.client.render.entity.model.NautilusEntityModel;
import net.minecraft.client.render.entity.model.NautilusSaddleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.NautilusEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractNautilusEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class NautilusEntityRenderer<T extends AbstractNautilusEntity>
extends AgeableMobEntityRenderer<T, NautilusEntityRenderState, NautilusEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/nautilus/nautilus.png");
    private static final Identifier BABY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/nautilus/nautilus_baby.png");

    public NautilusEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new NautilusEntityModel(context.getPart(EntityModelLayers.NAUTILUS)), (EntityModel)new NautilusEntityModel(context.getPart(EntityModelLayers.NAUTILUS_BABY)), 0.7f);
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_BODY, state -> state.armorStack, (EntityModel)new NautilusArmorEntityModel(context.getPart(EntityModelLayers.NAUTILUS_ARMOR)), null));
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_SADDLE, state -> state.saddleStack, (EntityModel)new NautilusSaddleEntityModel(context.getPart(EntityModelLayers.NAUTILUS_SADDLE)), null));
    }

    public Identifier getTexture(NautilusEntityRenderState nautilusEntityRenderState) {
        return nautilusEntityRenderState.baby ? BABY_TEXTURE : TEXTURE;
    }

    public NautilusEntityRenderState createRenderState() {
        return new NautilusEntityRenderState();
    }

    public void updateRenderState(T abstractNautilusEntity, NautilusEntityRenderState nautilusEntityRenderState, float f) {
        super.updateRenderState(abstractNautilusEntity, (LivingEntityRenderState)nautilusEntityRenderState, f);
        nautilusEntityRenderState.saddleStack = abstractNautilusEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        nautilusEntityRenderState.armorStack = abstractNautilusEntity.getBodyArmor().copy();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((NautilusEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

