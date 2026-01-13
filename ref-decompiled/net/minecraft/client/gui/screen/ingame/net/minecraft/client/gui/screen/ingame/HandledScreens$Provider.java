/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static interface HandledScreens.Provider<T extends ScreenHandler, U extends Screen> {
    default public void open(Text name, ScreenHandlerType<T> type, MinecraftClient client, int id) {
        U screen = this.create(type.create(id, client.player.getInventory()), client.player.getInventory(), name);
        client.player.currentScreenHandler = ((ScreenHandlerProvider)screen).getScreenHandler();
        client.setScreen((Screen)screen);
    }

    public U create(T var1, PlayerInventory var2, Text var3);
}
