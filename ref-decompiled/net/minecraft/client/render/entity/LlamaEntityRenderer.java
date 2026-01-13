/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LlamaEntityRenderer
 *  net.minecraft.client.render.entity.LlamaEntityRenderer$1
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.LlamaEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.LlamaEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.LlamaEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LlamaEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityRenderer
extends AgeableMobEntityRenderer<LlamaEntity, LlamaEntityRenderState, LlamaEntityModel> {
    private static final Identifier CREAMY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/llama/creamy.png");
    private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/llama/white.png");
    private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla((String)"textures/entity/llama/brown.png");
    private static final Identifier GRAY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/llama/gray.png");

    public LlamaEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer) {
        super(context, (EntityModel)new LlamaEntityModel(context.getPart(layer)), (EntityModel)new LlamaEntityModel(context.getPart(babyLayer)), 0.7f);
        this.addFeature((FeatureRenderer)new LlamaDecorFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getEquipmentRenderer()));
    }

    public Identifier getTexture(LlamaEntityRenderState llamaEntityRenderState) {
        return switch (1.field_41635[llamaEntityRenderState.variant.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> CREAMY_TEXTURE;
            case 2 -> WHITE_TEXTURE;
            case 3 -> BROWN_TEXTURE;
            case 4 -> GRAY_TEXTURE;
        };
    }

    public LlamaEntityRenderState createRenderState() {
        return new LlamaEntityRenderState();
    }

    public void updateRenderState(LlamaEntity llamaEntity, LlamaEntityRenderState llamaEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)llamaEntity, (LivingEntityRenderState)llamaEntityRenderState, f);
        llamaEntityRenderState.variant = llamaEntity.getVariant();
        llamaEntityRenderState.hasChest = !llamaEntity.isBaby() && llamaEntity.hasChest();
        llamaEntityRenderState.bodyArmor = llamaEntity.getBodyArmor();
        llamaEntityRenderState.trader = llamaEntity.isTrader();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((LlamaEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

