package net.minecraft.util.context;

import net.minecraft.util.Identifier;

public class ContextParameter {
   private final Identifier id;

   public ContextParameter(Identifier id) {
      this.id = id;
   }

   public static ContextParameter of(String id) {
      return new ContextParameter(Identifier.ofVanilla(id));
   }

   public Identifier getId() {
      return this.id;
   }

   public String toString() {
      return "<parameter " + String.valueOf(this.id) + ">";
   }
}
