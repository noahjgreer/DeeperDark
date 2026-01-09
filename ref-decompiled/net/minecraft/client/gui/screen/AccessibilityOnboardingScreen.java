package net.minecraft.client.gui.screen;

import com.mojang.text2speech.Narrator;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class AccessibilityOnboardingScreen extends Screen {
   private static final Text TITLE_TEXT = Text.translatable("accessibility.onboarding.screen.title");
   private static final Text NARRATOR_PROMPT = Text.translatable("accessibility.onboarding.screen.narrator");
   private static final int field_41838 = 4;
   private static final int field_41839 = 16;
   private static final float field_60459 = 1000.0F;
   private final LogoDrawer logoDrawer;
   private final GameOptions gameOptions;
   private final boolean isNarratorUsable;
   private boolean narratorPrompted;
   private float narratorPromptTimer;
   private final Runnable onClose;
   @Nullable
   private NarratedMultilineTextWidget textWidget;
   private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, this.yMargin(), 33);
   private float fadeTime;
   private boolean fading = true;
   private float closeTime;

   public AccessibilityOnboardingScreen(GameOptions gameOptions, Runnable onClose) {
      super(TITLE_TEXT);
      this.gameOptions = gameOptions;
      this.onClose = onClose;
      this.logoDrawer = new LogoDrawer(true);
      this.isNarratorUsable = MinecraftClient.getInstance().getNarratorManager().isActive();
   }

   public void init() {
      DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addBody(DirectionalLayoutWidget.vertical());
      directionalLayoutWidget.getMainPositioner().alignHorizontalCenter().margin(4);
      this.textWidget = (NarratedMultilineTextWidget)directionalLayoutWidget.add(new NarratedMultilineTextWidget(this.width, this.title, this.textRenderer), (Consumer)((positioner) -> {
         positioner.margin(8);
      }));
      ClickableWidget var3 = this.gameOptions.getNarrator().createWidget(this.gameOptions);
      if (var3 instanceof CyclingButtonWidget cyclingButtonWidget) {
         this.narratorToggleButton = cyclingButtonWidget;
         this.narratorToggleButton.active = this.isNarratorUsable;
         directionalLayoutWidget.add(this.narratorToggleButton);
      }

      directionalLayoutWidget.add(AccessibilityOnboardingButtons.createAccessibilityButton(150, (button) -> {
         this.setScreen(new AccessibilityOptionsScreen(this, this.client.options));
      }, false));
      directionalLayoutWidget.add(AccessibilityOnboardingButtons.createLanguageButton(150, (button) -> {
         this.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
      }, false));
      this.layout.addFooter(ButtonWidget.builder(ScreenTexts.CONTINUE, (button) -> {
         this.close();
      }).build());
      this.layout.forEachChild(this::addDrawableChild);
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      if (this.textWidget != null) {
         this.textWidget.initMaxWidth(this.width);
      }

      this.layout.refreshPositions();
   }

   protected void setInitialFocus() {
      if (this.isNarratorUsable && this.narratorToggleButton != null) {
         this.setInitialFocus(this.narratorToggleButton);
      } else {
         super.setInitialFocus();
      }

   }

   private int yMargin() {
      return 90;
   }

   public void close() {
      this.closeTime = (float)Util.getMeasuringTimeMs();
   }

   private void setScreen(Screen screen) {
      this.saveAndRun(false, () -> {
         this.client.setScreen(screen);
      });
   }

   private void saveAndRun(boolean dontShowAgain, Runnable callback) {
      if (dontShowAgain) {
         this.gameOptions.setAccessibilityOnboarded();
      }

      Narrator.getNarrator().clear();
      callback.run();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      this.tickNarratorPrompt();
      if (this.fadeTime == 0.0F && this.fading) {
         this.fadeTime = (float)Util.getMeasuringTimeMs();
      }

      float f;
      float g;
      if (this.fadeTime > 0.0F) {
         f = ((float)Util.getMeasuringTimeMs() - this.fadeTime) / 2000.0F;
         g = 1.0F;
         if (f >= 1.0F) {
            this.fading = false;
            this.fadeTime = 0.0F;
         } else {
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            g = MathHelper.clampedMap(f, 0.5F, 1.0F, 0.0F, 1.0F);
         }

         this.setWidgetAlpha(g);
      }

      if (this.closeTime > 0.0F) {
         f = 1.0F - ((float)Util.getMeasuringTimeMs() - this.closeTime) / 1000.0F;
         g = 0.0F;
         if (f <= 0.0F) {
            this.closeTime = 0.0F;
            this.saveAndRun(true, this.onClose);
         } else {
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            g = MathHelper.clampedMap(f, 0.5F, 1.0F, 0.0F, 1.0F);
         }

         this.setWidgetAlpha(g);
      }

      this.logoDrawer.draw(context, this.width, 1.0F);
   }

   protected void renderPanoramaBackground(DrawContext context, float deltaTicks) {
      this.client.gameRenderer.getRotatingPanoramaRenderer().render(context, this.width, this.height, false);
   }

   private void tickNarratorPrompt() {
      if (!this.narratorPrompted && this.isNarratorUsable) {
         if (this.narratorPromptTimer < 40.0F) {
            ++this.narratorPromptTimer;
         } else if (this.client.isWindowFocused()) {
            Narrator.getNarrator().say(NARRATOR_PROMPT.getString(), true, 1.0F);
            this.narratorPrompted = true;
         }
      }

   }
}
