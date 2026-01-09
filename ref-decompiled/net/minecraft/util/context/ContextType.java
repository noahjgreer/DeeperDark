package net.minecraft.util.context;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;

public class ContextType {
   private final Set required;
   private final Set allowed;

   ContextType(Set required, Set allowed) {
      this.required = Set.copyOf(required);
      this.allowed = Set.copyOf(Sets.union(required, allowed));
   }

   public Set getRequired() {
      return this.required;
   }

   public Set getAllowed() {
      return this.allowed;
   }

   public String toString() {
      Joiner var10000 = Joiner.on(", ");
      Iterator var10001 = this.allowed.stream().map((parameter) -> {
         String var10000 = this.required.contains(parameter) ? "!" : "";
         return var10000 + String.valueOf(parameter.getId());
      }).iterator();
      return "[" + var10000.join(var10001) + "]";
   }

   public static class Builder {
      private final Set required = Sets.newIdentityHashSet();
      private final Set allowed = Sets.newIdentityHashSet();

      public Builder require(ContextParameter parameter) {
         if (this.allowed.contains(parameter)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(parameter.getId()) + " is already optional");
         } else {
            this.required.add(parameter);
            return this;
         }
      }

      public Builder allow(ContextParameter parameter) {
         if (this.required.contains(parameter)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(parameter.getId()) + " is already required");
         } else {
            this.allowed.add(parameter);
            return this;
         }
      }

      public ContextType build() {
         return new ContextType(this.required, this.allowed);
      }
   }
}
