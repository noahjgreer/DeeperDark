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
import net.minecraft.client.render.entity.model.NautilusArmorEntityModel;
import net.minecraft.client.render.entity.model.NautilusEntityModel;
import net.minecraft.client.render.entity.model.NautilusSaddleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.NautilusEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractNautilusEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class NautilusEntityRenderer<T extends AbstractNautilusEntity>
extends AgeableMobEntityRenderer<T, NautilusEntityRenderState, NautilusEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/nautilus/nautilus.png");
    private static final Identifier BABY_TEXTURE = Identifier.ofVanilla("textures/entity/nautilus/nautilus_baby.png");

    public NautilusEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new NautilusEntityModel(context.getPart(EntityModelLayers.NAUTILUS)), new NautilusEntityModel(context.getPart(EntityModelLayers.NAUTILUS_BABY)), 0.7f);
        this.addFeature(new SaddleFeatureRenderer<NautilusEntityRenderState, NautilusEntityModel, Object>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_BODY, state -> state.armorStack, new NautilusArmorEntityModel(context.getPart(EntityModelLayers.NAUTILUS_ARMOR)), null));
        this.addFeature(new SaddleFeatureRenderer<NautilusEntityRenderState, NautilusEntityModel, Object>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_SADDLE, state -> state.saddleStack, new NautilusSaddleEntityModel(context.getPart(EntityModelLayers.NAUTILUS_SADDLE)), null));
    }

    @Override
    public Identifier getTexture(NautilusEntityRenderState nautilusEntityRenderState) {
        return nautilusEntityRenderState.baby ? BABY_TEXTURE : TEXTURE;
    }

    @Override
    public NautilusEntityRenderState createRenderState() {
        return new NautilusEntityRenderState();
    }

    @Override
    public void updateRenderState(T abstractNautilusEntity, NautilusEntityRenderState nautilusEntityRenderState, float f) {
        super.updateRenderState(abstractNautilusEntity, nautilusEntityRenderState, f);
        nautilusEntityRenderState.saddleStack = ((LivingEntity)abstractNautilusEntity).getEquippedStack(EquipmentSlot.SADDLE).copy();
        nautilusEntityRenderState.armorStack = ((MobEntity)abstractNautilusEntity).getBodyArmor().copy();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((NautilusEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
