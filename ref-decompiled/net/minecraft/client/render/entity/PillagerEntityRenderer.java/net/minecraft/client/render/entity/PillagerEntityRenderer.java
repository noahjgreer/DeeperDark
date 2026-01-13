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
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PillagerEntityRenderer
extends IllagerEntityRenderer<PillagerEntity, IllagerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/pillager.png");

    public PillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.PILLAGER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<IllagerEntityRenderState, IllagerEntityModel<IllagerEntityRenderState>>(this));
    }

    @Override
    public Identifier getTexture(IllagerEntityRenderState illagerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public IllagerEntityRenderState createRenderState() {
        return new IllagerEntityRenderState();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IllagerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
