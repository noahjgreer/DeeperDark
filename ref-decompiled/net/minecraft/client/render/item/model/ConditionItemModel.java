package net.minecraft.client.render.item.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.property.PropertyTester;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.ContextSwapper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ConditionItemModel implements ItemModel {
   private final PropertyTester property;
   private final ItemModel onTrue;
   private final ItemModel onFalse;

   public ConditionItemModel(PropertyTester property, ItemModel onTrue, ItemModel onFalse) {
      this.property = property;
      this.onTrue = onTrue;
      this.onFalse = onFalse;
   }

   public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
      state.addModelKey(this);
      (this.property.test(stack, world, user, seed, displayContext) ? this.onTrue : this.onFalse).update(state, stack, resolver, displayContext, world, user, seed);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(BooleanProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) implements ItemModel.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(BooleanProperties.CODEC.forGetter(Unbaked::property), ItemModelTypes.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue), ItemModelTypes.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse)).apply(instance, Unbaked::new);
      });

      public Unbaked(BooleanProperty booleanProperty, ItemModel.Unbaked unbaked, ItemModel.Unbaked unbaked2) {
         this.property = booleanProperty;
         this.onTrue = unbaked;
         this.onFalse = unbaked2;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public ItemModel bake(ItemModel.BakeContext context) {
         return new ConditionItemModel(this.makeWorldIndependentProperty(this.property, context.contextSwapper()), this.onTrue.bake(context), this.onFalse.bake(context));
      }

      private PropertyTester makeWorldIndependentProperty(BooleanProperty property, @Nullable ContextSwapper contextSwapper) {
         if (contextSwapper == null) {
            return property;
         } else {
            DataCache dataCache = new DataCache((world) -> {
               return swapContext(property, contextSwapper, world);
            });
            return (stack, world, entity, seed, transformationMode) -> {
               PropertyTester propertyTester = world == null ? property : (PropertyTester)dataCache.compute(world);
               return ((PropertyTester)propertyTester).test(stack, world, entity, seed, transformationMode);
            };
         }
      }

      private static BooleanProperty swapContext(BooleanProperty value, ContextSwapper contextSwapper, ClientWorld world) {
         return (BooleanProperty)contextSwapper.swapContext(value.getCodec().codec(), value, world.getRegistryManager()).result().orElse(value);
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         this.onTrue.resolve(resolver);
         this.onFalse.resolve(resolver);
      }

      public BooleanProperty property() {
         return this.property;
      }

      public ItemModel.Unbaked onTrue() {
         return this.onTrue;
      }

      public ItemModel.Unbaked onFalse() {
         return this.onFalse;
      }
   }
}
