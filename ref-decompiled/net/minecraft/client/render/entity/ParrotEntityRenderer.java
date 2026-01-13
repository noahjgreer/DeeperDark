/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.ParrotEntityRenderer
 *  net.minecraft.client.render.entity.ParrotEntityRenderer$1
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.ParrotEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ParrotEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.ParrotEntity
 *  net.minecraft.entity.passive.ParrotEntity$Variant
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ParrotEntityRenderer
extends MobEntityRenderer<ParrotEntity, ParrotEntityRenderState, ParrotEntityModel> {
    private static final Identifier RED_BLUE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/parrot/parrot_red_blue.png");
    private static final Identifier BLUE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/parrot/parrot_blue.png");
    private static final Identifier GREEN_TEXTURE = Identifier.ofVanilla((String)"textures/entity/parrot/parrot_green.png");
    private static final Identifier YELLOW_TEXTURE = Identifier.ofVanilla((String)"textures/entity/parrot/parrot_yellow_blue.png");
    private static final Identifier GREY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/parrot/parrot_grey.png");

    public ParrotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT)), 0.3f);
    }

    public Identifier getTexture(ParrotEntityRenderState parrotEntityRenderState) {
        return ParrotEntityRenderer.getTexture((ParrotEntity.Variant)parrotEntityRenderState.variant);
    }

    public ParrotEntityRenderState createRenderState() {
        return new ParrotEntityRenderState();
    }

    public void updateRenderState(ParrotEntity parrotEntity, ParrotEntityRenderState parrotEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)parrotEntity, (LivingEntityRenderState)parrotEntityRenderState, f);
        parrotEntityRenderState.variant = parrotEntity.getVariant();
        float g = MathHelper.lerp((float)f, (float)parrotEntity.lastFlapProgress, (float)parrotEntity.flapProgress);
        float h = MathHelper.lerp((float)f, (float)parrotEntity.lastMaxWingDeviation, (float)parrotEntity.maxWingDeviation);
        parrotEntityRenderState.flapAngle = (MathHelper.sin((double)g) + 1.0f) * h;
        parrotEntityRenderState.parrotPose = ParrotEntityModel.getPose((ParrotEntity)parrotEntity);
    }

    public static Identifier getTexture(ParrotEntity.Variant variant) {
        return switch (1.field_41641[variant.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> RED_BLUE_TEXTURE;
            case 2 -> BLUE_TEXTURE;
            case 3 -> GREEN_TEXTURE;
            case 4 -> YELLOW_TEXTURE;
            case 5 -> GREY_TEXTURE;
        };
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ParrotEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

