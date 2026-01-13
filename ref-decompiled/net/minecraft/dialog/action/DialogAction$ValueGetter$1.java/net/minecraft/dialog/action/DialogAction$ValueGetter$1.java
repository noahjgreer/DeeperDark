/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.dialog.action;

import net.minecraft.dialog.action.DialogAction;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

static class DialogAction.ValueGetter.1
implements DialogAction.ValueGetter {
    final /* synthetic */ String field_60974;

    DialogAction.ValueGetter.1(String string) {
        this.field_60974 = string;
    }

    @Override
    public String get() {
        return this.field_60974;
    }

    @Override
    public NbtElement getAsNbt() {
        return NbtString.of(this.field_60974);
    }
}
