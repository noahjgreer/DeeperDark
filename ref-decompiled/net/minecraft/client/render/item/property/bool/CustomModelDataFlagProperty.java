package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record CustomModelDataFlagProperty(int index) implements BooleanProperty {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataFlagProperty::index)).apply(instance, CustomModelDataFlagProperty::new);
   });

   public CustomModelDataFlagProperty(int i) {
      this.index = i;
   }

   public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
      CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
      if (customModelDataComponent != null) {
         return customModelDataComponent.getFlag(this.index) == Boolean.TRUE;
      } else {
         return false;
      }
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public int index() {
      return this.index;
   }
}
