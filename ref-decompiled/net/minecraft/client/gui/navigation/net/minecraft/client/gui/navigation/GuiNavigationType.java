/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class GuiNavigationType
extends Enum<GuiNavigationType> {
    public static final /* enum */ GuiNavigationType NONE = new GuiNavigationType();
    public static final /* enum */ GuiNavigationType MOUSE = new GuiNavigationType();
    public static final /* enum */ GuiNavigationType KEYBOARD_ARROW = new GuiNavigationType();
    public static final /* enum */ GuiNavigationType KEYBOARD_TAB = new GuiNavigationType();
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
