package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.math.floatprovider.MultipliedFloatSupplier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ScopedProfiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SoundManager extends SinglePreparationResourceReloader {
   public static final Identifier EMPTY_ID = Identifier.ofVanilla("empty");
   public static final Sound MISSING_SOUND;
   public static final Identifier INTENTIONALLY_EMPTY_ID;
   public static final WeightedSoundSet INTENTIONALLY_EMPTY_SOUND_SET;
   public static final Sound INTENTIONALLY_EMPTY_SOUND;
   static final Logger LOGGER;
   private static final String SOUNDS_JSON = "sounds.json";
   private static final Gson GSON;
   private static final TypeToken TYPE;
   private final Map sounds = Maps.newHashMap();
   private final SoundSystem soundSystem;
   private final Map soundResources = new HashMap();

   public SoundManager(GameOptions gameOptions, MusicTracker musicTracker) {
      this.soundSystem = new SoundSystem(musicTracker, this, gameOptions, ResourceFactory.fromMap(this.soundResources));
   }

   protected SoundList prepare(ResourceManager resourceManager, Profiler profiler) {
      SoundList soundList = new SoundList();
      ScopedProfiler scopedProfiler = profiler.scoped("list");

      try {
         soundList.findSounds(resourceManager);
      } catch (Throwable var17) {
         if (scopedProfiler != null) {
            try {
               scopedProfiler.close();
            } catch (Throwable var14) {
               var17.addSuppressed(var14);
            }
         }

         throw var17;
      }

      if (scopedProfiler != null) {
         scopedProfiler.close();
      }

      Iterator var22 = resourceManager.getAllNamespaces().iterator();

      while(var22.hasNext()) {
         String string = (String)var22.next();

         try {
            ScopedProfiler scopedProfiler2 = profiler.scoped(string);

            try {
               List list = resourceManager.getAllResources(Identifier.of(string, "sounds.json"));

               for(Iterator var8 = list.iterator(); var8.hasNext(); profiler.pop()) {
                  Resource resource = (Resource)var8.next();
                  profiler.push(resource.getPackId());

                  try {
                     Reader reader = resource.getReader();

                     try {
                        profiler.push("parse");
                        Map map = (Map)JsonHelper.deserialize(GSON, (Reader)reader, (TypeToken)TYPE);
                        profiler.swap("register");
                        Iterator var12 = map.entrySet().iterator();

                        while(true) {
                           if (!var12.hasNext()) {
                              profiler.pop();
                              break;
                           }

                           Map.Entry entry = (Map.Entry)var12.next();
                           soundList.register(Identifier.of(string, (String)entry.getKey()), (SoundEntry)entry.getValue());
                        }
                     } catch (Throwable var18) {
                        if (reader != null) {
                           try {
                              reader.close();
                           } catch (Throwable var16) {
                              var18.addSuppressed(var16);
                           }
                        }

                        throw var18;
                     }

                     if (reader != null) {
                        reader.close();
                     }
                  } catch (RuntimeException var19) {
                     LOGGER.warn("Invalid {} in resourcepack: '{}'", new Object[]{"sounds.json", resource.getPackId(), var19});
                  }
               }
            } catch (Throwable var20) {
               if (scopedProfiler2 != null) {
                  try {
                     scopedProfiler2.close();
                  } catch (Throwable var15) {
                     var20.addSuppressed(var15);
                  }
               }

               throw var20;
            }

            if (scopedProfiler2 != null) {
               scopedProfiler2.close();
            }
         } catch (IOException var21) {
         }
      }

      return soundList;
   }

   protected void apply(SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
      soundList.reload(this.sounds, this.soundResources, this.soundSystem);
      Iterator var4;
      Identifier identifier;
      if (SharedConstants.isDevelopment) {
         var4 = this.sounds.keySet().iterator();

         while(var4.hasNext()) {
            identifier = (Identifier)var4.next();
            WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.sounds.get(identifier);
            if (!Texts.hasTranslation(weightedSoundSet.getSubtitle()) && Registries.SOUND_EVENT.containsId(identifier)) {
               LOGGER.error("Missing subtitle {} for sound event: {}", weightedSoundSet.getSubtitle(), identifier);
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         var4 = this.sounds.keySet().iterator();

         while(var4.hasNext()) {
            identifier = (Identifier)var4.next();
            if (!Registries.SOUND_EVENT.containsId(identifier)) {
               LOGGER.debug("Not having sound event for: {}", identifier);
            }
         }
      }

      this.soundSystem.reloadSounds();
   }

   public List getSoundDevices() {
      return this.soundSystem.getSoundDevices();
   }

   public SoundListenerTransform getListenerTransform() {
      return this.soundSystem.getListenerTransform();
   }

   static boolean isSoundResourcePresent(Sound sound, Identifier id, ResourceFactory resourceFactory) {
      Identifier identifier = sound.getLocation();
      if (resourceFactory.getResource(identifier).isEmpty()) {
         LOGGER.warn("File {} does not exist, cannot add it to event {}", identifier, id);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public WeightedSoundSet get(Identifier id) {
      return (WeightedSoundSet)this.sounds.get(id);
   }

   public Collection getKeys() {
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

   public void pauseAllExcept(SoundCategory... categories) {
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

   public void updateSoundVolume(SoundCategory soundCategory, float f) {
      this.soundSystem.updateSoundVolume(soundCategory, f);
   }

   public void stop(SoundInstance sound) {
      this.soundSystem.stop(sound);
   }

   public void setVolume(SoundInstance sound, float volume) {
      this.soundSystem.setVolume(sound, volume);
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

   // $FF: synthetic method
   protected Object prepare(final ResourceManager manager, final Profiler profiler) {
      return this.prepare(manager, profiler);
   }

   static {
      MISSING_SOUND = new Sound(EMPTY_ID, ConstantFloatProvider.create(1.0F), ConstantFloatProvider.create(1.0F), 1, Sound.RegistrationType.FILE, false, false, 16);
      INTENTIONALLY_EMPTY_ID = Identifier.ofVanilla("intentionally_empty");
      INTENTIONALLY_EMPTY_SOUND_SET = new WeightedSoundSet(INTENTIONALLY_EMPTY_ID, (String)null);
      INTENTIONALLY_EMPTY_SOUND = new Sound(INTENTIONALLY_EMPTY_ID, ConstantFloatProvider.create(1.0F), ConstantFloatProvider.create(1.0F), 1, Sound.RegistrationType.FILE, false, false, 16);
      LOGGER = LogUtils.getLogger();
      GSON = (new GsonBuilder()).registerTypeAdapter(SoundEntry.class, new SoundEntryDeserializer()).create();
      TYPE = new TypeToken() {
      };
   }

   @Environment(EnvType.CLIENT)
   protected static class SoundList {
      final Map loadedSounds = Maps.newHashMap();
      private Map foundSounds = Map.of();

      void findSounds(ResourceManager resourceManager) {
         this.foundSounds = Sound.FINDER.findResources(resourceManager);
      }

      void register(Identifier id, SoundEntry entry) {
         WeightedSoundSet weightedSoundSet = (WeightedSoundSet)this.loadedSounds.get(id);
         boolean bl = weightedSoundSet == null;
         if (bl || entry.canReplace()) {
            if (!bl) {
               SoundManager.LOGGER.debug("Replaced sound event location {}", id);
            }

            weightedSoundSet = new WeightedSoundSet(id, entry.getSubtitle());
            this.loadedSounds.put(id, weightedSoundSet);
         }

         ResourceFactory resourceFactory = ResourceFactory.fromMap(this.foundSounds);
         Iterator var6 = entry.getSounds().iterator();

         while(var6.hasNext()) {
            final Sound sound = (Sound)var6.next();
            final Identifier identifier = sound.getIdentifier();
            Object soundContainer;
            switch (sound.getRegistrationType()) {
               case FILE:
                  if (!SoundManager.isSoundResourcePresent(sound, id, resourceFactory)) {
                     continue;
                  }

                  soundContainer = sound;
                  break;
               case SOUND_EVENT:
                  soundContainer = new SoundContainer() {
                     public int getWeight() {
                        WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier);
                        return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
                     }

                     public Sound getSound(Random random) {
                        WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier);
                        if (weightedSoundSet == null) {
                           return SoundManager.MISSING_SOUND;
                        } else {
                           Sound soundx = weightedSoundSet.getSound(random);
                           return new Sound(soundx.getIdentifier(), new MultipliedFloatSupplier(new FloatSupplier[]{soundx.getVolume(), sound.getVolume()}), new MultipliedFloatSupplier(new FloatSupplier[]{soundx.getPitch(), sound.getPitch()}), sound.getWeight(), Sound.RegistrationType.FILE, soundx.isStreamed() || sound.isStreamed(), soundx.isPreloaded(), soundx.getAttenuation());
                        }
                     }

                     public void preload(SoundSystem soundSystem) {
                        WeightedSoundSet weightedSoundSet = (WeightedSoundSet)SoundList.this.loadedSounds.get(identifier);
                        if (weightedSoundSet != null) {
                           weightedSoundSet.preload(soundSystem);
                        }
                     }

                     // $FF: synthetic method
                     public Object getSound(final Random random) {
                        return this.getSound(random);
                     }
                  };
                  break;
               default:
                  throw new IllegalStateException("Unknown SoundEventRegistration type: " + String.valueOf(sound.getRegistrationType()));
            }

            weightedSoundSet.add((SoundContainer)soundContainer);
         }

      }

      public void reload(Map sounds, Map soundResources, SoundSystem system) {
         sounds.clear();
         soundResources.clear();
         soundResources.putAll(this.foundSounds);
         Iterator var4 = this.loadedSounds.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            sounds.put((Identifier)entry.getKey(), (WeightedSoundSet)entry.getValue());
            ((WeightedSoundSet)entry.getValue()).preload(system);
         }

      }
   }
}
