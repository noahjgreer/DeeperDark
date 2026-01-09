package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.random.Random;

public class SurvivesExplosionLootCondition implements LootCondition {
   private static final SurvivesExplosionLootCondition INSTANCE = new SurvivesExplosionLootCondition();
   public static final MapCodec CODEC;

   private SurvivesExplosionLootCondition() {
   }

   public LootConditionType getType() {
      return LootConditionTypes.SURVIVES_EXPLOSION;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.EXPLOSION_RADIUS);
   }

   public boolean test(LootContext lootContext) {
      Float float_ = (Float)lootContext.get(LootContextParameters.EXPLOSION_RADIUS);
      if (float_ != null) {
         Random random = lootContext.getRandom();
         float f = 1.0F / float_;
         return random.nextFloat() <= f;
      } else {
         return true;
      }
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
