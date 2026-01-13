/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;

public static interface ScreenHandlerType.Factory<T extends ScreenHandler> {
    public T create(int var1, PlayerInventory var2);
}
