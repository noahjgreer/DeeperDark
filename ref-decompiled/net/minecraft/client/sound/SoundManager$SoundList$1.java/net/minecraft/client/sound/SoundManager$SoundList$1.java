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
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.MultipliedFloatSupplier;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
class SoundManager.SoundList.1
implements SoundContainer<Sound> {
    final /* synthetic */ Identifier field_5596;
    final /* synthetic */ Sound field_5595;

    SoundManager.SoundList.1(Identifier identifier, Sound sound) {
        this.field_5596 = identifier;
        this.field_5595 = sound;
    }

    @Override
    public int getWeight() {
        WeightedSoundSet weightedSoundSet = SoundList.this.loadedSounds.get(this.field_5596);
        return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
    }

    @Override
    public Sound getSound(Random random) {
        WeightedSoundSet weightedSoundSet = SoundList.this.loadedSounds.get(this.field_5596);
        if (weightedSoundSet == null) {
            return MISSING_SOUND;
        }
        Sound sound = weightedSoundSet.getSound(random);
        return new Sound(sound.getIdentifier(), new MultipliedFloatSupplier(sound.getVolume(), this.field_5595.getVolume()), new MultipliedFloatSupplier(sound.getPitch(), this.field_5595.getPitch()), this.field_5595.getWeight(), Sound.RegistrationType.FILE, sound.isStreamed() || this.field_5595.isStreamed(), sound.isPreloaded(), sound.getAttenuation());
    }

    @Override
    public void preload(SoundSystem soundSystem) {
        WeightedSoundSet weightedSoundSet = SoundList.this.loadedSounds.get(this.field_5596);
        if (weightedSoundSet == null) {
            return;
        }
        weightedSoundSet.preload(soundSystem);
    }

    @Override
    public /* synthetic */ Object getSound(Random random) {
        return this.getSound(random);
    }
}
