package net.minecraft.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record SlimePredicate(NumberRange.IntRange size) implements EntitySubPredicate {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("size", NumberRange.IntRange.ANY).forGetter(SlimePredicate::size)).apply(instance, SlimePredicate::new);
   });

   public SlimePredicate(NumberRange.IntRange size) {
      this.size = size;
   }

   public static SlimePredicate of(NumberRange.IntRange size) {
      return new SlimePredicate(size);
   }

   public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
      if (entity instanceof SlimeEntity slimeEntity) {
         return this.size.test(slimeEntity.getSize());
      } else {
         return false;
      }
   }

   public MapCodec getCodec() {
      return EntitySubPredicateTypes.SLIME;
   }

   public NumberRange.IntRange size() {
      return this.size;
   }
}
