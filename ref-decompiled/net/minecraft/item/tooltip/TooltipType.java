package net.minecraft.item.tooltip;

public interface TooltipType {
   Default BASIC = new Default(false, false);
   Default ADVANCED = new Default(true, false);

   boolean isAdvanced();

   boolean isCreative();

   public static record Default(boolean advanced, boolean creative) implements TooltipType {
      public Default(boolean bl, boolean bl2) {
         this.advanced = bl;
         this.creative = bl2;
      }

      public boolean isAdvanced() {
         return this.advanced;
      }

      public boolean isCreative() {
         return this.creative;
      }

      public Default withCreative() {
         return new Default(this.advanced, true);
      }

      public boolean advanced() {
         return this.advanced;
      }

      public boolean creative() {
         return this.creative;
      }
   }
}
