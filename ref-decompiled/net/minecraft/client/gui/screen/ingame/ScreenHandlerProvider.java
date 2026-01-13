/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider
 *  net.minecraft.screen.ScreenHandler
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.ScreenHandler;

@Environment(value=EnvType.CLIENT)
public interface ScreenHandlerProvider<T extends ScreenHandler> {
    public T getScreenHandler();
}

