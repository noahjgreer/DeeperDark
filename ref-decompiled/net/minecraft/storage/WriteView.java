package net.minecraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.serialization.v1.view.FabricWriteView;
import org.jetbrains.annotations.Nullable;

public interface WriteView extends FabricWriteView {
   void put(String key, Codec codec, Object value);

   void putNullable(String key, Codec codec, @Nullable Object value);

   /** @deprecated */
   @Deprecated
   void put(MapCodec codec, Object value);

   void putBoolean(String key, boolean value);

   void putByte(String key, byte value);

   void putShort(String key, short value);

   void putInt(String key, int value);

   void putLong(String key, long value);

   void putFloat(String key, float value);

   void putDouble(String key, double value);

   void putString(String key, String value);

   void putIntArray(String key, int[] value);

   WriteView get(String key);

   ListView getList(String key);

   ListAppender getListAppender(String key, Codec codec);

   void remove(String key);

   boolean isEmpty();

   public interface ListAppender {
      void add(Object value);

      boolean isEmpty();
   }

   public interface ListView {
      WriteView add();

      void removeLast();

      boolean isEmpty();
   }
}
