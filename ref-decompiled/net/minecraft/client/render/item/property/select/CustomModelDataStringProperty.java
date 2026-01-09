package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
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
public record CustomModelDataStringProperty(int index) implements SelectProperty {
   public static final PrimitiveCodec VALUE_CODEC;
   public static final SelectProperty.Type TYPE;

   public CustomModelDataStringProperty(int i) {
      this.index = i;
   }

   @Nullable
   public String getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
      CustomModelDataComponent customModelDataComponent = (CustomModelDataComponent)itemStack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
      return customModelDataComponent != null ? customModelDataComponent.getString(this.index) : null;
   }

   public SelectProperty.Type getType() {
      return TYPE;
   }

   public Codec valueCodec() {
      return VALUE_CODEC;
   }

   public int index() {
      return this.index;
   }

   // $FF: synthetic method
   @Nullable
   public Object getValue(final ItemStack stack, @Nullable final ClientWorld world, @Nullable final LivingEntity user, final int seed, final ItemDisplayContext displayContext) {
      return this.getValue(stack, world, user, seed, displayContext);
   }

   static {
      VALUE_CODEC = Codec.STRING;
      TYPE = SelectProperty.Type.create(RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataStringProperty::index)).apply(instance, CustomModelDataStringProperty::new);
      }), VALUE_CODEC);
   }
}
