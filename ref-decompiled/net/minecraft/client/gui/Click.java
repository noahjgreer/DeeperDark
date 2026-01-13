/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.input.AbstractInput$Modifier
 *  net.minecraft.client.input.MouseInput
 *  net.minecraft.client.input.MouseInput$ButtonCode
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.input.MouseInput;

@Environment(value=EnvType.CLIENT)
public record Click(double x, double y, MouseInput buttonInfo) implements AbstractInput
{
    private final double x;
    private final double y;
    private final MouseInput buttonInfo;

    public Click(double x, double y, MouseInput buttonInfo) {
        this.x = x;
        this.y = y;
        this.buttonInfo = buttonInfo;
    }

    public int getKeycode() {
        return this.button();
    }

    @MouseInput.ButtonCode
    public int button() {
        return this.buttonInfo().button();
    }

    @AbstractInput.Modifier
    public int modifiers() {
        return this.buttonInfo().modifiers();
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public MouseInput buttonInfo() {
        return this.buttonInfo;
    }
}

