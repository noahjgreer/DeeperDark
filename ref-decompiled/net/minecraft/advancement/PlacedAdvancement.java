package net.minecraft.advancement;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class PlacedAdvancement {
   private final AdvancementEntry advancementEntry;
   @Nullable
   private final PlacedAdvancement parent;
   private final Set children = new ReferenceOpenHashSet();

   @VisibleForTesting
   public PlacedAdvancement(AdvancementEntry advancementEntry, @Nullable PlacedAdvancement parent) {
      this.advancementEntry = advancementEntry;
      this.parent = parent;
   }

   public Advancement getAdvancement() {
      return this.advancementEntry.value();
   }

   public AdvancementEntry getAdvancementEntry() {
      return this.advancementEntry;
   }

   @Nullable
   public PlacedAdvancement getParent() {
      return this.parent;
   }

   public PlacedAdvancement getRoot() {
      return findRoot(this);
   }

   public static PlacedAdvancement findRoot(PlacedAdvancement advancement) {
      PlacedAdvancement placedAdvancement = advancement;

      while(true) {
         PlacedAdvancement placedAdvancement2 = placedAdvancement.getParent();
         if (placedAdvancement2 == null) {
            return placedAdvancement;
         }

         placedAdvancement = placedAdvancement2;
      }
   }

   public Iterable getChildren() {
      return this.children;
   }

   @VisibleForTesting
   public void addChild(PlacedAdvancement advancement) {
      this.children.add(advancement);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof PlacedAdvancement) {
            PlacedAdvancement placedAdvancement = (PlacedAdvancement)o;
            if (this.advancementEntry.equals(placedAdvancement.advancementEntry)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.advancementEntry.hashCode();
   }

   public String toString() {
      return this.advancementEntry.id().toString();
   }
}
