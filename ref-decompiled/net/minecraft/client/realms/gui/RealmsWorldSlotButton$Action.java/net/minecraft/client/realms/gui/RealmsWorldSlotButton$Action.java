/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class RealmsWorldSlotButton.Action
extends Enum<RealmsWorldSlotButton.Action> {
    public static final /* enum */ RealmsWorldSlotButton.Action NOTHING = new RealmsWorldSlotButton.Action();
    public static final /* enum */ RealmsWorldSlotButton.Action SWITCH_SLOT = new RealmsWorldSlotButton.Action();
    private static final /* synthetic */ RealmsWorldSlotButton.Action[] field_19681;

    public static RealmsWorldSlotButton.Action[] values() {
        return (RealmsWorldSlotButton.Action[])field_19681.clone();
    }

    public static RealmsWorldSlotButton.Action valueOf(String name) {
        return Enum.valueOf(RealmsWorldSlotButton.Action.class, name);
    }

    private static /* synthetic */ RealmsWorldSlotButton.Action[] method_36853() {
        return new RealmsWorldSlotButton.Action[]{NOTHING, SWITCH_SLOT};
    }

    static {
        field_19681 = RealmsWorldSlotButton.Action.method_36853();
    }
}
