/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class SelectionManager.SelectionType
extends Enum<SelectionManager.SelectionType> {
    public static final /* enum */ SelectionManager.SelectionType CHARACTER = new SelectionManager.SelectionType();
    public static final /* enum */ SelectionManager.SelectionType WORD = new SelectionManager.SelectionType();
    private static final /* synthetic */ SelectionManager.SelectionType[] field_38310;

    public static SelectionManager.SelectionType[] values() {
        return (SelectionManager.SelectionType[])field_38310.clone();
    }

    public static SelectionManager.SelectionType valueOf(String string) {
        return Enum.valueOf(SelectionManager.SelectionType.class, string);
    }

    private static /* synthetic */ SelectionManager.SelectionType[] method_42577() {
        return new SelectionManager.SelectionType[]{CHARACTER, WORD};
    }

    static {
        field_38310 = SelectionManager.SelectionType.method_42577();
    }
}
