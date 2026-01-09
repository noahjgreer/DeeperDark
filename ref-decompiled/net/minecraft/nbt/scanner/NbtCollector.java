package net.minecraft.nbt.scanner;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import org.jetbrains.annotations.Nullable;

public class NbtCollector implements NbtScanner {
   private final Deque queue = new ArrayDeque();

   public NbtCollector() {
      this.queue.addLast(new RootNode());
   }

   @Nullable
   public NbtElement getRoot() {
      return ((Node)this.queue.getFirst()).getValue();
   }

   protected int getDepth() {
      return this.queue.size() - 1;
   }

   private void append(NbtElement nbt) {
      ((Node)this.queue.getLast()).append(nbt);
   }

   public NbtScanner.Result visitEnd() {
      this.append(NbtEnd.INSTANCE);
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitString(String value) {
      this.append(NbtString.of(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitByte(byte value) {
      this.append(NbtByte.of(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitShort(short value) {
      this.append(NbtShort.of(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitInt(int value) {
      this.append(NbtInt.of(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitLong(long value) {
      this.append(NbtLong.of(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitFloat(float value) {
      this.append(NbtFloat.of(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitDouble(double value) {
      this.append(NbtDouble.of(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitByteArray(byte[] value) {
      this.append(new NbtByteArray(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitIntArray(int[] value) {
      this.append(new NbtIntArray(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitLongArray(long[] value) {
      this.append(new NbtLongArray(value));
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result visitListMeta(NbtType entryType, int length) {
      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.NestedResult startListItem(NbtType type, int index) {
      this.pushStack(type);
      return NbtScanner.NestedResult.ENTER;
   }

   public NbtScanner.NestedResult visitSubNbtType(NbtType type) {
      return NbtScanner.NestedResult.ENTER;
   }

   public NbtScanner.NestedResult startSubNbt(NbtType type, String key) {
      ((Node)this.queue.getLast()).setKey(key);
      this.pushStack(type);
      return NbtScanner.NestedResult.ENTER;
   }

   private void pushStack(NbtType type) {
      if (type == NbtList.TYPE) {
         this.queue.addLast(new ListNode());
      } else if (type == NbtCompound.TYPE) {
         this.queue.addLast(new CompoundNode());
      }

   }

   public NbtScanner.Result endNested() {
      Node node = (Node)this.queue.removeLast();
      NbtElement nbtElement = node.getValue();
      if (nbtElement != null) {
         ((Node)this.queue.getLast()).append(nbtElement);
      }

      return NbtScanner.Result.CONTINUE;
   }

   public NbtScanner.Result start(NbtType rootType) {
      this.pushStack(rootType);
      return NbtScanner.Result.CONTINUE;
   }

   private static class RootNode implements Node {
      @Nullable
      private NbtElement value;

      RootNode() {
      }

      public void append(NbtElement value) {
         this.value = value;
      }

      @Nullable
      public NbtElement getValue() {
         return this.value;
      }
   }

   private interface Node {
      default void setKey(String key) {
      }

      void append(NbtElement value);

      @Nullable
      NbtElement getValue();
   }

   static class ListNode implements Node {
      private final NbtList value = new NbtList();

      public void append(NbtElement value) {
         this.value.unwrapAndAdd(value);
      }

      public NbtElement getValue() {
         return this.value;
      }
   }

   private static class CompoundNode implements Node {
      private final NbtCompound value = new NbtCompound();
      private String key = "";

      CompoundNode() {
      }

      public void setKey(String key) {
         this.key = key;
      }

      public void append(NbtElement value) {
         this.value.put(this.key, value);
      }

      public NbtElement getValue() {
         return this.value;
      }
   }
}
