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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(value=EnvType.CLIENT)
public static class AmbientSoundLoops.MusicLoop
extends MovingSoundInstance {
    private final ClientPlayerEntity player;

    protected AmbientSoundLoops.MusicLoop(ClientPlayerEntity player, SoundEvent soundEvent) {
        super(soundEvent, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = false;
        this.repeatDelay = 0;
        this.volume = 1.0f;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (this.player.isRemoved() || !this.player.isSubmergedInWater()) {
            this.setDone();
        }
    }
}
