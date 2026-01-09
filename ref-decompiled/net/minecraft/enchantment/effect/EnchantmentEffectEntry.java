package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.context.ContextType;

public record EnchantmentEffectEntry(Object effect, Optional requirements) {
   public EnchantmentEffectEntry(Object object, Optional optional) {
      this.effect = object;
      this.requirements = optional;
   }

   public static Codec createRequirementsCodec(ContextType lootContextType) {
      return LootCondition.CODEC.validate((condition) -> {
         ErrorReporter.Impl impl = new ErrorReporter.Impl();
         LootTableReporter lootTableReporter = new LootTableReporter(impl, lootContextType);
         condition.validate(lootTableReporter);
         return !impl.isEmpty() ? DataResult.error(() -> {
            return "Validation error in enchantment effect condition: " + impl.getErrorsAsString();
         }) : DataResult.success(condition);
      });
   }

   public static Codec createCodec(Codec effectCodec, ContextType lootContextType) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(effectCodec.fieldOf("effect").forGetter(EnchantmentEffectEntry::effect), createRequirementsCodec(lootContextType).optionalFieldOf("requirements").forGetter(EnchantmentEffectEntry::requirements)).apply(instance, EnchantmentEffectEntry::new);
      });
   }

   public boolean test(LootContext context) {
      return this.requirements.isEmpty() ? true : ((LootCondition)this.requirements.get()).test(context);
   }

   public Object effect() {
      return this.effect;
   }

   public Optional requirements() {
      return this.requirements;
   }
}
