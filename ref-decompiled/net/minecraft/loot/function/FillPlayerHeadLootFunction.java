package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;

public class FillPlayerHeadLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter((function) -> {
         return function.entity;
      })).apply(instance, FillPlayerHeadLootFunction::new);
   });
   private final LootContext.EntityTarget entity;

   public FillPlayerHeadLootFunction(List conditions, LootContext.EntityTarget entity) {
      super(conditions);
      this.entity = entity;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.FILL_PLAYER_HEAD;
   }

   public Set getAllowedParameters() {
      return Set.of(this.entity.getParameter());
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      if (stack.isOf(Items.PLAYER_HEAD)) {
         Object var4 = context.get(this.entity.getParameter());
         if (var4 instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)var4;
            stack.set(DataComponentTypes.PROFILE, new ProfileComponent(playerEntity.getGameProfile()));
         }
      }

      return stack;
   }

   public static ConditionalLootFunction.Builder builder(LootContext.EntityTarget target) {
      return builder((conditions) -> {
         return new FillPlayerHeadLootFunction(conditions, target);
      });
   }
}
