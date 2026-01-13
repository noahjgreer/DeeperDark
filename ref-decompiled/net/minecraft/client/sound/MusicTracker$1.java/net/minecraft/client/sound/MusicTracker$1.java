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
import net.minecraft.client.sound.SoundSystem;

@Environment(value=EnvType.CLIENT)
static class MusicTracker.1 {
    static final /* synthetic */ int[] field_60952;

    static {
        field_60952 = new int[SoundSystem.PlayResult.values().length];
        try {
            MusicTracker.1.field_60952[SoundSystem.PlayResult.STARTED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            MusicTracker.1.field_60952[SoundSystem.PlayResult.STARTED_SILENTLY.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
