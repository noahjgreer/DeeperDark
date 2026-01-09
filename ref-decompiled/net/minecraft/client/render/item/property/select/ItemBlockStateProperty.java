package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ItemBlockStateProperty(String property) implements SelectProperty {
   public static final PrimitiveCodec VALUE_CODEC;
   public static final SelectProperty.Type TYPE;

   public ItemBlockStateProperty(String string) {
      this.property = string;
   }

   @Nullable
   public String getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
      BlockStateComponent blockStateComponent = (BlockStateComponent)itemStack.get(DataComponentTypes.BLOCK_STATE);
      return blockStateComponent == null ? null : (String)blockStateComponent.properties().get(this.property);
   }

   public SelectProperty.Type getType() {
      return TYPE;
   }

   public Codec valueCodec() {
      return VALUE_CODEC;
   }

   public String property() {
      return this.property;
   }

   // $FF: synthetic method
   @Nullable
   public Object getValue(final ItemStack stack, @Nullable final ClientWorld world, @Nullable final LivingEntity user, final int seed, final ItemDisplayContext displayContext) {
      return this.getValue(stack, world, user, seed, displayContext);
   }

   static {
      VALUE_CODEC = Codec.STRING;
      TYPE = SelectProperty.Type.create(RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.STRING.fieldOf("block_state_property").forGetter(ItemBlockStateProperty::property)).apply(instance, ItemBlockStateProperty::new);
      }), VALUE_CODEC);
   }
}
