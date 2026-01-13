/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component;

import java.util.Set;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;

public record ComponentChanges.AddedRemovedPair(ComponentMap added, Set<ComponentType<?>> removed) {
    public static final ComponentChanges.AddedRemovedPair EMPTY = new ComponentChanges.AddedRemovedPair(ComponentMap.EMPTY, Set.of());
}
