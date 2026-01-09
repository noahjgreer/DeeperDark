package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record TradedItem(RegistryEntry item, int count, ComponentMapPredicate components, ItemStack itemStack) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Item.ENTRY_CODEC.fieldOf("id").forGetter(TradedItem::item), Codecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(TradedItem::count), ComponentMapPredicate.CODEC.optionalFieldOf("components", ComponentMapPredicate.EMPTY).forGetter(TradedItem::components)).apply(instance, TradedItem::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final PacketCodec OPTIONAL_PACKET_CODEC;

   public TradedItem(ItemConvertible item) {
      this(item, 1);
   }

   public TradedItem(ItemConvertible item, int count) {
      this(item.asItem().getRegistryEntry(), count, ComponentMapPredicate.EMPTY);
   }

   public TradedItem(RegistryEntry item, int count, ComponentMapPredicate components) {
      this(item, count, components, createDisplayStack(item, count, components));
   }

   public TradedItem(RegistryEntry registryEntry, int i, ComponentMapPredicate componentMapPredicate, ItemStack itemStack) {
      this.item = registryEntry;
      this.count = i;
      this.components = componentMapPredicate;
      this.itemStack = itemStack;
   }

   public TradedItem withComponents(UnaryOperator builderCallback) {
      return new TradedItem(this.item, this.count, ((ComponentMapPredicate.Builder)builderCallback.apply(ComponentMapPredicate.builder())).build());
   }

   private static ItemStack createDisplayStack(RegistryEntry item, int count, ComponentMapPredicate components) {
      return new ItemStack(item, count, components.toChanges());
   }

   public boolean matches(ItemStack stack) {
      return stack.itemMatches(this.item) && this.components.test((ComponentsAccess)stack);
   }

   public RegistryEntry item() {
      return this.item;
   }

   public int count() {
      return this.count;
   }

   public ComponentMapPredicate components() {
      return this.components;
   }

   public ItemStack itemStack() {
      return this.itemStack;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(Item.ENTRY_PACKET_CODEC, TradedItem::item, PacketCodecs.VAR_INT, TradedItem::count, ComponentMapPredicate.PACKET_CODEC, TradedItem::components, TradedItem::new);
      OPTIONAL_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs::optional);
   }
}
