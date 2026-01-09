package net.minecraft.util;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public sealed interface ActionResult {
   Success SUCCESS = new Success(ActionResult.SwingSource.CLIENT, ActionResult.ItemContext.KEEP_HAND_STACK);
   Success SUCCESS_SERVER = new Success(ActionResult.SwingSource.SERVER, ActionResult.ItemContext.KEEP_HAND_STACK);
   Success CONSUME = new Success(ActionResult.SwingSource.NONE, ActionResult.ItemContext.KEEP_HAND_STACK);
   Fail FAIL = new Fail();
   Pass PASS = new Pass();
   PassToDefaultBlockAction PASS_TO_DEFAULT_BLOCK_ACTION = new PassToDefaultBlockAction();

   default boolean isAccepted() {
      return false;
   }

   public static record Success(SwingSource swingSource, ItemContext itemContext) implements ActionResult {
      public Success(SwingSource swingSource, ItemContext itemContext) {
         this.swingSource = swingSource;
         this.itemContext = itemContext;
      }

      public boolean isAccepted() {
         return true;
      }

      public Success withNewHandStack(ItemStack newHandStack) {
         return new Success(this.swingSource, new ItemContext(true, newHandStack));
      }

      public Success noIncrementStat() {
         return new Success(this.swingSource, ActionResult.ItemContext.KEEP_HAND_STACK_NO_INCREMENT_STAT);
      }

      public boolean shouldIncrementStat() {
         return this.itemContext.incrementStat;
      }

      @Nullable
      public ItemStack getNewHandStack() {
         return this.itemContext.newHandStack;
      }

      public SwingSource swingSource() {
         return this.swingSource;
      }

      public ItemContext itemContext() {
         return this.itemContext;
      }
   }

   public static enum SwingSource {
      NONE,
      CLIENT,
      SERVER;

      // $FF: synthetic method
      private static SwingSource[] method_61397() {
         return new SwingSource[]{NONE, CLIENT, SERVER};
      }
   }

   public static record ItemContext(boolean incrementStat, @Nullable ItemStack newHandStack) {
      final boolean incrementStat;
      @Nullable
      final ItemStack newHandStack;
      static ItemContext KEEP_HAND_STACK_NO_INCREMENT_STAT = new ItemContext(false, (ItemStack)null);
      static ItemContext KEEP_HAND_STACK = new ItemContext(true, (ItemStack)null);

      public ItemContext(boolean bl, @Nullable ItemStack itemStack) {
         this.incrementStat = bl;
         this.newHandStack = itemStack;
      }

      public boolean incrementStat() {
         return this.incrementStat;
      }

      @Nullable
      public ItemStack newHandStack() {
         return this.newHandStack;
      }
   }

   public static record Fail() implements ActionResult {
   }

   public static record Pass() implements ActionResult {
   }

   public static record PassToDefaultBlockAction() implements ActionResult {
   }
}
