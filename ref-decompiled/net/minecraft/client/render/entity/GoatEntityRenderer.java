/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.GoatEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.GoatEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.GoatEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.GoatEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GoatEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GoatEntityRenderer
extends AgeableMobEntityRenderer<GoatEntity, GoatEntityRenderState, GoatEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/goat/goat.png");

    public GoatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new GoatEntityModel(context.getPart(EntityModelLayers.GOAT)), (EntityModel)new GoatEntityModel(context.getPart(EntityModelLayers.GOAT_BABY)), 0.7f);
    }

    public Identifier getTexture(GoatEntityRenderState goatEntityRenderState) {
        return TEXTURE;
    }

    public GoatEntityRenderState createRenderState() {
        return new GoatEntityRenderState();
    }

    public void updateRenderState(GoatEntity goatEntity, GoatEntityRenderState goatEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)goatEntity, (LivingEntityRenderState)goatEntityRenderState, f);
        goatEntityRenderState.hasLeftHorn = goatEntity.hasLeftHorn();
        goatEntityRenderState.hasRightHorn = goatEntity.hasRightHorn();
        goatEntityRenderState.headPitch = goatEntity.getHeadPitch();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GoatEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

