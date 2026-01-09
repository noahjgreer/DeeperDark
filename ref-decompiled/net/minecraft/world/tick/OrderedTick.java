package net.minecraft.world.tick;

import it.unimi.dsi.fastutil.Hash;
import java.util.Comparator;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record OrderedTick(Object type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
   public static final Comparator TRIGGER_TICK_COMPARATOR = (first, second) -> {
      int i = Long.compare(first.triggerTick, second.triggerTick);
      if (i != 0) {
         return i;
      } else {
         i = first.priority.compareTo(second.priority);
         return i != 0 ? i : Long.compare(first.subTickOrder, second.subTickOrder);
      }
   };
   public static final Comparator BASIC_COMPARATOR = (first, second) -> {
      int i = first.priority.compareTo(second.priority);
      return i != 0 ? i : Long.compare(first.subTickOrder, second.subTickOrder);
   };
   public static final Hash.Strategy HASH_STRATEGY = new Hash.Strategy() {
      public int hashCode(OrderedTick orderedTick) {
         return 31 * orderedTick.pos().hashCode() + orderedTick.type().hashCode();
      }

      public boolean equals(@Nullable OrderedTick orderedTick, @Nullable OrderedTick orderedTick2) {
         if (orderedTick == orderedTick2) {
            return true;
         } else if (orderedTick != null && orderedTick2 != null) {
            return orderedTick.type() == orderedTick2.type() && orderedTick.pos().equals(orderedTick2.pos());
         } else {
            return false;
         }
      }

      // $FF: synthetic method
      public boolean equals(@Nullable final Object first, @Nullable final Object second) {
         return this.equals((OrderedTick)first, (OrderedTick)second);
      }

      // $FF: synthetic method
      public int hashCode(final Object orderedTick) {
         return this.hashCode((OrderedTick)orderedTick);
      }
   };

   public OrderedTick(Object type, BlockPos pos, long triggerTick, long subTickOrder) {
      this(type, pos, triggerTick, TickPriority.NORMAL, subTickOrder);
   }

   public OrderedTick(Object object, BlockPos blockPos, long l, TickPriority tickPriority, long m) {
      blockPos = blockPos.toImmutable();
      this.type = object;
      this.pos = blockPos;
      this.triggerTick = l;
      this.priority = tickPriority;
      this.subTickOrder = m;
   }

   public static OrderedTick create(Object type, BlockPos pos) {
      return new OrderedTick(type, pos, 0L, TickPriority.NORMAL, 0L);
   }

   public Tick toTick(long time) {
      return new Tick(this.type, this.pos, (int)(this.triggerTick - time), this.priority);
   }

   public Object type() {
      return this.type;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public long triggerTick() {
      return this.triggerTick;
   }

   public TickPriority priority() {
      return this.priority;
   }

   public long subTickOrder() {
      return this.subTickOrder;
   }
}
