package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

public record ContainerLootComponent(RegistryKey lootTable, long seed) implements TooltipAppender {
   private static final Text UNKNOWN_LOOT_TABLE_TOOLTIP_TEXT = Text.translatable("item.container.loot_table.unknown");
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(LootTable.TABLE_KEY.fieldOf("loot_table").forGetter(ContainerLootComponent::lootTable), Codec.LONG.optionalFieldOf("seed", 0L).forGetter(ContainerLootComponent::seed)).apply(instance, ContainerLootComponent::new);
   });

   public ContainerLootComponent(RegistryKey registryKey, long l) {
      this.lootTable = registryKey;
      this.seed = l;
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      textConsumer.accept(UNKNOWN_LOOT_TABLE_TOOLTIP_TEXT);
   }

   public RegistryKey lootTable() {
      return this.lootTable;
   }

   public long seed() {
      return this.seed;
   }
}
