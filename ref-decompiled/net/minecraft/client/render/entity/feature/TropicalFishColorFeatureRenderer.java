/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer$1
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.TropicalFishEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.passive.TropicalFishEntity$Pattern
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TropicalFishColorFeatureRenderer
extends FeatureRenderer<TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> {
    private static final Identifier KOB_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_a_pattern_1.png");
    private static final Identifier SUNSTREAK_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_a_pattern_2.png");
    private static final Identifier SNOOPER_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_a_pattern_3.png");
    private static final Identifier DASHER_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_a_pattern_4.png");
    private static final Identifier BRINELY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_a_pattern_5.png");
    private static final Identifier SPOTTY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_a_pattern_6.png");
    private static final Identifier FLOPPER_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_b_pattern_1.png");
    private static final Identifier STRIPEY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_b_pattern_2.png");
    private static final Identifier GLITTER_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_b_pattern_3.png");
    private static final Identifier BLOCKFISH_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_b_pattern_4.png");
    private static final Identifier BETTY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_b_pattern_5.png");
    private static final Identifier CLAYFISH_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_b_pattern_6.png");
    private final SmallTropicalFishEntityModel smallModel;
    private final LargeTropicalFishEntityModel largeModel;

    public TropicalFishColorFeatureRenderer(FeatureRendererContext<TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> context, LoadedEntityModels loader) {
        super(context);
        this.smallModel = new SmallTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.largeModel = new LargeTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f, float g) {
        TropicalFishEntity.Pattern pattern = tropicalFishEntityRenderState.variety;
        SmallTropicalFishEntityModel entityModel = switch (1.field_41658[pattern.getSize().ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> this.smallModel;
            case 2 -> this.largeModel;
        };
        Identifier identifier = switch (1.field_41659[pattern.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> KOB_TEXTURE;
            case 2 -> SUNSTREAK_TEXTURE;
            case 3 -> SNOOPER_TEXTURE;
            case 4 -> DASHER_TEXTURE;
            case 5 -> BRINELY_TEXTURE;
            case 6 -> SPOTTY_TEXTURE;
            case 7 -> FLOPPER_TEXTURE;
            case 8 -> STRIPEY_TEXTURE;
            case 9 -> GLITTER_TEXTURE;
            case 10 -> BLOCKFISH_TEXTURE;
            case 11 -> BETTY_TEXTURE;
            case 12 -> CLAYFISH_TEXTURE;
        };
        TropicalFishColorFeatureRenderer.render((Model)entityModel, (Identifier)identifier, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (LivingEntityRenderState)tropicalFishEntityRenderState, (int)tropicalFishEntityRenderState.patternColor, (int)1);
    }
}

