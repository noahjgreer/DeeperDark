package net.minecraft.component;

import com.mojang.serialization.Codec;
import java.util.function.UnaryOperator;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.BeesComponent;
import net.minecraft.component.type.BlockPredicatesComponent;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.component.type.DebugStickStateComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.InstrumentComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.MapPostProcessingComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.OminousBottleAmplifierComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.ProvidesTrimMaterialComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigVariant;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.WolfSoundVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.CodecCache;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.village.VillagerType;

public class DataComponentTypes {
   static final CodecCache CACHE = new CodecCache(512);
   public static final ComponentType CUSTOM_DATA = register("custom_data", (builder) -> {
      return builder.codec(NbtComponent.CODEC);
   });
   public static final ComponentType MAX_STACK_SIZE = register("max_stack_size", (builder) -> {
      return builder.codec(Codecs.rangedInt(1, 99)).packetCodec(PacketCodecs.VAR_INT);
   });
   public static final ComponentType MAX_DAMAGE = register("max_damage", (builder) -> {
      return builder.codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.VAR_INT);
   });
   public static final ComponentType DAMAGE = register("damage", (builder) -> {
      return builder.codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT);
   });
   public static final ComponentType UNBREAKABLE = register("unbreakable", (builder) -> {
      return builder.codec(Unit.CODEC).packetCodec(Unit.PACKET_CODEC);
   });
   public static final ComponentType CUSTOM_NAME = register("custom_name", (builder) -> {
      return builder.codec(TextCodecs.CODEC).packetCodec(TextCodecs.REGISTRY_PACKET_CODEC).cache();
   });
   public static final ComponentType ITEM_NAME = register("item_name", (builder) -> {
      return builder.codec(TextCodecs.CODEC).packetCodec(TextCodecs.REGISTRY_PACKET_CODEC).cache();
   });
   public static final ComponentType ITEM_MODEL = register("item_model", (builder) -> {
      return builder.codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC).cache();
   });
   public static final ComponentType LORE = register("lore", (builder) -> {
      return builder.codec(LoreComponent.CODEC).packetCodec(LoreComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType RARITY = register("rarity", (builder) -> {
      return builder.codec(Rarity.CODEC).packetCodec(Rarity.PACKET_CODEC);
   });
   public static final ComponentType ENCHANTMENTS = register("enchantments", (builder) -> {
      return builder.codec(ItemEnchantmentsComponent.CODEC).packetCodec(ItemEnchantmentsComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType CAN_PLACE_ON = register("can_place_on", (builder) -> {
      return builder.codec(BlockPredicatesComponent.CODEC).packetCodec(BlockPredicatesComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType CAN_BREAK = register("can_break", (builder) -> {
      return builder.codec(BlockPredicatesComponent.CODEC).packetCodec(BlockPredicatesComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType ATTRIBUTE_MODIFIERS = register("attribute_modifiers", (builder) -> {
      return builder.codec(AttributeModifiersComponent.CODEC).packetCodec(AttributeModifiersComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType CUSTOM_MODEL_DATA = register("custom_model_data", (builder) -> {
      return builder.codec(CustomModelDataComponent.CODEC).packetCodec(CustomModelDataComponent.PACKET_CODEC);
   });
   public static final ComponentType TOOLTIP_DISPLAY = register("tooltip_display", (builder) -> {
      return builder.codec(TooltipDisplayComponent.CODEC).packetCodec(TooltipDisplayComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType REPAIR_COST = register("repair_cost", (builder) -> {
      return builder.codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT);
   });
   public static final ComponentType CREATIVE_SLOT_LOCK = register("creative_slot_lock", (builder) -> {
      return builder.packetCodec(Unit.PACKET_CODEC);
   });
   public static final ComponentType ENCHANTMENT_GLINT_OVERRIDE = register("enchantment_glint_override", (builder) -> {
      return builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN);
   });
   public static final ComponentType INTANGIBLE_PROJECTILE = register("intangible_projectile", (builder) -> {
      return builder.codec(Unit.CODEC);
   });
   public static final ComponentType FOOD = register("food", (builder) -> {
      return builder.codec(FoodComponent.CODEC).packetCodec(FoodComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType CONSUMABLE = register("consumable", (builder) -> {
      return builder.codec(ConsumableComponent.CODEC).packetCodec(ConsumableComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType USE_REMAINDER = register("use_remainder", (builder) -> {
      return builder.codec(UseRemainderComponent.CODEC).packetCodec(UseRemainderComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType USE_COOLDOWN = register("use_cooldown", (builder) -> {
      return builder.codec(UseCooldownComponent.CODEC).packetCodec(UseCooldownComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType DAMAGE_RESISTANT = register("damage_resistant", (builder) -> {
      return builder.codec(DamageResistantComponent.CODEC).packetCodec(DamageResistantComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType TOOL = register("tool", (builder) -> {
      return builder.codec(ToolComponent.CODEC).packetCodec(ToolComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType WEAPON = register("weapon", (builder) -> {
      return builder.codec(WeaponComponent.CODEC).packetCodec(WeaponComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType ENCHANTABLE = register("enchantable", (builder) -> {
      return builder.codec(EnchantableComponent.CODEC).packetCodec(EnchantableComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType EQUIPPABLE = register("equippable", (builder) -> {
      return builder.codec(EquippableComponent.CODEC).packetCodec(EquippableComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType REPAIRABLE = register("repairable", (builder) -> {
      return builder.codec(RepairableComponent.CODEC).packetCodec(RepairableComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType GLIDER = register("glider", (builder) -> {
      return builder.codec(Unit.CODEC).packetCodec(Unit.PACKET_CODEC);
   });
   public static final ComponentType TOOLTIP_STYLE = register("tooltip_style", (builder) -> {
      return builder.codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC).cache();
   });
   public static final ComponentType DEATH_PROTECTION = register("death_protection", (builder) -> {
      return builder.codec(DeathProtectionComponent.CODEC).packetCodec(DeathProtectionComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType BLOCKS_ATTACKS = register("blocks_attacks", (builder) -> {
      return builder.codec(BlocksAttacksComponent.CODEC).packetCodec(BlocksAttacksComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType STORED_ENCHANTMENTS = register("stored_enchantments", (builder) -> {
      return builder.codec(ItemEnchantmentsComponent.CODEC).packetCodec(ItemEnchantmentsComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType DYED_COLOR = register("dyed_color", (builder) -> {
      return builder.codec(DyedColorComponent.CODEC).packetCodec(DyedColorComponent.PACKET_CODEC);
   });
   public static final ComponentType MAP_COLOR = register("map_color", (builder) -> {
      return builder.codec(MapColorComponent.CODEC).packetCodec(MapColorComponent.PACKET_CODEC);
   });
   public static final ComponentType MAP_ID = register("map_id", (builder) -> {
      return builder.codec(MapIdComponent.CODEC).packetCodec(MapIdComponent.PACKET_CODEC);
   });
   public static final ComponentType MAP_DECORATIONS = register("map_decorations", (builder) -> {
      return builder.codec(MapDecorationsComponent.CODEC).cache();
   });
   public static final ComponentType MAP_POST_PROCESSING = register("map_post_processing", (builder) -> {
      return builder.packetCodec(MapPostProcessingComponent.PACKET_CODEC);
   });
   public static final ComponentType CHARGED_PROJECTILES = register("charged_projectiles", (builder) -> {
      return builder.codec(ChargedProjectilesComponent.CODEC).packetCodec(ChargedProjectilesComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType BUNDLE_CONTENTS = register("bundle_contents", (builder) -> {
      return builder.codec(BundleContentsComponent.CODEC).packetCodec(BundleContentsComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType POTION_CONTENTS = register("potion_contents", (builder) -> {
      return builder.codec(PotionContentsComponent.CODEC).packetCodec(PotionContentsComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType POTION_DURATION_SCALE = register("potion_duration_scale", (builder) -> {
      return builder.codec(Codecs.NON_NEGATIVE_FLOAT).packetCodec(PacketCodecs.FLOAT).cache();
   });
   public static final ComponentType SUSPICIOUS_STEW_EFFECTS = register("suspicious_stew_effects", (builder) -> {
      return builder.codec(SuspiciousStewEffectsComponent.CODEC).packetCodec(SuspiciousStewEffectsComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType WRITABLE_BOOK_CONTENT = register("writable_book_content", (builder) -> {
      return builder.codec(WritableBookContentComponent.CODEC).packetCodec(WritableBookContentComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType WRITTEN_BOOK_CONTENT = register("written_book_content", (builder) -> {
      return builder.codec(WrittenBookContentComponent.CODEC).packetCodec(WrittenBookContentComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType TRIM = register("trim", (builder) -> {
      return builder.codec(ArmorTrim.CODEC).packetCodec(ArmorTrim.PACKET_CODEC).cache();
   });
   public static final ComponentType DEBUG_STICK_STATE = register("debug_stick_state", (builder) -> {
      return builder.codec(DebugStickStateComponent.CODEC).cache();
   });
   public static final ComponentType ENTITY_DATA = register("entity_data", (builder) -> {
      return builder.codec(NbtComponent.CODEC_WITH_ID).packetCodec(NbtComponent.PACKET_CODEC);
   });
   public static final ComponentType BUCKET_ENTITY_DATA = register("bucket_entity_data", (builder) -> {
      return builder.codec(NbtComponent.CODEC).packetCodec(NbtComponent.PACKET_CODEC);
   });
   public static final ComponentType BLOCK_ENTITY_DATA = register("block_entity_data", (builder) -> {
      return builder.codec(NbtComponent.CODEC_WITH_ID).packetCodec(NbtComponent.PACKET_CODEC);
   });
   public static final ComponentType INSTRUMENT = register("instrument", (builder) -> {
      return builder.codec(InstrumentComponent.CODEC).packetCodec(InstrumentComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType PROVIDES_TRIM_MATERIAL = register("provides_trim_material", (builder) -> {
      return builder.codec(ProvidesTrimMaterialComponent.CODEC).packetCodec(ProvidesTrimMaterialComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType OMINOUS_BOTTLE_AMPLIFIER = register("ominous_bottle_amplifier", (builder) -> {
      return builder.codec(OminousBottleAmplifierComponent.CODEC).packetCodec(OminousBottleAmplifierComponent.PACKET_CODEC);
   });
   public static final ComponentType JUKEBOX_PLAYABLE = register("jukebox_playable", (builder) -> {
      return builder.codec(JukeboxPlayableComponent.CODEC).packetCodec(JukeboxPlayableComponent.PACKET_CODEC);
   });
   public static final ComponentType PROVIDES_BANNER_PATTERNS = register("provides_banner_patterns", (builder) -> {
      return builder.codec(TagKey.codec(RegistryKeys.BANNER_PATTERN)).packetCodec(TagKey.packetCodec(RegistryKeys.BANNER_PATTERN)).cache();
   });
   public static final ComponentType RECIPES = register("recipes", (builder) -> {
      return builder.codec(Recipe.KEY_CODEC.listOf()).cache();
   });
   public static final ComponentType LODESTONE_TRACKER = register("lodestone_tracker", (builder) -> {
      return builder.codec(LodestoneTrackerComponent.CODEC).packetCodec(LodestoneTrackerComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType FIREWORK_EXPLOSION = register("firework_explosion", (builder) -> {
      return builder.codec(FireworkExplosionComponent.CODEC).packetCodec(FireworkExplosionComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType FIREWORKS = register("fireworks", (builder) -> {
      return builder.codec(FireworksComponent.CODEC).packetCodec(FireworksComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType PROFILE = register("profile", (builder) -> {
      return builder.codec(ProfileComponent.CODEC).packetCodec(ProfileComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType NOTE_BLOCK_SOUND = register("note_block_sound", (builder) -> {
      return builder.codec(Identifier.CODEC).packetCodec(Identifier.PACKET_CODEC);
   });
   public static final ComponentType BANNER_PATTERNS = register("banner_patterns", (builder) -> {
      return builder.codec(BannerPatternsComponent.CODEC).packetCodec(BannerPatternsComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType BASE_COLOR = register("base_color", (builder) -> {
      return builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC);
   });
   public static final ComponentType POT_DECORATIONS = register("pot_decorations", (builder) -> {
      return builder.codec(Sherds.CODEC).packetCodec(Sherds.PACKET_CODEC).cache();
   });
   public static final ComponentType CONTAINER = register("container", (builder) -> {
      return builder.codec(ContainerComponent.CODEC).packetCodec(ContainerComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType BLOCK_STATE = register("block_state", (builder) -> {
      return builder.codec(BlockStateComponent.CODEC).packetCodec(BlockStateComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType BEES = register("bees", (builder) -> {
      return builder.codec(BeesComponent.CODEC).packetCodec(BeesComponent.PACKET_CODEC).cache();
   });
   public static final ComponentType LOCK = register("lock", (builder) -> {
      return builder.codec(ContainerLock.CODEC);
   });
   public static final ComponentType CONTAINER_LOOT = register("container_loot", (builder) -> {
      return builder.codec(ContainerLootComponent.CODEC);
   });
   public static final ComponentType BREAK_SOUND = register("break_sound", (builder) -> {
      return builder.codec(SoundEvent.ENTRY_CODEC).packetCodec(SoundEvent.ENTRY_PACKET_CODEC).cache();
   });
   public static final ComponentType VILLAGER_VARIANT = register("villager/variant", (builder) -> {
      return builder.codec(VillagerType.CODEC).packetCodec(VillagerType.PACKET_CODEC);
   });
   public static final ComponentType WOLF_VARIANT = register("wolf/variant", (builder) -> {
      return builder.codec(WolfVariant.ENTRY_CODEC).packetCodec(WolfVariant.ENTRY_PACKET_CODEC);
   });
   public static final ComponentType WOLF_SOUND_VARIANT = register("wolf/sound_variant", (builder) -> {
      return builder.codec(WolfSoundVariant.ENTRY_CODEC).packetCodec(WolfSoundVariant.PACKET_CODEC);
   });
   public static final ComponentType WOLF_COLLAR = register("wolf/collar", (builder) -> {
      return builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC);
   });
   public static final ComponentType FOX_VARIANT = register("fox/variant", (builder) -> {
      return builder.codec(FoxEntity.Variant.CODEC).packetCodec(FoxEntity.Variant.PACKET_CODEC);
   });
   public static final ComponentType SALMON_SIZE = register("salmon/size", (builder) -> {
      return builder.codec(SalmonEntity.Variant.CODEC).packetCodec(SalmonEntity.Variant.PACKET_CODEC);
   });
   public static final ComponentType PARROT_VARIANT = register("parrot/variant", (builder) -> {
      return builder.codec(ParrotEntity.Variant.CODEC).packetCodec(ParrotEntity.Variant.PACKET_CODEC);
   });
   public static final ComponentType TROPICAL_FISH_PATTERN = register("tropical_fish/pattern", (builder) -> {
      return builder.codec(TropicalFishEntity.Pattern.CODEC).packetCodec(TropicalFishEntity.Pattern.PACKET_CODEC);
   });
   public static final ComponentType TROPICAL_FISH_BASE_COLOR = register("tropical_fish/base_color", (builder) -> {
      return builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC);
   });
   public static final ComponentType TROPICAL_FISH_PATTERN_COLOR = register("tropical_fish/pattern_color", (builder) -> {
      return builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC);
   });
   public static final ComponentType MOOSHROOM_VARIANT = register("mooshroom/variant", (builder) -> {
      return builder.codec(MooshroomEntity.Variant.CODEC).packetCodec(MooshroomEntity.Variant.PACKET_CODEC);
   });
   public static final ComponentType RABBIT_VARIANT = register("rabbit/variant", (builder) -> {
      return builder.codec(RabbitEntity.Variant.CODEC).packetCodec(RabbitEntity.Variant.PACKET_CODEC);
   });
   public static final ComponentType PIG_VARIANT = register("pig/variant", (builder) -> {
      return builder.codec(PigVariant.ENTRY_CODEC).packetCodec(PigVariant.ENTRY_PACKET_CODEC);
   });
   public static final ComponentType COW_VARIANT = register("cow/variant", (builder) -> {
      return builder.codec(CowVariant.ENTRY_CODEC).packetCodec(CowVariant.ENTRY_PACKET_CODEC);
   });
   public static final ComponentType CHICKEN_VARIANT = register("chicken/variant", (builder) -> {
      return builder.codec(LazyRegistryEntryReference.createCodec(RegistryKeys.CHICKEN_VARIANT, ChickenVariant.ENTRY_CODEC)).packetCodec(LazyRegistryEntryReference.createPacketCodec(RegistryKeys.CHICKEN_VARIANT, ChickenVariant.ENTRY_PACKET_CODEC));
   });
   public static final ComponentType FROG_VARIANT = register("frog/variant", (builder) -> {
      return builder.codec(FrogVariant.ENTRY_CODEC).packetCodec(FrogVariant.PACKET_CODEC);
   });
   public static final ComponentType HORSE_VARIANT = register("horse/variant", (builder) -> {
      return builder.codec(HorseColor.CODEC).packetCodec(HorseColor.PACKET_CODEC);
   });
   public static final ComponentType PAINTING_VARIANT = register("painting/variant", (builder) -> {
      return builder.codec(PaintingVariant.ENTRY_CODEC).packetCodec(PaintingVariant.ENTRY_PACKET_CODEC);
   });
   public static final ComponentType LLAMA_VARIANT = register("llama/variant", (builder) -> {
      return builder.codec(LlamaEntity.Variant.CODEC).packetCodec(LlamaEntity.Variant.PACKET_CODEC);
   });
   public static final ComponentType AXOLOTL_VARIANT = register("axolotl/variant", (builder) -> {
      return builder.codec(AxolotlEntity.Variant.CODEC).packetCodec(AxolotlEntity.Variant.PACKET_CODEC);
   });
   public static final ComponentType CAT_VARIANT = register("cat/variant", (builder) -> {
      return builder.codec(CatVariant.ENTRY_CODEC).packetCodec(CatVariant.PACKET_CODEC);
   });
   public static final ComponentType CAT_COLLAR = register("cat/collar", (builder) -> {
      return builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC);
   });
   public static final ComponentType SHEEP_COLOR = register("sheep/color", (builder) -> {
      return builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC);
   });
   public static final ComponentType SHULKER_COLOR = register("shulker/color", (builder) -> {
      return builder.codec(DyeColor.CODEC).packetCodec(DyeColor.PACKET_CODEC);
   });
   public static final ComponentMap DEFAULT_ITEM_COMPONENTS;

   public static ComponentType getDefault(Registry registry) {
      return CUSTOM_DATA;
   }

   private static ComponentType register(String id, UnaryOperator builderOperator) {
      return (ComponentType)Registry.register(Registries.DATA_COMPONENT_TYPE, (String)id, ((ComponentType.Builder)builderOperator.apply(ComponentType.builder())).build());
   }

   static {
      DEFAULT_ITEM_COMPONENTS = ComponentMap.builder().add(MAX_STACK_SIZE, 64).add(LORE, LoreComponent.DEFAULT).add(ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).add(REPAIR_COST, 0).add(ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT).add(RARITY, Rarity.COMMON).add(BREAK_SOUND, SoundEvents.ENTITY_ITEM_BREAK).add(TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT).build();
   }
}
