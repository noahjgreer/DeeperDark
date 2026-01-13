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
static final class SoundSystem.DeviceChangeStatus
extends Enum<SoundSystem.DeviceChangeStatus> {
    public static final /* enum */ SoundSystem.DeviceChangeStatus ONGOING = new SoundSystem.DeviceChangeStatus();
    public static final /* enum */ SoundSystem.DeviceChangeStatus CHANGE_DETECTED = new SoundSystem.DeviceChangeStatus();
    public static final /* enum */ SoundSystem.DeviceChangeStatus NO_CHANGE = new SoundSystem.DeviceChangeStatus();
    private static final /* synthetic */ SoundSystem.DeviceChangeStatus[] field_35087;

    public static SoundSystem.DeviceChangeStatus[] values() {
        return (SoundSystem.DeviceChangeStatus[])field_35087.clone();
    }

    public static SoundSystem.DeviceChangeStatus valueOf(String string) {
        return Enum.valueOf(SoundSystem.DeviceChangeStatus.class, string);
    }

    private static /* synthetic */ SoundSystem.DeviceChangeStatus[] method_38939() {
        return new SoundSystem.DeviceChangeStatus[]{ONGOING, CHANGE_DETECTED, NO_CHANGE};
    }

    static {
        field_35087 = SoundSystem.DeviceChangeStatus.method_38939();
    }
}
