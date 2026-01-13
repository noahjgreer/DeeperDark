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
public static final class SoundSystem.PlayResult
extends Enum<SoundSystem.PlayResult> {
    public static final /* enum */ SoundSystem.PlayResult STARTED = new SoundSystem.PlayResult();
    public static final /* enum */ SoundSystem.PlayResult STARTED_SILENTLY = new SoundSystem.PlayResult();
    public static final /* enum */ SoundSystem.PlayResult NOT_STARTED = new SoundSystem.PlayResult();
    private static final /* synthetic */ SoundSystem.PlayResult[] field_60957;

    public static SoundSystem.PlayResult[] values() {
        return (SoundSystem.PlayResult[])field_60957.clone();
    }

    public static SoundSystem.PlayResult valueOf(String string) {
        return Enum.valueOf(SoundSystem.PlayResult.class, string);
    }

    private static /* synthetic */ SoundSystem.PlayResult[] method_72056() {
        return new SoundSystem.PlayResult[]{STARTED, STARTED_SILENTLY, NOT_STARTED};
    }

    static {
        field_60957 = SoundSystem.PlayResult.method_72056();
    }
}
