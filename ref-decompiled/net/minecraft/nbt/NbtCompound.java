package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class NbtCompound implements NbtElement {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec CODEC;
   private static final int SIZE = 48;
   private static final int field_41719 = 32;
   public static final NbtType TYPE;
   private final Map entries;

   NbtCompound(Map entries) {
      this.entries = entries;
   }

   public NbtCompound() {
      this(new HashMap());
   }

   public void write(DataOutput output) throws IOException {
      Iterator var2 = this.entries.keySet().iterator();

      while(var2.hasNext()) {
         String string = (String)var2.next();
         NbtElement nbtElement = (NbtElement)this.entries.get(string);
         write(string, nbtElement, output);
      }

      output.writeByte(0);
   }

   public int getSizeInBytes() {
      int i = 48;

      Map.Entry entry;
      for(Iterator var2 = this.entries.entrySet().iterator(); var2.hasNext(); i += ((NbtElement)entry.getValue()).getSizeInBytes()) {
         entry = (Map.Entry)var2.next();
         i += 28 + 2 * ((String)entry.getKey()).length();
         i += 36;
      }

      return i;
   }

   public Set getKeys() {
      return this.entries.keySet();
   }

   public Set entrySet() {
      return this.entries.entrySet();
   }

   public Collection values() {
      return this.entries.values();
   }

   public void forEach(BiConsumer entryConsumer) {
      this.entries.forEach(entryConsumer);
   }

   public byte getType() {
      return 10;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public int getSize() {
      return this.entries.size();
   }

   @Nullable
   public NbtElement put(String key, NbtElement element) {
      return (NbtElement)this.entries.put(key, element);
   }

   public void putByte(String key, byte value) {
      this.entries.put(key, NbtByte.of(value));
   }

   public void putShort(String key, short value) {
      this.entries.put(key, NbtShort.of(value));
   }

   public void putInt(String key, int value) {
      this.entries.put(key, NbtInt.of(value));
   }

   public void putLong(String key, long value) {
      this.entries.put(key, NbtLong.of(value));
   }

   public void putFloat(String key, float value) {
      this.entries.put(key, NbtFloat.of(value));
   }

   public void putDouble(String key, double value) {
      this.entries.put(key, NbtDouble.of(value));
   }

   public void putString(String key, String value) {
      this.entries.put(key, NbtString.of(value));
   }

   public void putByteArray(String key, byte[] value) {
      this.entries.put(key, new NbtByteArray(value));
   }

   public void putIntArray(String key, int[] value) {
      this.entries.put(key, new NbtIntArray(value));
   }

   public void putLongArray(String key, long[] value) {
      this.entries.put(key, new NbtLongArray(value));
   }

   public void putBoolean(String key, boolean value) {
      this.entries.put(key, NbtByte.of(value));
   }

   @Nullable
   public NbtElement get(String key) {
      return (NbtElement)this.entries.get(key);
   }

   public boolean contains(String key) {
      return this.entries.containsKey(key);
   }

   private Optional getOptional(String key) {
      return Optional.ofNullable((NbtElement)this.entries.get(key));
   }

   public Optional getByte(String key) {
      return this.getOptional(key).flatMap(NbtElement::asByte);
   }

   public byte getByte(String key, byte fallback) {
      Object var4 = this.entries.get(key);
      if (var4 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.byteValue();
      } else {
         return fallback;
      }
   }

   public Optional getShort(String key) {
      return this.getOptional(key).flatMap(NbtElement::asShort);
   }

   public short getShort(String key, short fallback) {
      Object var4 = this.entries.get(key);
      if (var4 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.shortValue();
      } else {
         return fallback;
      }
   }

   public Optional getInt(String key) {
      return this.getOptional(key).flatMap(NbtElement::asInt);
   }

   public int getInt(String key, int fallback) {
      Object var4 = this.entries.get(key);
      if (var4 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.intValue();
      } else {
         return fallback;
      }
   }

   public Optional getLong(String key) {
      return this.getOptional(key).flatMap(NbtElement::asLong);
   }

   public long getLong(String key, long fallback) {
      Object var5 = this.entries.get(key);
      if (var5 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.longValue();
      } else {
         return fallback;
      }
   }

   public Optional getFloat(String key) {
      return this.getOptional(key).flatMap(NbtElement::asFloat);
   }

   public float getFloat(String key, float fallback) {
      Object var4 = this.entries.get(key);
      if (var4 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.floatValue();
      } else {
         return fallback;
      }
   }

   public Optional getDouble(String key) {
      return this.getOptional(key).flatMap(NbtElement::asDouble);
   }

   public double getDouble(String key, double fallback) {
      Object var5 = this.entries.get(key);
      if (var5 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.doubleValue();
      } else {
         return fallback;
      }
   }

   public Optional getString(String key) {
      return this.getOptional(key).flatMap(NbtElement::asString);
   }

   public String getString(String key, String fallback) {
      Object var5 = this.entries.get(key);
      if (var5 instanceof NbtString var3) {
         NbtString var10000 = var3;

         String var8;
         try {
            var8 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String var6 = var8;
         return var6;
      } else {
         return fallback;
      }
   }

   public Optional getByteArray(String key) {
      Object var3 = this.entries.get(key);
      if (var3 instanceof NbtByteArray nbtByteArray) {
         return Optional.of(nbtByteArray.getByteArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional getIntArray(String key) {
      Object var3 = this.entries.get(key);
      if (var3 instanceof NbtIntArray nbtIntArray) {
         return Optional.of(nbtIntArray.getIntArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional getLongArray(String key) {
      Object var3 = this.entries.get(key);
      if (var3 instanceof NbtLongArray nbtLongArray) {
         return Optional.of(nbtLongArray.getLongArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional getCompound(String key) {
      Object var3 = this.entries.get(key);
      if (var3 instanceof NbtCompound nbtCompound) {
         return Optional.of(nbtCompound);
      } else {
         return Optional.empty();
      }
   }

   public NbtCompound getCompoundOrEmpty(String key) {
      return (NbtCompound)this.getCompound(key).orElseGet(NbtCompound::new);
   }

   public Optional getList(String key) {
      Object var3 = this.entries.get(key);
      if (var3 instanceof NbtList nbtList) {
         return Optional.of(nbtList);
      } else {
         return Optional.empty();
      }
   }

   public NbtList getListOrEmpty(String key) {
      return (NbtList)this.getList(key).orElseGet(NbtList::new);
   }

   public Optional getBoolean(String key) {
      return this.getOptional(key).flatMap(NbtElement::asBoolean);
   }

   public boolean getBoolean(String key, boolean fallback) {
      return this.getByte(key, (byte)(fallback ? 1 : 0)) != 0;
   }

   public void remove(String key) {
      this.entries.remove(key);
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitCompound(this);
      return stringNbtWriter.getString();
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   protected NbtCompound shallowCopy() {
      return new NbtCompound(new HashMap(this.entries));
   }

   public NbtCompound copy() {
      HashMap hashMap = new HashMap();
      this.entries.forEach((key, value) -> {
         hashMap.put(key, value.copy());
      });
      return new NbtCompound(hashMap);
   }

   public Optional asCompound() {
      return Optional.of(this);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof NbtCompound && Objects.equals(this.entries, ((NbtCompound)o).entries);
      }
   }

   public int hashCode() {
      return this.entries.hashCode();
   }

   private static void write(String key, NbtElement element, DataOutput output) throws IOException {
      output.writeByte(element.getType());
      if (element.getType() != 0) {
         output.writeUTF(key);
         element.write(output);
      }
   }

   static NbtElement read(NbtType reader, String key, DataInput input, NbtSizeTracker tracker) {
      try {
         return reader.read(input, tracker);
      } catch (IOException var7) {
         CrashReport crashReport = CrashReport.create(var7, "Loading NBT data");
         CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
         crashReportSection.add("Tag name", (Object)key);
         crashReportSection.add("Tag type", (Object)reader.getCrashReportName());
         throw new NbtCrashException(crashReport);
      }
   }

   public NbtCompound copyFrom(NbtCompound source) {
      Iterator var2 = source.entries.keySet().iterator();

      while(true) {
         while(var2.hasNext()) {
            String string = (String)var2.next();
            NbtElement nbtElement = (NbtElement)source.entries.get(string);
            if (nbtElement instanceof NbtCompound nbtCompound) {
               Object var7 = this.entries.get(string);
               if (var7 instanceof NbtCompound nbtCompound2) {
                  nbtCompound2.copyFrom(nbtCompound);
                  continue;
               }
            }

            this.put(string, nbtElement.copy());
         }

         return this;
      }
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitCompound(this);
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      Iterator var2 = this.entries.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         NbtElement nbtElement = (NbtElement)entry.getValue();
         NbtType nbtType = nbtElement.getNbtType();
         NbtScanner.NestedResult nestedResult = visitor.visitSubNbtType(nbtType);
         switch (nestedResult) {
            case HALT:
               return NbtScanner.Result.HALT;
            case BREAK:
               return visitor.endNested();
            case SKIP:
               break;
            default:
               nestedResult = visitor.startSubNbt(nbtType, (String)entry.getKey());
               switch (nestedResult) {
                  case HALT:
                     return NbtScanner.Result.HALT;
                  case BREAK:
                     return visitor.endNested();
                  case SKIP:
                     break;
                  default:
                     NbtScanner.Result result = nbtElement.doAccept(visitor);
                     switch (result) {
                        case HALT:
                           return NbtScanner.Result.HALT;
                        case BREAK:
                           return visitor.endNested();
                     }
               }
         }
      }

      return visitor.endNested();
   }

   public void put(String key, Codec codec, Object value) {
      this.put(key, codec, NbtOps.INSTANCE, value);
   }

   public void putNullable(String key, Codec codec, @Nullable Object value) {
      if (value != null) {
         this.put(key, codec, value);
      }

   }

   public void put(String key, Codec codec, DynamicOps ops, Object value) {
      this.put(key, (NbtElement)codec.encodeStart(ops, value).getOrThrow());
   }

   public void putNullable(String key, Codec codec, DynamicOps ops, @Nullable Object value) {
      if (value != null) {
         this.put(key, codec, ops, value);
      }

   }

   public void copyFromCodec(MapCodec codec, Object value) {
      this.copyFromCodec(codec, NbtOps.INSTANCE, value);
   }

   public void copyFromCodec(MapCodec codec, DynamicOps ops, Object value) {
      this.copyFrom((NbtCompound)codec.encoder().encodeStart(ops, value).getOrThrow());
   }

   public Optional get(String key, Codec codec) {
      return this.get(key, codec, NbtOps.INSTANCE);
   }

   public Optional get(String key, Codec codec, DynamicOps ops) {
      NbtElement nbtElement = this.get(key);
      return nbtElement == null ? Optional.empty() : codec.parse(ops, nbtElement).resultOrPartial((error) -> {
         LOGGER.error("Failed to read field ({}={}): {}", new Object[]{key, nbtElement, error});
      });
   }

   public Optional decode(MapCodec codec) {
      return this.decode(codec, NbtOps.INSTANCE);
   }

   public Optional decode(MapCodec codec, DynamicOps ops) {
      return codec.decode(ops, (MapLike)ops.getMap(this).getOrThrow()).resultOrPartial((error) -> {
         LOGGER.error("Failed to read value ({}): {}", this, error);
      });
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }

   static {
      CODEC = Codec.PASSTHROUGH.comapFlatMap((dynamic) -> {
         NbtElement nbtElement = (NbtElement)dynamic.convert(NbtOps.INSTANCE).getValue();
         if (nbtElement instanceof NbtCompound nbtCompound) {
            return DataResult.success(nbtCompound == dynamic.getValue() ? nbtCompound.copy() : nbtCompound);
         } else {
            return DataResult.error(() -> {
               return "Not a compound tag: " + String.valueOf(nbtElement);
            });
         }
      }, (nbt) -> {
         return new Dynamic(NbtOps.INSTANCE, nbt.copy());
      });
      TYPE = new NbtType.OfVariableSize() {
         public NbtCompound read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
            nbtSizeTracker.pushStack();

            NbtCompound var3;
            try {
               var3 = readCompound(dataInput, nbtSizeTracker);
            } finally {
               nbtSizeTracker.popStack();
            }

            return var3;
         }

         private static NbtCompound readCompound(DataInput input, NbtSizeTracker tracker) throws IOException {
            tracker.add(48L);
            Map map = Maps.newHashMap();

            byte b;
            while((b = input.readByte()) != 0) {
               String string = readString(input, tracker);
               NbtElement nbtElement = NbtCompound.read(NbtTypes.byId(b), string, input, tracker);
               if (map.put(string, nbtElement) == null) {
                  tracker.add(36L);
               }
            }

            return new NbtCompound(map);
         }

         public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            tracker.pushStack();

            NbtScanner.Result var4;
            try {
               var4 = scanCompound(input, visitor, tracker);
            } finally {
               tracker.popStack();
            }

            return var4;
         }

         private static NbtScanner.Result scanCompound(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            tracker.add(48L);

            while(true) {
               byte b;
               if ((b = input.readByte()) != 0) {
                  NbtType nbtType = NbtTypes.byId(b);
                  switch (visitor.visitSubNbtType(nbtType)) {
                     case HALT:
                        return NbtScanner.Result.HALT;
                     case BREAK:
                        NbtString.skip(input);
                        nbtType.skip(input, tracker);
                        break;
                     case SKIP:
                        NbtString.skip(input);
                        nbtType.skip(input, tracker);
                        continue;
                     default:
                        String string = readString(input, tracker);
                        switch (visitor.startSubNbt(nbtType, string)) {
                           case HALT:
                              return NbtScanner.Result.HALT;
                           case BREAK:
                              nbtType.skip(input, tracker);
                              break;
                           case SKIP:
                              nbtType.skip(input, tracker);
                              continue;
                           default:
                              tracker.add(36L);
                              switch (nbtType.doAccept(input, visitor, tracker)) {
                                 case HALT:
                                    return NbtScanner.Result.HALT;
                                 case BREAK:
                                 default:
                                    continue;
                              }
                        }
                  }
               }

               if (b != 0) {
                  while((b = input.readByte()) != 0) {
                     NbtString.skip(input);
                     NbtTypes.byId(b).skip(input, tracker);
                  }
               }

               return visitor.endNested();
            }
         }

         private static String readString(DataInput input, NbtSizeTracker tracker) throws IOException {
            String string = input.readUTF();
            tracker.add(28L);
            tracker.add(2L, (long)string.length());
            return string;
         }

         public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
            tracker.pushStack();

            byte b;
            try {
               while((b = input.readByte()) != 0) {
                  NbtString.skip(input);
                  NbtTypes.byId(b).skip(input, tracker);
               }
            } finally {
               tracker.popStack();
            }

         }

         public String getCrashReportName() {
            return "COMPOUND";
         }

         public String getCommandFeedbackName() {
            return "TAG_Compound";
         }

         // $FF: synthetic method
         public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
            return this.read(input, tracker);
         }
      };
   }
}
