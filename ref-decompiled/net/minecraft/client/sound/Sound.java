/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.Sound$RegistrationType
 *  net.minecraft.client.sound.SoundContainer
 *  net.minecraft.client.sound.SoundSystem
 *  net.minecraft.resource.ResourceFinder
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.floatprovider.FloatSupplier
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class Sound
implements SoundContainer<Sound> {
    public static final ResourceFinder FINDER = new ResourceFinder("sounds", ".ogg");
    private final Identifier id;
    private final FloatSupplier volume;
    private final FloatSupplier pitch;
    private final int weight;
    private final RegistrationType registrationType;
    private final boolean stream;
    private final boolean preload;
    private final int attenuation;

    public Sound(Identifier id, FloatSupplier volume, FloatSupplier pitch, int weight, RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        this.id = id;
        this.volume = volume;
        this.pitch = pitch;
        this.weight = weight;
        this.registrationType = registrationType;
        this.stream = stream;
        this.preload = preload;
        this.attenuation = attenuation;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public Identifier getLocation() {
        return FINDER.toResourcePath(this.id);
    }

    public FloatSupplier getVolume() {
        return this.volume;
    }

    public FloatSupplier getPitch() {
        return this.pitch;
    }

    public int getWeight() {
        return this.weight;
    }

    public Sound getSound(Random random) {
        return this;
    }

    public void preload(SoundSystem soundSystem) {
        if (this.preload) {
            soundSystem.addPreloadedSound(this);
        }
    }

    public RegistrationType getRegistrationType() {
        return this.registrationType;
    }

    public boolean isStreamed() {
        return this.stream;
    }

    public boolean isPreloaded() {
        return this.preload;
    }

    public int getAttenuation() {
        return this.attenuation;
    }

    public String toString() {
        return "Sound[" + String.valueOf(this.id) + "]";
    }

    public /* synthetic */ Object getSound(Random random) {
        return this.getSound(random);
    }
}

