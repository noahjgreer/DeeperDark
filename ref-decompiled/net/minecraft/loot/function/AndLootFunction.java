package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;

public class AndLootFunction implements LootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootFunctionTypes.BASE_CODEC.listOf().fieldOf("functions").forGetter((function) -> {
         return function.terms;
      })).apply(instance, AndLootFunction::new);
   });
   public static final Codec INLINE_CODEC;
   private final List terms;
   private final BiFunction applier;

   private AndLootFunction(List terms) {
      this.terms = terms;
      this.applier = LootFunctionTypes.join(terms);
   }

   public static AndLootFunction create(List terms) {
      return new AndLootFunction(List.copyOf(terms));
   }

   public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
      return (ItemStack)this.applier.apply(itemStack, lootContext);
   }

   public void validate(LootTableReporter reporter) {
      LootFunction.super.validate(reporter);

      for(int i = 0; i < this.terms.size(); ++i) {
         ((LootFunction)this.terms.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("functions", i)));
      }

   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SEQUENCE;
   }

   // $FF: synthetic method
   public Object apply(final Object stack, final Object context) {
      return this.apply((ItemStack)stack, (LootContext)context);
   }

   static {
      INLINE_CODEC = LootFunctionTypes.BASE_CODEC.listOf().xmap(AndLootFunction::new, (function) -> {
         return function.terms;
      });
   }
}
