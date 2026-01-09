package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;

public record BannerPatternsComponent(List layers) implements TooltipAppender {
   final List layers;
   static final Logger LOGGER = LogUtils.getLogger();
   public static final BannerPatternsComponent DEFAULT = new BannerPatternsComponent(List.of());
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public BannerPatternsComponent(List list) {
      this.layers = list;
   }

   public BannerPatternsComponent withoutTopLayer() {
      return new BannerPatternsComponent(List.copyOf(this.layers.subList(0, this.layers.size() - 1)));
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      for(int i = 0; i < Math.min(this.layers().size(), 6); ++i) {
         textConsumer.accept(((Layer)this.layers().get(i)).getTooltipText().formatted(Formatting.GRAY));
      }

   }

   public List layers() {
      return this.layers;
   }

   static {
      CODEC = BannerPatternsComponent.Layer.CODEC.listOf().xmap(BannerPatternsComponent::new, BannerPatternsComponent::layers);
      PACKET_CODEC = BannerPatternsComponent.Layer.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(BannerPatternsComponent::new, BannerPatternsComponent::layers);
   }

   public static record Layer(RegistryEntry pattern, DyeColor color) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(BannerPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(Layer::pattern), DyeColor.CODEC.fieldOf("color").forGetter(Layer::color)).apply(instance, Layer::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public Layer(RegistryEntry registryEntry, DyeColor dyeColor) {
         this.pattern = registryEntry;
         this.color = dyeColor;
      }

      public MutableText getTooltipText() {
         String string = ((BannerPattern)this.pattern.value()).translationKey();
         return Text.translatable(string + "." + this.color.getId());
      }

      public RegistryEntry pattern() {
         return this.pattern;
      }

      public DyeColor color() {
         return this.color;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(BannerPattern.ENTRY_PACKET_CODEC, Layer::pattern, DyeColor.PACKET_CODEC, Layer::color, Layer::new);
      }
   }

   public static class Builder {
      private final ImmutableList.Builder entries = ImmutableList.builder();

      /** @deprecated */
      @Deprecated
      public Builder add(RegistryEntryLookup patternLookup, RegistryKey pattern, DyeColor color) {
         Optional optional = patternLookup.getOptional(pattern);
         if (optional.isEmpty()) {
            BannerPatternsComponent.LOGGER.warn("Unable to find banner pattern with id: '{}'", pattern.getValue());
            return this;
         } else {
            return this.add((RegistryEntry)optional.get(), color);
         }
      }

      public Builder add(RegistryEntry pattern, DyeColor color) {
         return this.add(new Layer(pattern, color));
      }

      public Builder add(Layer layer) {
         this.entries.add(layer);
         return this;
      }

      public Builder addAll(BannerPatternsComponent patterns) {
         this.entries.addAll(patterns.layers);
         return this;
      }

      public BannerPatternsComponent build() {
         return new BannerPatternsComponent(this.entries.build());
      }
   }
}
