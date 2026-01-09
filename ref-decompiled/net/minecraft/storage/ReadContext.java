package net.minecraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryWrapper;

public class ReadContext {
   final RegistryWrapper.WrapperLookup registries;
   private final DynamicOps ops;
   final ReadView.ListReadView emptyListReadView = new ReadView.ListReadView(this) {
      public boolean isEmpty() {
         return true;
      }

      public Stream stream() {
         return Stream.empty();
      }

      public Iterator iterator() {
         return Collections.emptyIterator();
      }
   };
   private final ReadView.TypedListReadView emptyTypedListReadView = new ReadView.TypedListReadView(this) {
      public boolean isEmpty() {
         return true;
      }

      public Stream stream() {
         return Stream.empty();
      }

      public Iterator iterator() {
         return Collections.emptyIterator();
      }
   };
   private final ReadView emptyReadView = new ReadView() {
      public Optional read(String key, Codec codec) {
         return Optional.empty();
      }

      public Optional read(MapCodec mapCodec) {
         return Optional.empty();
      }

      public Optional getOptionalReadView(String key) {
         return Optional.empty();
      }

      public ReadView getReadView(String key) {
         return this;
      }

      public Optional getOptionalListReadView(String key) {
         return Optional.empty();
      }

      public ReadView.ListReadView getListReadView(String key) {
         return ReadContext.this.emptyListReadView;
      }

      public Optional getOptionalTypedListView(String key, Codec typeCodec) {
         return Optional.empty();
      }

      public ReadView.TypedListReadView getTypedListView(String key, Codec typeCodec) {
         return ReadContext.this.getEmptyTypedListReadView();
      }

      public boolean getBoolean(String key, boolean fallback) {
         return fallback;
      }

      public byte getByte(String key, byte fallback) {
         return fallback;
      }

      public int getShort(String key, short fallback) {
         return fallback;
      }

      public Optional getOptionalInt(String key) {
         return Optional.empty();
      }

      public int getInt(String key, int fallback) {
         return fallback;
      }

      public long getLong(String key, long fallback) {
         return fallback;
      }

      public Optional getOptionalLong(String key) {
         return Optional.empty();
      }

      public float getFloat(String key, float fallback) {
         return fallback;
      }

      public double getDouble(String key, double fallback) {
         return fallback;
      }

      public Optional getOptionalString(String key) {
         return Optional.empty();
      }

      public String getString(String key, String fallback) {
         return fallback;
      }

      public RegistryWrapper.WrapperLookup getRegistries() {
         return ReadContext.this.registries;
      }

      public Optional getOptionalIntArray(String key) {
         return Optional.empty();
      }
   };

   public ReadContext(RegistryWrapper.WrapperLookup registries, DynamicOps ops) {
      this.registries = registries;
      this.ops = registries.getOps(ops);
   }

   public DynamicOps getOps() {
      return this.ops;
   }

   public RegistryWrapper.WrapperLookup getRegistries() {
      return this.registries;
   }

   public ReadView getEmptyReadView() {
      return this.emptyReadView;
   }

   public ReadView.ListReadView getEmptyListReadView() {
      return this.emptyListReadView;
   }

   public ReadView.TypedListReadView getEmptyTypedListReadView() {
      return this.emptyTypedListReadView;
   }
}
