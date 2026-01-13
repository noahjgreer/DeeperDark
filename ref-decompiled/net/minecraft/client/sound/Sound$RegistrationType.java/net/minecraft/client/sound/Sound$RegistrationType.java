/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static final class Sound.RegistrationType
extends Enum<Sound.RegistrationType> {
    public static final /* enum */ Sound.RegistrationType FILE = new Sound.RegistrationType("file");
    public static final /* enum */ Sound.RegistrationType SOUND_EVENT = new Sound.RegistrationType("event");
    private final String name;
    private static final /* synthetic */ Sound.RegistrationType[] field_5471;

    public static Sound.RegistrationType[] values() {
        return (Sound.RegistrationType[])field_5471.clone();
    }

    public static Sound.RegistrationType valueOf(String string) {
        return Enum.valueOf(Sound.RegistrationType.class, string);
    }

    private Sound.RegistrationType(String name) {
        this.name = name;
    }

    public static @Nullable Sound.RegistrationType getByName(String name) {
        for (Sound.RegistrationType registrationType : Sound.RegistrationType.values()) {
            if (!registrationType.name.equals(name)) continue;
            return registrationType;
        }
        return null;
    }

    private static /* synthetic */ Sound.RegistrationType[] method_36926() {
        return new Sound.RegistrationType[]{FILE, SOUND_EVENT};
    }

    static {
        field_5471 = Sound.RegistrationType.method_36926();
    }
}
