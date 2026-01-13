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

@Environment(value=EnvType.CLIENT)
public static final class SoundInstance.AttenuationType
extends Enum<SoundInstance.AttenuationType> {
    public static final /* enum */ SoundInstance.AttenuationType NONE = new SoundInstance.AttenuationType();
    public static final /* enum */ SoundInstance.AttenuationType LINEAR = new SoundInstance.AttenuationType();
    private static final /* synthetic */ SoundInstance.AttenuationType[] field_5477;

    public static SoundInstance.AttenuationType[] values() {
        return (SoundInstance.AttenuationType[])field_5477.clone();
    }

    public static SoundInstance.AttenuationType valueOf(String string) {
        return Enum.valueOf(SoundInstance.AttenuationType.class, string);
    }

    private static /* synthetic */ SoundInstance.AttenuationType[] method_36927() {
        return new SoundInstance.AttenuationType[]{NONE, LINEAR};
    }

    static {
        field_5477 = SoundInstance.AttenuationType.method_36927();
    }
}
