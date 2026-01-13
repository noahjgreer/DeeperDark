/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TropicalFishColorFeatureRenderer
extends FeatureRenderer<TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> {
    private static final Identifier KOB_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_1.png");
    private static final Identifier SUNSTREAK_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_2.png");
    private static final Identifier SNOOPER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_3.png");
    private static final Identifier DASHER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_4.png");
    private static final Identifier BRINELY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_5.png");
    private static final Identifier SPOTTY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_6.png");
    private static final Identifier FLOPPER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_1.png");
    private static final Identifier STRIPEY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_2.png");
    private static final Identifier GLITTER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_3.png");
    private static final Identifier BLOCKFISH_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_4.png");
    private static final Identifier BETTY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_5.png");
    private static final Identifier CLAYFISH_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_6.png");
    private final SmallTropicalFishEntityModel smallModel;
    private final LargeTropicalFishEntityModel largeModel;

    public TropicalFishColorFeatureRenderer(FeatureRendererContext<TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> context, LoadedEntityModels loader) {
        super(context);
        this.smallModel = new SmallTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.largeModel = new LargeTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f, float g) {
        TropicalFishEntity.Pattern pattern = tropicalFishEntityRenderState.variety;
        EntityModel entityModel = switch (pattern.getSize()) {
            default -> throw new MatchException(null, null);
            case TropicalFishEntity.Size.SMALL -> this.smallModel;
            case TropicalFishEntity.Size.LARGE -> this.largeModel;
        };
        Identifier identifier = switch (pattern) {
            default -> throw new MatchException(null, null);
            case TropicalFishEntity.Pattern.KOB -> KOB_TEXTURE;
            case TropicalFishEntity.Pattern.SUNSTREAK -> SUNSTREAK_TEXTURE;
            case TropicalFishEntity.Pattern.SNOOPER -> SNOOPER_TEXTURE;
            case TropicalFishEntity.Pattern.DASHER -> DASHER_TEXTURE;
            case TropicalFishEntity.Pattern.BRINELY -> BRINELY_TEXTURE;
            case TropicalFishEntity.Pattern.SPOTTY -> SPOTTY_TEXTURE;
            case TropicalFishEntity.Pattern.FLOPPER -> FLOPPER_TEXTURE;
            case TropicalFishEntity.Pattern.STRIPEY -> STRIPEY_TEXTURE;
            case TropicalFishEntity.Pattern.GLITTER -> GLITTER_TEXTURE;
            case TropicalFishEntity.Pattern.BLOCKFISH -> BLOCKFISH_TEXTURE;
            case TropicalFishEntity.Pattern.BETTY -> BETTY_TEXTURE;
            case TropicalFishEntity.Pattern.CLAYFISH -> CLAYFISH_TEXTURE;
        };
        TropicalFishColorFeatureRenderer.render(entityModel, identifier, matrixStack, orderedRenderCommandQueue, i, tropicalFishEntityRenderState, tropicalFishEntityRenderState.patternColor, 1);
    }
}
