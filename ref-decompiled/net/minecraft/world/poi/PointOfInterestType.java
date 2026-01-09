package net.minecraft.world.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;

public record PointOfInterestType(Set blockStates, int ticketCount, int searchDistance) {
   public static final Predicate NONE = (type) -> {
      return false;
   };

   public PointOfInterestType(Set set, int i, int j) {
      set = Set.copyOf(set);
      this.blockStates = set;
      this.ticketCount = i;
      this.searchDistance = j;
   }

   public boolean contains(BlockState state) {
      return this.blockStates.contains(state);
   }

   public Set blockStates() {
      return this.blockStates;
   }

   public int ticketCount() {
      return this.ticketCount;
   }

   public int searchDistance() {
      return this.searchDistance;
   }
}
