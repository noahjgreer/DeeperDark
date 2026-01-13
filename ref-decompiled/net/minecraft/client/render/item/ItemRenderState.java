/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.item.ItemRenderState$LayerRenderState
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Box$Builder
 *  net.minecraft.util.math.random.Random
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item;

import java.util.Arrays;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemRenderState
implements FabricRenderState {
    ItemDisplayContext displayContext = ItemDisplayContext.NONE;
    private int layerCount;
    private boolean animated;
    private boolean oversizedInGui;
    private @Nullable Box cachedModelBoundingBox;
    private LayerRenderState[] layers = new LayerRenderState[]{new LayerRenderState(this)};

    public void addLayers(int add) {
        int j = this.layerCount + add;
        int i = this.layers.length;
        if (j > i) {
            this.layers = Arrays.copyOf(this.layers, j);
            for (int k = i; k < j; ++k) {
                this.layers[k] = new LayerRenderState(this);
            }
        }
    }

    public LayerRenderState newLayer() {
        this.addLayers(1);
        return this.layers[this.layerCount++];
    }

    public void clear() {
        this.displayContext = ItemDisplayContext.NONE;
        for (int i = 0; i < this.layerCount; ++i) {
            this.layers[i].clear();
        }
        this.layerCount = 0;
        this.animated = false;
        this.oversizedInGui = false;
        this.cachedModelBoundingBox = null;
    }

    public void markAnimated() {
        this.animated = true;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void addModelKey(Object modelKey) {
    }

    private LayerRenderState getFirstLayer() {
        return this.layers[0];
    }

    public boolean isEmpty() {
        return this.layerCount == 0;
    }

    public boolean isSideLit() {
        return this.getFirstLayer().useLight;
    }

    public @Nullable Sprite getParticleSprite(Random random) {
        if (this.layerCount == 0) {
            return null;
        }
        return this.layers[random.nextInt((int)this.layerCount)].particle;
    }

    public void load(Consumer<Vector3fc> posConsumer) {
        Vector3f vector3f = new Vector3f();
        MatrixStack.Entry entry = new MatrixStack.Entry();
        for (int i = 0; i < this.layerCount; ++i) {
            Vector3fc[] vector3fcs;
            LayerRenderState layerRenderState = this.layers[i];
            layerRenderState.transform.apply(this.displayContext.isLeftHand(), entry);
            Matrix4f matrix4f = entry.getPositionMatrix();
            for (Vector3fc vector3fc : vector3fcs = (Vector3fc[])layerRenderState.vertices.get()) {
                posConsumer.accept((Vector3fc)vector3f.set(vector3fc).mulPosition((Matrix4fc)matrix4f));
            }
            entry.loadIdentity();
        }
    }

    public void render(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, int i) {
        for (int j = 0; j < this.layerCount; ++j) {
            this.layers[j].render(matrices, orderedRenderCommandQueue, light, overlay, i);
        }
    }

    public Box getModelBoundingBox() {
        Box box;
        if (this.cachedModelBoundingBox != null) {
            return this.cachedModelBoundingBox;
        }
        Box.Builder builder = new Box.Builder();
        this.load(arg_0 -> ((Box.Builder)builder).encompass(arg_0));
        this.cachedModelBoundingBox = box = builder.build();
        return box;
    }

    public void setOversizedInGui(boolean oversizedInGui) {
        this.oversizedInGui = oversizedInGui;
    }

    public boolean isOversizedInGui() {
        return this.oversizedInGui;
    }
}

