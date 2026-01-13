/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.util.StringHelper;

@Environment(value=EnvType.CLIENT)
public record CharInput(int codepoint, @AbstractInput.Modifier int modifiers) {
    public String asString() {
        return Character.toString(this.codepoint);
    }

    public boolean isValidChar() {
        return StringHelper.isValidChar(this.codepoint);
    }
}
