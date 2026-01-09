package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ContextDimensionProperty() implements SelectProperty {
   public static final Codec VALUE_CODEC;
   public static final SelectProperty.Type TYPE;

   @Nullable
   public RegistryKey getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
      return clientWorld != null ? clientWorld.getRegistryKey() : null;
   }

   public SelectProperty.Type getType() {
      return TYPE;
   }

   public Codec valueCodec() {
      return VALUE_CODEC;
   }

   // $FF: synthetic method
   @Nullable
   public Object getValue(final ItemStack stack, @Nullable final ClientWorld world, @Nullable final LivingEntity user, final int seed, final ItemDisplayContext displayContext) {
      return this.getValue(stack, world, user, seed, displayContext);
   }

   static {
      VALUE_CODEC = RegistryKey.createCodec(RegistryKeys.WORLD);
      TYPE = SelectProperty.Type.create(MapCodec.unit(new ContextDimensionProperty()), VALUE_CODEC);
   }
}
