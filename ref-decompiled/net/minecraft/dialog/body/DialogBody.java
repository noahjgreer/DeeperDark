package net.minecraft.dialog.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;

public interface DialogBody {
   Codec CODEC = Registries.DIALOG_BODY_TYPE.getCodec().dispatch(DialogBody::getTypeCodec, (mapCodec) -> {
      return mapCodec;
   });
   Codec LIST_CODEC = Codecs.listOrSingle(CODEC);

   MapCodec getTypeCodec();
}
