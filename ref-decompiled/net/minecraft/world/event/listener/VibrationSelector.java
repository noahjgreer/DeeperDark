package net.minecraft.world.event.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.event.Vibrations;
import org.apache.commons.lang3.tuple.Pair;

public class VibrationSelector {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Vibration.CODEC.lenientOptionalFieldOf("event").forGetter((vibrationSelector) -> {
         return vibrationSelector.current.map(Pair::getLeft);
      }), Codec.LONG.fieldOf("tick").forGetter((vibrationSelector) -> {
         return (Long)vibrationSelector.current.map(Pair::getRight).orElse(-1L);
      })).apply(instance, VibrationSelector::new);
   });
   private Optional current;

   public VibrationSelector(Optional vibration, long tick) {
      this.current = vibration.map((vibration2) -> {
         return Pair.of(vibration2, tick);
      });
   }

   public VibrationSelector() {
      this.current = Optional.empty();
   }

   public void tryAccept(Vibration vibration, long tick) {
      if (this.shouldSelect(vibration, tick)) {
         this.current = Optional.of(Pair.of(vibration, tick));
      }

   }

   private boolean shouldSelect(Vibration vibration, long tick) {
      if (this.current.isEmpty()) {
         return true;
      } else {
         Pair pair = (Pair)this.current.get();
         long l = (Long)pair.getRight();
         if (tick != l) {
            return false;
         } else {
            Vibration vibration2 = (Vibration)pair.getLeft();
            if (vibration.distance() < vibration2.distance()) {
               return true;
            } else if (vibration.distance() > vibration2.distance()) {
               return false;
            } else {
               return Vibrations.getFrequency(vibration.gameEvent()) > Vibrations.getFrequency(vibration2.gameEvent());
            }
         }
      }
   }

   public Optional getVibrationToTick(long currentTick) {
      if (this.current.isEmpty()) {
         return Optional.empty();
      } else {
         return (Long)((Pair)this.current.get()).getRight() < currentTick ? Optional.of((Vibration)((Pair)this.current.get()).getLeft()) : Optional.empty();
      }
   }

   public void clear() {
      this.current = Optional.empty();
   }
}
