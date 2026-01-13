/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.input.AbstractInput$Modifier
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.util.InputUtil$Keycode
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.util.InputUtil;

@Environment(value=EnvType.CLIENT)
public record KeyInput(@InputUtil.Keycode int key, int scancode, @AbstractInput.Modifier int modifiers) implements AbstractInput
{
    @InputUtil.Keycode
    private final int key;
    private final int scancode;
    @AbstractInput.Modifier
    private final int modifiers;

    public KeyInput(@InputUtil.Keycode int key, int scancode, @AbstractInput.Modifier int modifiers) {
        this.key = key;
        this.scancode = scancode;
        this.modifiers = modifiers;
    }

    public int getKeycode() {
        return this.key;
    }

    @InputUtil.Keycode
    public int key() {
        return this.key;
    }

    public int scancode() {
        return this.scancode;
    }

    @AbstractInput.Modifier
    public int modifiers() {
        return this.modifiers;
    }
}

