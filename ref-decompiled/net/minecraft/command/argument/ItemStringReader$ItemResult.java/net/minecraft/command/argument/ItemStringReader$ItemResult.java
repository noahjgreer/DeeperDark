/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.argument;

import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

public record ItemStringReader.ItemResult(RegistryEntry<Item> item, ComponentChanges components) {
}
