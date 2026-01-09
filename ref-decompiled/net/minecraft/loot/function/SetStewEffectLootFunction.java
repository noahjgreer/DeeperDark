package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;

public class SetStewEffectLootFunction extends ConditionalLootFunction {
   private static final Codec STEW_EFFECT_LIST_CODEC;
   public static final MapCodec CODEC;
   private final List stewEffects;

   SetStewEffectLootFunction(List conditions, List stewEffects) {
      super(conditions);
      this.stewEffects = stewEffects;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_STEW_EFFECT;
   }

   public Set getAllowedParameters() {
      return (Set)this.stewEffects.stream().flatMap((stewEffect) -> {
         return stewEffect.duration().getAllowedParameters().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      if (stack.isOf(Items.SUSPICIOUS_STEW) && !this.stewEffects.isEmpty()) {
         StewEffect stewEffect = (StewEffect)Util.getRandom(this.stewEffects, context.getRandom());
         RegistryEntry registryEntry = stewEffect.effect();
         int i = stewEffect.duration().nextInt(context);
         if (!((StatusEffect)registryEntry.value()).isInstant()) {
            i *= 20;
         }

         SuspiciousStewEffectsComponent.StewEffect stewEffect2 = new SuspiciousStewEffectsComponent.StewEffect(registryEntry, i);
         stack.apply(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT, stewEffect2, SuspiciousStewEffectsComponent::with);
         return stack;
      } else {
         return stack;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   static {
      STEW_EFFECT_LIST_CODEC = SetStewEffectLootFunction.StewEffect.CODEC.listOf().validate((stewEffects) -> {
         Set set = new ObjectOpenHashSet();
         Iterator var2 = stewEffects.iterator();

         StewEffect stewEffect;
         do {
            if (!var2.hasNext()) {
               return DataResult.success(stewEffects);
            }

            stewEffect = (StewEffect)var2.next();
         } while(set.add(stewEffect.effect()));

         return DataResult.error(() -> {
            return "Encountered duplicate mob effect: '" + String.valueOf(stewEffect.effect()) + "'";
         });
      });
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return addConditionsField(instance).and(STEW_EFFECT_LIST_CODEC.optionalFieldOf("effects", List.of()).forGetter((function) -> {
            return function.stewEffects;
         })).apply(instance, SetStewEffectLootFunction::new);
      });
   }

   static record StewEffect(RegistryEntry effect, LootNumberProvider duration) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(StatusEffect.ENTRY_CODEC.fieldOf("type").forGetter(StewEffect::effect), LootNumberProviderTypes.CODEC.fieldOf("duration").forGetter(StewEffect::duration)).apply(instance, StewEffect::new);
      });

      StewEffect(RegistryEntry registryEntry, LootNumberProvider lootNumberProvider) {
         this.effect = registryEntry;
         this.duration = lootNumberProvider;
      }

      public RegistryEntry effect() {
         return this.effect;
      }

      public LootNumberProvider duration() {
         return this.duration;
      }
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final ImmutableList.Builder map = ImmutableList.builder();

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder withEffect(RegistryEntry effect, LootNumberProvider durationRange) {
         this.map.add(new StewEffect(effect, durationRange));
         return this;
      }

      public LootFunction build() {
         return new SetStewEffectLootFunction(this.getConditions(), this.map.build());
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
