package net.minecraft.nbt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class NbtOps implements DynamicOps {
   public static final NbtOps INSTANCE = new NbtOps();

   private NbtOps() {
   }

   public NbtElement empty() {
      return NbtEnd.INSTANCE;
   }

   public Object convertTo(DynamicOps dynamicOps, NbtElement nbtElement) {
      Objects.requireNonNull(nbtElement);
      byte var4 = 0;
      Object var10000;
      boolean var10001;
      Throwable var40;
      switch (nbtElement.typeSwitch<invokedynamic>(nbtElement, var4)) {
         case 0:
            NbtEnd nbtEnd = (NbtEnd)nbtElement;
            var10000 = (Object)dynamicOps.empty();
            return var10000;
         case 1:
            NbtByte var6 = (NbtByte)nbtElement;
            NbtByte var54 = var6;

            byte var55;
            try {
               var55 = var54.value();
            } catch (Throwable var33) {
               var40 = var33;
               var10001 = false;
               break;
            }

            byte var34 = var55;
            var10000 = (Object)dynamicOps.createByte(var34);
            return var10000;
         case 2:
            NbtShort var8 = (NbtShort)nbtElement;
            NbtShort var52 = var8;

            short var53;
            try {
               var53 = var52.value();
            } catch (Throwable var32) {
               var40 = var32;
               var10001 = false;
               break;
            }

            short var35 = var53;
            var10000 = (Object)dynamicOps.createShort(var35);
            return var10000;
         case 3:
            NbtInt var10 = (NbtInt)nbtElement;
            NbtInt var49 = var10;

            int var51;
            try {
               var51 = var49.value();
            } catch (Throwable var31) {
               var40 = var31;
               var10001 = false;
               break;
            }

            int var36 = var51;
            var10000 = (Object)dynamicOps.createInt(var36);
            return var10000;
         case 4:
            NbtLong var12 = (NbtLong)nbtElement;
            NbtLong var47 = var12;

            long var48;
            try {
               var48 = var47.value();
            } catch (Throwable var30) {
               var40 = var30;
               var10001 = false;
               break;
            }

            long var37 = var48;
            var10000 = (Object)dynamicOps.createLong(var37);
            return var10000;
         case 5:
            NbtFloat var15 = (NbtFloat)nbtElement;
            NbtFloat var44 = var15;

            float var46;
            try {
               var46 = var44.value();
            } catch (Throwable var29) {
               var40 = var29;
               var10001 = false;
               break;
            }

            float var39 = var46;
            var10000 = (Object)dynamicOps.createFloat(var39);
            return var10000;
         case 6:
            NbtDouble var17 = (NbtDouble)nbtElement;
            NbtDouble var42 = var17;

            double var43;
            try {
               var43 = var42.value();
            } catch (Throwable var28) {
               var40 = var28;
               var10001 = false;
               break;
            }

            double var45 = var43;
            var10000 = (Object)dynamicOps.createDouble(var45);
            return var10000;
         case 7:
            NbtByteArray nbtByteArray = (NbtByteArray)nbtElement;
            var10000 = (Object)dynamicOps.createByteList(ByteBuffer.wrap(nbtByteArray.getByteArray()));
            return var10000;
         case 8:
            NbtString var21 = (NbtString)nbtElement;
            NbtString var38 = var21;

            String var41;
            try {
               var41 = var38.value();
            } catch (Throwable var27) {
               var40 = var27;
               var10001 = false;
               break;
            }

            String var50 = var41;
            var10000 = (Object)dynamicOps.createString(var50);
            return var10000;
         case 9:
            NbtList nbtList = (NbtList)nbtElement;
            var10000 = (Object)this.convertList(dynamicOps, nbtList);
            return var10000;
         case 10:
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            var10000 = (Object)this.convertMap(dynamicOps, nbtCompound);
            return var10000;
         case 11:
            NbtIntArray nbtIntArray = (NbtIntArray)nbtElement;
            var10000 = (Object)dynamicOps.createIntList(Arrays.stream(nbtIntArray.getIntArray()));
            return var10000;
         case 12:
            NbtLongArray nbtLongArray = (NbtLongArray)nbtElement;
            var10000 = (Object)dynamicOps.createLongList(Arrays.stream(nbtLongArray.getLongArray()));
            return var10000;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Throwable var3 = var40;
      throw new MatchException(var3.toString(), var3);
   }

   public DataResult getNumberValue(NbtElement nbtElement) {
      return (DataResult)nbtElement.asNumber().map(DataResult::success).orElseGet(() -> {
         return DataResult.error(() -> {
            return "Not a number";
         });
      });
   }

   public NbtElement createNumeric(Number number) {
      return NbtDouble.of(number.doubleValue());
   }

   public NbtElement createByte(byte b) {
      return NbtByte.of(b);
   }

   public NbtElement createShort(short s) {
      return NbtShort.of(s);
   }

   public NbtElement createInt(int i) {
      return NbtInt.of(i);
   }

   public NbtElement createLong(long l) {
      return NbtLong.of(l);
   }

   public NbtElement createFloat(float f) {
      return NbtFloat.of(f);
   }

   public NbtElement createDouble(double d) {
      return NbtDouble.of(d);
   }

   public NbtElement createBoolean(boolean bl) {
      return NbtByte.of(bl);
   }

   public DataResult getStringValue(NbtElement nbtElement) {
      if (nbtElement instanceof NbtString var2) {
         NbtString var10000 = var2;

         String var6;
         try {
            var6 = var10000.value();
         } catch (Throwable var5) {
            throw new MatchException(var5.toString(), var5);
         }

         String var4 = var6;
         return DataResult.success(var4);
      } else {
         return DataResult.error(() -> {
            return "Not a string";
         });
      }
   }

   public NbtElement createString(String string) {
      return NbtString.of(string);
   }

   public DataResult mergeToList(NbtElement nbtElement, NbtElement nbtElement2) {
      return (DataResult)createMerger(nbtElement).map((merger) -> {
         return DataResult.success(merger.merge(nbtElement2).getResult());
      }).orElseGet(() -> {
         return DataResult.error(() -> {
            return "mergeToList called with not a list: " + String.valueOf(nbtElement);
         }, nbtElement);
      });
   }

   public DataResult mergeToList(NbtElement nbtElement, List list) {
      return (DataResult)createMerger(nbtElement).map((merger) -> {
         return DataResult.success(merger.merge((Iterable)list).getResult());
      }).orElseGet(() -> {
         return DataResult.error(() -> {
            return "mergeToList called with not a list: " + String.valueOf(nbtElement);
         }, nbtElement);
      });
   }

   public DataResult mergeToMap(NbtElement nbtElement, NbtElement nbtElement2, NbtElement nbtElement3) {
      if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
         return DataResult.error(() -> {
            return "mergeToMap called with not a map: " + String.valueOf(nbtElement);
         }, nbtElement);
      } else if (nbtElement2 instanceof NbtString) {
         NbtString var5 = (NbtString)nbtElement2;
         NbtString var10000 = var5;

         String var9;
         try {
            var9 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         Object nbtCompound = var9;
         NbtCompound var10;
         if (nbtElement instanceof NbtCompound) {
            nbtCompound = (NbtCompound)nbtElement;
            var10 = ((NbtCompound)nbtCompound).shallowCopy();
         } else {
            var10 = new NbtCompound();
         }

         NbtCompound nbtCompound2 = var10;
         nbtCompound2.put((String)nbtCompound, nbtElement3);
         return DataResult.success(nbtCompound2);
      } else {
         return DataResult.error(() -> {
            return "key is not a string: " + String.valueOf(nbtElement2);
         }, nbtElement);
      }
   }

   public DataResult mergeToMap(NbtElement nbtElement, MapLike mapLike) {
      if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
         return DataResult.error(() -> {
            return "mergeToMap called with not a map: " + String.valueOf(nbtElement);
         }, nbtElement);
      } else {
         NbtCompound var10000;
         if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            var10000 = nbtCompound.shallowCopy();
         } else {
            var10000 = new NbtCompound();
         }

         NbtCompound nbtCompound2 = var10000;
         List list = new ArrayList();
         mapLike.entries().forEach((pair) -> {
            NbtElement nbtElement = (NbtElement)pair.getFirst();
            if (nbtElement instanceof NbtString nbtString) {
               NbtString var10000 = nbtString;

               String var8;
               try {
                  var8 = var10000.value();
               } catch (Throwable var7) {
                  throw new MatchException(var7.toString(), var7);
               }

               String string = var8;
               nbtCompound2.put(string, (NbtElement)pair.getSecond());
            } else {
               list.add(nbtElement);
            }
         });
         return !list.isEmpty() ? DataResult.error(() -> {
            return "some keys are not strings: " + String.valueOf(list);
         }, nbtCompound2) : DataResult.success(nbtCompound2);
      }
   }

   public DataResult mergeToMap(NbtElement nbtElement, Map map) {
      if (!(nbtElement instanceof NbtCompound) && !(nbtElement instanceof NbtEnd)) {
         return DataResult.error(() -> {
            return "mergeToMap called with not a map: " + String.valueOf(nbtElement);
         }, nbtElement);
      } else {
         NbtCompound var10000;
         if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            var10000 = nbtCompound.shallowCopy();
         } else {
            var10000 = new NbtCompound();
         }

         NbtCompound nbtCompound2 = var10000;
         List list = new ArrayList();
         Iterator var5 = map.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry entry = (Map.Entry)var5.next();
            NbtElement nbtElement2 = (NbtElement)entry.getKey();
            if (nbtElement2 instanceof NbtString) {
               NbtString var8 = (NbtString)nbtElement2;
               NbtString var13 = var8;

               String var14;
               try {
                  var14 = var13.value();
               } catch (Throwable var11) {
                  throw new MatchException(var11.toString(), var11);
               }

               String var10 = var14;
               nbtCompound2.put(var10, (NbtElement)entry.getValue());
            } else {
               list.add(nbtElement2);
            }
         }

         if (!list.isEmpty()) {
            return DataResult.error(() -> {
               return "some keys are not strings: " + String.valueOf(list);
            }, nbtCompound2);
         } else {
            return DataResult.success(nbtCompound2);
         }
      }
   }

   public DataResult getMapValues(NbtElement nbtElement) {
      if (nbtElement instanceof NbtCompound nbtCompound) {
         return DataResult.success(nbtCompound.entrySet().stream().map((entry) -> {
            return Pair.of(this.createString((String)entry.getKey()), (NbtElement)entry.getValue());
         }));
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + String.valueOf(nbtElement);
         });
      }
   }

   public DataResult getMapEntries(NbtElement nbtElement) {
      if (nbtElement instanceof NbtCompound nbtCompound) {
         return DataResult.success((biConsumer) -> {
            Iterator var3 = nbtCompound.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry entry = (Map.Entry)var3.next();
               biConsumer.accept(this.createString((String)entry.getKey()), (NbtElement)entry.getValue());
            }

         });
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + String.valueOf(nbtElement);
         });
      }
   }

   public DataResult getMap(NbtElement nbtElement) {
      if (nbtElement instanceof final NbtCompound nbtCompound) {
         return DataResult.success(new MapLike() {
            @Nullable
            public NbtElement get(NbtElement nbtElement) {
               if (nbtElement instanceof NbtString var2) {
                  NbtString var10000 = var2;

                  String var6;
                  try {
                     var6 = var10000.value();
                  } catch (Throwable var5) {
                     throw new MatchException(var5.toString(), var5);
                  }

                  String var4 = var6;
                  return nbtCompound.get(var4);
               } else {
                  throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + String.valueOf(nbtElement));
               }
            }

            @Nullable
            public NbtElement get(String string) {
               return nbtCompound.get(string);
            }

            public Stream entries() {
               return nbtCompound.entrySet().stream().map((entry) -> {
                  return Pair.of(NbtOps.this.createString((String)entry.getKey()), (NbtElement)entry.getValue());
               });
            }

            public String toString() {
               return "MapLike[" + String.valueOf(nbtCompound) + "]";
            }

            // $FF: synthetic method
            @Nullable
            public Object get(final String key) {
               return this.get(key);
            }

            // $FF: synthetic method
            @Nullable
            public Object get(final Object nbt) {
               return this.get((NbtElement)nbt);
            }
         });
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + String.valueOf(nbtElement);
         });
      }
   }

   public NbtElement createMap(Stream stream) {
      NbtCompound nbtCompound = new NbtCompound();
      stream.forEach((entry) -> {
         NbtElement nbtElement = (NbtElement)entry.getFirst();
         NbtElement nbtElement2 = (NbtElement)entry.getSecond();
         if (nbtElement instanceof NbtString nbtString) {
            NbtString var10000 = nbtString;

            String var8;
            try {
               var8 = var10000.value();
            } catch (Throwable var7) {
               throw new MatchException(var7.toString(), var7);
            }

            String string = var8;
            nbtCompound.put(string, nbtElement2);
         } else {
            throw new UnsupportedOperationException("Cannot create map with non-string key: " + String.valueOf(nbtElement));
         }
      });
      return nbtCompound;
   }

   public DataResult getStream(NbtElement nbtElement) {
      if (nbtElement instanceof AbstractNbtList abstractNbtList) {
         return DataResult.success(abstractNbtList.stream());
      } else {
         return DataResult.error(() -> {
            return "Not a list";
         });
      }
   }

   public DataResult getList(NbtElement nbtElement) {
      if (nbtElement instanceof AbstractNbtList abstractNbtList) {
         Objects.requireNonNull(abstractNbtList);
         return DataResult.success(abstractNbtList::forEach);
      } else {
         return DataResult.error(() -> {
            return "Not a list: " + String.valueOf(nbtElement);
         });
      }
   }

   public DataResult getByteBuffer(NbtElement nbtElement) {
      if (nbtElement instanceof NbtByteArray nbtByteArray) {
         return DataResult.success(ByteBuffer.wrap(nbtByteArray.getByteArray()));
      } else {
         return super.getByteBuffer(nbtElement);
      }
   }

   public NbtElement createByteList(ByteBuffer byteBuffer) {
      ByteBuffer byteBuffer2 = byteBuffer.duplicate().clear();
      byte[] bs = new byte[byteBuffer.capacity()];
      byteBuffer2.get(0, bs, 0, bs.length);
      return new NbtByteArray(bs);
   }

   public DataResult getIntStream(NbtElement nbtElement) {
      if (nbtElement instanceof NbtIntArray nbtIntArray) {
         return DataResult.success(Arrays.stream(nbtIntArray.getIntArray()));
      } else {
         return super.getIntStream(nbtElement);
      }
   }

   public NbtElement createIntList(IntStream intStream) {
      return new NbtIntArray(intStream.toArray());
   }

   public DataResult getLongStream(NbtElement nbtElement) {
      if (nbtElement instanceof NbtLongArray nbtLongArray) {
         return DataResult.success(Arrays.stream(nbtLongArray.getLongArray()));
      } else {
         return super.getLongStream(nbtElement);
      }
   }

   public NbtElement createLongList(LongStream longStream) {
      return new NbtLongArray(longStream.toArray());
   }

   public NbtElement createList(Stream stream) {
      return new NbtList((List)stream.collect(Util.toArrayList()));
   }

   public NbtElement remove(NbtElement nbtElement, String string) {
      if (nbtElement instanceof NbtCompound nbtCompound) {
         NbtCompound nbtCompound2 = nbtCompound.shallowCopy();
         nbtCompound2.remove(string);
         return nbtCompound2;
      } else {
         return nbtElement;
      }
   }

   public String toString() {
      return "NBT";
   }

   public RecordBuilder mapBuilder() {
      return new MapBuilder(this);
   }

   private static Optional createMerger(NbtElement nbt) {
      if (nbt instanceof NbtEnd) {
         return Optional.of(new CompoundListMerger());
      } else if (nbt instanceof AbstractNbtList) {
         AbstractNbtList abstractNbtList = (AbstractNbtList)nbt;
         if (abstractNbtList.isEmpty()) {
            return Optional.of(new CompoundListMerger());
         } else {
            Objects.requireNonNull(abstractNbtList);
            byte var3 = 0;
            Optional var10000;
            switch (abstractNbtList.typeSwitch<invokedynamic>(abstractNbtList, var3)) {
               case 0:
                  NbtList nbtList = (NbtList)abstractNbtList;
                  var10000 = Optional.of(new CompoundListMerger(nbtList));
                  break;
               case 1:
                  NbtByteArray nbtByteArray = (NbtByteArray)abstractNbtList;
                  var10000 = Optional.of(new ByteArrayMerger(nbtByteArray.getByteArray()));
                  break;
               case 2:
                  NbtIntArray nbtIntArray = (NbtIntArray)abstractNbtList;
                  var10000 = Optional.of(new IntArrayMerger(nbtIntArray.getIntArray()));
                  break;
               case 3:
                  NbtLongArray nbtLongArray = (NbtLongArray)abstractNbtList;
                  var10000 = Optional.of(new LongArrayMerger(nbtLongArray.getLongArray()));
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         }
      } else {
         return Optional.empty();
      }
   }

   // $FF: synthetic method
   public Object remove(final Object element, final String key) {
      return this.remove((NbtElement)element, key);
   }

   // $FF: synthetic method
   public Object createLongList(final LongStream stream) {
      return this.createLongList(stream);
   }

   // $FF: synthetic method
   public DataResult getLongStream(final Object element) {
      return this.getLongStream((NbtElement)element);
   }

   // $FF: synthetic method
   public Object createIntList(final IntStream stream) {
      return this.createIntList(stream);
   }

   // $FF: synthetic method
   public DataResult getIntStream(final Object element) {
      return this.getIntStream((NbtElement)element);
   }

   // $FF: synthetic method
   public Object createByteList(final ByteBuffer buf) {
      return this.createByteList(buf);
   }

   // $FF: synthetic method
   public DataResult getByteBuffer(final Object element) {
      return this.getByteBuffer((NbtElement)element);
   }

   // $FF: synthetic method
   public Object createList(final Stream stream) {
      return this.createList(stream);
   }

   // $FF: synthetic method
   public DataResult getList(final Object element) {
      return this.getList((NbtElement)element);
   }

   // $FF: synthetic method
   public DataResult getStream(final Object element) {
      return this.getStream((NbtElement)element);
   }

   // $FF: synthetic method
   public DataResult getMap(final Object element) {
      return this.getMap((NbtElement)element);
   }

   // $FF: synthetic method
   public Object createMap(final Stream entries) {
      return this.createMap(entries);
   }

   // $FF: synthetic method
   public DataResult getMapEntries(final Object element) {
      return this.getMapEntries((NbtElement)element);
   }

   // $FF: synthetic method
   public DataResult getMapValues(final Object element) {
      return this.getMapValues((NbtElement)element);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object element, final MapLike map) {
      return this.mergeToMap((NbtElement)element, map);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object nbt, final Map map) {
      return this.mergeToMap((NbtElement)nbt, map);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object map, final Object key, final Object value) {
      return this.mergeToMap((NbtElement)map, (NbtElement)key, (NbtElement)value);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object list, final List values) {
      return this.mergeToList((NbtElement)list, values);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object list, final Object value) {
      return this.mergeToList((NbtElement)list, (NbtElement)value);
   }

   // $FF: synthetic method
   public Object createString(final String string) {
      return this.createString(string);
   }

   // $FF: synthetic method
   public DataResult getStringValue(final Object element) {
      return this.getStringValue((NbtElement)element);
   }

   // $FF: synthetic method
   public Object createBoolean(final boolean value) {
      return this.createBoolean(value);
   }

   // $FF: synthetic method
   public Object createDouble(final double value) {
      return this.createDouble(value);
   }

   // $FF: synthetic method
   public Object createFloat(final float value) {
      return this.createFloat(value);
   }

   // $FF: synthetic method
   public Object createLong(final long value) {
      return this.createLong(value);
   }

   // $FF: synthetic method
   public Object createInt(final int value) {
      return this.createInt(value);
   }

   // $FF: synthetic method
   public Object createShort(final short value) {
      return this.createShort(value);
   }

   // $FF: synthetic method
   public Object createByte(final byte value) {
      return this.createByte(value);
   }

   // $FF: synthetic method
   public Object createNumeric(final Number value) {
      return this.createNumeric(value);
   }

   // $FF: synthetic method
   public DataResult getNumberValue(final Object element) {
      return this.getNumberValue((NbtElement)element);
   }

   // $FF: synthetic method
   public Object convertTo(final DynamicOps ops, final Object element) {
      return this.convertTo(ops, (NbtElement)element);
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }

   class MapBuilder extends RecordBuilder.AbstractStringBuilder {
      protected MapBuilder(final NbtOps ops) {
         super(ops);
      }

      protected NbtCompound initBuilder() {
         return new NbtCompound();
      }

      protected NbtCompound append(String string, NbtElement nbtElement, NbtCompound nbtCompound) {
         nbtCompound.put(string, nbtElement);
         return nbtCompound;
      }

      protected DataResult build(NbtCompound nbtCompound, NbtElement nbtElement) {
         if (nbtElement != null && nbtElement != NbtEnd.INSTANCE) {
            if (!(nbtElement instanceof NbtCompound)) {
               return DataResult.error(() -> {
                  return "mergeToMap called with not a map: " + String.valueOf(nbtElement);
               }, nbtElement);
            } else {
               NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
               NbtCompound nbtCompound3 = nbtCompound2.shallowCopy();
               Iterator var5 = nbtCompound.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry entry = (Map.Entry)var5.next();
                  nbtCompound3.put((String)entry.getKey(), (NbtElement)entry.getValue());
               }

               return DataResult.success(nbtCompound3);
            }
         } else {
            return DataResult.success(nbtCompound);
         }
      }

      // $FF: synthetic method
      protected Object append(final String key, final Object value, final Object nbt) {
         return this.append(key, (NbtElement)value, (NbtCompound)nbt);
      }

      // $FF: synthetic method
      protected DataResult build(final Object nbt, final Object mergedValue) {
         return this.build((NbtCompound)nbt, (NbtElement)mergedValue);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }

   private static class CompoundListMerger implements Merger {
      private final NbtList list = new NbtList();

      CompoundListMerger() {
      }

      CompoundListMerger(NbtList nbtList) {
         this.list.addAll(nbtList);
      }

      public CompoundListMerger(IntArrayList list) {
         list.forEach((value) -> {
            this.list.add(NbtInt.of(value));
         });
      }

      public CompoundListMerger(ByteArrayList list) {
         list.forEach((value) -> {
            this.list.add(NbtByte.of(value));
         });
      }

      public CompoundListMerger(LongArrayList list) {
         list.forEach((value) -> {
            this.list.add(NbtLong.of(value));
         });
      }

      public Merger merge(NbtElement nbt) {
         this.list.add(nbt);
         return this;
      }

      public NbtElement getResult() {
         return this.list;
      }
   }

   static class ByteArrayMerger implements Merger {
      private final ByteArrayList list = new ByteArrayList();

      public ByteArrayMerger(byte[] values) {
         this.list.addElements(0, values);
      }

      public Merger merge(NbtElement nbt) {
         if (nbt instanceof NbtByte nbtByte) {
            this.list.add(nbtByte.byteValue());
            return this;
         } else {
            return (new CompoundListMerger(this.list)).merge(nbt);
         }
      }

      public NbtElement getResult() {
         return new NbtByteArray(this.list.toByteArray());
      }
   }

   private static class IntArrayMerger implements Merger {
      private final IntArrayList list = new IntArrayList();

      public IntArrayMerger(int[] values) {
         this.list.addElements(0, values);
      }

      public Merger merge(NbtElement nbt) {
         if (nbt instanceof NbtInt nbtInt) {
            this.list.add(nbtInt.intValue());
            return this;
         } else {
            return (new CompoundListMerger(this.list)).merge(nbt);
         }
      }

      public NbtElement getResult() {
         return new NbtIntArray(this.list.toIntArray());
      }
   }

   static class LongArrayMerger implements Merger {
      private final LongArrayList list = new LongArrayList();

      public LongArrayMerger(long[] values) {
         this.list.addElements(0, values);
      }

      public Merger merge(NbtElement nbt) {
         if (nbt instanceof NbtLong nbtLong) {
            this.list.add(nbtLong.longValue());
            return this;
         } else {
            return (new CompoundListMerger(this.list)).merge(nbt);
         }
      }

      public NbtElement getResult() {
         return new NbtLongArray(this.list.toLongArray());
      }
   }

   private interface Merger {
      Merger merge(NbtElement nbt);

      default Merger merge(Iterable nbts) {
         Merger merger = this;

         NbtElement nbtElement;
         for(Iterator var3 = nbts.iterator(); var3.hasNext(); merger = merger.merge(nbtElement)) {
            nbtElement = (NbtElement)var3.next();
         }

         return merger;
      }

      default Merger merge(Stream nbts) {
         Objects.requireNonNull(nbts);
         return this.merge(nbts::iterator);
      }

      NbtElement getResult();
   }
}
