/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.component.type;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

public static class ItemEnchantmentsComponent.Builder {
    private final Object2IntOpenHashMap<RegistryEntry<Enchantment>> enchantments = new Object2IntOpenHashMap();

    public ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent enchantmentsComponent) {
        this.enchantments.putAll(enchantmentsComponent.enchantments);
    }

    public void set(RegistryEntry<Enchantment> enchantment, int level) {
        if (level <= 0) {
            this.enchantments.removeInt(enchantment);
        } else {
            this.enchantments.put(enchantment, Math.min(level, 255));
        }
    }

    public void add(RegistryEntry<Enchantment> enchantment, int level) {
        if (level > 0) {
            this.enchantments.merge(enchantment, Math.min(level, 255), Integer::max);
        }
    }

    public void remove(Predicate<RegistryEntry<Enchantment>> predicate) {
        this.enchantments.keySet().removeIf(predicate);
    }

    public int getLevel(RegistryEntry<Enchantment> enchantment) {
        return this.enchantments.getOrDefault(enchantment, 0);
    }

    public Set<RegistryEntry<Enchantment>> getEnchantments() {
        return this.enchantments.keySet();
    }

    public ItemEnchantmentsComponent build() {
        return new ItemEnchantmentsComponent(this.enchantments);
    }
}
