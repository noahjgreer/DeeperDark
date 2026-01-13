/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.sync;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.screen.sync.TrackedSlot;

class TrackedSlot.1
implements TrackedSlot {
    TrackedSlot.1() {
    }

    @Override
    public void setReceivedHash(ItemStackHash receivedHash) {
    }

    @Override
    public void setReceivedStack(ItemStack receivedStack) {
    }

    @Override
    public boolean isInSync(ItemStack actualStack) {
        return true;
    }
}
