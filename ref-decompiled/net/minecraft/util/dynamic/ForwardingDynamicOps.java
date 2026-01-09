package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class ForwardingDynamicOps implements DynamicOps {
   protected final DynamicOps delegate;

   protected ForwardingDynamicOps(DynamicOps delegate) {
      this.delegate = delegate;
   }

   public Object empty() {
      return this.delegate.empty();
   }

   public Object emptyMap() {
      return this.delegate.emptyMap();
   }

   public Object emptyList() {
      return this.delegate.emptyList();
   }

   public Object convertTo(DynamicOps outputOps, Object input) {
      return Objects.equals(outputOps, this.delegate) ? input : this.delegate.convertTo(outputOps, input);
   }

   public DataResult getNumberValue(Object input) {
      return this.delegate.getNumberValue(input);
   }

   public Object createNumeric(Number number) {
      return this.delegate.createNumeric(number);
   }

   public Object createByte(byte b) {
      return this.delegate.createByte(b);
   }

   public Object createShort(short s) {
      return this.delegate.createShort(s);
   }

   public Object createInt(int i) {
      return this.delegate.createInt(i);
   }

   public Object createLong(long l) {
      return this.delegate.createLong(l);
   }

   public Object createFloat(float f) {
      return this.delegate.createFloat(f);
   }

   public Object createDouble(double d) {
      return this.delegate.createDouble(d);
   }

   public DataResult getBooleanValue(Object input) {
      return this.delegate.getBooleanValue(input);
   }

   public Object createBoolean(boolean bl) {
      return this.delegate.createBoolean(bl);
   }

   public DataResult getStringValue(Object input) {
      return this.delegate.getStringValue(input);
   }

   public Object createString(String string) {
      return this.delegate.createString(string);
   }

   public DataResult mergeToList(Object list, Object value) {
      return this.delegate.mergeToList(list, value);
   }

   public DataResult mergeToList(Object list, List values) {
      return this.delegate.mergeToList(list, values);
   }

   public DataResult mergeToMap(Object map, Object key, Object value) {
      return this.delegate.mergeToMap(map, key, value);
   }

   public DataResult mergeToMap(Object map, MapLike values) {
      return this.delegate.mergeToMap(map, values);
   }

   public DataResult mergeToMap(Object map, Map values) {
      return this.delegate.mergeToMap(map, values);
   }

   public DataResult mergeToPrimitive(Object prefix, Object value) {
      return this.delegate.mergeToPrimitive(prefix, value);
   }

   public DataResult getMapValues(Object input) {
      return this.delegate.getMapValues(input);
   }

   public DataResult getMapEntries(Object input) {
      return this.delegate.getMapEntries(input);
   }

   public Object createMap(Map map) {
      return this.delegate.createMap(map);
   }

   public Object createMap(Stream map) {
      return this.delegate.createMap(map);
   }

   public DataResult getMap(Object input) {
      return this.delegate.getMap(input);
   }

   public DataResult getStream(Object input) {
      return this.delegate.getStream(input);
   }

   public DataResult getList(Object input) {
      return this.delegate.getList(input);
   }

   public Object createList(Stream stream) {
      return this.delegate.createList(stream);
   }

   public DataResult getByteBuffer(Object input) {
      return this.delegate.getByteBuffer(input);
   }

   public Object createByteList(ByteBuffer buf) {
      return this.delegate.createByteList(buf);
   }

   public DataResult getIntStream(Object input) {
      return this.delegate.getIntStream(input);
   }

   public Object createIntList(IntStream stream) {
      return this.delegate.createIntList(stream);
   }

   public DataResult getLongStream(Object input) {
      return this.delegate.getLongStream(input);
   }

   public Object createLongList(LongStream stream) {
      return this.delegate.createLongList(stream);
   }

   public Object remove(Object input, String key) {
      return this.delegate.remove(input, key);
   }

   public boolean compressMaps() {
      return this.delegate.compressMaps();
   }

   public ListBuilder listBuilder() {
      return new ForwardingListBuilder(this.delegate.listBuilder());
   }

   public RecordBuilder mapBuilder() {
      return new ForwardingRecordBuilder(this.delegate.mapBuilder());
   }

   protected class ForwardingListBuilder implements ListBuilder {
      private final ListBuilder delegate;

      protected ForwardingListBuilder(final ListBuilder delegate) {
         this.delegate = delegate;
      }

      public DynamicOps ops() {
         return ForwardingDynamicOps.this;
      }

      public DataResult build(Object prefix) {
         return this.delegate.build(prefix);
      }

      public ListBuilder add(Object value) {
         this.delegate.add(value);
         return this;
      }

      public ListBuilder add(DataResult value) {
         this.delegate.add(value);
         return this;
      }

      public ListBuilder add(Object value, Encoder encoder) {
         this.delegate.add(encoder.encodeStart(this.ops(), value));
         return this;
      }

      public ListBuilder addAll(Iterable values, Encoder encoder) {
         values.forEach((value) -> {
            this.delegate.add(encoder.encode(value, this.ops(), this.ops().empty()));
         });
         return this;
      }

      public ListBuilder withErrorsFrom(DataResult result) {
         this.delegate.withErrorsFrom(result);
         return this;
      }

      public ListBuilder mapError(UnaryOperator onError) {
         this.delegate.mapError(onError);
         return this;
      }

      public DataResult build(DataResult prefix) {
         return this.delegate.build(prefix);
      }
   }

   protected class ForwardingRecordBuilder implements RecordBuilder {
      private final RecordBuilder delegate;

      protected ForwardingRecordBuilder(final RecordBuilder delegate) {
         this.delegate = delegate;
      }

      public DynamicOps ops() {
         return ForwardingDynamicOps.this;
      }

      public RecordBuilder add(Object key, Object value) {
         this.delegate.add(key, value);
         return this;
      }

      public RecordBuilder add(Object key, DataResult value) {
         this.delegate.add(key, value);
         return this;
      }

      public RecordBuilder add(DataResult key, DataResult value) {
         this.delegate.add(key, value);
         return this;
      }

      public RecordBuilder add(String key, Object value) {
         this.delegate.add(key, value);
         return this;
      }

      public RecordBuilder add(String key, DataResult value) {
         this.delegate.add(key, value);
         return this;
      }

      public RecordBuilder add(String key, Object value, Encoder encoder) {
         return this.delegate.add(key, encoder.encodeStart(this.ops(), value));
      }

      public RecordBuilder withErrorsFrom(DataResult result) {
         this.delegate.withErrorsFrom(result);
         return this;
      }

      public RecordBuilder setLifecycle(Lifecycle lifecycle) {
         this.delegate.setLifecycle(lifecycle);
         return this;
      }

      public RecordBuilder mapError(UnaryOperator onError) {
         this.delegate.mapError(onError);
         return this;
      }

      public DataResult build(Object prefix) {
         return this.delegate.build(prefix);
      }

      public DataResult build(DataResult prefix) {
         return this.delegate.build(prefix);
      }
   }
}
