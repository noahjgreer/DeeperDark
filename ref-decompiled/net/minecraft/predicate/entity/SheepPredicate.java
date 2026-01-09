package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record SheepPredicate(Optional sheared) implements EntitySubPredicate {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("sheared").forGetter(SheepPredicate::sheared)).apply(instance, SheepPredicate::new);
   });

   public SheepPredicate(Optional optional) {
      this.sheared = optional;
   }

   public MapCodec getCodec() {
      return EntitySubPredicateTypes.SHEEP;
   }

   public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
      if (entity instanceof SheepEntity sheepEntity) {
         return !this.sheared.isPresent() || sheepEntity.isSheared() == (Boolean)this.sheared.get();
      } else {
         return false;
      }
   }

   public static SheepPredicate unsheared() {
      return new SheepPredicate(Optional.of(false));
   }

   public Optional sheared() {
      return this.sheared;
   }
}
