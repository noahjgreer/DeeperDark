/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MooshroomEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer
 *  net.minecraft.client.render.entity.model.CowEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.MooshroomEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.MooshroomEntity
 *  net.minecraft.entity.passive.MooshroomEntity$Variant
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.MooshroomEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MooshroomEntityRenderer
extends AgeableMobEntityRenderer<MooshroomEntity, MooshroomEntityRenderState, CowEntityModel> {
    private static final Map<MooshroomEntity.Variant, Identifier> TEXTURES = (Map)Util.make((Object)Maps.newHashMap(), map -> {
        map.put(MooshroomEntity.Variant.BROWN, Identifier.ofVanilla((String)"textures/entity/cow/brown_mooshroom.png"));
        map.put(MooshroomEntity.Variant.RED, Identifier.ofVanilla((String)"textures/entity/cow/red_mooshroom.png"));
    });

    public MooshroomEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new CowEntityModel(context.getPart(EntityModelLayers.MOOSHROOM)), (EntityModel)new CowEntityModel(context.getPart(EntityModelLayers.MOOSHROOM_BABY)), 0.7f);
        this.addFeature((FeatureRenderer)new MooshroomMushroomFeatureRenderer((FeatureRendererContext)this, context.getBlockRenderManager()));
    }

    public Identifier getTexture(MooshroomEntityRenderState mooshroomEntityRenderState) {
        return (Identifier)TEXTURES.get(mooshroomEntityRenderState.type);
    }

    public MooshroomEntityRenderState createRenderState() {
        return new MooshroomEntityRenderState();
    }

    public void updateRenderState(MooshroomEntity mooshroomEntity, MooshroomEntityRenderState mooshroomEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)mooshroomEntity, (LivingEntityRenderState)mooshroomEntityRenderState, f);
        mooshroomEntityRenderState.type = mooshroomEntity.getVariant();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((MooshroomEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

