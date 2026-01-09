package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;

public class KilledByPlayerLootCondition implements LootCondition {
   private static final KilledByPlayerLootCondition INSTANCE = new KilledByPlayerLootCondition();
   public static final MapCodec CODEC;

   private KilledByPlayerLootCondition() {
   }

   public LootConditionType getType() {
      return LootConditionTypes.KILLED_BY_PLAYER;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.LAST_DAMAGE_PLAYER);
   }

   public boolean test(LootContext lootContext) {
      return lootContext.hasParameter(LootContextParameters.LAST_DAMAGE_PLAYER);
   }

   public static LootCondition.Builder builder() {
      return () -> {
         return INSTANCE;
      };
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
