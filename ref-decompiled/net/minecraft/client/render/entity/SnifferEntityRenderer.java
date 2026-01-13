/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.SnifferEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SnifferEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SnifferEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.SnifferEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnifferEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SnifferEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public class SnifferEntityRenderer
extends AgeableMobEntityRenderer<SnifferEntity, SnifferEntityRenderState, SnifferEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/sniffer/sniffer.png");

    public SnifferEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new SnifferEntityModel(context.getPart(EntityModelLayers.SNIFFER)), (EntityModel)new SnifferEntityModel(context.getPart(EntityModelLayers.SNIFFER_BABY)), 1.1f);
    }

    public Identifier getTexture(SnifferEntityRenderState snifferEntityRenderState) {
        return TEXTURE;
    }

    public SnifferEntityRenderState createRenderState() {
        return new SnifferEntityRenderState();
    }

    public void updateRenderState(SnifferEntity snifferEntity, SnifferEntityRenderState snifferEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)snifferEntity, (LivingEntityRenderState)snifferEntityRenderState, f);
        snifferEntityRenderState.searching = snifferEntity.isSearching();
        snifferEntityRenderState.diggingAnimationState.copyFrom(snifferEntity.diggingAnimationState);
        snifferEntityRenderState.sniffingAnimationState.copyFrom(snifferEntity.sniffingAnimationState);
        snifferEntityRenderState.risingAnimationState.copyFrom(snifferEntity.risingAnimationState);
        snifferEntityRenderState.feelingHappyAnimationState.copyFrom(snifferEntity.feelingHappyAnimationState);
        snifferEntityRenderState.scentingAnimationState.copyFrom(snifferEntity.scentingAnimationState);
    }

    protected Box getBoundingBox(SnifferEntity snifferEntity) {
        return super.getBoundingBox((LivingEntity)snifferEntity).expand((double)0.6f);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SnifferEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

