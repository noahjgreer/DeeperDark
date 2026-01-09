package net.minecraft.entity.spawn;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.predicate.NumberRange;

public record MoonBrightnessSpawnCondition(NumberRange.DoubleRange range) implements SpawnCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(NumberRange.DoubleRange.CODEC.fieldOf("range").forGetter(MoonBrightnessSpawnCondition::range)).apply(instance, MoonBrightnessSpawnCondition::new);
   });

   public MoonBrightnessSpawnCondition(NumberRange.DoubleRange doubleRange) {
      this.range = doubleRange;
   }

   public boolean test(SpawnContext spawnContext) {
      return this.range.test((double)spawnContext.world().toServerWorld().getMoonSize());
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public NumberRange.DoubleRange range() {
      return this.range;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((SpawnContext)context);
   }
}
