package net.minecraft.world;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.datafixer.DataFixTypes;

public record PersistentStateType(String id, Function constructor, Function codec, DataFixTypes dataFixType) {
   public PersistentStateType(String id, Supplier constructor, Codec codec, DataFixTypes dataFixType) {
      this(id, (context) -> {
         return (PersistentState)constructor.get();
      }, (context) -> {
         return codec;
      }, dataFixType);
   }

   public PersistentStateType(String string, Function function, Function function2, DataFixTypes dataFixTypes) {
      this.id = string;
      this.constructor = function;
      this.codec = function2;
      this.dataFixType = dataFixTypes;
   }

   public boolean equals(Object o) {
      boolean var10000;
      if (o instanceof PersistentStateType persistentStateType) {
         if (this.id.equals(persistentStateType.id)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return "SavedDataType[" + this.id + "]";
   }

   public String id() {
      return this.id;
   }

   public Function constructor() {
      return this.constructor;
   }

   public Function codec() {
      return this.codec;
   }

   public DataFixTypes dataFixType() {
      return this.dataFixType;
   }
}
