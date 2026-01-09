package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public class ApplyBonusLootFunction extends ConditionalLootFunction {
   private static final Map FACTORIES;
   private static final Codec TYPE_CODEC;
   private static final MapCodec FORMULA_CODEC;
   public static final MapCodec CODEC;
   private final RegistryEntry enchantment;
   private final Formula formula;

   private ApplyBonusLootFunction(List conditions, RegistryEntry enchantment, Formula formula) {
      super(conditions);
      this.enchantment = enchantment;
      this.formula = formula;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.APPLY_BONUS;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.TOOL);
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      ItemStack itemStack = (ItemStack)context.get(LootContextParameters.TOOL);
      if (itemStack != null) {
         int i = EnchantmentHelper.getLevel(this.enchantment, itemStack);
         int j = this.formula.getValue(context.getRandom(), stack.getCount(), i);
         stack.setCount(j);
      }

      return stack;
   }

   public static ConditionalLootFunction.Builder binomialWithBonusCount(RegistryEntry enchantment, float probability, int extra) {
      return builder((conditions) -> {
         return new ApplyBonusLootFunction(conditions, enchantment, new BinomialWithBonusCount(extra, probability));
      });
   }

   public static ConditionalLootFunction.Builder oreDrops(RegistryEntry enchantment) {
      return builder((conditions) -> {
         return new ApplyBonusLootFunction(conditions, enchantment, new OreDrops());
      });
   }

   public static ConditionalLootFunction.Builder uniformBonusCount(RegistryEntry enchantment) {
      return builder((conditions) -> {
         return new ApplyBonusLootFunction(conditions, enchantment, new UniformBonusCount(1));
      });
   }

   public static ConditionalLootFunction.Builder uniformBonusCount(RegistryEntry enchantment, int bonusMultiplier) {
      return builder((conditions) -> {
         return new ApplyBonusLootFunction(conditions, enchantment, new UniformBonusCount(bonusMultiplier));
      });
   }

   static {
      FACTORIES = (Map)Stream.of(ApplyBonusLootFunction.BinomialWithBonusCount.TYPE, ApplyBonusLootFunction.OreDrops.TYPE, ApplyBonusLootFunction.UniformBonusCount.TYPE).collect(Collectors.toMap(Type::id, Function.identity()));
      TYPE_CODEC = Identifier.CODEC.comapFlatMap((id) -> {
         Type type = (Type)FACTORIES.get(id);
         return type != null ? DataResult.success(type) : DataResult.error(() -> {
            return "No formula type with id: '" + String.valueOf(id) + "'";
         });
      }, Type::id);
      FORMULA_CODEC = Codecs.parameters("formula", "parameters", TYPE_CODEC, Formula::getType, Type::codec);
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return addConditionsField(instance).and(instance.group(Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter((function) -> {
            return function.enchantment;
         }), FORMULA_CODEC.forGetter((function) -> {
            return function.formula;
         }))).apply(instance, ApplyBonusLootFunction::new);
      });
   }

   private interface Formula {
      int getValue(Random random, int initialCount, int enchantmentLevel);

      Type getType();
   }

   private static record UniformBonusCount(int bonusMultiplier) implements Formula {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.INT.fieldOf("bonusMultiplier").forGetter(UniformBonusCount::bonusMultiplier)).apply(instance, UniformBonusCount::new);
      });
      public static final Type TYPE;

      UniformBonusCount(int bonusMultiplier) {
         this.bonusMultiplier = bonusMultiplier;
      }

      public int getValue(Random random, int initialCount, int enchantmentLevel) {
         return initialCount + random.nextInt(this.bonusMultiplier * enchantmentLevel + 1);
      }

      public Type getType() {
         return TYPE;
      }

      public int bonusMultiplier() {
         return this.bonusMultiplier;
      }

      static {
         TYPE = new Type(Identifier.ofVanilla("uniform_bonus_count"), CODEC);
      }
   }

   private static record OreDrops() implements Formula {
      public static final Codec CODEC = Codec.unit(OreDrops::new);
      public static final Type TYPE;

      OreDrops() {
      }

      public int getValue(Random random, int initialCount, int enchantmentLevel) {
         if (enchantmentLevel > 0) {
            int i = random.nextInt(enchantmentLevel + 2) - 1;
            if (i < 0) {
               i = 0;
            }

            return initialCount * (i + 1);
         } else {
            return initialCount;
         }
      }

      public Type getType() {
         return TYPE;
      }

      static {
         TYPE = new Type(Identifier.ofVanilla("ore_drops"), CODEC);
      }
   }

   private static record BinomialWithBonusCount(int extra, float probability) implements Formula {
      private static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.INT.fieldOf("extra").forGetter(BinomialWithBonusCount::extra), Codec.FLOAT.fieldOf("probability").forGetter(BinomialWithBonusCount::probability)).apply(instance, BinomialWithBonusCount::new);
      });
      public static final Type TYPE;

      BinomialWithBonusCount(int extra, float probability) {
         this.extra = extra;
         this.probability = probability;
      }

      public int getValue(Random random, int initialCount, int enchantmentLevel) {
         for(int i = 0; i < enchantmentLevel + this.extra; ++i) {
            if (random.nextFloat() < this.probability) {
               ++initialCount;
            }
         }

         return initialCount;
      }

      public Type getType() {
         return TYPE;
      }

      public int extra() {
         return this.extra;
      }

      public float probability() {
         return this.probability;
      }

      static {
         TYPE = new Type(Identifier.ofVanilla("binomial_with_bonus_count"), CODEC);
      }
   }

   private static record Type(Identifier id, Codec codec) {
      Type(Identifier identifier, Codec codec) {
         this.id = identifier;
         this.codec = codec;
      }

      public Identifier id() {
         return this.id;
      }

      public Codec codec() {
         return this.codec;
      }
   }
}
