/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

@Environment(value=EnvType.CLIENT)
class InputControlHandlers.TextInputControlHandler.1
implements DialogAction.ValueGetter {
    final /* synthetic */ Supplier field_61056;

    InputControlHandlers.TextInputControlHandler.1() {
        this.field_61056 = supplier;
    }

    @Override
    public String get() {
        return NbtString.escapeUnquoted((String)this.field_61056.get());
    }

    @Override
    public NbtElement getAsNbt() {
        return NbtString.of((String)this.field_61056.get());
    }
}
