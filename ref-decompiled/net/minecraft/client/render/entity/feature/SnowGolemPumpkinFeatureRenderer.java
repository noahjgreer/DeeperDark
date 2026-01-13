/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.render.BlockRenderLayers
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.BlockRenderManager
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer
 *  net.minecraft.client.render.entity.model.SnowGolemEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SnowGolemEntityRenderState
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SnowGolemEntityRenderState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class SnowGolemPumpkinFeatureRenderer
extends FeatureRenderer<SnowGolemEntityRenderState, SnowGolemEntityModel> {
    private final BlockRenderManager blockRenderManager;

    public SnowGolemPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntityRenderState, SnowGolemEntityModel> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, SnowGolemEntityRenderState snowGolemEntityRenderState, float f, float g) {
        if (!snowGolemEntityRenderState.hasPumpkin) {
            return;
        }
        if (snowGolemEntityRenderState.invisible && !snowGolemEntityRenderState.hasOutline()) {
            return;
        }
        matrixStack.push();
        ((SnowGolemEntityModel)this.getContextModel()).getHead().applyTransform(matrixStack);
        float h = 0.625f;
        matrixStack.translate(0.0f, -0.34375f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrixStack.scale(0.625f, -0.625f, -0.625f);
        BlockState blockState = Blocks.CARVED_PUMPKIN.getDefaultState();
        BlockStateModel blockStateModel = this.blockRenderManager.getModel(blockState);
        int j = LivingEntityRenderer.getOverlay((LivingEntityRenderState)snowGolemEntityRenderState, (float)0.0f);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        RenderLayer renderLayer = snowGolemEntityRenderState.hasOutline() && snowGolemEntityRenderState.invisible ? RenderLayers.outlineNoCull((Identifier)SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE) : BlockRenderLayers.getEntityBlockLayer((BlockState)blockState);
        orderedRenderCommandQueue.submitBlockStateModel(matrixStack, renderLayer, blockStateModel, 0.0f, 0.0f, 0.0f, i, j, snowGolemEntityRenderState.outlineColor);
        matrixStack.pop();
    }
}

