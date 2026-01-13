/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstance$AttenuationType
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.WeightedSoundSet
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.random.Random
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SoundInstance
extends FabricSoundInstance {
    public Identifier getId();

    public @Nullable WeightedSoundSet getSoundSet(SoundManager var1);

    public @Nullable Sound getSound();

    public SoundCategory getCategory();

    public boolean isRepeatable();

    public boolean isRelative();

    public int getRepeatDelay();

    public float getVolume();

    public float getPitch();

    public double getX();

    public double getY();

    public double getZ();

    public AttenuationType getAttenuationType();

    default public boolean shouldAlwaysPlay() {
        return false;
    }

    default public boolean canPlay() {
        return true;
    }

    public static Random createRandom() {
        return Random.create();
    }
}

