package net.minecraft.loot;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ErrorReporter;

public record LootDataType(RegistryKey registryKey, Codec codec, Validator validator) {
   public static final LootDataType PREDICATES;
   public static final LootDataType ITEM_MODIFIERS;
   public static final LootDataType LOOT_TABLES;

   public LootDataType(RegistryKey registryKey, Codec codec, Validator validator) {
      this.registryKey = registryKey;
      this.codec = codec;
      this.validator = validator;
   }

   public void validate(LootTableReporter reporter, RegistryKey key, Object value) {
      this.validator.run(reporter, key, value);
   }

   public static Stream stream() {
      return Stream.of(PREDICATES, ITEM_MODIFIERS, LOOT_TABLES);
   }

   private static Validator simpleValidator() {
      return (reporter, key, value) -> {
         value.validate(reporter.makeChild(new ErrorReporter.LootTableContext(key), key));
      };
   }

   private static Validator tableValidator() {
      return (reporter, key, value) -> {
         value.validate(reporter.withContextType(value.getType()).makeChild(new ErrorReporter.LootTableContext(key), key));
      };
   }

   public RegistryKey registryKey() {
      return this.registryKey;
   }

   public Codec codec() {
      return this.codec;
   }

   public Validator validator() {
      return this.validator;
   }

   static {
      PREDICATES = new LootDataType(RegistryKeys.PREDICATE, LootCondition.CODEC, simpleValidator());
      ITEM_MODIFIERS = new LootDataType(RegistryKeys.ITEM_MODIFIER, LootFunctionTypes.CODEC, simpleValidator());
      LOOT_TABLES = new LootDataType(RegistryKeys.LOOT_TABLE, LootTable.CODEC, tableValidator());
   }

   @FunctionalInterface
   public interface Validator {
      void run(LootTableReporter reporter, RegistryKey key, Object value);
   }
}
