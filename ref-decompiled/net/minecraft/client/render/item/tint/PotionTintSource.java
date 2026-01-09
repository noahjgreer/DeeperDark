package net.minecraft.client.render.item.tint;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record PotionTintSource(int defaultColor) implements TintSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.RGB.fieldOf("default").forGetter(PotionTintSource::defaultColor)).apply(instance, PotionTintSource::new);
   });

   public PotionTintSource() {
      this(-13083194);
   }

   public PotionTintSource(int i) {
      this.defaultColor = i;
   }

   public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
      PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.get(DataComponentTypes.POTION_CONTENTS);
      return potionContentsComponent != null ? ColorHelper.fullAlpha(potionContentsComponent.getColor(this.defaultColor)) : ColorHelper.fullAlpha(this.defaultColor);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public int defaultColor() {
      return this.defaultColor;
   }
}
