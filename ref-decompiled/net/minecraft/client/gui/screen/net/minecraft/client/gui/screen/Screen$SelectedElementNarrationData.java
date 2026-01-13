/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Selectable;

@Environment(value=EnvType.CLIENT)
public static final class Screen.SelectedElementNarrationData
extends Record {
    final Selectable selectable;
    final int index;
    final Selectable.SelectionType selectType;

    public Screen.SelectedElementNarrationData(Selectable selectable, int index, Selectable.SelectionType selectType) {
        this.selectable = selectable;
        this.index = index;
        this.selectType = selectType;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Screen.SelectedElementNarrationData.class, "entry;index;priority", "selectable", "index", "selectType"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Screen.SelectedElementNarrationData.class, "entry;index;priority", "selectable", "index", "selectType"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Screen.SelectedElementNarrationData.class, "entry;index;priority", "selectable", "index", "selectType"}, this, object);
    }

    public Selectable selectable() {
        return this.selectable;
    }

    public int index() {
        return this.index;
    }

    public Selectable.SelectionType selectType() {
        return this.selectType;
    }
}
