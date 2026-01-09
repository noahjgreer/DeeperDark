package net.minecraft.state.property;

import java.util.List;
import java.util.Optional;

public final class BooleanProperty extends Property {
   private static final List VALUES = List.of(true, false);
   private static final int TRUE_ORDINAL = 0;
   private static final int FALSE_ORDINAL = 1;

   private BooleanProperty(String name) {
      super(name, Boolean.class);
   }

   public List getValues() {
      return VALUES;
   }

   public static BooleanProperty of(String name) {
      return new BooleanProperty(name);
   }

   public Optional parse(String name) {
      Optional var10000;
      switch (name) {
         case "true":
            var10000 = Optional.of(true);
            break;
         case "false":
            var10000 = Optional.of(false);
            break;
         default:
            var10000 = Optional.empty();
      }

      return var10000;
   }

   public String name(Boolean boolean_) {
      return boolean_.toString();
   }

   public int ordinal(Boolean boolean_) {
      return boolean_ ? 0 : 1;
   }

   // $FF: synthetic method
   public int ordinal(final Comparable value) {
      return this.ordinal((Boolean)value);
   }

   // $FF: synthetic method
   public String name(final Comparable value) {
      return this.name((Boolean)value);
   }
}
