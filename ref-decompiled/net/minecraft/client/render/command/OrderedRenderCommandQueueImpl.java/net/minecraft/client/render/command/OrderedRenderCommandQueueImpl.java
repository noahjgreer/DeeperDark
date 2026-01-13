/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.command;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
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
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OrderedRenderCommandQueueImpl
implements OrderedRenderCommandQueue {
    private final Int2ObjectAVLTreeMap<BatchingRenderCommandQueue> batchingQueues = new Int2ObjectAVLTreeMap();

    @Override
    public BatchingRenderCommandQueue getBatchingQueue(int i) {
        return (BatchingRenderCommandQueue)this.batchingQueues.computeIfAbsent(i, orderx -> new BatchingRenderCommandQueue(this));
    }

    @Override
    public void submitShadowPieces(MatrixStack matrices, float shadowRadius, List<EntityRenderState.ShadowPiece> shadowPieces) {
        this.getBatchingQueue(0).submitShadowPieces(matrices, shadowRadius, shadowPieces);
    }

    @Override
    public void submitLabel(MatrixStack matrices, @Nullable Vec3d nameLabelPos, int y, Text label, boolean notSneaking, int light, double squaredDistanceToCamera, CameraRenderState cameraState) {
        this.getBatchingQueue(0).submitLabel(matrices, nameLabelPos, y, label, notSneaking, light, squaredDistanceToCamera, cameraState);
    }

    @Override
    public void submitText(MatrixStack matrices, float x, float y, OrderedText text, boolean dropShadow, TextRenderer.TextLayerType layerType, int light, int color, int backgroundColor, int outlineColor) {
        this.getBatchingQueue(0).submitText(matrices, x, y, text, dropShadow, layerType, light, color, backgroundColor, outlineColor);
    }

    @Override
    public void submitFire(MatrixStack matrices, EntityRenderState renderState, Quaternionf rotation) {
        this.getBatchingQueue(0).submitFire(matrices, renderState, rotation);
    }

    @Override
    public void submitLeash(MatrixStack matrices, EntityRenderState.LeashData leashData) {
        this.getBatchingQueue(0).submitLeash(matrices, leashData);
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, int tintedColor, @Nullable Sprite sprite, int outlineColor,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        this.getBatchingQueue(0).submitModel(model, state, matrices, renderLayer, light, overlay, tintedColor, sprite, outlineColor, crumblingOverlay);
    }

    @Override
    public void submitModelPart(ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i) {
        this.getBatchingQueue(0).submitModelPart(part, matrices, renderLayer, light, overlay, sprite, sheeted, hasGlint, tintedColor, crumblingOverlay, i);
    }

    @Override
    public void submitBlock(MatrixStack matrices, BlockState state, int light, int overlay, int outlineColor) {
        this.getBatchingQueue(0).submitBlock(matrices, state, light, overlay, outlineColor);
    }

    @Override
    public void submitMovingBlock(MatrixStack matrices, MovingBlockRenderState state) {
        this.getBatchingQueue(0).submitMovingBlock(matrices, state);
    }

    @Override
    public void submitBlockStateModel(MatrixStack matrices, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int light, int overlay, int outlineColor) {
        this.getBatchingQueue(0).submitBlockStateModel(matrices, renderLayer, model, r, g, b, light, overlay, outlineColor);
    }

    @Override
    public void submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType) {
        this.getBatchingQueue(0).submitItem(matrices, displayContext, light, overlay, outlineColors, tintLayers, quads, renderLayer, glintType);
    }

    @Override
    public void submitCustom(MatrixStack matrices, RenderLayer renderLayer, OrderedRenderCommandQueue.Custom customRenderer) {
        this.getBatchingQueue(0).submitCustom(matrices, renderLayer, customRenderer);
    }

    @Override
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

    @Override
    public /* synthetic */ RenderCommandQueue getBatchingQueue(int order) {
        return this.getBatchingQueue(order);
    }

    @Environment(value=EnvType.CLIENT)
    public record CustomCommand(MatrixStack.Entry matricesEntry, OrderedRenderCommandQueue.Custom customRenderer) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CustomCommand.class, "pose;customGeometryRenderer", "matricesEntry", "customRenderer"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CustomCommand.class, "pose;customGeometryRenderer", "matricesEntry", "customRenderer"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CustomCommand.class, "pose;customGeometryRenderer", "matricesEntry", "customRenderer"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ItemCommand(MatrixStack.Entry positionMatrix, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemCommand.class, "pose;displayContext;lightCoords;overlayCoords;outlineColor;tintLayers;quads;renderType;foilType", "positionMatrix", "displayContext", "lightCoords", "overlayCoords", "outlineColor", "tintLayers", "quads", "renderLayer", "glintType"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemCommand.class, "pose;displayContext;lightCoords;overlayCoords;outlineColor;tintLayers;quads;renderType;foilType", "positionMatrix", "displayContext", "lightCoords", "overlayCoords", "outlineColor", "tintLayers", "quads", "renderLayer", "glintType"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemCommand.class, "pose;displayContext;lightCoords;overlayCoords;outlineColor;tintLayers;quads;renderType;foilType", "positionMatrix", "displayContext", "lightCoords", "overlayCoords", "outlineColor", "tintLayers", "quads", "renderLayer", "glintType"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record BlockStateModelCommand(MatrixStack.Entry matricesEntry, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int lightCoords, int overlayCoords, int outlineColor) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockStateModelCommand.class, "pose;renderType;model;r;g;b;lightCoords;overlayCoords;outlineColor", "matricesEntry", "renderLayer", "model", "r", "g", "b", "lightCoords", "overlayCoords", "outlineColor"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockStateModelCommand.class, "pose;renderType;model;r;g;b;lightCoords;overlayCoords;outlineColor", "matricesEntry", "renderLayer", "model", "r", "g", "b", "lightCoords", "overlayCoords", "outlineColor"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockStateModelCommand.class, "pose;renderType;model;r;g;b;lightCoords;overlayCoords;outlineColor", "matricesEntry", "renderLayer", "model", "r", "g", "b", "lightCoords", "overlayCoords", "outlineColor"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record MovingBlockCommand(Matrix4f matricesEntry, MovingBlockRenderState movingBlockRenderState) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{MovingBlockCommand.class, "pose;movingBlockRenderState", "matricesEntry", "movingBlockRenderState"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MovingBlockCommand.class, "pose;movingBlockRenderState", "matricesEntry", "movingBlockRenderState"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MovingBlockCommand.class, "pose;movingBlockRenderState", "matricesEntry", "movingBlockRenderState"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record BlockCommand(MatrixStack.Entry matricesEntry, BlockState state, int lightCoords, int overlayCoords, int outlineColor) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockCommand.class, "pose;state;lightCoords;overlayCoords;outlineColor", "matricesEntry", "state", "lightCoords", "overlayCoords", "outlineColor"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockCommand.class, "pose;state;lightCoords;overlayCoords;outlineColor", "matricesEntry", "state", "lightCoords", "overlayCoords", "outlineColor"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockCommand.class, "pose;state;lightCoords;overlayCoords;outlineColor", "matricesEntry", "state", "lightCoords", "overlayCoords", "outlineColor"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record BlendedModelCommand<S>(ModelCommand<S> model, RenderLayer renderType, Vector3f position) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlendedModelCommand.class, "modelSubmit;renderType;position", "model", "renderType", "position"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlendedModelCommand.class, "modelSubmit;renderType;position", "model", "renderType", "position"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlendedModelCommand.class, "modelSubmit;renderType;position", "model", "renderType", "position"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ModelPartCommand(MatrixStack.Entry matricesEntry, ModelPart modelPart, int lightCoords, int overlayCoords, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int outlineColor) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelPartCommand.class, "pose;modelPart;lightCoords;overlayCoords;sprite;sheeted;hasFoil;tintedColor;crumblingOverlay;outlineColor", "matricesEntry", "modelPart", "lightCoords", "overlayCoords", "sprite", "sheeted", "hasGlint", "tintedColor", "crumblingOverlay", "outlineColor"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelPartCommand.class, "pose;modelPart;lightCoords;overlayCoords;sprite;sheeted;hasFoil;tintedColor;crumblingOverlay;outlineColor", "matricesEntry", "modelPart", "lightCoords", "overlayCoords", "sprite", "sheeted", "hasGlint", "tintedColor", "crumblingOverlay", "outlineColor"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelPartCommand.class, "pose;modelPart;lightCoords;overlayCoords;sprite;sheeted;hasFoil;tintedColor;crumblingOverlay;outlineColor", "matricesEntry", "modelPart", "lightCoords", "overlayCoords", "sprite", "sheeted", "hasGlint", "tintedColor", "crumblingOverlay", "outlineColor"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ModelCommand<S>(MatrixStack.Entry matricesEntry, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable Sprite sprite, int outlineColor,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelCommand.class, "pose;model;state;lightCoords;overlayCoords;tintedColor;sprite;outlineColor;crumblingOverlay", "matricesEntry", "model", "state", "lightCoords", "overlayCoords", "tintedColor", "sprite", "outlineColor", "crumblingOverlay"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelCommand.class, "pose;model;state;lightCoords;overlayCoords;tintedColor;sprite;outlineColor;crumblingOverlay", "matricesEntry", "model", "state", "lightCoords", "overlayCoords", "tintedColor", "sprite", "outlineColor", "crumblingOverlay"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelCommand.class, "pose;model;state;lightCoords;overlayCoords;tintedColor;sprite;outlineColor;crumblingOverlay", "matricesEntry", "model", "state", "lightCoords", "overlayCoords", "tintedColor", "sprite", "outlineColor", "crumblingOverlay"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record LeashCommand(Matrix4f matricesEntry, EntityRenderState.LeashData leashState) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LeashCommand.class, "pose;leashState", "matricesEntry", "leashState"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LeashCommand.class, "pose;leashState", "matricesEntry", "leashState"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LeashCommand.class, "pose;leashState", "matricesEntry", "leashState"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TextCommand(Matrix4f matricesEntry, float x, float y, OrderedText text, boolean dropShadow, TextRenderer.TextLayerType layerType, int lightCoords, int color, int backgroundColor, int outlineColor) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{TextCommand.class, "pose;x;y;string;dropShadow;displayMode;lightCoords;color;backgroundColor;outlineColor", "matricesEntry", "x", "y", "text", "dropShadow", "layerType", "lightCoords", "color", "backgroundColor", "outlineColor"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TextCommand.class, "pose;x;y;string;dropShadow;displayMode;lightCoords;color;backgroundColor;outlineColor", "matricesEntry", "x", "y", "text", "dropShadow", "layerType", "lightCoords", "color", "backgroundColor", "outlineColor"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TextCommand.class, "pose;x;y;string;dropShadow;displayMode;lightCoords;color;backgroundColor;outlineColor", "matricesEntry", "x", "y", "text", "dropShadow", "layerType", "lightCoords", "color", "backgroundColor", "outlineColor"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record LabelCommand(Matrix4f matricesEntry, float x, float y, Text text, int lightCoords, int color, int backgroundColor, double distanceToCameraSq) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LabelCommand.class, "pose;x;y;text;lightCoords;color;backgroundColor;distanceToCameraSq", "matricesEntry", "x", "y", "text", "lightCoords", "color", "backgroundColor", "distanceToCameraSq"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LabelCommand.class, "pose;x;y;text;lightCoords;color;backgroundColor;distanceToCameraSq", "matricesEntry", "x", "y", "text", "lightCoords", "color", "backgroundColor", "distanceToCameraSq"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LabelCommand.class, "pose;x;y;text;lightCoords;color;backgroundColor;distanceToCameraSq", "matricesEntry", "x", "y", "text", "lightCoords", "color", "backgroundColor", "distanceToCameraSq"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record FireCommand(MatrixStack.Entry matricesEntry, EntityRenderState renderState, Quaternionf rotation) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{FireCommand.class, "pose;entityRenderState;rotation", "matricesEntry", "renderState", "rotation"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FireCommand.class, "pose;entityRenderState;rotation", "matricesEntry", "renderState", "rotation"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FireCommand.class, "pose;entityRenderState;rotation", "matricesEntry", "renderState", "rotation"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ShadowPiecesCommand(Matrix4f matricesEntry, float radius, List<EntityRenderState.ShadowPiece> pieces) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ShadowPiecesCommand.class, "pose;radius;pieces", "matricesEntry", "radius", "pieces"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ShadowPiecesCommand.class, "pose;radius;pieces", "matricesEntry", "radius", "pieces"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ShadowPiecesCommand.class, "pose;radius;pieces", "matricesEntry", "radius", "pieces"}, this, object);
        }
    }
}
