/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.debug.DebugHudEntryCategory
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public record DebugHudEntryCategory(Text label, float sortKey) {
    private final Text label;
    private final float sortKey;
    public static final DebugHudEntryCategory TEXT = new DebugHudEntryCategory((Text)Text.translatable((String)"debug.options.category.text"), 1.0f);
    public static final DebugHudEntryCategory RENDERER = new DebugHudEntryCategory((Text)Text.translatable((String)"debug.options.category.renderer"), 2.0f);

    public DebugHudEntryCategory(Text label, float sortKey) {
        this.label = label;
        this.sortKey = sortKey;
    }

    public Text label() {
        return this.label;
    }

    public float sortKey() {
        return this.sortKey;
    }
}

