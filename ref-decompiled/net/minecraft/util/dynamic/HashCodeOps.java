package net.minecraft.util.dynamic;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class HashCodeOps implements DynamicOps {
   private static final byte field_58094 = 1;
   private static final byte field_58095 = 2;
   private static final byte field_58096 = 3;
   private static final byte field_58097 = 4;
   private static final byte field_58098 = 5;
   private static final byte field_58099 = 6;
   private static final byte field_58100 = 7;
   private static final byte field_58101 = 8;
   private static final byte field_58102 = 9;
   private static final byte field_58103 = 10;
   private static final byte field_58104 = 11;
   private static final byte field_58105 = 12;
   private static final byte field_58106 = 13;
   private static final byte field_58107 = 14;
   private static final byte field_58108 = 15;
   private static final byte field_58109 = 16;
   private static final byte field_58110 = 17;
   private static final byte field_58111 = 18;
   private static final byte field_58112 = 19;
   private static final byte[] emptyByteArray = new byte[]{1};
   private static final byte[] falseByteArray = new byte[]{13, 0};
   private static final byte[] trueByteArray = new byte[]{13, 1};
   public static final byte[] emptyMapByteArray = new byte[]{2, 3};
   public static final byte[] emptyListByteArray = new byte[]{4, 5};
   private static final DataResult ERROR = DataResult.error(() -> {
      return "Unsupported operation";
   });
   private static final Comparator HASH_CODE_COMPARATOR = Comparator.comparingLong(HashCode::padToLong);
   private static final Comparator ENTRY_COMPARATOR;
   private static final Comparator PAIR_COMPARATOR;
   public static final HashCodeOps INSTANCE;
   final HashFunction function;
   final HashCode empty;
   private final HashCode emptyMap;
   private final HashCode emptyList;
   private final HashCode hashTrue;
   private final HashCode hashFalse;

   public HashCodeOps(HashFunction function) {
      this.function = function;
      this.empty = function.hashBytes(emptyByteArray);
      this.emptyMap = function.hashBytes(emptyMapByteArray);
      this.emptyList = function.hashBytes(emptyListByteArray);
      this.hashFalse = function.hashBytes(falseByteArray);
      this.hashTrue = function.hashBytes(trueByteArray);
   }

   public HashCode empty() {
      return this.empty;
   }

   public HashCode emptyMap() {
      return this.emptyMap;
   }

   public HashCode emptyList() {
      return this.emptyList;
   }

   public HashCode createNumeric(Number number) {
      Objects.requireNonNull(number);
      byte var3 = 0;
      HashCode var10000;
      switch (number.typeSwitch<invokedynamic>(number, var3)) {
         case 0:
            Byte byte_ = (Byte)number;
            var10000 = this.createByte(byte_);
            break;
         case 1:
            Short short_ = (Short)number;
            var10000 = this.createShort(short_);
            break;
         case 2:
            Integer integer = (Integer)number;
            var10000 = this.createInt(integer);
            break;
         case 3:
            Long long_ = (Long)number;
            var10000 = this.createLong(long_);
            break;
         case 4:
            Double double_ = (Double)number;
            var10000 = this.createDouble(double_);
            break;
         case 5:
            Float float_ = (Float)number;
            var10000 = this.createFloat(float_);
            break;
         default:
            var10000 = this.createDouble(number.doubleValue());
      }

      return var10000;
   }

   public HashCode createByte(byte b) {
      return this.function.newHasher(2).putByte((byte)6).putByte(b).hash();
   }

   public HashCode createShort(short s) {
      return this.function.newHasher(3).putByte((byte)7).putShort(s).hash();
   }

   public HashCode createInt(int i) {
      return this.function.newHasher(5).putByte((byte)8).putInt(i).hash();
   }

   public HashCode createLong(long l) {
      return this.function.newHasher(9).putByte((byte)9).putLong(l).hash();
   }

   public HashCode createFloat(float f) {
      return this.function.newHasher(5).putByte((byte)10).putFloat(f).hash();
   }

   public HashCode createDouble(double d) {
      return this.function.newHasher(9).putByte((byte)11).putDouble(d).hash();
   }

   public HashCode createString(String string) {
      return this.function.newHasher().putByte((byte)12).putInt(string.length()).putUnencodedChars(string).hash();
   }

   public HashCode createBoolean(boolean bl) {
      return bl ? this.hashTrue : this.hashFalse;
   }

   private static Hasher hash(Hasher hasher, Map map) {
      hasher.putByte((byte)2);
      map.entrySet().stream().sorted(ENTRY_COMPARATOR).forEach((entry) -> {
         hasher.putBytes(((HashCode)entry.getKey()).asBytes()).putBytes(((HashCode)entry.getValue()).asBytes());
      });
      hasher.putByte((byte)3);
      return hasher;
   }

   static Hasher hash(Hasher hasher, Stream pairs) {
      hasher.putByte((byte)2);
      pairs.sorted(PAIR_COMPARATOR).forEach((pair) -> {
         hasher.putBytes(((HashCode)pair.getFirst()).asBytes()).putBytes(((HashCode)pair.getSecond()).asBytes());
      });
      hasher.putByte((byte)3);
      return hasher;
   }

   public HashCode createMap(Stream stream) {
      return hash(this.function.newHasher(), stream).hash();
   }

   public HashCode createMap(Map map) {
      return hash(this.function.newHasher(), map).hash();
   }

   public HashCode createList(Stream stream) {
      Hasher hasher = this.function.newHasher();
      hasher.putByte((byte)4);
      stream.forEach((hashCode) -> {
         hasher.putBytes(hashCode.asBytes());
      });
      hasher.putByte((byte)5);
      return hasher.hash();
   }

   public HashCode createByteList(ByteBuffer byteBuffer) {
      Hasher hasher = this.function.newHasher();
      hasher.putByte((byte)14);
      hasher.putBytes(byteBuffer);
      hasher.putByte((byte)15);
      return hasher.hash();
   }

   public HashCode createIntList(IntStream intStream) {
      Hasher hasher = this.function.newHasher();
      hasher.putByte((byte)16);
      Objects.requireNonNull(hasher);
      intStream.forEach(hasher::putInt);
      hasher.putByte((byte)17);
      return hasher.hash();
   }

   public HashCode createLongList(LongStream longStream) {
      Hasher hasher = this.function.newHasher();
      hasher.putByte((byte)18);
      Objects.requireNonNull(hasher);
      longStream.forEach(hasher::putLong);
      hasher.putByte((byte)19);
      return hasher.hash();
   }

   public HashCode remove(HashCode hashCode, String string) {
      return hashCode;
   }

   public RecordBuilder mapBuilder() {
      return new Builder();
   }

   public com.mojang.serialization.ListBuilder listBuilder() {
      return new ListBuilder();
   }

   public String toString() {
      return "Hash " + String.valueOf(this.function);
   }

   public Object convertTo(DynamicOps dynamicOps, HashCode hashCode) {
      throw new UnsupportedOperationException("Can't convert from this type");
   }

   public Number getNumberValue(HashCode hashCode, Number number) {
      return number;
   }

   public HashCode set(HashCode hashCode, String string, HashCode hashCode2) {
      return hashCode;
   }

   public HashCode update(HashCode hashCode, String string, Function function) {
      return hashCode;
   }

   public HashCode updateGeneric(HashCode hashCode, HashCode hashCode2, Function function) {
      return hashCode;
   }

   private static DataResult error() {
      return ERROR;
   }

   public DataResult get(HashCode hashCode, String string) {
      return error();
   }

   public DataResult getGeneric(HashCode hashCode, HashCode hashCode2) {
      return error();
   }

   public DataResult getNumberValue(HashCode hashCode) {
      return error();
   }

   public DataResult getBooleanValue(HashCode hashCode) {
      return error();
   }

   public DataResult getStringValue(HashCode hashCode) {
      return error();
   }

   public DataResult mergeToList(HashCode hashCode, HashCode hashCode2) {
      return error();
   }

   public DataResult mergeToList(HashCode hashCode, List list) {
      return error();
   }

   public DataResult mergeToMap(HashCode hashCode, HashCode hashCode2, HashCode hashCode3) {
      return error();
   }

   public DataResult mergeToMap(HashCode hashCode, Map map) {
      return error();
   }

   public DataResult mergeToMap(HashCode hashCode, MapLike mapLike) {
      return error();
   }

   public DataResult getMapValues(HashCode hashCode) {
      return error();
   }

   public DataResult getMapEntries(HashCode hashCode) {
      return error();
   }

   public DataResult getStream(HashCode hashCode) {
      return error();
   }

   public DataResult getList(HashCode hashCode) {
      return error();
   }

   public DataResult getMap(HashCode hashCode) {
      return error();
   }

   public DataResult getByteBuffer(HashCode hashCode) {
      return error();
   }

   public DataResult getIntStream(HashCode hashCode) {
      return error();
   }

   public DataResult getLongStream(HashCode hashCode) {
      return error();
   }

   // $FF: synthetic method
   public Object updateGeneric(final Object object, final Object object2, final Function function) {
      return this.updateGeneric((HashCode)object, (HashCode)object2, function);
   }

   // $FF: synthetic method
   public Object update(final Object object, final String string, final Function function) {
      return this.update((HashCode)object, string, function);
   }

   // $FF: synthetic method
   public Object set(final Object object, final String string, final Object object2) {
      return this.set((HashCode)object, string, (HashCode)object2);
   }

   // $FF: synthetic method
   public DataResult getGeneric(final Object object, final Object object2) {
      return this.getGeneric((HashCode)object, (HashCode)object2);
   }

   // $FF: synthetic method
   public DataResult get(final Object object, final String string) {
      return this.get((HashCode)object, string);
   }

   // $FF: synthetic method
   public Object remove(final Object object, final String string) {
      return this.remove((HashCode)object, string);
   }

   // $FF: synthetic method
   public Object createLongList(final LongStream longStream) {
      return this.createLongList(longStream);
   }

   // $FF: synthetic method
   public DataResult getLongStream(final Object object) {
      return this.getLongStream((HashCode)object);
   }

   // $FF: synthetic method
   public Object createIntList(final IntStream intStream) {
      return this.createIntList(intStream);
   }

   // $FF: synthetic method
   public DataResult getIntStream(final Object object) {
      return this.getIntStream((HashCode)object);
   }

   // $FF: synthetic method
   public Object createByteList(final ByteBuffer byteBuffer) {
      return this.createByteList(byteBuffer);
   }

   // $FF: synthetic method
   public DataResult getByteBuffer(final Object object) {
      return this.getByteBuffer((HashCode)object);
   }

   // $FF: synthetic method
   public Object createList(final Stream stream) {
      return this.createList(stream);
   }

   // $FF: synthetic method
   public DataResult getList(final Object object) {
      return this.getList((HashCode)object);
   }

   // $FF: synthetic method
   public DataResult getStream(final Object object) {
      return this.getStream((HashCode)object);
   }

   // $FF: synthetic method
   public Object createMap(final Map map) {
      return this.createMap(map);
   }

   // $FF: synthetic method
   public DataResult getMap(final Object object) {
      return this.getMap((HashCode)object);
   }

   // $FF: synthetic method
   public Object createMap(final Stream stream) {
      return this.createMap(stream);
   }

   // $FF: synthetic method
   public DataResult getMapEntries(final Object object) {
      return this.getMapEntries((HashCode)object);
   }

   // $FF: synthetic method
   public DataResult getMapValues(final Object object) {
      return this.getMapValues((HashCode)object);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object object, final MapLike mapLike) {
      return this.mergeToMap((HashCode)object, mapLike);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object object, final Map map) {
      return this.mergeToMap((HashCode)object, map);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object object, final Object object2, final Object object3) {
      return this.mergeToMap((HashCode)object, (HashCode)object2, (HashCode)object3);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object object, final List list) {
      return this.mergeToList((HashCode)object, list);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object object, final Object object2) {
      return this.mergeToList((HashCode)object, (HashCode)object2);
   }

   // $FF: synthetic method
   public Object createString(final String string) {
      return this.createString(string);
   }

   // $FF: synthetic method
   public DataResult getStringValue(final Object object) {
      return this.getStringValue((HashCode)object);
   }

   // $FF: synthetic method
   public Object createBoolean(final boolean bl) {
      return this.createBoolean(bl);
   }

   // $FF: synthetic method
   public DataResult getBooleanValue(final Object object) {
      return this.getBooleanValue((HashCode)object);
   }

   // $FF: synthetic method
   public Object createDouble(final double d) {
      return this.createDouble(d);
   }

   // $FF: synthetic method
   public Object createFloat(final float f) {
      return this.createFloat(f);
   }

   // $FF: synthetic method
   public Object createLong(final long l) {
      return this.createLong(l);
   }

   // $FF: synthetic method
   public Object createInt(final int i) {
      return this.createInt(i);
   }

   // $FF: synthetic method
   public Object createShort(final short s) {
      return this.createShort(s);
   }

   // $FF: synthetic method
   public Object createByte(final byte b) {
      return this.createByte(b);
   }

   // $FF: synthetic method
   public Object createNumeric(final Number number) {
      return this.createNumeric(number);
   }

   // $FF: synthetic method
   public Number getNumberValue(final Object object, final Number number) {
      return this.getNumberValue((HashCode)object, number);
   }

   // $FF: synthetic method
   public DataResult getNumberValue(final Object object) {
      return this.getNumberValue((HashCode)object);
   }

   // $FF: synthetic method
   public Object convertTo(final DynamicOps dynamicOps, final Object object) {
      return this.convertTo(dynamicOps, (HashCode)object);
   }

   // $FF: synthetic method
   public Object emptyList() {
      return this.emptyList();
   }

   // $FF: synthetic method
   public Object emptyMap() {
      return this.emptyMap();
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }

   static {
      ENTRY_COMPARATOR = Entry.comparingByKey(HASH_CODE_COMPARATOR).thenComparing(Entry.comparingByValue(HASH_CODE_COMPARATOR));
      PAIR_COMPARATOR = Comparator.comparing(Pair::getFirst, HASH_CODE_COMPARATOR).thenComparing(Pair::getSecond, HASH_CODE_COMPARATOR);
      INSTANCE = new HashCodeOps(Hashing.crc32c());
   }

   private final class Builder extends RecordBuilder.AbstractUniversalBuilder {
      public Builder() {
         super(HashCodeOps.this);
      }

      protected List initBuilder() {
         return new ArrayList();
      }

      protected List append(HashCode hashCode, HashCode hashCode2, List list) {
         list.add(Pair.of(hashCode, hashCode2));
         return list;
      }

      protected DataResult build(List list, HashCode hashCode) {
         assert hashCode.equals(HashCodeOps.this.empty());

         return DataResult.success(HashCodeOps.hash(HashCodeOps.this.function.newHasher(), list.stream()).hash());
      }

      // $FF: synthetic method
      protected Object append(final Object object, final Object object2, final Object object3) {
         return this.append((HashCode)object, (HashCode)object2, (List)object3);
      }

      // $FF: synthetic method
      protected DataResult build(final Object object, final Object object2) {
         return this.build((List)object, (HashCode)object2);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }

   private class ListBuilder extends AbstractListBuilder {
      public ListBuilder() {
         super(HashCodeOps.this);
      }

      protected Hasher initBuilder() {
         return HashCodeOps.this.function.newHasher().putByte((byte)4);
      }

      protected Hasher add(Hasher hasher, HashCode hashCode) {
         return hasher.putBytes(hashCode.asBytes());
      }

      protected DataResult build(Hasher hasher, HashCode hashCode) {
         assert hashCode.equals(HashCodeOps.this.empty);

         hasher.putByte((byte)5);
         return DataResult.success(hasher.hash());
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }
}
