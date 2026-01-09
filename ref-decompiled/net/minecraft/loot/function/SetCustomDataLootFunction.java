package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class SetCustomDataLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(StringNbtReader.NBT_COMPOUND_CODEC.fieldOf("tag").forGetter((function) -> {
         return function.nbt;
      })).apply(instance, SetCustomDataLootFunction::new);
   });
   private final NbtCompound nbt;

   private SetCustomDataLootFunction(List conditions, NbtCompound nbt) {
      super(conditions);
      this.nbt = nbt;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_CUSTOM_DATA;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, (nbt) -> {
         nbt.copyFrom(this.nbt);
      });
      return stack;
   }

   /** @deprecated */
   @Deprecated
   public static ConditionalLootFunction.Builder builder(NbtCompound nbt) {
      return builder((conditions) -> {
         return new SetCustomDataLootFunction(conditions, nbt);
      });
   }
}
