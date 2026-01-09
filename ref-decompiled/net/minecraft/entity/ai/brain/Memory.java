package net.minecraft.entity.ai.brain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.annotation.Debug;

public class Memory {
   private final Object value;
   private long expiry;

   public Memory(Object value, long expiry) {
      this.value = value;
      this.expiry = expiry;
   }

   public void tick() {
      if (this.isTimed()) {
         --this.expiry;
      }

   }

   public static Memory permanent(Object value) {
      return new Memory(value, Long.MAX_VALUE);
   }

   public static Memory timed(Object value, long expiry) {
      return new Memory(value, expiry);
   }

   public long getExpiry() {
      return this.expiry;
   }

   public Object getValue() {
      return this.value;
   }

   public boolean isExpired() {
      return this.expiry <= 0L;
   }

   public String toString() {
      String var10000 = String.valueOf(this.value);
      return var10000 + (this.isTimed() ? " (ttl: " + this.expiry + ")" : "");
   }

   @Debug
   public boolean isTimed() {
      return this.expiry != Long.MAX_VALUE;
   }

   public static Codec createCodec(Codec codec) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(codec.fieldOf("value").forGetter((memory) -> {
            return memory.value;
         }), Codec.LONG.lenientOptionalFieldOf("ttl").forGetter((memory) -> {
            return memory.isTimed() ? Optional.of(memory.expiry) : Optional.empty();
         })).apply(instance, (value, expiry) -> {
            return new Memory(value, (Long)expiry.orElse(Long.MAX_VALUE));
         });
      });
   }
}
