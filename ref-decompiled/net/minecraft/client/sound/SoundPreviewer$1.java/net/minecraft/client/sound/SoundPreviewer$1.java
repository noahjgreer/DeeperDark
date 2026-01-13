/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;

@Environment(value=EnvType.CLIENT)
static class SoundPreviewer.1 {
    static final /* synthetic */ int[] field_62994;

    static {
        field_62994 = new int[SoundCategory.values().length];
        try {
            SoundPreviewer.1.field_62994[SoundCategory.RECORDS.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundPreviewer.1.field_62994[SoundCategory.WEATHER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundPreviewer.1.field_62994[SoundCategory.BLOCKS.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundPreviewer.1.field_62994[SoundCategory.HOSTILE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundPreviewer.1.field_62994[SoundCategory.NEUTRAL.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundPreviewer.1.field_62994[SoundCategory.PLAYERS.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundPreviewer.1.field_62994[SoundCategory.AMBIENT.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundPreviewer.1.field_62994[SoundCategory.UI.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
