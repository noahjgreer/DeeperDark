package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record UsingItemProperty() implements BooleanProperty {
   public static final MapCodec CODEC = MapCodec.unit(new UsingItemProperty());

   public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
      if (entity == null) {
         return false;
      } else {
         return entity.isUsingItem() && entity.getActiveItem() == stack;
      }
   }

   public MapCodec getCodec() {
      return CODEC;
   }
}
