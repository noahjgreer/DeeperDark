package net.minecraft.client.render.item.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RangeDispatchItemModel implements ItemModel {
   private static final int field_55353 = 16;
   private final NumericProperty property;
   private final float scale;
   private final float[] thresholds;
   private final ItemModel[] models;
   private final ItemModel fallback;

   RangeDispatchItemModel(NumericProperty property, float scale, float[] thresholds, ItemModel[] models, ItemModel fallback) {
      this.property = property;
      this.thresholds = thresholds;
      this.models = models;
      this.fallback = fallback;
      this.scale = scale;
   }

   private static int getIndex(float[] thresholds, float value) {
      int i;
      if (thresholds.length < 16) {
         for(i = 0; i < thresholds.length; ++i) {
            if (thresholds[i] > value) {
               return i - 1;
            }
         }

         return thresholds.length - 1;
      } else {
         i = Arrays.binarySearch(thresholds, value);
         if (i < 0) {
            int j = ~i;
            return j - 1;
         } else {
            return i;
         }
      }
   }

   public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
      state.addModelKey(this);
      float f = this.property.getValue(stack, world, user, seed) * this.scale;
      ItemModel itemModel;
      if (Float.isNaN(f)) {
         itemModel = this.fallback;
      } else {
         int i = getIndex(this.thresholds, f);
         itemModel = i == -1 ? this.fallback : this.models[i];
      }

      itemModel.update(state, stack, resolver, displayContext, world, user, seed);
   }

   @Environment(EnvType.CLIENT)
   public static record Entry(float threshold, ItemModel.Unbaked model) {
      final float threshold;
      final ItemModel.Unbaked model;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.FLOAT.fieldOf("threshold").forGetter(Entry::threshold), ItemModelTypes.CODEC.fieldOf("model").forGetter(Entry::model)).apply(instance, Entry::new);
      });
      public static final Comparator COMPARATOR = Comparator.comparingDouble(Entry::threshold);

      public Entry(float f, ItemModel.Unbaked unbaked) {
         this.threshold = f;
         this.model = unbaked;
      }

      public float threshold() {
         return this.threshold;
      }

      public ItemModel.Unbaked model() {
         return this.model;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(NumericProperty property, float scale, List entries, Optional fallback) implements ItemModel.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(NumericProperties.CODEC.forGetter(Unbaked::property), Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Unbaked::scale), RangeDispatchItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(Unbaked::entries), ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply(instance, Unbaked::new);
      });

      public Unbaked(NumericProperty numericProperty, float f, List list, Optional optional) {
         this.property = numericProperty;
         this.scale = f;
         this.entries = list;
         this.fallback = optional;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public ItemModel bake(ItemModel.BakeContext context) {
         float[] fs = new float[this.entries.size()];
         ItemModel[] itemModels = new ItemModel[this.entries.size()];
         List list = new ArrayList(this.entries);
         list.sort(RangeDispatchItemModel.Entry.COMPARATOR);

         for(int i = 0; i < list.size(); ++i) {
            Entry entry = (Entry)list.get(i);
            fs[i] = entry.threshold;
            itemModels[i] = entry.model.bake(context);
         }

         ItemModel itemModel = (ItemModel)this.fallback.map((model) -> {
            return model.bake(context);
         }).orElse(context.missingItemModel());
         return new RangeDispatchItemModel(this.property, this.scale, fs, itemModels, itemModel);
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         this.fallback.ifPresent((model) -> {
            model.resolve(resolver);
         });
         this.entries.forEach((entry) -> {
            entry.model.resolve(resolver);
         });
      }

      public NumericProperty property() {
         return this.property;
      }

      public float scale() {
         return this.scale;
      }

      public List entries() {
         return this.entries;
      }

      public Optional fallback() {
         return this.fallback;
      }
   }
}
