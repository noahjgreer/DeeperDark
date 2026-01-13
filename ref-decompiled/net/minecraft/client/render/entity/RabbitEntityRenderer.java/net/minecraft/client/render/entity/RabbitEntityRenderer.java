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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.RabbitEntityRenderState;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityRenderer
extends AgeableMobEntityRenderer<RabbitEntity, RabbitEntityRenderState, RabbitEntityModel> {
    private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/brown.png");
    private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white.png");
    private static final Identifier BLACK_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/black.png");
    private static final Identifier GOLD_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/gold.png");
    private static final Identifier SALT_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/salt.png");
    private static final Identifier WHITE_SPLOTCHED_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/white_splotched.png");
    private static final Identifier TOAST_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/toast.png");
    private static final Identifier CAERBANNOG_TEXTURE = Identifier.ofVanilla("textures/entity/rabbit/caerbannog.png");

    public RabbitEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT)), new RabbitEntityModel(context.getPart(EntityModelLayers.RABBIT_BABY)), 0.3f);
    }

    @Override
    public Identifier getTexture(RabbitEntityRenderState rabbitEntityRenderState) {
        if (rabbitEntityRenderState.isToast) {
            return TOAST_TEXTURE;
        }
        return switch (rabbitEntityRenderState.type) {
            default -> throw new MatchException(null, null);
            case RabbitEntity.Variant.BROWN -> BROWN_TEXTURE;
            case RabbitEntity.Variant.WHITE -> WHITE_TEXTURE;
            case RabbitEntity.Variant.BLACK -> BLACK_TEXTURE;
            case RabbitEntity.Variant.GOLD -> GOLD_TEXTURE;
            case RabbitEntity.Variant.SALT -> SALT_TEXTURE;
            case RabbitEntity.Variant.WHITE_SPLOTCHED -> WHITE_SPLOTCHED_TEXTURE;
            case RabbitEntity.Variant.EVIL -> CAERBANNOG_TEXTURE;
        };
    }

    @Override
    public RabbitEntityRenderState createRenderState() {
        return new RabbitEntityRenderState();
    }

    @Override
    public void updateRenderState(RabbitEntity rabbitEntity, RabbitEntityRenderState rabbitEntityRenderState, float f) {
        super.updateRenderState(rabbitEntity, rabbitEntityRenderState, f);
        rabbitEntityRenderState.jumpProgress = rabbitEntity.getJumpProgress(f);
        rabbitEntityRenderState.isToast = RabbitEntityRenderer.nameEquals(rabbitEntity, "Toast");
        rabbitEntityRenderState.type = rabbitEntity.getVariant();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((RabbitEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
