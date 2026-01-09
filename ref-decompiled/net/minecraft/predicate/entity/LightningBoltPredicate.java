package net.minecraft.predicate.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record LightningBoltPredicate(NumberRange.IntRange blocksSetOnFire, Optional entityStruck) implements EntitySubPredicate {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("blocks_set_on_fire", NumberRange.IntRange.ANY).forGetter(LightningBoltPredicate::blocksSetOnFire), EntityPredicate.CODEC.optionalFieldOf("entity_struck").forGetter(LightningBoltPredicate::entityStruck)).apply(instance, LightningBoltPredicate::new);
   });

   public LightningBoltPredicate(NumberRange.IntRange blocksSetOnFire, Optional optional) {
      this.blocksSetOnFire = blocksSetOnFire;
      this.entityStruck = optional;
   }

   public static LightningBoltPredicate of(NumberRange.IntRange blocksSetOnFire) {
      return new LightningBoltPredicate(blocksSetOnFire, Optional.empty());
   }

   public MapCodec getCodec() {
      return EntitySubPredicateTypes.LIGHTNING;
   }

   public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
      if (!(entity instanceof LightningEntity lightningEntity)) {
         return false;
      } else {
         return this.blocksSetOnFire.test(lightningEntity.getBlocksSetOnFire()) && (this.entityStruck.isEmpty() || lightningEntity.getStruckEntities().anyMatch((struckEntity) -> {
            return ((EntityPredicate)this.entityStruck.get()).test(world, pos, struckEntity);
         }));
      }
   }

   public NumberRange.IntRange blocksSetOnFire() {
      return this.blocksSetOnFire;
   }

   public Optional entityStruck() {
      return this.entityStruck;
   }
}
