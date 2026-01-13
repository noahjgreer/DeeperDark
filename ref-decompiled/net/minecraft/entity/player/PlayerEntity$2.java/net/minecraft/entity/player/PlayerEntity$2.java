/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;

class PlayerEntity.2
implements StackReference {
    final /* synthetic */ int field_49737;

    PlayerEntity.2() {
        this.field_49737 = i;
    }

    @Override
    public ItemStack get() {
        return PlayerEntity.this.playerScreenHandler.getCraftingInput().getStack(this.field_49737);
    }

    @Override
    public boolean set(ItemStack stack) {
        PlayerEntity.this.playerScreenHandler.getCraftingInput().setStack(this.field_49737, stack);
        PlayerEntity.this.playerScreenHandler.onContentChanged(PlayerEntity.this.inventory);
        return true;
    }
}
