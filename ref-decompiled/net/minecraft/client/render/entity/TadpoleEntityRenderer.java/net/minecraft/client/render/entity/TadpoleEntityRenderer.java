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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TadpoleEntityRenderer
extends MobEntityRenderer<TadpoleEntity, LivingEntityRenderState, TadpoleEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/tadpole/tadpole.png");

    public TadpoleEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new TadpoleEntityModel(context.getPart(EntityModelLayers.TADPOLE)), 0.14f);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
