/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.CamelEntityRenderer
 *  net.minecraft.client.render.entity.CamelHuskEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.CamelEntityModel
 *  net.minecraft.client.render.entity.model.CamelSaddleEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.CamelEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.CamelEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.CamelEntityModel;
import net.minecraft.client.render.entity.model.CamelSaddleEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CamelEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CamelHuskEntityRenderer
extends CamelEntityRenderer {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/camel/camel_husk.png");

    public CamelHuskEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    protected SaddleFeatureRenderer<CamelEntityRenderState, CamelEntityModel, CamelSaddleEntityModel> createSaddleFeatureRenderer(EntityRendererFactory.Context context) {
        return new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.CAMEL_HUSK_SADDLE, state -> state.saddleStack, (EntityModel)new CamelSaddleEntityModel(context.getPart(EntityModelLayers.CAMEL_HUSK)), (EntityModel)new CamelSaddleEntityModel(context.getPart(EntityModelLayers.CAMEL_HUSK_BABY)));
    }

    public Identifier getTexture(CamelEntityRenderState camelEntityRenderState) {
        return TEXTURE;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CamelEntityRenderState)state);
    }
}

