/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.ForgingSlotsManager;

public static class ForgingSlotsManager.Builder {
    private final List<ForgingSlotsManager.ForgingSlot> inputs = new ArrayList<ForgingSlotsManager.ForgingSlot>();
    private ForgingSlotsManager.ForgingSlot resultSlot = ForgingSlotsManager.ForgingSlot.DEFAULT;

    public ForgingSlotsManager.Builder input(int slotId, int x, int y, Predicate<ItemStack> mayPlace) {
        this.inputs.add(new ForgingSlotsManager.ForgingSlot(slotId, x, y, mayPlace));
        return this;
    }

    public ForgingSlotsManager.Builder output(int slotId, int x, int y) {
        this.resultSlot = new ForgingSlotsManager.ForgingSlot(slotId, x, y, stack -> false);
        return this;
    }

    public ForgingSlotsManager build() {
        int i = this.inputs.size();
        for (int j = 0; j < i; ++j) {
            ForgingSlotsManager.ForgingSlot forgingSlot = this.inputs.get(j);
            if (forgingSlot.slotId == j) continue;
            throw new IllegalArgumentException("Expected input slots to have continous indexes");
        }
        if (this.resultSlot.slotId != i) {
            throw new IllegalArgumentException("Expected result slot index to follow last input slot");
        }
        return new ForgingSlotsManager(this.inputs, this.resultSlot);
    }
}
