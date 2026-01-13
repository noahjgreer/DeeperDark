/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.navigation.GuiNavigationType
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class GuiNavigationType
extends Enum<GuiNavigationType> {
    public static final /* enum */ GuiNavigationType NONE = new GuiNavigationType("NONE", 0);
    public static final /* enum */ GuiNavigationType MOUSE = new GuiNavigationType("MOUSE", 1);
    public static final /* enum */ GuiNavigationType KEYBOARD_ARROW = new GuiNavigationType("KEYBOARD_ARROW", 2);
    public static final /* enum */ GuiNavigationType KEYBOARD_TAB = new GuiNavigationType("KEYBOARD_TAB", 3);
    private static final /* synthetic */ GuiNavigationType[] field_41781;

    public static GuiNavigationType[] values() {
        return (GuiNavigationType[])field_41781.clone();
    }

    public static GuiNavigationType valueOf(String string) {
        return Enum.valueOf(GuiNavigationType.class, string);
    }

    public boolean isMouse() {
        return this == MOUSE;
    }

    public boolean isKeyboard() {
        return this == KEYBOARD_ARROW || this == KEYBOARD_TAB;
    }

    private static /* synthetic */ GuiNavigationType[] method_48184() {
        return new GuiNavigationType[]{NONE, MOUSE, KEYBOARD_ARROW, KEYBOARD_TAB};
    }

    static {
        field_41781 = GuiNavigationType.method_48184();
    }
}

