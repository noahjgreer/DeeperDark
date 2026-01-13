/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.LootChoice;
import net.minecraft.util.math.MathHelper;

protected abstract class LeafEntry.Choice
implements LootChoice {
    protected LeafEntry.Choice() {
    }

    @Override
    public int getWeight(float luck) {
        return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * luck), 0);
    }
}
