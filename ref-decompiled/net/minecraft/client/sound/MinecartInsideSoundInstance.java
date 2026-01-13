/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.EntityRidingSoundInstance
 *  net.minecraft.client.sound.MinecartInsideSoundInstance
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.vehicle.AbstractMinecartEntity
 *  net.minecraft.entity.vehicle.ExperimentalMinecartController
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.EntityRidingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

@Environment(value=EnvType.CLIENT)
public class MinecartInsideSoundInstance
extends EntityRidingSoundInstance {
    private final PlayerEntity player;
    private final AbstractMinecartEntity minecart;
    private final boolean underwater;

    public MinecartInsideSoundInstance(PlayerEntity player, AbstractMinecartEntity minecart, boolean underwater, SoundEvent sound, float minVolume, float maxVolume, float multiplier) {
        super(player, (Entity)minecart, underwater, sound, SoundCategory.NEUTRAL, minVolume, maxVolume, multiplier);
        this.player = player;
        this.minecart = minecart;
        this.underwater = underwater;
    }

    protected boolean cannotPlayUnderwater() {
        return this.underwater != this.player.isSubmergedInWater();
    }

    protected float getVehicleSpeed() {
        return (float)this.minecart.getVelocity().horizontalLength();
    }

    protected boolean canPlaySound() {
        return this.minecart.isOnRail() || !(this.minecart.getController() instanceof ExperimentalMinecartController);
    }
}

