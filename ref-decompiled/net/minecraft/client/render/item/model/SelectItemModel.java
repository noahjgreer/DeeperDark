package net.minecraft.client.render.item.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.ContextSwapper;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SelectItemModel implements ItemModel {
   private final SelectProperty property;
   private final ModelSelector selector;

   public SelectItemModel(SelectProperty property, ModelSelector selector) {
      this.property = property;
      this.selector = selector;
   }

   public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
      state.addModelKey(this);
      Object object = this.property.getValue(stack, world, user, seed, displayContext);
      ItemModel itemModel = this.selector.get(object, world);
      if (itemModel != null) {
         itemModel.update(state, stack, resolver, displayContext, world, user, seed);
      }

   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface ModelSelector {
      @Nullable
      ItemModel get(@Nullable Object propertyValue, @Nullable ClientWorld world);
   }

   @Environment(EnvType.CLIENT)
   public static record SwitchCase(List values, ItemModel.Unbaked model) {
      final List values;
      final ItemModel.Unbaked model;

      public SwitchCase(List list, ItemModel.Unbaked unbaked) {
         this.values = list;
         this.model = unbaked;
      }

      public static Codec createCodec(Codec conditionCodec) {
         return RecordCodecBuilder.create((instance) -> {
            return instance.group(Codecs.nonEmptyList(Codecs.listOrSingle(conditionCodec)).fieldOf("when").forGetter(SwitchCase::values), ItemModelTypes.CODEC.fieldOf("model").forGetter(SwitchCase::model)).apply(instance, SwitchCase::new);
         });
      }

      public List values() {
         return this.values;
      }

      public ItemModel.Unbaked model() {
         return this.model;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record UnbakedSwitch(SelectProperty property, List cases) {
      public static final MapCodec CODEC;

      public UnbakedSwitch(SelectProperty selectProperty, List list) {
         this.property = selectProperty;
         this.cases = list;
      }

      public ItemModel bake(ItemModel.BakeContext context, ItemModel fallback) {
         Object2ObjectMap object2ObjectMap = new Object2ObjectOpenHashMap();
         Iterator var4 = this.cases.iterator();

         while(var4.hasNext()) {
            SwitchCase switchCase = (SwitchCase)var4.next();
            ItemModel.Unbaked unbaked = switchCase.model;
            ItemModel itemModel = unbaked.bake(context);
            Iterator var8 = switchCase.values.iterator();

            while(var8.hasNext()) {
               Object object = var8.next();
               object2ObjectMap.put(object, itemModel);
            }
         }

         object2ObjectMap.defaultReturnValue(fallback);
         return new SelectItemModel(this.property, this.buildModelSelector(object2ObjectMap, context.contextSwapper()));
      }

      private ModelSelector buildModelSelector(Object2ObjectMap models, @Nullable ContextSwapper contextSwapper) {
         if (contextSwapper == null) {
            return (value, world) -> {
               return (ItemModel)models.get(value);
            };
         } else {
            ItemModel itemModel = (ItemModel)models.defaultReturnValue();
            DataCache dataCache = new DataCache((world) -> {
               Object2ObjectMap object2ObjectMap2 = new Object2ObjectOpenHashMap(models.size());
               object2ObjectMap2.defaultReturnValue(itemModel);
               models.forEach((value, worldx) -> {
                  contextSwapper.swapContext(this.property.valueCodec(), value, world.getRegistryManager()).ifSuccess((swappedValue) -> {
                     object2ObjectMap2.put(swappedValue, worldx);
                  });
               });
               return object2ObjectMap2;
            });
            return (value, world) -> {
               if (world == null) {
                  return (ItemModel)models.get(value);
               } else {
                  return value == null ? itemModel : (ItemModel)((Object2ObjectMap)dataCache.compute(world)).get(value);
               }
            };
         }
      }

      public void resolveCases(ResolvableModel.Resolver resolver) {
         Iterator var2 = this.cases.iterator();

         while(var2.hasNext()) {
            SwitchCase switchCase = (SwitchCase)var2.next();
            switchCase.model.resolve(resolver);
         }

      }

      public SelectProperty property() {
         return this.property;
      }

      public List cases() {
         return this.cases;
      }

      static {
         CODEC = SelectProperties.CODEC.dispatchMap("property", (unbakedSwitch) -> {
            return unbakedSwitch.property().getType();
         }, SelectProperty.Type::switchCodec);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(UnbakedSwitch unbakedSwitch, Optional fallback) implements ItemModel.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(SelectItemModel.UnbakedSwitch.CODEC.forGetter(Unbaked::unbakedSwitch), ItemModelTypes.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply(instance, Unbaked::new);
      });

      public Unbaked(UnbakedSwitch unbakedSwitch, Optional optional) {
         this.unbakedSwitch = unbakedSwitch;
         this.fallback = optional;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public ItemModel bake(ItemModel.BakeContext context) {
         ItemModel itemModel = (ItemModel)this.fallback.map((model) -> {
            return model.bake(context);
         }).orElse(context.missingItemModel());
         return this.unbakedSwitch.bake(context, itemModel);
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         this.unbakedSwitch.resolveCases(resolver);
         this.fallback.ifPresent((model) -> {
            model.resolve(resolver);
         });
      }

      public UnbakedSwitch unbakedSwitch() {
         return this.unbakedSwitch;
      }

      public Optional fallback() {
         return this.fallback;
      }
   }
}
