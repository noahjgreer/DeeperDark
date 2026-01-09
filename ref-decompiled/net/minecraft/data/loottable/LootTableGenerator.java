package net.minecraft.data.loottable;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface LootTableGenerator {
   void accept(BiConsumer lootTableBiConsumer);
}
