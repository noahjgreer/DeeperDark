package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Formatting;

public record Sherds(Optional back, Optional left, Optional right, Optional front) implements TooltipAppender {
   public static final Sherds DEFAULT = new Sherds(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   private Sherds(List sherds) {
      this(getSherd(sherds, 0), getSherd(sherds, 1), getSherd(sherds, 2), getSherd(sherds, 3));
   }

   public Sherds(Item back, Item left, Item right, Item front) {
      this(List.of(back, left, right, front));
   }

   public Sherds(Optional optional, Optional optional2, Optional optional3, Optional optional4) {
      this.back = optional;
      this.left = optional2;
      this.right = optional3;
      this.front = optional4;
   }

   private static Optional getSherd(List sherds, int index) {
      if (index >= sherds.size()) {
         return Optional.empty();
      } else {
         Item item = (Item)sherds.get(index);
         return item == Items.BRICK ? Optional.empty() : Optional.of(item);
      }
   }

   public List toList() {
      return Stream.of(this.back, this.left, this.right, this.front).map((item) -> {
         return (Item)item.orElse(Items.BRICK);
      }).toList();
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      if (!this.equals(DEFAULT)) {
         textConsumer.accept(ScreenTexts.EMPTY);
         appendSherdTooltip(textConsumer, this.front);
         appendSherdTooltip(textConsumer, this.left);
         appendSherdTooltip(textConsumer, this.right);
         appendSherdTooltip(textConsumer, this.back);
      }
   }

   private static void appendSherdTooltip(Consumer textConsumer, Optional sherdItem) {
      textConsumer.accept((new ItemStack((ItemConvertible)sherdItem.orElse(Items.BRICK), 1)).getName().copyContentOnly().formatted(Formatting.GRAY));
   }

   public Optional back() {
      return this.back;
   }

   public Optional left() {
      return this.left;
   }

   public Optional right() {
      return this.right;
   }

   public Optional front() {
      return this.front;
   }

   static {
      CODEC = Registries.ITEM.getCodec().sizeLimitedListOf(4).xmap(Sherds::new, Sherds::toList);
      PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.ITEM).collect(PacketCodecs.toList(4)).xmap(Sherds::new, Sherds::toList);
   }
}
