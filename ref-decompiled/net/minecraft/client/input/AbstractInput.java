/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.input.AbstractInput$Modifier
 *  net.minecraft.client.input.SystemKeycodes
 *  net.minecraft.client.util.InputUtil$Keycode
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.util.InputUtil;

@Environment(value=EnvType.CLIENT)
public interface AbstractInput {
    public static final int NOT_A_NUMBER = -1;

    @InputUtil.Keycode
    public int getKeycode();

    @Modifier
    public int modifiers();

    default public boolean isEnterOrSpace() {
        return this.getKeycode() == 257 || this.getKeycode() == 32 || this.getKeycode() == 335;
    }

    default public boolean isEnter() {
        return this.getKeycode() == 257 || this.getKeycode() == 335;
    }

    default public boolean isEscape() {
        return this.getKeycode() == 256;
    }

    default public boolean isLeft() {
        return this.getKeycode() == 263;
    }

    default public boolean isRight() {
        return this.getKeycode() == 262;
    }

    default public boolean isUp() {
        return this.getKeycode() == 265;
    }

    default public boolean isDown() {
        return this.getKeycode() == 264;
    }

    default public boolean isTab() {
        return this.getKeycode() == 258;
    }

    default public int asNumber() {
        int i = this.getKeycode() - 48;
        if (i >= 0 && i <= 9) {
            return i;
        }
        return -1;
    }

    default public boolean hasAlt() {
        return (this.modifiers() & 4) != 0;
    }

    default public boolean hasShift() {
        return (this.modifiers() & 1) != 0;
    }

    default public boolean hasCtrl() {
        return (this.modifiers() & 2) != 0;
    }

    default public boolean hasCtrlOrCmd() {
        return (this.modifiers() & SystemKeycodes.CTRL_MOD) != 0;
    }

    default public boolean isSelectAll() {
        return this.getKeycode() == 65 && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
    }

    default public boolean isCopy() {
        return this.getKeycode() == 67 && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
    }

    default public boolean isPaste() {
        return this.getKeycode() == 86 && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
    }

    default public boolean isCut() {
        return this.getKeycode() == 88 && this.hasCtrlOrCmd() && !this.hasShift() && !this.hasAlt();
    }
}

