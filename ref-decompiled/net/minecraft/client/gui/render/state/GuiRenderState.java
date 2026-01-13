/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState
 *  net.minecraft.client.gui.render.state.GuiElementRenderState
 *  net.minecraft.client.gui.render.state.GuiRenderState
 *  net.minecraft.client.gui.render.state.GuiRenderState$Layer
 *  net.minecraft.client.gui.render.state.GuiRenderState$LayerFilter
 *  net.minecraft.client.gui.render.state.ItemGuiElementRenderState
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.gui.render.state.TextGuiElementRenderState
 *  net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.texture.TextureSetup
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GuiRenderState {
    private static final int field_60454 = 0x774444FF;
    private final List<Layer> rootLayers = new ArrayList();
    private int blurLayer = Integer.MAX_VALUE;
    private Layer currentLayer;
    private final Set<Object> itemModelKeys = new HashSet();
    private @Nullable ScreenRect currentLayerBounds;

    public GuiRenderState() {
        this.createNewRootLayer();
    }

    public void createNewRootLayer() {
        this.currentLayer = new Layer(null);
        this.rootLayers.add(this.currentLayer);
    }

    public void applyBlur() {
        if (this.blurLayer != Integer.MAX_VALUE) {
            throw new IllegalStateException("Can only blur once per frame");
        }
        this.blurLayer = this.rootLayers.size() - 1;
    }

    public void goUpLayer() {
        if (this.currentLayer.up == null) {
            this.currentLayer.up = new Layer(this.currentLayer);
        }
        this.currentLayer = this.currentLayer.up;
    }

    public void addItem(ItemGuiElementRenderState state) {
        if (!this.findAndGoToLayerToAdd((GuiElementRenderState)state)) {
            return;
        }
        this.itemModelKeys.add(state.state().getModelKey());
        this.currentLayer.addItem(state);
        this.onElementAdded(state.bounds());
    }

    public void addText(TextGuiElementRenderState state) {
        if (!this.findAndGoToLayerToAdd((GuiElementRenderState)state)) {
            return;
        }
        this.currentLayer.addText(state);
        this.onElementAdded(state.bounds());
    }

    public void addSpecialElement(SpecialGuiElementRenderState state) {
        if (!this.findAndGoToLayerToAdd((GuiElementRenderState)state)) {
            return;
        }
        this.currentLayer.addSpecialElement(state);
        this.onElementAdded(state.bounds());
    }

    public void addSimpleElement(SimpleGuiElementRenderState state) {
        if (!this.findAndGoToLayerToAdd((GuiElementRenderState)state)) {
            return;
        }
        this.currentLayer.addSimpleElement(state);
        this.onElementAdded(state.bounds());
    }

    private void onElementAdded(@Nullable ScreenRect bounds) {
        if (!SharedConstants.RENDER_UI_LAYERING_RECTANGLES || bounds == null) {
            return;
        }
        this.goUpLayer();
        this.currentLayer.addSimpleElement((SimpleGuiElementRenderState)new ColoredQuadGuiElementRenderState(RenderPipelines.GUI, TextureSetup.empty(), (Matrix3x2fc)new Matrix3x2f(), 0, 0, 10000, 10000, 0x774444FF, 0x774444FF, bounds));
    }

    private boolean findAndGoToLayerToAdd(GuiElementRenderState state) {
        ScreenRect screenRect = state.bounds();
        if (screenRect == null) {
            return false;
        }
        if (this.currentLayerBounds != null && this.currentLayerBounds.contains(screenRect)) {
            this.goUpLayer();
        } else {
            this.findAndGoToLayerIntersecting(screenRect);
        }
        this.currentLayerBounds = screenRect;
        return true;
    }

    private void findAndGoToLayerIntersecting(ScreenRect bounds) {
        Layer layer = (Layer)this.rootLayers.getLast();
        while (layer.up != null) {
            layer = layer.up;
        }
        boolean bl = false;
        while (!bl) {
            boolean bl2 = bl = this.anyIntersect(bounds, layer.simpleElementRenderStates) || this.anyIntersect(bounds, layer.itemElementRenderStates) || this.anyIntersect(bounds, layer.textElementRenderStates) || this.anyIntersect(bounds, layer.specialElementRenderStates);
            if (layer.parent == null) break;
            if (bl) continue;
            layer = layer.parent;
        }
        this.currentLayer = layer;
        if (bl) {
            this.goUpLayer();
        }
    }

    private boolean anyIntersect(ScreenRect bounds, @Nullable List<? extends GuiElementRenderState> elementRenderStates) {
        if (elementRenderStates != null) {
            for (GuiElementRenderState guiElementRenderState : elementRenderStates) {
                ScreenRect screenRect = guiElementRenderState.bounds();
                if (screenRect == null || !screenRect.intersects(bounds)) continue;
                return true;
            }
        }
        return false;
    }

    public void addSimpleElementToCurrentLayer(TexturedQuadGuiElementRenderState state) {
        this.currentLayer.addSimpleElement((SimpleGuiElementRenderState)state);
    }

    public void addPreparedTextElement(SimpleGuiElementRenderState state) {
        this.currentLayer.addPreparedText(state);
    }

    public Set<Object> getItemModelKeys() {
        return this.itemModelKeys;
    }

    public void forEachSimpleElement(Consumer<SimpleGuiElementRenderState> consumer, LayerFilter filter) {
        this.forEachLayer(layer -> {
            if (layer.simpleElementRenderStates == null && layer.preparedTextElementRenderStates == null) {
                return;
            }
            if (layer.simpleElementRenderStates != null) {
                for (SimpleGuiElementRenderState simpleGuiElementRenderState : layer.simpleElementRenderStates) {
                    consumer.accept(simpleGuiElementRenderState);
                }
            }
            if (layer.preparedTextElementRenderStates != null) {
                for (SimpleGuiElementRenderState simpleGuiElementRenderState : layer.preparedTextElementRenderStates) {
                    consumer.accept(simpleGuiElementRenderState);
                }
            }
        }, filter);
    }

    public void forEachItemElement(Consumer<ItemGuiElementRenderState> itemElementStateConsumer) {
        Layer layer2 = this.currentLayer;
        this.forEachLayer(layer -> {
            if (layer.itemElementRenderStates != null) {
                this.currentLayer = layer;
                for (ItemGuiElementRenderState itemGuiElementRenderState : layer.itemElementRenderStates) {
                    itemElementStateConsumer.accept(itemGuiElementRenderState);
                }
            }
        }, LayerFilter.ALL);
        this.currentLayer = layer2;
    }

    public void forEachTextElement(Consumer<TextGuiElementRenderState> textElementStateConsumer) {
        Layer layer2 = this.currentLayer;
        this.forEachLayer(layer -> {
            if (layer.textElementRenderStates != null) {
                for (TextGuiElementRenderState textGuiElementRenderState : layer.textElementRenderStates) {
                    this.currentLayer = layer;
                    textElementStateConsumer.accept(textGuiElementRenderState);
                }
            }
        }, LayerFilter.ALL);
        this.currentLayer = layer2;
    }

    public void forEachSpecialElement(Consumer<SpecialGuiElementRenderState> specialElementStateConsumer) {
        Layer layer2 = this.currentLayer;
        this.forEachLayer(layer -> {
            if (layer.specialElementRenderStates != null) {
                this.currentLayer = layer;
                for (SpecialGuiElementRenderState specialGuiElementRenderState : layer.specialElementRenderStates) {
                    specialElementStateConsumer.accept(specialGuiElementRenderState);
                }
            }
        }, LayerFilter.ALL);
        this.currentLayer = layer2;
    }

    public void sortSimpleElements(Comparator<SimpleGuiElementRenderState> simpleElementStateComparator) {
        this.forEachLayer(layer -> {
            if (layer.simpleElementRenderStates != null) {
                if (SharedConstants.SHUFFLE_UI_RENDERING_ORDER) {
                    Collections.shuffle(layer.simpleElementRenderStates);
                }
                layer.simpleElementRenderStates.sort(simpleElementStateComparator);
            }
        }, LayerFilter.ALL);
    }

    private void forEachLayer(Consumer<Layer> layerConsumer, LayerFilter filter) {
        int i = 0;
        int j = this.rootLayers.size();
        if (filter == LayerFilter.BEFORE_BLUR) {
            j = Math.min(this.blurLayer, this.rootLayers.size());
        } else if (filter == LayerFilter.AFTER_BLUR) {
            i = this.blurLayer;
        }
        for (int k = i; k < j; ++k) {
            Layer layer = (Layer)this.rootLayers.get(k);
            this.traverseLayers(layer, layerConsumer);
        }
    }

    private void traverseLayers(Layer layer, Consumer<Layer> layerConsumer) {
        layerConsumer.accept(layer);
        if (layer.up != null) {
            this.traverseLayers(layer.up, layerConsumer);
        }
    }

    public void clear() {
        this.itemModelKeys.clear();
        this.rootLayers.clear();
        this.blurLayer = Integer.MAX_VALUE;
        this.createNewRootLayer();
    }
}

