package net.minecraft.registry;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DefaultedRegistry extends Registry {
   @NotNull
   Identifier getId(Object value);

   @NotNull
   Object get(@Nullable Identifier id);

   @NotNull
   Object get(int index);

   Identifier getDefaultId();
}
