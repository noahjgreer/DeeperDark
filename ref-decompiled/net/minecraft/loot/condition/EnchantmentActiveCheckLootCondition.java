package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;

public record EnchantmentActiveCheckLootCondition(boolean active) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.fieldOf("active").forGetter(EnchantmentActiveCheckLootCondition::active)).apply(instance, EnchantmentActiveCheckLootCondition::new);
   });

   public EnchantmentActiveCheckLootCondition(boolean bl) {
      this.active = bl;
   }

   public boolean test(LootContext lootContext) {
      return (Boolean)lootContext.getOrThrow(LootContextParameters.ENCHANTMENT_ACTIVE) == this.active;
   }

   public LootConditionType getType() {
      return LootConditionTypes.ENCHANTMENT_ACTIVE_CHECK;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.ENCHANTMENT_ACTIVE);
   }

   public static LootCondition.Builder requireActive() {
      return () -> {
         return new EnchantmentActiveCheckLootCondition(true);
      };
   }

   public static LootCondition.Builder requireInactive() {
      return () -> {
         return new EnchantmentActiveCheckLootCondition(false);
      };
   }

   public boolean active() {
      return this.active;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
