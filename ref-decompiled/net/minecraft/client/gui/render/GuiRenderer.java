/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$MappedView
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.ProjectionType
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$ShapeIndexBuffer
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  com.mojang.blaze3d.vertex.VertexFormat$IndexType
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer$GlyphDrawer
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gl.MappableRingBuffer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.GuiRenderer
 *  net.minecraft.client.gui.render.GuiRenderer$Draw
 *  net.minecraft.client.gui.render.GuiRenderer$Preparation
 *  net.minecraft.client.gui.render.GuiRenderer$RenderedItem
 *  net.minecraft.client.gui.render.OversizedItemGuiElementRenderer
 *  net.minecraft.client.gui.render.SpecialGuiElementRenderer
 *  net.minecraft.client.gui.render.state.GuiRenderState
 *  net.minecraft.client.gui.render.state.GuiRenderState$LayerFilter
 *  net.minecraft.client.gui.render.state.ItemGuiElementRenderState
 *  net.minecraft.client.gui.render.state.SimpleGuiElementRenderState
 *  net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.OversizedItemGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.BuiltBuffer$DrawParameters
 *  net.minecraft.client.render.DiffuseLighting$Type
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.ProjectionMatrix2
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.command.RenderDispatcher
 *  net.minecraft.client.render.item.KeyedItemRenderState
 *  net.minecraft.client.texture.TextureSetup
 *  net.minecraft.client.util.BufferAllocator
 *  net.minecraft.client.util.Window
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.MathHelper
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.joml.Matrix3x2fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.OversizedItemGuiElementRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.OversizedItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GuiRenderer
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float field_59906 = 10000.0f;
    public static final float field_59901 = 0.0f;
    private static final float field_59907 = 1000.0f;
    public static final int field_59902 = 1000;
    public static final int field_59903 = -1000;
    public static final int field_59908 = 16;
    private static final int field_59909 = 512;
    private static final int MAX_TEXTURE_SIZE = RenderSystem.getDevice().getMaxTextureSize();
    public static final int field_59904 = 0;
    private static final Comparator<ScreenRect> SCISSOR_AREA_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(ScreenRect::getTop).thenComparing(ScreenRect::getBottom).thenComparing(ScreenRect::getLeft).thenComparing(ScreenRect::getRight));
    private static final Comparator<TextureSetup> TEXTURE_SETUP_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(TextureSetup::getSortKey));
    private static final Comparator<SimpleGuiElementRenderState> SIMPLE_ELEMENT_COMPARATOR = Comparator.comparing(SimpleGuiElementRenderState::scissorArea, SCISSOR_AREA_COMPARATOR).thenComparing(SimpleGuiElementRenderState::pipeline, Comparator.comparing(RenderPipeline::getSortKey)).thenComparing(SimpleGuiElementRenderState::textureSetup, TEXTURE_SETUP_COMPARATOR);
    private final Map<Object, RenderedItem> renderedItems = new Object2ObjectOpenHashMap();
    private final Map<Object, OversizedItemGuiElementRenderer> oversizedItems = new Object2ObjectOpenHashMap();
    final GuiRenderState state;
    private final List<Draw> draws = new ArrayList();
    private final List<Preparation> preparations = new ArrayList();
    private final BufferAllocator allocator = new BufferAllocator(786432);
    private final Map<VertexFormat, MappableRingBuffer> bufferByVertexFormat = new Object2ObjectOpenHashMap();
    private int blurLayer = Integer.MAX_VALUE;
    private final ProjectionMatrix2 guiProjectionMatrix = new ProjectionMatrix2("gui", 1000.0f, 11000.0f, true);
    private final ProjectionMatrix2 itemsProjectionMatrix = new ProjectionMatrix2("items", -1000.0f, 1000.0f, true);
    private final VertexConsumerProvider.Immediate vertexConsumers;
    private final OrderedRenderCommandQueue commandQueue;
    private final RenderDispatcher dispatcher;
    private final Map<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> specialElementRenderers;
    private @Nullable GpuTexture itemAtlasTexture;
    private @Nullable GpuTextureView itemAtlasTextureView;
    private @Nullable GpuTexture itemAtlasDepthTexture;
    private @Nullable GpuTextureView itemAtlasDepthTextureView;
    private int itemAtlasX;
    private int itemAtlasY;
    private int windowScaleFactor;
    private int frame;
    private @Nullable ScreenRect scissorArea = null;
    private @Nullable RenderPipeline pipeline = null;
    private @Nullable TextureSetup textureSetup = null;
    private @Nullable BufferBuilder buffer = null;

    public GuiRenderer(GuiRenderState state, VertexConsumerProvider.Immediate vertexConsumers, OrderedRenderCommandQueue queue, RenderDispatcher dispatcher, List<SpecialGuiElementRenderer<?>> specialElementRenderers) {
        this.state = state;
        this.vertexConsumers = vertexConsumers;
        this.commandQueue = queue;
        this.dispatcher = dispatcher;
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (SpecialGuiElementRenderer<?> specialGuiElementRenderer : specialElementRenderers) {
            builder.put((Object)specialGuiElementRenderer.getElementClass(), specialGuiElementRenderer);
        }
        this.specialElementRenderers = builder.buildOrThrow();
    }

    public void incrementFrame() {
        ++this.frame;
    }

    public void render(GpuBufferSlice fogBuffer) {
        this.prepare();
        this.renderPreparedDraws(fogBuffer);
        for (MappableRingBuffer mappableRingBuffer : this.bufferByVertexFormat.values()) {
            mappableRingBuffer.rotate();
        }
        this.draws.clear();
        this.preparations.clear();
        this.state.clear();
        this.blurLayer = Integer.MAX_VALUE;
        this.clearOversizedItems();
        if (SharedConstants.SHUFFLE_UI_RENDERING_ORDER) {
            RenderPipeline.updateSortKeySeed();
            TextureSetup.shuffleRenderingOrder();
        }
    }

    private void clearOversizedItems() {
        Iterator iterator = this.oversizedItems.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            OversizedItemGuiElementRenderer oversizedItemGuiElementRenderer = (OversizedItemGuiElementRenderer)entry.getValue();
            if (!oversizedItemGuiElementRenderer.isOversized()) {
                oversizedItemGuiElementRenderer.close();
                iterator.remove();
                continue;
            }
            oversizedItemGuiElementRenderer.clearOversized();
        }
    }

    private void prepare() {
        this.vertexConsumers.draw();
        this.prepareSpecialElements();
        this.prepareItemElements();
        this.prepareTextElements();
        this.state.sortSimpleElements(SIMPLE_ELEMENT_COMPARATOR);
        this.prepareSimpleElements(GuiRenderState.LayerFilter.BEFORE_BLUR);
        this.blurLayer = this.preparations.size();
        this.prepareSimpleElements(GuiRenderState.LayerFilter.AFTER_BLUR);
        this.finishPreparation();
    }

    private void prepareSimpleElements(GuiRenderState.LayerFilter filter) {
        this.scissorArea = null;
        this.pipeline = null;
        this.textureSetup = null;
        this.buffer = null;
        this.state.forEachSimpleElement(arg_0 -> this.prepareSimpleElement(arg_0), filter);
        if (this.buffer != null) {
            this.endBuffer(this.buffer, this.pipeline, this.textureSetup, this.scissorArea);
        }
    }

    private void renderPreparedDraws(GpuBufferSlice fogBuffer) {
        if (this.draws.isEmpty()) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Window window = minecraftClient.getWindow();
        RenderSystem.setProjectionMatrix((GpuBufferSlice)this.guiProjectionMatrix.set((float)window.getFramebufferWidth() / (float)window.getScaleFactor(), (float)window.getFramebufferHeight() / (float)window.getScaleFactor()), (ProjectionType)ProjectionType.ORTHOGRAPHIC);
        Framebuffer framebuffer = minecraftClient.getFramebuffer();
        int i = 0;
        for (Draw draw : this.draws) {
            if (draw.indexCount <= i) continue;
            i = draw.indexCount;
        }
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(i);
        VertexFormat.IndexType indexType = shapeIndexBuffer.getIndexType();
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)new Matrix4f().setTranslation(0.0f, 0.0f, -11000.0f), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        if (this.blurLayer > 0) {
            this.render(() -> "GUI before blur", framebuffer, fogBuffer, gpuBufferSlice, gpuBuffer, indexType, 0, Math.min(this.blurLayer, this.draws.size()));
        }
        if (this.draws.size() <= this.blurLayer) {
            return;
        }
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(framebuffer.getDepthAttachment(), 1.0);
        minecraftClient.gameRenderer.renderBlur();
        this.render(() -> "GUI after blur", framebuffer, fogBuffer, gpuBufferSlice, gpuBuffer, indexType, this.blurLayer, this.draws.size());
    }

    private void render(Supplier<String> nameSupplier, Framebuffer framebuffer, GpuBufferSlice fogBuffer, GpuBufferSlice dynamicTransformsBuffer, GpuBuffer buffer, VertexFormat.IndexType indexType, int from, int to) {
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(nameSupplier, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null, OptionalDouble.empty());){
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("Fog", fogBuffer);
            renderPass.setUniform("DynamicTransforms", dynamicTransformsBuffer);
            for (int i = from; i < to; ++i) {
                Draw draw = (Draw)this.draws.get(i);
                this.render(draw, renderPass, buffer, indexType);
            }
        }
    }

    private void prepareSimpleElement(SimpleGuiElementRenderState state) {
        RenderPipeline renderPipeline = state.pipeline();
        TextureSetup textureSetup = state.textureSetup();
        ScreenRect screenRect = state.scissorArea();
        if (renderPipeline != this.pipeline || this.scissorChanged(screenRect, this.scissorArea) || !textureSetup.equals((Object)this.textureSetup)) {
            if (this.buffer != null) {
                this.endBuffer(this.buffer, this.pipeline, this.textureSetup, this.scissorArea);
            }
            this.buffer = this.startBuffer(renderPipeline);
            this.pipeline = renderPipeline;
            this.textureSetup = textureSetup;
            this.scissorArea = screenRect;
        }
        state.setupVertices((VertexConsumer)this.buffer);
    }

    private void prepareTextElements() {
        this.state.forEachTextElement(state -> {
            Matrix3x2fc matrix3x2fc = state.matrix;
            ScreenRect screenRect = state.clipBounds;
            state.prepare().draw((TextRenderer.GlyphDrawer)new /* Unavailable Anonymous Inner Class!! */);
        });
    }

    private void prepareItemElements() {
        if (this.state.getItemModelKeys().isEmpty()) {
            return;
        }
        int i = this.getWindowScaleFactor();
        int j = 16 * i;
        int k = this.calcItemAtlasSideLength(j);
        if (this.itemAtlasTexture == null) {
            this.createItemAtlas(k);
        }
        RenderSystem.outputColorTextureOverride = this.itemAtlasTextureView;
        RenderSystem.outputDepthTextureOverride = this.itemAtlasDepthTextureView;
        RenderSystem.setProjectionMatrix((GpuBufferSlice)this.itemsProjectionMatrix.set((float)k, (float)k), (ProjectionType)ProjectionType.ORTHOGRAPHIC);
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
        MatrixStack matrixStack = new MatrixStack();
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        MutableBoolean mutableBoolean2 = new MutableBoolean(false);
        this.state.forEachItemElement(elem -> {
            int l;
            boolean bl;
            if (elem.oversizedBounds() != null) {
                mutableBoolean2.setTrue();
                return;
            }
            KeyedItemRenderState keyedItemRenderState = elem.state();
            RenderedItem renderedItem = (RenderedItem)this.renderedItems.get(keyedItemRenderState.getModelKey());
            if (!(renderedItem == null || keyedItemRenderState.isAnimated() && renderedItem.frame != this.frame)) {
                this.prepareItem(elem, renderedItem.u, renderedItem.v, j, k);
                return;
            }
            if (this.itemAtlasX + j > k) {
                this.itemAtlasX = 0;
                this.itemAtlasY += j;
            }
            boolean bl2 = bl = keyedItemRenderState.isAnimated() && renderedItem != null;
            if (!bl && this.itemAtlasY + j > k) {
                if (mutableBoolean.isFalse()) {
                    LOGGER.warn("Trying to render too many items in GUI at the same time. Skipping some of them.");
                    mutableBoolean.setTrue();
                }
                return;
            }
            int k = bl ? renderedItem.x : this.itemAtlasX;
            int n = l = bl ? renderedItem.y : this.itemAtlasY;
            if (bl) {
                RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.itemAtlasTexture, 0, this.itemAtlasDepthTexture, 1.0, k, k - l - j, j, j);
            }
            this.prepareItemInitially(keyedItemRenderState, matrixStack, k, l, j);
            float f = (float)k / (float)k;
            float g = (float)(k - l) / (float)k;
            this.prepareItem(elem, f, g, j, k);
            if (bl) {
                renderedItem.frame = this.frame;
            } else {
                this.renderedItems.put(elem.state().getModelKey(), new RenderedItem(this.itemAtlasX, this.itemAtlasY, f, g, this.frame));
                this.itemAtlasX += j;
            }
        });
        RenderSystem.outputColorTextureOverride = null;
        RenderSystem.outputDepthTextureOverride = null;
        if (mutableBoolean2.booleanValue()) {
            this.state.forEachItemElement(elem -> {
                if (elem.oversizedBounds() != null) {
                    KeyedItemRenderState keyedItemRenderState = elem.state();
                    OversizedItemGuiElementRenderer oversizedItemGuiElementRenderer = this.oversizedItems.computeIfAbsent(keyedItemRenderState.getModelKey(), object -> new OversizedItemGuiElementRenderer(this.vertexConsumers));
                    ScreenRect screenRect = elem.oversizedBounds();
                    OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState = new OversizedItemGuiElementRenderState(elem, screenRect.getLeft(), screenRect.getTop(), screenRect.getRight(), screenRect.getBottom());
                    oversizedItemGuiElementRenderer.render((SpecialGuiElementRenderState)oversizedItemGuiElementRenderState, this.state, i);
                }
            });
        }
    }

    private void prepareSpecialElements() {
        int i = MinecraftClient.getInstance().getWindow().getScaleFactor();
        this.state.forEachSpecialElement(state -> this.prepareSpecialElement(state, i));
    }

    private <T extends SpecialGuiElementRenderState> void prepareSpecialElement(T elementState, int windowScaleFactor) {
        SpecialGuiElementRenderer specialGuiElementRenderer = (SpecialGuiElementRenderer)this.specialElementRenderers.get(elementState.getClass());
        if (specialGuiElementRenderer != null) {
            specialGuiElementRenderer.render(elementState, this.state, windowScaleFactor);
        }
    }

    private void prepareItemInitially(KeyedItemRenderState state, MatrixStack matrices, int x, int y, int scale) {
        boolean bl;
        matrices.push();
        matrices.translate((float)x + (float)scale / 2.0f, (float)y + (float)scale / 2.0f, 0.0f);
        matrices.scale((float)scale, (float)(-scale), (float)scale);
        boolean bl2 = bl = !state.isSideLit();
        if (bl) {
            MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
        } else {
            MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
        }
        RenderSystem.enableScissorForRenderTypeDraws((int)x, (int)(this.itemAtlasTexture.getHeight(0) - y - scale), (int)scale, (int)scale);
        state.render(matrices, this.commandQueue, 0xF000F0, OverlayTexture.DEFAULT_UV, 0);
        this.dispatcher.render();
        this.vertexConsumers.draw();
        RenderSystem.disableScissorForRenderTypeDraws();
        matrices.pop();
    }

    private void prepareItem(ItemGuiElementRenderState state, float u, float v, int pixelsPerItem, int itemAtlasSideLength) {
        float f = u + (float)pixelsPerItem / (float)itemAtlasSideLength;
        float g = v + (float)(-pixelsPerItem) / (float)itemAtlasSideLength;
        this.state.addSimpleElementToCurrentLayer(new TexturedQuadGuiElementRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.of((GpuTextureView)this.itemAtlasTextureView, (GpuSampler)RenderSystem.getSamplerCache().getRepeated(FilterMode.NEAREST)), state.pose(), state.x(), state.y(), state.x() + 16, state.y() + 16, u, f, v, g, -1, state.scissorArea(), null));
    }

    private void createItemAtlas(int sideLength) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.itemAtlasTexture = gpuDevice.createTexture("UI items atlas", 12, TextureFormat.RGBA8, sideLength, sideLength, 1, 1);
        this.itemAtlasTextureView = gpuDevice.createTextureView(this.itemAtlasTexture);
        this.itemAtlasDepthTexture = gpuDevice.createTexture("UI items atlas depth", 8, TextureFormat.DEPTH32, sideLength, sideLength, 1, 1);
        this.itemAtlasDepthTextureView = gpuDevice.createTextureView(this.itemAtlasDepthTexture);
        gpuDevice.createCommandEncoder().clearColorAndDepthTextures(this.itemAtlasTexture, 0, this.itemAtlasDepthTexture, 1.0);
    }

    private int calcItemAtlasSideLength(int itemCount) {
        int i;
        Set set = this.state.getItemModelKeys();
        if (this.renderedItems.isEmpty()) {
            i = set.size();
        } else {
            i = this.renderedItems.size();
            for (Object object : set) {
                if (this.renderedItems.containsKey(object)) continue;
                ++i;
            }
        }
        if (this.itemAtlasTexture != null) {
            int j = this.itemAtlasTexture.getWidth(0) / itemCount;
            int k = j * j;
            if (i < k) {
                return this.itemAtlasTexture.getWidth(0);
            }
            this.onItemAtlasChanged();
        }
        int j = set.size();
        int k = MathHelper.smallestEncompassingSquareSideLength((int)(j + j / 2));
        return Math.clamp((long)MathHelper.smallestEncompassingPowerOfTwo((int)(k * itemCount)), 512, MAX_TEXTURE_SIZE);
    }

    private int getWindowScaleFactor() {
        int i = MinecraftClient.getInstance().getWindow().getScaleFactor();
        if (i != this.windowScaleFactor) {
            this.onItemAtlasChanged();
            for (OversizedItemGuiElementRenderer oversizedItemGuiElementRenderer : this.oversizedItems.values()) {
                oversizedItemGuiElementRenderer.clearModel();
            }
            this.windowScaleFactor = i;
        }
        return i;
    }

    private void onItemAtlasChanged() {
        this.itemAtlasX = 0;
        this.itemAtlasY = 0;
        this.renderedItems.clear();
        if (this.itemAtlasTexture != null) {
            this.itemAtlasTexture.close();
            this.itemAtlasTexture = null;
        }
        if (this.itemAtlasTextureView != null) {
            this.itemAtlasTextureView.close();
            this.itemAtlasTextureView = null;
        }
        if (this.itemAtlasDepthTexture != null) {
            this.itemAtlasDepthTexture.close();
            this.itemAtlasDepthTexture = null;
        }
        if (this.itemAtlasDepthTextureView != null) {
            this.itemAtlasDepthTextureView.close();
            this.itemAtlasDepthTextureView = null;
        }
    }

    private void endBuffer(BufferBuilder builder, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRect scissorArea) {
        BuiltBuffer builtBuffer = builder.endNullable();
        if (builtBuffer != null) {
            this.preparations.add(new Preparation(builtBuffer, pipeline, textureSetup, scissorArea));
        }
    }

    private void finishPreparation() {
        this.initVertexBuffers();
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        for (Preparation preparation : this.preparations) {
            BuiltBuffer builtBuffer = preparation.mesh;
            BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();
            VertexFormat vertexFormat = drawParameters.format();
            MappableRingBuffer mappableRingBuffer = (MappableRingBuffer)this.bufferByVertexFormat.get(vertexFormat);
            if (!object2IntMap.containsKey((Object)vertexFormat)) {
                object2IntMap.put((Object)vertexFormat, 0);
            }
            ByteBuffer byteBuffer = builtBuffer.getBuffer();
            int i = byteBuffer.remaining();
            int j = object2IntMap.getInt((Object)vertexFormat);
            try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(mappableRingBuffer.getBlocking().slice((long)j, (long)i), false, true);){
                MemoryUtil.memCopy((ByteBuffer)byteBuffer, (ByteBuffer)mappedView.data());
            }
            object2IntMap.put((Object)vertexFormat, j + i);
            this.draws.add(new Draw(mappableRingBuffer.getBlocking(), j / vertexFormat.getVertexSize(), drawParameters.mode(), drawParameters.indexCount(), preparation.pipeline, preparation.textureSetup, preparation.scissorArea));
            preparation.close();
        }
    }

    private void initVertexBuffers() {
        Object2IntMap object2IntMap = this.collectVertexSizes();
        for (Object2IntMap.Entry entry : object2IntMap.object2IntEntrySet()) {
            VertexFormat vertexFormat = (VertexFormat)entry.getKey();
            int i = entry.getIntValue();
            MappableRingBuffer mappableRingBuffer = (MappableRingBuffer)this.bufferByVertexFormat.get(vertexFormat);
            if (mappableRingBuffer != null && mappableRingBuffer.size() >= i) continue;
            if (mappableRingBuffer != null) {
                mappableRingBuffer.close();
            }
            this.bufferByVertexFormat.put(vertexFormat, new MappableRingBuffer(() -> "GUI vertex buffer for " + String.valueOf(vertexFormat), 34, i));
        }
    }

    private Object2IntMap<VertexFormat> collectVertexSizes() {
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        for (Preparation preparation : this.preparations) {
            BuiltBuffer.DrawParameters drawParameters = preparation.mesh.getDrawParameters();
            VertexFormat vertexFormat = drawParameters.format();
            if (!object2IntMap.containsKey((Object)vertexFormat)) {
                object2IntMap.put((Object)vertexFormat, 0);
            }
            object2IntMap.put((Object)vertexFormat, object2IntMap.getInt((Object)vertexFormat) + drawParameters.vertexCount() * vertexFormat.getVertexSize());
        }
        return object2IntMap;
    }

    private void render(Draw draw, RenderPass pass, GpuBuffer indexBuffer, VertexFormat.IndexType indexType) {
        RenderPipeline renderPipeline = draw.pipeline();
        pass.setPipeline(renderPipeline);
        pass.setVertexBuffer(0, draw.vertexBuffer);
        ScreenRect screenRect = draw.scissorArea();
        if (screenRect != null) {
            this.enableScissor(screenRect, pass);
        } else {
            pass.disableScissor();
        }
        if (draw.textureSetup.texure0() != null) {
            pass.bindTexture("Sampler0", draw.textureSetup.texure0(), draw.textureSetup.sampler0());
        }
        if (draw.textureSetup.texure1() != null) {
            pass.bindTexture("Sampler1", draw.textureSetup.texure1(), draw.textureSetup.sampler1());
        }
        if (draw.textureSetup.texure2() != null) {
            pass.bindTexture("Sampler2", draw.textureSetup.texure2(), draw.textureSetup.sampler2());
        }
        pass.setIndexBuffer(indexBuffer, indexType);
        pass.drawIndexed(draw.baseVertex, 0, draw.indexCount, 1);
    }

    private BufferBuilder startBuffer(RenderPipeline pipeline) {
        return new BufferBuilder(this.allocator, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
    }

    private boolean scissorChanged(@Nullable ScreenRect oldScissorArea, @Nullable ScreenRect newScissorArea) {
        if (oldScissorArea == newScissorArea) {
            return false;
        }
        if (oldScissorArea != null) {
            return !oldScissorArea.equals((Object)newScissorArea);
        }
        return true;
    }

    private void enableScissor(ScreenRect scissorArea, RenderPass pass) {
        Window window = MinecraftClient.getInstance().getWindow();
        int i = window.getFramebufferHeight();
        int j = window.getScaleFactor();
        double d = scissorArea.getLeft() * j;
        double e = i - scissorArea.getBottom() * j;
        double f = scissorArea.width() * j;
        double g = scissorArea.height() * j;
        pass.enableScissor((int)d, (int)e, Math.max(0, (int)f), Math.max(0, (int)g));
    }

    @Override
    public void close() {
        this.allocator.close();
        if (this.itemAtlasTexture != null) {
            this.itemAtlasTexture.close();
        }
        if (this.itemAtlasTextureView != null) {
            this.itemAtlasTextureView.close();
        }
        if (this.itemAtlasDepthTexture != null) {
            this.itemAtlasDepthTexture.close();
        }
        if (this.itemAtlasDepthTextureView != null) {
            this.itemAtlasDepthTextureView.close();
        }
        this.specialElementRenderers.values().forEach(SpecialGuiElementRenderer::close);
        this.guiProjectionMatrix.close();
        this.itemsProjectionMatrix.close();
        for (MappableRingBuffer mappableRingBuffer : this.bufferByVertexFormat.values()) {
            mappableRingBuffer.close();
        }
        this.oversizedItems.values().forEach(SpecialGuiElementRenderer::close);
    }
}

