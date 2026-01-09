package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record DamageSourcePredicate(List tags, Optional directEntity, Optional sourceEntity, Optional isDirect) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(TagPredicate.createCodec(RegistryKeys.DAMAGE_TYPE).listOf().optionalFieldOf("tags", List.of()).forGetter(DamageSourcePredicate::tags), EntityPredicate.CODEC.optionalFieldOf("direct_entity").forGetter(DamageSourcePredicate::directEntity), EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamageSourcePredicate::sourceEntity), Codec.BOOL.optionalFieldOf("is_direct").forGetter(DamageSourcePredicate::isDirect)).apply(instance, DamageSourcePredicate::new);
   });

   public DamageSourcePredicate(List tagPredicates, Optional optional, Optional optional2, Optional optional3) {
      this.tags = tagPredicates;
      this.directEntity = optional;
      this.sourceEntity = optional2;
      this.isDirect = optional3;
   }

   public boolean test(ServerPlayerEntity player, DamageSource damageSource) {
      return this.test(player.getWorld(), player.getPos(), damageSource);
   }

   public boolean test(ServerWorld world, Vec3d pos, DamageSource damageSource) {
      Iterator var4 = this.tags.iterator();

      TagPredicate tagPredicate;
      do {
         if (!var4.hasNext()) {
            if (this.directEntity.isPresent() && !((EntityPredicate)this.directEntity.get()).test(world, pos, damageSource.getSource())) {
               return false;
            }

            if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).test(world, pos, damageSource.getAttacker())) {
               return false;
            }

            if (this.isDirect.isPresent() && (Boolean)this.isDirect.get() != damageSource.isDirect()) {
               return false;
            }

            return true;
         }

         tagPredicate = (TagPredicate)var4.next();
      } while(tagPredicate.test(damageSource.getTypeRegistryEntry()));

      return false;
   }

   public List tags() {
      return this.tags;
   }

   public Optional directEntity() {
      return this.directEntity;
   }

   public Optional sourceEntity() {
      return this.sourceEntity;
   }

   public Optional isDirect() {
      return this.isDirect;
   }

   public static class Builder {
      private final ImmutableList.Builder tagPredicates = ImmutableList.builder();
      private Optional directEntity = Optional.empty();
      private Optional sourceEntity = Optional.empty();
      private Optional isDirect = Optional.empty();

      public static Builder create() {
         return new Builder();
      }

      public Builder tag(TagPredicate tagPredicate) {
         this.tagPredicates.add(tagPredicate);
         return this;
      }

      public Builder directEntity(EntityPredicate.Builder entity) {
         this.directEntity = Optional.of(entity.build());
         return this;
      }

      public Builder sourceEntity(EntityPredicate.Builder entity) {
         this.sourceEntity = Optional.of(entity.build());
         return this;
      }

      public Builder isDirect(boolean direct) {
         this.isDirect = Optional.of(direct);
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.tagPredicates.build(), this.directEntity, this.sourceEntity, this.isDirect);
      }
   }
}
