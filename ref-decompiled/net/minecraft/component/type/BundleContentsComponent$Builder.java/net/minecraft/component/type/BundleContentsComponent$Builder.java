/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.Fraction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component.type;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.math.Fraction;
import org.jspecify.annotations.Nullable;

public static class BundleContentsComponent.Builder {
    private final List<ItemStack> stacks;
    private Fraction occupancy;
    private int selectedStackIndex;

    public BundleContentsComponent.Builder(BundleContentsComponent base) {
        this.stacks = new ArrayList<ItemStack>(base.stacks);
        this.occupancy = base.occupancy;
        this.selectedStackIndex = base.selectedStackIndex;
    }

    public BundleContentsComponent.Builder clear() {
        this.stacks.clear();
        this.occupancy = Fraction.ZERO;
        this.selectedStackIndex = -1;
        return this;
    }

    private int getInsertionIndex(ItemStack stack) {
        if (!stack.isStackable()) {
            return -1;
        }
        for (int i = 0; i < this.stacks.size(); ++i) {
            if (!ItemStack.areItemsAndComponentsEqual(this.stacks.get(i), stack)) continue;
            return i;
        }
        return -1;
    }

    private int getMaxAllowed(ItemStack stack) {
        Fraction fraction = Fraction.ONE.subtract(this.occupancy);
        return Math.max(fraction.divideBy(BundleContentsComponent.getOccupancy(stack)).intValue(), 0);
    }

    public int add(ItemStack stack) {
        if (!BundleContentsComponent.canBeBundled(stack)) {
            return 0;
        }
        int i = Math.min(stack.getCount(), this.getMaxAllowed(stack));
        if (i == 0) {
            return 0;
        }
        this.occupancy = this.occupancy.add(BundleContentsComponent.getOccupancy(stack).multiplyBy(Fraction.getFraction((int)i, (int)1)));
        int j = this.getInsertionIndex(stack);
        if (j != -1) {
            ItemStack itemStack = this.stacks.remove(j);
            ItemStack itemStack2 = itemStack.copyWithCount(itemStack.getCount() + i);
            stack.decrement(i);
            this.stacks.add(0, itemStack2);
        } else {
            this.stacks.add(0, stack.split(i));
        }
        return i;
    }

    public int add(Slot slot, PlayerEntity player) {
        ItemStack itemStack = slot.getStack();
        int i = this.getMaxAllowed(itemStack);
        return BundleContentsComponent.canBeBundled(itemStack) ? this.add(slot.takeStackRange(itemStack.getCount(), i, player)) : 0;
    }

    public void setSelectedStackIndex(int selectedStackIndex) {
        this.selectedStackIndex = this.selectedStackIndex == selectedStackIndex || this.isOutOfBounds(selectedStackIndex) ? -1 : selectedStackIndex;
    }

    private boolean isOutOfBounds(int index) {
        return index < 0 || index >= this.stacks.size();
    }

    public @Nullable ItemStack removeSelected() {
        if (this.stacks.isEmpty()) {
            return null;
        }
        int i = this.isOutOfBounds(this.selectedStackIndex) ? 0 : this.selectedStackIndex;
        ItemStack itemStack = this.stacks.remove(i).copy();
        this.occupancy = this.occupancy.subtract(BundleContentsComponent.getOccupancy(itemStack).multiplyBy(Fraction.getFraction((int)itemStack.getCount(), (int)1)));
        this.setSelectedStackIndex(-1);
        return itemStack;
    }

    public Fraction getOccupancy() {
        return this.occupancy;
    }

    public BundleContentsComponent build() {
        return new BundleContentsComponent(List.copyOf(this.stacks), this.occupancy, this.selectedStackIndex);
    }
}
