package net.minecraft.client.render.item.model;

import com.mojang.serialization.MapCodec;
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
public class EmptyItemModel implements ItemModel {
   public static final ItemModel INSTANCE = new EmptyItemModel();

   public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
      state.addModelKey(this);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked() implements ItemModel.Unbaked {
      public static final MapCodec CODEC = MapCodec.unit(Unbaked::new);

      public void resolve(ResolvableModel.Resolver resolver) {
      }

      public ItemModel bake(ItemModel.BakeContext context) {
         return EmptyItemModel.INSTANCE;
      }

      public MapCodec getCodec() {
         return CODEC;
      }
   }
}
