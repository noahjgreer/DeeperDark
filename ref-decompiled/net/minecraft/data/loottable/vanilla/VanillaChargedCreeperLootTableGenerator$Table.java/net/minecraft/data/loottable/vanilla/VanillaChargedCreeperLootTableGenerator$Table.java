/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.loottable.vanilla;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;

static final class VanillaChargedCreeperLootTableGenerator.Table
extends Record {
    final RegistryKey<LootTable> lootTable;
    final EntityType<?> entityType;
    final Item item;

    VanillaChargedCreeperLootTableGenerator.Table(RegistryKey<LootTable> lootTable, EntityType<?> entityType, Item item) {
        this.lootTable = lootTable;
        this.entityType = entityType;
        this.item = item;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{VanillaChargedCreeperLootTableGenerator.Table.class, "lootTable;entityType;item", "lootTable", "entityType", "item"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{VanillaChargedCreeperLootTableGenerator.Table.class, "lootTable;entityType;item", "lootTable", "entityType", "item"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{VanillaChargedCreeperLootTableGenerator.Table.class, "lootTable;entityType;item", "lootTable", "entityType", "item"}, this, object);
    }

    public RegistryKey<LootTable> lootTable() {
        return this.lootTable;
    }

    public EntityType<?> entityType() {
        return this.entityType;
    }

    public Item item() {
        return this.item;
    }
}
