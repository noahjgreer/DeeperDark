/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.EntityRidingSoundInstance
 *  net.minecraft.client.sound.MovingSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstance$AttenuationType
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EntityRidingSoundInstance
extends MovingSoundInstance {
    private final PlayerEntity player;
    private final Entity vehicle;
    private final boolean forUnderwater;
    private final float minVolume;
    private final float maxVolume;
    private final float multiplier;

    public EntityRidingSoundInstance(PlayerEntity player, Entity vehicle, boolean forUnderwater, SoundEvent sound, SoundCategory category, float volume, float maxVolume, float multiplier) {
        super(sound, category, SoundInstance.createRandom());
        this.player = player;
        this.vehicle = vehicle;
        this.forUnderwater = forUnderwater;
        this.minVolume = volume;
        this.maxVolume = maxVolume;
        this.multiplier = multiplier;
        this.attenuationType = SoundInstance.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = volume;
    }

    public boolean canPlay() {
        return !this.vehicle.isSilent();
    }

    public boolean shouldAlwaysPlay() {
        return true;
    }

    protected boolean cannotPlayUnderwater() {
        return this.forUnderwater != this.vehicle.isSubmergedInWater();
    }

    protected float getVehicleSpeed() {
        return (float)this.vehicle.getVelocity().length();
    }

    protected boolean canPlaySound() {
        return true;
    }

    public void tick() {
        if (this.vehicle.isRemoved() || !this.player.hasVehicle() || this.player.getVehicle() != this.vehicle) {
            this.setDone();
            return;
        }
        if (this.cannotPlayUnderwater()) {
            this.volume = this.minVolume;
            return;
        }
        float f = this.getVehicleSpeed();
        this.volume = f >= 0.01f && this.canPlaySound() ? this.multiplier * MathHelper.clampedLerp((float)f, (float)this.minVolume, (float)this.maxVolume) : this.minVolume;
    }
}

