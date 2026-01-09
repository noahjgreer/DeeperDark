package net.minecraft.nbt.scanner;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;

public class ExclusiveNbtCollector extends NbtCollector {
   private final Deque treeStack = new ArrayDeque();

   public ExclusiveNbtCollector(NbtScanQuery... excludedQueries) {
      NbtTreeNode nbtTreeNode = NbtTreeNode.createRoot();
      NbtScanQuery[] var3 = excludedQueries;
      int var4 = excludedQueries.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         NbtScanQuery nbtScanQuery = var3[var5];
         nbtTreeNode.add(nbtScanQuery);
      }

      this.treeStack.push(nbtTreeNode);
   }

   public NbtScanner.NestedResult startSubNbt(NbtType type, String key) {
      NbtTreeNode nbtTreeNode = (NbtTreeNode)this.treeStack.element();
      if (nbtTreeNode.isTypeEqual(type, key)) {
         return NbtScanner.NestedResult.SKIP;
      } else {
         if (type == NbtCompound.TYPE) {
            NbtTreeNode nbtTreeNode2 = (NbtTreeNode)nbtTreeNode.fieldsToRecurse().get(key);
            if (nbtTreeNode2 != null) {
               this.treeStack.push(nbtTreeNode2);
            }
         }

         return super.startSubNbt(type, key);
      }
   }

   public NbtScanner.Result endNested() {
      if (this.getDepth() == ((NbtTreeNode)this.treeStack.element()).depth()) {
         this.treeStack.pop();
      }

      return super.endNested();
   }
}
