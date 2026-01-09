package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SoundOptionsScreen extends GameOptionsScreen {
   private static final Text TITLE_TEXT = Text.translatable("options.sounds.title");

   public SoundOptionsScreen(Screen parent, GameOptions options) {
      super(parent, options, TITLE_TEXT);
   }

   protected void addOptions() {
      this.body.addSingleOptionEntry(this.gameOptions.getSoundVolumeOption(SoundCategory.MASTER));
      this.body.addAll(this.getVolumeOptions());
      this.body.addSingleOptionEntry(this.gameOptions.getSoundDevice());
      this.body.addAll(this.gameOptions.getShowSubtitles(), this.gameOptions.getDirectionalAudio());
      this.body.addAll(this.gameOptions.getMusicFrequency(), this.gameOptions.getShowNowPlayingToast());
   }

   private SimpleOption[] getVolumeOptions() {
      Stream var10000 = Arrays.stream(SoundCategory.values()).filter((category) -> {
         return category != SoundCategory.MASTER;
      });
      GameOptions var10001 = this.gameOptions;
      Objects.requireNonNull(var10001);
      return (SimpleOption[])var10000.map(var10001::getSoundVolumeOption).toArray((i) -> {
         return new SimpleOption[i];
      });
   }
}
