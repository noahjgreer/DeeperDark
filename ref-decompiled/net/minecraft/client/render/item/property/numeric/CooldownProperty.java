package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record CooldownProperty() implements NumericProperty {
   public static final MapCodec CODEC = MapCodec.unit(new CooldownProperty());

   public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
      float var10000;
      if (holder instanceof PlayerEntity playerEntity) {
         var10000 = playerEntity.getItemCooldownManager().getCooldownProgress(stack, 0.0F);
      } else {
         var10000 = 0.0F;
      }

      return var10000;
   }

   public MapCodec getCodec() {
      return CODEC;
   }
}
