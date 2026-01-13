/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.AbstractSoundInstance
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstance$AttenuationType
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.WeightedSoundSet
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.random.Random
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSoundInstance
implements SoundInstance {
    protected @Nullable Sound sound;
    protected final SoundCategory category;
    protected final Identifier id;
    protected float volume = 1.0f;
    protected float pitch = 1.0f;
    protected double x;
    protected double y;
    protected double z;
    protected boolean repeat;
    protected int repeatDelay;
    protected SoundInstance.AttenuationType attenuationType = SoundInstance.AttenuationType.LINEAR;
    protected boolean relative;
    protected Random random;

    protected AbstractSoundInstance(SoundEvent sound, SoundCategory category, Random random) {
        this(sound.id(), category, random);
    }

    protected AbstractSoundInstance(Identifier soundId, SoundCategory category, Random random) {
        this.id = soundId;
        this.category = category;
        this.random = random;
    }

    public Identifier getId() {
        return this.id;
    }

    public @Nullable WeightedSoundSet getSoundSet(SoundManager soundManager) {
        if (this.id.equals((Object)SoundManager.INTENTIONALLY_EMPTY_ID)) {
            this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
            return SoundManager.INTENTIONALLY_EMPTY_SOUND_SET;
        }
        WeightedSoundSet weightedSoundSet = soundManager.get(this.id);
        this.sound = weightedSoundSet == null ? SoundManager.MISSING_SOUND : weightedSoundSet.getSound(this.random);
        return weightedSoundSet;
    }

    public @Nullable Sound getSound() {
        return this.sound;
    }

    public SoundCategory getCategory() {
        return this.category;
    }

    public boolean isRepeatable() {
        return this.repeat;
    }

    public int getRepeatDelay() {
        return this.repeatDelay;
    }

    public float getVolume() {
        return this.volume * this.sound.getVolume().get(this.random);
    }

    public float getPitch() {
        return this.pitch * this.sound.getPitch().get(this.random);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public SoundInstance.AttenuationType getAttenuationType() {
        return this.attenuationType;
    }

    public boolean isRelative() {
        return this.relative;
    }

    public String toString() {
        return "SoundInstance[" + String.valueOf(this.id) + "]";
    }
}

