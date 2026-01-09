package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public record BlockStateComponent(Map properties) implements TooltipAppender {
   public static final BlockStateComponent DEFAULT = new BlockStateComponent(Map.of());
   public static final Codec CODEC;
   private static final PacketCodec MAP_PACKET_CODEC;
   public static final PacketCodec PACKET_CODEC;

   public BlockStateComponent(Map map) {
      this.properties = map;
   }

   public BlockStateComponent with(Property property, Comparable value) {
      return new BlockStateComponent(Util.mapWith(this.properties, property.getName(), property.name(value)));
   }

   public BlockStateComponent with(Property property, BlockState fromState) {
      return this.with(property, fromState.get(property));
   }

   @Nullable
   public Comparable getValue(Property property) {
      String string = (String)this.properties.get(property.getName());
      return string == null ? null : (Comparable)property.parse(string).orElse((Object)null);
   }

   public BlockState applyToState(BlockState state) {
      StateManager stateManager = state.getBlock().getStateManager();
      Iterator var3 = this.properties.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         Property property = stateManager.getProperty((String)entry.getKey());
         if (property != null) {
            state = applyToState(state, property, (String)entry.getValue());
         }
      }

      return state;
   }

   private static BlockState applyToState(BlockState state, Property property, String value) {
      return (BlockState)property.parse(value).map((valuex) -> {
         return (BlockState)state.with(property, valuex);
      }).orElse(state);
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      Integer integer = (Integer)this.getValue(BeehiveBlock.HONEY_LEVEL);
      if (integer != null) {
         textConsumer.accept(Text.translatable("container.beehive.honey", integer, 5).formatted(Formatting.GRAY));
      }

   }

   public Map properties() {
      return this.properties;
   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(BlockStateComponent::new, BlockStateComponent::properties);
      MAP_PACKET_CODEC = PacketCodecs.map(Object2ObjectOpenHashMap::new, PacketCodecs.STRING, PacketCodecs.STRING);
      PACKET_CODEC = MAP_PACKET_CODEC.xmap(BlockStateComponent::new, BlockStateComponent::properties);
   }
}
