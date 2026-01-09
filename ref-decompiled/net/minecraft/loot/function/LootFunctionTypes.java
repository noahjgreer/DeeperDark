package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.Identifier;

public class LootFunctionTypes {
   public static final BiFunction NOOP = (stack, context) -> {
      return stack;
   };
   public static final Codec BASE_CODEC;
   public static final Codec CODEC;
   public static final Codec ENTRY_CODEC;
   public static final LootFunctionType SET_COUNT;
   public static final LootFunctionType SET_ITEM;
   public static final LootFunctionType ENCHANT_WITH_LEVELS;
   public static final LootFunctionType ENCHANT_RANDOMLY;
   public static final LootFunctionType SET_ENCHANTMENTS;
   public static final LootFunctionType SET_CUSTOM_DATA;
   public static final LootFunctionType SET_COMPONENTS;
   public static final LootFunctionType FURNACE_SMELT;
   public static final LootFunctionType ENCHANTED_COUNT_INCREASE;
   public static final LootFunctionType SET_DAMAGE;
   public static final LootFunctionType SET_ATTRIBUTES;
   public static final LootFunctionType SET_NAME;
   public static final LootFunctionType EXPLORATION_MAP;
   public static final LootFunctionType SET_STEW_EFFECT;
   public static final LootFunctionType COPY_NAME;
   public static final LootFunctionType SET_CONTENTS;
   public static final LootFunctionType MODIFY_CONTENTS;
   public static final LootFunctionType FILTERED;
   public static final LootFunctionType LIMIT_COUNT;
   public static final LootFunctionType APPLY_BONUS;
   public static final LootFunctionType SET_LOOT_TABLE;
   public static final LootFunctionType EXPLOSION_DECAY;
   public static final LootFunctionType SET_LORE;
   public static final LootFunctionType FILL_PLAYER_HEAD;
   public static final LootFunctionType COPY_CUSTOM_DATA;
   public static final LootFunctionType COPY_STATE;
   public static final LootFunctionType SET_BANNER_PATTERN;
   public static final LootFunctionType SET_POTION;
   public static final LootFunctionType SET_INSTRUMENT;
   public static final LootFunctionType REFERENCE;
   public static final LootFunctionType SEQUENCE;
   public static final LootFunctionType COPY_COMPONENTS;
   public static final LootFunctionType SET_FIREWORKS;
   public static final LootFunctionType SET_FIREWORK_EXPLOSION;
   public static final LootFunctionType SET_BOOK_COVER;
   public static final LootFunctionType SET_WRITTEN_BOOK_PAGES;
   public static final LootFunctionType SET_WRITABLE_BOOK_PAGES;
   public static final LootFunctionType TOGGLE_TOOLTIPS;
   public static final LootFunctionType SET_OMINOUS_BOTTLE_AMPLIFIER;
   public static final LootFunctionType SET_CUSTOM_MODEL_DATA;

   private static LootFunctionType register(String id, MapCodec codec) {
      return (LootFunctionType)Registry.register(Registries.LOOT_FUNCTION_TYPE, (Identifier)Identifier.ofVanilla(id), new LootFunctionType(codec));
   }

   public static BiFunction join(List terms) {
      List list = List.copyOf(terms);
      BiFunction var10000;
      switch (list.size()) {
         case 0:
            var10000 = NOOP;
            break;
         case 1:
            var10000 = (BiFunction)list.get(0);
            break;
         case 2:
            BiFunction biFunction = (BiFunction)list.get(0);
            BiFunction biFunction2 = (BiFunction)list.get(1);
            var10000 = (stack, context) -> {
               return (ItemStack)biFunction2.apply((ItemStack)biFunction.apply(stack, context), context);
            };
            break;
         default:
            var10000 = (stack, context) -> {
               BiFunction biFunction;
               for(Iterator var3 = list.iterator(); var3.hasNext(); stack = (ItemStack)biFunction.apply(stack, context)) {
                  biFunction = (BiFunction)var3.next();
               }

               return stack;
            };
      }

      return var10000;
   }

   static {
      BASE_CODEC = Registries.LOOT_FUNCTION_TYPE.getCodec().dispatch("function", LootFunction::getType, LootFunctionType::codec);
      CODEC = Codec.lazyInitialized(() -> {
         return Codec.withAlternative(BASE_CODEC, AndLootFunction.INLINE_CODEC);
      });
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.ITEM_MODIFIER, CODEC);
      SET_COUNT = register("set_count", SetCountLootFunction.CODEC);
      SET_ITEM = register("set_item", SetItemLootFunction.CODEC);
      ENCHANT_WITH_LEVELS = register("enchant_with_levels", EnchantWithLevelsLootFunction.CODEC);
      ENCHANT_RANDOMLY = register("enchant_randomly", EnchantRandomlyLootFunction.CODEC);
      SET_ENCHANTMENTS = register("set_enchantments", SetEnchantmentsLootFunction.CODEC);
      SET_CUSTOM_DATA = register("set_custom_data", SetCustomDataLootFunction.CODEC);
      SET_COMPONENTS = register("set_components", SetComponentsLootFunction.CODEC);
      FURNACE_SMELT = register("furnace_smelt", FurnaceSmeltLootFunction.CODEC);
      ENCHANTED_COUNT_INCREASE = register("enchanted_count_increase", EnchantedCountIncreaseLootFunction.CODEC);
      SET_DAMAGE = register("set_damage", SetDamageLootFunction.CODEC);
      SET_ATTRIBUTES = register("set_attributes", SetAttributesLootFunction.CODEC);
      SET_NAME = register("set_name", SetNameLootFunction.CODEC);
      EXPLORATION_MAP = register("exploration_map", ExplorationMapLootFunction.CODEC);
      SET_STEW_EFFECT = register("set_stew_effect", SetStewEffectLootFunction.CODEC);
      COPY_NAME = register("copy_name", CopyNameLootFunction.CODEC);
      SET_CONTENTS = register("set_contents", SetContentsLootFunction.CODEC);
      MODIFY_CONTENTS = register("modify_contents", ModifyContentsLootFunction.CODEC);
      FILTERED = register("filtered", FilteredLootFunction.CODEC);
      LIMIT_COUNT = register("limit_count", LimitCountLootFunction.CODEC);
      APPLY_BONUS = register("apply_bonus", ApplyBonusLootFunction.CODEC);
      SET_LOOT_TABLE = register("set_loot_table", SetLootTableLootFunction.CODEC);
      EXPLOSION_DECAY = register("explosion_decay", ExplosionDecayLootFunction.CODEC);
      SET_LORE = register("set_lore", SetLoreLootFunction.CODEC);
      FILL_PLAYER_HEAD = register("fill_player_head", FillPlayerHeadLootFunction.CODEC);
      COPY_CUSTOM_DATA = register("copy_custom_data", CopyNbtLootFunction.CODEC);
      COPY_STATE = register("copy_state", CopyStateLootFunction.CODEC);
      SET_BANNER_PATTERN = register("set_banner_pattern", SetBannerPatternLootFunction.CODEC);
      SET_POTION = register("set_potion", SetPotionLootFunction.CODEC);
      SET_INSTRUMENT = register("set_instrument", SetInstrumentLootFunction.CODEC);
      REFERENCE = register("reference", ReferenceLootFunction.CODEC);
      SEQUENCE = register("sequence", AndLootFunction.CODEC);
      COPY_COMPONENTS = register("copy_components", CopyComponentsLootFunction.CODEC);
      SET_FIREWORKS = register("set_fireworks", SetFireworksLootFunction.CODEC);
      SET_FIREWORK_EXPLOSION = register("set_firework_explosion", SetFireworkExplosionLootFunction.CODEC);
      SET_BOOK_COVER = register("set_book_cover", SetBookCoverLootFunction.CODEC);
      SET_WRITTEN_BOOK_PAGES = register("set_written_book_pages", SetWrittenBookPagesLootFunction.CODEC);
      SET_WRITABLE_BOOK_PAGES = register("set_writable_book_pages", SetWritableBookPagesLootFunction.CODEC);
      TOGGLE_TOOLTIPS = register("toggle_tooltips", ToggleTooltipsLootFunction.CODEC);
      SET_OMINOUS_BOTTLE_AMPLIFIER = register("set_ominous_bottle_amplifier", SetOminousBottleAmplifierLootFunction.CODEC);
      SET_CUSTOM_MODEL_DATA = register("set_custom_model_data", SetCustomModelDataLootFunction.CODEC);
   }
}
