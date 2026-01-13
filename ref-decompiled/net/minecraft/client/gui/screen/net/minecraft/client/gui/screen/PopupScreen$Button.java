/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
record PopupScreen.Button(Text message, Consumer<PopupScreen> action) {
}
