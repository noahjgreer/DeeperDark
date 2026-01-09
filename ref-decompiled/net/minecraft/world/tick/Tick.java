package net.minecraft.world.tick;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Hash;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

public record Tick(Object type, BlockPos pos, int delay, TickPriority priority) {
   public static final Hash.Strategy HASH_STRATEGY = new Hash.Strategy() {
      public int hashCode(Tick tick) {
         return 31 * tick.pos().hashCode() + tick.type().hashCode();
      }

      public boolean equals(@Nullable Tick tick, @Nullable Tick tick2) {
         if (tick == tick2) {
            return true;
         } else if (tick != null && tick2 != null) {
            return tick.type() == tick2.type() && tick.pos().equals(tick2.pos());
         } else {
            return false;
         }
      }

      // $FF: synthetic method
      public boolean equals(@Nullable final Object first, @Nullable final Object second) {
         return this.equals((Tick)first, (Tick)second);
      }

      // $FF: synthetic method
      public int hashCode(final Object tick) {
         return this.hashCode((Tick)tick);
      }
   };

   public Tick(Object object, BlockPos blockPos, int i, TickPriority tickPriority) {
      this.type = object;
      this.pos = blockPos;
      this.delay = i;
      this.priority = tickPriority;
   }

   public static Codec createCodec(Codec typeCodec) {
      MapCodec mapCodec = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.INT.fieldOf("x").forGetter(Vec3i::getX), Codec.INT.fieldOf("y").forGetter(Vec3i::getY), Codec.INT.fieldOf("z").forGetter(Vec3i::getZ)).apply(instance, BlockPos::new);
      });
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(typeCodec.fieldOf("i").forGetter(Tick::type), mapCodec.forGetter(Tick::pos), Codec.INT.fieldOf("t").forGetter(Tick::delay), TickPriority.CODEC.fieldOf("p").forGetter(Tick::priority)).apply(instance, Tick::new);
      });
   }

   public static List filter(List ticks, ChunkPos chunkPos) {
      long l = chunkPos.toLong();
      return ticks.stream().filter((tick) -> {
         return ChunkPos.toLong(tick.pos()) == l;
      }).toList();
   }

   public OrderedTick createOrderedTick(long time, long subTickOrder) {
      return new OrderedTick(this.type, this.pos, time + (long)this.delay, this.priority, subTickOrder);
   }

   public static Tick create(Object type, BlockPos pos) {
      return new Tick(type, pos, 0, TickPriority.NORMAL);
   }

   public Object type() {
      return this.type;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public int delay() {
      return this.delay;
   }

   public TickPriority priority() {
      return this.priority;
   }
}
