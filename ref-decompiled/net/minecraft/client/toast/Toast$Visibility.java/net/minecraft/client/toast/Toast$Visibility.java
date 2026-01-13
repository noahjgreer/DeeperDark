/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

@Environment(value=EnvType.CLIENT)
public static final class Toast.Visibility
extends Enum<Toast.Visibility> {
    public static final /* enum */ Toast.Visibility SHOW = new Toast.Visibility(SoundEvents.UI_TOAST_IN);
    public static final /* enum */ Toast.Visibility HIDE = new Toast.Visibility(SoundEvents.UI_TOAST_OUT);
    private final SoundEvent sound;
    private static final /* synthetic */ Toast.Visibility[] field_2212;

    public static Toast.Visibility[] values() {
        return (Toast.Visibility[])field_2212.clone();
    }

    public static Toast.Visibility valueOf(String string) {
        return Enum.valueOf(Toast.Visibility.class, string);
    }

    private Toast.Visibility(SoundEvent sound) {
        this.sound = sound;
    }

    public void playSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.ui(this.sound, 1.0f, 1.0f));
    }

    private static /* synthetic */ Toast.Visibility[] method_36872() {
        return new Toast.Visibility[]{SHOW, HIDE};
    }

    static {
        field_2212 = Toast.Visibility.method_36872();
    }
}
