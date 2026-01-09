package net.minecraft.item;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class SmithingTemplateItem extends Item {
   private static final Formatting TITLE_FORMATTING;
   private static final Formatting DESCRIPTION_FORMATTING;
   private static final Text INGREDIENTS_TEXT;
   private static final Text APPLIES_TO_TEXT;
   private static final Text SMITHING_TEMPLATE_TEXT;
   private static final Text ARMOR_TRIM_APPLIES_TO_TEXT;
   private static final Text ARMOR_TRIM_INGREDIENTS_TEXT;
   private static final Text ARMOR_TRIM_BASE_SLOT_DESCRIPTION_TEXT;
   private static final Text ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION_TEXT;
   private static final Text NETHERITE_UPGRADE_APPLIES_TO_TEXT;
   private static final Text NETHERITE_UPGRADE_INGREDIENTS_TEXT;
   private static final Text NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION_TEXT;
   private static final Text NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION_TEXT;
   private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE;
   private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE;
   private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE;
   private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE;
   private static final Identifier EMPTY_SLOT_HOE_TEXTURE;
   private static final Identifier EMPTY_SLOT_AXE_TEXTURE;
   private static final Identifier EMPTY_SLOT_SWORD_TEXTURE;
   private static final Identifier EMPTY_SLOT_SHOVEL_TEXTURE;
   private static final Identifier EMPTY_SLOT_PICKAXE_TEXTURE;
   private static final Identifier EMPTY_SLOT_INGOT_TEXTURE;
   private static final Identifier EMPTY_SLOT_REDSTONE_DUST_TEXTURE;
   private static final Identifier EMPTY_SLOT_QUARTZ_TEXTURE;
   private static final Identifier EMPTY_SLOT_EMERALD_TEXTURE;
   private static final Identifier EMPTY_SLOT_DIAMOND_TEXTURE;
   private static final Identifier EMPTY_SLOT_LAPIS_LAZULI_TEXTURE;
   private static final Identifier EMPTY_SLOT_AMETHYST_SHARD_TEXTURE;
   private final Text appliesToText;
   private final Text ingredientsText;
   private final Text baseSlotDescriptionText;
   private final Text additionsSlotDescriptionText;
   private final List emptyBaseSlotTextures;
   private final List emptyAdditionsSlotTextures;

   public SmithingTemplateItem(Text appliesToText, Text ingredientsText, Text baseSlotDescriptionText, Text additionsSlotDescriptionText, List emptyBaseSlotTextures, List emptyAdditionsSlotTextures, Item.Settings settings) {
      super(settings);
      this.appliesToText = appliesToText;
      this.ingredientsText = ingredientsText;
      this.baseSlotDescriptionText = baseSlotDescriptionText;
      this.additionsSlotDescriptionText = additionsSlotDescriptionText;
      this.emptyBaseSlotTextures = emptyBaseSlotTextures;
      this.emptyAdditionsSlotTextures = emptyAdditionsSlotTextures;
   }

   public static SmithingTemplateItem of(Item.Settings settings) {
      return new SmithingTemplateItem(ARMOR_TRIM_APPLIES_TO_TEXT, ARMOR_TRIM_INGREDIENTS_TEXT, ARMOR_TRIM_BASE_SLOT_DESCRIPTION_TEXT, ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION_TEXT, getArmorTrimEmptyBaseSlotTextures(), getArmorTrimEmptyAdditionsSlotTextures(), settings);
   }

   public static SmithingTemplateItem createNetheriteUpgrade(Item.Settings settings) {
      return new SmithingTemplateItem(NETHERITE_UPGRADE_APPLIES_TO_TEXT, NETHERITE_UPGRADE_INGREDIENTS_TEXT, NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION_TEXT, NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION_TEXT, getNetheriteUpgradeEmptyBaseSlotTextures(), getNetheriteUpgradeEmptyAdditionsSlotTextures(), settings);
   }

   private static List getArmorTrimEmptyBaseSlotTextures() {
      return List.of(EMPTY_ARMOR_SLOT_HELMET_TEXTURE, EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE, EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE, EMPTY_ARMOR_SLOT_BOOTS_TEXTURE);
   }

   private static List getArmorTrimEmptyAdditionsSlotTextures() {
      return List.of(EMPTY_SLOT_INGOT_TEXTURE, EMPTY_SLOT_REDSTONE_DUST_TEXTURE, EMPTY_SLOT_LAPIS_LAZULI_TEXTURE, EMPTY_SLOT_QUARTZ_TEXTURE, EMPTY_SLOT_DIAMOND_TEXTURE, EMPTY_SLOT_EMERALD_TEXTURE, EMPTY_SLOT_AMETHYST_SHARD_TEXTURE);
   }

   private static List getNetheriteUpgradeEmptyBaseSlotTextures() {
      return List.of(EMPTY_ARMOR_SLOT_HELMET_TEXTURE, EMPTY_SLOT_SWORD_TEXTURE, EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE, EMPTY_SLOT_PICKAXE_TEXTURE, EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE, EMPTY_SLOT_AXE_TEXTURE, EMPTY_ARMOR_SLOT_BOOTS_TEXTURE, EMPTY_SLOT_HOE_TEXTURE, EMPTY_SLOT_SHOVEL_TEXTURE);
   }

   private static List getNetheriteUpgradeEmptyAdditionsSlotTextures() {
      return List.of(EMPTY_SLOT_INGOT_TEXTURE);
   }

   public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer textConsumer, TooltipType type) {
      textConsumer.accept(SMITHING_TEMPLATE_TEXT);
      textConsumer.accept(ScreenTexts.EMPTY);
      textConsumer.accept(APPLIES_TO_TEXT);
      textConsumer.accept(ScreenTexts.space().append(this.appliesToText));
      textConsumer.accept(INGREDIENTS_TEXT);
      textConsumer.accept(ScreenTexts.space().append(this.ingredientsText));
   }

   public Text getBaseSlotDescription() {
      return this.baseSlotDescriptionText;
   }

   public Text getAdditionsSlotDescription() {
      return this.additionsSlotDescriptionText;
   }

   public List getEmptyBaseSlotTextures() {
      return this.emptyBaseSlotTextures;
   }

   public List getEmptyAdditionsSlotTextures() {
      return this.emptyAdditionsSlotTextures;
   }

   static {
      TITLE_FORMATTING = Formatting.GRAY;
      DESCRIPTION_FORMATTING = Formatting.BLUE;
      INGREDIENTS_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.ingredients"))).formatted(TITLE_FORMATTING);
      APPLIES_TO_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.applies_to"))).formatted(TITLE_FORMATTING);
      SMITHING_TEMPLATE_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template"))).formatted(TITLE_FORMATTING);
      ARMOR_TRIM_APPLIES_TO_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.applies_to"))).formatted(DESCRIPTION_FORMATTING);
      ARMOR_TRIM_INGREDIENTS_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.ingredients"))).formatted(DESCRIPTION_FORMATTING);
      ARMOR_TRIM_BASE_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.base_slot_description")));
      ARMOR_TRIM_ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.armor_trim.additions_slot_description")));
      NETHERITE_UPGRADE_APPLIES_TO_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.applies_to"))).formatted(DESCRIPTION_FORMATTING);
      NETHERITE_UPGRADE_INGREDIENTS_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.ingredients"))).formatted(DESCRIPTION_FORMATTING);
      NETHERITE_UPGRADE_BASE_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.base_slot_description")));
      NETHERITE_UPGRADE_ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.netherite_upgrade.additions_slot_description")));
      EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.ofVanilla("container/slot/helmet");
      EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.ofVanilla("container/slot/chestplate");
      EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.ofVanilla("container/slot/leggings");
      EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.ofVanilla("container/slot/boots");
      EMPTY_SLOT_HOE_TEXTURE = Identifier.ofVanilla("container/slot/hoe");
      EMPTY_SLOT_AXE_TEXTURE = Identifier.ofVanilla("container/slot/axe");
      EMPTY_SLOT_SWORD_TEXTURE = Identifier.ofVanilla("container/slot/sword");
      EMPTY_SLOT_SHOVEL_TEXTURE = Identifier.ofVanilla("container/slot/shovel");
      EMPTY_SLOT_PICKAXE_TEXTURE = Identifier.ofVanilla("container/slot/pickaxe");
      EMPTY_SLOT_INGOT_TEXTURE = Identifier.ofVanilla("container/slot/ingot");
      EMPTY_SLOT_REDSTONE_DUST_TEXTURE = Identifier.ofVanilla("container/slot/redstone_dust");
      EMPTY_SLOT_QUARTZ_TEXTURE = Identifier.ofVanilla("container/slot/quartz");
      EMPTY_SLOT_EMERALD_TEXTURE = Identifier.ofVanilla("container/slot/emerald");
      EMPTY_SLOT_DIAMOND_TEXTURE = Identifier.ofVanilla("container/slot/diamond");
      EMPTY_SLOT_LAPIS_LAZULI_TEXTURE = Identifier.ofVanilla("container/slot/lapis_lazuli");
      EMPTY_SLOT_AMETHYST_SHARD_TEXTURE = Identifier.ofVanilla("container/slot/amethyst_shard");
   }
}
