package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public record ArmorTrim(RegistryEntry material, RegistryEntry pattern) implements TooltipAppender {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ArmorTrimMaterial.ENTRY_CODEC.fieldOf("material").forGetter(ArmorTrim::material), ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern)).apply(instance, ArmorTrim::new);
   });
   public static final PacketCodec PACKET_CODEC;
   private static final Text UPGRADE_TEXT;

   public ArmorTrim(RegistryEntry material, RegistryEntry pattern) {
      this.material = material;
      this.pattern = pattern;
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      textConsumer.accept(UPGRADE_TEXT);
      textConsumer.accept(ScreenTexts.space().append(((ArmorTrimPattern)this.pattern.value()).getDescription(this.material)));
      textConsumer.accept(ScreenTexts.space().append(((ArmorTrimMaterial)this.material.value()).description()));
   }

   public Identifier getTextureId(String trimsDirectory, RegistryKey equipmentAsset) {
      ArmorTrimAssets.AssetId assetId = ((ArmorTrimMaterial)this.material().value()).assets().getAssetId(equipmentAsset);
      return ((ArmorTrimPattern)this.pattern().value()).assetId().withPath((patternId) -> {
         return trimsDirectory + "/" + patternId + "_" + assetId.suffix();
      });
   }

   public RegistryEntry material() {
      return this.material;
   }

   public RegistryEntry pattern() {
      return this.pattern;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(ArmorTrimMaterial.ENTRY_PACKET_CODEC, ArmorTrim::material, ArmorTrimPattern.ENTRY_PACKET_CODEC, ArmorTrim::pattern, ArmorTrim::new);
      UPGRADE_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.upgrade"))).formatted(Formatting.GRAY);
   }
}
