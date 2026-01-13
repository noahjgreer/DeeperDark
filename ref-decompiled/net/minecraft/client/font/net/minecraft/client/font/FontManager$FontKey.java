/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
record FontManager.FontKey(Identifier fontId, String pack, int index) {
    @Override
    public String toString() {
        return "(" + String.valueOf(this.fontId) + ": builder #" + this.index + " from pack " + this.pack + ")";
    }
}
