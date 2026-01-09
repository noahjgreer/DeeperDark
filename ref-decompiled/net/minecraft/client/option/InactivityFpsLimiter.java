package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class InactivityFpsLimiter {
   private static final int IN_GUI_FPS = 60;
   private static final int MINIMIZED_FPS = 10;
   private static final int AFK_STAGE_1_FPS = 30;
   private static final int AFK_STAGE_2_FPS = 10;
   private static final long AFK_STAGE_1_THRESHOLD = 60000L;
   private static final long AFK_STAGE_2_THRESHOLD = 600000L;
   private final GameOptions options;
   private final MinecraftClient client;
   private int maxFps;
   private long lastInputTime;

   public InactivityFpsLimiter(GameOptions options, MinecraftClient client) {
      this.options = options;
      this.client = client;
      this.maxFps = (Integer)options.getMaxFps().getValue();
   }

   public int update() {
      int var10000;
      switch (this.getLimitReason().ordinal()) {
         case 0:
            var10000 = this.maxFps;
            break;
         case 1:
            var10000 = 10;
            break;
         case 2:
            var10000 = 10;
            break;
         case 3:
            var10000 = Math.min(this.maxFps, 30);
            break;
         case 4:
            var10000 = 60;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public LimitReason getLimitReason() {
      InactivityFpsLimit inactivityFpsLimit = (InactivityFpsLimit)this.options.getInactivityFpsLimit().getValue();
      if (this.client.getWindow().isMinimized()) {
         return InactivityFpsLimiter.LimitReason.WINDOW_ICONIFIED;
      } else {
         if (inactivityFpsLimit == InactivityFpsLimit.AFK) {
            long l = Util.getMeasuringTimeMs() - this.lastInputTime;
            if (l > 600000L) {
               return InactivityFpsLimiter.LimitReason.LONG_AFK;
            }

            if (l > 60000L) {
               return InactivityFpsLimiter.LimitReason.SHORT_AFK;
            }
         }

         return this.client.world != null || this.client.currentScreen == null && this.client.getOverlay() == null ? InactivityFpsLimiter.LimitReason.NONE : InactivityFpsLimiter.LimitReason.OUT_OF_LEVEL_MENU;
      }
   }

   public boolean shouldDisableProfilerTimeout() {
      LimitReason limitReason = this.getLimitReason();
      return limitReason == InactivityFpsLimiter.LimitReason.WINDOW_ICONIFIED || limitReason == InactivityFpsLimiter.LimitReason.LONG_AFK;
   }

   public void setMaxFps(int maxFps) {
      this.maxFps = maxFps;
   }

   public void onInput() {
      this.lastInputTime = Util.getMeasuringTimeMs();
   }

   @Environment(EnvType.CLIENT)
   public static enum LimitReason {
      NONE,
      WINDOW_ICONIFIED,
      LONG_AFK,
      SHORT_AFK,
      OUT_OF_LEVEL_MENU;

      // $FF: synthetic method
      private static LimitReason[] method_66516() {
         return new LimitReason[]{NONE, WINDOW_ICONIFIED, LONG_AFK, SHORT_AFK, OUT_OF_LEVEL_MENU};
      }
   }
}
