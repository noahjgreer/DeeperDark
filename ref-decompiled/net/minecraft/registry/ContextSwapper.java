package net.minecraft.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public interface ContextSwapper {
   DataResult swapContext(Codec codec, Object value, RegistryWrapper.WrapperLookup registries);
}
