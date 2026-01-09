package net.minecraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ErrorReporter;
import org.jetbrains.annotations.Nullable;

public class NbtWriteView implements WriteView {
   private final ErrorReporter reporter;
   private final DynamicOps ops;
   private final NbtCompound nbt;

   NbtWriteView(ErrorReporter reporter, DynamicOps ops, NbtCompound nbt) {
      this.reporter = reporter;
      this.ops = ops;
      this.nbt = nbt;
   }

   public static NbtWriteView create(ErrorReporter reporter, RegistryWrapper.WrapperLookup registries) {
      return new NbtWriteView(reporter, registries.getOps(NbtOps.INSTANCE), new NbtCompound());
   }

   public static NbtWriteView create(ErrorReporter reporter) {
      return new NbtWriteView(reporter, NbtOps.INSTANCE, new NbtCompound());
   }

   public void put(String key, Codec codec, Object value) {
      DataResult var10000 = codec.encodeStart(this.ops, value);
      Objects.requireNonNull(var10000);
      DataResult var4 = var10000;
      byte var5 = 0;
      switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
         case 0:
            DataResult.Success success = (DataResult.Success)var4;
            this.nbt.put(key, (NbtElement)success.value());
            break;
         case 1:
            DataResult.Error error = (DataResult.Error)var4;
            this.reporter.report(new EncodeFieldError(key, value, error));
            error.partialValue().ifPresent((partialValue) -> {
               this.nbt.put(key, partialValue);
            });
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   public void putNullable(String key, Codec codec, @Nullable Object value) {
      if (value != null) {
         this.put(key, codec, value);
      }

   }

   public void put(MapCodec codec, Object value) {
      DataResult var10000 = codec.encoder().encodeStart(this.ops, value);
      Objects.requireNonNull(var10000);
      DataResult var3 = var10000;
      byte var4 = 0;
      switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
         case 0:
            DataResult.Success success = (DataResult.Success)var3;
            this.nbt.copyFrom((NbtCompound)success.value());
            break;
         case 1:
            DataResult.Error error = (DataResult.Error)var3;
            this.reporter.report(new MergeError(value, error));
            error.partialValue().ifPresent((partialValue) -> {
               this.nbt.copyFrom((NbtCompound)partialValue);
            });
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   public void putBoolean(String key, boolean value) {
      this.nbt.putBoolean(key, value);
   }

   public void putByte(String key, byte value) {
      this.nbt.putByte(key, value);
   }

   public void putShort(String key, short value) {
      this.nbt.putShort(key, value);
   }

   public void putInt(String key, int value) {
      this.nbt.putInt(key, value);
   }

   public void putLong(String key, long value) {
      this.nbt.putLong(key, value);
   }

   public void putFloat(String key, float value) {
      this.nbt.putFloat(key, value);
   }

   public void putDouble(String key, double value) {
      this.nbt.putDouble(key, value);
   }

   public void putString(String key, String value) {
      this.nbt.putString(key, value);
   }

   public void putIntArray(String key, int[] value) {
      this.nbt.putIntArray(key, value);
   }

   private ErrorReporter makeChildReporter(String key) {
      return this.reporter.makeChild(new ErrorReporter.MapElementContext(key));
   }

   public WriteView get(String key) {
      NbtCompound nbtCompound = new NbtCompound();
      this.nbt.put(key, nbtCompound);
      return new NbtWriteView(this.makeChildReporter(key), this.ops, nbtCompound);
   }

   public WriteView.ListView getList(String key) {
      NbtList nbtList = new NbtList();
      this.nbt.put(key, nbtList);
      return new NbtListView(key, this.reporter, this.ops, nbtList);
   }

   public WriteView.ListAppender getListAppender(String key, Codec codec) {
      NbtList nbtList = new NbtList();
      this.nbt.put(key, nbtList);
      return new NbtListAppender(this.reporter, key, this.ops, codec, nbtList);
   }

   public void remove(String key) {
      this.nbt.remove(key);
   }

   public boolean isEmpty() {
      return this.nbt.isEmpty();
   }

   public NbtCompound getNbt() {
      return this.nbt;
   }

   public static record EncodeFieldError(String name, Object value, DataResult.Error error) implements ErrorReporter.Error {
      public EncodeFieldError(String string, Object object, DataResult.Error error) {
         this.name = string;
         this.value = object;
         this.error = error;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.value);
         return "Failed to encode value '" + var10000 + "' to field '" + this.name + "': " + this.error.message();
      }

      public String name() {
         return this.name;
      }

      public Object value() {
         return this.value;
      }

      public DataResult.Error error() {
         return this.error;
      }
   }

   public static record MergeError(Object value, DataResult.Error error) implements ErrorReporter.Error {
      public MergeError(Object object, DataResult.Error error) {
         this.value = object;
         this.error = error;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.value);
         return "Failed to merge value '" + var10000 + "' to an object: " + this.error.message();
      }

      public Object value() {
         return this.value;
      }

      public DataResult.Error error() {
         return this.error;
      }
   }

   static class NbtListView implements WriteView.ListView {
      private final String key;
      private final ErrorReporter reporter;
      private final DynamicOps ops;
      private final NbtList list;

      NbtListView(String key, ErrorReporter reporter, DynamicOps ops, NbtList list) {
         this.key = key;
         this.reporter = reporter;
         this.ops = ops;
         this.list = list;
      }

      public WriteView add() {
         int i = this.list.size();
         NbtCompound nbtCompound = new NbtCompound();
         this.list.add(nbtCompound);
         return new NbtWriteView(this.reporter.makeChild(new ErrorReporter.NamedListElementContext(this.key, i)), this.ops, nbtCompound);
      }

      public void removeLast() {
         this.list.removeLast();
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }
   }

   static class NbtListAppender implements WriteView.ListAppender {
      private final ErrorReporter reporter;
      private final String key;
      private final DynamicOps ops;
      private final Codec codec;
      private final NbtList list;

      NbtListAppender(ErrorReporter reporter, String key, DynamicOps ops, Codec codec, NbtList list) {
         this.reporter = reporter;
         this.key = key;
         this.ops = ops;
         this.codec = codec;
         this.list = list;
      }

      public void add(Object value) {
         DataResult var10000 = this.codec.encodeStart(this.ops, value);
         Objects.requireNonNull(var10000);
         DataResult var2 = var10000;
         byte var3 = 0;
         switch (var2.typeSwitch<invokedynamic>(var2, var3)) {
            case 0:
               DataResult.Success success = (DataResult.Success)var2;
               this.list.add((NbtElement)success.value());
               break;
            case 1:
               DataResult.Error error = (DataResult.Error)var2;
               this.reporter.report(new AppendToListError(this.key, value, error));
               Optional var6 = error.partialValue();
               NbtList var10001 = this.list;
               Objects.requireNonNull(var10001);
               var6.ifPresent(var10001::add);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }
   }

   public static record AppendToListError(String name, Object value, DataResult.Error error) implements ErrorReporter.Error {
      public AppendToListError(String string, Object object, DataResult.Error error) {
         this.name = string;
         this.value = object;
         this.error = error;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.value);
         return "Failed to append value '" + var10000 + "' to list '" + this.name + "': " + this.error.message();
      }

      public String name() {
         return this.name;
      }

      public Object value() {
         return this.value;
      }

      public DataResult.Error error() {
         return this.error;
      }
   }
}
