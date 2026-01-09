package net.minecraft.predicate.component;

import com.mojang.serialization.Codec;
import net.minecraft.predicate.item.AttributeModifiersPredicate;
import net.minecraft.predicate.item.BundleContentsPredicate;
import net.minecraft.predicate.item.ContainerPredicate;
import net.minecraft.predicate.item.DamagePredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.FireworkExplosionPredicate;
import net.minecraft.predicate.item.FireworksPredicate;
import net.minecraft.predicate.item.JukeboxPlayablePredicate;
import net.minecraft.predicate.item.PotionContentsPredicate;
import net.minecraft.predicate.item.TrimPredicate;
import net.minecraft.predicate.item.WritableBookContentPredicate;
import net.minecraft.predicate.item.WrittenBookContentPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ComponentPredicateTypes {
   public static final ComponentPredicate.Type DAMAGE;
   public static final ComponentPredicate.Type ENCHANTMENTS;
   public static final ComponentPredicate.Type STORED_ENCHANTMENTS;
   public static final ComponentPredicate.Type POTION_CONTENTS;
   public static final ComponentPredicate.Type CUSTOM_DATA;
   public static final ComponentPredicate.Type CONTAINER;
   public static final ComponentPredicate.Type BUNDLE_CONTENTS;
   public static final ComponentPredicate.Type FIREWORK_EXPLOSION;
   public static final ComponentPredicate.Type FIREWORKS;
   public static final ComponentPredicate.Type WRITABLE_BOOK_CONTENT;
   public static final ComponentPredicate.Type WRITTEN_BOOK_CONTENT;
   public static final ComponentPredicate.Type ATTRIBUTE_MODIFIERS;
   public static final ComponentPredicate.Type TRIM;
   public static final ComponentPredicate.Type JUKEBOX_PLAYABLE;

   private static ComponentPredicate.Type register(String id, Codec codec) {
      return (ComponentPredicate.Type)Registry.register(Registries.DATA_COMPONENT_PREDICATE_TYPE, (String)id, new ComponentPredicate.Type(codec));
   }

   public static ComponentPredicate.Type getDefault(Registry registry) {
      return DAMAGE;
   }

   static {
      DAMAGE = register("damage", DamagePredicate.CODEC);
      ENCHANTMENTS = register("enchantments", EnchantmentsPredicate.Enchantments.CODEC);
      STORED_ENCHANTMENTS = register("stored_enchantments", EnchantmentsPredicate.StoredEnchantments.CODEC);
      POTION_CONTENTS = register("potion_contents", PotionContentsPredicate.CODEC);
      CUSTOM_DATA = register("custom_data", CustomDataPredicate.CODEC);
      CONTAINER = register("container", ContainerPredicate.CODEC);
      BUNDLE_CONTENTS = register("bundle_contents", BundleContentsPredicate.CODEC);
      FIREWORK_EXPLOSION = register("firework_explosion", FireworkExplosionPredicate.CODEC);
      FIREWORKS = register("fireworks", FireworksPredicate.CODEC);
      WRITABLE_BOOK_CONTENT = register("writable_book_content", WritableBookContentPredicate.CODEC);
      WRITTEN_BOOK_CONTENT = register("written_book_content", WrittenBookContentPredicate.CODEC);
      ATTRIBUTE_MODIFIERS = register("attribute_modifiers", AttributeModifiersPredicate.CODEC);
      TRIM = register("trim", TrimPredicate.CODEC);
      JUKEBOX_PLAYABLE = register("jukebox_playable", JukeboxPlayablePredicate.CODEC);
   }
}
