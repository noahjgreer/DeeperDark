package net.noahsarch.deeperdark.intro;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ARGB;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DeeperDarkLogoScreen extends Screen {

    private static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath("deeperdark", "textures/gui/intro.png");
    private static final SoundEvent INTRO_SOUND = SoundEvent.createVariableRangeEvent(
            Identifier.fromNamespaceAndPath("deeperdark", "intro"));

    // Standard (SRC_ALPHA, ONE_MINUS_SRC_ALPHA) blending — matches original GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
    private static final RenderPipeline INTRO_TEXTURED = RenderPipeline
            .builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("deeperdark", "pipeline/intro_textured"))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .build();

    // Alpha-weighted additive (SRC_ALPHA, ONE) — matches original GL_SRC_ALPHA, GL_ONE
    private static final RenderPipeline INTRO_TEXTURED_ADDITIVE = RenderPipeline
            .builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("deeperdark", "pipeline/intro_textured_additive"))
            .withColorTargetState(new ColorTargetState(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE)))
            .build();

    // Translucent solid fill — GUI_SNIPPET + TRANSLUCENT for overlays, scanlines, noise
    private static final RenderPipeline INTRO_FILL = RenderPipeline
            .builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("deeperdark", "pipeline/intro_fill"))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .build();

    private static final int BLEND_ONE                = 1;
    private static final int BLEND_SRC_ALPHA          = 770;
    private static final int BLEND_ONE_MINUS_SRC_ALPHA = 771;

    private static final int PARAM_COUNT = 22;

    private static final double DEFAULT_ICON_FADE_IN_START_SECONDS    = 3.0;
    private static final double DEFAULT_ICON_FADE_DURATION_SECONDS    = 0.85;
    private static final double DEFAULT_ICON_FADE_OUT_START_SECONDS   = 9.0;

    private static final double DEFAULT_TITLE_TRANSITION_START_SECONDS    = 12.0;
    private static final double DEFAULT_TITLE_TRANSITION_DURATION_SECONDS = 0.85;

    private static final float DEFAULT_JITTER_X_AMPLITUDE_PIXELS    = 0.40F;
    private static final float DEFAULT_JITTER_Y_AMPLITUDE_PIXELS    = 0.425F;
    private static final float DEFAULT_JITTER_PRIMARY_FREQUENCY_HZ  = 1.9F;
    private static final float DEFAULT_JITTER_SECONDARY_FREQUENCY_HZ = 4.85F;
    private static final float DEFAULT_JITTER_SECONDARY_WEIGHT      = 0.35F;
    private static final float DEFAULT_JITTER_PHASE_OFFSET_RADIANS  = 1.2F;

    private static final double DEFAULT_FLASH_ONE_START_SECONDS  = 5.8;
    private static final double DEFAULT_FLASH_DURATION_SECONDS   = 0.3;
    private static final double DEFAULT_FLASH_GAP_SECONDS        = 0.3;
    private static final float  DEFAULT_FLASH_ATTACK_RATIO       = 0.78F;
    private static final float  DEFAULT_FLASH_INTENSITY          = 1.0F;

    private static final double DEFAULT_FINAL_FLASH_OFFSET_FROM_FADE_OUT_SECONDS = 0.5;
    private static final double DEFAULT_FINAL_FLASH_DURATION_SECONDS             = 0.5;
    private static final float  DEFAULT_FINAL_FLASH_ATTACK_RATIO                 = 0.72F;
    private static final float  DEFAULT_FINAL_FLASH_INTENSITY                    = 1.0F;

    private static final int   DEFAULT_EXPOSURE_LAYER_COUNT = 10;
    private static final float DEFAULT_EXPOSURE_LAYER_DECAY = 0.78F;

    private static final float DEFAULT_CHROMA_SPLIT_PIXELS = 0.95F;
    private static final float DEFAULT_CHROMA_ALPHA        = 0.30F;

    private static final float DEFAULT_TEAR_CHANCE           = 0.02F;
    private static final float DEFAULT_TEAR_MAX_OFFSET_PIXELS = 10.10F;
    private static final float DEFAULT_TEAR_BAND_HEIGHT_PIXELS = 18.0F;

    private static final float DEFAULT_GHOST_ALPHA          = 0.18F;
    private static final float DEFAULT_GHOST_OFFSET_X_PIXELS = 1.8F;
    private static final float DEFAULT_GHOST_OFFSET_Y_PIXELS = 0.8F;

    private static final float DEFAULT_BLOOM_ALPHA        = 0.16F;
    private static final float DEFAULT_BLOOM_SCALE        = 1.175F;
    private static final float DEFAULT_BLOOM_PULSE_HZ     = 1.8F;
    private static final float DEFAULT_BLOOM_PULSE_AMOUNT = 0.4F;

    private static final int   DEFAULT_SCANLINE_SPACING_PIXELS = 2;
    private static final float DEFAULT_SCANLINE_ALPHA          = 0.20F;
    private static final float DEFAULT_SCANLINE_SCROLL_SPEED   = 17.0F;

    private static final float DEFAULT_NOISE_ALPHA       = 0.11F;
    private static final float DEFAULT_NOISE_DENSITY     = 0.14F;
    private static final int   DEFAULT_NOISE_MAX_BLOCK_SIZE = 3;

    private static final double DEFAULT_GHOST_BURST_DURATION_SECONDS        = 0.325;
    private static final float  DEFAULT_GHOST_BURST_INTENSITY               = 1.00F;
    private static final float  DEFAULT_GHOST_BURST_RANGE_MIN_PIXELS        = 13.20F;
    private static final float  DEFAULT_GHOST_BURST_RANGE_MAX_PIXELS        = 20.40F;
    private static final double DEFAULT_GHOST_BURST_BEFORE_FIRST_FLASH_SECONDS = 0.08;

    private static final double DEFAULT_SMEAR_BURST_DELAY_AFTER_FIRST_FLASH_SECONDS = 0.5;
    private static final double DEFAULT_SMEAR_BURST_DURATION_SECONDS                = 0.40;
    private static final float  DEFAULT_SMEAR_BURST_INTENSITY                       = 0.52F;

    private static final double DEBUG_LOOP_DURATION_SECONDS =
            DEFAULT_TITLE_TRANSITION_START_SECONDS + DEFAULT_TITLE_TRANSITION_DURATION_SECONDS;

    // Shared across instances so a restart (debug loop) syncs the timing
    private static boolean introSoundPlayed;
    private static long    introSoundStartNanos;

    // Mutable parameters (static so debug tweaks persist across loop restarts)
    private static double iconFadeInStartSeconds    = DEFAULT_ICON_FADE_IN_START_SECONDS;
    private static double iconFadeDurationSeconds   = DEFAULT_ICON_FADE_DURATION_SECONDS;
    private static double iconFadeOutStartSeconds   = DEFAULT_ICON_FADE_OUT_START_SECONDS;

    private static double titleTransitionStartSeconds    = DEFAULT_TITLE_TRANSITION_START_SECONDS;
    private static double titleTransitionDurationSeconds = DEFAULT_TITLE_TRANSITION_DURATION_SECONDS;

    private static float jitterXAmplitudePixels    = DEFAULT_JITTER_X_AMPLITUDE_PIXELS;
    private static float jitterYAmplitudePixels    = DEFAULT_JITTER_Y_AMPLITUDE_PIXELS;
    private static float jitterPrimaryFrequencyHz  = DEFAULT_JITTER_PRIMARY_FREQUENCY_HZ;
    private static float jitterSecondaryFrequencyHz = DEFAULT_JITTER_SECONDARY_FREQUENCY_HZ;
    private static float jitterSecondaryWeight     = DEFAULT_JITTER_SECONDARY_WEIGHT;
    private static float jitterPhaseOffsetRadians  = DEFAULT_JITTER_PHASE_OFFSET_RADIANS;

    private static double flashOneStartSeconds  = DEFAULT_FLASH_ONE_START_SECONDS;
    private static double flashDurationSeconds  = DEFAULT_FLASH_DURATION_SECONDS;
    private static double flashGapSeconds       = DEFAULT_FLASH_GAP_SECONDS;
    private static float  flashAttackRatio      = DEFAULT_FLASH_ATTACK_RATIO;
    private static float  flashIntensity        = DEFAULT_FLASH_INTENSITY;

    private static double finalFlashOffsetFromFadeOutSeconds = DEFAULT_FINAL_FLASH_OFFSET_FROM_FADE_OUT_SECONDS;
    private static double finalFlashDurationSeconds          = DEFAULT_FINAL_FLASH_DURATION_SECONDS;
    private static float  finalFlashAttackRatio              = DEFAULT_FINAL_FLASH_ATTACK_RATIO;
    private static float  finalFlashIntensity                = DEFAULT_FINAL_FLASH_INTENSITY;

    private static int   exposureLayerCount = DEFAULT_EXPOSURE_LAYER_COUNT;
    private static float exposureLayerDecay = DEFAULT_EXPOSURE_LAYER_DECAY;

    private static float chromaSplitPixels = DEFAULT_CHROMA_SPLIT_PIXELS;
    private static float chromaAlpha       = DEFAULT_CHROMA_ALPHA;

    private static float tearChance            = DEFAULT_TEAR_CHANCE;
    private static float tearMaxOffsetPixels   = DEFAULT_TEAR_MAX_OFFSET_PIXELS;
    private static float tearBandHeightPixels  = DEFAULT_TEAR_BAND_HEIGHT_PIXELS;

    private static float ghostAlpha          = DEFAULT_GHOST_ALPHA;
    private static float ghostOffsetXPixels  = DEFAULT_GHOST_OFFSET_X_PIXELS;
    private static float ghostOffsetYPixels  = DEFAULT_GHOST_OFFSET_Y_PIXELS;

    private static float bloomAlpha       = DEFAULT_BLOOM_ALPHA;
    private static float bloomScale       = DEFAULT_BLOOM_SCALE;
    private static float bloomPulseHz     = DEFAULT_BLOOM_PULSE_HZ;
    private static float bloomPulseAmount = DEFAULT_BLOOM_PULSE_AMOUNT;

    private static int   scanlineSpacingPixels = DEFAULT_SCANLINE_SPACING_PIXELS;
    private static float scanlineAlpha         = DEFAULT_SCANLINE_ALPHA;
    private static float scanlineScrollSpeed   = DEFAULT_SCANLINE_SCROLL_SPEED;

    private static float noiseAlpha       = DEFAULT_NOISE_ALPHA;
    private static float noiseDensity     = DEFAULT_NOISE_DENSITY;
    private static int   noiseMaxBlockSize = DEFAULT_NOISE_MAX_BLOCK_SIZE;

    private static double ghostBurstDurationSeconds        = DEFAULT_GHOST_BURST_DURATION_SECONDS;
    private static float  ghostBurstIntensity              = DEFAULT_GHOST_BURST_INTENSITY;
    private static float  ghostBurstRangeMinPixels         = DEFAULT_GHOST_BURST_RANGE_MIN_PIXELS;
    private static float  ghostBurstRangeMaxPixels         = DEFAULT_GHOST_BURST_RANGE_MAX_PIXELS;
    private static double ghostBurstBeforeFirstFlashSeconds = DEFAULT_GHOST_BURST_BEFORE_FIRST_FLASH_SECONDS;

    private static double smearBurstDelayAfterFirstFlashSeconds = DEFAULT_SMEAR_BURST_DELAY_AFTER_FIRST_FLASH_SECONDS;
    private static double smearBurstDurationSeconds             = DEFAULT_SMEAR_BURST_DURATION_SECONDS;
    private static float  smearBurstIntensity                   = DEFAULT_SMEAR_BURST_INTENSITY;

    // Instance state
    private final Screen nextScreen;
    private final double preDelaySeconds;
    private long  initNanos;
    private long  soundStartNanos;
    private boolean introSoundStarted;
    private boolean nextScreenInitialized;
    private boolean debugOverlayEnabled;
    private boolean debugLoopEnabled;
    private int selectedParameter;

    public DeeperDarkLogoScreen(Screen nextScreen) {
        this(nextScreen, 0.0);
    }

    public DeeperDarkLogoScreen(Screen nextScreen, double preDelaySeconds) {
        super(Component.empty());
        this.nextScreen = nextScreen;
        this.preDelaySeconds = preDelaySeconds;
    }

    // ===== Lifecycle =====

    @Override
    protected void init() {
        this.initNanos           = System.nanoTime();
        this.debugOverlayEnabled = false;
        this.debugLoopEnabled    = false;
        this.selectedParameter   = 0;
        if (preDelaySeconds <= 0.0) {
            this.soundStartNanos = this.resolveSharedSoundStartTime();
            this.startIntroSound();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();

        if (key == GLFW.GLFW_KEY_F6) {
            this.debugOverlayEnabled = !this.debugOverlayEnabled;
            return true;
        }
        if (key == GLFW.GLFW_KEY_F7) {
            this.debugLoopEnabled = !this.debugLoopEnabled;
            if (this.debugLoopEnabled) {
                this.restartDebugLoopCycle();
            }
            return true;
        }
        if (key == GLFW.GLFW_KEY_R) {
            resetAllParametersToDefaults();
            return true;
        }

        if (!this.debugOverlayEnabled) {
            return super.keyPressed(event);
        }

        if (key == GLFW.GLFW_KEY_UP) {
            this.selectedParameter = (this.selectedParameter - 1 + PARAM_COUNT) % PARAM_COUNT;
            return true;
        }
        if (key == GLFW.GLFW_KEY_DOWN) {
            this.selectedParameter = (this.selectedParameter + 1) % PARAM_COUNT;
            return true;
        }

        boolean coarse = event.hasShiftDown();
        if (key == GLFW.GLFW_KEY_LEFT) {
            this.adjustSelectedParameter(-1, coarse);
            return true;
        }
        if (key == GLFW.GLFW_KEY_RIGHT) {
            this.adjustSelectedParameter(1, coarse);
            return true;
        }

        return super.keyPressed(event);
    }

    // ===== Rendering =====

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        if (!this.introSoundStarted) {
            if (preDelaySeconds > 0.0) {
                double waited = (System.nanoTime() - this.initNanos) / 1_000_000_000.0;
                if (waited < preDelaySeconds) {
                    fillBlackOverlay(g, 1.0F);
                    return;
                }
            }
            this.soundStartNanos = this.resolveSharedSoundStartTime();
            this.startIntroSound();
        }

        double elapsedSeconds = this.getElapsedSeconds();

        if (this.debugLoopEnabled && elapsedSeconds >= DEBUG_LOOP_DURATION_SECONDS) {
            this.restartDebugLoopCycle();
            elapsedSeconds = this.getElapsedSeconds();
        }

        if (!this.debugLoopEnabled && elapsedSeconds >= titleTransitionStartSeconds) {
            this.ensureNextScreenInitialized();
            this.nextScreen.extractRenderState(g, mouseX, mouseY, partialTick);

            float transitionProgress = clamp01((float) ((elapsedSeconds - titleTransitionStartSeconds) / titleTransitionDurationSeconds));
            float blackAlpha = 1.0F - transitionProgress;
            fillBlackOverlay(g, blackAlpha);

            if (transitionProgress >= 1.0F) {
                this.minecraft.setScreen(this.nextScreen);
            }
            return;
        }

        fillBlackOverlay(g, 1.0F);

        float iconAlpha = this.computeIconAlpha(elapsedSeconds);
        if (iconAlpha > 0.0F) {
            int iconSize = Math.max(128, Math.min(256, Math.min(this.width, this.height) / 2));
            float jitterX  = this.computeJitterX(elapsedSeconds);
            float jitterY  = this.computeJitterY(elapsedSeconds);
            float centerX  = this.width  * 0.5F + jitterX;
            float centerY  = this.height * 0.5F + jitterY;

            this.renderLogoEffects(g, elapsedSeconds, iconAlpha, centerX, centerY, iconSize);
        }

        this.renderScanlines(g, elapsedSeconds);
        this.renderNoiseOverlay(g, elapsedSeconds);

        if (this.debugOverlayEnabled) {
            this.renderDebugOverlay(g, elapsedSeconds);
        }
    }

    private void renderLogoEffects(GuiGraphicsExtractor g, double elapsedSeconds, float iconAlpha,
                                   float centerX, float centerY, int iconSize) {
        float bloomPulse       = 0.5F + 0.5F * (float) Math.sin(2.0 * Math.PI * bloomPulseHz * elapsedSeconds);
        float bloomMultiplier  = 1.0F + (bloomPulse - 0.5F) * 2.0F * bloomPulseAmount;

        float[] burstOffset = this.computeGhostBurstOffset(elapsedSeconds);
        float burstX        = burstOffset[0];
        float burstY        = burstOffset[1];
        float burstStrength = burstOffset[2];

        centerX += burstX;
        centerY += burstY;

        float ghostPassAlpha = clamp01(iconAlpha * ghostAlpha);
        if (ghostPassAlpha > 0.0F) {
            float burstGhostX = burstX * (0.6F + 0.4F * burstStrength);
            float burstGhostY = burstY * (0.6F + 0.4F * burstStrength);
            renderIconPass(g, centerX + ghostOffsetXPixels + burstGhostX,
                    centerY + ghostOffsetYPixels + burstGhostY,
                    iconSize, 1.0F, 1.0F, 1.0F, 1.0F, ghostPassAlpha,
                    BLEND_SRC_ALPHA, BLEND_ONE_MINUS_SRC_ALPHA);
        }

        float bloomPassAlpha = clamp01(iconAlpha * bloomAlpha * bloomMultiplier);
        if (bloomPassAlpha > 0.0F) {
            renderIconPass(g, centerX, centerY, iconSize,
                    bloomScale, 1.0F, 1.0F, 1.0F, bloomPassAlpha,
                    BLEND_SRC_ALPHA, BLEND_ONE);
        }

        renderIconPass(g, centerX, centerY, iconSize,
                1.0F, 1.0F, 1.0F, 1.0F, iconAlpha,
                BLEND_SRC_ALPHA, BLEND_ONE_MINUS_SRC_ALPHA);

        float chromaPassAlpha = clamp01(iconAlpha * chromaAlpha);
        if (chromaPassAlpha > 0.0F) {
            renderIconPass(g, centerX - chromaSplitPixels, centerY, iconSize,
                    1.0F, 0.25F, 1.0F, 1.0F, chromaPassAlpha,
                    BLEND_SRC_ALPHA, BLEND_ONE);
            renderIconPass(g, centerX + chromaSplitPixels, centerY, iconSize,
                    1.0F, 1.0F, 0.35F, 0.35F, chromaPassAlpha,
                    BLEND_SRC_ALPHA, BLEND_ONE);
        }

        float exposureAlpha = this.computeExposureAlpha(elapsedSeconds);
        if (exposureAlpha > 0.0F) {
            renderExposure(g, centerX, centerY, iconSize, iconAlpha, exposureAlpha);
        }

        float smearAlpha = this.computeSmearBurstAlpha(elapsedSeconds);
        if (smearAlpha > 0.0F) {
            renderVerticalSmear(g, centerX, centerY, iconSize, iconAlpha, smearAlpha);
        }

        this.renderTearBand(g, elapsedSeconds, centerX, centerY, iconSize, iconAlpha);
    }

    private float[] computeGhostBurstOffset(double elapsedSeconds) {
        double firstStart       = flashOneStartSeconds - ghostBurstBeforeFirstFlashSeconds;
        double finalFlashStart  = iconFadeOutStartSeconds - finalFlashOffsetFromFadeOutSeconds;

        float burst1 = computePulse(elapsedSeconds, firstStart,      ghostBurstDurationSeconds, 0.62F, ghostBurstIntensity);
        float burst2 = computePulse(elapsedSeconds, finalFlashStart,  ghostBurstDurationSeconds, 0.62F, ghostBurstIntensity);

        if (burst1 <= 0.0F && burst2 <= 0.0F) {
            return new float[]{0.0F, 0.0F, 0.0F};
        }

        float rangeMin = Math.min(ghostBurstRangeMinPixels, ghostBurstRangeMaxPixels);
        float rangeMax = Math.max(ghostBurstRangeMinPixels, ghostBurstRangeMaxPixels);
        float rangeA   = lerp(rangeMin, rangeMax, noiseHash01(271, 17));
        float rangeB   = lerp(rangeMin, rangeMax, noiseHash01(271, 31));

        float signX1 = noiseHash01(271, 43) < 0.5F ? -1.0F : 1.0F;
        float signY1 = noiseHash01(271, 59) < 0.5F ? -1.0F : 1.0F;
        float signX2 = noiseHash01(887, 43) < 0.5F ? -1.0F : 1.0F;
        float signY2 = noiseHash01(887, 59) < 0.5F ? -1.0F : 1.0F;

        float dx = signX1 * rangeA * burst1 + signX2 * rangeB * burst2;
        float dy = signY1 * rangeA * 0.92F * burst1 + signY2 * rangeB * 0.92F * burst2;
        float strength = Math.max(burst1, burst2);

        return new float[]{dx, dy, strength};
    }

    private float computeSmearBurstAlpha(double elapsedSeconds) {
        double smearStart = flashOneStartSeconds + smearBurstDelayAfterFirstFlashSeconds;
        return computePulse(elapsedSeconds, smearStart, smearBurstDurationSeconds, 0.68F, smearBurstIntensity);
    }

    private void renderVerticalSmear(GuiGraphicsExtractor g, float centerX, float centerY,
                                     int iconSize, float iconAlpha, float smearAlpha) {
        float baseAlpha = clamp01(iconAlpha * smearAlpha);
        if (baseAlpha <= 0.0F) {
            return;
        }

        int streakCount = 6 + Math.round(smearBurstIntensity * 8.0F);
        for (int i = 0; i < streakCount; ++i) {
            float progress = i / (float) Math.max(1, streakCount - 1);
            float stretch  = 1.0F + 0.35F * smearBurstIntensity + progress * 1.15F * smearBurstIntensity;
            float alpha    = baseAlpha * (1.0F - progress) * 0.75F;
            float offsetY  = (progress - 0.5F) * iconSize * 0.48F * smearBurstIntensity;
            float offsetX  = (noiseHash01((int)(progress * 2000), 913) - 0.5F) * 2.0F;
            renderIconPass(g, centerX + offsetX, centerY + offsetY, iconSize,
                    1.0F, stretch, 1.0F, 1.0F, 1.0F, alpha,
                    BLEND_SRC_ALPHA, BLEND_ONE);
        }
    }

    private void renderExposure(GuiGraphicsExtractor g, float centerX, float centerY,
                                int iconSize, float iconAlpha, float exposureAlpha) {
        float layerAlpha = clamp01(iconAlpha * exposureAlpha);
        for (int i = 0; i < exposureLayerCount && layerAlpha > 0.01F; ++i) {
            renderIconPass(g, centerX, centerY, iconSize,
                    1.0F + i * 0.01F, 1.0F, 1.0F, 1.0F, layerAlpha,
                    BLEND_SRC_ALPHA, BLEND_ONE);
            layerAlpha *= exposureLayerDecay;
        }
    }

    private void renderTearBand(GuiGraphicsExtractor g, double elapsedSeconds,
                                float centerX, float centerY, int iconSize, float iconAlpha) {
        if (tearChance <= 0.0F || iconAlpha <= 0.0F) {
            return;
        }
        if (noiseHash01((int)(elapsedSeconds * 1000.0), 97) > tearChance) {
            return;
        }

        float bandHeight = clamp(tearBandHeightPixels, 4.0F, iconSize * 0.6F);
        float topPx      = noiseHash01((int)(elapsedSeconds * 770.0),  1337) * Math.max(1.0F, iconSize - bandHeight);
        float offset     = (noiseHash01((int)(elapsedSeconds * 1300.0), 777) * 2.0F - 1.0F) * tearMaxOffsetPixels;
        renderIconSlicePass(g, centerX + offset, centerY, iconSize, topPx, bandHeight, iconAlpha * 0.9F);

        int iconTop = Math.round(centerY - iconSize * 0.5F);
        int yLine   = iconTop + Math.round(topPx);
        int alpha   = (int)(clamp01(iconAlpha * 0.25F) * 255.0F);
        g.fill(INTRO_FILL, Math.round(centerX - iconSize * 0.5F), yLine,
               Math.round(centerX + iconSize * 0.5F), yLine + 1,
               (alpha << 24) | 0xFFFFFF);
    }

    private void renderScanlines(GuiGraphicsExtractor g, double elapsedSeconds) {
        if (scanlineAlpha <= 0.0F) {
            return;
        }

        int spacing = Math.max(1, scanlineSpacingPixels);
        for (int y = 0; y < this.height; y += spacing) {
            float wave  = 0.75F + 0.25F * (float) Math.sin(elapsedSeconds * scanlineScrollSpeed + y * 0.14F);
            int   alpha = (int)(clamp01(scanlineAlpha * wave) * 255.0F);
            g.fill(INTRO_FILL, 0, y, this.width, y + 1, alpha << 24);
        }
    }

    private void renderNoiseOverlay(GuiGraphicsExtractor g, double elapsedSeconds) {
        if (noiseAlpha <= 0.0F || noiseDensity <= 0.0F) {
            return;
        }

        int frame   = (int)(elapsedSeconds * 60.0);
        int samples = Math.max(1, (int)(noiseDensity * 140.0F));
        int maxSize = Math.max(1, noiseMaxBlockSize);

        for (int i = 0; i < samples; ++i) {
            float gate = noiseHash01(frame, i * 23 + 11);
            if (gate < 0.6F) {
                continue;
            }
            int x     = (int)(noiseHash01(frame, i * 41 + 3)  * this.width);
            int y     = (int)(noiseHash01(frame, i * 59 + 17) * this.height);
            int size  = 1 + (int)(noiseHash01(frame, i * 83 + 9) * maxSize);
            int alpha = (int)(clamp01(noiseAlpha * (0.35F + 0.65F * gate)) * 255.0F);
            g.fill(INTRO_FILL, x, y, x + size, y + size, (alpha << 24) | 0xFFFFFF);
        }
    }

    private void renderDebugOverlay(GuiGraphicsExtractor g, double elapsedSeconds) {
        int y = 6;
        g.text(this.font,
                "Logo Debug: F6 overlay  F7 loop  Arrows select/edit  Shift=coarse  R reset",
                6, y, 0xFF90FF90, true);
        y += 10;
        g.text(this.font,
                String.format("t=%.2fs  loop=%s", elapsedSeconds, this.debugLoopEnabled ? "on" : "off"),
                6, y, 0xFFFFFFFF, true);
        y += 10;

        for (int i = 0; i < PARAM_COUNT; ++i) {
            int color = i == this.selectedParameter ? 0xFFFFE066 : 0xFFC8C8C8;
            g.text(this.font, this.getParameterLine(i), 6, y, color, true);
            y += 9;
        }
    }

    // ===== Low-level texture rendering =====

    /**
     * Renders the intro texture as a screen-space quad with the given color tint and blend mode.
     * blendSrc/blendDst follow the original GL constants:
     *   BLEND_SRC_ALPHA + BLEND_ONE_MINUS_SRC_ALPHA = normal alpha
     *   BLEND_SRC_ALPHA + BLEND_ONE                  = additive
     */
    private void renderIconPass(GuiGraphicsExtractor g,
                                float centerX, float centerY, int iconSize,
                                float scaleX, float scaleY,
                                float red, float green, float blue, float alpha,
                                int blendSrc, int blendDst) {
        if (alpha <= 0.0F) {
            return;
        }

        float scaledW = iconSize * scaleX;
        float scaledH = iconSize * scaleY;
        int x = Math.round(centerX - scaledW * 0.5F);
        int y = Math.round(centerY - scaledH * 0.5F);
        int w = Math.max(1, Math.round(scaledW));
        int h = Math.max(1, Math.round(scaledH));

        int color = argb(alpha, red, green, blue);
        // Additive when blendDst == BLEND_ONE; normal alpha otherwise
        boolean additive = (blendDst == BLEND_ONE);
        RenderPipeline pipeline = additive ? INTRO_TEXTURED_ADDITIVE : INTRO_TEXTURED;

        // Setting textureWidth = w and srcWidth = w gives UV u0=0, u1=1 (full texture)
        g.blit(pipeline, TEXTURE_ID, x, y, 0.0f, 0.0f, w, h, w, h, w, h, color);
    }

    /** Uniform-scale convenience overload (matches source). */
    private void renderIconPass(GuiGraphicsExtractor g,
                                float centerX, float centerY, int iconSize,
                                float scale,
                                float red, float green, float blue, float alpha,
                                int blendSrc, int blendDst) {
        renderIconPass(g, centerX, centerY, iconSize, scale, scale, red, green, blue, alpha, blendSrc, blendDst);
    }

    /**
     * Renders a horizontal band of the texture from [topPx, topPx+bandHeight) (in iconSize units)
     * at the corresponding screen position, offset by (centerX, centerY).
     */
    private void renderIconSlicePass(GuiGraphicsExtractor g,
                                     float centerX, float centerY, int iconSize,
                                     float topPx, float bandHeight, float alpha) {
        if (alpha <= 0.0F || bandHeight <= 0.0F) {
            return;
        }

        int sx  = Math.round(centerX - iconSize * 0.5F);
        int sy  = Math.round(centerY - iconSize * 0.5F + topPx);
        int sw  = iconSize;
        int sh  = Math.max(1, Math.round(bandHeight));

        // UV slice: v0 = topPx/iconSize, v1 = (topPx+bandHeight)/iconSize
        // With textureHeight = iconSize: v = topPx, srcH = bandHeight
        int topI  = Math.round(topPx);
        int bandI = Math.max(1, Math.round(bandHeight));

        int color = argb(alpha, 1.0f, 1.0f, 1.0f);
        g.blit(INTRO_TEXTURED, TEXTURE_ID,
                sx, sy, 0.0f, topI, sw, sh, iconSize, bandI, iconSize, iconSize, color);
    }

    // ===== Sound =====

    private void startIntroSound() {
        if (!introSoundPlayed) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(INTRO_SOUND, 1.0f, 1.0f));
            introSoundPlayed = true;
        }
        this.introSoundStarted = true;
    }

    private long resolveSharedSoundStartTime() {
        if (!introSoundPlayed) {
            introSoundStartNanos = System.nanoTime();
        }
        return introSoundStartNanos;
    }

    private void restartDebugLoopCycle() {
        introSoundStartNanos  = System.nanoTime();
        this.soundStartNanos  = introSoundStartNanos;
        this.minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(INTRO_SOUND, 1.0f, 1.0f));
    }

    // ===== Timing =====

    private double getElapsedSeconds() {
        return (System.nanoTime() - this.soundStartNanos) / 1_000_000_000.0;
    }

    private float computeIconAlpha(double elapsedSeconds) {
        if (elapsedSeconds < iconFadeInStartSeconds) {
            return 0.0F;
        }
        if (elapsedSeconds < iconFadeInStartSeconds + iconFadeDurationSeconds) {
            return clamp01((float)((elapsedSeconds - iconFadeInStartSeconds) / iconFadeDurationSeconds));
        }
        if (elapsedSeconds < iconFadeOutStartSeconds) {
            return 1.0F;
        }
        if (elapsedSeconds < iconFadeOutStartSeconds + iconFadeDurationSeconds) {
            return 1.0F - clamp01((float)((elapsedSeconds - iconFadeOutStartSeconds) / iconFadeDurationSeconds));
        }
        return 0.0F;
    }

    private float computeJitterX(double elapsedSeconds) {
        double primary   = Math.sin(2.0 * Math.PI * jitterPrimaryFrequencyHz   * elapsedSeconds);
        double secondary = Math.sin(2.0 * Math.PI * jitterSecondaryFrequencyHz * elapsedSeconds + jitterPhaseOffsetRadians);
        return (float)((primary + secondary * jitterSecondaryWeight) * jitterXAmplitudePixels);
    }

    private float computeJitterY(double elapsedSeconds) {
        double primary   = Math.cos(2.0 * Math.PI * jitterPrimaryFrequencyHz   * elapsedSeconds + jitterPhaseOffsetRadians * 0.5);
        double secondary = Math.cos(2.0 * Math.PI * jitterSecondaryFrequencyHz * elapsedSeconds);
        return (float)((primary + secondary * jitterSecondaryWeight) * jitterYAmplitudePixels);
    }

    private float computeExposureAlpha(double elapsedSeconds) {
        double finalFlashStart = iconFadeOutStartSeconds - finalFlashOffsetFromFadeOutSeconds;

        float first = computePulse(elapsedSeconds,
                flashOneStartSeconds,
                flashDurationSeconds,
                flashAttackRatio, flashIntensity);

        float second = computePulse(elapsedSeconds,
                flashOneStartSeconds + flashDurationSeconds + flashGapSeconds,
                flashDurationSeconds,
                flashAttackRatio, flashIntensity);

        float finalBurst = computePulse(elapsedSeconds,
                finalFlashStart,
                finalFlashDurationSeconds,
                finalFlashAttackRatio, finalFlashIntensity);

        return Math.max(first, Math.max(second, finalBurst));
    }

    private void ensureNextScreenInitialized() {
        if (this.nextScreenInitialized) {
            return;
        }
        this.nextScreen.init(this.width, this.height);
        this.nextScreenInitialized = true;
    }

    // ===== Overlay helper =====

    private void fillBlackOverlay(GuiGraphicsExtractor g, float alpha) {
        int overlayAlpha = (int)(clamp01(alpha) * 255.0F);
        g.fill(INTRO_FILL, 0, 0, this.width, this.height, overlayAlpha << 24);
    }

    // ===== Debug parameter tweaking =====

    private String getParameterLine(int index) {
        return switch (index) {
            case  0 -> String.format("Jitter X amp: %.2f",           jitterXAmplitudePixels);
            case  1 -> String.format("Jitter Y amp: %.2f",           jitterYAmplitudePixels);
            case  2 -> String.format("Jitter primary Hz: %.2f",      jitterPrimaryFrequencyHz);
            case  3 -> String.format("Jitter secondary Hz: %.2f",    jitterSecondaryFrequencyHz);
            case  4 -> String.format("Flash intensity: %.2f",        flashIntensity);
            case  5 -> String.format("Final flash intensity: %.2f",  finalFlashIntensity);
            case  6 -> String.format("Chroma split px: %.2f",        chromaSplitPixels);
            case  7 -> String.format("Chroma alpha: %.2f",           chromaAlpha);
            case  8 -> String.format("Tear chance: %.2f",            tearChance);
            case  9 -> String.format("Tear max offset: %.2f",        tearMaxOffsetPixels);
            case 10 -> String.format("Ghost alpha: %.2f",            ghostAlpha);
            case 11 -> String.format("Bloom alpha: %.2f",            bloomAlpha);
            case 12 -> String.format("Bloom scale: %.3f",            bloomScale);
            case 13 -> String.format("Scanline alpha: %.2f",         scanlineAlpha);
            case 14 -> String.format("Scanline spacing: %d",         scanlineSpacingPixels);
            case 15 -> String.format("Noise alpha: %.2f",            noiseAlpha);
            case 16 -> String.format("Noise density: %.2f",          noiseDensity);
            case 17 -> String.format("Exposure layers: %d",          exposureLayerCount);
            case 18 -> String.format("Smear burst intensity: %.2f",  smearBurstIntensity);
            case 19 -> String.format("Ghost burst range min: %.2f",  ghostBurstRangeMinPixels);
            case 20 -> String.format("Ghost burst range max: %.2f",  ghostBurstRangeMaxPixels);
            case 21 -> String.format("Ghost burst intensity: %.2f",  ghostBurstIntensity);
            default -> "n/a";
        };
    }

    private void adjustSelectedParameter(int direction, boolean coarse) {
        float mult = coarse ? 5.0F : 1.0F;
        switch (this.selectedParameter) {
            case  0 -> jitterXAmplitudePixels    = clamp(jitterXAmplitudePixels    + direction * 0.1F  * mult, 0.0F, 12.0F);
            case  1 -> jitterYAmplitudePixels    = clamp(jitterYAmplitudePixels    + direction * 0.1F  * mult, 0.0F, 12.0F);
            case  2 -> jitterPrimaryFrequencyHz  = clamp(jitterPrimaryFrequencyHz  + direction * 0.1F  * mult, 0.0F, 50.0F);
            case  3 -> jitterSecondaryFrequencyHz = clamp(jitterSecondaryFrequencyHz + direction * 0.1F * mult, 0.0F, 70.0F);
            case  4 -> flashIntensity            = clamp(flashIntensity            + direction * 0.05F * mult, 0.0F, 1.0F);
            case  5 -> finalFlashIntensity       = clamp(finalFlashIntensity       + direction * 0.05F * mult, 0.0F, 1.0F);
            case  6 -> chromaSplitPixels         = clamp(chromaSplitPixels         + direction * 0.1F  * mult, 0.0F, 18.0F);
            case  7 -> chromaAlpha               = clamp(chromaAlpha               + direction * 0.02F * mult, 0.0F, 1.0F);
            case  8 -> tearChance                = clamp(tearChance                + direction * 0.02F * mult, 0.0F, 1.0F);
            case  9 -> tearMaxOffsetPixels       = clamp(tearMaxOffsetPixels       + direction * 0.2F  * mult, 0.0F, 64.0F);
            case 10 -> ghostAlpha                = clamp(ghostAlpha                + direction * 0.02F * mult, 0.0F, 1.0F);
            case 11 -> bloomAlpha                = clamp(bloomAlpha                + direction * 0.02F * mult, 0.0F, 1.0F);
            case 12 -> bloomScale                = clamp(bloomScale                + direction * 0.005F * mult, 1.0F, 1.5F);
            case 13 -> scanlineAlpha             = clamp(scanlineAlpha             + direction * 0.01F * mult, 0.0F, 1.0F);
            case 14 -> scanlineSpacingPixels     = (int) clamp(scanlineSpacingPixels + direction * mult, 1.0F, 8.0F);
            case 15 -> noiseAlpha                = clamp(noiseAlpha                + direction * 0.01F * mult, 0.0F, 1.0F);
            case 16 -> noiseDensity              = clamp(noiseDensity              + direction * 0.02F * mult, 0.0F, 1.0F);
            case 17 -> exposureLayerCount        = (int) clamp(exposureLayerCount + direction * mult,   1.0F, 10.0F);
            case 18 -> smearBurstIntensity       = clamp(smearBurstIntensity       + direction * 0.03F * mult, 0.0F, 1.0F);
            case 19 -> ghostBurstRangeMinPixels  = clamp(ghostBurstRangeMinPixels  + direction * 0.2F  * mult, 0.0F, 64.0F);
            case 20 -> ghostBurstRangeMaxPixels  = clamp(ghostBurstRangeMaxPixels  + direction * 0.2F  * mult, 0.0F, 80.0F);
            case 21 -> ghostBurstIntensity       = clamp(ghostBurstIntensity       + direction * 0.03F * mult, 0.0F, 1.0F);
        }
        if (ghostBurstRangeMinPixels > ghostBurstRangeMaxPixels) {
            float tmp = ghostBurstRangeMinPixels;
            ghostBurstRangeMinPixels = ghostBurstRangeMaxPixels;
            ghostBurstRangeMaxPixels = tmp;
        }
    }

    private static void resetAllParametersToDefaults() {
        iconFadeInStartSeconds    = DEFAULT_ICON_FADE_IN_START_SECONDS;
        iconFadeDurationSeconds   = DEFAULT_ICON_FADE_DURATION_SECONDS;
        iconFadeOutStartSeconds   = DEFAULT_ICON_FADE_OUT_START_SECONDS;
        titleTransitionStartSeconds    = DEFAULT_TITLE_TRANSITION_START_SECONDS;
        titleTransitionDurationSeconds = DEFAULT_TITLE_TRANSITION_DURATION_SECONDS;
        jitterXAmplitudePixels    = DEFAULT_JITTER_X_AMPLITUDE_PIXELS;
        jitterYAmplitudePixels    = DEFAULT_JITTER_Y_AMPLITUDE_PIXELS;
        jitterPrimaryFrequencyHz  = DEFAULT_JITTER_PRIMARY_FREQUENCY_HZ;
        jitterSecondaryFrequencyHz = DEFAULT_JITTER_SECONDARY_FREQUENCY_HZ;
        jitterSecondaryWeight     = DEFAULT_JITTER_SECONDARY_WEIGHT;
        jitterPhaseOffsetRadians  = DEFAULT_JITTER_PHASE_OFFSET_RADIANS;
        flashOneStartSeconds  = DEFAULT_FLASH_ONE_START_SECONDS;
        flashDurationSeconds  = DEFAULT_FLASH_DURATION_SECONDS;
        flashGapSeconds       = DEFAULT_FLASH_GAP_SECONDS;
        flashAttackRatio      = DEFAULT_FLASH_ATTACK_RATIO;
        flashIntensity        = DEFAULT_FLASH_INTENSITY;
        finalFlashOffsetFromFadeOutSeconds = DEFAULT_FINAL_FLASH_OFFSET_FROM_FADE_OUT_SECONDS;
        finalFlashDurationSeconds          = DEFAULT_FINAL_FLASH_DURATION_SECONDS;
        finalFlashAttackRatio              = DEFAULT_FINAL_FLASH_ATTACK_RATIO;
        finalFlashIntensity                = DEFAULT_FINAL_FLASH_INTENSITY;
        exposureLayerCount = DEFAULT_EXPOSURE_LAYER_COUNT;
        exposureLayerDecay = DEFAULT_EXPOSURE_LAYER_DECAY;
        chromaSplitPixels = DEFAULT_CHROMA_SPLIT_PIXELS;
        chromaAlpha       = DEFAULT_CHROMA_ALPHA;
        tearChance            = DEFAULT_TEAR_CHANCE;
        tearMaxOffsetPixels   = DEFAULT_TEAR_MAX_OFFSET_PIXELS;
        tearBandHeightPixels  = DEFAULT_TEAR_BAND_HEIGHT_PIXELS;
        ghostAlpha          = DEFAULT_GHOST_ALPHA;
        ghostOffsetXPixels  = DEFAULT_GHOST_OFFSET_X_PIXELS;
        ghostOffsetYPixels  = DEFAULT_GHOST_OFFSET_Y_PIXELS;
        bloomAlpha       = DEFAULT_BLOOM_ALPHA;
        bloomScale       = DEFAULT_BLOOM_SCALE;
        bloomPulseHz     = DEFAULT_BLOOM_PULSE_HZ;
        bloomPulseAmount = DEFAULT_BLOOM_PULSE_AMOUNT;
        scanlineSpacingPixels = DEFAULT_SCANLINE_SPACING_PIXELS;
        scanlineAlpha         = DEFAULT_SCANLINE_ALPHA;
        scanlineScrollSpeed   = DEFAULT_SCANLINE_SCROLL_SPEED;
        noiseAlpha       = DEFAULT_NOISE_ALPHA;
        noiseDensity     = DEFAULT_NOISE_DENSITY;
        noiseMaxBlockSize = DEFAULT_NOISE_MAX_BLOCK_SIZE;
        ghostBurstDurationSeconds        = DEFAULT_GHOST_BURST_DURATION_SECONDS;
        ghostBurstIntensity              = DEFAULT_GHOST_BURST_INTENSITY;
        ghostBurstRangeMinPixels         = DEFAULT_GHOST_BURST_RANGE_MIN_PIXELS;
        ghostBurstRangeMaxPixels         = DEFAULT_GHOST_BURST_RANGE_MAX_PIXELS;
        ghostBurstBeforeFirstFlashSeconds = DEFAULT_GHOST_BURST_BEFORE_FIRST_FLASH_SECONDS;
        smearBurstDelayAfterFirstFlashSeconds = DEFAULT_SMEAR_BURST_DELAY_AFTER_FIRST_FLASH_SECONDS;
        smearBurstDurationSeconds             = DEFAULT_SMEAR_BURST_DURATION_SECONDS;
        smearBurstIntensity                   = DEFAULT_SMEAR_BURST_INTENSITY;
    }

    // ===== Color helpers =====

    private static int argb(float alpha, float r, float g, float b) {
        return ARGB.color(
            Math.round(clamp01(alpha) * 255),
            Math.round(clamp01(r)    * 255),
            Math.round(clamp01(g)    * 255),
            Math.round(clamp01(b)    * 255));
    }

    // ===== Math helpers =====

    private static float computePulse(double elapsedSeconds, double startSeconds,
                                      double durationSeconds, float attackRatio, float intensity) {
        if (elapsedSeconds < startSeconds || elapsedSeconds > startSeconds + durationSeconds) {
            return 0.0F;
        }
        float progress = clamp01((float)((elapsedSeconds - startSeconds) / durationSeconds));
        float attack   = clamp01(attackRatio);
        float curve;
        if (progress <= attack) {
            float ap = clamp01(progress / Math.max(attack, 0.0001F));
            curve = easeInExpo(ap);
        } else {
            float rp = clamp01((progress - attack) / Math.max(1.0F - attack, 0.0001F));
            curve = (float) Math.exp(-8.0F * rp);
        }
        return clamp01(intensity * curve);
    }

    private static float easeInExpo(float t) {
        if (t <= 0.0F) return 0.0F;
        if (t >= 1.0F) return 1.0F;
        return (float) Math.pow(2.0, 10.0 * (t - 1.0F));
    }

    private static float noiseHash01(int seedA, int seedB) {
        int n = seedA * 374761393 + seedB * 668265263;
        n = (n ^ (n >>> 13)) * 1274126177;
        n = n ^ (n >>> 16);
        return (n & 0x7fffffff) / (float) 0x7fffffff;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp01(t);
    }

    private static float clamp01(float value) {
        return value < 0.0F ? 0.0F : Math.min(value, 1.0F);
    }

    private static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }
}
