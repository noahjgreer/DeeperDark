/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.TurtleEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.TurtleEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.TurtleEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.TurtleEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.TurtleEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityRenderer
extends AgeableMobEntityRenderer<TurtleEntity, TurtleEntityRenderState, TurtleEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/turtle/big_sea_turtle.png");

    public TurtleEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new TurtleEntityModel(context.getPart(EntityModelLayers.TURTLE)), (EntityModel)new TurtleEntityModel(context.getPart(EntityModelLayers.TURTLE_BABY)), 0.7f);
    }

    protected float getShadowRadius(TurtleEntityRenderState turtleEntityRenderState) {
        float f = super.getShadowRadius((LivingEntityRenderState)turtleEntityRenderState);
        if (turtleEntityRenderState.baby) {
            return f * 0.83f;
        }
        return f;
    }

    public TurtleEntityRenderState createRenderState() {
        return new TurtleEntityRenderState();
    }

    public void updateRenderState(TurtleEntity turtleEntity, TurtleEntityRenderState turtleEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)turtleEntity, (LivingEntityRenderState)turtleEntityRenderState, f);
        turtleEntityRenderState.onLand = !turtleEntity.isTouchingWater() && turtleEntity.isOnGround();
        turtleEntityRenderState.diggingSand = turtleEntity.isDiggingSand();
        turtleEntityRenderState.hasEgg = !turtleEntity.isBaby() && turtleEntity.hasEgg();
    }

    public Identifier getTexture(TurtleEntityRenderState turtleEntityRenderState) {
        return TEXTURE;
    }

    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((TurtleEntityRenderState)livingEntityRenderState);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((TurtleEntityRenderState)state);
    }
}

