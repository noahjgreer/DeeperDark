package net.minecraft.storage;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ErrorReporter;
import org.jetbrains.annotations.Nullable;

public class NbtReadView implements ReadView {
   private final ErrorReporter reporter;
   private final ReadContext context;
   private final NbtCompound nbt;

   private NbtReadView(ErrorReporter reporter, ReadContext context, NbtCompound nbt) {
      this.reporter = reporter;
      this.context = context;
      this.nbt = nbt;
   }

   public static ReadView create(ErrorReporter reporter, RegistryWrapper.WrapperLookup registries, NbtCompound nbt) {
      return new NbtReadView(reporter, new ReadContext(registries, NbtOps.INSTANCE), nbt);
   }

   public static ReadView.ListReadView createList(ErrorReporter reporter, RegistryWrapper.WrapperLookup registries, List elements) {
      return new NbtListReadView(reporter, new ReadContext(registries, NbtOps.INSTANCE), elements);
   }

   public Optional read(String key, Codec codec) {
      NbtElement nbtElement = this.nbt.get(key);
      if (nbtElement == null) {
         return Optional.empty();
      } else {
         DataResult var10000 = codec.parse(this.context.getOps(), nbtElement);
         Objects.requireNonNull(var10000);
         DataResult var4 = var10000;
         byte var5 = 0;
         Optional var8;
         switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
            case 0:
               DataResult.Success success = (DataResult.Success)var4;
               var8 = Optional.of(success.value());
               break;
            case 1:
               DataResult.Error error = (DataResult.Error)var4;
               this.reporter.report(new DecodeError(key, nbtElement, error));
               var8 = error.partialValue();
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var8;
      }
   }

   public Optional read(MapCodec mapCodec) {
      DynamicOps dynamicOps = this.context.getOps();
      DataResult var10000 = dynamicOps.getMap(this.nbt).flatMap((map) -> {
         return mapCodec.decode(dynamicOps, map);
      });
      Objects.requireNonNull(var10000);
      DataResult var3 = var10000;
      byte var4 = 0;
      Optional var7;
      switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
         case 0:
            DataResult.Success success = (DataResult.Success)var3;
            var7 = Optional.of(success.value());
            break;
         case 1:
            DataResult.Error error = (DataResult.Error)var3;
            this.reporter.report(new DecodeMapError(error));
            var7 = error.partialValue();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var7;
   }

   @Nullable
   private NbtElement get(String key, NbtType type) {
      NbtElement nbtElement = this.nbt.get(key);
      if (nbtElement == null) {
         return null;
      } else {
         NbtType nbtType = nbtElement.getNbtType();
         if (nbtType != type) {
            this.reporter.report(new ExpectedTypeError(key, type, nbtType));
            return null;
         } else {
            return nbtElement;
         }
      }
   }

   @Nullable
   private AbstractNbtNumber get(String key) {
      NbtElement nbtElement = this.nbt.get(key);
      if (nbtElement == null) {
         return null;
      } else if (nbtElement instanceof AbstractNbtNumber) {
         AbstractNbtNumber abstractNbtNumber = (AbstractNbtNumber)nbtElement;
         return abstractNbtNumber;
      } else {
         this.reporter.report(new ExpectedNumberError(key, nbtElement.getNbtType()));
         return null;
      }
   }

   public Optional getOptionalReadView(String key) {
      NbtCompound nbtCompound = (NbtCompound)this.get(key, NbtCompound.TYPE);
      return nbtCompound != null ? Optional.of(this.createChildReadView(key, nbtCompound)) : Optional.empty();
   }

   public ReadView getReadView(String key) {
      NbtCompound nbtCompound = (NbtCompound)this.get(key, NbtCompound.TYPE);
      return nbtCompound != null ? this.createChildReadView(key, nbtCompound) : this.context.getEmptyReadView();
   }

   public Optional getOptionalListReadView(String key) {
      NbtList nbtList = (NbtList)this.get(key, NbtList.TYPE);
      return nbtList != null ? Optional.of(this.createChildListReadView(key, this.context, nbtList)) : Optional.empty();
   }

   public ReadView.ListReadView getListReadView(String key) {
      NbtList nbtList = (NbtList)this.get(key, NbtList.TYPE);
      return nbtList != null ? this.createChildListReadView(key, this.context, nbtList) : this.context.getEmptyListReadView();
   }

   public Optional getOptionalTypedListView(String key, Codec typeCodec) {
      NbtList nbtList = (NbtList)this.get(key, NbtList.TYPE);
      return nbtList != null ? Optional.of(this.createTypedListReadView(key, nbtList, typeCodec)) : Optional.empty();
   }

   public ReadView.TypedListReadView getTypedListView(String key, Codec typeCodec) {
      NbtList nbtList = (NbtList)this.get(key, NbtList.TYPE);
      return nbtList != null ? this.createTypedListReadView(key, nbtList, typeCodec) : this.context.getEmptyTypedListReadView();
   }

   public boolean getBoolean(String key, boolean fallback) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? abstractNbtNumber.byteValue() != 0 : fallback;
   }

   public byte getByte(String key, byte fallback) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? abstractNbtNumber.byteValue() : fallback;
   }

   public int getShort(String key, short fallback) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? abstractNbtNumber.shortValue() : fallback;
   }

   public Optional getOptionalInt(String key) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? Optional.of(abstractNbtNumber.intValue()) : Optional.empty();
   }

   public int getInt(String key, int fallback) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? abstractNbtNumber.intValue() : fallback;
   }

   public long getLong(String key, long fallback) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? abstractNbtNumber.longValue() : fallback;
   }

   public Optional getOptionalLong(String key) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? Optional.of(abstractNbtNumber.longValue()) : Optional.empty();
   }

   public float getFloat(String key, float fallback) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? abstractNbtNumber.floatValue() : fallback;
   }

   public double getDouble(String key, double fallback) {
      AbstractNbtNumber abstractNbtNumber = this.get(key);
      return abstractNbtNumber != null ? abstractNbtNumber.doubleValue() : fallback;
   }

   public Optional getOptionalString(String key) {
      NbtString nbtString = (NbtString)this.get(key, NbtString.TYPE);
      return nbtString != null ? Optional.of(nbtString.value()) : Optional.empty();
   }

   public String getString(String key, String fallback) {
      NbtString nbtString = (NbtString)this.get(key, NbtString.TYPE);
      return nbtString != null ? nbtString.value() : fallback;
   }

   public Optional getOptionalIntArray(String key) {
      NbtIntArray nbtIntArray = (NbtIntArray)this.get(key, NbtIntArray.TYPE);
      return nbtIntArray != null ? Optional.of(nbtIntArray.getIntArray()) : Optional.empty();
   }

   public RegistryWrapper.WrapperLookup getRegistries() {
      return this.context.getRegistries();
   }

   private ReadView createChildReadView(String key, NbtCompound nbt) {
      return (ReadView)(nbt.isEmpty() ? this.context.getEmptyReadView() : new NbtReadView(this.reporter.makeChild(new ErrorReporter.MapElementContext(key)), this.context, nbt));
   }

   static ReadView createReadView(ErrorReporter reporter, ReadContext context, NbtCompound nbt) {
      return (ReadView)(nbt.isEmpty() ? context.getEmptyReadView() : new NbtReadView(reporter, context, nbt));
   }

   private ReadView.ListReadView createChildListReadView(String key, ReadContext context, NbtList list) {
      return (ReadView.ListReadView)(list.isEmpty() ? context.getEmptyListReadView() : new ChildListReadView(this.reporter, key, context, list));
   }

   private ReadView.TypedListReadView createTypedListReadView(String key, NbtList list, Codec typeCodec) {
      return (ReadView.TypedListReadView)(list.isEmpty() ? this.context.getEmptyTypedListReadView() : new NbtTypedListReadView(this.reporter, key, this.context, typeCodec, list));
   }

   private static class NbtListReadView implements ReadView.ListReadView {
      private final ErrorReporter reporter;
      private final ReadContext context;
      private final List nbts;

      public NbtListReadView(ErrorReporter reporter, ReadContext context, List nbts) {
         this.reporter = reporter;
         this.context = context;
         this.nbts = nbts;
      }

      ReadView createReadView(int index, NbtCompound nbt) {
         return NbtReadView.createReadView(this.reporter.makeChild(new ErrorReporter.ListElementContext(index)), this.context, nbt);
      }

      public boolean isEmpty() {
         return this.nbts.isEmpty();
      }

      public Stream stream() {
         return Streams.mapWithIndex(this.nbts.stream(), (nbt, index) -> {
            return this.createReadView((int)index, nbt);
         });
      }

      public Iterator iterator() {
         final ListIterator listIterator = this.nbts.listIterator();
         return new AbstractIterator() {
            @Nullable
            protected ReadView computeNext() {
               if (listIterator.hasNext()) {
                  int i = listIterator.nextIndex();
                  NbtCompound nbtCompound = (NbtCompound)listIterator.next();
                  return NbtListReadView.this.createReadView(i, nbtCompound);
               } else {
                  return (ReadView)this.endOfData();
               }
            }

            // $FF: synthetic method
            @Nullable
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      }
   }

   public static record DecodeError(String name, NbtElement element, DataResult.Error error) implements ErrorReporter.Error {
      public DecodeError(String string, NbtElement nbtElement, DataResult.Error error) {
         this.name = string;
         this.element = nbtElement;
         this.error = error;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.element);
         return "Failed to decode value '" + var10000 + "' from field '" + this.name + "': " + this.error.message();
      }

      public String name() {
         return this.name;
      }

      public NbtElement element() {
         return this.element;
      }

      public DataResult.Error error() {
         return this.error;
      }
   }

   public static record DecodeMapError(DataResult.Error error) implements ErrorReporter.Error {
      public DecodeMapError(DataResult.Error error) {
         this.error = error;
      }

      public String getMessage() {
         return "Failed to decode from map: " + this.error.message();
      }

      public DataResult.Error error() {
         return this.error;
      }
   }

   public static record ExpectedTypeError(String name, NbtType expected, NbtType actual) implements ErrorReporter.Error {
      public ExpectedTypeError(String string, NbtType nbtType, NbtType nbtType2) {
         this.name = string;
         this.expected = nbtType;
         this.actual = nbtType2;
      }

      public String getMessage() {
         String var10000 = this.name;
         return "Expected field '" + var10000 + "' to contain value of type " + this.expected.getCrashReportName() + ", but got " + this.actual.getCrashReportName();
      }

      public String name() {
         return this.name;
      }

      public NbtType expected() {
         return this.expected;
      }

      public NbtType actual() {
         return this.actual;
      }
   }

   public static record ExpectedNumberError(String name, NbtType actual) implements ErrorReporter.Error {
      public ExpectedNumberError(String string, NbtType nbtType) {
         this.name = string;
         this.actual = nbtType;
      }

      public String getMessage() {
         String var10000 = this.name;
         return "Expected field '" + var10000 + "' to contain number, but got " + this.actual.getCrashReportName();
      }

      public String name() {
         return this.name;
      }

      public NbtType actual() {
         return this.actual;
      }
   }

   private static class ChildListReadView implements ReadView.ListReadView {
      private final ErrorReporter reporter;
      private final String name;
      final ReadContext context;
      private final NbtList list;

      ChildListReadView(ErrorReporter reporter, String name, ReadContext context, NbtList list) {
         this.reporter = reporter;
         this.name = name;
         this.context = context;
         this.list = list;
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      ErrorReporter createErrorReporter(int index) {
         return this.reporter.makeChild(new ErrorReporter.NamedListElementContext(this.name, index));
      }

      void reportExpectedTypeAtIndexError(int index, NbtElement element) {
         this.reporter.report(new ExpectedTypeAtIndexError(this.name, index, NbtCompound.TYPE, element.getNbtType()));
      }

      public Stream stream() {
         return Streams.mapWithIndex(this.list.stream(), (element, index) -> {
            if (element instanceof NbtCompound nbtCompound) {
               return NbtReadView.createReadView(this.createErrorReporter((int)index), this.context, nbtCompound);
            } else {
               this.reportExpectedTypeAtIndexError((int)index, element);
               return null;
            }
         }).filter(Objects::nonNull);
      }

      public Iterator iterator() {
         final Iterator iterator = this.list.iterator();
         return new AbstractIterator() {
            private int index;

            @Nullable
            protected ReadView computeNext() {
               while(iterator.hasNext()) {
                  NbtElement nbtElement = (NbtElement)iterator.next();
                  int i = this.index++;
                  if (nbtElement instanceof NbtCompound nbtCompound) {
                     return NbtReadView.createReadView(ChildListReadView.this.createErrorReporter(i), ChildListReadView.this.context, nbtCompound);
                  }

                  ChildListReadView.this.reportExpectedTypeAtIndexError(i, nbtElement);
               }

               return (ReadView)this.endOfData();
            }

            // $FF: synthetic method
            @Nullable
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      }
   }

   static class NbtTypedListReadView implements ReadView.TypedListReadView {
      private final ErrorReporter reporter;
      private final String name;
      final ReadContext context;
      final Codec typeCodec;
      private final NbtList list;

      NbtTypedListReadView(ErrorReporter reporter, String name, ReadContext context, Codec typeCodec, NbtList list) {
         this.reporter = reporter;
         this.name = name;
         this.context = context;
         this.typeCodec = typeCodec;
         this.list = list;
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      void reportDecodeAtIndexError(int index, NbtElement element, DataResult.Error error) {
         this.reporter.report(new DecodeAtIndexError(this.name, index, element, error));
      }

      public Stream stream() {
         return Streams.mapWithIndex(this.list.stream(), (element, index) -> {
            DataResult var10000 = this.typeCodec.parse(this.context.getOps(), element);
            Objects.requireNonNull(var10000);
            DataResult dataResult = var10000;
            int i = 0;
            Object var8;
            switch (dataResult.typeSwitch<invokedynamic>(dataResult, i)) {
               case 0:
                  DataResult.Success success = (DataResult.Success)dataResult;
                  var8 = (Object)success.value();
                  break;
               case 1:
                  DataResult.Error error = (DataResult.Error)dataResult;
                  this.reportDecodeAtIndexError((int)index, element, error);
                  var8 = (Object)error.partialValue().orElse((Object)null);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var8;
         }).filter(Objects::nonNull);
      }

      public Iterator iterator() {
         final ListIterator listIterator = this.list.listIterator();
         return new AbstractIterator() {
            @Nullable
            protected Object computeNext() {
               while(true) {
                  if (listIterator.hasNext()) {
                     int i = listIterator.nextIndex();
                     NbtElement nbtElement = (NbtElement)listIterator.next();
                     DataResult var10000 = NbtTypedListReadView.this.typeCodec.parse(NbtTypedListReadView.this.context.getOps(), nbtElement);
                     Objects.requireNonNull(var10000);
                     DataResult var3 = var10000;
                     byte var4 = 0;
                     switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
                        case 0:
                           DataResult.Success success = (DataResult.Success)var3;
                           return success.value();
                        case 1:
                           DataResult.Error error = (DataResult.Error)var3;
                           NbtTypedListReadView.this.reportDecodeAtIndexError(i, nbtElement, error);
                           if (!error.partialValue().isPresent()) {
                              continue;
                           }

                           return error.partialValue().get();
                        default:
                           throw new MatchException((String)null, (Throwable)null);
                     }
                  }

                  return this.endOfData();
               }
            }
         };
      }
   }

   public static record ExpectedTypeAtIndexError(String name, int index, NbtType expected, NbtType actual) implements ErrorReporter.Error {
      public ExpectedTypeAtIndexError(String string, int i, NbtType nbtType, NbtType nbtType2) {
         this.name = string;
         this.index = i;
         this.expected = nbtType;
         this.actual = nbtType2;
      }

      public String getMessage() {
         String var10000 = this.name;
         return "Expected list '" + var10000 + "' to contain at index " + this.index + " value of type " + this.expected.getCrashReportName() + ", but got " + this.actual.getCrashReportName();
      }

      public String name() {
         return this.name;
      }

      public int index() {
         return this.index;
      }

      public NbtType expected() {
         return this.expected;
      }

      public NbtType actual() {
         return this.actual;
      }
   }

   public static record DecodeAtIndexError(String name, int index, NbtElement element, DataResult.Error error) implements ErrorReporter.Error {
      public DecodeAtIndexError(String string, int i, NbtElement nbtElement, DataResult.Error error) {
         this.name = string;
         this.index = i;
         this.element = nbtElement;
         this.error = error;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.element);
         return "Failed to decode value '" + var10000 + "' from field '" + this.name + "' at index " + this.index + "': " + this.error.message();
      }

      public String name() {
         return this.name;
      }

      public int index() {
         return this.index;
      }

      public NbtElement element() {
         return this.element;
      }

      public DataResult.Error error() {
         return this.error;
      }
   }
}
