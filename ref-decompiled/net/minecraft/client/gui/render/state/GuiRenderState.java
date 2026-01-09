package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GuiRenderState {
   private static final int field_60454 = 2000962815;
   private final List rootLayers = new ArrayList();
   private int blurLayer = Integer.MAX_VALUE;
   private Layer currentLayer;
   private final Set itemModelKeys = new HashSet();
   @Nullable
   private ScreenRect currentLayerBounds;

   public GuiRenderState() {
      this.createNewRootLayer();
   }

   public void createNewRootLayer() {
      this.currentLayer = new Layer((Layer)null);
      this.rootLayers.add(this.currentLayer);
   }

   public void applyBlur() {
      if (this.blurLayer != Integer.MAX_VALUE) {
         throw new IllegalStateException("Can only blur once per frame");
      } else {
         this.blurLayer = this.rootLayers.size() - 1;
      }
   }

   public void goUpLayer() {
      if (this.currentLayer.up == null) {
         this.currentLayer.up = new Layer(this.currentLayer);
      }

      this.currentLayer = this.currentLayer.up;
   }

   public void goDownLayer() {
      if (this.currentLayer.down == null) {
         this.currentLayer.down = new Layer(this.currentLayer);
      }

      this.currentLayer = this.currentLayer.down;
   }

   public void addItem(ItemGuiElementRenderState state) {
      if (this.findAndGoToLayerToAdd(state)) {
         this.itemModelKeys.add(state.state().getModelKey());
         this.currentLayer.addItem(state);
         this.onElementAdded(state.bounds());
      }
   }

   public void addText(TextGuiElementRenderState state) {
      if (this.findAndGoToLayerToAdd(state)) {
         this.currentLayer.addText(state);
         this.onElementAdded(state.bounds());
      }
   }

   public void addSpecialElement(SpecialGuiElementRenderState state) {
      if (this.findAndGoToLayerToAdd(state)) {
         this.currentLayer.addSpecialElement(state);
         this.onElementAdded(state.bounds());
      }
   }

   public void addSimpleElement(SimpleGuiElementRenderState state) {
      if (this.findAndGoToLayerToAdd(state)) {
         this.currentLayer.addSimpleElement(state);
         this.onElementAdded(state.bounds());
      }
   }

   private void onElementAdded(@Nullable ScreenRect bounds) {
   }

   private boolean findAndGoToLayerToAdd(GuiElementRenderState state) {
      ScreenRect screenRect = state.bounds();
      if (screenRect == null) {
         return false;
      } else {
         if (this.currentLayerBounds != null && this.currentLayerBounds.contains(screenRect)) {
            this.goUpLayer();
         } else {
            this.findAndGoToLayerIntersecting(screenRect);
         }

         this.currentLayerBounds = screenRect;
         return true;
      }
   }

   private void findAndGoToLayerIntersecting(ScreenRect bounds) {
      Layer layer;
      for(layer = (Layer)this.rootLayers.getLast(); layer.up != null; layer = layer.up) {
      }

      boolean bl = false;

      while(!bl) {
         bl = this.anyIntersect(bounds, layer.simpleElementRenderStates) || this.anyIntersect(bounds, layer.itemElementRenderStates) || this.anyIntersect(bounds, layer.textElementRenderStates) || this.anyIntersect(bounds, layer.specialElementRenderStates);
         if (layer.parent == null) {
            break;
         }

         if (!bl) {
            layer = layer.parent;
         }
      }

      this.currentLayer = layer;
      if (bl) {
         this.goUpLayer();
      }

   }

   private boolean anyIntersect(ScreenRect bounds, @Nullable List elementRenderStates) {
      if (elementRenderStates != null) {
         Iterator var3 = elementRenderStates.iterator();

         while(var3.hasNext()) {
            GuiElementRenderState guiElementRenderState = (GuiElementRenderState)var3.next();
            ScreenRect screenRect = guiElementRenderState.bounds();
            if (screenRect != null && screenRect.intersects(bounds)) {
               return true;
            }
         }
      }

      return false;
   }

   public void addSimpleElementToCurrentLayer(TexturedQuadGuiElementRenderState state) {
      this.currentLayer.addSimpleElement(state);
   }

   public void addPreparedTextElement(SimpleGuiElementRenderState state) {
      this.currentLayer.addPreparedText(state);
   }

   public Set getItemModelKeys() {
      return this.itemModelKeys;
   }

   public void forEachSimpleElement(SimpleElementStateConsumer simpleElementStateConsumer, LayerFilter filter) {
      MutableInt mutableInt = new MutableInt(0);
      this.forEachLayer((layer) -> {
         if (layer.simpleElementRenderStates != null || layer.preparedTextElementRenderStates != null) {
            int i = mutableInt.incrementAndGet();
            Iterator var4;
            SimpleGuiElementRenderState simpleGuiElementRenderState;
            if (layer.simpleElementRenderStates != null) {
               var4 = layer.simpleElementRenderStates.iterator();

               while(var4.hasNext()) {
                  simpleGuiElementRenderState = (SimpleGuiElementRenderState)var4.next();
                  simpleElementStateConsumer.accept(simpleGuiElementRenderState, i);
               }
            }

            if (layer.preparedTextElementRenderStates != null) {
               var4 = layer.preparedTextElementRenderStates.iterator();

               while(var4.hasNext()) {
                  simpleGuiElementRenderState = (SimpleGuiElementRenderState)var4.next();
                  simpleElementStateConsumer.accept(simpleGuiElementRenderState, i);
               }
            }

         }
      }, filter);
   }

   public void forEachItemElement(Consumer itemElementStateConsumer) {
      Layer layer = this.currentLayer;
      this.forEachLayer((layerx) -> {
         if (layerx.itemElementRenderStates != null) {
            this.currentLayer = layerx;
            Iterator var3 = layerx.itemElementRenderStates.iterator();

            while(var3.hasNext()) {
               ItemGuiElementRenderState itemGuiElementRenderState = (ItemGuiElementRenderState)var3.next();
               itemElementStateConsumer.accept(itemGuiElementRenderState);
            }
         }

      }, GuiRenderState.LayerFilter.ALL);
      this.currentLayer = layer;
   }

   public void forEachTextElement(Consumer textElementStateConsumer) {
      Layer layer = this.currentLayer;
      this.forEachLayer((layerx) -> {
         if (layerx.textElementRenderStates != null) {
            Iterator var3 = layerx.textElementRenderStates.iterator();

            while(var3.hasNext()) {
               TextGuiElementRenderState textGuiElementRenderState = (TextGuiElementRenderState)var3.next();
               this.currentLayer = layerx;
               textElementStateConsumer.accept(textGuiElementRenderState);
            }
         }

      }, GuiRenderState.LayerFilter.ALL);
      this.currentLayer = layer;
   }

   public void forEachSpecialElement(Consumer specialElementStateConsumer) {
      Layer layer = this.currentLayer;
      this.forEachLayer((layerx) -> {
         if (layerx.specialElementRenderStates != null) {
            this.currentLayer = layerx;
            Iterator var3 = layerx.specialElementRenderStates.iterator();

            while(var3.hasNext()) {
               SpecialGuiElementRenderState specialGuiElementRenderState = (SpecialGuiElementRenderState)var3.next();
               specialElementStateConsumer.accept(specialGuiElementRenderState);
            }
         }

      }, GuiRenderState.LayerFilter.ALL);
      this.currentLayer = layer;
   }

   public void sortSimpleElements(Comparator simpleElementStateComparator) {
      this.forEachLayer((layer) -> {
         if (layer.simpleElementRenderStates != null) {
            layer.simpleElementRenderStates.sort(simpleElementStateComparator);
         }

      }, GuiRenderState.LayerFilter.ALL);
   }

   private void forEachLayer(Consumer layerConsumer, LayerFilter filter) {
      int i = 0;
      int j = this.rootLayers.size();
      if (filter == GuiRenderState.LayerFilter.BEFORE_BLUR) {
         j = Math.min(this.blurLayer, this.rootLayers.size());
      } else if (filter == GuiRenderState.LayerFilter.AFTER_BLUR) {
         i = this.blurLayer;
      }

      for(int k = i; k < j; ++k) {
         Layer layer = (Layer)this.rootLayers.get(k);
         this.traverseLayers(layer, layerConsumer);
      }

   }

   private void traverseLayers(Layer layer, Consumer layerConsumer) {
      if (layer.down != null) {
         this.traverseLayers(layer.down, layerConsumer);
      }

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

   @Environment(EnvType.CLIENT)
   static class Layer {
      @Nullable
      public final Layer parent;
      @Nullable
      public Layer up;
      @Nullable
      public Layer down;
      @Nullable
      public List simpleElementRenderStates;
      @Nullable
      public List preparedTextElementRenderStates;
      @Nullable
      public List itemElementRenderStates;
      @Nullable
      public List textElementRenderStates;
      @Nullable
      public List specialElementRenderStates;

      Layer(@Nullable Layer parent) {
         this.parent = parent;
      }

      public void addItem(ItemGuiElementRenderState state) {
         if (this.itemElementRenderStates == null) {
            this.itemElementRenderStates = new ArrayList();
         }

         this.itemElementRenderStates.add(state);
      }

      public void addText(TextGuiElementRenderState state) {
         if (this.textElementRenderStates == null) {
            this.textElementRenderStates = new ArrayList();
         }

         this.textElementRenderStates.add(state);
      }

      public void addSpecialElement(SpecialGuiElementRenderState state) {
         if (this.specialElementRenderStates == null) {
            this.specialElementRenderStates = new ArrayList();
         }

         this.specialElementRenderStates.add(state);
      }

      public void addSimpleElement(SimpleGuiElementRenderState state) {
         if (this.simpleElementRenderStates == null) {
            this.simpleElementRenderStates = new ArrayList();
         }

         this.simpleElementRenderStates.add(state);
      }

      public void addPreparedText(SimpleGuiElementRenderState state) {
         if (this.preparedTextElementRenderStates == null) {
            this.preparedTextElementRenderStates = new ArrayList();
         }

         this.preparedTextElementRenderStates.add(state);
      }
   }

   @Environment(EnvType.CLIENT)
   public interface SimpleElementStateConsumer {
      void accept(SimpleGuiElementRenderState simpleElementState, int depth);
   }

   @Environment(EnvType.CLIENT)
   public static enum LayerFilter {
      ALL,
      BEFORE_BLUR,
      AFTER_BLUR;

      // $FF: synthetic method
      private static LayerFilter[] method_71300() {
         return new LayerFilter[]{ALL, BEFORE_BLUR, AFTER_BLUR};
      }
   }
}
