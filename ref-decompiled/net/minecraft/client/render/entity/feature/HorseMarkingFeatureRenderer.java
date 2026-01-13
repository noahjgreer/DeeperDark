/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer
 *  net.minecraft.client.render.entity.model.HorseEntityModel
 *  net.minecraft.client.render.entity.state.HorseEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.passive.HorseMarking
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HorseMarkingFeatureRenderer
extends FeatureRenderer<HorseEntityRenderState, HorseEntityModel> {
    private static final Identifier INVISIBLE_ID = Identifier.ofVanilla((String)"invisible");
    private static final Map<HorseMarking, Identifier> TEXTURES = Maps.newEnumMap(Map.of(HorseMarking.NONE, INVISIBLE_ID, HorseMarking.WHITE, Identifier.ofVanilla((String)"textures/entity/horse/horse_markings_white.png"), HorseMarking.WHITE_FIELD, Identifier.ofVanilla((String)"textures/entity/horse/horse_markings_whitefield.png"), HorseMarking.WHITE_DOTS, Identifier.ofVanilla((String)"textures/entity/horse/horse_markings_whitedots.png"), HorseMarking.BLACK_DOTS, Identifier.ofVanilla((String)"textures/entity/horse/horse_markings_blackdots.png")));

    public HorseMarkingFeatureRenderer(FeatureRendererContext<HorseEntityRenderState, HorseEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, HorseEntityRenderState horseEntityRenderState, float f, float g) {
        Identifier identifier = (Identifier)TEXTURES.get(horseEntityRenderState.marking);
        if (identifier == INVISIBLE_ID || horseEntityRenderState.invisible) {
            return;
        }
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.getContextModel(), (Object)horseEntityRenderState, matrixStack, RenderLayers.entityTranslucent((Identifier)identifier), i, LivingEntityRenderer.getOverlay((LivingEntityRenderState)horseEntityRenderState, (float)0.0f), -1, null, horseEntityRenderState.outlineColor, null);
    }
}

