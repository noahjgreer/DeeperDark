/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.AxolotlEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.AxolotlEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.AxolotlEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.AxolotlEntity
 *  net.minecraft.entity.passive.AxolotlEntity$Variant
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityRenderer
extends AgeableMobEntityRenderer<AxolotlEntity, AxolotlEntityRenderState, AxolotlEntityModel> {
    private static final Map<AxolotlEntity.Variant, Identifier> TEXTURES = (Map)Util.make((Object)Maps.newHashMap(), variants -> {
        for (AxolotlEntity.Variant variant : AxolotlEntity.Variant.values()) {
            variants.put(variant, Identifier.ofVanilla((String)String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", variant.getId())));
        }
    });

    public AxolotlEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL)), (EntityModel)new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL_BABY)), 0.5f);
    }

    public Identifier getTexture(AxolotlEntityRenderState axolotlEntityRenderState) {
        return (Identifier)TEXTURES.get(axolotlEntityRenderState.variant);
    }

    public AxolotlEntityRenderState createRenderState() {
        return new AxolotlEntityRenderState();
    }

    public void updateRenderState(AxolotlEntity axolotlEntity, AxolotlEntityRenderState axolotlEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)axolotlEntity, (LivingEntityRenderState)axolotlEntityRenderState, f);
        axolotlEntityRenderState.variant = axolotlEntity.getVariant();
        axolotlEntityRenderState.playingDeadValue = axolotlEntity.playingDeadFf.getValue(f);
        axolotlEntityRenderState.inWaterValue = axolotlEntity.inWaterFf.getValue(f);
        axolotlEntityRenderState.onGroundValue = axolotlEntity.onGroundFf.getValue(f);
        axolotlEntityRenderState.isMovingValue = axolotlEntity.isMovingFf.getValue(f);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((AxolotlEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

