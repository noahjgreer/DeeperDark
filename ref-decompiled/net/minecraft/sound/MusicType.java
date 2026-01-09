package net.minecraft.sound;

import net.minecraft.registry.entry.RegistryEntry;

public class MusicType {
   private static final int MENU_MIN_DELAY = 20;
   private static final int MENU_MAX_DELAY = 600;
   private static final int GAME_MIN_DELAY = 12000;
   private static final int GAME_MAX_DELAY = 24000;
   private static final int END_MIN_DELAY = 6000;
   public static final MusicSound MENU;
   public static final MusicSound CREATIVE;
   public static final MusicSound CREDITS;
   public static final MusicSound DRAGON;
   public static final MusicSound END;
   public static final MusicSound UNDERWATER;
   public static final MusicSound GAME;

   public static MusicSound createIngameMusic(RegistryEntry sound) {
      return new MusicSound(sound, 12000, 24000, false);
   }

   static {
      MENU = new MusicSound(SoundEvents.MUSIC_MENU, 20, 600, true);
      CREATIVE = new MusicSound(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
      CREDITS = new MusicSound(SoundEvents.MUSIC_CREDITS, 0, 0, true);
      DRAGON = new MusicSound(SoundEvents.MUSIC_DRAGON, 0, 0, true);
      END = new MusicSound(SoundEvents.MUSIC_END, 6000, 24000, true);
      UNDERWATER = createIngameMusic(SoundEvents.MUSIC_UNDER_WATER);
      GAME = createIngameMusic(SoundEvents.MUSIC_GAME);
   }
}
