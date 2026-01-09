package net.minecraft.client.sound;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Environment(EnvType.CLIENT)
public class SoundSystem {
   private static final Marker MARKER = MarkerFactory.getMarker("SOUNDS");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float MIN_PITCH = 0.5F;
   private static final float MAX_PITCH = 2.0F;
   private static final float MIN_VOLUME = 0.0F;
   private static final float MAX_VOLUME = 1.0F;
   private static final int field_33025 = 20;
   private static final Set UNKNOWN_SOUNDS = Sets.newHashSet();
   private static final long MIN_TIME_INTERVAL_TO_RELOAD_SOUNDS = 1000L;
   public static final String FOR_THE_DEBUG = "FOR THE DEBUG!";
   public static final String OPENAL_SOFT_ON = "OpenAL Soft on ";
   public static final int OPENAL_SOFT_ON_LENGTH = "OpenAL Soft on ".length();
   private final MusicTracker musicTracker;
   private final SoundManager soundManager;
   private final GameOptions options;
   private boolean started;
   private final SoundEngine soundEngine = new SoundEngine();
   private final SoundListener listener;
   private final SoundLoader soundLoader;
   private final SoundExecutor taskQueue;
   private final Channel channel;
   private int ticks;
   private long lastSoundDeviceCheckTime;
   private final AtomicReference deviceChangeStatus;
   private final Map sources;
   private final Multimap sounds;
   private final List tickingSounds;
   private final Map soundStartTicks;
   private final Map soundEndTicks;
   private final List listeners;
   private final List soundsToPlayNextTick;
   private final List preloadedSounds;

   public SoundSystem(MusicTracker musicTracker, SoundManager soundManager, GameOptions options, ResourceFactory resourceFactory) {
      this.listener = this.soundEngine.getListener();
      this.taskQueue = new SoundExecutor();
      this.channel = new Channel(this.soundEngine, this.taskQueue);
      this.deviceChangeStatus = new AtomicReference(SoundSystem.DeviceChangeStatus.NO_CHANGE);
      this.sources = Maps.newHashMap();
      this.sounds = HashMultimap.create();
      this.tickingSounds = Lists.newArrayList();
      this.soundStartTicks = Maps.newHashMap();
      this.soundEndTicks = Maps.newHashMap();
      this.listeners = Lists.newArrayList();
      this.soundsToPlayNextTick = Lists.newArrayList();
      this.preloadedSounds = Lists.newArrayList();
      this.musicTracker = musicTracker;
      this.soundManager = soundManager;
      this.options = options;
      this.soundLoader = new SoundLoader(resourceFactory);
   }

   public void reloadSounds() {
      UNKNOWN_SOUNDS.clear();
      Iterator var1 = Registries.SOUND_EVENT.iterator();

      while(var1.hasNext()) {
         SoundEvent soundEvent = (SoundEvent)var1.next();
         if (soundEvent != SoundEvents.INTENTIONALLY_EMPTY) {
            Identifier identifier = soundEvent.id();
            if (this.soundManager.get(identifier) == null) {
               LOGGER.warn("Missing sound for event: {}", Registries.SOUND_EVENT.getId(soundEvent));
               UNKNOWN_SOUNDS.add(identifier);
            }
         }
      }

      this.stop();
      this.start();
   }

   private synchronized void start() {
      if (!this.started) {
         try {
            String string = (String)this.options.getSoundDevice().getValue();
            this.soundEngine.init("".equals(string) ? null : string, (Boolean)this.options.getDirectionalAudio().getValue());
            this.listener.init();
            this.listener.method_72232(this.options.getCategorySoundVolume(SoundCategory.MASTER));
            CompletableFuture var10000 = this.soundLoader.loadStatic((Collection)this.preloadedSounds);
            List var10001 = this.preloadedSounds;
            Objects.requireNonNull(var10001);
            var10000.thenRun(var10001::clear);
            this.started = true;
            LOGGER.info(MARKER, "Sound engine started");
         } catch (RuntimeException var2) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", var2);
         }

      }
   }

   private float method_72233(@Nullable SoundCategory soundCategory) {
      return soundCategory != null && soundCategory != SoundCategory.MASTER ? this.options.getCategorySoundVolume(soundCategory) : 1.0F;
   }

   public void updateSoundVolume(SoundCategory soundCategory, float f) {
      if (this.started) {
         if (soundCategory == SoundCategory.MASTER) {
            this.listener.method_72232(f);
         } else {
            if (soundCategory == SoundCategory.MUSIC && this.options.getCategorySoundVolume(SoundCategory.MUSIC) > 0.0F) {
               this.musicTracker.tryShowToast();
            }

            this.sources.forEach((source, sourceManager) -> {
               float f = this.getAdjustedVolume(source);
               sourceManager.run((sourcex) -> {
                  sourcex.setVolume(f);
               });
            });
         }
      }
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
      if (this.started) {
         Channel.SourceManager sourceManager = (Channel.SourceManager)this.sources.get(sound);
         if (sourceManager != null) {
            sourceManager.run(Source::stop);
         }
      }

   }

   public void setVolume(SoundInstance sound, float volume) {
      if (this.started) {
         Channel.SourceManager sourceManager = (Channel.SourceManager)this.sources.get(sound);
         if (sourceManager != null) {
            sourceManager.run((source) -> {
               source.setVolume(volume * this.getAdjustedVolume(sound));
            });
         }
      }

   }

   public void stopAll() {
      if (this.started) {
         this.taskQueue.restart();
         this.sources.values().forEach((source) -> {
            source.run(Source::stop);
         });
         this.sources.clear();
         this.channel.close();
         this.soundStartTicks.clear();
         this.tickingSounds.clear();
         this.sounds.clear();
         this.soundEndTicks.clear();
         this.soundsToPlayNextTick.clear();
      }

   }

   public void registerListener(SoundInstanceListener listener) {
      this.listeners.add(listener);
   }

   public void unregisterListener(SoundInstanceListener listener) {
      this.listeners.remove(listener);
   }

   private boolean shouldReloadSounds() {
      if (this.soundEngine.isDeviceUnavailable()) {
         LOGGER.info("Audio device was lost!");
         return true;
      } else {
         long l = Util.getMeasuringTimeMs();
         boolean bl = l - this.lastSoundDeviceCheckTime >= 1000L;
         if (bl) {
            this.lastSoundDeviceCheckTime = l;
            if (this.deviceChangeStatus.compareAndSet(SoundSystem.DeviceChangeStatus.NO_CHANGE, SoundSystem.DeviceChangeStatus.ONGOING)) {
               String string = (String)this.options.getSoundDevice().getValue();
               Util.getIoWorkerExecutor().execute(() -> {
                  if ("".equals(string)) {
                     if (this.soundEngine.updateDeviceSpecifier()) {
                        LOGGER.info("System default audio device has changed!");
                        this.deviceChangeStatus.compareAndSet(SoundSystem.DeviceChangeStatus.ONGOING, SoundSystem.DeviceChangeStatus.CHANGE_DETECTED);
                     }
                  } else if (!this.soundEngine.getCurrentDeviceName().equals(string) && this.soundEngine.getSoundDevices().contains(string)) {
                     LOGGER.info("Preferred audio device has become available!");
                     this.deviceChangeStatus.compareAndSet(SoundSystem.DeviceChangeStatus.ONGOING, SoundSystem.DeviceChangeStatus.CHANGE_DETECTED);
                  }

                  this.deviceChangeStatus.compareAndSet(SoundSystem.DeviceChangeStatus.ONGOING, SoundSystem.DeviceChangeStatus.NO_CHANGE);
               });
            }
         }

         return this.deviceChangeStatus.compareAndSet(SoundSystem.DeviceChangeStatus.CHANGE_DETECTED, SoundSystem.DeviceChangeStatus.NO_CHANGE);
      }
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
      Iterator iterator = this.tickingSounds.iterator();

      while(iterator.hasNext()) {
         TickableSoundInstance tickableSoundInstance = (TickableSoundInstance)iterator.next();
         if (!tickableSoundInstance.canPlay()) {
            this.stop(tickableSoundInstance);
         }

         tickableSoundInstance.tick();
         if (tickableSoundInstance.isDone()) {
            this.stop(tickableSoundInstance);
         } else {
            float f = this.getAdjustedVolume(tickableSoundInstance);
            float g = this.getAdjustedPitch(tickableSoundInstance);
            Vec3d vec3d = new Vec3d(tickableSoundInstance.getX(), tickableSoundInstance.getY(), tickableSoundInstance.getZ());
            Channel.SourceManager sourceManager = (Channel.SourceManager)this.sources.get(tickableSoundInstance);
            if (sourceManager != null) {
               sourceManager.run((source) -> {
                  source.setVolume(f);
                  source.setPitch(g);
                  source.setPosition(vec3d);
               });
            }
         }
      }

      iterator = this.sources.entrySet().iterator();

      SoundInstance soundInstance;
      while(iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         Channel.SourceManager sourceManager2 = (Channel.SourceManager)entry.getValue();
         soundInstance = (SoundInstance)entry.getKey();
         if (sourceManager2.isStopped()) {
            int i = (Integer)this.soundEndTicks.get(soundInstance);
            if (i <= this.ticks) {
               if (shouldDelayRepeat(soundInstance)) {
                  this.soundStartTicks.put(soundInstance, this.ticks + soundInstance.getRepeatDelay());
               }

               iterator.remove();
               LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", sourceManager2);
               this.soundEndTicks.remove(soundInstance);

               try {
                  this.sounds.remove(soundInstance.getCategory(), soundInstance);
               } catch (RuntimeException var7) {
               }

               if (soundInstance instanceof TickableSoundInstance) {
                  this.tickingSounds.remove(soundInstance);
               }
            }
         }
      }

      Iterator iterator2 = this.soundStartTicks.entrySet().iterator();

      while(iterator2.hasNext()) {
         Map.Entry entry2 = (Map.Entry)iterator2.next();
         if (this.ticks >= (Integer)entry2.getValue()) {
            soundInstance = (SoundInstance)entry2.getKey();
            if (soundInstance instanceof TickableSoundInstance) {
               ((TickableSoundInstance)soundInstance).tick();
            }

            this.play(soundInstance);
            iterator2.remove();
         }
      }

   }

   private void tickPaused() {
      Iterator iterator = this.sources.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         Channel.SourceManager sourceManager = (Channel.SourceManager)entry.getValue();
         SoundInstance soundInstance = (SoundInstance)entry.getKey();
         if (soundInstance.getCategory() == SoundCategory.MUSIC && sourceManager.isStopped()) {
            iterator.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", sourceManager);
            this.soundEndTicks.remove(soundInstance);
            this.sounds.remove(soundInstance.getCategory(), soundInstance);
         }
      }

   }

   private static boolean hasRepeatDelay(SoundInstance sound) {
      return sound.getRepeatDelay() > 0;
   }

   private static boolean shouldDelayRepeat(SoundInstance sound) {
      return sound.isRepeatable() && hasRepeatDelay(sound);
   }

   private static boolean shouldRepeatInstantly(SoundInstance sound) {
      return sound.isRepeatable() && !hasRepeatDelay(sound);
   }

   public boolean isPlaying(SoundInstance sound) {
      if (!this.started) {
         return false;
      } else {
         return this.soundEndTicks.containsKey(sound) && (Integer)this.soundEndTicks.get(sound) <= this.ticks ? true : this.sources.containsKey(sound);
      }
   }

   public PlayResult play(SoundInstance sound) {
      if (!this.started) {
         return SoundSystem.PlayResult.NOT_STARTED;
      } else if (!sound.canPlay()) {
         return SoundSystem.PlayResult.NOT_STARTED;
      } else {
         WeightedSoundSet weightedSoundSet = sound.getSoundSet(this.soundManager);
         Identifier identifier = sound.getId();
         if (weightedSoundSet == null) {
            if (UNKNOWN_SOUNDS.add(identifier)) {
               LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", identifier);
            }

            return SoundSystem.PlayResult.NOT_STARTED;
         } else {
            Sound sound2 = sound.getSound();
            if (sound2 == SoundManager.INTENTIONALLY_EMPTY_SOUND) {
               return SoundSystem.PlayResult.NOT_STARTED;
            } else if (sound2 == SoundManager.MISSING_SOUND) {
               if (UNKNOWN_SOUNDS.add(identifier)) {
                  LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", identifier);
               }

               return SoundSystem.PlayResult.NOT_STARTED;
            } else {
               float f = sound.getVolume();
               float g = Math.max(f, 1.0F) * (float)sound2.getAttenuation();
               SoundCategory soundCategory = sound.getCategory();
               float h = this.getAdjustedVolume(f, soundCategory);
               float i = this.getAdjustedPitch(sound);
               SoundInstance.AttenuationType attenuationType = sound.getAttenuationType();
               boolean bl = sound.isRelative();
               if (!this.listeners.isEmpty()) {
                  float j = !bl && attenuationType != SoundInstance.AttenuationType.NONE ? g : Float.POSITIVE_INFINITY;
                  Iterator var13 = this.listeners.iterator();

                  while(var13.hasNext()) {
                     SoundInstanceListener soundInstanceListener = (SoundInstanceListener)var13.next();
                     soundInstanceListener.onSoundPlayed(sound, weightedSoundSet, j);
                  }
               }

               boolean bl2 = false;
               if (h == 0.0F) {
                  if (!sound.shouldAlwaysPlay() && soundCategory != SoundCategory.MUSIC) {
                     LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", sound2.getIdentifier());
                     return SoundSystem.PlayResult.NOT_STARTED;
                  }

                  bl2 = true;
               }

               Vec3d vec3d = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
               if (this.listener.method_72231() <= 0.0F && soundCategory != SoundCategory.MUSIC) {
                  LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", identifier);
                  return SoundSystem.PlayResult.NOT_STARTED;
               } else {
                  boolean bl3 = shouldRepeatInstantly(sound);
                  boolean bl4 = sound2.isStreamed();
                  CompletableFuture completableFuture = this.channel.createSource(sound2.isStreamed() ? SoundEngine.RunMode.STREAMING : SoundEngine.RunMode.STATIC);
                  Channel.SourceManager sourceManager = (Channel.SourceManager)completableFuture.join();
                  if (sourceManager == null) {
                     if (SharedConstants.isDevelopment) {
                        LOGGER.warn("Failed to create new sound handle");
                     }

                     return SoundSystem.PlayResult.NOT_STARTED;
                  } else {
                     LOGGER.debug(MARKER, "Playing sound {} for event {}", sound2.getIdentifier(), identifier);
                     this.soundEndTicks.put(sound, this.ticks + 20);
                     this.sources.put(sound, sourceManager);
                     this.sounds.put(soundCategory, sound);
                     sourceManager.run((source) -> {
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
                        this.soundLoader.loadStatic(sound2.getLocation()).thenAccept((soundx) -> {
                           sourceManager.run((source) -> {
                              source.setBuffer(soundx);
                              source.play();
                           });
                        });
                     } else {
                        this.soundLoader.loadStreamed(sound2.getLocation(), bl3).thenAccept((stream) -> {
                           sourceManager.run((source) -> {
                              source.setStream(stream);
                              source.play();
                           });
                        });
                     }

                     if (sound instanceof TickableSoundInstance) {
                        this.tickingSounds.add((TickableSoundInstance)sound);
                     }

                     return bl2 ? SoundSystem.PlayResult.STARTED_SILENTLY : SoundSystem.PlayResult.STARTED;
                  }
               }
            }
         }
      }
   }

   public void playNextTick(TickableSoundInstance sound) {
      this.soundsToPlayNextTick.add(sound);
   }

   public void addPreloadedSound(Sound sound) {
      this.preloadedSounds.add(sound);
   }

   private float getAdjustedPitch(SoundInstance sound) {
      return MathHelper.clamp(sound.getPitch(), 0.5F, 2.0F);
   }

   private float getAdjustedVolume(SoundInstance sound) {
      return this.getAdjustedVolume(sound.getVolume(), sound.getCategory());
   }

   private float getAdjustedVolume(float volume, SoundCategory category) {
      return MathHelper.clamp(volume * this.method_72233(category), 0.0F, 1.0F);
   }

   public void pauseAllExcept(SoundCategory... categories) {
      if (this.started) {
         Iterator var2 = this.sources.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry entry = (Map.Entry)var2.next();
            if (!List.of(categories).contains(((SoundInstance)entry.getKey()).getCategory())) {
               ((Channel.SourceManager)entry.getValue()).run(Source::pause);
            }
         }

      }
   }

   public void resumeAll() {
      if (this.started) {
         this.channel.execute((sources) -> {
            sources.forEach(Source::resume);
         });
      }

   }

   public void play(SoundInstance sound, int delay) {
      this.soundStartTicks.put(sound, this.ticks + delay);
   }

   public void updateListenerPosition(Camera camera) {
      if (this.started && camera.isReady()) {
         SoundListenerTransform soundListenerTransform = new SoundListenerTransform(camera.getPos(), new Vec3d(camera.getHorizontalPlane()), new Vec3d(camera.getVerticalPlane()));
         this.taskQueue.execute(() -> {
            this.listener.setTransform(soundListenerTransform);
         });
      }
   }

   public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory category) {
      Iterator var3;
      SoundInstance soundInstance;
      if (category != null) {
         var3 = this.sounds.get(category).iterator();

         while(true) {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               soundInstance = (SoundInstance)var3.next();
            } while(id != null && !soundInstance.getId().equals(id));

            this.stop(soundInstance);
         }
      } else if (id == null) {
         this.stopAll();
      } else {
         var3 = this.sources.keySet().iterator();

         while(var3.hasNext()) {
            soundInstance = (SoundInstance)var3.next();
            if (soundInstance.getId().equals(id)) {
               this.stop(soundInstance);
            }
         }
      }

   }

   public String getDebugString() {
      return this.soundEngine.getDebugString();
   }

   public List getSoundDevices() {
      return this.soundEngine.getSoundDevices();
   }

   public SoundListenerTransform getListenerTransform() {
      return this.listener.getTransform();
   }

   @Environment(EnvType.CLIENT)
   private static enum DeviceChangeStatus {
      ONGOING,
      CHANGE_DETECTED,
      NO_CHANGE;

      // $FF: synthetic method
      private static DeviceChangeStatus[] method_38939() {
         return new DeviceChangeStatus[]{ONGOING, CHANGE_DETECTED, NO_CHANGE};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum PlayResult {
      STARTED,
      STARTED_SILENTLY,
      NOT_STARTED;

      // $FF: synthetic method
      private static PlayResult[] method_72056() {
         return new PlayResult[]{STARTED, STARTED_SILENTLY, NOT_STARTED};
      }
   }
}
