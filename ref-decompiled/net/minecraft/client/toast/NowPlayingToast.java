package net.minecraft.client.toast;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.ColorLerper;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class NowPlayingToast implements Toast {
   private static final Identifier TEXTURE = Identifier.ofVanilla("toast/now_playing");
   private static final Identifier MUSIC_NOTES_ICON = Identifier.of("icon/music_notes");
   private static final int MARGIN = 7;
   private static final int MUSIC_NOTES_ICON_SIZE = 16;
   private static final int field_60727 = 30;
   private static final int field_60728 = 30;
   private static final int VISIBILITY_DURATION = 5000;
   private static final int TEXT_COLOR;
   private static final long MUSIC_NOTE_COLOR_CHANGE_INTERVAL = 25L;
   private static int musicNoteColorChanges;
   private static long lastMusicNoteColorChangeTime;
   private static int musicNotesIconColor;
   private boolean showing;
   private double displayTimeMultiplier;
   @Nullable
   private static String musicTranslationKey;
   private final MinecraftClient client;
   private Toast.Visibility visibility;

   public NowPlayingToast() {
      this.visibility = Toast.Visibility.HIDE;
      this.client = MinecraftClient.getInstance();
   }

   public static void draw(DrawContext context, TextRenderer textRenderer) {
      if (musicTranslationKey != null) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, getMusicTextWidth(musicTranslationKey, textRenderer), 30);
         int i = true;
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MUSIC_NOTES_ICON, 7, 7, 16, 16, musicNotesIconColor);
         Text var10002 = getMusicText(musicTranslationKey);
         Objects.requireNonNull(textRenderer);
         context.drawTextWithShadow(textRenderer, (Text)var10002, 30, 15 - 9 / 2, TEXT_COLOR);
      }

   }

   public static void tick() {
      musicTranslationKey = MinecraftClient.getInstance().getMusicTracker().getCurrentMusicTranslationKey();
      if (musicTranslationKey != null) {
         long l = System.currentTimeMillis();
         if (l > lastMusicNoteColorChangeTime + 25L) {
            ++musicNoteColorChanges;
            lastMusicNoteColorChangeTime = l;
            musicNotesIconColor = ColorLerper.lerpColor(ColorLerper.Type.MUSIC_NOTE, (float)musicNoteColorChanges);
         }
      }

   }

   private static Text getMusicText(@Nullable String translationKey) {
      return translationKey == null ? Text.empty() : Text.translatable(translationKey.replace("/", "."));
   }

   public void show(GameOptions options) {
      this.showing = true;
      this.displayTimeMultiplier = (Double)options.getNotificationDisplayTime().getValue();
      this.setVisibility(Toast.Visibility.SHOW);
   }

   public void update(ToastManager manager, long time) {
      if (this.showing) {
         this.visibility = (double)time < 5000.0 * this.displayTimeMultiplier ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
         tick();
      }

   }

   public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
      draw(context, textRenderer);
   }

   public void onFinishedRendering() {
      this.showing = false;
   }

   public int getWidth() {
      return getMusicTextWidth(musicTranslationKey, this.client.textRenderer);
   }

   private static int getMusicTextWidth(@Nullable String translationKey, TextRenderer textRenderer) {
      return 30 + textRenderer.getWidth((StringVisitable)getMusicText(translationKey)) + 7;
   }

   public int getHeight() {
      return 30;
   }

   public float getXPos(int scaledWindowWidth, float visibleWidthPortion) {
      return (float)this.getWidth() * visibleWidthPortion - (float)this.getWidth();
   }

   public float getYPos(int topIndex) {
      return 0.0F;
   }

   public Toast.Visibility getVisibility() {
      return this.visibility;
   }

   public void setVisibility(Toast.Visibility visibility) {
      this.visibility = visibility;
   }

   static {
      TEXT_COLOR = DyeColor.LIGHT_GRAY.getSignColor();
      musicNotesIconColor = -1;
   }
}
