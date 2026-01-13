/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.InactivityFpsLimit;
import net.minecraft.client.option.TextureFilteringMode;
import net.minecraft.client.render.ChunkBuilderMode;

@Environment(value=EnvType.CLIENT)
static class GameOptions.5 {
    static final /* synthetic */ int[] field_64662;
    static final /* synthetic */ int[] field_37883;
    static final /* synthetic */ int[] field_52763;

    static {
        field_52763 = new int[InactivityFpsLimit.values().length];
        try {
            GameOptions.5.field_52763[InactivityFpsLimit.MINIMIZED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameOptions.5.field_52763[InactivityFpsLimit.AFK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_37883 = new int[ChunkBuilderMode.values().length];
        try {
            GameOptions.5.field_37883[ChunkBuilderMode.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameOptions.5.field_37883[ChunkBuilderMode.PLAYER_AFFECTED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameOptions.5.field_37883[ChunkBuilderMode.NEARBY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_64662 = new int[TextureFilteringMode.values().length];
        try {
            GameOptions.5.field_64662[TextureFilteringMode.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameOptions.5.field_64662[TextureFilteringMode.RGSS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            GameOptions.5.field_64662[TextureFilteringMode.ANISOTROPIC.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
