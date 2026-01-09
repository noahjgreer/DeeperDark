package net.minecraft.client.render.item.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CompositeItemModel implements ItemModel {
   private final List models;

   public CompositeItemModel(List models) {
      this.models = models;
   }

   public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
      state.addModelKey(this);
      state.addLayers(this.models.size());
      Iterator var8 = this.models.iterator();

      while(var8.hasNext()) {
         ItemModel itemModel = (ItemModel)var8.next();
         itemModel.update(state, stack, resolver, displayContext, world, user, seed);
      }

   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(List models) implements ItemModel.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(ItemModelTypes.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply(instance, Unbaked::new);
      });

      public Unbaked(List list) {
         this.models = list;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         Iterator var2 = this.models.iterator();

         while(var2.hasNext()) {
            ItemModel.Unbaked unbaked = (ItemModel.Unbaked)var2.next();
            unbaked.resolve(resolver);
         }

      }

      public ItemModel bake(ItemModel.BakeContext context) {
         return new CompositeItemModel(this.models.stream().map((model) -> {
            return model.bake(context);
         }).toList());
      }

      public List models() {
         return this.models;
      }
   }
}
