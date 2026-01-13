/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ButtonTextures(Identifier enabled, Identifier disabled, Identifier enabledFocused, Identifier disabledFocused) {
    private final Identifier enabled;
    private final Identifier disabled;
    private final Identifier enabledFocused;
    private final Identifier disabledFocused;

    public ButtonTextures(Identifier texture) {
        this(texture, texture, texture, texture);
    }

    public ButtonTextures(Identifier unfocused, Identifier focused) {
        this(unfocused, unfocused, focused, focused);
    }

    public ButtonTextures(Identifier enabled, Identifier disabled, Identifier focused) {
        this(enabled, disabled, focused, disabled);
    }

    public ButtonTextures(Identifier enabled, Identifier disabled, Identifier enabledFocused, Identifier disabledFocused) {
        this.enabled = enabled;
        this.disabled = disabled;
        this.enabledFocused = enabledFocused;
        this.disabledFocused = disabledFocused;
    }

    public Identifier get(boolean enabled, boolean focused) {
        if (enabled) {
            return focused ? this.enabledFocused : this.enabled;
        }
        return focused ? this.disabledFocused : this.disabled;
    }

    public Identifier enabled() {
        return this.enabled;
    }

    public Identifier disabled() {
        return this.disabled;
    }

    public Identifier enabledFocused() {
        return this.enabledFocused;
    }

    public Identifier disabledFocused() {
        return this.disabledFocused;
    }
}

