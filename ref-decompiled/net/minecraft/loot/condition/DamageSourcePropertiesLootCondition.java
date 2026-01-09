package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.util.math.Vec3d;

public record DamageSourcePropertiesLootCondition(Optional predicate) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DamageSourcePredicate.CODEC.optionalFieldOf("predicate").forGetter(DamageSourcePropertiesLootCondition::predicate)).apply(instance, DamageSourcePropertiesLootCondition::new);
   });

   public DamageSourcePropertiesLootCondition(Optional optional) {
      this.predicate = optional;
   }

   public LootConditionType getType() {
      return LootConditionTypes.DAMAGE_SOURCE_PROPERTIES;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.ORIGIN, LootContextParameters.DAMAGE_SOURCE);
   }

   public boolean test(LootContext lootContext) {
      DamageSource damageSource = (DamageSource)lootContext.get(LootContextParameters.DAMAGE_SOURCE);
      Vec3d vec3d = (Vec3d)lootContext.get(LootContextParameters.ORIGIN);
      if (vec3d != null && damageSource != null) {
         return this.predicate.isEmpty() || ((DamageSourcePredicate)this.predicate.get()).test(lootContext.getWorld(), vec3d, damageSource);
      } else {
         return false;
      }
   }

   public static LootCondition.Builder builder(DamageSourcePredicate.Builder builder) {
      return () -> {
         return new DamageSourcePropertiesLootCondition(Optional.of(builder.build()));
      };
   }

   public Optional predicate() {
      return this.predicate;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
