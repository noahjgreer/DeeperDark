package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

public class LimitCountLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(BoundedIntUnaryOperator.CODEC.fieldOf("limit").forGetter((function) -> {
         return function.limit;
      })).apply(instance, LimitCountLootFunction::new);
   });
   private final BoundedIntUnaryOperator limit;

   private LimitCountLootFunction(List conditions, BoundedIntUnaryOperator limit) {
      super(conditions);
      this.limit = limit;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.LIMIT_COUNT;
   }

   public Set getAllowedParameters() {
      return this.limit.getRequiredParameters();
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      int i = this.limit.apply(context, stack.getCount());
      stack.setCount(i);
      return stack;
   }

   public static ConditionalLootFunction.Builder builder(BoundedIntUnaryOperator limit) {
      return builder((conditions) -> {
         return new LimitCountLootFunction(conditions, limit);
      });
   }
}
