package net.minecraft.client.render.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.render.FabricLayerRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ItemRenderState {
   ItemDisplayContext displayContext;
   private int layerCount;
   private boolean animated;
   private boolean oversizedInGui;
   @Nullable
   private Box cachedModelBoundingBox;
   private LayerRenderState[] layers;

   public ItemRenderState() {
      this.displayContext = ItemDisplayContext.NONE;
      this.layers = new LayerRenderState[]{new LayerRenderState()};
   }

   public void addLayers(int add) {
      int i = this.layers.length;
      int j = this.layerCount + add;
      if (j > i) {
         this.layers = (LayerRenderState[])Arrays.copyOf(this.layers, j);

         for(int k = i; k < j; ++k) {
            this.layers[k] = new LayerRenderState();
         }
      }

   }

   public LayerRenderState newLayer() {
      this.addLayers(1);
      return this.layers[this.layerCount++];
   }

   public void clear() {
      this.displayContext = ItemDisplayContext.NONE;

      for(int i = 0; i < this.layerCount; ++i) {
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

   @Nullable
   public Sprite getParticleSprite(Random random) {
      return this.layerCount == 0 ? null : this.layers[random.nextInt(this.layerCount)].particle;
   }

   public void load(Consumer posConsumer) {
      Vector3f vector3f = new Vector3f();
      MatrixStack.Entry entry = new MatrixStack.Entry();

      for(int i = 0; i < this.layerCount; ++i) {
         LayerRenderState layerRenderState = this.layers[i];
         layerRenderState.transform.apply(this.displayContext.isLeftHand(), entry);
         Matrix4f matrix4f = entry.getPositionMatrix();
         Vector3f[] vector3fs = (Vector3f[])layerRenderState.vertices.get();
         Vector3f[] var8 = vector3fs;
         int var9 = vector3fs.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Vector3f vector3f2 = var8[var10];
            posConsumer.accept(vector3f.set(vector3f2).mulPosition(matrix4f));
         }

         entry.loadIdentity();
      }

   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
      for(int i = 0; i < this.layerCount; ++i) {
         this.layers[i].render(matrices, vertexConsumers, light, overlay);
      }

   }

   public Box getModelBoundingBox() {
      if (this.cachedModelBoundingBox != null) {
         return this.cachedModelBoundingBox;
      } else {
         Box.Builder builder = new Box.Builder();
         Objects.requireNonNull(builder);
         this.load(builder::encompass);
         Box box = builder.build();
         this.cachedModelBoundingBox = box;
         return box;
      }
   }

   public void setOversizedInGui(boolean oversizedInGui) {
      this.oversizedInGui = oversizedInGui;
   }

   public boolean isOversizedInGui() {
      return this.oversizedInGui;
   }

   @Environment(EnvType.CLIENT)
   public class LayerRenderState implements FabricLayerRenderState {
      private static final Vector3f[] EMPTY = new Vector3f[0];
      public static final Supplier DEFAULT = () -> {
         return EMPTY;
      };
      private final List quads = new ArrayList();
      boolean useLight;
      @Nullable
      Sprite particle;
      Transformation transform;
      @Nullable
      private RenderLayer renderLayer;
      private Glint glint;
      private int[] tints;
      @Nullable
      private SpecialModelRenderer specialModelType;
      @Nullable
      private Object data;
      Supplier vertices;

      public LayerRenderState() {
         this.transform = Transformation.IDENTITY;
         this.glint = ItemRenderState.Glint.NONE;
         this.tints = new int[0];
         this.vertices = DEFAULT;
      }

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

      public List getQuads() {
         return this.quads;
      }

      public void setRenderLayer(RenderLayer layer) {
         this.renderLayer = layer;
      }

      public void setUseLight(boolean useLight) {
         this.useLight = useLight;
      }

      public void setVertices(Supplier vertices) {
         this.vertices = vertices;
      }

      public void setParticle(Sprite particle) {
         this.particle = particle;
      }

      public void setTransform(Transformation transform) {
         this.transform = transform;
      }

      public void setSpecialModel(SpecialModelRenderer specialModelType, @Nullable Object data) {
         this.specialModelType = eraseType(specialModelType);
         this.data = data;
      }

      private static SpecialModelRenderer eraseType(SpecialModelRenderer specialModelType) {
         return specialModelType;
      }

      public void setGlint(Glint glint) {
         this.glint = glint;
      }

      public int[] initTints(int maxIndex) {
         if (maxIndex > this.tints.length) {
            this.tints = new int[maxIndex];
            Arrays.fill(this.tints, -1);
         }

         return this.tints;
      }

      void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
         matrices.push();
         this.transform.apply(ItemRenderState.this.displayContext.isLeftHand(), matrices.peek());
         if (this.specialModelType != null) {
            this.specialModelType.render(this.data, ItemRenderState.this.displayContext, matrices, vertexConsumers, light, overlay, this.glint != ItemRenderState.Glint.NONE);
         } else if (this.renderLayer != null) {
            ItemRenderer.renderItem(ItemRenderState.this.displayContext, matrices, vertexConsumers, light, overlay, this.tints, this.quads, this.renderLayer, this.glint);
         }

         matrices.pop();
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum Glint {
      NONE,
      STANDARD,
      SPECIAL;

      // $FF: synthetic method
      private static Glint[] method_65611() {
         return new Glint[]{NONE, STANDARD, SPECIAL};
      }
   }
}
