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
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GlyphEffectGuiElementRenderState;
import net.minecraft.client.gui.render.state.GlyphGuiElementRenderState;
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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class GuiRenderer implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float field_59906 = 10000.0F;
   private static final float field_59901 = 0.0F;
   private static final float field_59907 = 1000.0F;
   public static final int field_59902 = 1000;
   public static final int field_59903 = -1000;
   public static final int field_59908 = 16;
   private static final int field_59909 = 512;
   private static final int MAX_TEXTURE_SIZE = RenderSystem.getDevice().getMaxTextureSize();
   public static final int field_59904 = 0;
   private static final Comparator SCISSOR_AREA_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(ScreenRect::getTop).thenComparing(ScreenRect::getBottom).thenComparing(ScreenRect::getLeft).thenComparing(ScreenRect::getRight));
   private static final Comparator TEXTURE_SETUP_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(TextureSetup::getSortKey));
   private static final Comparator SIMPLE_ELEMENT_COMPARATOR;
   private final Map renderedItems = new Object2ObjectOpenHashMap();
   private final Map oversizedItems = new Object2ObjectOpenHashMap();
   final GuiRenderState state;
   private final List draws = new ArrayList();
   private final List preparations = new ArrayList();
   private final BufferAllocator allocator = new BufferAllocator(786432);
   private final Map bufferByVertexFormat = new Object2ObjectOpenHashMap();
   private int blurLayer = Integer.MAX_VALUE;
   private final ProjectionMatrix2 guiProjectionMatrix = new ProjectionMatrix2("gui", 1000.0F, 11000.0F, true);
   private final ProjectionMatrix2 itemsProjectionMatrix = new ProjectionMatrix2("items", -1000.0F, 1000.0F, true);
   private final VertexConsumerProvider.Immediate vertexConsumers;
   private final Map specialElementRenderers;
   @Nullable
   private GpuTexture itemAtlasTexture;
   @Nullable
   private GpuTextureView itemAtlasTextureView;
   @Nullable
   private GpuTexture itemAtlasDepthTexture;
   @Nullable
   private GpuTextureView itemAtlasDepthTextureView;
   private int itemAtlasX;
   private int itemAtlasY;
   private int windowScaleFactor;
   private int frame;
   @Nullable
   private ScreenRect scissorArea = null;
   @Nullable
   private RenderPipeline pipeline = null;
   @Nullable
   private TextureSetup textureSetup = null;
   @Nullable
   private BufferBuilder buffer = null;

   public GuiRenderer(GuiRenderState state, VertexConsumerProvider.Immediate vertexConsumers, List specialElementRenderers) {
      this.state = state;
      this.vertexConsumers = vertexConsumers;
      ImmutableMap.Builder builder = ImmutableMap.builder();
      Iterator var5 = specialElementRenderers.iterator();

      while(var5.hasNext()) {
         SpecialGuiElementRenderer specialGuiElementRenderer = (SpecialGuiElementRenderer)var5.next();
         builder.put(specialGuiElementRenderer.getElementClass(), specialGuiElementRenderer);
      }

      this.specialElementRenderers = builder.buildOrThrow();
   }

   public void incrementFrame() {
      ++this.frame;
   }

   public void render(GpuBufferSlice fogBuffer) {
      this.prepare();
      this.renderPreparedDraws(fogBuffer);
      Iterator var2 = this.bufferByVertexFormat.values().iterator();

      while(var2.hasNext()) {
         MappableRingBuffer mappableRingBuffer = (MappableRingBuffer)var2.next();
         mappableRingBuffer.rotate();
      }

      this.draws.clear();
      this.preparations.clear();
      this.state.clear();
      this.blurLayer = Integer.MAX_VALUE;
      this.clearOversizedItems();
   }

   private void clearOversizedItems() {
      Iterator iterator = this.oversizedItems.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         OversizedItemGuiElementRenderer oversizedItemGuiElementRenderer = (OversizedItemGuiElementRenderer)entry.getValue();
         if (!oversizedItemGuiElementRenderer.isOversized()) {
            oversizedItemGuiElementRenderer.close();
            iterator.remove();
         } else {
            oversizedItemGuiElementRenderer.clearOversized();
         }
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
      this.state.forEachSimpleElement(this::prepareSimpleElement, filter);
      if (this.buffer != null) {
         this.endBuffer(this.buffer, this.pipeline, this.textureSetup, this.scissorArea);
      }

   }

   private void renderPreparedDraws(GpuBufferSlice fogBuffer) {
      if (!this.draws.isEmpty()) {
         MinecraftClient minecraftClient = MinecraftClient.getInstance();
         Window window = minecraftClient.getWindow();
         RenderSystem.setProjectionMatrix(this.guiProjectionMatrix.set((float)window.getFramebufferWidth() / (float)window.getScaleFactor(), (float)window.getFramebufferHeight() / (float)window.getScaleFactor()), ProjectionType.ORTHOGRAPHIC);
         Framebuffer framebuffer = minecraftClient.getFramebuffer();
         int i = 0;
         Iterator var6 = this.draws.iterator();

         while(var6.hasNext()) {
            Draw draw = (Draw)var6.next();
            if (draw.indexCount > i) {
               i = draw.indexCount;
            }
         }

         RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
         GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(i);
         VertexFormat.IndexType indexType = shapeIndexBuffer.getIndexType();
         GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((new Matrix4f()).setTranslation(0.0F, 0.0F, -11000.0F), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 0.0F);
         if (this.blurLayer > 0) {
            this.render(() -> {
               return "GUI before blur";
            }, framebuffer, fogBuffer, gpuBufferSlice, gpuBuffer, indexType, 0, Math.min(this.blurLayer, this.draws.size()));
         }

         if (this.draws.size() > this.blurLayer) {
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(framebuffer.getDepthAttachment(), 1.0);
            minecraftClient.gameRenderer.renderBlur();
            this.render(() -> {
               return "GUI after blur";
            }, framebuffer, fogBuffer, gpuBufferSlice, gpuBuffer, indexType, this.blurLayer, this.draws.size());
         }
      }
   }

   private void render(Supplier nameSupplier, Framebuffer framebuffer, GpuBufferSlice fogBuffer, GpuBufferSlice dynamicTransformsBuffer, GpuBuffer buffer, VertexFormat.IndexType indexType, int from, int to) {
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(nameSupplier, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null, OptionalDouble.empty());

      try {
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setUniform("Fog", fogBuffer);
         renderPass.setUniform("DynamicTransforms", dynamicTransformsBuffer);

         for(int i = from; i < to; ++i) {
            Draw draw = (Draw)this.draws.get(i);
            this.render(draw, renderPass, buffer, indexType);
         }
      } catch (Throwable var13) {
         if (renderPass != null) {
            try {
               renderPass.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (renderPass != null) {
         renderPass.close();
      }

   }

   private void prepareSimpleElement(SimpleGuiElementRenderState state, int depth) {
      RenderPipeline renderPipeline = state.pipeline();
      TextureSetup textureSetup = state.textureSetup();
      ScreenRect screenRect = state.scissorArea();
      if (renderPipeline != this.pipeline || this.scissorChanged(screenRect, this.scissorArea) || !textureSetup.equals(this.textureSetup)) {
         if (this.buffer != null) {
            this.endBuffer(this.buffer, this.pipeline, this.textureSetup, this.scissorArea);
         }

         this.buffer = this.startBuffer(renderPipeline);
         this.pipeline = renderPipeline;
         this.textureSetup = textureSetup;
         this.scissorArea = screenRect;
      }

      state.setupVertices(this.buffer, 0.0F + (float)depth);
   }

   private void prepareTextElements() {
      this.state.forEachTextElement((state) -> {
         final Matrix3x2f matrix3x2f = state.matrix;
         final ScreenRect screenRect = state.clipBounds;
         state.prepare().draw(new TextRenderer.GlyphDrawer() {
            public void drawGlyph(BakedGlyph.DrawnGlyph glyph) {
               if (glyph.glyph().getTexture() != null) {
                  GuiRenderer.this.state.addPreparedTextElement(new GlyphGuiElementRenderState(matrix3x2f, glyph, screenRect));
               }

            }

            public void drawRectangle(BakedGlyph bakedGlyph, BakedGlyph.Rectangle rect) {
               if (bakedGlyph.getTexture() != null) {
                  GuiRenderer.this.state.addPreparedTextElement(new GlyphEffectGuiElementRenderState(matrix3x2f, bakedGlyph, rect, screenRect));
               }

            }
         });
      });
   }

   private void prepareItemElements() {
      if (!this.state.getItemModelKeys().isEmpty()) {
         int i = this.getWindowScaleFactor();
         int j = 16 * i;
         int k = this.calcItemAtlasSideLength(j);
         if (this.itemAtlasTexture == null) {
            this.createItemAtlas(k);
         }

         RenderSystem.outputColorTextureOverride = this.itemAtlasTextureView;
         RenderSystem.outputDepthTextureOverride = this.itemAtlasDepthTextureView;
         RenderSystem.setProjectionMatrix(this.itemsProjectionMatrix.set((float)k, (float)k), ProjectionType.ORTHOGRAPHIC);
         MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
         MatrixStack matrixStack = new MatrixStack();
         MutableBoolean mutableBoolean = new MutableBoolean(false);
         MutableBoolean mutableBoolean2 = new MutableBoolean(false);
         this.state.forEachItemElement((elem) -> {
            if (elem.oversizedBounds() != null) {
               mutableBoolean2.setTrue();
            } else {
               KeyedItemRenderState keyedItemRenderState = elem.state();
               RenderedItem renderedItem = (RenderedItem)this.renderedItems.get(keyedItemRenderState.getModelKey());
               if (renderedItem != null && (!keyedItemRenderState.isAnimated() || renderedItem.frame == this.frame)) {
                  this.prepareItem(elem, renderedItem.u, renderedItem.v, j, k);
               } else {
                  if (this.itemAtlasX + j > k) {
                     this.itemAtlasX = 0;
                     this.itemAtlasY += j;
                  }

                  boolean bl = keyedItemRenderState.isAnimated() && renderedItem != null;
                  if (!bl && this.itemAtlasY + j > k) {
                     if (mutableBoolean.isFalse()) {
                        LOGGER.warn("Trying to render too many items in GUI at the same time. Skipping some of them.");
                        mutableBoolean.setTrue();
                     }

                  } else {
                     int kx = bl ? renderedItem.x : this.itemAtlasX;
                     int l = bl ? renderedItem.y : this.itemAtlasY;
                     if (bl) {
                        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.itemAtlasTexture, 0, this.itemAtlasDepthTexture, 1.0, kx, k - l - j, j, j);
                     }

                     this.prepareItemInitially(keyedItemRenderState, matrixStack, kx, l, j);
                     float f = (float)kx / (float)k;
                     float g = (float)(k - l) / (float)k;
                     this.prepareItem(elem, f, g, j, k);
                     if (bl) {
                        renderedItem.frame = this.frame;
                     } else {
                        this.renderedItems.put(elem.state().getModelKey(), new RenderedItem(this.itemAtlasX, this.itemAtlasY, f, g, this.frame));
                        this.itemAtlasX += j;
                     }

                  }
               }
            }
         });
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         if (mutableBoolean2.getValue()) {
            this.state.forEachItemElement((elem) -> {
               if (elem.oversizedBounds() != null) {
                  KeyedItemRenderState keyedItemRenderState = elem.state();
                  OversizedItemGuiElementRenderer oversizedItemGuiElementRenderer = (OversizedItemGuiElementRenderer)this.oversizedItems.computeIfAbsent(keyedItemRenderState.getModelKey(), (object) -> {
                     return new OversizedItemGuiElementRenderer(this.vertexConsumers);
                  });
                  ScreenRect screenRect = elem.oversizedBounds();
                  OversizedItemGuiElementRenderState oversizedItemGuiElementRenderState = new OversizedItemGuiElementRenderState(elem, screenRect.getLeft(), screenRect.getTop(), screenRect.getRight(), screenRect.getBottom());
                  oversizedItemGuiElementRenderer.render(oversizedItemGuiElementRenderState, this.state, i);
               }

            });
         }

      }
   }

   private void prepareSpecialElements() {
      int i = MinecraftClient.getInstance().getWindow().getScaleFactor();
      this.state.forEachSpecialElement((state) -> {
         this.prepareSpecialElement(state, i);
      });
   }

   private void prepareSpecialElement(SpecialGuiElementRenderState elementState, int windowScaleFactor) {
      SpecialGuiElementRenderer specialGuiElementRenderer = (SpecialGuiElementRenderer)this.specialElementRenderers.get(elementState.getClass());
      if (specialGuiElementRenderer != null) {
         specialGuiElementRenderer.render(elementState, this.state, windowScaleFactor);
      }

   }

   private void prepareItemInitially(KeyedItemRenderState state, MatrixStack matrices, int x, int y, int scale) {
      matrices.push();
      matrices.translate((float)x + (float)scale / 2.0F, (float)y + (float)scale / 2.0F, 0.0F);
      matrices.scale((float)scale, (float)(-scale), (float)scale);
      boolean bl = !state.isSideLit();
      if (bl) {
         MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
      } else {
         MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
      }

      RenderSystem.enableScissorForRenderTypeDraws(x, this.itemAtlasTexture.getHeight(0) - y - scale, scale, scale);
      state.render(matrices, this.vertexConsumers, 15728880, OverlayTexture.DEFAULT_UV);
      this.vertexConsumers.draw();
      RenderSystem.disableScissorForRenderTypeDraws();
      matrices.pop();
   }

   private void prepareItem(ItemGuiElementRenderState state, float u, float v, int pixelsPerItem, int itemAtlasSideLength) {
      float f = u + (float)pixelsPerItem / (float)itemAtlasSideLength;
      float g = v + (float)(-pixelsPerItem) / (float)itemAtlasSideLength;
      this.state.addSimpleElementToCurrentLayer(new TexturedQuadGuiElementRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.withoutGlTexture(this.itemAtlasTextureView), state.pose(), state.x(), state.y(), state.x() + 16, state.y() + 16, u, f, v, g, -1, state.scissorArea(), (ScreenRect)null));
   }

   private void createItemAtlas(int sideLength) {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.itemAtlasTexture = gpuDevice.createTexture((String)"UI items atlas", 12, TextureFormat.RGBA8, sideLength, sideLength, 1, 1);
      this.itemAtlasTexture.setTextureFilter(FilterMode.NEAREST, false);
      this.itemAtlasTextureView = gpuDevice.createTextureView(this.itemAtlasTexture);
      this.itemAtlasDepthTexture = gpuDevice.createTexture((String)"UI items atlas depth", 8, TextureFormat.DEPTH32, sideLength, sideLength, 1, 1);
      this.itemAtlasDepthTextureView = gpuDevice.createTextureView(this.itemAtlasDepthTexture);
      gpuDevice.createCommandEncoder().clearColorAndDepthTextures(this.itemAtlasTexture, 0, this.itemAtlasDepthTexture, 1.0);
   }

   private int calcItemAtlasSideLength(int itemCount) {
      Set set = this.state.getItemModelKeys();
      int i;
      if (this.renderedItems.isEmpty()) {
         i = set.size();
      } else {
         i = this.renderedItems.size();
         Iterator var4 = set.iterator();

         while(var4.hasNext()) {
            Object object = var4.next();
            if (!this.renderedItems.containsKey(object)) {
               ++i;
            }
         }
      }

      int j;
      int k;
      if (this.itemAtlasTexture != null) {
         j = this.itemAtlasTexture.getWidth(0) / itemCount;
         k = j * j;
         if (i < k) {
            return this.itemAtlasTexture.getWidth(0);
         }

         this.onItemAtlasChanged();
      }

      j = set.size();
      k = MathHelper.smallestEncompassingSquareSideLength(j + j / 2);
      return Math.clamp((long)MathHelper.smallestEncompassingPowerOfTwo(k * itemCount), 512, MAX_TEXTURE_SIZE);
   }

   private int getWindowScaleFactor() {
      int i = MinecraftClient.getInstance().getWindow().getScaleFactor();
      if (i != this.windowScaleFactor) {
         this.onItemAtlasChanged();
         Iterator var2 = this.oversizedItems.values().iterator();

         while(var2.hasNext()) {
            OversizedItemGuiElementRenderer oversizedItemGuiElementRenderer = (OversizedItemGuiElementRenderer)var2.next();
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
      BuiltBuffer builtBuffer = builder.end();
      this.preparations.add(new Preparation(builtBuffer, pipeline, textureSetup, scissorArea));
   }

   private void finishPreparation() {
      this.initVertexBuffers();
      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      Iterator var3 = this.preparations.iterator();

      while(var3.hasNext()) {
         Preparation preparation = (Preparation)var3.next();
         BuiltBuffer builtBuffer = preparation.mesh;
         BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();
         VertexFormat vertexFormat = drawParameters.format();
         MappableRingBuffer mappableRingBuffer = (MappableRingBuffer)this.bufferByVertexFormat.get(vertexFormat);
         if (!object2IntMap.containsKey(vertexFormat)) {
            object2IntMap.put(vertexFormat, 0);
         }

         ByteBuffer byteBuffer = builtBuffer.getBuffer();
         int i = byteBuffer.remaining();
         int j = object2IntMap.getInt(vertexFormat);
         GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(mappableRingBuffer.getBlocking().slice(j, i), false, true);

         try {
            MemoryUtil.memCopy(byteBuffer, mappedView.data());
         } catch (Throwable var16) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }
            }

            throw var16;
         }

         if (mappedView != null) {
            mappedView.close();
         }

         object2IntMap.put(vertexFormat, j + i);
         this.draws.add(new Draw(mappableRingBuffer.getBlocking(), j / vertexFormat.getVertexSize(), drawParameters.mode(), drawParameters.indexCount(), preparation.pipeline, preparation.textureSetup, preparation.scissorArea));
         preparation.close();
      }

   }

   private void initVertexBuffers() {
      Object2IntMap object2IntMap = this.collectVertexSizes();
      ObjectIterator var2 = object2IntMap.object2IntEntrySet().iterator();

      while(true) {
         VertexFormat vertexFormat;
         int i;
         MappableRingBuffer mappableRingBuffer;
         do {
            if (!var2.hasNext()) {
               return;
            }

            Object2IntMap.Entry entry = (Object2IntMap.Entry)var2.next();
            vertexFormat = (VertexFormat)entry.getKey();
            i = entry.getIntValue();
            mappableRingBuffer = (MappableRingBuffer)this.bufferByVertexFormat.get(vertexFormat);
         } while(mappableRingBuffer != null && mappableRingBuffer.size() >= i);

         if (mappableRingBuffer != null) {
            mappableRingBuffer.close();
         }

         this.bufferByVertexFormat.put(vertexFormat, new MappableRingBuffer(() -> {
            return "GUI vertex buffer for " + String.valueOf(vertexFormat);
         }, 34, i));
      }
   }

   private Object2IntMap collectVertexSizes() {
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();

      BuiltBuffer.DrawParameters drawParameters;
      VertexFormat vertexFormat;
      for(Iterator var2 = this.preparations.iterator(); var2.hasNext(); object2IntMap.put(vertexFormat, object2IntMap.getInt(vertexFormat) + drawParameters.vertexCount() * vertexFormat.getVertexSize())) {
         Preparation preparation = (Preparation)var2.next();
         drawParameters = preparation.mesh.getDrawParameters();
         vertexFormat = drawParameters.format();
         if (!object2IntMap.containsKey(vertexFormat)) {
            object2IntMap.put(vertexFormat, 0);
         }
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
         pass.bindSampler("Sampler0", draw.textureSetup.texure0());
      }

      if (draw.textureSetup.texure1() != null) {
         pass.bindSampler("Sampler1", draw.textureSetup.texure1());
      }

      if (draw.textureSetup.texure2() != null) {
         pass.bindSampler("Sampler2", draw.textureSetup.texure2());
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
      } else if (oldScissorArea != null) {
         return !oldScissorArea.equals(newScissorArea);
      } else {
         return true;
      }
   }

   private void enableScissor(ScreenRect scissorArea, RenderPass pass) {
      Window window = MinecraftClient.getInstance().getWindow();
      int i = window.getFramebufferHeight();
      int j = window.getScaleFactor();
      double d = (double)(scissorArea.getLeft() * j);
      double e = (double)(i - scissorArea.getBottom() * j);
      double f = (double)(scissorArea.width() * j);
      double g = (double)(scissorArea.height() * j);
      pass.enableScissor((int)d, (int)e, Math.max(0, (int)f), Math.max(0, (int)g));
   }

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
      Iterator var1 = this.bufferByVertexFormat.values().iterator();

      while(var1.hasNext()) {
         MappableRingBuffer mappableRingBuffer = (MappableRingBuffer)var1.next();
         mappableRingBuffer.close();
      }

      this.oversizedItems.values().forEach(SpecialGuiElementRenderer::close);
   }

   static {
      SIMPLE_ELEMENT_COMPARATOR = Comparator.comparing(SimpleGuiElementRenderState::scissorArea, SCISSOR_AREA_COMPARATOR).thenComparing(SimpleGuiElementRenderState::pipeline, Comparator.comparing(RenderPipeline::getSortKey)).thenComparing(SimpleGuiElementRenderState::textureSetup, TEXTURE_SETUP_COMPARATOR);
   }

   @Environment(EnvType.CLIENT)
   static record Draw(GpuBuffer vertexBuffer, int baseVertex, VertexFormat.DrawMode mode, int indexCount, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRect scissorArea) {
      final GpuBuffer vertexBuffer;
      final int baseVertex;
      final int indexCount;
      final TextureSetup textureSetup;

      Draw(GpuBuffer gpuBuffer, int i, VertexFormat.DrawMode drawMode, int j, RenderPipeline renderPipeline, TextureSetup textureSetup, @Nullable ScreenRect screenRect) {
         this.vertexBuffer = gpuBuffer;
         this.baseVertex = i;
         this.mode = drawMode;
         this.indexCount = j;
         this.pipeline = renderPipeline;
         this.textureSetup = textureSetup;
         this.scissorArea = screenRect;
      }

      public GpuBuffer vertexBuffer() {
         return this.vertexBuffer;
      }

      public int baseVertex() {
         return this.baseVertex;
      }

      public VertexFormat.DrawMode mode() {
         return this.mode;
      }

      public int indexCount() {
         return this.indexCount;
      }

      public RenderPipeline pipeline() {
         return this.pipeline;
      }

      public TextureSetup textureSetup() {
         return this.textureSetup;
      }

      @Nullable
      public ScreenRect scissorArea() {
         return this.scissorArea;
      }
   }

   @Environment(EnvType.CLIENT)
   static record Preparation(BuiltBuffer mesh, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRect scissorArea) implements AutoCloseable {
      final BuiltBuffer mesh;
      final RenderPipeline pipeline;
      final TextureSetup textureSetup;
      @Nullable
      final ScreenRect scissorArea;

      Preparation(BuiltBuffer builtBuffer, RenderPipeline renderPipeline, TextureSetup textureSetup, @Nullable ScreenRect screenRect) {
         this.mesh = builtBuffer;
         this.pipeline = renderPipeline;
         this.textureSetup = textureSetup;
         this.scissorArea = screenRect;
      }

      public void close() {
         this.mesh.close();
      }

      public BuiltBuffer mesh() {
         return this.mesh;
      }

      public RenderPipeline pipeline() {
         return this.pipeline;
      }

      public TextureSetup textureSetup() {
         return this.textureSetup;
      }

      @Nullable
      public ScreenRect scissorArea() {
         return this.scissorArea;
      }
   }

   @Environment(EnvType.CLIENT)
   static final class RenderedItem {
      final int x;
      final int y;
      final float u;
      final float v;
      int frame;

      RenderedItem(int x, int y, float u, float v, int frame) {
         this.x = x;
         this.y = y;
         this.u = u;
         this.v = v;
         this.frame = frame;
      }
   }
}
