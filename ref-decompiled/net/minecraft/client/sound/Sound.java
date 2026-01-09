package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class Sound implements SoundContainer {
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

   // $FF: synthetic method
   public Object getSound(final Random random) {
      return this.getSound(random);
   }

   @Environment(EnvType.CLIENT)
   public static enum RegistrationType {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private RegistrationType(final String name) {
         this.name = name;
      }

      @Nullable
      public static RegistrationType getByName(String name) {
         RegistrationType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            RegistrationType registrationType = var1[var3];
            if (registrationType.name.equals(name)) {
               return registrationType;
            }
         }

         return null;
      }

      // $FF: synthetic method
      private static RegistrationType[] method_36926() {
         return new RegistrationType[]{FILE, SOUND_EVENT};
      }
   }
}
