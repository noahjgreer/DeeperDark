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
public static final class SoundEngine.RunMode
extends Enum<SoundEngine.RunMode> {
    public static final /* enum */ SoundEngine.RunMode STATIC = new SoundEngine.RunMode();
    public static final /* enum */ SoundEngine.RunMode STREAMING = new SoundEngine.RunMode();
    private static final /* synthetic */ SoundEngine.RunMode[] field_18354;

    public static SoundEngine.RunMode[] values() {
        return (SoundEngine.RunMode[])field_18354.clone();
    }

    public static SoundEngine.RunMode valueOf(String string) {
        return Enum.valueOf(SoundEngine.RunMode.class, string);
    }

    private static /* synthetic */ SoundEngine.RunMode[] method_36800() {
        return new SoundEngine.RunMode[]{STATIC, STREAMING};
    }

    static {
        field_18354 = SoundEngine.RunMode.method_36800();
    }
}
