/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item.tooltip;

import net.minecraft.item.tooltip.TooltipType;

public record TooltipType.Default(boolean advanced, boolean creative) implements TooltipType
{
    @Override
    public boolean isAdvanced() {
        return this.advanced;
    }

    @Override
    public boolean isCreative() {
        return this.creative;
    }

    public TooltipType.Default withCreative() {
        return new TooltipType.Default(this.advanced, true);
    }
}
