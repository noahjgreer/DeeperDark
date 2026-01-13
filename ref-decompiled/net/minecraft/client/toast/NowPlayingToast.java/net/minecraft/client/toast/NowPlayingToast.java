/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.ColorLerper;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class NowPlayingToast
implements Toast {
    private static final Identifier TEXTURE = Identifier.ofVanilla("toast/now_playing");
    private static final Identifier MUSIC_NOTES_ICON = Identifier.of("icon/music_notes");
    private static final int MARGIN = 7;
    private static final int MUSIC_NOTES_ICON_SIZE = 16;
    private static final int field_60727 = 30;
    private static final int field_60728 = 30;
    private static final int VISIBILITY_DURATION = 5000;
    private static final int TEXT_COLOR = DyeColor.LIGHT_GRAY.getSignColor();
    private static final long MUSIC_NOTE_COLOR_CHANGE_INTERVAL = 25L;
    private static int musicNoteColorChanges;
    private static long lastMusicNoteColorChangeTime;
    private static int musicNotesIconColor;
    private boolean showing;
    private double displayTimeMultiplier;
    private final MinecraftClient client;
    private Toast.Visibility visibility = Toast.Visibility.HIDE;

    public NowPlayingToast() {
        this.client = MinecraftClient.getInstance();
    }

    public static void draw(DrawContext context, TextRenderer textRenderer) {
        String string = NowPlayingToast.getCurrentMusicTranslationKey();
        if (string != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, NowPlayingToast.getMusicTextWidth(string, textRenderer), 30);
            int i = 7;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MUSIC_NOTES_ICON, 7, 7, 16, 16, musicNotesIconColor);
            context.drawTextWithShadow(textRenderer, NowPlayingToast.getMusicText(string), 30, 15 - textRenderer.fontHeight / 2, TEXT_COLOR);
        }
    }

    private static @Nullable String getCurrentMusicTranslationKey() {
        return MinecraftClient.getInstance().getMusicTracker().getCurrentMusicTranslationKey();
    }

    public static void tick() {
        long l;
        if (NowPlayingToast.getCurrentMusicTranslationKey() != null && (l = System.currentTimeMillis()) > lastMusicNoteColorChangeTime + 25L) {
            lastMusicNoteColorChangeTime = l;
            musicNotesIconColor = ColorLerper.lerpColor(ColorLerper.Type.MUSIC_NOTE, ++musicNoteColorChanges);
        }
    }

    private static Text getMusicText(@Nullable String translationKey) {
        if (translationKey == null) {
            return Text.empty();
        }
        return Text.translatable(translationKey.replace("/", "."));
    }

    public void show(GameOptions options) {
        this.showing = true;
        this.displayTimeMultiplier = options.getNotificationDisplayTime().getValue();
        this.setVisibility(Toast.Visibility.SHOW);
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (this.showing) {
            this.visibility = (double)time < 5000.0 * this.displayTimeMultiplier ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
            NowPlayingToast.tick();
        }
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        NowPlayingToast.draw(context, textRenderer);
    }

    @Override
    public void onFinishedRendering() {
        this.showing = false;
    }

    @Override
    public int getWidth() {
        return NowPlayingToast.getMusicTextWidth(NowPlayingToast.getCurrentMusicTranslationKey(), this.client.textRenderer);
    }

    private static int getMusicTextWidth(@Nullable String translationKey, TextRenderer textRenderer) {
        return 30 + textRenderer.getWidth(NowPlayingToast.getMusicText(translationKey)) + 7;
    }

    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public float getXPos(int scaledWindowWidth, float visibleWidthPortion) {
        return (float)this.getWidth() * visibleWidthPortion - (float)this.getWidth();
    }

    @Override
    public float getYPos(int topIndex) {
        return 0.0f;
    }

    @Override
    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    public void setVisibility(Toast.Visibility visibility) {
        this.visibility = visibility;
    }

    static {
        musicNotesIconColor = -1;
    }
}
