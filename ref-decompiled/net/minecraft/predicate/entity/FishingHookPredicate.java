package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record FishingHookPredicate(Optional inOpenWater) implements EntitySubPredicate {
   public static final FishingHookPredicate ALL = new FishingHookPredicate(Optional.empty());
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("in_open_water").forGetter(FishingHookPredicate::inOpenWater)).apply(instance, FishingHookPredicate::new);
   });

   public FishingHookPredicate(Optional optional) {
      this.inOpenWater = optional;
   }

   public static FishingHookPredicate of(boolean inOpenWater) {
      return new FishingHookPredicate(Optional.of(inOpenWater));
   }

   public MapCodec getCodec() {
      return EntitySubPredicateTypes.FISHING_HOOK;
   }

   public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
      if (this.inOpenWater.isEmpty()) {
         return true;
      } else if (entity instanceof FishingBobberEntity) {
         FishingBobberEntity fishingBobberEntity = (FishingBobberEntity)entity;
         return (Boolean)this.inOpenWater.get() == fishingBobberEntity.isInOpenWater();
      } else {
         return false;
      }
   }

   public Optional inOpenWater() {
      return this.inOpenWater;
   }
}
