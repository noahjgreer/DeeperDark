/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HorseMarkingFeatureRenderer
extends FeatureRenderer<HorseEntityRenderState, HorseEntityModel> {
    private static final Identifier INVISIBLE_ID = Identifier.ofVanilla("invisible");
    private static final Map<HorseMarking, Identifier> TEXTURES = Maps.newEnumMap(Map.of(HorseMarking.NONE, INVISIBLE_ID, HorseMarking.WHITE, Identifier.ofVanilla("textures/entity/horse/horse_markings_white.png"), HorseMarking.WHITE_FIELD, Identifier.ofVanilla("textures/entity/horse/horse_markings_whitefield.png"), HorseMarking.WHITE_DOTS, Identifier.ofVanilla("textures/entity/horse/horse_markings_whitedots.png"), HorseMarking.BLACK_DOTS, Identifier.ofVanilla("textures/entity/horse/horse_markings_blackdots.png")));

    public HorseMarkingFeatureRenderer(FeatureRendererContext<HorseEntityRenderState, HorseEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, HorseEntityRenderState horseEntityRenderState, float f, float g) {
        Identifier identifier = TEXTURES.get((Object)horseEntityRenderState.marking);
        if (identifier == INVISIBLE_ID || horseEntityRenderState.invisible) {
            return;
        }
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel(this.getContextModel(), horseEntityRenderState, matrixStack, RenderLayers.entityTranslucent(identifier), i, LivingEntityRenderer.getOverlay(horseEntityRenderState, 0.0f), -1, (Sprite)null, horseEntityRenderState.outlineColor, (ModelCommandRenderer.CrumblingOverlayCommand)null);
    }
}
