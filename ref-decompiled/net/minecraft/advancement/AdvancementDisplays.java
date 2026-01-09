package net.minecraft.advancement;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public class AdvancementDisplays {
   private static final int DISPLAY_DEPTH = 2;

   private static Status getStatus(Advancement advancement, boolean force) {
      Optional optional = advancement.display();
      if (optional.isEmpty()) {
         return AdvancementDisplays.Status.HIDE;
      } else if (force) {
         return AdvancementDisplays.Status.SHOW;
      } else {
         return ((AdvancementDisplay)optional.get()).isHidden() ? AdvancementDisplays.Status.HIDE : AdvancementDisplays.Status.NO_CHANGE;
      }
   }

   private static boolean shouldDisplay(Stack statuses) {
      for(int i = 0; i <= 2; ++i) {
         Status status = (Status)statuses.peek(i);
         if (status == AdvancementDisplays.Status.SHOW) {
            return true;
         }

         if (status == AdvancementDisplays.Status.HIDE) {
            return false;
         }
      }

      return false;
   }

   private static boolean shouldDisplay(PlacedAdvancement advancement, Stack statuses, Predicate donePredicate, ResultConsumer consumer) {
      boolean bl = donePredicate.test(advancement);
      Status status = getStatus(advancement.getAdvancement(), bl);
      boolean bl2 = bl;
      statuses.push(status);

      PlacedAdvancement placedAdvancement;
      for(Iterator var7 = advancement.getChildren().iterator(); var7.hasNext(); bl2 |= shouldDisplay(placedAdvancement, statuses, donePredicate, consumer)) {
         placedAdvancement = (PlacedAdvancement)var7.next();
      }

      boolean bl3 = bl2 || shouldDisplay(statuses);
      statuses.pop();
      consumer.accept(advancement, bl3);
      return bl2;
   }

   public static void calculateDisplay(PlacedAdvancement advancement, Predicate donePredicate, ResultConsumer consumer) {
      PlacedAdvancement placedAdvancement = advancement.getRoot();
      Stack stack = new ObjectArrayList();

      for(int i = 0; i <= 2; ++i) {
         stack.push(AdvancementDisplays.Status.NO_CHANGE);
      }

      shouldDisplay(placedAdvancement, stack, donePredicate, consumer);
   }

   private static enum Status {
      SHOW,
      HIDE,
      NO_CHANGE;

      // $FF: synthetic method
      private static Status[] method_48034() {
         return new Status[]{SHOW, HIDE, NO_CHANGE};
      }
   }

   @FunctionalInterface
   public interface ResultConsumer {
      void accept(PlacedAdvancement advancement, boolean shouldDisplay);
   }
}
