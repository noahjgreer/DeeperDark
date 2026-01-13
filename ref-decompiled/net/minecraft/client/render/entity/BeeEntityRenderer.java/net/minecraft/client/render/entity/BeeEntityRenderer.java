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
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BeeEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BeeEntityRenderer
extends AgeableMobEntityRenderer<BeeEntity, BeeEntityRenderState, BeeEntityModel> {
    private static final Identifier ANGRY_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_angry.png");
    private static final Identifier ANGRY_NECTAR_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_angry_nectar.png");
    private static final Identifier PASSIVE_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee.png");
    private static final Identifier NECTAR_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_nectar.png");

    public BeeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BeeEntityModel(context.getPart(EntityModelLayers.BEE)), new BeeEntityModel(context.getPart(EntityModelLayers.BEE_BABY)), 0.4f);
    }

    @Override
    public Identifier getTexture(BeeEntityRenderState beeEntityRenderState) {
        if (beeEntityRenderState.angry) {
            if (beeEntityRenderState.hasNectar) {
                return ANGRY_NECTAR_TEXTURE;
            }
            return ANGRY_TEXTURE;
        }
        if (beeEntityRenderState.hasNectar) {
            return NECTAR_TEXTURE;
        }
        return PASSIVE_TEXTURE;
    }

    @Override
    public BeeEntityRenderState createRenderState() {
        return new BeeEntityRenderState();
    }

    @Override
    public void updateRenderState(BeeEntity beeEntity, BeeEntityRenderState beeEntityRenderState, float f) {
        super.updateRenderState(beeEntity, beeEntityRenderState, f);
        beeEntityRenderState.bodyPitch = beeEntity.getBodyPitch(f);
        beeEntityRenderState.hasStinger = !beeEntity.hasStung();
        beeEntityRenderState.stoppedOnGround = beeEntity.isOnGround() && beeEntity.getVelocity().lengthSquared() < 1.0E-7;
        beeEntityRenderState.angry = beeEntity.hasAngerTime();
        beeEntityRenderState.hasNectar = beeEntity.hasNectar();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BeeEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
