/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.CyclingButtonWidget;

@Environment(value=EnvType.CLIENT)
static class CyclingButtonWidget.Values.2
implements CyclingButtonWidget.Values<T> {
    final /* synthetic */ BooleanSupplier field_27980;
    final /* synthetic */ List field_27981;
    final /* synthetic */ List field_27982;

    CyclingButtonWidget.Values.2(BooleanSupplier booleanSupplier, List list, List list2) {
        this.field_27980 = booleanSupplier;
        this.field_27981 = list;
        this.field_27982 = list2;
    }

    @Override
    public List<T> getCurrent() {
        return this.field_27980.getAsBoolean() ? this.field_27981 : this.field_27982;
    }

    @Override
    public List<T> getDefaults() {
        return this.field_27982;
    }
}
