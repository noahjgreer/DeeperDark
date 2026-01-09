package net.minecraft.resource;

public record ResourcePackPosition(boolean required, ResourcePackProfile.InsertionPosition defaultPosition, boolean fixedPosition) {
   public ResourcePackPosition(boolean bl, ResourcePackProfile.InsertionPosition insertionPosition, boolean bl2) {
      this.required = bl;
      this.defaultPosition = insertionPosition;
      this.fixedPosition = bl2;
   }

   public boolean required() {
      return this.required;
   }

   public ResourcePackProfile.InsertionPosition defaultPosition() {
      return this.defaultPosition;
   }

   public boolean fixedPosition() {
      return this.fixedPosition;
   }
}
