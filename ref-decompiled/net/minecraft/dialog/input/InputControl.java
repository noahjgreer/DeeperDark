package net.minecraft.dialog.input;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;

public interface InputControl {
   MapCodec CODEC = Registries.INPUT_CONTROL_TYPE.getCodec().dispatchMap(InputControl::getCodec, (mapCodec) -> {
      return mapCodec;
   });

   MapCodec getCodec();
}
