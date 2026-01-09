package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record KeybindDownProperty(KeyBinding keybind) implements BooleanProperty {
   private static final Codec KEY_BINDING_CODEC;
   public static final MapCodec CODEC;

   public KeybindDownProperty(KeyBinding keyBinding) {
      this.keybind = keyBinding;
   }

   public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
      return this.keybind.isPressed();
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public KeyBinding keybind() {
      return this.keybind;
   }

   static {
      KEY_BINDING_CODEC = Codec.STRING.comapFlatMap((id) -> {
         KeyBinding keyBinding = KeyBinding.byId(id);
         return keyBinding != null ? DataResult.success(keyBinding) : DataResult.error(() -> {
            return "Invalid keybind: " + id;
         });
      }, KeyBinding::getTranslationKey);
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(KEY_BINDING_CODEC.fieldOf("keybind").forGetter(KeybindDownProperty::keybind)).apply(instance, KeybindDownProperty::new);
      });
   }
}
