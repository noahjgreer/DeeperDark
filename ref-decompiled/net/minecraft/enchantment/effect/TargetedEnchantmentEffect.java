package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.context.ContextType;

public record TargetedEnchantmentEffect(EnchantmentEffectTarget enchanted, EnchantmentEffectTarget affected, Object effect, Optional requirements) {
   public TargetedEnchantmentEffect(EnchantmentEffectTarget enchantmentEffectTarget, EnchantmentEffectTarget enchantmentEffectTarget2, Object object, Optional optional) {
      this.enchanted = enchantmentEffectTarget;
      this.affected = enchantmentEffectTarget2;
      this.effect = object;
      this.requirements = optional;
   }

   public static Codec createPostAttackCodec(Codec effectCodec, ContextType lootContextType) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(EnchantmentEffectTarget.CODEC.fieldOf("enchanted").forGetter(TargetedEnchantmentEffect::enchanted), EnchantmentEffectTarget.CODEC.fieldOf("affected").forGetter(TargetedEnchantmentEffect::affected), effectCodec.fieldOf("effect").forGetter(TargetedEnchantmentEffect::effect), EnchantmentEffectEntry.createRequirementsCodec(lootContextType).optionalFieldOf("requirements").forGetter(TargetedEnchantmentEffect::requirements)).apply(instance, TargetedEnchantmentEffect::new);
      });
   }

   public static Codec createEquipmentDropsCodec(Codec effectCodec, ContextType lootContextType) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(EnchantmentEffectTarget.CODEC.validate((enchanted) -> {
            return enchanted != EnchantmentEffectTarget.DAMAGING_ENTITY ? DataResult.success(enchanted) : DataResult.error(() -> {
               return "enchanted must be attacker or victim";
            });
         }).fieldOf("enchanted").forGetter(TargetedEnchantmentEffect::enchanted), effectCodec.fieldOf("effect").forGetter(TargetedEnchantmentEffect::effect), EnchantmentEffectEntry.createRequirementsCodec(lootContextType).optionalFieldOf("requirements").forGetter(TargetedEnchantmentEffect::requirements)).apply(instance, (enchantedx, effect, requirements) -> {
            return new TargetedEnchantmentEffect(enchantedx, EnchantmentEffectTarget.VICTIM, effect, requirements);
         });
      });
   }

   public boolean test(LootContext lootContext) {
      return this.requirements.isEmpty() ? true : ((LootCondition)this.requirements.get()).test(lootContext);
   }

   public EnchantmentEffectTarget enchanted() {
      return this.enchanted;
   }

   public EnchantmentEffectTarget affected() {
      return this.affected;
   }

   public Object effect() {
      return this.effect;
   }

   public Optional requirements() {
      return this.requirements;
   }
}
