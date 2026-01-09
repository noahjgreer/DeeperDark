package net.minecraft.util.packrat;

public record Symbol(String name) {
   public Symbol(String string) {
      this.name = string;
   }

   public String toString() {
      return "<" + this.name + ">";
   }

   public static Symbol of(String name) {
      return new Symbol(name);
   }

   public String name() {
      return this.name;
   }
}
