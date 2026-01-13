/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.RabbitEntityRenderer
 *  net.minecraft.client.render.entity.RabbitEntityRenderer$1
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.RabbitEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.RabbitEntityRenderState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.RabbitEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.RabbitEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RabbitEntityRenderer
extends AgeableMobEntityRenderer<RabbitEntity, RabbitEntityRenderState, RabbitEntityModel> {
    private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/brown.png");
    private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/white.png");
    private static final Identifier BLACK_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/black.png");
    private static final Identifier GOLD_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/gold.png");
    private static final Identifier SALT_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/salt.png");
    private static final Identifier WHITE_SPLOTCHED_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/white_splotched.png");
    private static final Identifier TOAST_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/toast.png");
    private static final Identifier CAERBANNOG_TEXTURE = Identifier.ofVanilla((String)"textures/entity/rabbit/caerbannog.png");

    public RabbitEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT)), (EntityModel)new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT_BABY)), 0.3f);
    }

    public Identifier getTexture(RabbitEntityRenderState rabbitEntityRenderState) {
        if (rabbitEntityRenderState.isToast) {
            return TOAST_TEXTURE;
        }
        return switch (1.field_41642[rabbitEntityRenderState.type.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> BROWN_TEXTURE;
            case 2 -> WHITE_TEXTURE;
            case 3 -> BLACK_TEXTURE;
            case 4 -> GOLD_TEXTURE;
            case 5 -> SALT_TEXTURE;
            case 6 -> WHITE_SPLOTCHED_TEXTURE;
            case 7 -> CAERBANNOG_TEXTURE;
        };
    }

    public RabbitEntityRenderState createRenderState() {
        return new RabbitEntityRenderState();
    }

    public void updateRenderState(RabbitEntity rabbitEntity, RabbitEntityRenderState rabbitEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)rabbitEntity, (LivingEntityRenderState)rabbitEntityRenderState, f);
        rabbitEntityRenderState.jumpProgress = rabbitEntity.getJumpProgress(f);
        rabbitEntityRenderState.isToast = RabbitEntityRenderer.nameEquals((Entity)rabbitEntity, (String)"Toast");
        rabbitEntityRenderState.type = rabbitEntity.getVariant();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((RabbitEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

