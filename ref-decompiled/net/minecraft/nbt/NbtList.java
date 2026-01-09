package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;
import org.jetbrains.annotations.Nullable;

public final class NbtList extends AbstractList implements AbstractNbtList {
   private static final String HOMOGENIZED_ENTRY_KEY = "";
   private static final int SIZE = 36;
   public static final NbtType TYPE = new NbtType.OfVariableSize() {
      public NbtList read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
         nbtSizeTracker.pushStack();

         NbtList var3;
         try {
            var3 = readList(dataInput, nbtSizeTracker);
         } finally {
            nbtSizeTracker.popStack();
         }

         return var3;
      }

      private static NbtList readList(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.add(36L);
         byte b = input.readByte();
         int i = method_72224(input);
         if (b == 0 && i > 0) {
            throw new InvalidNbtException("Missing type on ListTag");
         } else {
            tracker.add(4L, (long)i);
            NbtType nbtType = NbtTypes.byId(b);
            NbtList nbtList = new NbtList(new ArrayList(i));

            for(int j = 0; j < i; ++j) {
               nbtList.unwrapAndAdd(nbtType.read(input, tracker));
            }

            return nbtList;
         }
      }

      public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         tracker.pushStack();

         NbtScanner.Result var4;
         try {
            var4 = scanList(input, visitor, tracker);
         } finally {
            tracker.popStack();
         }

         return var4;
      }

      private static NbtScanner.Result scanList(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
         tracker.add(36L);
         NbtType nbtType = NbtTypes.byId(input.readByte());
         int i = method_72224(input);
         switch (visitor.visitListMeta(nbtType, i)) {
            case HALT:
               return NbtScanner.Result.HALT;
            case BREAK:
               nbtType.skip(input, i, tracker);
               return visitor.endNested();
            default:
               tracker.add(4L, (long)i);
               int j = 0;

               label34:
               for(; j < i; ++j) {
                  switch (visitor.startListItem(nbtType, j)) {
                     case HALT:
                        return NbtScanner.Result.HALT;
                     case BREAK:
                        nbtType.skip(input, tracker);
                        break label34;
                     case SKIP:
                        nbtType.skip(input, tracker);
                        break;
                     default:
                        switch (nbtType.doAccept(input, visitor, tracker)) {
                           case HALT:
                              return NbtScanner.Result.HALT;
                           case BREAK:
                              break label34;
                        }
                  }
               }

               int k = i - 1 - j;
               if (k > 0) {
                  nbtType.skip(input, k, tracker);
               }

               return visitor.endNested();
         }
      }

      private static int method_72224(DataInput dataInput) throws IOException {
         int i = dataInput.readInt();
         if (i < 0) {
            throw new InvalidNbtException("ListTag length cannot be negative: " + i);
         } else {
            return i;
         }
      }

      public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
         tracker.pushStack();

         try {
            NbtType nbtType = NbtTypes.byId(input.readByte());
            int i = input.readInt();
            nbtType.skip(input, i, tracker);
         } finally {
            tracker.popStack();
         }

      }

      public String getCrashReportName() {
         return "LIST";
      }

      public String getCommandFeedbackName() {
         return "TAG_List";
      }

      // $FF: synthetic method
      public NbtElement read(final DataInput input, final NbtSizeTracker tracker) throws IOException {
         return this.read(input, tracker);
      }
   };
   private final List value;

   public NbtList() {
      this(new ArrayList());
   }

   NbtList(List value) {
      this.value = value;
   }

   private static NbtElement unwrap(NbtCompound nbt) {
      if (nbt.getSize() == 1) {
         NbtElement nbtElement = nbt.get("");
         if (nbtElement != null) {
            return nbtElement;
         }
      }

      return nbt;
   }

   private static boolean isConvertedEntry(NbtCompound nbt) {
      return nbt.getSize() == 1 && nbt.contains("");
   }

   private static NbtElement wrapIfNeeded(byte type, NbtElement value) {
      if (type != 10) {
         return value;
      } else {
         if (value instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)value;
            if (!isConvertedEntry(nbtCompound)) {
               return nbtCompound;
            }
         }

         return convertToCompound(value);
      }
   }

   private static NbtCompound convertToCompound(NbtElement nbt) {
      return new NbtCompound(Map.of("", nbt));
   }

   public void write(DataOutput output) throws IOException {
      byte b = this.getValueType();
      output.writeByte(b);
      output.writeInt(this.value.size());
      Iterator var3 = this.value.iterator();

      while(var3.hasNext()) {
         NbtElement nbtElement = (NbtElement)var3.next();
         wrapIfNeeded(b, nbtElement).write(output);
      }

   }

   @VisibleForTesting
   byte getValueType() {
      byte b = 0;
      Iterator var2 = this.value.iterator();

      while(var2.hasNext()) {
         NbtElement nbtElement = (NbtElement)var2.next();
         byte c = nbtElement.getType();
         if (b == 0) {
            b = c;
         } else if (b != c) {
            return 10;
         }
      }

      return b;
   }

   public void unwrapAndAdd(NbtElement nbt) {
      if (nbt instanceof NbtCompound nbtCompound) {
         this.add(unwrap(nbtCompound));
      } else {
         this.add(nbt);
      }

   }

   public int getSizeInBytes() {
      int i = 36;
      i += 4 * this.value.size();

      NbtElement nbtElement;
      for(Iterator var2 = this.value.iterator(); var2.hasNext(); i += nbtElement.getSizeInBytes()) {
         nbtElement = (NbtElement)var2.next();
      }

      return i;
   }

   public byte getType() {
      return 9;
   }

   public NbtType getNbtType() {
      return TYPE;
   }

   public String toString() {
      StringNbtWriter stringNbtWriter = new StringNbtWriter();
      stringNbtWriter.visitList(this);
      return stringNbtWriter.getString();
   }

   public NbtElement method_10536(int i) {
      return (NbtElement)this.value.remove(i);
   }

   public boolean isEmpty() {
      return this.value.isEmpty();
   }

   public Optional getCompound(int index) {
      NbtElement var3 = this.getNullable(index);
      if (var3 instanceof NbtCompound nbtCompound) {
         return Optional.of(nbtCompound);
      } else {
         return Optional.empty();
      }
   }

   public NbtCompound getCompoundOrEmpty(int index) {
      return (NbtCompound)this.getCompound(index).orElseGet(NbtCompound::new);
   }

   public Optional getList(int index) {
      NbtElement var3 = this.getNullable(index);
      if (var3 instanceof NbtList nbtList) {
         return Optional.of(nbtList);
      } else {
         return Optional.empty();
      }
   }

   public NbtList getListOrEmpty(int index) {
      return (NbtList)this.getList(index).orElseGet(NbtList::new);
   }

   public Optional getShort(int index) {
      return this.getOptional(index).flatMap(NbtElement::asShort);
   }

   public short getShort(int index, short fallback) {
      NbtElement var4 = this.getNullable(index);
      if (var4 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.shortValue();
      } else {
         return fallback;
      }
   }

   public Optional getInt(int index) {
      return this.getOptional(index).flatMap(NbtElement::asInt);
   }

   public int getInt(int index, int fallback) {
      NbtElement var4 = this.getNullable(index);
      if (var4 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.intValue();
      } else {
         return fallback;
      }
   }

   public Optional getIntArray(int index) {
      NbtElement var3 = this.getNullable(index);
      if (var3 instanceof NbtIntArray nbtIntArray) {
         return Optional.of(nbtIntArray.getIntArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional getLongArray(int index) {
      NbtElement var3 = this.getNullable(index);
      if (var3 instanceof NbtLongArray nbtLongArray) {
         return Optional.of(nbtLongArray.getLongArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional getDouble(int index) {
      return this.getOptional(index).flatMap(NbtElement::asDouble);
   }

   public double getDouble(int index, double fallback) {
      NbtElement var5 = this.getNullable(index);
      if (var5 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.doubleValue();
      } else {
         return fallback;
      }
   }

   public Optional getFloat(int index) {
      return this.getOptional(index).flatMap(NbtElement::asFloat);
   }

   public float getFloat(int index, float fallback) {
      NbtElement var4 = this.getNullable(index);
      if (var4 instanceof AbstractNbtNumber abstractNbtNumber) {
         return abstractNbtNumber.floatValue();
      } else {
         return fallback;
      }
   }

   public Optional getString(int index) {
      return this.getOptional(index).flatMap(NbtElement::asString);
   }

   public String getString(int index, String fallback) {
      NbtElement nbtElement = this.getNullable(index);
      if (nbtElement instanceof NbtString var4) {
         NbtString var10000 = var4;

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

   @Nullable
   private NbtElement getNullable(int index) {
      return index >= 0 && index < this.value.size() ? (NbtElement)this.value.get(index) : null;
   }

   private Optional getOptional(int index) {
      return Optional.ofNullable(this.getNullable(index));
   }

   public int size() {
      return this.value.size();
   }

   public NbtElement method_10534(int i) {
      return (NbtElement)this.value.get(i);
   }

   public NbtElement set(int i, NbtElement nbtElement) {
      return (NbtElement)this.value.set(i, nbtElement);
   }

   public void add(int i, NbtElement nbtElement) {
      this.value.add(i, nbtElement);
   }

   public boolean setElement(int index, NbtElement element) {
      this.value.set(index, element);
      return true;
   }

   public boolean addElement(int index, NbtElement element) {
      this.value.add(index, element);
      return true;
   }

   public NbtList copy() {
      List list = new ArrayList(this.value.size());
      Iterator var2 = this.value.iterator();

      while(var2.hasNext()) {
         NbtElement nbtElement = (NbtElement)var2.next();
         list.add(nbtElement.copy());
      }

      return new NbtList(list);
   }

   public Optional asNbtList() {
      return Optional.of(this);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return o instanceof NbtList && Objects.equals(this.value, ((NbtList)o).value);
      }
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   public Stream stream() {
      return super.stream();
   }

   public Stream streamCompounds() {
      return this.stream().mapMulti((nbt, callback) -> {
         if (nbt instanceof NbtCompound nbtCompound) {
            callback.accept(nbtCompound);
         }

      });
   }

   public void accept(NbtElementVisitor visitor) {
      visitor.visitList(this);
   }

   public void clear() {
      this.value.clear();
   }

   public NbtScanner.Result doAccept(NbtScanner visitor) {
      byte b = this.getValueType();
      switch (visitor.visitListMeta(NbtTypes.byId(b), this.value.size())) {
         case HALT:
            return NbtScanner.Result.HALT;
         case BREAK:
            return visitor.endNested();
         default:
            int i = 0;

            while(i < this.value.size()) {
               NbtElement nbtElement = wrapIfNeeded(b, (NbtElement)this.value.get(i));
               switch (visitor.startListItem(nbtElement.getNbtType(), i)) {
                  case HALT:
                     return NbtScanner.Result.HALT;
                  case BREAK:
                     return visitor.endNested();
                  default:
                     switch (nbtElement.doAccept(visitor)) {
                        case HALT:
                           return NbtScanner.Result.HALT;
                        case BREAK:
                           return visitor.endNested();
                     }
                  case SKIP:
                     ++i;
               }
            }

            return visitor.endNested();
      }
   }

   // $FF: synthetic method
   public Object remove(final int i) {
      return this.method_10536(i);
   }

   // $FF: synthetic method
   public void add(final int index, final Object element) {
      this.add(index, (NbtElement)element);
   }

   // $FF: synthetic method
   public Object set(final int index, final Object element) {
      return this.set(index, (NbtElement)element);
   }

   // $FF: synthetic method
   public Object get(final int index) {
      return this.method_10534(index);
   }

   // $FF: synthetic method
   public NbtElement copy() {
      return this.copy();
   }
}
