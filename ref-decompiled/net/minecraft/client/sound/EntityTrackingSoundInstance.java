/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.EntityTrackingSoundInstance
 *  net.minecraft.client.sound.MovingSoundInstance
 *  net.minecraft.entity.Entity
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class EntityTrackingSoundInstance
extends MovingSoundInstance {
    private final Entity entity;

    public EntityTrackingSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity, long seed) {
        super(sound, category, Random.create((long)seed));
        this.volume = volume;
        this.pitch = pitch;
        this.entity = entity;
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }

    public boolean canPlay() {
        return !this.entity.isSilent();
    }

    public void tick() {
        if (this.entity.isRemoved()) {
            this.setDone();
            return;
        }
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }
}

