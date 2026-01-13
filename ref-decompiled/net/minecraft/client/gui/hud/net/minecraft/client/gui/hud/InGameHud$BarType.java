/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class InGameHud.BarType
extends Enum<InGameHud.BarType> {
    public static final /* enum */ InGameHud.BarType EMPTY = new InGameHud.BarType();
    public static final /* enum */ InGameHud.BarType EXPERIENCE = new InGameHud.BarType();
    public static final /* enum */ InGameHud.BarType LOCATOR = new InGameHud.BarType();
    public static final /* enum */ InGameHud.BarType JUMPABLE_VEHICLE = new InGameHud.BarType();
    private static final /* synthetic */ InGameHud.BarType[] field_59823;

    public static InGameHud.BarType[] values() {
        return (InGameHud.BarType[])field_59823.clone();
    }

    public static InGameHud.BarType valueOf(String string) {
        return Enum.valueOf(InGameHud.BarType.class, string);
    }

    private static /* synthetic */ InGameHud.BarType[] method_70844() {
        return new InGameHud.BarType[]{EMPTY, EXPERIENCE, LOCATOR, JUMPABLE_VEHICLE};
    }

    static {
        field_59823 = InGameHud.BarType.method_70844();
    }
}
