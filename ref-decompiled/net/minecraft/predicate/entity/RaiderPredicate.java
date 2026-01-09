package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record RaiderPredicate(boolean hasRaid, boolean isCaptain) implements EntitySubPredicate {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("has_raid", false).forGetter(RaiderPredicate::hasRaid), Codec.BOOL.optionalFieldOf("is_captain", false).forGetter(RaiderPredicate::isCaptain)).apply(instance, RaiderPredicate::new);
   });
   public static final RaiderPredicate CAPTAIN_WITHOUT_RAID = new RaiderPredicate(false, true);

   public RaiderPredicate(boolean bl, boolean bl2) {
      this.hasRaid = bl;
      this.isCaptain = bl2;
   }

   public MapCodec getCodec() {
      return EntitySubPredicateTypes.RAIDER;
   }

   public boolean test(Entity entity, ServerWorld world, @Nullable Vec3d pos) {
      if (!(entity instanceof RaiderEntity raiderEntity)) {
         return false;
      } else {
         return raiderEntity.hasRaid() == this.hasRaid && raiderEntity.isCaptain() == this.isCaptain;
      }
   }

   public boolean hasRaid() {
      return this.hasRaid;
   }

   public boolean isCaptain() {
      return this.isCaptain;
   }
}
