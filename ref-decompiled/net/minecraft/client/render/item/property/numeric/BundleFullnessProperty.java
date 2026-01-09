package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BundleFullnessProperty() implements NumericProperty {
   public static final MapCodec CODEC = MapCodec.unit(new BundleFullnessProperty());

   public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
      return BundleItem.getAmountFilled(stack);
   }

   public MapCodec getCodec() {
      return CODEC;
   }
}
