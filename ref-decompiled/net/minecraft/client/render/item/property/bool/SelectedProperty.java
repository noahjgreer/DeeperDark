package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record SelectedProperty() implements BooleanProperty {
   public static final MapCodec CODEC = MapCodec.unit(new SelectedProperty());

   public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
      boolean var10000;
      if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
         if (clientPlayerEntity.getInventory().getSelectedStack() == stack) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public MapCodec getCodec() {
      return CODEC;
   }
}
