/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.MultipliedFloatSupplier;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
protected static class SoundManager.SoundList {
    final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.newHashMap();
    private Map<Identifier, Resource> foundSounds = Map.of();

    protected SoundManager.SoundList() {
    }

    void findSounds(ResourceManager resourceManager) {
        this.foundSounds = Sound.FINDER.findResources(resourceManager);
    }

    void register(Identifier id, SoundEntry entry) {
        boolean bl;
        WeightedSoundSet weightedSoundSet = this.loadedSounds.get(id);
        boolean bl2 = bl = weightedSoundSet == null;
        if (bl || entry.canReplace()) {
            if (!bl) {
                LOGGER.debug("Replaced sound event location {}", (Object)id);
            }
            weightedSoundSet = new WeightedSoundSet(id, entry.getSubtitle());
            this.loadedSounds.put(id, weightedSoundSet);
        }
        ResourceFactory resourceFactory = ResourceFactory.fromMap(this.foundSounds);
        block4: for (final Sound sound : entry.getSounds()) {
            final Identifier identifier = sound.getIdentifier();
            weightedSoundSet.add(switch (sound.getRegistrationType()) {
                case Sound.RegistrationType.FILE -> {
                    if (!SoundManager.isSoundResourcePresent(sound, id, resourceFactory)) continue block4;
                    yield sound;
                }
                case Sound.RegistrationType.SOUND_EVENT -> new SoundContainer<Sound>(){

                    @Override
                    public int getWeight() {
                        WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                        return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
                    }

                    @Override
                    public Sound getSound(Random random) {
                        WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                        if (weightedSoundSet == null) {
                            return MISSING_SOUND;
                        }
                        Sound sound2 = weightedSoundSet.getSound(random);
                        return new Sound(sound2.getIdentifier(), new MultipliedFloatSupplier(sound2.getVolume(), sound.getVolume()), new MultipliedFloatSupplier(sound2.getPitch(), sound.getPitch()), sound.getWeight(), Sound.RegistrationType.FILE, sound2.isStreamed() || sound.isStreamed(), sound2.isPreloaded(), sound2.getAttenuation());
                    }

                    @Override
                    public void preload(SoundSystem soundSystem) {
                        WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                        if (weightedSoundSet == null) {
                            return;
                        }
                        weightedSoundSet.preload(soundSystem);
                    }

                    @Override
                    public /* synthetic */ Object getSound(Random random) {
                        return this.getSound(random);
                    }
                };
                default -> throw new IllegalStateException("Unknown SoundEventRegistration type: " + String.valueOf((Object)sound.getRegistrationType()));
            });
        }
    }

    public void reload(Map<Identifier, WeightedSoundSet> sounds, Map<Identifier, Resource> soundResources, SoundSystem system) {
        sounds.clear();
        soundResources.clear();
        soundResources.putAll(this.foundSounds);
        for (Map.Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
            sounds.put(entry.getKey(), entry.getValue());
            entry.getValue().preload(system);
        }
    }
}
