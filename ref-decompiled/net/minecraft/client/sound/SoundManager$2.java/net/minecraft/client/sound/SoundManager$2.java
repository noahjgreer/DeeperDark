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
import net.minecraft.client.sound.Sound;

@Environment(value=EnvType.CLIENT)
static class SoundManager.2 {
    static final /* synthetic */ int[] field_5598;

    static {
        field_5598 = new int[Sound.RegistrationType.values().length];
        try {
            SoundManager.2.field_5598[Sound.RegistrationType.FILE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SoundManager.2.field_5598[Sound.RegistrationType.SOUND_EVENT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
