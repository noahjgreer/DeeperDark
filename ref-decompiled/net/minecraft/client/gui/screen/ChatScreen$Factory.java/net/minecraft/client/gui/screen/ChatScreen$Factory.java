/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface ChatScreen.Factory<T extends ChatScreen> {
    public T create(String var1, boolean var2);
}
