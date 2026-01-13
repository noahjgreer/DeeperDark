/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.Sound$RegistrationType
 *  net.minecraft.client.sound.SoundEntry
 *  net.minecraft.client.sound.SoundEntryDeserializer
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstanceListener
 *  net.minecraft.client.sound.SoundListenerTransform
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.SoundManager$SoundList
 *  net.minecraft.client.sound.SoundSystem
 *  net.minecraft.client.sound.SoundSystem$PlayResult
 *  net.minecraft.client.sound.TickableSoundInstance
 *  net.minecraft.client.sound.WeightedSoundSet
 *  net.minecraft.registry.Registries
 *  net.minecraft.resource.Resource
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SinglePreparationResourceReloader
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.JsonHelper
 *  net.minecraft.util.math.floatprovider.ConstantFloatProvider
 *  net.minecraft.util.math.floatprovider.FloatSupplier
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.util.profiler.ScopedProfiler
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEntryDeserializer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundListenerTransform;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ScopedProfiler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SoundManager
extends SinglePreparationResourceReloader<SoundList> {
    public static final Identifier EMPTY_ID = Identifier.ofVanilla((String)"empty");
    public static final Sound MISSING_SOUND = new Sound(EMPTY_ID, (FloatSupplier)ConstantFloatProvider.create((float)1.0f), (FloatSupplier)ConstantFloatProvider.create((float)1.0f), 1, Sound.RegistrationType.FILE, false, false, 16);
    public static final Identifier INTENTIONALLY_EMPTY_ID = Identifier.ofVanilla((String)"intentionally_empty");
    public static final WeightedSoundSet INTENTIONALLY_EMPTY_SOUND_SET = new WeightedSoundSet(INTENTIONALLY_EMPTY_ID, null);
    public static final Sound INTENTIONALLY_EMPTY_SOUND = new Sound(INTENTIONALLY_EMPTY_ID, (FloatSupplier)ConstantFloatProvider.create((float)1.0f), (FloatSupplier)ConstantFloatProvider.create((float)1.0f), 1, Sound.RegistrationType.FILE, false, false, 16);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String SOUNDS_JSON = "sounds.json";
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundEntry.class, (Object)new SoundEntryDeserializer()).create();
    private static final TypeToken<Map<String, SoundEntry>> TYPE = new /* Unavailable Anonymous Inner Class!! */;
    private final Map<Identifier, WeightedSoundSet> sounds = Maps.newHashMap();
    private final SoundSystem soundSystem;
    private final Map<Identifier, Resource> soundResources = new HashMap();

    public SoundManager(GameOptions gameOptions) {
        this.soundSystem = new SoundSystem(this, gameOptions, ResourceFactory.fromMap((Map)this.soundResources));
    }

    protected SoundList prepare(ResourceManager resourceManager, Profiler profiler) {
        SoundList soundList = new SoundList();
        try (ScopedProfiler scopedProfiler = profiler.scoped("list");){
            soundList.findSounds(resourceManager);
        }
        for (String string : resourceManager.getAllNamespaces()) {
            try {
                ScopedProfiler scopedProfiler2 = profiler.scoped(string);
                try {
                    List list = resourceManager.getAllResources(Identifier.of((String)string, (String)SOUNDS_JSON));
                    for (Resource resource : list) {
                        profiler.push(resource.getPackId());
                        try (BufferedReader reader = resource.getReader();){
                            profiler.push("parse");
                            Map map = (Map)JsonHelper.deserialize((Gson)GSON, (Reader)reader, (TypeToken)TYPE);
                            profiler.swap("register");
                            for (Map.Entry entry : map.entrySet()) {
                                soundList.register(Identifier.of((String)string, (String)((String)entry.getKey())), (SoundEntry)entry.getValue());
                            }
                            profiler.pop();
                        }
                        catch (RuntimeException runtimeException) {
                            LOGGER.warn("Invalid {} in resourcepack: '{}'", new Object[]{SOUNDS_JSON, resource.getPackId(), runtimeException});
                        }
                        profiler.pop();
                    }
                }
                finally {
                    if (scopedProfiler2 == null) continue;
                    scopedProfiler2.close();
                }
            }
            catch (IOException iOException) {}
        }
        return soundList;
    }

    protected void apply(SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
        soundList.reload(this.sounds, this.soundResources, this.soundSystem);
        if (SharedConstants.isDevelopment) {
            for (Identifier identifier : this.sounds.keySet()) {
                WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
                if (Texts.hasTranslation((Text)weightedSoundSet.getSubtitle()) || !Registries.SOUND_EVENT.containsId(identifier)) continue;
                LOGGER.error("Missing subtitle {} for sound event: {}", (Object)weightedSoundSet.getSubtitle(), (Object)identifier);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            for (Identifier identifier : this.sounds.keySet()) {
                if (Registries.SOUND_EVENT.containsId(identifier)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)identifier);
            }
        }
        this.soundSystem.reloadSounds();
    }

    public List<String> getSoundDevices() {
        return this.soundSystem.getSoundDevices();
    }

    public SoundListenerTransform getListenerTransform() {
        return this.soundSystem.getListenerTransform();
    }

    static boolean isSoundResourcePresent(Sound sound, Identifier id, ResourceFactory resourceFactory) {
        Identifier identifier = sound.getLocation();
        if (resourceFactory.getResource(identifier).isEmpty()) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)identifier, (Object)id);
            return false;
        }
        return true;
    }

    public @Nullable WeightedSoundSet get(Identifier id) {
        return (WeightedSoundSet)this.sounds.get(id);
    }

    public Collection<Identifier> getKeys() {
        return this.sounds.keySet();
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundSystem.playNextTick(sound);
    }

    public SoundSystem.PlayResult play(SoundInstance sound) {
        return this.soundSystem.play(sound);
    }

    public void play(SoundInstance sound, int delay) {
        this.soundSystem.play(sound, delay);
    }

    public void updateListenerPosition(Camera camera) {
        this.soundSystem.updateListenerPosition(camera);
    }

    public void pauseAllExcept(SoundCategory ... categories) {
        this.soundSystem.pauseAllExcept(categories);
    }

    public void stopAll() {
        this.soundSystem.stopAll();
    }

    public void close() {
        this.soundSystem.stop();
    }

    public void stopAbruptly() {
        this.soundSystem.stopAbruptly();
    }

    public void tick(boolean paused) {
        this.soundSystem.tick(paused);
    }

    public void resumeAll() {
        this.soundSystem.resumeAll();
    }

    public void refreshSoundVolumes(SoundCategory category) {
        this.soundSystem.refreshSoundVolumes(category);
    }

    public void stop(SoundInstance sound) {
        this.soundSystem.stop(sound);
    }

    public void setVolume(SoundCategory category, float volume) {
        this.soundSystem.setVolume(category, volume);
    }

    public boolean isPlaying(SoundInstance sound) {
        return this.soundSystem.isPlaying(sound);
    }

    public void registerListener(SoundInstanceListener listener) {
        this.soundSystem.registerListener(listener);
    }

    public void unregisterListener(SoundInstanceListener listener) {
        this.soundSystem.unregisterListener(listener);
    }

    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory soundCategory) {
        this.soundSystem.stopSounds(id, soundCategory);
    }

    public String getDebugString() {
        return this.soundSystem.getDebugString();
    }

    public void reloadSounds() {
        this.soundSystem.reloadSounds();
    }

    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

