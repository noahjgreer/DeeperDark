/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer
 *  net.minecraft.client.render.entity.model.IronGolemEntityModel
 *  net.minecraft.client.render.entity.state.IronGolemEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.passive.Cracks$CrackLevel
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class IronGolemCrackFeatureRenderer
extends FeatureRenderer<IronGolemEntityRenderState, IronGolemEntityModel> {
    private static final Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES = ImmutableMap.of((Object)Cracks.CrackLevel.LOW, (Object)Identifier.ofVanilla((String)"textures/entity/iron_golem/iron_golem_crackiness_low.png"), (Object)Cracks.CrackLevel.MEDIUM, (Object)Identifier.ofVanilla((String)"textures/entity/iron_golem/iron_golem_crackiness_medium.png"), (Object)Cracks.CrackLevel.HIGH, (Object)Identifier.ofVanilla((String)"textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCrackFeatureRenderer(FeatureRendererContext<IronGolemEntityRenderState, IronGolemEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, IronGolemEntityRenderState ironGolemEntityRenderState, float f, float g) {
        if (ironGolemEntityRenderState.invisible) {
            return;
        }
        Cracks.CrackLevel crackLevel = ironGolemEntityRenderState.crackLevel;
        if (crackLevel == Cracks.CrackLevel.NONE) {
            return;
        }
        Identifier identifier = (Identifier)CRACK_TEXTURES.get(crackLevel);
        IronGolemCrackFeatureRenderer.renderModel((Model)this.getContextModel(), (Identifier)identifier, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (LivingEntityRenderState)ironGolemEntityRenderState, (int)-1, (int)1);
    }
}

