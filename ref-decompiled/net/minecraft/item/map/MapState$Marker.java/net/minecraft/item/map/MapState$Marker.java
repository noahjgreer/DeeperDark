/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.map;

import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.entry.RegistryEntry;

record MapState.Marker(RegistryEntry<MapDecorationType> type, byte x, byte y, byte rot) {
}
