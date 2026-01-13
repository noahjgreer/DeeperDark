/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.input.AbstractInput$Modifier
 *  net.minecraft.client.input.MouseInput
 *  net.minecraft.client.input.MouseInput$ButtonCode
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.input.MouseInput;

@Environment(value=EnvType.CLIENT)
public record MouseInput(@ButtonCode int button, @AbstractInput.Modifier int modifiers) implements AbstractInput
{
    @ButtonCode
    private final int button;
    @AbstractInput.Modifier
    private final int modifiers;

    public MouseInput(@ButtonCode int button, @AbstractInput.Modifier int modifiers) {
        this.button = button;
        this.modifiers = modifiers;
    }

    @ButtonCode
    public int getKeycode() {
        return this.button;
    }

    @ButtonCode
    public int button() {
        return this.button;
    }

    @AbstractInput.Modifier
    public int modifiers() {
        return this.modifiers;
    }
}

