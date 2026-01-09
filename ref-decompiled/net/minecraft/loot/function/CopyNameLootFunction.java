package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Nameable;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;

public class CopyNameLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(CopyNameLootFunction.Source.CODEC.fieldOf("source").forGetter((function) -> {
         return function.source;
      })).apply(instance, CopyNameLootFunction::new);
   });
   private final Source source;

   private CopyNameLootFunction(List conditions, Source source) {
      super(conditions);
      this.source = source;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.COPY_NAME;
   }

   public Set getAllowedParameters() {
      return Set.of(this.source.parameter);
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      Object object = context.get(this.source.parameter);
      if (object instanceof Nameable nameable) {
         stack.set(DataComponentTypes.CUSTOM_NAME, nameable.getCustomName());
      }

      return stack;
   }

   public static ConditionalLootFunction.Builder builder(Source source) {
      return builder((conditions) -> {
         return new CopyNameLootFunction(conditions, source);
      });
   }

   public static enum Source implements StringIdentifiable {
      THIS("this", LootContextParameters.THIS_ENTITY),
      ATTACKING_ENTITY("attacking_entity", LootContextParameters.ATTACKING_ENTITY),
      LAST_DAMAGE_PLAYER("last_damage_player", LootContextParameters.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY);

      public static final Codec CODEC = StringIdentifiable.createCodec(Source::values);
      private final String name;
      final ContextParameter parameter;

      private Source(final String name, final ContextParameter parameter) {
         this.name = name;
         this.parameter = parameter;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Source[] method_36794() {
         return new Source[]{THIS, ATTACKING_ENTITY, LAST_DAMAGE_PLAYER, BLOCK_ENTITY};
      }
   }
}
