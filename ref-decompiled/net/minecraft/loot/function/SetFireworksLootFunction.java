package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.collection.ListOperation;
import net.minecraft.util.dynamic.Codecs;

public class SetFireworksLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(ListOperation.Values.createCodec(FireworkExplosionComponent.CODEC, 256).optionalFieldOf("explosions").forGetter((function) -> {
         return function.explosions;
      }), Codecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration").forGetter((function) -> {
         return function.flightDuration;
      }))).apply(instance, SetFireworksLootFunction::new);
   });
   public static final FireworksComponent DEFAULT_FIREWORKS = new FireworksComponent(0, List.of());
   private final Optional explosions;
   private final Optional flightDuration;

   protected SetFireworksLootFunction(List conditions, Optional explosions, Optional flightDuration) {
      super(conditions);
      this.explosions = explosions;
      this.flightDuration = flightDuration;
   }

   protected ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.FIREWORKS, DEFAULT_FIREWORKS, this::apply);
      return stack;
   }

   private FireworksComponent apply(FireworksComponent fireworksComponent) {
      Optional var10002 = this.flightDuration;
      Objects.requireNonNull(fireworksComponent);
      return new FireworksComponent((Integer)var10002.orElseGet(fireworksComponent::flightDuration), (List)this.explosions.map((values) -> {
         return values.apply(fireworksComponent.explosions());
      }).orElse(fireworksComponent.explosions()));
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_FIREWORKS;
   }
}
