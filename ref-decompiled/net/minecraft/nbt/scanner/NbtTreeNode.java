package net.minecraft.nbt.scanner;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtType;

public record NbtTreeNode(int depth, Map selectedFields, Map fieldsToRecurse) {
   private NbtTreeNode(int depth) {
      this(depth, new HashMap(), new HashMap());
   }

   public NbtTreeNode(int i, Map map, Map map2) {
      this.depth = i;
      this.selectedFields = map;
      this.fieldsToRecurse = map2;
   }

   public static NbtTreeNode createRoot() {
      return new NbtTreeNode(1);
   }

   public void add(NbtScanQuery query) {
      if (this.depth <= query.path().size()) {
         ((NbtTreeNode)this.fieldsToRecurse.computeIfAbsent((String)query.path().get(this.depth - 1), (path) -> {
            return new NbtTreeNode(this.depth + 1);
         })).add(query);
      } else {
         this.selectedFields.put(query.key(), query.type());
      }

   }

   public boolean isTypeEqual(NbtType type, String key) {
      return type.equals(this.selectedFields().get(key));
   }

   public int depth() {
      return this.depth;
   }

   public Map selectedFields() {
      return this.selectedFields;
   }

   public Map fieldsToRecurse() {
      return this.fieldsToRecurse;
   }
}
