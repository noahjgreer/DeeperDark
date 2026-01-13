/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.GuiElementRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface GuiElementRenderState {
    public @Nullable ScreenRect bounds();
}

