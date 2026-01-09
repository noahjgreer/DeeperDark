package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.map.MapState;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record MapIdComponent(int id) implements TooltipAppender {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private static final Text LOCKED_TOOLTIP_TEXT;

   public MapIdComponent(int i) {
      this.id = i;
   }

   public String asString() {
      return "map_" + this.id;
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      MapState mapState = context.getMapState(this);
      if (mapState == null) {
         textConsumer.accept(Text.translatable("filled_map.unknown").formatted(Formatting.GRAY));
      } else {
         MapPostProcessingComponent mapPostProcessingComponent = (MapPostProcessingComponent)components.get(DataComponentTypes.MAP_POST_PROCESSING);
         if (components.get(DataComponentTypes.CUSTOM_NAME) == null && mapPostProcessingComponent == null) {
            textConsumer.accept(Text.translatable("filled_map.id", this.id).formatted(Formatting.GRAY));
         }

         if (mapState.locked || mapPostProcessingComponent == MapPostProcessingComponent.LOCK) {
            textConsumer.accept(LOCKED_TOOLTIP_TEXT);
         }

         if (type.isAdvanced()) {
            int i = mapPostProcessingComponent == MapPostProcessingComponent.SCALE ? 1 : 0;
            int j = Math.min(mapState.scale + i, 4);
            textConsumer.accept(Text.translatable("filled_map.scale", 1 << j).formatted(Formatting.GRAY));
            textConsumer.accept(Text.translatable("filled_map.level", j, 4).formatted(Formatting.GRAY));
         }

      }
   }

   public int id() {
      return this.id;
   }

   static {
      CODEC = Codec.INT.xmap(MapIdComponent::new, MapIdComponent::id);
      PACKET_CODEC = PacketCodecs.VAR_INT.xmap(MapIdComponent::new, MapIdComponent::id);
      LOCKED_TOOLTIP_TEXT = Text.translatable("filled_map.locked").formatted(Formatting.GRAY);
   }
}
