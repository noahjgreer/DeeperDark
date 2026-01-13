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
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.sound.Channel
 *  net.minecraft.client.sound.Channel$SourceManager
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.SoundEngine
 *  net.minecraft.client.sound.SoundExecutor
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstanceListener
 *  net.minecraft.client.sound.SoundListener
 *  net.minecraft.client.sound.SoundListenerTransform
 *  net.minecraft.client.sound.SoundLoader
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.SoundSystem
 *  net.minecraft.client.sound.SoundSystem$DeviceChangeStatus
 *  net.minecraft.client.sound.SoundSystem$PlayResult
 *  net.minecraft.client.sound.Source
 *  net.minecraft.client.sound.TickableSoundInstance
 *  net.minecraft.registry.Registries
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
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
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.Source;
import net.minecraft.client.sound.TickableSoundInstance;
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

/*
 * Exception performing whole class analysis ignored.
 */
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
    private final Channel channel = new Channel(this.soundEngine, (Executor)this.taskQueue);
    private int ticks;
    private long lastSoundDeviceCheckTime;
    private final AtomicReference<DeviceChangeStatus> deviceChangeStatus = new AtomicReference<DeviceChangeStatus>(DeviceChangeStatus.NO_CHANGE);
    private final Map<SoundInstance, Channel.SourceManager> sources = Maps.newHashMap();
    private final Multimap<SoundCategory, SoundInstance> sounds = HashMultimap.create();
    private final Object2FloatMap<SoundCategory> volumes = (Object2FloatMap)Util.make((Object)new Object2FloatOpenHashMap(), map -> map.defaultReturnValue(1.0f));
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
            LOGGER.warn("Missing sound for event: {}", (Object)Registries.SOUND_EVENT.getId((Object)soundEvent));
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
            String string = (String)this.options.getSoundDevice().getValue();
            this.soundEngine.init("".equals(string) ? null : string, ((Boolean)this.options.getDirectionalAudio().getValue()).booleanValue());
            this.listener.init();
            this.soundLoader.loadStatic((Collection)this.preloadedSounds).thenRun(this.preloadedSounds::clear);
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
                float f = this.getAdjustedVolume(sound);
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
        if (this.started && (sourceManager = (Channel.SourceManager)this.sources.get(sound)) != null) {
            sourceManager.run(Source::stop);
        }
    }

    public void setVolume(SoundCategory category, float volume) {
        this.volumes.put((Object)category, MathHelper.clamp((float)volume, (float)0.0f, (float)1.0f));
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
                String string = (String)this.options.getSoundDevice().getValue();
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
        this.soundsToPlayNextTick.stream().filter(SoundInstance::canPlay).forEach(arg_0 -> this.play(arg_0));
        this.soundsToPlayNextTick.clear();
        for (TickableSoundInstance tickableSoundInstance : this.tickingSounds) {
            if (!tickableSoundInstance.canPlay()) {
                this.stop((SoundInstance)tickableSoundInstance);
            }
            tickableSoundInstance.tick();
            if (tickableSoundInstance.isDone()) {
                this.stop((SoundInstance)tickableSoundInstance);
                continue;
            }
            float f = this.getAdjustedVolume((SoundInstance)tickableSoundInstance);
            float g = this.getAdjustedPitch((SoundInstance)tickableSoundInstance);
            Vec3d vec3d = new Vec3d(tickableSoundInstance.getX(), tickableSoundInstance.getY(), tickableSoundInstance.getZ());
            Channel.SourceManager sourceManager = (Channel.SourceManager)this.sources.get(tickableSoundInstance);
            if (sourceManager == null) continue;
            sourceManager.run(source -> {
                source.setVolume(f);
                source.setPitch(g);
                source.setPosition(vec3d);
            });
        }
        Iterator iterator = this.sources.entrySet().iterator();
        while (iterator.hasNext()) {
            int i;
            Map.Entry entry = iterator.next();
            Channel.SourceManager sourceManager2 = (Channel.SourceManager)entry.getValue();
            SoundInstance soundInstance = (SoundInstance)entry.getKey();
            if (!sourceManager2.isStopped() || (i = ((Integer)this.soundEndTicks.get(soundInstance)).intValue()) > this.ticks) continue;
            if (SoundSystem.shouldDelayRepeat((SoundInstance)soundInstance)) {
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
        Iterator iterator2 = this.soundStartTicks.entrySet().iterator();
        while (iterator2.hasNext()) {
            Map.Entry entry2 = iterator2.next();
            if (this.ticks < (Integer)entry2.getValue()) continue;
            SoundInstance soundInstance = (SoundInstance)entry2.getKey();
            if (soundInstance instanceof TickableSoundInstance) {
                ((TickableSoundInstance)soundInstance).tick();
            }
            this.play(soundInstance);
            iterator2.remove();
        }
    }

    private void tickPaused() {
        Iterator iterator = this.sources.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            Channel.SourceManager sourceManager = (Channel.SourceManager)entry.getValue();
            SoundInstance soundInstance = (SoundInstance)entry.getKey();
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
        return sound.isRepeatable() && SoundSystem.hasRepeatDelay((SoundInstance)sound);
    }

    private static boolean shouldRepeatInstantly(SoundInstance sound) {
        return sound.isRepeatable() && !SoundSystem.hasRepeatDelay((SoundInstance)sound);
    }

    public boolean isPlaying(SoundInstance sound) {
        if (!this.started) {
            return false;
        }
        if (this.soundEndTicks.containsKey(sound) && (Integer)this.soundEndTicks.get(sound) <= this.ticks) {
            return true;
        }
        return this.sources.containsKey(sound);
    }

    /*
     * Exception decompiling
     */
    public PlayResult play(SoundInstance sound2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.NullPointerException: Cannot invoke "org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated.pathIterator()" because the return value of "org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated.access$300(org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated)" is null
         *     at org.benf.cfr.reader.bytecode.analysis.types.JavaRefTypeInstance$Annotated$Iterator.moveNested(JavaRefTypeInstance.java:200)
         *     at org.benf.cfr.reader.entities.attributes.TypePathPartNested.apply(TypePathPartNested.java:14)
         *     at org.benf.cfr.reader.bytecode.analysis.types.TypeAnnotationHelper.apply(TypeAnnotationHelper.java:54)
         *     at org.benf.cfr.reader.bytecode.analysis.types.TypeAnnotationHelper.apply(TypeAnnotationHelper.java:45)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.handleStatement(TypeAnnotationTransformer.java:141)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredAssignment.rewriteExpressions(StructuredAssignment.java:144)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.transform(TypeAnnotationTransformer.java:61)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.transform(Op04StructuredStatement.java:680)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.transformStructuredChildren(Block.java:421)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.transform(TypeAnnotationTransformer.java:60)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.transform(Op04StructuredStatement.java:680)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.transformers.TypeAnnotationTransformer.transform(TypeAnnotationTransformer.java:55)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.applyTypeAnnotations(Op04StructuredStatement.java:733)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:957)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundsToPlayNextTick.add(sound);
    }

    public void addPreloadedSound(Sound sound) {
        this.preloadedSounds.add(sound);
    }

    private float getAdjustedPitch(SoundInstance sound) {
        return MathHelper.clamp((float)sound.getPitch(), (float)0.5f, (float)2.0f);
    }

    private float getAdjustedVolume(SoundInstance sound) {
        return this.getAdjustedVolume(sound.getVolume(), sound.getCategory());
    }

    private float getAdjustedVolume(float volume, SoundCategory category) {
        return MathHelper.clamp((float)volume, (float)0.0f, (float)1.0f) * MathHelper.clamp((float)this.options.getSoundVolume(category), (float)0.0f, (float)1.0f) * this.volumes.getFloat((Object)category);
    }

    public void pauseAllExcept(SoundCategory ... categories) {
        if (!this.started) {
            return;
        }
        for (Map.Entry entry : this.sources.entrySet()) {
            if (List.of(categories).contains(((SoundInstance)entry.getKey()).getCategory())) continue;
            ((Channel.SourceManager)entry.getValue()).run(Source::pause);
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
                if (id != null && !soundInstance.getId().equals((Object)id)) continue;
                this.stop(soundInstance);
            }
        } else if (id == null) {
            this.stopAll();
        } else {
            for (SoundInstance soundInstance : this.sources.keySet()) {
                if (!soundInstance.getId().equals((Object)id)) continue;
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
}

