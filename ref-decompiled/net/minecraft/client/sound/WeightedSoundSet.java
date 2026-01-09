package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class WeightedSoundSet implements SoundContainer {
   private final List sounds = Lists.newArrayList();
   @Nullable
   private final Text subtitle;

   public WeightedSoundSet(Identifier id, @Nullable String subtitle) {
      this.subtitle = subtitle == null ? null : Text.translatable(subtitle);
   }

   public int getWeight() {
      int i = 0;

      SoundContainer soundContainer;
      for(Iterator var2 = this.sounds.iterator(); var2.hasNext(); i += soundContainer.getWeight()) {
         soundContainer = (SoundContainer)var2.next();
      }

      return i;
   }

   public Sound getSound(Random random) {
      int i = this.getWeight();
      if (!this.sounds.isEmpty() && i != 0) {
         int j = random.nextInt(i);
         Iterator var4 = this.sounds.iterator();

         SoundContainer soundContainer;
         do {
            if (!var4.hasNext()) {
               return SoundManager.MISSING_SOUND;
            }

            soundContainer = (SoundContainer)var4.next();
            j -= soundContainer.getWeight();
         } while(j >= 0);

         return (Sound)soundContainer.getSound(random);
      } else {
         return SoundManager.MISSING_SOUND;
      }
   }

   public void add(SoundContainer container) {
      this.sounds.add(container);
   }

   @Nullable
   public Text getSubtitle() {
      return this.subtitle;
   }

   public void preload(SoundSystem soundSystem) {
      Iterator var2 = this.sounds.iterator();

      while(var2.hasNext()) {
         SoundContainer soundContainer = (SoundContainer)var2.next();
         soundContainer.preload(soundSystem);
      }

   }

   // $FF: synthetic method
   public Object getSound(final Random random) {
      return this.getSound(random);
   }
}
