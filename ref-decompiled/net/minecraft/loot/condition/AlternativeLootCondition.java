package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;

public abstract class AlternativeLootCondition implements LootCondition {
   protected final List terms;
   private final Predicate predicate;

   protected AlternativeLootCondition(List terms, Predicate predicate) {
      this.terms = terms;
      this.predicate = predicate;
   }

   protected static MapCodec createCodec(Function termsToCondition) {
      return RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(LootCondition.CODEC.listOf().fieldOf("terms").forGetter((condition) -> {
            return condition.terms;
         })).apply(instance, termsToCondition);
      });
   }

   protected static Codec createInlineCodec(Function termsToCondition) {
      return LootCondition.CODEC.listOf().xmap(termsToCondition, (condition) -> {
         return condition.terms;
      });
   }

   public final boolean test(LootContext lootContext) {
      return this.predicate.test(lootContext);
   }

   public void validate(LootTableReporter reporter) {
      LootCondition.super.validate(reporter);

      for(int i = 0; i < this.terms.size(); ++i) {
         ((LootCondition)this.terms.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("terms", i)));
      }

   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }

   public abstract static class Builder implements LootCondition.Builder {
      private final ImmutableList.Builder terms = ImmutableList.builder();

      protected Builder(LootCondition.Builder... terms) {
         LootCondition.Builder[] var2 = terms;
         int var3 = terms.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootCondition.Builder builder = var2[var4];
            this.terms.add(builder.build());
         }

      }

      public void add(LootCondition.Builder builder) {
         this.terms.add(builder.build());
      }

      public LootCondition build() {
         return this.build(this.terms.build());
      }

      protected abstract LootCondition build(List terms);
   }
}
