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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.TridentRiptideEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentRiptideFeatureRenderer
extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident_riptide.png");
    private final TridentRiptideEntityModel model;

    public TridentRiptideFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, LoadedEntityModels entityModels) {
        super(context);
        this.model = new TridentRiptideEntityModel(entityModels.getModelPart(EntityModelLayers.SPIN_ATTACK));
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        if (!playerEntityRenderState.usingRiptide) {
            return;
        }
        orderedRenderCommandQueue.submitModel(this.model, playerEntityRenderState, matrixStack, this.model.getLayer(TEXTURE), i, OverlayTexture.DEFAULT_UV, playerEntityRenderState.outlineColor, null);
    }
}
