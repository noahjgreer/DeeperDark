/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Overlay
 *  net.minecraft.client.gui.screen.SplashOverlay
 *  net.minecraft.client.gui.screen.SplashOverlay$LogoTexture
 *  net.minecraft.client.texture.ReloadableTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.Window
 *  net.minecraft.resource.ResourceReload
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SplashOverlay
extends Overlay {
    public static final Identifier LOGO = Identifier.ofVanilla((String)"textures/gui/title/mojangstudios.png");
    private static final int MOJANG_RED = ColorHelper.getArgb((int)255, (int)239, (int)50, (int)61);
    private static final int MONOCHROME_BLACK = ColorHelper.getArgb((int)255, (int)0, (int)0, (int)0);
    private static final IntSupplier BRAND_ARGB = () -> (Boolean)MinecraftClient.getInstance().options.getMonochromeLogo().getValue() != false ? MONOCHROME_BLACK : MOJANG_RED;
    private static final int field_32251 = 240;
    private static final float LOGO_RIGHT_HALF_V = 60.0f;
    private static final int field_32253 = 60;
    private static final int field_32254 = 120;
    private static final float LOGO_OVERLAP = 0.0625f;
    private static final float PROGRESS_LERP_DELTA = 0.95f;
    public static final long RELOAD_COMPLETE_FADE_DURATION = 1000L;
    public static final long RELOAD_START_FADE_DURATION = 500L;
    private final MinecraftClient client;
    private final ResourceReload reload;
    private final Consumer<Optional<Throwable>> exceptionHandler;
    private final boolean reloading;
    private float progress;
    private long reloadCompleteTime = -1L;
    private long reloadStartTime = -1L;

    public SplashOverlay(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading) {
        this.client = client;
        this.reload = monitor;
        this.exceptionHandler = exceptionHandler;
        this.reloading = reloading;
    }

    public static void init(TextureManager textureManager) {
        textureManager.registerTexture(LOGO, (ReloadableTexture)new LogoTexture());
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        float h;
        int k;
        float g;
        int i = context.getScaledWindowWidth();
        int j = context.getScaledWindowHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }
        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0f : -1.0f;
        float f2 = g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0f : -1.0f;
        if (f >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.renderWithTooltip(context, 0, 0, deltaTicks);
            } else {
                this.client.inGameHud.renderDeferredSubtitles();
            }
            k = MathHelper.ceil((float)((1.0f - MathHelper.clamp((float)(f - 1.0f), (float)0.0f, (float)1.0f)) * 255.0f));
            context.createNewRootLayer();
            context.fill(0, 0, i, j, SplashOverlay.withAlpha((int)BRAND_ARGB.getAsInt(), (int)k));
            h = 1.0f - MathHelper.clamp((float)(f - 1.0f), (float)0.0f, (float)1.0f);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && g < 1.0f) {
                this.client.currentScreen.renderWithTooltip(context, mouseX, mouseY, deltaTicks);
            } else {
                this.client.inGameHud.renderDeferredSubtitles();
            }
            k = MathHelper.ceil((double)(MathHelper.clamp((double)g, (double)0.15, (double)1.0) * 255.0));
            context.createNewRootLayer();
            context.fill(0, 0, i, j, SplashOverlay.withAlpha((int)BRAND_ARGB.getAsInt(), (int)k));
            h = MathHelper.clamp((float)g, (float)0.0f, (float)1.0f);
        } else {
            k = BRAND_ARGB.getAsInt();
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.client.getFramebuffer().getColorAttachment(), k);
            h = 1.0f;
        }
        k = (int)((double)context.getScaledWindowWidth() * 0.5);
        int m = (int)((double)context.getScaledWindowHeight() * 0.5);
        double d = Math.min((double)context.getScaledWindowWidth() * 0.75, (double)context.getScaledWindowHeight()) * 0.25;
        int n = (int)(d * 0.5);
        double e = d * 4.0;
        int o = (int)(e * 0.5);
        int p = ColorHelper.getWhite((float)h);
        context.drawTexture(RenderPipelines.MOJANG_LOGO, LOGO, k - o, m - n, -0.0625f, 0.0f, o, (int)d, 120, 60, 120, 120, p);
        context.drawTexture(RenderPipelines.MOJANG_LOGO, LOGO, k, m - n, 0.0625f, 60.0f, o, (int)d, 120, 60, 120, 120, p);
        int q = (int)((double)context.getScaledWindowHeight() * 0.8325);
        float r = this.reload.getProgress();
        this.progress = MathHelper.clamp((float)(this.progress * 0.95f + r * 0.050000012f), (float)0.0f, (float)1.0f);
        if (f < 1.0f) {
            this.renderProgressBar(context, i / 2 - o, q - 5, i / 2 + o, q + 5, 1.0f - MathHelper.clamp((float)f, (float)0.0f, (float)1.0f));
        }
        if (f >= 2.0f) {
            this.client.setOverlay(null);
        }
    }

    public void tick() {
        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && this.isInGracePeriod()) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            }
            catch (Throwable throwable) {
                this.exceptionHandler.accept(Optional.of(throwable));
            }
            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                Window window = this.client.getWindow();
                this.client.currentScreen.init(window.getScaledWidth(), window.getScaledHeight());
            }
        }
    }

    private boolean isInGracePeriod() {
        return !this.reloading || this.reloadStartTime > -1L && Util.getMeasuringTimeMs() - this.reloadStartTime >= 1000L;
    }

    private void renderProgressBar(DrawContext context, int minX, int minY, int maxX, int maxY, float opacity) {
        int i = MathHelper.ceil((float)((float)(maxX - minX - 2) * this.progress));
        int j = Math.round(opacity * 255.0f);
        int k = ColorHelper.getArgb((int)j, (int)255, (int)255, (int)255);
        context.fill(minX + 2, minY + 2, minX + i, maxY - 2, k);
        context.fill(minX + 1, minY, maxX - 1, minY + 1, k);
        context.fill(minX + 1, maxY, maxX - 1, maxY - 1, k);
        context.fill(minX, minY, minX + 1, maxY, k);
        context.fill(maxX, minY, maxX - 1, maxY, k);
    }

    public boolean pausesGame() {
        return true;
    }
}

