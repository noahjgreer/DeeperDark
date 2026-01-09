package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;

public record InvertedLootCondition(LootCondition term) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootCondition.CODEC.fieldOf("term").forGetter(InvertedLootCondition::term)).apply(instance, InvertedLootCondition::new);
   });

   public InvertedLootCondition(LootCondition term) {
      this.term = term;
   }

   public LootConditionType getType() {
      return LootConditionTypes.INVERTED;
   }

   public boolean test(LootContext lootContext) {
      return !this.term.test(lootContext);
   }

   public Set getAllowedParameters() {
      return this.term.getAllowedParameters();
   }

   public void validate(LootTableReporter reporter) {
      LootCondition.super.validate(reporter);
      this.term.validate(reporter);
   }

   public static LootCondition.Builder builder(LootCondition.Builder term) {
      InvertedLootCondition invertedLootCondition = new InvertedLootCondition(term.build());
      return () -> {
         return invertedLootCondition;
      };
   }

   public LootCondition term() {
      return this.term;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
