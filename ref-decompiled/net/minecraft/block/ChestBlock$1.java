/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Optional;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;

class ChestBlock.1
implements DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> {
    ChestBlock.1() {
    }

    @Override
    public Optional<Inventory> getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
        return Optional.of(new DoubleInventory(chestBlockEntity, chestBlockEntity2));
    }

    @Override
    public Optional<Inventory> getFrom(ChestBlockEntity chestBlockEntity) {
        return Optional.of(chestBlockEntity);
    }

    @Override
    public Optional<Inventory> getFallback() {
        return Optional.empty();
    }

    @Override
    public /* synthetic */ Object getFallback() {
        return this.getFallback();
    }
}
