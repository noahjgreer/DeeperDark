/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class Selectable.SelectionType
extends Enum<Selectable.SelectionType> {
    public static final /* enum */ Selectable.SelectionType NONE = new Selectable.SelectionType();
    public static final /* enum */ Selectable.SelectionType HOVERED = new Selectable.SelectionType();
    public static final /* enum */ Selectable.SelectionType FOCUSED = new Selectable.SelectionType();
    private static final /* synthetic */ Selectable.SelectionType[] field_33787;

    public static Selectable.SelectionType[] values() {
        return (Selectable.SelectionType[])field_33787.clone();
    }

    public static Selectable.SelectionType valueOf(String string) {
        return Enum.valueOf(Selectable.SelectionType.class, string);
    }

    public boolean isFocused() {
        return this == FOCUSED;
    }

    private static /* synthetic */ Selectable.SelectionType[] method_37029() {
        return new Selectable.SelectionType[]{NONE, HOVERED, FOCUSED};
    }

    static {
        field_33787 = Selectable.SelectionType.method_37029();
    }
}
