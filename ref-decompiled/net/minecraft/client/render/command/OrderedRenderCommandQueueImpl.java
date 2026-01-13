/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.block.MovingBlockRenderState
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue$Custom
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue$LayeredCustom
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl
 *  net.minecraft.client.render.command.RenderCommandQueue
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState$LeashData
 *  net.minecraft.client.render.entity.state.EntityRenderState$ShadowPiece
 *  net.minecraft.client.render.item.ItemRenderState$Glint
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionf
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.command;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OrderedRenderCommandQueueImpl
implements OrderedRenderCommandQueue {
    private final Int2ObjectAVLTreeMap<BatchingRenderCommandQueue> batchingQueues = new Int2ObjectAVLTreeMap();

    public BatchingRenderCommandQueue getBatchingQueue(int i) {
        return (BatchingRenderCommandQueue)this.batchingQueues.computeIfAbsent(i, orderx -> new BatchingRenderCommandQueue(this));
    }

    public void submitShadowPieces(MatrixStack matrices, float shadowRadius, List<EntityRenderState.ShadowPiece> shadowPieces) {
        this.getBatchingQueue(0).submitShadowPieces(matrices, shadowRadius, shadowPieces);
    }

    public void submitLabel(MatrixStack matrices, @Nullable Vec3d nameLabelPos, int y, Text label, boolean notSneaking, int light, double squaredDistanceToCamera, CameraRenderState cameraState) {
        this.getBatchingQueue(0).submitLabel(matrices, nameLabelPos, y, label, notSneaking, light, squaredDistanceToCamera, cameraState);
    }

    public void submitText(MatrixStack matrices, float x, float y, OrderedText text, boolean dropShadow, TextRenderer.TextLayerType layerType, int light, int color, int backgroundColor, int outlineColor) {
        this.getBatchingQueue(0).submitText(matrices, x, y, text, dropShadow, layerType, light, color, backgroundColor, outlineColor);
    }

    public void submitFire(MatrixStack matrices, EntityRenderState renderState, Quaternionf rotation) {
        this.getBatchingQueue(0).submitFire(matrices, renderState, rotation);
    }

    public void submitLeash(MatrixStack matrices, EntityRenderState.LeashData leashData) {
        this.getBatchingQueue(0).submitLeash(matrices, leashData);
    }

    public <S> void submitModel(Model<? super S> model, S state, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, int tintedColor, @Nullable Sprite sprite, int outlineColor, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        this.getBatchingQueue(0).submitModel(model, state, matrices, renderLayer, light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay);
    }

    public void submitModelPart(ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i) {
        this.getBatchingQueue(0).submitModelPart(part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i);
    }

    public void submitBlock(MatrixStack matrices, BlockState state, int light, int overlay, int outlineColor) {
        this.getBatchingQueue(0).submitBlock(matrices, state, light, overlay, outlineColor);
    }

    public void submitMovingBlock(MatrixStack matrices, MovingBlockRenderState state) {
        this.getBatchingQueue(0).submitMovingBlock(matrices, state);
    }

    public void submitBlockStateModel(MatrixStack matrices, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int light, int overlay, int outlineColor) {
        this.getBatchingQueue(0).submitBlockStateModel(matrices, renderLayer, model, r, g, b, light, overlay, outlineColor);
    }

    public void submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType) {
        this.getBatchingQueue(0).submitItem(matrices, displayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);
    }

    public void submitCustom(MatrixStack matrices, RenderLayer renderLayer, OrderedRenderCommandQueue.Custom customRenderer) {
        this.getBatchingQueue(0).submitCustom(matrices, renderLayer, customRenderer);
    }

    public void submitCustom(OrderedRenderCommandQueue.LayeredCustom customRenderer) {
        this.getBatchingQueue(0).submitCustom(customRenderer);
    }

    public void clear() {
        this.batchingQueues.values().forEach(BatchingRenderCommandQueue::clear);
    }

    public void onNextFrame() {
        this.batchingQueues.values().removeIf(queue -> !queue.hasCommands());
        this.batchingQueues.values().forEach(BatchingRenderCommandQueue::onNextFrame);
    }

    public Int2ObjectAVLTreeMap<BatchingRenderCommandQueue> getBatchingQueues() {
        return this.batchingQueues;
    }

    public /* synthetic */ RenderCommandQueue getBatchingQueue(int order) {
        return this.getBatchingQueue(order);
    }
}

