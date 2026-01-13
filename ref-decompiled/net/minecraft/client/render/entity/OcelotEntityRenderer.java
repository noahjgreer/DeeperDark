/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.OcelotEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.OcelotEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FelineEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.OcelotEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityRenderer
extends AgeableMobEntityRenderer<OcelotEntity, FelineEntityRenderState, OcelotEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/cat/ocelot.png");

    public OcelotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new OcelotEntityModel(context.getPart(EntityModelLayers.OCELOT)), (EntityModel)new OcelotEntityModel(context.getPart(EntityModelLayers.OCELOT_BABY)), 0.4f);
    }

    public Identifier getTexture(FelineEntityRenderState felineEntityRenderState) {
        return TEXTURE;
    }

    public FelineEntityRenderState createRenderState() {
        return new FelineEntityRenderState();
    }

    public void updateRenderState(OcelotEntity ocelotEntity, FelineEntityRenderState felineEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)ocelotEntity, (LivingEntityRenderState)felineEntityRenderState, f);
        felineEntityRenderState.inSneakingPose = ocelotEntity.isInSneakingPose();
        felineEntityRenderState.sprinting = ocelotEntity.isSprinting();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FelineEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

