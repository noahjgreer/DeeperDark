/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public record DebugHudEntryCategory(Text label, float sortKey) {
    public static final DebugHudEntryCategory TEXT = new DebugHudEntryCategory(Text.translatable("debug.options.category.text"), 1.0f);
    public static final DebugHudEntryCategory RENDERER = new DebugHudEntryCategory(Text.translatable("debug.options.category.renderer"), 2.0f);
}
