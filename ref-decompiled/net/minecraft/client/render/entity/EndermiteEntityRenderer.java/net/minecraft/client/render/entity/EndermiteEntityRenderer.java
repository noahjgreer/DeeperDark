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
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EndermiteEntityRenderer
extends MobEntityRenderer<EndermiteEntity, LivingEntityRenderState, EndermiteEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/endermite.png");

    public EndermiteEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new EndermiteEntityModel(context.getPart(EntityModelLayers.ENDERMITE)), 0.3f);
    }

    @Override
    protected float getLyingPositionRotationDegrees() {
        return 180.0f;
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
