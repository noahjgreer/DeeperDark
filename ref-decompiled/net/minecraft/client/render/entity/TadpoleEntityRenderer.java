/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.TadpoleEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.TadpoleEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.passive.TadpoleEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TadpoleEntityRenderer
extends MobEntityRenderer<TadpoleEntity, LivingEntityRenderState, TadpoleEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/tadpole/tadpole.png");

    public TadpoleEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new TadpoleEntityModel(context.getPart(EntityModelLayers.TADPOLE)), 0.14f);
    }

    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

