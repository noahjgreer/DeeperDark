/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemRenderState.LayerRenderState
implements FabricLayerRenderState,
FabricRenderState {
    private static final Vector3fc[] EMPTY = new Vector3fc[0];
    public static final Supplier<Vector3fc[]> DEFAULT = () -> EMPTY;
    private final List<BakedQuad> quads = new ArrayList<BakedQuad>();
    boolean useLight;
    @Nullable Sprite particle;
    Transformation transform = Transformation.IDENTITY;
    private @Nullable RenderLayer renderLayer;
    private ItemRenderState.Glint glint = ItemRenderState.Glint.NONE;
    private int[] tints = new int[0];
    private @Nullable SpecialModelRenderer<Object> specialModelType;
    private @Nullable Object data;
    Supplier<Vector3fc[]> vertices = DEFAULT;

    public void clear() {
        this.quads.clear();
        this.renderLayer = null;
        this.glint = ItemRenderState.Glint.NONE;
        this.specialModelType = null;
        this.data = null;
        Arrays.fill(this.tints, -1);
        this.useLight = false;
        this.particle = null;
        this.transform = Transformation.IDENTITY;
        this.vertices = DEFAULT;
    }

    public List<BakedQuad> getQuads() {
        return this.quads;
    }

    public void setRenderLayer(RenderLayer layer) {
        this.renderLayer = layer;
    }

    public void setUseLight(boolean useLight) {
        this.useLight = useLight;
    }

    public void setVertices(Supplier<Vector3fc[]> vertices) {
        this.vertices = vertices;
    }

    public void setParticle(Sprite particle) {
        this.particle = particle;
    }

    public void setTransform(Transformation transform) {
        this.transform = transform;
    }

    public <T> void setSpecialModel(SpecialModelRenderer<T> specialModelType, @Nullable T data) {
        this.specialModelType = ItemRenderState.LayerRenderState.eraseType(specialModelType);
        this.data = data;
    }

    private static SpecialModelRenderer<Object> eraseType(SpecialModelRenderer<?> specialModelType) {
        return specialModelType;
    }

    public void setGlint(ItemRenderState.Glint glint) {
        this.glint = glint;
    }

    public int[] initTints(int maxIndex) {
        if (maxIndex > this.tints.length) {
            this.tints = new int[maxIndex];
            Arrays.fill(this.tints, -1);
        }
        return this.tints;
    }

    void render(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, int i) {
        matrices.push();
        this.transform.apply(ItemRenderState.this.displayContext.isLeftHand(), matrices.peek());
        if (this.specialModelType != null) {
            this.specialModelType.render(this.data, ItemRenderState.this.displayContext, matrices, orderedRenderCommandQueue, light, overlay, this.glint != ItemRenderState.Glint.NONE, i);
        } else if (this.renderLayer != null) {
            orderedRenderCommandQueue.submitItem(matrices, ItemRenderState.this.displayContext, light, overlay, i, this.tints, this.quads, this.renderLayer, this.glint);
        }
        matrices.pop();
    }
}
