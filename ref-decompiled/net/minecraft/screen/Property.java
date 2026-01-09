package net.minecraft.screen;

public abstract class Property {
   private int oldValue;

   public static Property create(final PropertyDelegate delegate, final int index) {
      return new Property() {
         public int get() {
            return delegate.get(index);
         }

         public void set(int value) {
            delegate.set(index, value);
         }
      };
   }

   public static Property create(final int[] array, final int index) {
      return new Property() {
         public int get() {
            return array[index];
         }

         public void set(int value) {
            array[index] = value;
         }
      };
   }

   public static Property create() {
      return new Property() {
         private int value;

         public int get() {
            return this.value;
         }

         public void set(int value) {
            this.value = value;
         }
      };
   }

   public abstract int get();

   public abstract void set(int value);

   public boolean hasChanged() {
      int i = this.get();
      boolean bl = i != this.oldValue;
      this.oldValue = i;
      return bl;
   }
}
