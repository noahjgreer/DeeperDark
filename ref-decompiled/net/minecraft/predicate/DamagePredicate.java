package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public record DamagePredicate(NumberRange.DoubleRange dealt, NumberRange.DoubleRange taken, Optional sourceEntity, Optional blocked, Optional type) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(NumberRange.DoubleRange.CODEC.optionalFieldOf("dealt", NumberRange.DoubleRange.ANY).forGetter(DamagePredicate::dealt), NumberRange.DoubleRange.CODEC.optionalFieldOf("taken", NumberRange.DoubleRange.ANY).forGetter(DamagePredicate::taken), EntityPredicate.CODEC.optionalFieldOf("source_entity").forGetter(DamagePredicate::sourceEntity), Codec.BOOL.optionalFieldOf("blocked").forGetter(DamagePredicate::blocked), DamageSourcePredicate.CODEC.optionalFieldOf("type").forGetter(DamagePredicate::type)).apply(instance, DamagePredicate::new);
   });

   public DamagePredicate(NumberRange.DoubleRange dealt, NumberRange.DoubleRange taken, Optional optional, Optional optional2, Optional optional3) {
      this.dealt = dealt;
      this.taken = taken;
      this.sourceEntity = optional;
      this.blocked = optional2;
      this.type = optional3;
   }

   public boolean test(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
      if (!this.dealt.test((double)dealt)) {
         return false;
      } else if (!this.taken.test((double)taken)) {
         return false;
      } else if (this.sourceEntity.isPresent() && !((EntityPredicate)this.sourceEntity.get()).test(player, source.getAttacker())) {
         return false;
      } else if (this.blocked.isPresent() && (Boolean)this.blocked.get() != blocked) {
         return false;
      } else {
         return !this.type.isPresent() || ((DamageSourcePredicate)this.type.get()).test(player, source);
      }
   }

   public NumberRange.DoubleRange dealt() {
      return this.dealt;
   }

   public NumberRange.DoubleRange taken() {
      return this.taken;
   }

   public Optional sourceEntity() {
      return this.sourceEntity;
   }

   public Optional blocked() {
      return this.blocked;
   }

   public Optional type() {
      return this.type;
   }

   public static class Builder {
      private NumberRange.DoubleRange dealt;
      private NumberRange.DoubleRange taken;
      private Optional sourceEntity;
      private Optional blocked;
      private Optional type;

      public Builder() {
         this.dealt = NumberRange.DoubleRange.ANY;
         this.taken = NumberRange.DoubleRange.ANY;
         this.sourceEntity = Optional.empty();
         this.blocked = Optional.empty();
         this.type = Optional.empty();
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder dealt(NumberRange.DoubleRange dealt) {
         this.dealt = dealt;
         return this;
      }

      public Builder taken(NumberRange.DoubleRange taken) {
         this.taken = taken;
         return this;
      }

      public Builder sourceEntity(EntityPredicate sourceEntity) {
         this.sourceEntity = Optional.of(sourceEntity);
         return this;
      }

      public Builder blocked(Boolean blocked) {
         this.blocked = Optional.of(blocked);
         return this;
      }

      public Builder type(DamageSourcePredicate type) {
         this.type = Optional.of(type);
         return this;
      }

      public Builder type(DamageSourcePredicate.Builder builder) {
         this.type = Optional.of(builder.build());
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealt, this.taken, this.sourceEntity, this.blocked, this.type);
      }
   }
}
