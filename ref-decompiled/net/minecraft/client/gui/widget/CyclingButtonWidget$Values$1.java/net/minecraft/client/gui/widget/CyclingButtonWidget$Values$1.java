/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.CyclingButtonWidget;

@Environment(value=EnvType.CLIENT)
static class CyclingButtonWidget.Values.1
implements CyclingButtonWidget.Values<T> {
    final /* synthetic */ List field_27979;

    CyclingButtonWidget.Values.1(List list) {
        this.field_27979 = list;
    }

    @Override
    public List<T> getCurrent() {
        return this.field_27979;
    }

    @Override
    public List<T> getDefaults() {
        return this.field_27979;
    }
}
