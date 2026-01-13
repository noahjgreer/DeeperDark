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
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public static class BiomeEffectSoundPlayer.MusicLoop
extends MovingSoundInstance {
    private int delta;
    private int strength;

    public BiomeEffectSoundPlayer.MusicLoop(SoundEvent sound) {
        super(sound, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.0f;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (this.strength < 0) {
            this.setDone();
        }
        this.strength += this.delta;
        this.volume = MathHelper.clamp((float)this.strength / 40.0f, 0.0f, 1.0f);
    }

    public void fadeOut() {
        this.strength = Math.min(this.strength, 40);
        this.delta = -1;
    }

    public void fadeIn() {
        this.strength = Math.max(0, this.strength);
        this.delta = 1;
    }
}
