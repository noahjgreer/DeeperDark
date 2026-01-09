package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import java.util.List;
import java.util.SequencedSet;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record TooltipDisplayComponent(boolean hideTooltip, SequencedSet hiddenComponents) {
   private static final Codec HIDDEN_COMPONENTS_CODEC;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final TooltipDisplayComponent DEFAULT;

   public TooltipDisplayComponent(boolean bl, SequencedSet sequencedSet) {
      this.hideTooltip = bl;
      this.hiddenComponents = sequencedSet;
   }

   public TooltipDisplayComponent with(ComponentType component, boolean hidden) {
      if (this.hiddenComponents.contains(component) == hidden) {
         return this;
      } else {
         SequencedSet sequencedSet = new ReferenceLinkedOpenHashSet(this.hiddenComponents);
         if (hidden) {
            sequencedSet.add(component);
         } else {
            sequencedSet.remove(component);
         }

         return new TooltipDisplayComponent(this.hideTooltip, sequencedSet);
      }
   }

   public boolean shouldDisplay(ComponentType component) {
      return !this.hideTooltip && !this.hiddenComponents.contains(component);
   }

   public boolean hideTooltip() {
      return this.hideTooltip;
   }

   public SequencedSet hiddenComponents() {
      return this.hiddenComponents;
   }

   static {
      HIDDEN_COMPONENTS_CODEC = ComponentType.CODEC.listOf().xmap(ReferenceLinkedOpenHashSet::new, List::copyOf);
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.BOOL.optionalFieldOf("hide_tooltip", false).forGetter(TooltipDisplayComponent::hideTooltip), HIDDEN_COMPONENTS_CODEC.optionalFieldOf("hidden_components", ReferenceSortedSets.emptySet()).forGetter(TooltipDisplayComponent::hiddenComponents)).apply(instance, TooltipDisplayComponent::new);
      });
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, TooltipDisplayComponent::hideTooltip, ComponentType.PACKET_CODEC.collect(PacketCodecs.toCollection(ReferenceLinkedOpenHashSet::new)), TooltipDisplayComponent::hiddenComponents, TooltipDisplayComponent::new);
      DEFAULT = new TooltipDisplayComponent(false, ReferenceSortedSets.emptySet());
   }
}
