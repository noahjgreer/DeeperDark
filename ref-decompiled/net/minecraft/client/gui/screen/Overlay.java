/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.screen.Overlay
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;

@Environment(value=EnvType.CLIENT)
public abstract class Overlay
implements Drawable {
    public boolean pausesGame() {
        return true;
    }

    public void tick() {
    }
}

