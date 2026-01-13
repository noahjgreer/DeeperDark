/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.input.AbstractInput$Modifier
 *  net.minecraft.client.input.CharInput
 *  net.minecraft.util.StringHelper
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.util.StringHelper;

@Environment(value=EnvType.CLIENT)
public record CharInput(int codepoint, @AbstractInput.Modifier int modifiers) {
    private final int codepoint;
    @AbstractInput.Modifier
    private final int modifiers;

    public CharInput(int codepoint, @AbstractInput.Modifier int modifiers) {
        this.codepoint = codepoint;
        this.modifiers = modifiers;
    }

    public String asString() {
        return Character.toString(this.codepoint);
    }

    public boolean isValidChar() {
        return StringHelper.isValidChar((int)this.codepoint);
    }

    public int codepoint() {
        return this.codepoint;
    }

    @AbstractInput.Modifier
    public int modifiers() {
        return this.modifiers;
    }
}

