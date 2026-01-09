package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.util.Unit;

public class NullOps implements DynamicOps {
   public static final NullOps INSTANCE = new NullOps();

   private NullOps() {
   }

   public Object convertTo(DynamicOps dynamicOps, Unit unit) {
      return dynamicOps.empty();
   }

   public Unit empty() {
      return Unit.INSTANCE;
   }

   public Unit emptyMap() {
      return Unit.INSTANCE;
   }

   public Unit emptyList() {
      return Unit.INSTANCE;
   }

   public Unit createNumeric(Number number) {
      return Unit.INSTANCE;
   }

   public Unit createByte(byte b) {
      return Unit.INSTANCE;
   }

   public Unit createShort(short s) {
      return Unit.INSTANCE;
   }

   public Unit createInt(int i) {
      return Unit.INSTANCE;
   }

   public Unit createLong(long l) {
      return Unit.INSTANCE;
   }

   public Unit createFloat(float f) {
      return Unit.INSTANCE;
   }

   public Unit createDouble(double d) {
      return Unit.INSTANCE;
   }

   public Unit createBoolean(boolean bl) {
      return Unit.INSTANCE;
   }

   public Unit createString(String string) {
      return Unit.INSTANCE;
   }

   public DataResult getNumberValue(Unit unit) {
      return DataResult.error(() -> {
         return "Not a number";
      });
   }

   public DataResult getBooleanValue(Unit unit) {
      return DataResult.error(() -> {
         return "Not a boolean";
      });
   }

   public DataResult getStringValue(Unit unit) {
      return DataResult.error(() -> {
         return "Not a string";
      });
   }

   public DataResult mergeToList(Unit unit, Unit unit2) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToList(Unit unit, List list) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToMap(Unit unit, Unit unit2, Unit unit3) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToMap(Unit unit, Map map) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult mergeToMap(Unit unit, MapLike mapLike) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult getMapValues(Unit unit) {
      return DataResult.error(() -> {
         return "Not a map";
      });
   }

   public DataResult getMapEntries(Unit unit) {
      return DataResult.error(() -> {
         return "Not a map";
      });
   }

   public DataResult getMap(Unit unit) {
      return DataResult.error(() -> {
         return "Not a map";
      });
   }

   public DataResult getStream(Unit unit) {
      return DataResult.error(() -> {
         return "Not a list";
      });
   }

   public DataResult getList(Unit unit) {
      return DataResult.error(() -> {
         return "Not a list";
      });
   }

   public DataResult getByteBuffer(Unit unit) {
      return DataResult.error(() -> {
         return "Not a byte list";
      });
   }

   public DataResult getIntStream(Unit unit) {
      return DataResult.error(() -> {
         return "Not an int list";
      });
   }

   public DataResult getLongStream(Unit unit) {
      return DataResult.error(() -> {
         return "Not a long list";
      });
   }

   public Unit createMap(Stream stream) {
      return Unit.INSTANCE;
   }

   public Unit createMap(Map map) {
      return Unit.INSTANCE;
   }

   public Unit createList(Stream stream) {
      return Unit.INSTANCE;
   }

   public Unit createByteList(ByteBuffer byteBuffer) {
      return Unit.INSTANCE;
   }

   public Unit createIntList(IntStream intStream) {
      return Unit.INSTANCE;
   }

   public Unit createLongList(LongStream longStream) {
      return Unit.INSTANCE;
   }

   public Unit remove(Unit unit, String string) {
      return unit;
   }

   public RecordBuilder mapBuilder() {
      return new NullMapBuilder(this);
   }

   public ListBuilder listBuilder() {
      return new NullListBuilder(this);
   }

   public String toString() {
      return "Null";
   }

   // $FF: synthetic method
   public Object remove(final Object input, final String key) {
      return this.remove((Unit)input, key);
   }

   // $FF: synthetic method
   public Object createLongList(final LongStream stream) {
      return this.createLongList(stream);
   }

   // $FF: synthetic method
   public DataResult getLongStream(final Object input) {
      return this.getLongStream((Unit)input);
   }

   // $FF: synthetic method
   public Object createIntList(final IntStream stream) {
      return this.createIntList(stream);
   }

   // $FF: synthetic method
   public DataResult getIntStream(final Object input) {
      return this.getIntStream((Unit)input);
   }

   // $FF: synthetic method
   public Object createByteList(final ByteBuffer buf) {
      return this.createByteList(buf);
   }

   // $FF: synthetic method
   public DataResult getByteBuffer(final Object input) {
      return this.getByteBuffer((Unit)input);
   }

   // $FF: synthetic method
   public Object createList(final Stream list) {
      return this.createList(list);
   }

   // $FF: synthetic method
   public DataResult getList(final Object input) {
      return this.getList((Unit)input);
   }

   // $FF: synthetic method
   public DataResult getStream(final Object input) {
      return this.getStream((Unit)input);
   }

   // $FF: synthetic method
   public Object createMap(final Map map) {
      return this.createMap(map);
   }

   // $FF: synthetic method
   public DataResult getMap(final Object input) {
      return this.getMap((Unit)input);
   }

   // $FF: synthetic method
   public Object createMap(final Stream map) {
      return this.createMap(map);
   }

   // $FF: synthetic method
   public DataResult getMapEntries(final Object input) {
      return this.getMapEntries((Unit)input);
   }

   // $FF: synthetic method
   public DataResult getMapValues(final Object input) {
      return this.getMapValues((Unit)input);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object map, final MapLike values) {
      return this.mergeToMap((Unit)map, values);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object map, final Map values) {
      return this.mergeToMap((Unit)map, values);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object map, final Object key, final Object value) {
      return this.mergeToMap((Unit)map, (Unit)key, (Unit)value);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object list, final List values) {
      return this.mergeToList((Unit)list, values);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object list, final Object value) {
      return this.mergeToList((Unit)list, (Unit)value);
   }

   // $FF: synthetic method
   public Object createString(final String string) {
      return this.createString(string);
   }

   // $FF: synthetic method
   public DataResult getStringValue(final Object input) {
      return this.getStringValue((Unit)input);
   }

   // $FF: synthetic method
   public Object createBoolean(final boolean bl) {
      return this.createBoolean(bl);
   }

   // $FF: synthetic method
   public DataResult getBooleanValue(final Object input) {
      return this.getBooleanValue((Unit)input);
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
   public DataResult getNumberValue(final Object input) {
      return this.getNumberValue((Unit)input);
   }

   // $FF: synthetic method
   public Object convertTo(final DynamicOps ops, final Object unit) {
      return this.convertTo(ops, (Unit)unit);
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

   static final class NullMapBuilder extends RecordBuilder.AbstractUniversalBuilder {
      public NullMapBuilder(DynamicOps ops) {
         super(ops);
      }

      protected Unit initBuilder() {
         return Unit.INSTANCE;
      }

      protected Unit append(Unit unit, Unit unit2, Unit unit3) {
         return unit3;
      }

      protected DataResult build(Unit unit, Unit unit2) {
         return DataResult.success(unit2);
      }

      // $FF: synthetic method
      protected Object append(final Object key, final Object value, final Object builder) {
         return this.append((Unit)key, (Unit)value, (Unit)builder);
      }

      // $FF: synthetic method
      protected DataResult build(final Object builder, final Object prefix) {
         return this.build((Unit)builder, (Unit)prefix);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }

   static final class NullListBuilder extends AbstractListBuilder {
      public NullListBuilder(DynamicOps dynamicOps) {
         super(dynamicOps);
      }

      protected Unit initBuilder() {
         return Unit.INSTANCE;
      }

      protected Unit add(Unit unit, Unit unit2) {
         return unit;
      }

      protected DataResult build(Unit unit, Unit unit2) {
         return DataResult.success(unit);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }
}
