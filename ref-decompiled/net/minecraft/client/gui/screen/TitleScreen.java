/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.LogoDrawer
 *  net.minecraft.client.gui.RotatingCubeMapRenderer
 *  net.minecraft.client.gui.screen.AccessibilityOnboardingButtons
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.SplashTextRenderer
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen
 *  net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen
 *  net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen
 *  net.minecraft.client.gui.screen.option.LanguageOptionsScreen
 *  net.minecraft.client.gui.screen.option.OptionsScreen
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.screen.world.SelectWorldScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.PressableTextWidget
 *  net.minecraft.client.gui.widget.TextIconButtonWidget
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.toast.SystemToast
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.gen.GeneratorOptions
 *  net.minecraft.world.gen.WorldPresets
 *  net.minecraft.world.level.storage.LevelStorage
 *  net.minecraft.world.level.storage.LevelStorage$Session
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.AccessibilityOnboardingButtons;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.storage.LevelStorage;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TitleScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text NARRATOR_SCREEN_TITLE = Text.translatable((String)"narrator.screen.title");
    private static final Text COPYRIGHT = Text.translatable((String)"title.credits");
    private static final String DEMO_WORLD_NAME = "Demo_World";
    private @Nullable SplashTextRenderer splashText;
    private @Nullable RealmsNotificationsScreen realmsNotificationGui;
    private boolean doBackgroundFade;
    private long backgroundFadeStart;
    private final LogoDrawer logoDrawer;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean doBackgroundFade) {
        this(doBackgroundFade, null);
    }

    public TitleScreen(boolean doBackgroundFade, @Nullable LogoDrawer logoDrawer) {
        super(NARRATOR_SCREEN_TITLE);
        this.doBackgroundFade = doBackgroundFade;
        this.logoDrawer = Objects.requireNonNullElseGet(logoDrawer, () -> new LogoDrawer(false));
    }

    private boolean isRealmsNotificationsGuiDisplayed() {
        return this.realmsNotificationGui != null;
    }

    public void tick() {
        if (this.isRealmsNotificationsGuiDisplayed()) {
            this.realmsNotificationGui.tick();
        }
    }

    public static void registerTextures(TextureManager textureManager) {
        textureManager.registerTexture(LogoDrawer.LOGO_TEXTURE);
        textureManager.registerTexture(LogoDrawer.EDITION_TEXTURE);
        textureManager.registerTexture(RotatingCubeMapRenderer.OVERLAY_TEXTURE);
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        if (this.splashText == null) {
            this.splashText = this.client.getSplashTextLoader().get();
        }
        int i = this.textRenderer.getWidth((StringVisitable)COPYRIGHT);
        int j = this.width - i - 2;
        int k = 24;
        int l = this.height / 4 + 48;
        l = this.client.isDemo() ? this.addDemoWidgets(l, 24) : this.addNormalWidgets(l, 24);
        l = this.addDevelopmentWidgets(l, 24);
        TextIconButtonWidget textIconButtonWidget = (TextIconButtonWidget)this.addDrawableChild((Element)AccessibilityOnboardingButtons.createLanguageButton((int)20, button -> this.client.setScreen((Screen)new LanguageOptionsScreen((Screen)this, this.client.options, this.client.getLanguageManager())), (boolean)true));
        textIconButtonWidget.setPosition(this.width / 2 - 124, l += 36);
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"menu.options"), button -> this.client.setScreen((Screen)new OptionsScreen((Screen)this, this.client.options))).dimensions(this.width / 2 - 100, l, 98, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"menu.quit"), button -> this.client.scheduleStop()).dimensions(this.width / 2 + 2, l, 98, 20).build());
        TextIconButtonWidget textIconButtonWidget2 = (TextIconButtonWidget)this.addDrawableChild((Element)AccessibilityOnboardingButtons.createAccessibilityButton((int)20, button -> this.client.setScreen((Screen)new AccessibilityOptionsScreen((Screen)this, this.client.options)), (boolean)true));
        textIconButtonWidget2.setPosition(this.width / 2 + 104, l);
        this.addDrawableChild((Element)new PressableTextWidget(j, this.height - 10, i, 10, COPYRIGHT, button -> this.client.setScreen((Screen)new CreditsAndAttributionScreen((Screen)this)), this.textRenderer));
        if (this.realmsNotificationGui == null) {
            this.realmsNotificationGui = new RealmsNotificationsScreen();
        }
        if (this.isRealmsNotificationsGuiDisplayed()) {
            this.realmsNotificationGui.init(this.width, this.height);
        }
    }

    private int addDevelopmentWidgets(int y, int spacingY) {
        if (SharedConstants.isDevelopment) {
            this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"Create Test World"), button -> CreateWorldScreen.showTestWorld((MinecraftClient)this.client, () -> this.client.setScreen((Screen)this))).dimensions(this.width / 2 - 100, y += spacingY, 200, 20).build());
        }
        return y;
    }

    private int addNormalWidgets(int y, int spacingY) {
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"menu.singleplayer"), button -> this.client.setScreen((Screen)new SelectWorldScreen((Screen)this))).dimensions(this.width / 2 - 100, y, 200, 20).build());
        Text text = this.getMultiplayerDisabledText();
        boolean bl = text == null;
        Tooltip tooltip = text != null ? Tooltip.of((Text)text) : null;
        y += spacingY;
        ((ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"menu.multiplayer"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, onMultiplayerButtonPressed(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this)).dimensions((int)(this.width / 2 - 100), (int)v0, (int)200, (int)20).tooltip((Tooltip)tooltip).build())).active = bl;
        ((ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"menu.online"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, method_55814(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this)).dimensions((int)(this.width / 2 - 100), (int)v1, (int)200, (int)20).tooltip((Tooltip)tooltip).build())).active = bl;
        return y += spacingY;
    }

    private @Nullable Text getMultiplayerDisabledText() {
        if (this.client.isMultiplayerEnabled()) {
            return null;
        }
        if (this.client.isUsernameBanned()) {
            return Text.translatable((String)"title.multiplayer.disabled.banned.name");
        }
        BanDetails banDetails = this.client.getMultiplayerBanDetails();
        if (banDetails != null) {
            if (banDetails.expires() != null) {
                return Text.translatable((String)"title.multiplayer.disabled.banned.temporary");
            }
            return Text.translatable((String)"title.multiplayer.disabled.banned.permanent");
        }
        return Text.translatable((String)"title.multiplayer.disabled");
    }

    private int addDemoWidgets(int y, int spacingY) {
        boolean bl = this.canReadDemoWorldData();
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"menu.playdemo"), button -> {
            if (bl) {
                this.client.createIntegratedServerLoader().start(DEMO_WORLD_NAME, () -> this.client.setScreen((Screen)this));
            } else {
                this.client.createIntegratedServerLoader().createAndStart(DEMO_WORLD_NAME, MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions, (Screen)this);
            }
        }).dimensions(this.width / 2 - 100, y, 200, 20).build());
        ButtonWidget buttonWidget = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"menu.resetdemo"), button -> {
            LevelStorage levelStorage = this.client.getLevelStorage();
            try (LevelStorage.Session session = levelStorage.createSessionWithoutSymlinkCheck(DEMO_WORLD_NAME);){
                if (session.levelDatExists()) {
                    this.client.setScreen((Screen)new ConfirmScreen(arg_0 -> this.onDemoDeletionConfirmed(arg_0), (Text)Text.translatable((String)"selectWorld.deleteQuestion"), (Text)Text.translatable((String)"selectWorld.deleteWarning", (Object[])new Object[]{MinecraftServer.DEMO_LEVEL_INFO.getLevelName()}), (Text)Text.translatable((String)"selectWorld.deleteButton"), ScreenTexts.CANCEL));
                }
            }
            catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast((MinecraftClient)this.client, (String)DEMO_WORLD_NAME);
                LOGGER.warn("Failed to access demo world", (Throwable)iOException);
            }
        }).dimensions(this.width / 2 - 100, y += spacingY, 200, 20).build());
        buttonWidget.active = bl;
        return y;
    }

    private boolean canReadDemoWorldData() {
        boolean bl;
        block8: {
            LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck(DEMO_WORLD_NAME);
            try {
                bl = session.levelDatExists();
                if (session == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (session != null) {
                        try {
                            session.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException iOException) {
                    SystemToast.addWorldAccessFailureToast((MinecraftClient)this.client, (String)DEMO_WORLD_NAME);
                    LOGGER.warn("Failed to read demo world data", (Throwable)iOException);
                    return false;
                }
            }
            session.close();
        }
        return bl;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }
        float f = 1.0f;
        if (this.doBackgroundFade) {
            float g = (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0f;
            if (g > 1.0f) {
                this.doBackgroundFade = false;
            } else {
                g = MathHelper.clamp((float)g, (float)0.0f, (float)1.0f);
                f = MathHelper.clampedMap((float)g, (float)0.5f, (float)1.0f, (float)0.0f, (float)1.0f);
            }
            this.setWidgetAlpha(f);
        }
        this.renderPanoramaBackground(context, deltaTicks);
        super.render(context, mouseX, mouseY, deltaTicks);
        this.logoDrawer.draw(context, this.width, this.logoDrawer.shouldIgnoreAlpha() ? 1.0f : f);
        if (this.splashText != null && !((Boolean)this.client.options.getHideSplashTexts().getValue()).booleanValue()) {
            this.splashText.render(context, this.width, this.textRenderer, f);
        }
        String string = "Minecraft " + SharedConstants.getGameVersion().name();
        string = this.client.isDemo() ? string + " Demo" : string + (String)("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
        if (MinecraftClient.getModStatus().isModded()) {
            string = string + I18n.translate((String)"menu.modded", (Object[])new Object[0]);
        }
        context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, ColorHelper.getWhite((float)f));
        if (this.isRealmsNotificationsGuiDisplayed() && f >= 1.0f) {
            this.realmsNotificationGui.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) {
            return true;
        }
        return this.isRealmsNotificationsGuiDisplayed() && this.realmsNotificationGui.mouseClicked(click, doubled);
    }

    public void removed() {
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.removed();
        }
    }

    public void onDisplayed() {
        super.onDisplayed();
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.onDisplayed();
        }
    }

    private void onDemoDeletionConfirmed(boolean delete) {
        if (delete) {
            try (LevelStorage.Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck(DEMO_WORLD_NAME);){
                session.deleteSessionLock();
            }
            catch (IOException iOException) {
                SystemToast.addWorldDeleteFailureToast((MinecraftClient)this.client, (String)DEMO_WORLD_NAME);
                LOGGER.warn("Failed to delete demo world", (Throwable)iOException);
            }
        }
        this.client.setScreen((Screen)this);
    }

    public boolean canInterruptOtherScreen() {
        return true;
    }

    private /* synthetic */ void method_55814(ButtonWidget button) {
        this.client.setScreen((Screen)new RealmsMainScreen((Screen)this));
    }

    private /* synthetic */ void onMultiplayerButtonPressed(ButtonWidget button) {
        MultiplayerScreen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen((Screen)this) : new MultiplayerWarningScreen((Screen)this);
        this.client.setScreen((Screen)screen);
    }
}

