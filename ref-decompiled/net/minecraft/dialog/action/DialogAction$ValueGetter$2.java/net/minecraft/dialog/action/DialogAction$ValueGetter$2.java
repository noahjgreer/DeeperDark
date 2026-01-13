/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.dialog.action;

import java.util.function.Supplier;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

static class DialogAction.ValueGetter.2
implements DialogAction.ValueGetter {
    final /* synthetic */ Supplier field_60975;

    DialogAction.ValueGetter.2(Supplier supplier) {
        this.field_60975 = supplier;
    }

    @Override
    public String get() {
        return (String)this.field_60975.get();
    }

    @Override
    public NbtElement getAsNbt() {
        return NbtString.of((String)this.field_60975.get());
    }
}
