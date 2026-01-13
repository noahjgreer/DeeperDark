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
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;

@Environment(value=EnvType.CLIENT)
public record Font.FontFilterPair(Font provider, FontFilterType.FilterMap filter) implements AutoCloseable
{
    @Override
    public void close() {
        this.provider.close();
    }
}
