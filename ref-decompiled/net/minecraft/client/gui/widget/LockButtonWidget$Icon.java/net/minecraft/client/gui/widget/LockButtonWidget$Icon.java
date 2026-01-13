/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static final class LockButtonWidget.Icon
extends Enum<LockButtonWidget.Icon> {
    public static final /* enum */ LockButtonWidget.Icon LOCKED = new LockButtonWidget.Icon(Identifier.ofVanilla("widget/locked_button"));
    public static final /* enum */ LockButtonWidget.Icon LOCKED_HOVER = new LockButtonWidget.Icon(Identifier.ofVanilla("widget/locked_button_highlighted"));
    public static final /* enum */ LockButtonWidget.Icon LOCKED_DISABLED = new LockButtonWidget.Icon(Identifier.ofVanilla("widget/locked_button_disabled"));
    public static final /* enum */ LockButtonWidget.Icon UNLOCKED = new LockButtonWidget.Icon(Identifier.ofVanilla("widget/unlocked_button"));
    public static final /* enum */ LockButtonWidget.Icon UNLOCKED_HOVER = new LockButtonWidget.Icon(Identifier.ofVanilla("widget/unlocked_button_highlighted"));
    public static final /* enum */ LockButtonWidget.Icon UNLOCKED_DISABLED = new LockButtonWidget.Icon(Identifier.ofVanilla("widget/unlocked_button_disabled"));
    final Identifier texture;
    private static final /* synthetic */ LockButtonWidget.Icon[] field_2136;

    public static LockButtonWidget.Icon[] values() {
        return (LockButtonWidget.Icon[])field_2136.clone();
    }

    public static LockButtonWidget.Icon valueOf(String string) {
        return Enum.valueOf(LockButtonWidget.Icon.class, string);
    }

    private LockButtonWidget.Icon(Identifier texture) {
        this.texture = texture;
    }

    private static /* synthetic */ LockButtonWidget.Icon[] method_36870() {
        return new LockButtonWidget.Icon[]{LOCKED, LOCKED_HOVER, LOCKED_DISABLED, UNLOCKED, UNLOCKED_HOVER, UNLOCKED_DISABLED};
    }

    static {
        field_2136 = LockButtonWidget.Icon.method_36870();
    }
}
