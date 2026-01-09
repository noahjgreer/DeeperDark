package net.minecraft.entity.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.registry.Registries;

public interface SpawnCondition extends VariantSelectorProvider.SelectorCondition {
   Codec CODEC = Registries.SPAWN_CONDITION_TYPE.getCodec().dispatch(SpawnCondition::getCodec, (codec) -> {
      return codec;
   });

   MapCodec getCodec();
}
