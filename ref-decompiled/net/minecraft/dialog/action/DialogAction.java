package net.minecraft.dialog.action;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;

public interface DialogAction {
   Codec CODEC = Registries.DIALOG_ACTION_TYPE.getCodec().dispatch(DialogAction::getCodec, (codec) -> {
      return codec;
   });

   MapCodec getCodec();

   Optional createClickEvent(Map valueGetters);

   public interface ValueGetter {
      String get();

      NbtElement getAsNbt();

      static Map resolveAll(Map valueGetters) {
         return Maps.transformValues(valueGetters, ValueGetter::get);
      }

      static ValueGetter of(final String value) {
         return new ValueGetter() {
            public String get() {
               return value;
            }

            public NbtElement getAsNbt() {
               return NbtString.of(value);
            }
         };
      }

      static ValueGetter of(final Supplier valueSupplier) {
         return new ValueGetter() {
            public String get() {
               return (String)valueSupplier.get();
            }

            public NbtElement getAsNbt() {
               return NbtString.of((String)valueSupplier.get());
            }
         };
      }
   }
}
