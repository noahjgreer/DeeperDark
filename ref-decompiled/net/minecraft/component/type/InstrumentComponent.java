package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public record InstrumentComponent(LazyRegistryEntryReference instrument) implements TooltipAppender {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public InstrumentComponent(RegistryEntry instrument) {
      this(new LazyRegistryEntryReference(instrument));
   }

   /** @deprecated */
   @Deprecated
   public InstrumentComponent(RegistryKey instrument) {
      this(new LazyRegistryEntryReference(instrument));
   }

   public InstrumentComponent(LazyRegistryEntryReference lazyRegistryEntryReference) {
      this.instrument = lazyRegistryEntryReference;
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
      if (wrapperLookup != null) {
         Optional optional = this.getInstrument(wrapperLookup);
         if (optional.isPresent()) {
            MutableText mutableText = ((Instrument)((RegistryEntry)optional.get()).value()).description().copy();
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
            textConsumer.accept(mutableText);
         }

      }
   }

   public Optional getInstrument(RegistryWrapper.WrapperLookup registries) {
      return this.instrument.resolveEntry(registries);
   }

   public LazyRegistryEntryReference instrument() {
      return this.instrument;
   }

   static {
      CODEC = LazyRegistryEntryReference.createCodec(RegistryKeys.INSTRUMENT, Instrument.ENTRY_CODEC).xmap(InstrumentComponent::new, InstrumentComponent::instrument);
      PACKET_CODEC = LazyRegistryEntryReference.createPacketCodec(RegistryKeys.INSTRUMENT, Instrument.ENTRY_PACKET_CODEC).xmap(InstrumentComponent::new, InstrumentComponent::instrument);
   }
}
