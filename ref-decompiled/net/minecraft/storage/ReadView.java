package net.minecraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.serialization.v1.view.FabricReadView;
import net.minecraft.registry.RegistryWrapper;

public interface ReadView extends FabricReadView {
   Optional read(String key, Codec codec);

   /** @deprecated */
   @Deprecated
   Optional read(MapCodec mapCodec);

   Optional getOptionalReadView(String key);

   ReadView getReadView(String key);

   Optional getOptionalListReadView(String key);

   ListReadView getListReadView(String key);

   Optional getOptionalTypedListView(String key, Codec typeCodec);

   TypedListReadView getTypedListView(String key, Codec typeCodec);

   boolean getBoolean(String key, boolean fallback);

   byte getByte(String key, byte fallback);

   int getShort(String key, short fallback);

   Optional getOptionalInt(String key);

   int getInt(String key, int fallback);

   long getLong(String key, long fallback);

   Optional getOptionalLong(String key);

   float getFloat(String key, float fallback);

   double getDouble(String key, double fallback);

   Optional getOptionalString(String key);

   String getString(String key, String fallback);

   Optional getOptionalIntArray(String key);

   /** @deprecated */
   @Deprecated
   RegistryWrapper.WrapperLookup getRegistries();

   public interface TypedListReadView extends Iterable {
      boolean isEmpty();

      Stream stream();
   }

   public interface ListReadView extends Iterable {
      boolean isEmpty();

      Stream stream();
   }
}
