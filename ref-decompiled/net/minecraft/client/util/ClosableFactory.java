package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ClosableFactory {
   Object create();

   default void prepare(Object value) {
   }

   void close(Object value);

   default boolean equals(ClosableFactory factory) {
      return this.equals(factory);
   }
}
