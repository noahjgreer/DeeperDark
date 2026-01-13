/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.cauldron;

import java.util.Map;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;

public record CauldronBehavior.CauldronBehaviorMap(String name, Map<Item, CauldronBehavior> map) {
}
