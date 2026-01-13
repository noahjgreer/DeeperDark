/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.minecraft.client.sound;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundListener;
import net.minecraft.client.sound.SoundListenerTransform;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Environment(value=EnvType.CLIENT)
public class SoundSystem {
    private static final Marker MARKER = MarkerFactory.getMarker((String)"SOUNDS");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float MIN_PITCH = 0.5f;
    private static final float MAX_PITCH = 2.0f;
    private static final float MIN_VOLUME = 0.0f;
    private static final float MAX_VOLUME = 1.0f;
    private static final int field_33025 = 20;
    private static final Set<Identifier> UNKNOWN_SOUNDS = Sets.newHashSet();
    private static final long MIN_TIME_INTERVAL_TO_RELOAD_SOUNDS = 1000L;
    public static final String FOR_THE_DEBUG = "FOR THE DEBUG!";
    public static final String OPENAL_SOFT_ON = "OpenAL Soft on ";
    public static final int OPENAL_SOFT_ON_LENGTH = "OpenAL Soft on ".length();
    private final SoundManager soundManager;
    private final GameOptions options;
    private boolean started;
    private final SoundEngine soundEngine = new SoundEngine();
    private final SoundListener listener = this.soundEngine.getListener();
    private final SoundLoader soundLoader;
    private final SoundExecutor taskQueue = new SoundExecutor();
    private final Channel channel = new Channel(this.soundEngine, this.taskQueue);
    private int ticks;
    private long lastSoundDeviceCheckTime;
    private final AtomicReference<DeviceChangeStatus> deviceChangeStatus = new AtomicReference<DeviceChangeStatus>(DeviceChangeStatus.NO_CHANGE);
    private final Map<SoundInstance, Channel.SourceManager> sources = Maps.newHashMap();
    private final Multimap<SoundCategory, SoundInstance> sounds = HashMultimap.create();
    private final Object2FloatMap<SoundCategory> volumes = (Object2FloatMap)Util.make(new Object2FloatOpenHashMap(), map -> map.defaultReturnValue(1.0f));
    private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
    private final Map<SoundInstance, Integer> soundStartTicks = Maps.newHashMap();
    private final Map<SoundInstance, Integer> soundEndTicks = Maps.newHashMap();
    private final List<SoundInstanceListener> listeners = Lists.newArrayList();
    private final List<TickableSoundInstance> soundsToPlayNextTick = Lists.newArrayList();
    private final List<Sound> preloadedSounds = Lists.newArrayList();

    public SoundSystem(SoundManager soundManager, GameOptions options, ResourceFactory resourceFactory) {
        this.soundManager = soundManager;
        this.options = options;
        this.soundLoader = new SoundLoader(resourceFactory);
    }

    public void reloadSounds() {
        UNKNOWN_SOUNDS.clear();
        for (SoundEvent soundEvent : Registries.SOUND_EVENT) {
            Identifier identifier;
            if (soundEvent == SoundEvents.INTENTIONALLY_EMPTY || this.soundManager.get(identifier = soundEvent.id()) != null) continue;
            LOGGER.warn("Missing sound for event: {}", (Object)Registries.SOUND_EVENT.getId(soundEvent));
            UNKNOWN_SOUNDS.add(identifier);
        }
        this.stop();
        this.start();
    }

    private synchronized void start() {
        if (this.started) {
            return;
        }
        try {
            String string = this.options.getSoundDevice().getValue();
            this.soundEngine.init("".equals(string) ? null : string, this.options.getDirectionalAudio().getValue());
            this.listener.init();
            this.soundLoader.loadStatic(this.preloadedSounds).thenRun(this.preloadedSounds::clear);
            this.started = true;
            LOGGER.info(MARKER, "Sound engine started");
        }
        catch (RuntimeException runtimeException) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeException);
        }
    }

    public void refreshSoundVolumes(SoundCategory category) {
        if (!this.started) {
            return;
        }
        this.sources.forEach((sound, manager) -> {
            if (category == sound.getCategory() || category == SoundCategory.MASTER) {
                float f = this.getAdjustedVolume((SoundInstance)sound);
                manager.run(source -> source.setVolume(f));
            }
        });
    }

    public void stop() {
        if (this.started) {
            this.stopAll();
            this.soundLoader.close();
            this.soundEngine.close();
            this.started = false;
        }
    }

    public void stopAbruptly() {
        if (this.started) {
            this.soundEngine.close();
        }
    }

    public void stop(SoundInstance sound) {
        Channel.SourceManager sourceManager;
        if (this.started && (sourceManager = this.sources.get(sound)) != null) {
            sourceManager.run(Source::stop);
        }
    }

    public void setVolume(SoundCategory category, float volume) {
        this.volumes.put((Object)category, MathHelper.clamp(volume, 0.0f, 1.0f));
        this.refreshSoundVolumes(category);
    }

    public void stopAll() {
        if (this.started) {
            this.taskQueue.stop();
            this.sources.clear();
            this.channel.close();
            this.soundStartTicks.clear();
            this.tickingSounds.clear();
            this.sounds.clear();
            this.soundEndTicks.clear();
            this.soundsToPlayNextTick.clear();
            this.volumes.clear();
            this.taskQueue.restart();
        }
    }

    public void registerListener(SoundInstanceListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(SoundInstanceListener listener) {
        this.listeners.remove(listener);
    }

    private boolean shouldReloadSounds() {
        boolean bl;
        if (this.soundEngine.isDeviceUnavailable()) {
            LOGGER.info("Audio device was lost!");
            return true;
        }
        long l = Util.getMeasuringTimeMs();
        boolean bl2 = bl = l - this.lastSoundDeviceCheckTime >= 1000L;
        if (bl) {
            this.lastSoundDeviceCheckTime = l;
            if (this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.NO_CHANGE, DeviceChangeStatus.ONGOING)) {
                String string = this.options.getSoundDevice().getValue();
                Util.getIoWorkerExecutor().execute(() -> {
                    if ("".equals(string)) {
                        if (this.soundEngine.updateDeviceSpecifier()) {
                            LOGGER.info("System default audio device has changed!");
                            this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.ONGOING, DeviceChangeStatus.CHANGE_DETECTED);
                        }
                    } else if (!this.soundEngine.getCurrentDeviceName().equals(string) && this.soundEngine.getSoundDevices().contains(string)) {
                        LOGGER.info("Preferred audio device has become available!");
                        this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.ONGOING, DeviceChangeStatus.CHANGE_DETECTED);
                    }
                    this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.ONGOING, DeviceChangeStatus.NO_CHANGE);
                });
            }
        }
        return this.deviceChangeStatus.compareAndSet(DeviceChangeStatus.CHANGE_DETECTED, DeviceChangeStatus.NO_CHANGE);
    }

    public void tick(boolean paused) {
        if (this.shouldReloadSounds()) {
            this.reloadSounds();
        }
        if (!paused) {
            this.tick();
        } else {
            this.tickPaused();
        }
        this.channel.tick();
    }

    private void tick() {
        ++this.ticks;
        this.soundsToPlayNextTick.stream().filter(SoundInstance::canPlay).forEach(this::play);
        this.soundsToPlayNextTick.clear();
        for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
            if (!tickableSoundInstance.canPlay()) {
                this.stop(tickableSoundInstance);
            }
            tickableSoundInstance.tick();
            if (tickableSoundInstance.isDone()) {
                this.stop(tickableSoundInstance);
                continue;
            }
            float f = this.getAdjustedVolume(tickableSoundInstance);
            float g = this.getAdjustedPitch(tickableSoundInstance);
            Vec3d vec3d = new Vec3d(tickableSoundInstance.getX(), tickableSoundInstance.getY(), tickableSoundInstance.getZ());
            Channel.SourceManager sourceManager = this.sources.get(tickableSoundInstance);
            if (sourceManager == null) continue;
            sourceManager.run(source -> {
                source.setVolume(f);
                source.setPitch(g);
                source.setPosition(vec3d);
            });
        }
        Iterator<Map.Entry<SoundInstance, Channel.SourceManager>> iterator = this.sources.entrySet().iterator();
        while (iterator.hasNext()) {
            int i;
            Map.Entry<SoundInstance, Channel.SourceManager> entry = iterator.next();
            Channel.SourceManager sourceManager2 = entry.getValue();
            SoundInstance soundInstance = entry.getKey();
            if (!sourceManager2.isStopped() || (i = this.soundEndTicks.get(soundInstance).intValue()) > this.ticks) continue;
            if (SoundSystem.shouldDelayRepeat(soundInstance)) {
                this.soundStartTicks.put(soundInstance, this.ticks + soundInstance.getRepeatDelay());
            }
            iterator.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)sourceManager2);
            this.soundEndTicks.remove(soundInstance);
            try {
                this.sounds.remove((Object)soundInstance.getCategory(), (Object)soundInstance);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (!(soundInstance instanceof TickableSoundInstance)) continue;
            this.tickingSounds.remove(soundInstance);
        }
        Iterator<Map.Entry<SoundInstance, Integer>> iterator2 = this.soundStartTicks.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry<SoundInstance, Integer> entry2 = iterator2.next();
            if (this.ticks < entry2.getValue()) continue;
            SoundInstance soundInstance = entry2.getKey();
            if (soundInstance instanceof TickableSoundInstance) {
                ((TickableSoundInstance)soundInstance).tick();
            }
            this.play(soundInstance);
            iterator2.remove();
        }
    }

    private void tickPaused() {
        Iterator<Map.Entry<SoundInstance, Channel.SourceManager>> iterator = this.sources.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SoundInstance, Channel.SourceManager> entry = iterator.next();
            Channel.SourceManager sourceManager = entry.getValue();
            SoundInstance soundInstance = entry.getKey();
            if (soundInstance.getCategory() != SoundCategory.MUSIC || !sourceManager.isStopped()) continue;
            iterator.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)sourceManager);
            this.soundEndTicks.remove(soundInstance);
            this.sounds.remove((Object)soundInstance.getCategory(), (Object)soundInstance);
        }
    }

    private static boolean hasRepeatDelay(SoundInstance sound) {
        return sound.getRepeatDelay() > 0;
    }

    private static boolean shouldDelayRepeat(SoundInstance sound) {
        return sound.isRepeatable() && SoundSystem.hasRepeatDelay(sound);
    }

    private static boolean shouldRepeatInstantly(SoundInstance sound) {
        return sound.isRepeatable() && !SoundSystem.hasRepeatDelay(sound);
    }

    public boolean isPlaying(SoundInstance sound) {
        if (!this.started) {
            return false;
        }
        if (this.soundEndTicks.containsKey(sound) && this.soundEndTicks.get(sound) <= this.ticks) {
            return true;
        }
        return this.sources.containsKey(sound);
    }

    public PlayResult play(SoundInstance sound2) {
        Sound sound22;
        if (!this.started) {
            return PlayResult.NOT_STARTED;
        }
        if (!sound2.canPlay()) {
            return PlayResult.NOT_STARTED;
        }
        WeightedSoundSet weightedSoundSet = sound2.getSoundSet(this.soundManager);
        Identifier identifier = sound2.getId();
        if (weightedSoundSet == null) {
            if (UNKNOWN_SOUNDS.add(identifier)) {
                LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)identifier);
            }
            if (!SharedConstants.SUBTITLES) {
                return PlayResult.NOT_STARTED;
            }
            weightedSoundSet = new WeightedSoundSet(identifier, FOR_THE_DEBUG);
        }
        if ((sound22 = sound2.getSound()) == SoundManager.INTENTIONALLY_EMPTY_SOUND) {
            return PlayResult.NOT_STARTED;
        }
        if (sound22 == SoundManager.MISSING_SOUND) {
            if (UNKNOWN_SOUNDS.add(identifier)) {
                LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)identifier);
            }
            return PlayResult.NOT_STARTED;
        }
        float f = sound2.getVolume();
        float g = Math.max(f, 1.0f) * (float)sound22.getAttenuation();
        SoundCategory soundCategory = sound2.getCategory();
        float h = this.getAdjustedVolume(f, soundCategory);
        float i = this.getAdjustedPitch(sound2);
        SoundInstance.AttenuationType attenuationType = sound2.getAttenuationType();
        boolean bl = sound2.isRelative();
        if (!this.listeners.isEmpty()) {
            float j = bl || attenuationType == SoundInstance.AttenuationType.NONE ? Float.POSITIVE_INFINITY : g;
            for (SoundInstanceListener soundInstanceListener : this.listeners) {
                soundInstanceListener.onSoundPlayed(sound2, weightedSoundSet, j);
            }
        }
        boolean bl2 = false;
        if (h == 0.0f) {
            if (sound2.shouldAlwaysPlay() || soundCategory == SoundCategory.MUSIC) {
                bl2 = true;
            } else {
                LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)sound22.getIdentifier());
                return PlayResult.NOT_STARTED;
            }
        }
        Vec3d vec3d = new Vec3d(sound2.getX(), sound2.getY(), sound2.getZ());
        boolean bl3 = SoundSystem.shouldRepeatInstantly(sound2);
        boolean bl4 = sound22.isStreamed();
        CompletableFuture<@Nullable Channel.SourceManager> completableFuture = this.channel.createSource(sound22.isStreamed() ? SoundEngine.RunMode.STREAMING : SoundEngine.RunMode.STATIC);
        Channel.SourceManager sourceManager = completableFuture.join();
        if (sourceManager == null) {
            if (SharedConstants.isDevelopment) {
                LOGGER.warn("Failed to create new sound handle");
            }
            return PlayResult.NOT_STARTED;
        }
        LOGGER.debug(MARKER, "Playing sound {} for event {}", (Object)sound22.getIdentifier(), (Object)identifier);
        this.soundEndTicks.put(sound2, this.ticks + 20);
        this.sources.put(sound2, sourceManager);
        this.sounds.put((Object)soundCategory, (Object)sound2);
        sourceManager.run(source -> {
            source.setPitch(i);
            source.setVolume(h);
            if (attenuationType == SoundInstance.AttenuationType.LINEAR) {
                source.setAttenuation(g);
            } else {
                source.disableAttenuation();
            }
            source.setLooping(bl3 && !bl4);
            source.setPosition(vec3d);
            source.setRelative(bl);
        });
        if (!bl4) {
            this.soundLoader.loadStatic(sound22.getLocation()).thenAccept(sound -> sourceManager.run(source -> {
                source.setBuffer((StaticSound)sound);
                source.play();
            }));
        } else {
            this.soundLoader.loadStreamed(sound22.getLocation(), bl3).thenAccept(stream -> sourceManager.run(source -> {
                source.setStream((AudioStream)stream);
                source.play();
            }));
        }
        if (sound2 instanceof TickableSoundInstance) {
            this.tickingSounds.add((TickableSoundInstance)sound2);
        }
        if (bl2) {
            return PlayResult.STARTED_SILENTLY;
        }
        return PlayResult.STARTED;
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundsToPlayNextTick.add(sound);
    }

    public void addPreloadedSound(Sound sound) {
        this.preloadedSounds.add(sound);
    }

    private float getAdjustedPitch(SoundInstance sound) {
        return MathHelper.clamp(sound.getPitch(), 0.5f, 2.0f);
    }

    private float getAdjustedVolume(SoundInstance sound) {
        return this.getAdjustedVolume(sound.getVolume(), sound.getCategory());
    }

    private float getAdjustedVolume(float volume, SoundCategory category) {
        return MathHelper.clamp(volume, 0.0f, 1.0f) * MathHelper.clamp(this.options.getSoundVolume(category), 0.0f, 1.0f) * this.volumes.getFloat((Object)category);
    }

    public void pauseAllExcept(SoundCategory ... categories) {
        if (!this.started) {
            return;
        }
        for (Map.Entry<SoundInstance, Channel.SourceManager> entry : this.sources.entrySet()) {
            if (List.of(categories).contains((Object)entry.getKey().getCategory())) continue;
            entry.getValue().run(Source::pause);
        }
    }

    public void resumeAll() {
        if (this.started) {
            this.channel.execute(sources -> sources.forEach(Source::resume));
        }
    }

    public void play(SoundInstance sound, int delay) {
        this.soundStartTicks.put(sound, this.ticks + delay);
    }

    public void updateListenerPosition(Camera camera) {
        if (!this.started || !camera.isReady()) {
            return;
        }
        SoundListenerTransform soundListenerTransform = new SoundListenerTransform(camera.getCameraPos(), new Vec3d(camera.getHorizontalPlane()), new Vec3d(camera.getVerticalPlane()));
        this.taskQueue.execute(() -> this.listener.setTransform(soundListenerTransform));
    }

    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory category) {
        if (category != null) {
            for (SoundInstance soundInstance : this.sounds.get((Object)category)) {
                if (id != null && !soundInstance.getId().equals(id)) continue;
                this.stop(soundInstance);
            }
        } else if (id == null) {
            this.stopAll();
        } else {
            for (SoundInstance soundInstance : this.sources.keySet()) {
                if (!soundInstance.getId().equals(id)) continue;
                this.stop(soundInstance);
            }
        }
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }

    public List<String> getSoundDevices() {
        return this.soundEngine.getSoundDevices();
    }

    public SoundListenerTransform getListenerTransform() {
        return this.listener.getTransform();
    }

    @Environment(value=EnvType.CLIENT)
    static final class DeviceChangeStatus
    extends Enum<DeviceChangeStatus> {
        public static final /* enum */ DeviceChangeStatus ONGOING = new DeviceChangeStatus();
        public static final /* enum */ DeviceChangeStatus CHANGE_DETECTED = new DeviceChangeStatus();
        public static final /* enum */ DeviceChangeStatus NO_CHANGE = new DeviceChangeStatus();
        private static final /* synthetic */ DeviceChangeStatus[] field_35087;

        public static DeviceChangeStatus[] values() {
            return (DeviceChangeStatus[])field_35087.clone();
        }

        public static DeviceChangeStatus valueOf(String string) {
            return Enum.valueOf(DeviceChangeStatus.class, string);
        }

        private static /* synthetic */ DeviceChangeStatus[] method_38939() {
            return new DeviceChangeStatus[]{ONGOING, CHANGE_DETECTED, NO_CHANGE};
        }

        static {
            field_35087 = DeviceChangeStatus.method_38939();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class PlayResult
    extends Enum<PlayResult> {
        public static final /* enum */ PlayResult STARTED = new PlayResult();
        public static final /* enum */ PlayResult STARTED_SILENTLY = new PlayResult();
        public static final /* enum */ PlayResult NOT_STARTED = new PlayResult();
        private static final /* synthetic */ PlayResult[] field_60957;

        public static PlayResult[] values() {
            return (PlayResult[])field_60957.clone();
        }

        public static PlayResult valueOf(String string) {
            return Enum.valueOf(PlayResult.class, string);
        }

        private static /* synthetic */ PlayResult[] method_72056() {
            return new PlayResult[]{STARTED, STARTED_SILENTLY, NOT_STARTED};
        }

        static {
            field_60957 = PlayResult.method_72056();
        }
    }
}
