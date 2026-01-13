/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.input.CursorMovement
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class CursorMovement
extends Enum<CursorMovement> {
    public static final /* enum */ CursorMovement ABSOLUTE = new CursorMovement("ABSOLUTE", 0);
    public static final /* enum */ CursorMovement RELATIVE = new CursorMovement("RELATIVE", 1);
    public static final /* enum */ CursorMovement END = new CursorMovement("END", 2);
    private static final /* synthetic */ CursorMovement[] field_39538;

    public static CursorMovement[] values() {
        return (CursorMovement[])field_39538.clone();
    }

    public static CursorMovement valueOf(String string) {
        return Enum.valueOf(CursorMovement.class, string);
    }

    private static /* synthetic */ CursorMovement[] method_44446() {
        return new CursorMovement[]{ABSOLUTE, RELATIVE, END};
    }

    static {
        field_39538 = CursorMovement.method_44446();
    }
}

