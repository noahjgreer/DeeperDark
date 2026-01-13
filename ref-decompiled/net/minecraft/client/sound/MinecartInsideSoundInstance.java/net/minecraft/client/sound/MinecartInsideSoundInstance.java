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
import net.minecraft.client.sound.EntityRidingSoundInstance;
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
        super(player, minecart, underwater, sound, SoundCategory.NEUTRAL, minVolume, maxVolume, multiplier);
        this.player = player;
        this.minecart = minecart;
        this.underwater = underwater;
    }

    @Override
    protected boolean cannotPlayUnderwater() {
        return this.underwater != this.player.isSubmergedInWater();
    }

    @Override
    protected float getVehicleSpeed() {
        return (float)this.minecart.getVelocity().horizontalLength();
    }

    @Override
    protected boolean canPlaySound() {
        return this.minecart.isOnRail() || !(this.minecart.getController() instanceof ExperimentalMinecartController);
    }
}
