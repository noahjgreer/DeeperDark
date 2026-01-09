package net.minecraft.util.context;

import com.google.common.collect.Sets;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class ContextParameterMap {
   private final Map map;

   ContextParameterMap(Map map) {
      this.map = map;
   }

   public boolean contains(ContextParameter parameter) {
      return this.map.containsKey(parameter);
   }

   public Object getOrThrow(ContextParameter parameter) {
      Object object = this.map.get(parameter);
      if (object == null) {
         throw new NoSuchElementException(parameter.getId().toString());
      } else {
         return object;
      }
   }

   @Nullable
   public Object getNullable(ContextParameter parameter) {
      return this.map.get(parameter);
   }

   @Nullable
   @Contract("_,!null->!null; _,_->_")
   public Object getOrDefault(ContextParameter parameter, @Nullable Object defaultValue) {
      return this.map.getOrDefault(parameter, defaultValue);
   }

   public static class Builder {
      private final Map map = new IdentityHashMap();

      public Builder add(ContextParameter parameter, Object value) {
         this.map.put(parameter, value);
         return this;
      }

      public Builder addNullable(ContextParameter parameter, @Nullable Object value) {
         if (value == null) {
            this.map.remove(parameter);
         } else {
            this.map.put(parameter, value);
         }

         return this;
      }

      public Object getOrThrow(ContextParameter parameter) {
         Object object = this.map.get(parameter);
         if (object == null) {
            throw new NoSuchElementException(parameter.getId().toString());
         } else {
            return object;
         }
      }

      @Nullable
      public Object getNullable(ContextParameter parameter) {
         return this.map.get(parameter);
      }

      public ContextParameterMap build(ContextType type) {
         Set set = Sets.difference(this.map.keySet(), type.getAllowed());
         if (!set.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + String.valueOf(set));
         } else {
            Set set2 = Sets.difference(type.getRequired(), this.map.keySet());
            if (!set2.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + String.valueOf(set2));
            } else {
               return new ContextParameterMap(this.map);
            }
         }
      }
   }
}
