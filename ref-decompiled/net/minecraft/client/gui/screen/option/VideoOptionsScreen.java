/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.screen.GraphicsWarningScreen
 *  net.minecraft.client.gui.screen.GraphicsWarningScreen$ChoiceButton
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.screen.option.VideoOptionsScreen
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.SliderWidget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.client.option.SimpleOption$Callbacks
 *  net.minecraft.client.option.SimpleOption$MaxSuppliableIntCallbacks
 *  net.minecraft.client.option.SimpleOption$ValidatingIntSliderCallbacks
 *  net.minecraft.client.option.TextureFilteringMode
 *  net.minecraft.client.resource.VideoWarningManager
 *  net.minecraft.client.util.Monitor
 *  net.minecraft.client.util.VideoMode
 *  net.minecraft.client.util.Window
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 */
package net.minecraft.client.gui.screen.option;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.GraphicsWarningScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.TextureFilteringMode;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class VideoOptionsScreen
extends GameOptionsScreen {
    private static final Text TITLE_TEXT = Text.translatable((String)"options.videoTitle");
    private static final Text IMPROVED_TRANSPARENCY_TEXT = Text.translatable((String)"options.improvedTransparency").formatted(Formatting.ITALIC);
    private static final Text GRAPHICS_WARNING_MESSAGE_TEXT = Text.translatable((String)"options.graphics.warning.message", (Object[])new Object[]{IMPROVED_TRANSPARENCY_TEXT, IMPROVED_TRANSPARENCY_TEXT});
    private static final Text GRAPHICS_WARNING_TITLE_TEXT = Text.translatable((String)"options.graphics.warning.title").formatted(Formatting.RED);
    private static final Text GRAPHICS_WARNING_ACCEPT_TEXT = Text.translatable((String)"options.graphics.warning.accept");
    private static final Text GRAPHICS_WARNING_CANCEL_TEXT = Text.translatable((String)"options.graphics.warning.cancel");
    private static final Text DISPLAY_HEADER_TEXT = Text.translatable((String)"options.video.display.header");
    private static final Text QUALITY_HEADER_TEXT = Text.translatable((String)"options.video.quality.header");
    private static final Text INTERFACE_HEADER_TEXT = Text.translatable((String)"options.video.preferences.header");
    private final VideoWarningManager warningManager;
    private final int mipmapLevels;
    private final int maxAnisotropy;
    private final TextureFilteringMode field_64673;

    private static SimpleOption<?>[] getQualityOptions(GameOptions options) {
        return new SimpleOption[]{options.getBiomeBlendRadius(), options.getViewDistance(), options.getChunkBuilderMode(), options.getSimulationDistance(), options.getAo(), options.getCloudRenderMode(), options.getParticles(), options.getMipmapLevels(), options.getEntityShadows(), options.getEntityDistanceScaling(), options.getMenuBackgroundBlurriness(), options.getCloudRenderDistance(), options.getCutoutLeaves(), options.getImprovedTransparency(), options.getTextureFiltering(), options.getMaxAnisotropy(), options.getWeatherRadius()};
    }

    private static SimpleOption<?>[] getDisplayOptions(GameOptions options) {
        return new SimpleOption[]{options.getMaxFps(), options.getEnableVsync(), options.getInactivityFpsLimit(), options.getGuiScale(), options.getFullscreen(), options.getGamma()};
    }

    private static SimpleOption<?>[] getInterfaceOptions(GameOptions options) {
        return new SimpleOption[]{options.getShowAutosaveIndicator(), options.getVignette(), options.getAttackIndicator(), options.getChunkFade()};
    }

    public VideoOptionsScreen(Screen parent, MinecraftClient client, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE_TEXT);
        this.warningManager = client.getVideoWarningManager();
        this.warningManager.reset();
        if (((Boolean)gameOptions.getImprovedTransparency().getValue()).booleanValue()) {
            this.warningManager.acceptAfterWarnings();
        }
        this.mipmapLevels = (Integer)gameOptions.getMipmapLevels().getValue();
        this.maxAnisotropy = (Integer)gameOptions.getMaxAnisotropy().getValue();
        this.field_64673 = (TextureFilteringMode)gameOptions.getTextureFiltering().getValue();
    }

    protected void addOptions() {
        int j;
        int i = -1;
        Window window = this.client.getWindow();
        Monitor monitor = window.getMonitor();
        if (monitor == null) {
            j = -1;
        } else {
            Optional optional = window.getFullscreenVideoMode();
            j = optional.map(arg_0 -> ((Monitor)monitor).findClosestVideoModeIndex(arg_0)).orElse(-1);
        }
        SimpleOption simpleOption = new SimpleOption("options.fullscreen.resolution", SimpleOption.emptyTooltip(), (optionText, value) -> {
            if (monitor == null) {
                return Text.translatable((String)"options.fullscreen.unavailable");
            }
            if (value == -1) {
                return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.fullscreen.current"));
            }
            VideoMode videoMode = monitor.getVideoMode(value.intValue());
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.fullscreen.entry", (Object[])new Object[]{videoMode.getWidth(), videoMode.getHeight(), videoMode.getRefreshRate(), videoMode.getRedBits() + videoMode.getGreenBits() + videoMode.getBlueBits()}));
        }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(-1, monitor != null ? monitor.getVideoModeCount() - 1 : -1), (Object)j, value -> {
            if (monitor == null) {
                return;
            }
            window.setFullscreenVideoMode(value == -1 ? Optional.empty() : Optional.of(monitor.getVideoMode(value.intValue())));
        });
        this.body.addHeader(DISPLAY_HEADER_TEXT);
        this.body.addSingleOptionEntry(simpleOption);
        this.body.addAll(VideoOptionsScreen.getDisplayOptions((GameOptions)this.gameOptions));
        this.body.addHeader(QUALITY_HEADER_TEXT);
        this.body.addSingleOptionEntry(this.gameOptions.getPreset());
        this.body.addAll(VideoOptionsScreen.getQualityOptions((GameOptions)this.gameOptions));
        this.body.addHeader(INTERFACE_HEADER_TEXT);
        this.body.addAll(VideoOptionsScreen.getInterfaceOptions((GameOptions)this.gameOptions));
    }

    public void tick() {
        ClickableWidget clickableWidget;
        if (this.body != null && (clickableWidget = this.body.getWidgetFor(this.gameOptions.getMaxAnisotropy())) instanceof SliderWidget) {
            SliderWidget sliderWidget = (SliderWidget)clickableWidget;
            sliderWidget.active = this.gameOptions.getTextureFiltering().getValue() == TextureFilteringMode.ANISOTROPIC;
        }
        super.tick();
    }

    public void close() {
        this.client.getWindow().applyFullscreenVideoMode();
        super.close();
    }

    public void removed() {
        if ((Integer)this.gameOptions.getMipmapLevels().getValue() != this.mipmapLevels || (Integer)this.gameOptions.getMaxAnisotropy().getValue() != this.maxAnisotropy || this.gameOptions.getTextureFiltering().getValue() != this.field_64673) {
            this.client.setMipmapLevels(((Integer)this.gameOptions.getMipmapLevels().getValue()).intValue());
            this.client.reloadResourcesConcurrently();
        }
        super.removed();
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) {
            if (this.warningManager.shouldWarn()) {
                String string3;
                String string2;
                ArrayList list = Lists.newArrayList((Object[])new Text[]{GRAPHICS_WARNING_MESSAGE_TEXT, ScreenTexts.LINE_BREAK});
                String string = this.warningManager.getRendererWarning();
                if (string != null) {
                    list.add(ScreenTexts.LINE_BREAK);
                    list.add(Text.translatable((String)"options.graphics.warning.renderer", (Object[])new Object[]{string}).formatted(Formatting.GRAY));
                }
                if ((string2 = this.warningManager.getVendorWarning()) != null) {
                    list.add(ScreenTexts.LINE_BREAK);
                    list.add(Text.translatable((String)"options.graphics.warning.vendor", (Object[])new Object[]{string2}).formatted(Formatting.GRAY));
                }
                if ((string3 = this.warningManager.getVersionWarning()) != null) {
                    list.add(ScreenTexts.LINE_BREAK);
                    list.add(Text.translatable((String)"options.graphics.warning.version", (Object[])new Object[]{string3}).formatted(Formatting.GRAY));
                }
                this.client.setScreen((Screen)new GraphicsWarningScreen(GRAPHICS_WARNING_TITLE_TEXT, (List)list, ImmutableList.of((Object)new GraphicsWarningScreen.ChoiceButton(GRAPHICS_WARNING_ACCEPT_TEXT, button -> {
                    this.gameOptions.getImprovedTransparency().setValue((Object)true);
                    MinecraftClient.getInstance().worldRenderer.reload();
                    this.warningManager.acceptAfterWarnings();
                    this.client.setScreen((Screen)this);
                }), (Object)new GraphicsWarningScreen.ChoiceButton(GRAPHICS_WARNING_CANCEL_TEXT, button -> {
                    this.warningManager.acceptAfterWarnings();
                    this.gameOptions.getImprovedTransparency().setValue((Object)false);
                    this.updateImprovedTransparencyButtonValue();
                    this.client.setScreen((Screen)this);
                }))));
            }
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.client.isCtrlPressed()) {
            SimpleOption simpleOption = this.gameOptions.getGuiScale();
            SimpleOption.Callbacks callbacks = simpleOption.getCallbacks();
            if (callbacks instanceof SimpleOption.MaxSuppliableIntCallbacks) {
                CyclingButtonWidget cyclingButtonWidget;
                SimpleOption.MaxSuppliableIntCallbacks maxSuppliableIntCallbacks = (SimpleOption.MaxSuppliableIntCallbacks)callbacks;
                int i = (Integer)simpleOption.getValue();
                int j = i == 0 ? maxSuppliableIntCallbacks.maxInclusive() + 1 : i;
                int k = j + (int)Math.signum(verticalAmount);
                if (k != 0 && k <= maxSuppliableIntCallbacks.maxInclusive() && k >= maxSuppliableIntCallbacks.minInclusive() && (cyclingButtonWidget = (CyclingButtonWidget)this.body.getWidgetFor(simpleOption)) != null) {
                    simpleOption.setValue((Object)k);
                    cyclingButtonWidget.setValue((Object)k);
                    this.body.setScrollY(0.0);
                    return true;
                }
            }
            return false;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void updateFullscreenButtonValue(boolean fullscreen) {
        ClickableWidget clickableWidget;
        if (this.body != null && (clickableWidget = this.body.getWidgetFor(this.gameOptions.getFullscreen())) != null) {
            CyclingButtonWidget cyclingButtonWidget = (CyclingButtonWidget)clickableWidget;
            cyclingButtonWidget.setValue((Object)fullscreen);
        }
    }

    public void updateImprovedTransparencyButtonValue() {
        SimpleOption simpleOption;
        ClickableWidget clickableWidget;
        if (this.body != null && (clickableWidget = this.body.getWidgetFor(simpleOption = this.gameOptions.getImprovedTransparency())) != null) {
            CyclingButtonWidget cyclingButtonWidget = (CyclingButtonWidget)clickableWidget;
            cyclingButtonWidget.setValue((Object)((Boolean)simpleOption.getValue()));
        }
    }
}

