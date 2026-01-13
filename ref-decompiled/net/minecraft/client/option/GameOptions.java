/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.ChatHud
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.input.SystemKeycodes
 *  net.minecraft.client.option.AttackIndicator
 *  net.minecraft.client.option.CloudRenderMode
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.GameOptions$5
 *  net.minecraft.client.option.GameOptions$OptionVisitor
 *  net.minecraft.client.option.GameOptions$Visitor
 *  net.minecraft.client.option.GraphicsMode
 *  net.minecraft.client.option.InactivityFpsLimit
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.option.KeyBinding$Category
 *  net.minecraft.client.option.MusicToastMode
 *  net.minecraft.client.option.NarratorMode
 *  net.minecraft.client.option.Perspective
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.client.option.SimpleOption$Callbacks
 *  net.minecraft.client.option.SimpleOption$CategoricalSliderCallbacks
 *  net.minecraft.client.option.SimpleOption$DoubleSliderCallbacks
 *  net.minecraft.client.option.SimpleOption$LazyCyclingCallbacks
 *  net.minecraft.client.option.SimpleOption$MaxSuppliableIntCallbacks
 *  net.minecraft.client.option.SimpleOption$PotentialValuesBasedCallbacks
 *  net.minecraft.client.option.SimpleOption$TooltipFactory
 *  net.minecraft.client.option.SimpleOption$ValidatingIntSliderCallbacks
 *  net.minecraft.client.option.StickyKeyBinding
 *  net.minecraft.client.option.TextureFilteringMode
 *  net.minecraft.client.render.ChunkBuilderMode
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.resource.VideoWarningManager
 *  net.minecraft.client.sound.MusicTracker$MusicFrequency
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.SoundPreviewer
 *  net.minecraft.client.sound.SoundSystem
 *  net.minecraft.client.tutorial.TutorialStep
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.InputUtil$Type
 *  net.minecraft.client.util.VideoMode
 *  net.minecraft.client.util.Window
 *  net.minecraft.datafixer.DataFixTypes
 *  net.minecraft.entity.player.PlayerModelPart
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.message.ChatVisibility
 *  net.minecraft.network.packet.c2s.common.SyncedClientOptions
 *  net.minecraft.particle.ParticlesMode
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.resource.ResourcePackManager
 *  net.minecraft.resource.ResourcePackProfile
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.text.Text
 *  net.minecraft.util.Arm
 *  net.minecraft.util.JsonHelper
 *  net.minecraft.util.Util
 *  net.minecraft.util.Util$OperatingSystem
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.option;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.InactivityFpsLimit;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.MusicToastMode;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.option.TextureFilteringMode;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundPreviewer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class GameOptions {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> STRING_LIST_TYPE = new /* Unavailable Anonymous Inner Class!! */;
    public static final int field_32150 = 4;
    public static final int field_32153 = 12;
    public static final int field_32154 = 16;
    public static final int field_32155 = 32;
    private static final Splitter COLON_SPLITTER = Splitter.on((char)':').limit(2);
    public static final String EMPTY_STRING = "";
    private static final Text DARK_MOJANG_STUDIOS_BACKGROUND_COLOR_TOOLTIP = Text.translatable((String)"options.darkMojangStudiosBackgroundColor.tooltip");
    private final SimpleOption<Boolean> monochromeLogo = SimpleOption.ofBoolean((String)"options.darkMojangStudiosBackgroundColor", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)DARK_MOJANG_STUDIOS_BACKGROUND_COLOR_TOOLTIP), (boolean)false);
    private static final Text HIDE_LIGHTNING_FLASHES_TOOLTIP = Text.translatable((String)"options.hideLightningFlashes.tooltip");
    private final SimpleOption<Boolean> hideLightningFlashes = SimpleOption.ofBoolean((String)"options.hideLightningFlashes", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)HIDE_LIGHTNING_FLASHES_TOOLTIP), (boolean)false);
    private static final Text HIDE_SPLASH_TEXTS_TOOLTIP = Text.translatable((String)"options.hideSplashTexts.tooltip");
    private final SimpleOption<Boolean> hideSplashTexts = SimpleOption.ofBoolean((String)"options.hideSplashTexts", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)HIDE_SPLASH_TEXTS_TOOLTIP), (boolean)false);
    private final SimpleOption<Double> mouseSensitivity = new SimpleOption("options.sensitivity", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 0.0) {
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.sensitivity.min"));
        }
        if (value == 1.0) {
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.sensitivity.max"));
        }
        return GameOptions.getPercentValueText((Text)optionText, (double)(2.0 * value));
    }, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)0.5, value -> {});
    private final SimpleOption<Integer> viewDistance;
    private final SimpleOption<Integer> simulationDistance;
    private int serverViewDistance = 0;
    private final SimpleOption<Double> entityDistanceScaling = new SimpleOption("options.entityDistanceScaling", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(2, 20).withModifier(sliderProgressValue -> (double)sliderProgressValue / 4.0, value -> (int)(value * 4.0), true), Codec.doubleRange((double)0.5, (double)5.0), (Object)1.0, value -> this.onChangeGraphicsOption());
    public static final int MAX_FPS_LIMIT = 260;
    private final SimpleOption<Integer> maxFps = new SimpleOption("options.framerateLimit", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 260) {
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.framerateLimit.max"));
        }
        return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.framerate", (Object[])new Object[]{value}));
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(1, 26).withModifier(value -> value * 10, value -> value / 10, true), Codec.intRange((int)10, (int)260), (Object)120, value -> MinecraftClient.getInstance().getInactivityFpsLimiter().setMaxFps(value.intValue()));
    private boolean applyingGraphicsMode;
    private final SimpleOption<GraphicsMode> preset = new SimpleOption("options.graphics.preset", SimpleOption.constantTooltip((Text)Text.translatable((String)"options.graphics.preset.tooltip")), (optionText, value) -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)value.getTranslationKey())), (SimpleOption.Callbacks)new SimpleOption.CategoricalSliderCallbacks(List.of(GraphicsMode.values()), GraphicsMode.CODEC), GraphicsMode.CODEC, (Object)GraphicsMode.FANCY, arg_0 -> this.applyGraphicsMode(arg_0));
    private static final Text INACTIVITY_FPS_LIMIT_MINIMIZED_TOOLTIP = Text.translatable((String)"options.inactivityFpsLimit.minimized.tooltip");
    private static final Text INACTIVITY_FPS_LIMIT_AFK_TOOLTIP = Text.translatable((String)"options.inactivityFpsLimit.afk.tooltip");
    private final SimpleOption<InactivityFpsLimit> inactivityFpsLimit = new SimpleOption("options.inactivityFpsLimit", option -> switch (5.field_52763[option.ordinal()]) {
        default -> throw new MatchException(null, null);
        case 1 -> Tooltip.of((Text)INACTIVITY_FPS_LIMIT_MINIMIZED_TOOLTIP);
        case 2 -> Tooltip.of((Text)INACTIVITY_FPS_LIMIT_AFK_TOOLTIP);
    }, (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(InactivityFpsLimit.values()), InactivityFpsLimit.CODEC), (Object)InactivityFpsLimit.AFK, inactivityFpsLimit -> {});
    private final SimpleOption<CloudRenderMode> cloudRenderMode = new SimpleOption("options.renderClouds", SimpleOption.emptyTooltip(), (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(CloudRenderMode.values()), Codec.withAlternative((Codec)CloudRenderMode.CODEC, (Codec)Codec.BOOL, value -> value != false ? CloudRenderMode.FANCY : CloudRenderMode.OFF)), (Object)CloudRenderMode.FANCY, value -> this.onChangeGraphicsOption());
    private final SimpleOption<Integer> cloudRenderDistance = new SimpleOption("options.renderCloudsDistance", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.chunks", (Object[])new Object[]{value})), (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(2, 128, true), (Object)128, value -> {
        GameOptions.refreshWorldRenderer((T worldRenderer) -> worldRenderer.getCloudRenderer().scheduleTerrainUpdate());
        this.onChangeGraphicsOption();
    });
    private static final Text WEATHER_RADIUS_TOOLTIP = Text.translatable((String)"options.weatherRadius.tooltip");
    private final SimpleOption<Integer> weatherRadius = new SimpleOption("options.weatherRadius", SimpleOption.constantTooltip((Text)WEATHER_RADIUS_TOOLTIP), (optionText, value) -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.blocks", (Object[])new Object[]{value})), (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(3, 10, true), (Object)10, value -> this.onChangeGraphicsOption());
    private static final Text CUTOUT_LEAVES_TOOLTIP = Text.translatable((String)"options.cutoutLeaves.tooltip");
    private final SimpleOption<Boolean> cutoutLeaves = SimpleOption.ofBoolean((String)"options.cutoutLeaves", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)CUTOUT_LEAVES_TOOLTIP), (boolean)true, boolean_ -> {
        GameOptions.refreshWorldRenderer(WorldRenderer::reload);
        this.onChangeGraphicsOption();
    });
    private static final Text VIGNETTE_TOOLTIP = Text.translatable((String)"options.vignette.tooltip");
    private final SimpleOption<Boolean> vignette = SimpleOption.ofBoolean((String)"options.vignette", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)VIGNETTE_TOOLTIP), (boolean)true);
    private static final Text IMPROVED_TRANSPARENCY_TOOLTIP = Text.translatable((String)"options.improvedTransparency.tooltip");
    private final SimpleOption<Boolean> improvedTransparency = SimpleOption.ofBoolean((String)"options.improvedTransparency", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)IMPROVED_TRANSPARENCY_TOOLTIP), (boolean)false, value -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        VideoWarningManager videoWarningManager = minecraftClient.getVideoWarningManager();
        if (value.booleanValue() && videoWarningManager.canWarn()) {
            videoWarningManager.scheduleWarning();
            return;
        }
        GameOptions.refreshWorldRenderer(WorldRenderer::reload);
        this.onChangeGraphicsOption();
    });
    private final SimpleOption<Boolean> ao = SimpleOption.ofBoolean((String)"options.ao", (boolean)true, value -> {
        GameOptions.refreshWorldRenderer(WorldRenderer::reload);
        this.onChangeGraphicsOption();
    });
    private static final Text CHUNK_FADE_TOOLTIP = Text.translatable((String)"options.chunkFade.tooltip");
    private final SimpleOption<Double> chunkFade = new SimpleOption("options.chunkFade", SimpleOption.constantTooltip((Text)CHUNK_FADE_TOOLTIP), (optionText, value) -> {
        if (value <= 0.0) {
            return Text.translatable((String)"options.chunkFade.none");
        }
        return Text.translatable((String)"options.chunkFade.seconds", (Object[])new Object[]{String.format(Locale.ROOT, "%.2f", value)});
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(0, 40).withModifier(ticks -> (double)ticks / 20.0, seconds -> (int)(seconds * 20.0), true), Codec.doubleRange((double)0.0, (double)2.0), (Object)0.75, value -> {});
    private static final Text NONE_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable((String)"options.prioritizeChunkUpdates.none.tooltip");
    private static final Text BY_PLAYER_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable((String)"options.prioritizeChunkUpdates.byPlayer.tooltip");
    private static final Text NEARBY_CHUNK_BUILDER_MODE_TOOLTIP = Text.translatable((String)"options.prioritizeChunkUpdates.nearby.tooltip");
    private final SimpleOption<ChunkBuilderMode> chunkBuilderMode = new SimpleOption("options.prioritizeChunkUpdates", value -> switch (5.field_37883[value.ordinal()]) {
        default -> throw new MatchException(null, null);
        case 1 -> Tooltip.of((Text)NONE_CHUNK_BUILDER_MODE_TOOLTIP);
        case 2 -> Tooltip.of((Text)BY_PLAYER_CHUNK_BUILDER_MODE_TOOLTIP);
        case 3 -> Tooltip.of((Text)NEARBY_CHUNK_BUILDER_MODE_TOOLTIP);
    }, (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(ChunkBuilderMode.values()), ChunkBuilderMode.CODEC), (Object)ChunkBuilderMode.NONE, value -> this.onChangeGraphicsOption());
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    private final SimpleOption<ChatVisibility> chatVisibility = new SimpleOption("options.chat.visibility", SimpleOption.emptyTooltip(), (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(ChatVisibility.values()), ChatVisibility.CODEC), (Object)ChatVisibility.FULL, value -> {});
    private final SimpleOption<Double> chatOpacity = new SimpleOption("options.chat.opacity", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPercentValueText((Text)optionText, (double)(value * 0.9 + 0.1)), (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatLineSpacing = new SimpleOption("options.chat.line_spacing", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)0.0, value -> {});
    private static final Text MENU_BACKGROUND_BLURRINESS_TOOLTIP = Text.translatable((String)"options.accessibility.menu_background_blurriness.tooltip");
    private static final int DEFAULT_MENU_BACKGROUND_BLURRINESS = 5;
    private final SimpleOption<Integer> menuBackgroundBlurriness = new SimpleOption("options.accessibility.menu_background_blurriness", SimpleOption.constantTooltip((Text)MENU_BACKGROUND_BLURRINESS_TOOLTIP), GameOptions::getGenericValueOrOffText, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(0, 10), (Object)5, value -> this.onChangeGraphicsOption());
    private final SimpleOption<Double> textBackgroundOpacity = new SimpleOption("options.accessibility.text_background_opacity", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)0.5, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> panoramaSpeed = new SimpleOption("options.accessibility.panorama_speed", SimpleOption.emptyTooltip(), GameOptions::getPercentValueText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, value -> {});
    private static final Text HIGH_CONTRAST_TOOLTIP = Text.translatable((String)"options.accessibility.high_contrast.tooltip");
    private final SimpleOption<Boolean> highContrast = SimpleOption.ofBoolean((String)"options.accessibility.high_contrast", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)HIGH_CONTRAST_TOOLTIP), (boolean)false, value -> {
        ResourcePackManager resourcePackManager = MinecraftClient.getInstance().getResourcePackManager();
        boolean bl = resourcePackManager.getEnabledIds().contains("high_contrast");
        if (!bl && value.booleanValue()) {
            if (resourcePackManager.enable("high_contrast")) {
                this.refreshResourcePacks(resourcePackManager);
            }
        } else if (bl && !value.booleanValue() && resourcePackManager.disable("high_contrast")) {
            this.refreshResourcePacks(resourcePackManager);
        }
    });
    private static final Text HIGH_CONTRAST_BLOCK_OUTLINE_TOOLTIP = Text.translatable((String)"options.accessibility.high_contrast_block_outline.tooltip");
    private final SimpleOption<Boolean> highContrastBlockOutline = SimpleOption.ofBoolean((String)"options.accessibility.high_contrast_block_outline", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)HIGH_CONTRAST_BLOCK_OUTLINE_TOOLTIP), (boolean)false);
    private final SimpleOption<Boolean> narratorHotkey = SimpleOption.ofBoolean((String)"options.accessibility.narrator_hotkey", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)(SystemKeycodes.IS_MAC_OS ? Text.translatable((String)"options.accessibility.narrator_hotkey.mac.tooltip") : Text.translatable((String)"options.accessibility.narrator_hotkey.tooltip"))), (boolean)true);
    public @Nullable String fullscreenResolution;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> enabledPlayerModelParts = EnumSet.allOf(PlayerModelPart.class);
    private final SimpleOption<Arm> mainArm = new SimpleOption("options.mainHand", SimpleOption.emptyTooltip(), (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(Arm.values()), Arm.CODEC), (Object)Arm.RIGHT, value -> {});
    public int overrideWidth;
    public int overrideHeight;
    private final SimpleOption<Double> chatScale = new SimpleOption("options.chat.scale", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 0.0) {
            return ScreenTexts.composeToggleText((Text)optionText, (boolean)false);
        }
        return GameOptions.getPercentValueText((Text)optionText, (double)value);
    }, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatWidth = new SimpleOption("options.chat.width", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPixelValueText((Text)optionText, (int)ChatHud.getWidth((double)value)), (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatHeightUnfocused = new SimpleOption("options.chat.height.unfocused", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPixelValueText((Text)optionText, (int)ChatHud.getHeight((double)value)), (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)ChatHud.getDefaultUnfocusedHeight(), value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatHeightFocused = new SimpleOption("options.chat.height.focused", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getPixelValueText((Text)optionText, (int)ChatHud.getHeight((double)value)), (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, value -> MinecraftClient.getInstance().inGameHud.getChatHud().reset());
    private final SimpleOption<Double> chatDelay = new SimpleOption("options.chat.delay_instant", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value <= 0.0) {
            return Text.translatable((String)"options.chat.delay_none");
        }
        return Text.translatable((String)"options.chat.delay", (Object[])new Object[]{String.format(Locale.ROOT, "%.1f", value)});
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(0, 60).withModifier(value -> (double)value / 10.0, value -> (int)(value * 10.0), true), Codec.doubleRange((double)0.0, (double)6.0), (Object)0.0, value -> MinecraftClient.getInstance().getMessageHandler().setChatDelay(value.doubleValue()));
    private static final Text NOTIFICATION_DISPLAY_TIME_TOOLTIP = Text.translatable((String)"options.notifications.display_time.tooltip");
    private final SimpleOption<Double> notificationDisplayTime = new SimpleOption("options.notifications.display_time", SimpleOption.constantTooltip((Text)NOTIFICATION_DISPLAY_TIME_TOOLTIP), (optionText, value) -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.multiplier", (Object[])new Object[]{value})), (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(5, 100).withModifier(sliderProgressValue -> (double)sliderProgressValue / 10.0, value -> (int)(value * 10.0), true), Codec.doubleRange((double)0.5, (double)10.0), (Object)1.0, value -> {});
    private final SimpleOption<Integer> mipmapLevels = new SimpleOption("options.mipmapLevels", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (value == 0) {
            return ScreenTexts.composeToggleText((Text)optionText, (boolean)false);
        }
        return GameOptions.getGenericValueText((Text)optionText, (int)value);
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(0, 4), (Object)4, value -> this.onChangeGraphicsOption());
    private static final Text MAX_ANISOTROPY_TOOLTIP = Text.translatable((String)"options.maxAnisotropy.tooltip");
    private final SimpleOption<Integer> maxAnisotropy = new SimpleOption("options.maxAnisotropy", SimpleOption.constantTooltip((Text)MAX_ANISOTROPY_TOOLTIP), (optionText, value) -> {
        if (value == 0) {
            return ScreenTexts.composeToggleText((Text)optionText, (boolean)false);
        }
        return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.multiplier", (Object[])new Object[]{Integer.toString(1 << value)}));
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(1, 3), (Object)2, value -> {
        this.onChangeGraphicsOption();
        GameOptions.refreshWorldRenderer(WorldRenderer::refreshTerrainSampler);
    });
    private static final Text TEXTURE_FILTERING_NONE_TOOLTIP = Text.translatable((String)"options.textureFiltering.none.tooltip");
    private static final Text TEXTURE_FILTERING_RGSS_TOOLTIP = Text.translatable((String)"options.textureFiltering.rgss.tooltip");
    private static final Text TEXTURE_FILTERING_ANISOTROPIC_TOOLTIP = Text.translatable((String)"options.textureFiltering.anisotropic.tooltip");
    private final SimpleOption<TextureFilteringMode> textureFiltering = new SimpleOption("options.textureFiltering", mode -> switch (5.field_64662[mode.ordinal()]) {
        default -> throw new MatchException(null, null);
        case 1 -> Tooltip.of((Text)TEXTURE_FILTERING_NONE_TOOLTIP);
        case 2 -> Tooltip.of((Text)TEXTURE_FILTERING_RGSS_TOOLTIP);
        case 3 -> Tooltip.of((Text)TEXTURE_FILTERING_ANISOTROPIC_TOOLTIP);
    }, (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(TextureFilteringMode.values()), TextureFilteringMode.CODEC), (Object)TextureFilteringMode.NONE, textureFilteringMode -> {
        this.onChangeGraphicsOption();
        GameOptions.refreshWorldRenderer(WorldRenderer::refreshTerrainSampler);
    });
    private boolean useNativeTransport = true;
    private final SimpleOption<AttackIndicator> attackIndicator = new SimpleOption("options.attackIndicator", SimpleOption.emptyTooltip(), (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(AttackIndicator.values()), AttackIndicator.CODEC), (Object)AttackIndicator.CROSSHAIR, value -> {});
    public TutorialStep tutorialStep = TutorialStep.MOVEMENT;
    public boolean joinedFirstServer = false;
    private final SimpleOption<Integer> biomeBlendRadius = new SimpleOption("options.biomeBlendRadius", SimpleOption.emptyTooltip(), (optionText, value) -> {
        int i = value * 2 + 1;
        return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)("options.biomeBlendRadius." + i)));
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(0, 7, false), (Object)2, value -> {
        GameOptions.refreshWorldRenderer(WorldRenderer::reload);
        this.onChangeGraphicsOption();
    });
    private final SimpleOption<Double> mouseWheelSensitivity = new SimpleOption("options.mouseWheelSensitivity", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.literal((String)String.format(Locale.ROOT, "%.2f", value))), (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(-200, 100).withModifier(GameOptions::toMouseWheelSensitivityValue, GameOptions::toMouseWheelSensitivitySliderProgressValue, false), Codec.doubleRange((double)GameOptions.toMouseWheelSensitivityValue((int)-200), (double)GameOptions.toMouseWheelSensitivityValue((int)100)), (Object)GameOptions.toMouseWheelSensitivityValue((int)0), value -> {});
    private final SimpleOption<Boolean> rawMouseInput = SimpleOption.ofBoolean((String)"options.rawMouseInput", (boolean)true, value -> {
        Window window = MinecraftClient.getInstance().getWindow();
        if (window != null) {
            window.setRawMouseMotion(value.booleanValue());
        }
    });
    private static final Text ALLOW_CURSOR_CHANGES_TOOLTIP = Text.translatable((String)"options.allowCursorChanges.tooltip");
    private final SimpleOption<Boolean> allowCursorChanges = SimpleOption.ofBoolean((String)"options.allowCursorChanges", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)ALLOW_CURSOR_CHANGES_TOOLTIP), (boolean)true, value -> {
        Window window = MinecraftClient.getInstance().getWindow();
        if (window != null) {
            window.setAllowCursorChanges(value.booleanValue());
        }
    });
    public int glDebugVerbosity = 1;
    private final SimpleOption<Boolean> autoJump = SimpleOption.ofBoolean((String)"options.autoJump", (boolean)false);
    private static final Text ROTATE_WITH_MINECART_TOOLTIP = Text.translatable((String)"options.rotateWithMinecart.tooltip");
    private final SimpleOption<Boolean> rotateWithMinecart = SimpleOption.ofBoolean((String)"options.rotateWithMinecart", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)ROTATE_WITH_MINECART_TOOLTIP), (boolean)false);
    private final SimpleOption<Boolean> operatorItemsTab = SimpleOption.ofBoolean((String)"options.operatorItemsTab", (boolean)false);
    private final SimpleOption<Boolean> autoSuggestions = SimpleOption.ofBoolean((String)"options.autoSuggestCommands", (boolean)true);
    private final SimpleOption<Boolean> chatColors = SimpleOption.ofBoolean((String)"options.chat.color", (boolean)true);
    private final SimpleOption<Boolean> chatLinks = SimpleOption.ofBoolean((String)"options.chat.links", (boolean)true);
    private final SimpleOption<Boolean> chatLinksPrompt = SimpleOption.ofBoolean((String)"options.chat.links.prompt", (boolean)true);
    private final SimpleOption<Boolean> enableVsync = SimpleOption.ofBoolean((String)"options.vsync", (boolean)true, value -> {
        if (MinecraftClient.getInstance().getWindow() != null) {
            MinecraftClient.getInstance().getWindow().setVsync(value.booleanValue());
        }
    });
    private final SimpleOption<Boolean> entityShadows = SimpleOption.ofBoolean((String)"options.entityShadows", (SimpleOption.TooltipFactory)SimpleOption.emptyTooltip(), (boolean)true, value -> this.onChangeGraphicsOption());
    private final SimpleOption<Boolean> forceUnicodeFont = SimpleOption.ofBoolean((String)"options.forceUnicodeFont", (boolean)false, value -> GameOptions.onFontOptionsChanged());
    private final SimpleOption<Boolean> japaneseGlyphVariants = SimpleOption.ofBoolean((String)"options.japaneseGlyphVariants", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)Text.translatable((String)"options.japaneseGlyphVariants.tooltip")), (boolean)GameOptions.shouldUseJapaneseGlyphsByDefault(), value -> GameOptions.onFontOptionsChanged());
    private final SimpleOption<Boolean> invertMouseX = SimpleOption.ofBoolean((String)"options.invertMouseX", (boolean)false);
    private final SimpleOption<Boolean> invertMouseY = SimpleOption.ofBoolean((String)"options.invertMouseY", (boolean)false);
    private final SimpleOption<Boolean> discreteMouseScroll = SimpleOption.ofBoolean((String)"options.discrete_mouse_scroll", (boolean)false);
    private static final Text REALMS_NOTIFICATIONS_TOOLTIP = Text.translatable((String)"options.realmsNotifications.tooltip");
    private final SimpleOption<Boolean> realmsNotifications = SimpleOption.ofBoolean((String)"options.realmsNotifications", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)REALMS_NOTIFICATIONS_TOOLTIP), (boolean)true);
    private static final Text ALLOW_SERVER_LISTING_TOOLTIP = Text.translatable((String)"options.allowServerListing.tooltip");
    private final SimpleOption<Boolean> allowServerListing = SimpleOption.ofBoolean((String)"options.allowServerListing", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)ALLOW_SERVER_LISTING_TOOLTIP), (boolean)true, value -> {});
    private final SimpleOption<Boolean> reducedDebugInfo = SimpleOption.ofBoolean((String)"options.reducedDebugInfo", (SimpleOption.TooltipFactory)SimpleOption.emptyTooltip(), (boolean)false, value -> MinecraftClient.getInstance().debugHudEntryList.updateVisibleEntries());
    private final Map<SoundCategory, SimpleOption<Double>> soundVolumeLevels = Util.mapEnum(SoundCategory.class, category -> this.createSoundVolumeOption("soundCategory." + category.getName(), category));
    private static final Text SHOW_SUBTITLES_TOOLTIP = Text.translatable((String)"options.showSubtitles.tooltip");
    private final SimpleOption<Boolean> showSubtitles = SimpleOption.ofBoolean((String)"options.showSubtitles", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)SHOW_SUBTITLES_TOOLTIP), (boolean)false);
    private static final Text DIRECTIONAL_AUDIO_ON_TOOLTIP = Text.translatable((String)"options.directionalAudio.on.tooltip");
    private static final Text DIRECTIONAL_AUDIO_OFF_TOOLTIP = Text.translatable((String)"options.directionalAudio.off.tooltip");
    private final SimpleOption<Boolean> directionalAudio = SimpleOption.ofBoolean((String)"options.directionalAudio", value -> value != false ? Tooltip.of((Text)DIRECTIONAL_AUDIO_ON_TOOLTIP) : Tooltip.of((Text)DIRECTIONAL_AUDIO_OFF_TOOLTIP), (boolean)false, value -> {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        soundManager.reloadSounds();
        soundManager.play((SoundInstance)PositionedSoundInstance.ui((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
    });
    private final SimpleOption<Boolean> backgroundForChatOnly = new SimpleOption("options.accessibility.text_background", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? Text.translatable((String)"options.accessibility.text_background.chat") : Text.translatable((String)"options.accessibility.text_background.everywhere"), (SimpleOption.Callbacks)SimpleOption.BOOLEAN, (Object)true, value -> {});
    private final SimpleOption<Boolean> touchscreen = SimpleOption.ofBoolean((String)"options.touchscreen", (boolean)false);
    private final SimpleOption<Boolean> fullscreen = SimpleOption.ofBoolean((String)"options.fullscreen", (boolean)false, value -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getWindow() != null && minecraftClient.getWindow().isFullscreen() != value.booleanValue()) {
            minecraftClient.getWindow().toggleFullscreen();
            this.getFullscreen().setValue((Object)minecraftClient.getWindow().isFullscreen());
        }
    });
    private final SimpleOption<Boolean> bobView = SimpleOption.ofBoolean((String)"options.viewBobbing", (boolean)true);
    private static final Text TOGGLE_KEY_TEXT = Text.translatable((String)"options.key.toggle");
    private static final Text HOLD_KEY_TEXT = Text.translatable((String)"options.key.hold");
    private final SimpleOption<Boolean> sneakToggled = new SimpleOption("key.sneak", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, (SimpleOption.Callbacks)SimpleOption.BOOLEAN, (Object)false, value -> {});
    private final SimpleOption<Boolean> sprintToggled = new SimpleOption("key.sprint", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, (SimpleOption.Callbacks)SimpleOption.BOOLEAN, (Object)false, value -> {});
    private final SimpleOption<Boolean> attackToggled = new SimpleOption("key.attack", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, (SimpleOption.Callbacks)SimpleOption.BOOLEAN, (Object)false, value -> {});
    private final SimpleOption<Boolean> useToggled = new SimpleOption("key.use", SimpleOption.emptyTooltip(), (optionText, value) -> value != false ? TOGGLE_KEY_TEXT : HOLD_KEY_TEXT, (SimpleOption.Callbacks)SimpleOption.BOOLEAN, (Object)false, value -> {});
    private static final Text SPRINT_WINDOW_TOOLTIP = Text.translatable((String)"options.sprintWindow.tooltip");
    private final SimpleOption<Integer> sprintWindow = new SimpleOption("options.sprintWindow", SimpleOption.constantTooltip((Text)SPRINT_WINDOW_TOOLTIP), (optionText, value) -> {
        if (value == 0) {
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.off"));
        }
        return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.value", (Object[])new Object[]{value}));
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(0, 10), (Object)7, value -> {});
    public boolean skipMultiplayerWarning;
    private static final Text HIDE_MATCHED_NAMES_TOOLTIP = Text.translatable((String)"options.hideMatchedNames.tooltip");
    private final SimpleOption<Boolean> hideMatchedNames = SimpleOption.ofBoolean((String)"options.hideMatchedNames", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)HIDE_MATCHED_NAMES_TOOLTIP), (boolean)true);
    private final SimpleOption<Boolean> showAutosaveIndicator = SimpleOption.ofBoolean((String)"options.autosaveIndicator", (boolean)true);
    private static final Text ONLY_SHOW_SECURE_CHAT_TOOLTIP = Text.translatable((String)"options.onlyShowSecureChat.tooltip");
    private final SimpleOption<Boolean> onlyShowSecureChat = SimpleOption.ofBoolean((String)"options.onlyShowSecureChat", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)ONLY_SHOW_SECURE_CHAT_TOOLTIP), (boolean)false);
    private static final Text CHAT_DRAFTS_TOOLTIP = Text.translatable((String)"options.chat.drafts.tooltip");
    private final SimpleOption<Boolean> chatDrafts = SimpleOption.ofBoolean((String)"options.chat.drafts", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)CHAT_DRAFTS_TOOLTIP), (boolean)false);
    public final KeyBinding forwardKey = new KeyBinding("key.forward", 87, KeyBinding.Category.MOVEMENT);
    public final KeyBinding leftKey = new KeyBinding("key.left", 65, KeyBinding.Category.MOVEMENT);
    public final KeyBinding backKey = new KeyBinding("key.back", 83, KeyBinding.Category.MOVEMENT);
    public final KeyBinding rightKey = new KeyBinding("key.right", 68, KeyBinding.Category.MOVEMENT);
    public final KeyBinding jumpKey = new KeyBinding("key.jump", 32, KeyBinding.Category.MOVEMENT);
    public final KeyBinding sneakKey = new StickyKeyBinding("key.sneak", 340, KeyBinding.Category.MOVEMENT, () -> ((SimpleOption)this.sneakToggled).getValue(), true);
    public final KeyBinding sprintKey = new StickyKeyBinding("key.sprint", 341, KeyBinding.Category.MOVEMENT, () -> ((SimpleOption)this.sprintToggled).getValue(), true);
    public final KeyBinding inventoryKey = new KeyBinding("key.inventory", 69, KeyBinding.Category.INVENTORY);
    public final KeyBinding swapHandsKey = new KeyBinding("key.swapOffhand", 70, KeyBinding.Category.INVENTORY);
    public final KeyBinding dropKey = new KeyBinding("key.drop", 81, KeyBinding.Category.INVENTORY);
    public final KeyBinding useKey = new StickyKeyBinding("key.use", InputUtil.Type.MOUSE, 1, KeyBinding.Category.GAMEPLAY, () -> ((SimpleOption)this.useToggled).getValue(), false);
    public final KeyBinding attackKey = new StickyKeyBinding("key.attack", InputUtil.Type.MOUSE, 0, KeyBinding.Category.GAMEPLAY, () -> ((SimpleOption)this.attackToggled).getValue(), true);
    public final KeyBinding pickItemKey = new KeyBinding("key.pickItem", InputUtil.Type.MOUSE, 2, KeyBinding.Category.GAMEPLAY);
    public final KeyBinding chatKey = new KeyBinding("key.chat", 84, KeyBinding.Category.MULTIPLAYER);
    public final KeyBinding playerListKey = new KeyBinding("key.playerlist", 258, KeyBinding.Category.MULTIPLAYER);
    public final KeyBinding commandKey = new KeyBinding("key.command", 47, KeyBinding.Category.MULTIPLAYER);
    public final KeyBinding socialInteractionsKey = new KeyBinding("key.socialInteractions", 80, KeyBinding.Category.MULTIPLAYER);
    public final KeyBinding screenshotKey = new KeyBinding("key.screenshot", 291, KeyBinding.Category.MISC);
    public final KeyBinding togglePerspectiveKey = new KeyBinding("key.togglePerspective", 294, KeyBinding.Category.MISC);
    public final KeyBinding smoothCameraKey = new KeyBinding("key.smoothCamera", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.Category.MISC);
    public final KeyBinding fullscreenKey = new KeyBinding("key.fullscreen", 300, KeyBinding.Category.MISC);
    public final KeyBinding advancementsKey = new KeyBinding("key.advancements", 76, KeyBinding.Category.MISC);
    public final KeyBinding quickActionsKey = new KeyBinding("key.quickActions", 71, KeyBinding.Category.MISC);
    public final KeyBinding toggleGuiKey = new KeyBinding("key.toggleGui", 290, KeyBinding.Category.MISC);
    public final KeyBinding toggleSpectatorShaderEffectsKey = new KeyBinding("key.toggleSpectatorShaderEffects", 293, KeyBinding.Category.MISC);
    public final KeyBinding[] hotbarKeys = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.2", 50, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.3", 51, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.4", 52, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.5", 53, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.6", 54, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.7", 55, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.8", 56, KeyBinding.Category.INVENTORY), new KeyBinding("key.hotbar.9", 57, KeyBinding.Category.INVENTORY)};
    public final KeyBinding saveToolbarActivatorKey = new KeyBinding("key.saveToolbarActivator", 67, KeyBinding.Category.CREATIVE);
    public final KeyBinding loadToolbarActivatorKey = new KeyBinding("key.loadToolbarActivator", 88, KeyBinding.Category.CREATIVE);
    public final KeyBinding spectatorOutlinesKey = new KeyBinding("key.spectatorOutlines", InputUtil.UNKNOWN_KEY.getCode(), KeyBinding.Category.SPECTATOR);
    public final KeyBinding spectatorHotbarKey = new KeyBinding("key.spectatorHotbar", InputUtil.Type.MOUSE, 2, KeyBinding.Category.SPECTATOR);
    public final KeyBinding debugOverlayKey = new KeyBinding("key.debug.overlay", InputUtil.Type.KEYSYM, 292, KeyBinding.Category.DEBUG, -2);
    public final KeyBinding debugModifierKey = new KeyBinding("key.debug.modifier", InputUtil.Type.KEYSYM, 292, KeyBinding.Category.DEBUG, -1);
    public final KeyBinding debugCrashKey = new KeyBinding("key.debug.crash", InputUtil.Type.KEYSYM, 67, KeyBinding.Category.DEBUG);
    public final KeyBinding debugReloadChunkKey = new KeyBinding("key.debug.reloadChunk", InputUtil.Type.KEYSYM, 65, KeyBinding.Category.DEBUG);
    public final KeyBinding debugShowHitboxesKey = new KeyBinding("key.debug.showHitboxes", InputUtil.Type.KEYSYM, 66, KeyBinding.Category.DEBUG);
    public final KeyBinding debugClearChatKey = new KeyBinding("key.debug.clearChat", InputUtil.Type.KEYSYM, 68, KeyBinding.Category.DEBUG);
    public final KeyBinding debugShowChunkBordersKey = new KeyBinding("key.debug.showChunkBorders", InputUtil.Type.KEYSYM, 71, KeyBinding.Category.DEBUG);
    public final KeyBinding debugShowAdvancedTooltipsKey = new KeyBinding("key.debug.showAdvancedTooltips", InputUtil.Type.KEYSYM, 72, KeyBinding.Category.DEBUG);
    public final KeyBinding debugCopyRecreateCommandKey = new KeyBinding("key.debug.copyRecreateCommand", InputUtil.Type.KEYSYM, 73, KeyBinding.Category.DEBUG);
    public final KeyBinding debugSpectateKey = new KeyBinding("key.debug.spectate", InputUtil.Type.KEYSYM, 78, KeyBinding.Category.DEBUG);
    public final KeyBinding debugSwitchGameModeKey = new KeyBinding("key.debug.switchGameMode", InputUtil.Type.KEYSYM, 293, KeyBinding.Category.DEBUG);
    public final KeyBinding debugOptionsKey = new KeyBinding("key.debug.debugOptions", InputUtil.Type.KEYSYM, 295, KeyBinding.Category.DEBUG);
    public final KeyBinding debugFocusPauseKey = new KeyBinding("key.debug.focusPause", InputUtil.Type.KEYSYM, 80, KeyBinding.Category.DEBUG);
    public final KeyBinding debugDumpDynamicTexturesKey = new KeyBinding("key.debug.dumpDynamicTextures", InputUtil.Type.KEYSYM, 83, KeyBinding.Category.DEBUG);
    public final KeyBinding debugReloadResourcePacksKey = new KeyBinding("key.debug.reloadResourcePacks", InputUtil.Type.KEYSYM, 84, KeyBinding.Category.DEBUG);
    public final KeyBinding debugProfilingKey = new KeyBinding("key.debug.profiling", InputUtil.Type.KEYSYM, 76, KeyBinding.Category.DEBUG);
    public final KeyBinding debugCopyLocationKey = new KeyBinding("key.debug.copyLocation", InputUtil.Type.KEYSYM, 67, KeyBinding.Category.DEBUG);
    public final KeyBinding debugDumpVersionKey = new KeyBinding("key.debug.dumpVersion", InputUtil.Type.KEYSYM, 86, KeyBinding.Category.DEBUG);
    public final KeyBinding debugProfilingChartKey = new KeyBinding("key.debug.profilingChart", InputUtil.Type.KEYSYM, 49, KeyBinding.Category.DEBUG, 1);
    public final KeyBinding debugFpsChartsKey = new KeyBinding("key.debug.fpsCharts", InputUtil.Type.KEYSYM, 50, KeyBinding.Category.DEBUG, 2);
    public final KeyBinding debugNetworkChartsKey = new KeyBinding("key.debug.networkCharts", InputUtil.Type.KEYSYM, 51, KeyBinding.Category.DEBUG, 3);
    public final KeyBinding[] debugKeys = new KeyBinding[]{this.debugReloadChunkKey, this.debugShowHitboxesKey, this.debugClearChatKey, this.debugCrashKey, this.debugShowChunkBordersKey, this.debugShowAdvancedTooltipsKey, this.debugCopyRecreateCommandKey, this.debugSpectateKey, this.debugSwitchGameModeKey, this.debugOptionsKey, this.debugFocusPauseKey, this.debugDumpDynamicTexturesKey, this.debugReloadResourcePacksKey, this.debugProfilingKey, this.debugCopyLocationKey, this.debugDumpVersionKey, this.debugProfilingChartKey, this.debugFpsChartsKey, this.debugNetworkChartsKey};
    public final KeyBinding[] allKeys = (KeyBinding[])Stream.of({this.attackKey, this.useKey, this.forwardKey, this.leftKey, this.backKey, this.rightKey, this.jumpKey, this.sneakKey, this.sprintKey, this.dropKey, this.inventoryKey, this.chatKey, this.playerListKey, this.pickItemKey, this.commandKey, this.socialInteractionsKey, this.toggleGuiKey, this.toggleSpectatorShaderEffectsKey, this.screenshotKey, this.togglePerspectiveKey, this.smoothCameraKey, this.fullscreenKey, this.spectatorOutlinesKey, this.spectatorHotbarKey, this.swapHandsKey, this.saveToolbarActivatorKey, this.loadToolbarActivatorKey, this.advancementsKey, this.quickActionsKey, this.debugOverlayKey, this.debugModifierKey}, this.hotbarKeys, this.debugKeys).flatMap(Stream::of).toArray(KeyBinding[]::new);
    protected MinecraftClient client;
    private final File optionsFile;
    public boolean hudHidden;
    private Perspective perspective = Perspective.FIRST_PERSON;
    public String lastServer = "";
    public boolean smoothCameraEnabled;
    private final SimpleOption<Integer> fov = new SimpleOption("options.fov", SimpleOption.emptyTooltip(), (optionText, value) -> switch (value) {
        case 70 -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.fov.min"));
        case 110 -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.fov.max"));
        default -> GameOptions.getGenericValueText((Text)optionText, (int)value);
    }, (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(30, 110), Codec.DOUBLE.xmap(value -> (int)(value * 40.0 + 70.0), value -> ((double)value.intValue() - 70.0) / 40.0), (Object)70, value -> GameOptions.refreshWorldRenderer(WorldRenderer::scheduleTerrainUpdate));
    private static final Text TELEMETRY_TOOLTIP = Text.translatable((String)"options.telemetry.button.tooltip", (Object[])new Object[]{Text.translatable((String)"options.telemetry.state.minimal"), Text.translatable((String)"options.telemetry.state.all")});
    private final SimpleOption<Boolean> telemetryOptInExtra = SimpleOption.ofBoolean((String)"options.telemetry.button", (SimpleOption.TooltipFactory)SimpleOption.constantTooltip((Text)TELEMETRY_TOOLTIP), (optionText, value) -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (!minecraftClient.isTelemetryEnabledByApi()) {
            return Text.translatable((String)"options.telemetry.state.none");
        }
        if (value.booleanValue() && minecraftClient.isOptionalTelemetryEnabledByApi()) {
            return Text.translatable((String)"options.telemetry.state.all");
        }
        return Text.translatable((String)"options.telemetry.state.minimal");
    }, (boolean)false, value -> {});
    private static final Text SCREEN_EFFECT_SCALE_TOOLTIP = Text.translatable((String)"options.screenEffectScale.tooltip");
    private final SimpleOption<Double> distortionEffectScale = new SimpleOption("options.screenEffectScale", SimpleOption.constantTooltip((Text)SCREEN_EFFECT_SCALE_TOOLTIP), GameOptions::getPercentValueOrOffText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, value -> {});
    private static final Text FOV_EFFECT_SCALE_TOOLTIP = Text.translatable((String)"options.fovEffectScale.tooltip");
    private final SimpleOption<Double> fovEffectScale = new SimpleOption("options.fovEffectScale", SimpleOption.constantTooltip((Text)FOV_EFFECT_SCALE_TOOLTIP), GameOptions::getPercentValueOrOffText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(MathHelper::square, Math::sqrt), Codec.doubleRange((double)0.0, (double)1.0), (Object)1.0, value -> {});
    private static final Text DARKNESS_EFFECT_SCALE_TOOLTIP = Text.translatable((String)"options.darknessEffectScale.tooltip");
    private final SimpleOption<Double> darknessEffectScale = new SimpleOption("options.darknessEffectScale", SimpleOption.constantTooltip((Text)DARKNESS_EFFECT_SCALE_TOOLTIP), GameOptions::getPercentValueOrOffText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE.withModifier(MathHelper::square, Math::sqrt), (Object)1.0, value -> {});
    private static final Text GLINT_SPEED_TOOLTIP = Text.translatable((String)"options.glintSpeed.tooltip");
    private final SimpleOption<Double> glintSpeed = new SimpleOption("options.glintSpeed", SimpleOption.constantTooltip((Text)GLINT_SPEED_TOOLTIP), GameOptions::getPercentValueOrOffText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)0.5, value -> {});
    private static final Text GLINT_STRENGTH_TOOLTIP = Text.translatable((String)"options.glintStrength.tooltip");
    private final SimpleOption<Double> glintStrength = new SimpleOption("options.glintStrength", SimpleOption.constantTooltip((Text)GLINT_STRENGTH_TOOLTIP), GameOptions::getPercentValueOrOffText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)0.75, value -> {});
    private static final Text DAMAGE_TILT_STRENGTH_TOOLTIP = Text.translatable((String)"options.damageTiltStrength.tooltip");
    private final SimpleOption<Double> damageTiltStrength = new SimpleOption("options.damageTiltStrength", SimpleOption.constantTooltip((Text)DAMAGE_TILT_STRENGTH_TOOLTIP), GameOptions::getPercentValueOrOffText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, double_ -> {});
    private final SimpleOption<Double> gamma = new SimpleOption("options.gamma", SimpleOption.emptyTooltip(), (optionText, value) -> {
        int i = (int)(value * 100.0);
        if (i == 0) {
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.gamma.min"));
        }
        if (i == 50) {
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.gamma.default"));
        }
        if (i == 100) {
            return GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.gamma.max"));
        }
        return GameOptions.getGenericValueText((Text)optionText, (int)i);
    }, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)0.5, value -> {});
    public static final int GUI_SCALE_AUTO = 0;
    private static final int MAX_SERIALIZABLE_GUI_SCALE = 0x7FFFFFFE;
    private final SimpleOption<Integer> guiScale = new SimpleOption("options.guiScale", SimpleOption.emptyTooltip(), (optionText, value) -> value == 0 ? Text.translatable((String)"options.guiScale.auto") : Text.literal((String)Integer.toString(value)), (SimpleOption.Callbacks)new SimpleOption.MaxSuppliableIntCallbacks(0, () -> {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (!minecraftClient.isRunning()) {
            return 0x7FFFFFFE;
        }
        return minecraftClient.getWindow().calculateScaleFactor(0, minecraftClient.forcesUnicodeFont());
    }, 0x7FFFFFFE), (Object)0, value -> this.client.onResolutionChanged());
    private final SimpleOption<ParticlesMode> particles = new SimpleOption("options.particles", SimpleOption.emptyTooltip(), (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(ParticlesMode.values()), ParticlesMode.CODEC), (Object)ParticlesMode.ALL, value -> this.onChangeGraphicsOption());
    private final SimpleOption<NarratorMode> narrator = new SimpleOption("options.narrator", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if (this.client.getNarratorManager().isActive()) {
            return value.getName();
        }
        return Text.translatable((String)"options.narrator.notavailable");
    }, (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(NarratorMode.values()), NarratorMode.CODEC), (Object)NarratorMode.OFF, value -> this.client.getNarratorManager().onModeChange(value));
    public String language = "en_us";
    private final SimpleOption<String> soundDevice = new SimpleOption("options.audioDevice", SimpleOption.emptyTooltip(), (optionText, value) -> {
        if ("".equals(value)) {
            return Text.translatable((String)"options.audioDevice.default");
        }
        if (value.startsWith("OpenAL Soft on ")) {
            return Text.literal((String)value.substring(SoundSystem.OPENAL_SOFT_ON_LENGTH));
        }
        return Text.literal((String)value);
    }, (SimpleOption.Callbacks)new SimpleOption.LazyCyclingCallbacks(() -> Stream.concat(Stream.of(""), MinecraftClient.getInstance().getSoundManager().getSoundDevices().stream()).toList(), value -> {
        if (!MinecraftClient.getInstance().isRunning() || value == "" || MinecraftClient.getInstance().getSoundManager().getSoundDevices().contains(value)) {
            return Optional.of(value);
        }
        return Optional.empty();
    }, (Codec)Codec.STRING), (Object)"", value -> {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        soundManager.reloadSounds();
        soundManager.play((SoundInstance)PositionedSoundInstance.ui((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
    });
    public boolean onboardAccessibility = true;
    private static final Text MUSIC_FREQUENCY_TOOLTIP = Text.translatable((String)"options.music_frequency.tooltip");
    private final SimpleOption<MusicTracker.MusicFrequency> musicFrequency = new SimpleOption("options.music_frequency", SimpleOption.constantTooltip((Text)MUSIC_FREQUENCY_TOOLTIP), (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(MusicTracker.MusicFrequency.values()), MusicTracker.MusicFrequency.CODEC), (Object)MusicTracker.MusicFrequency.DEFAULT, value -> MinecraftClient.getInstance().getMusicTracker().setMusicFrequency(value));
    private final SimpleOption<MusicToastMode> musicToast = new SimpleOption("options.musicToast", value -> Tooltip.of((Text)value.getTooltipText()), (optionText, value) -> value.getText(), (SimpleOption.Callbacks)new SimpleOption.PotentialValuesBasedCallbacks(Arrays.asList(MusicToastMode.values()), MusicToastMode.CODEC), (Object)MusicToastMode.NEVER, value -> this.client.getToastManager().onMusicToastModeUpdated(value));
    public boolean syncChunkWrites;
    public boolean startedCleanly = true;

    private static void refreshWorldRenderer(Consumer<WorldRenderer> refresher) {
        WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
        if (worldRenderer != null) {
            refresher.accept(worldRenderer);
        }
    }

    public SimpleOption<Boolean> getMonochromeLogo() {
        return this.monochromeLogo;
    }

    public SimpleOption<Boolean> getHideLightningFlashes() {
        return this.hideLightningFlashes;
    }

    public SimpleOption<Boolean> getHideSplashTexts() {
        return this.hideSplashTexts;
    }

    public SimpleOption<Double> getMouseSensitivity() {
        return this.mouseSensitivity;
    }

    public SimpleOption<Integer> getViewDistance() {
        return this.viewDistance;
    }

    public SimpleOption<Integer> getSimulationDistance() {
        return this.simulationDistance;
    }

    public SimpleOption<Double> getEntityDistanceScaling() {
        return this.entityDistanceScaling;
    }

    public SimpleOption<Integer> getMaxFps() {
        return this.maxFps;
    }

    public void applyGraphicsMode(GraphicsMode mode) {
        this.applyingGraphicsMode = true;
        mode.apply(this.client);
        this.applyingGraphicsMode = false;
    }

    public SimpleOption<GraphicsMode> getPreset() {
        return this.preset;
    }

    public SimpleOption<InactivityFpsLimit> getInactivityFpsLimit() {
        return this.inactivityFpsLimit;
    }

    public SimpleOption<CloudRenderMode> getCloudRenderMode() {
        return this.cloudRenderMode;
    }

    public SimpleOption<Integer> getCloudRenderDistance() {
        return this.cloudRenderDistance;
    }

    public SimpleOption<Integer> getWeatherRadius() {
        return this.weatherRadius;
    }

    public SimpleOption<Boolean> getCutoutLeaves() {
        return this.cutoutLeaves;
    }

    public SimpleOption<Boolean> getVignette() {
        return this.vignette;
    }

    public SimpleOption<Boolean> getImprovedTransparency() {
        return this.improvedTransparency;
    }

    public SimpleOption<Boolean> getAo() {
        return this.ao;
    }

    public SimpleOption<Double> getChunkFade() {
        return this.chunkFade;
    }

    public SimpleOption<ChunkBuilderMode> getChunkBuilderMode() {
        return this.chunkBuilderMode;
    }

    public void refreshResourcePacks(ResourcePackManager resourcePackManager) {
        ImmutableList list = ImmutableList.copyOf((Collection)this.resourcePacks);
        this.resourcePacks.clear();
        this.incompatibleResourcePacks.clear();
        for (ResourcePackProfile resourcePackProfile : resourcePackManager.getEnabledProfiles()) {
            if (resourcePackProfile.isPinned()) continue;
            this.resourcePacks.add(resourcePackProfile.getId());
            if (resourcePackProfile.getCompatibility().isCompatible()) continue;
            this.incompatibleResourcePacks.add(resourcePackProfile.getId());
        }
        this.write();
        ImmutableList list2 = ImmutableList.copyOf((Collection)this.resourcePacks);
        if (!list2.equals(list)) {
            this.client.reloadResources();
        }
    }

    public SimpleOption<ChatVisibility> getChatVisibility() {
        return this.chatVisibility;
    }

    public SimpleOption<Double> getChatOpacity() {
        return this.chatOpacity;
    }

    public SimpleOption<Double> getChatLineSpacing() {
        return this.chatLineSpacing;
    }

    public SimpleOption<Integer> getMenuBackgroundBlurriness() {
        return this.menuBackgroundBlurriness;
    }

    public int getMenuBackgroundBlurrinessValue() {
        return (Integer)this.getMenuBackgroundBlurriness().getValue();
    }

    public SimpleOption<Double> getTextBackgroundOpacity() {
        return this.textBackgroundOpacity;
    }

    public SimpleOption<Double> getPanoramaSpeed() {
        return this.panoramaSpeed;
    }

    public SimpleOption<Boolean> getHighContrast() {
        return this.highContrast;
    }

    public SimpleOption<Boolean> getHighContrastBlockOutline() {
        return this.highContrastBlockOutline;
    }

    public SimpleOption<Boolean> getNarratorHotkey() {
        return this.narratorHotkey;
    }

    public SimpleOption<Arm> getMainArm() {
        return this.mainArm;
    }

    public SimpleOption<Double> getChatScale() {
        return this.chatScale;
    }

    public SimpleOption<Double> getChatWidth() {
        return this.chatWidth;
    }

    public SimpleOption<Double> getChatHeightUnfocused() {
        return this.chatHeightUnfocused;
    }

    public SimpleOption<Double> getChatHeightFocused() {
        return this.chatHeightFocused;
    }

    public SimpleOption<Double> getChatDelay() {
        return this.chatDelay;
    }

    public SimpleOption<Double> getNotificationDisplayTime() {
        return this.notificationDisplayTime;
    }

    public SimpleOption<Integer> getMipmapLevels() {
        return this.mipmapLevels;
    }

    public SimpleOption<Integer> getMaxAnisotropy() {
        return this.maxAnisotropy;
    }

    public int getEffectiveAnisotropy() {
        return Math.min(1 << (Integer)this.maxAnisotropy.getValue(), RenderSystem.getDevice().getMaxSupportedAnisotropy());
    }

    public SimpleOption<TextureFilteringMode> getTextureFiltering() {
        return this.textureFiltering;
    }

    public SimpleOption<AttackIndicator> getAttackIndicator() {
        return this.attackIndicator;
    }

    public SimpleOption<Integer> getBiomeBlendRadius() {
        return this.biomeBlendRadius;
    }

    private static double toMouseWheelSensitivityValue(int value) {
        return Math.pow(10.0, (double)value / 100.0);
    }

    private static int toMouseWheelSensitivitySliderProgressValue(double value) {
        return MathHelper.floor((double)(Math.log10(value) * 100.0));
    }

    public SimpleOption<Double> getMouseWheelSensitivity() {
        return this.mouseWheelSensitivity;
    }

    public SimpleOption<Boolean> getRawMouseInput() {
        return this.rawMouseInput;
    }

    public SimpleOption<Boolean> getAllowCursorChanges() {
        return this.allowCursorChanges;
    }

    public SimpleOption<Boolean> getAutoJump() {
        return this.autoJump;
    }

    public SimpleOption<Boolean> getRotateWithMinecart() {
        return this.rotateWithMinecart;
    }

    public SimpleOption<Boolean> getOperatorItemsTab() {
        return this.operatorItemsTab;
    }

    public SimpleOption<Boolean> getAutoSuggestions() {
        return this.autoSuggestions;
    }

    public SimpleOption<Boolean> getChatColors() {
        return this.chatColors;
    }

    public SimpleOption<Boolean> getChatLinks() {
        return this.chatLinks;
    }

    public SimpleOption<Boolean> getChatLinksPrompt() {
        return this.chatLinksPrompt;
    }

    public SimpleOption<Boolean> getEnableVsync() {
        return this.enableVsync;
    }

    public SimpleOption<Boolean> getEntityShadows() {
        return this.entityShadows;
    }

    private static void onFontOptionsChanged() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getWindow() != null) {
            minecraftClient.onFontOptionsChanged();
            minecraftClient.onResolutionChanged();
        }
    }

    public SimpleOption<Boolean> getForceUnicodeFont() {
        return this.forceUnicodeFont;
    }

    private static boolean shouldUseJapaneseGlyphsByDefault() {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("ja");
    }

    public SimpleOption<Boolean> getJapaneseGlyphVariants() {
        return this.japaneseGlyphVariants;
    }

    public SimpleOption<Boolean> getInvertMouseX() {
        return this.invertMouseX;
    }

    public SimpleOption<Boolean> getInvertMouseY() {
        return this.invertMouseY;
    }

    public SimpleOption<Boolean> getDiscreteMouseScroll() {
        return this.discreteMouseScroll;
    }

    public SimpleOption<Boolean> getRealmsNotifications() {
        return this.realmsNotifications;
    }

    public SimpleOption<Boolean> getAllowServerListing() {
        return this.allowServerListing;
    }

    public SimpleOption<Boolean> getReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public final float getSoundVolume(SoundCategory category) {
        if (category == SoundCategory.MASTER) {
            return this.getCategorySoundVolume(category);
        }
        return this.getCategorySoundVolume(category) * this.getCategorySoundVolume(SoundCategory.MASTER);
    }

    public final float getCategorySoundVolume(SoundCategory category) {
        return ((Double)this.getSoundVolumeOption(category).getValue()).floatValue();
    }

    public final SimpleOption<Double> getSoundVolumeOption(SoundCategory category) {
        return Objects.requireNonNull((SimpleOption)this.soundVolumeLevels.get(category));
    }

    private SimpleOption<Double> createSoundVolumeOption(String key, SoundCategory category) {
        return new SimpleOption(key, SimpleOption.emptyTooltip(), GameOptions::getPercentValueOrOffText, (SimpleOption.Callbacks)SimpleOption.DoubleSliderCallbacks.INSTANCE, (Object)1.0, volume -> {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            SoundManager soundManager = minecraftClient.getSoundManager();
            if ((category == SoundCategory.MASTER || category == SoundCategory.MUSIC) && this.getSoundVolume(SoundCategory.MUSIC) > 0.0f) {
                minecraftClient.getMusicTracker().tryShowToast();
            }
            soundManager.refreshSoundVolumes(category);
            if (minecraftClient.world == null) {
                SoundPreviewer.preview((SoundManager)soundManager, (SoundCategory)category, (float)volume.floatValue());
            }
        });
    }

    public SimpleOption<Boolean> getShowSubtitles() {
        return this.showSubtitles;
    }

    public SimpleOption<Boolean> getDirectionalAudio() {
        return this.directionalAudio;
    }

    public SimpleOption<Boolean> getBackgroundForChatOnly() {
        return this.backgroundForChatOnly;
    }

    public SimpleOption<Boolean> getTouchscreen() {
        return this.touchscreen;
    }

    public SimpleOption<Boolean> getFullscreen() {
        return this.fullscreen;
    }

    public SimpleOption<Boolean> getBobView() {
        return this.bobView;
    }

    public SimpleOption<Boolean> getSneakToggled() {
        return this.sneakToggled;
    }

    public SimpleOption<Boolean> getSprintToggled() {
        return this.sprintToggled;
    }

    public SimpleOption<Boolean> getAttackToggled() {
        return this.attackToggled;
    }

    public SimpleOption<Boolean> getUseToggled() {
        return this.useToggled;
    }

    public SimpleOption<Integer> getSprintWindow() {
        return this.sprintWindow;
    }

    public SimpleOption<Boolean> getHideMatchedNames() {
        return this.hideMatchedNames;
    }

    public SimpleOption<Boolean> getShowAutosaveIndicator() {
        return this.showAutosaveIndicator;
    }

    public SimpleOption<Boolean> getOnlyShowSecureChat() {
        return this.onlyShowSecureChat;
    }

    public SimpleOption<Boolean> getChatDrafts() {
        return this.chatDrafts;
    }

    private void onChangeGraphicsOption() {
        if (this.applyingGraphicsMode) {
            return;
        }
        this.preset.setValue((Object)GraphicsMode.CUSTOM);
        Screen screen = this.client.currentScreen;
        if (screen instanceof GameOptionsScreen) {
            GameOptionsScreen gameOptionsScreen = (GameOptionsScreen)screen;
            gameOptionsScreen.update(this.preset);
        }
    }

    public SimpleOption<Integer> getFov() {
        return this.fov;
    }

    public SimpleOption<Boolean> getTelemetryOptInExtra() {
        return this.telemetryOptInExtra;
    }

    public SimpleOption<Double> getDistortionEffectScale() {
        return this.distortionEffectScale;
    }

    public SimpleOption<Double> getFovEffectScale() {
        return this.fovEffectScale;
    }

    public SimpleOption<Double> getDarknessEffectScale() {
        return this.darknessEffectScale;
    }

    public SimpleOption<Double> getGlintSpeed() {
        return this.glintSpeed;
    }

    public SimpleOption<Double> getGlintStrength() {
        return this.glintStrength;
    }

    public SimpleOption<Double> getDamageTiltStrength() {
        return this.damageTiltStrength;
    }

    public SimpleOption<Double> getGamma() {
        return this.gamma;
    }

    public SimpleOption<Integer> getGuiScale() {
        return this.guiScale;
    }

    public SimpleOption<ParticlesMode> getParticles() {
        return this.particles;
    }

    public SimpleOption<NarratorMode> getNarrator() {
        return this.narrator;
    }

    public SimpleOption<String> getSoundDevice() {
        return this.soundDevice;
    }

    public void setAccessibilityOnboarded() {
        this.onboardAccessibility = false;
        this.write();
    }

    public SimpleOption<MusicTracker.MusicFrequency> getMusicFrequency() {
        return this.musicFrequency;
    }

    public SimpleOption<MusicToastMode> getMusicToast() {
        return this.musicToast;
    }

    public GameOptions(MinecraftClient client, File optionsFile) {
        this.client = client;
        this.optionsFile = new File(optionsFile, "options.txt");
        boolean bl = Runtime.getRuntime().maxMemory() >= 1000000000L;
        this.viewDistance = new SimpleOption("options.renderDistance", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.chunks", (Object[])new Object[]{value})), (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(2, bl ? 32 : 16, false), (Object)12, value -> {
            GameOptions.refreshWorldRenderer(WorldRenderer::scheduleTerrainUpdate);
            this.onChangeGraphicsOption();
        });
        this.simulationDistance = new SimpleOption("options.simulationDistance", SimpleOption.emptyTooltip(), (optionText, value) -> GameOptions.getGenericValueText((Text)optionText, (Text)Text.translatable((String)"options.chunks", (Object[])new Object[]{value})), (SimpleOption.Callbacks)new SimpleOption.ValidatingIntSliderCallbacks(SharedConstants.ALLOW_LOW_SIM_DISTANCE ? 2 : 5, bl ? 32 : 16, false), (Object)12, value -> this.onChangeGraphicsOption());
        this.syncChunkWrites = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
        this.load();
    }

    public float getTextBackgroundOpacity(float fallback) {
        return (Boolean)this.backgroundForChatOnly.getValue() != false ? fallback : ((Double)this.getTextBackgroundOpacity().getValue()).floatValue();
    }

    public int getTextBackgroundColor(float fallbackOpacity) {
        return ColorHelper.fromFloats((float)this.getTextBackgroundOpacity(fallbackOpacity), (float)0.0f, (float)0.0f, (float)0.0f);
    }

    public int getTextBackgroundColor(int fallbackColor) {
        return (Boolean)this.backgroundForChatOnly.getValue() != false ? fallbackColor : ColorHelper.fromFloats((float)((Double)this.textBackgroundOpacity.getValue()).floatValue(), (float)0.0f, (float)0.0f, (float)0.0f);
    }

    private void acceptProfiledOptions(OptionVisitor visitor) {
        visitor.accept("ao", this.ao);
        visitor.accept("biomeBlendRadius", this.biomeBlendRadius);
        visitor.accept("chunkSectionFadeInTime", this.chunkFade);
        visitor.accept("cutoutLeaves", this.cutoutLeaves);
        visitor.accept("enableVsync", this.enableVsync);
        visitor.accept("entityDistanceScaling", this.entityDistanceScaling);
        visitor.accept("entityShadows", this.entityShadows);
        visitor.accept("forceUnicodeFont", this.forceUnicodeFont);
        visitor.accept("japaneseGlyphVariants", this.japaneseGlyphVariants);
        visitor.accept("fov", this.fov);
        visitor.accept("fovEffectScale", this.fovEffectScale);
        visitor.accept("darknessEffectScale", this.darknessEffectScale);
        visitor.accept("glintSpeed", this.glintSpeed);
        visitor.accept("glintStrength", this.glintStrength);
        visitor.accept("graphicsPreset", this.preset);
        visitor.accept("prioritizeChunkUpdates", this.chunkBuilderMode);
        visitor.accept("fullscreen", this.fullscreen);
        visitor.accept("gamma", this.gamma);
        visitor.accept("guiScale", this.guiScale);
        visitor.accept("maxAnisotropyBit", this.maxAnisotropy);
        visitor.accept("textureFiltering", this.textureFiltering);
        visitor.accept("maxFps", this.maxFps);
        visitor.accept("improvedTransparency", this.improvedTransparency);
        visitor.accept("inactivityFpsLimit", this.inactivityFpsLimit);
        visitor.accept("mipmapLevels", this.mipmapLevels);
        visitor.accept("narrator", this.narrator);
        visitor.accept("particles", this.particles);
        visitor.accept("reducedDebugInfo", this.reducedDebugInfo);
        visitor.accept("renderClouds", this.cloudRenderMode);
        visitor.accept("cloudRange", this.cloudRenderDistance);
        visitor.accept("renderDistance", this.viewDistance);
        visitor.accept("simulationDistance", this.simulationDistance);
        visitor.accept("screenEffectScale", this.distortionEffectScale);
        visitor.accept("soundDevice", this.soundDevice);
        visitor.accept("vignette", this.vignette);
        visitor.accept("weatherRadius", this.weatherRadius);
    }

    private void accept(Visitor visitor) {
        this.acceptProfiledOptions((OptionVisitor)visitor);
        visitor.accept("autoJump", this.autoJump);
        visitor.accept("rotateWithMinecart", this.rotateWithMinecart);
        visitor.accept("operatorItemsTab", this.operatorItemsTab);
        visitor.accept("autoSuggestions", this.autoSuggestions);
        visitor.accept("chatColors", this.chatColors);
        visitor.accept("chatLinks", this.chatLinks);
        visitor.accept("chatLinksPrompt", this.chatLinksPrompt);
        visitor.accept("discrete_mouse_scroll", this.discreteMouseScroll);
        visitor.accept("invertXMouse", this.invertMouseX);
        visitor.accept("invertYMouse", this.invertMouseY);
        visitor.accept("realmsNotifications", this.realmsNotifications);
        visitor.accept("showSubtitles", this.showSubtitles);
        visitor.accept("directionalAudio", this.directionalAudio);
        visitor.accept("touchscreen", this.touchscreen);
        visitor.accept("bobView", this.bobView);
        visitor.accept("toggleCrouch", this.sneakToggled);
        visitor.accept("toggleSprint", this.sprintToggled);
        visitor.accept("toggleAttack", this.attackToggled);
        visitor.accept("toggleUse", this.useToggled);
        visitor.accept("sprintWindow", this.sprintWindow);
        visitor.accept("darkMojangStudiosBackground", this.monochromeLogo);
        visitor.accept("hideLightningFlashes", this.hideLightningFlashes);
        visitor.accept("hideSplashTexts", this.hideSplashTexts);
        visitor.accept("mouseSensitivity", this.mouseSensitivity);
        visitor.accept("damageTiltStrength", this.damageTiltStrength);
        visitor.accept("highContrast", this.highContrast);
        visitor.accept("highContrastBlockOutline", this.highContrastBlockOutline);
        visitor.accept("narratorHotkey", this.narratorHotkey);
        this.resourcePacks = (List)visitor.visitObject("resourcePacks", (Object)this.resourcePacks, GameOptions::parseList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.incompatibleResourcePacks = (List)visitor.visitObject("incompatibleResourcePacks", (Object)this.incompatibleResourcePacks, GameOptions::parseList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.lastServer = visitor.visitString("lastServer", this.lastServer);
        this.language = visitor.visitString("lang", this.language);
        visitor.accept("chatVisibility", this.chatVisibility);
        visitor.accept("chatOpacity", this.chatOpacity);
        visitor.accept("chatLineSpacing", this.chatLineSpacing);
        visitor.accept("textBackgroundOpacity", this.textBackgroundOpacity);
        visitor.accept("backgroundForChatOnly", this.backgroundForChatOnly);
        this.hideServerAddress = visitor.visitBoolean("hideServerAddress", this.hideServerAddress);
        this.advancedItemTooltips = visitor.visitBoolean("advancedItemTooltips", this.advancedItemTooltips);
        this.pauseOnLostFocus = visitor.visitBoolean("pauseOnLostFocus", this.pauseOnLostFocus);
        this.overrideWidth = visitor.visitInt("overrideWidth", this.overrideWidth);
        this.overrideHeight = visitor.visitInt("overrideHeight", this.overrideHeight);
        visitor.accept("chatHeightFocused", this.chatHeightFocused);
        visitor.accept("chatDelay", this.chatDelay);
        visitor.accept("chatHeightUnfocused", this.chatHeightUnfocused);
        visitor.accept("chatScale", this.chatScale);
        visitor.accept("chatWidth", this.chatWidth);
        visitor.accept("notificationDisplayTime", this.notificationDisplayTime);
        this.useNativeTransport = visitor.visitBoolean("useNativeTransport", this.useNativeTransport);
        visitor.accept("mainHand", this.mainArm);
        visitor.accept("attackIndicator", this.attackIndicator);
        this.tutorialStep = (TutorialStep)visitor.visitObject("tutorialStep", (Object)this.tutorialStep, TutorialStep::byName, TutorialStep::getName);
        visitor.accept("mouseWheelSensitivity", this.mouseWheelSensitivity);
        visitor.accept("rawMouseInput", this.rawMouseInput);
        visitor.accept("allowCursorChanges", this.allowCursorChanges);
        this.glDebugVerbosity = visitor.visitInt("glDebugVerbosity", this.glDebugVerbosity);
        this.skipMultiplayerWarning = visitor.visitBoolean("skipMultiplayerWarning", this.skipMultiplayerWarning);
        visitor.accept("hideMatchedNames", this.hideMatchedNames);
        this.joinedFirstServer = visitor.visitBoolean("joinedFirstServer", this.joinedFirstServer);
        this.syncChunkWrites = visitor.visitBoolean("syncChunkWrites", this.syncChunkWrites);
        visitor.accept("showAutosaveIndicator", this.showAutosaveIndicator);
        visitor.accept("allowServerListing", this.allowServerListing);
        visitor.accept("onlyShowSecureChat", this.onlyShowSecureChat);
        visitor.accept("saveChatDrafts", this.chatDrafts);
        visitor.accept("panoramaScrollSpeed", this.panoramaSpeed);
        visitor.accept("telemetryOptInExtra", this.telemetryOptInExtra);
        this.onboardAccessibility = visitor.visitBoolean("onboardAccessibility", this.onboardAccessibility);
        visitor.accept("menuBackgroundBlurriness", this.menuBackgroundBlurriness);
        this.startedCleanly = visitor.visitBoolean("startedCleanly", this.startedCleanly);
        visitor.accept("musicToast", this.musicToast);
        visitor.accept("musicFrequency", this.musicFrequency);
        for (KeyBinding keyBinding : this.allKeys) {
            String string2;
            String string = keyBinding.getBoundKeyTranslationKey();
            if (string.equals(string2 = visitor.visitString("key_" + keyBinding.getId(), string))) continue;
            keyBinding.setBoundKey(InputUtil.fromTranslationKey((String)string2));
        }
        for (KeyBinding keyBinding : SoundCategory.values()) {
            visitor.accept("soundCategory_" + keyBinding.getName(), (SimpleOption)this.soundVolumeLevels.get(keyBinding));
        }
        for (KeyBinding keyBinding : PlayerModelPart.values()) {
            boolean bl = this.enabledPlayerModelParts.contains(keyBinding);
            boolean bl2 = visitor.visitBoolean("modelPart_" + keyBinding.getName(), bl);
            if (bl2 == bl) continue;
            this.setPlayerModelPart((PlayerModelPart)keyBinding, bl2);
        }
    }

    public void load() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            NbtCompound nbtCompound = new NbtCompound();
            try (BufferedReader bufferedReader = Files.newReader((File)this.optionsFile, (Charset)StandardCharsets.UTF_8);){
                bufferedReader.lines().forEach(line -> {
                    try {
                        Iterator iterator = COLON_SPLITTER.split((CharSequence)line).iterator();
                        nbtCompound.putString((String)iterator.next(), (String)iterator.next());
                    }
                    catch (Exception exception) {
                        LOGGER.warn("Skipping bad option: {}", line);
                    }
                });
            }
            NbtCompound nbtCompound2 = this.update(nbtCompound);
            this.accept((Visitor)new /* Unavailable Anonymous Inner Class!! */);
            nbtCompound2.getString("fullscreenResolution").ifPresent(string -> {
                this.fullscreenResolution = string;
            });
            KeyBinding.updateKeysByCode();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to load options", (Throwable)exception);
        }
    }

    static boolean isTrue(String value) {
        return "true".equals(value);
    }

    static boolean isFalse(String value) {
        return "false".equals(value);
    }

    private NbtCompound update(NbtCompound nbt) {
        int i = 0;
        try {
            i = nbt.getString("version").map(Integer::parseInt).orElse(0);
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return DataFixTypes.OPTIONS.update(this.client.getDataFixer(), nbt, i);
    }

    public void write() {
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));){
            printWriter.println("version:" + SharedConstants.getGameVersion().dataVersion().id());
            this.accept((Visitor)new /* Unavailable Anonymous Inner Class!! */);
            String string = this.getFullscreenResolution();
            if (string != null) {
                printWriter.println("fullscreenResolution:" + string);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save options", (Throwable)exception);
        }
        this.sendClientSettings();
    }

    private @Nullable String getFullscreenResolution() {
        Window window = this.client.getWindow();
        if (window == null) {
            return this.fullscreenResolution;
        }
        if (window.getFullscreenVideoMode().isPresent()) {
            return ((VideoMode)window.getFullscreenVideoMode().get()).asString();
        }
        return null;
    }

    public SyncedClientOptions getSyncedOptions() {
        int i = 0;
        for (PlayerModelPart playerModelPart : this.enabledPlayerModelParts) {
            i |= playerModelPart.getBitFlag();
        }
        return new SyncedClientOptions(this.language, ((Integer)this.viewDistance.getValue()).intValue(), (ChatVisibility)this.chatVisibility.getValue(), ((Boolean)this.chatColors.getValue()).booleanValue(), i, (Arm)this.mainArm.getValue(), this.client.shouldFilterText(), ((Boolean)this.allowServerListing.getValue()).booleanValue(), (ParticlesMode)this.particles.getValue());
    }

    public void sendClientSettings() {
        if (this.client.player != null) {
            this.client.player.networkHandler.syncOptions(this.getSyncedOptions());
        }
    }

    public void setPlayerModelPart(PlayerModelPart part, boolean enabled) {
        if (enabled) {
            this.enabledPlayerModelParts.add(part);
        } else {
            this.enabledPlayerModelParts.remove(part);
        }
    }

    public boolean isPlayerModelPartEnabled(PlayerModelPart part) {
        return this.enabledPlayerModelParts.contains(part);
    }

    public CloudRenderMode getCloudRenderModeValue() {
        return (CloudRenderMode)this.cloudRenderMode.getValue();
    }

    public boolean shouldUseNativeTransport() {
        return this.useNativeTransport;
    }

    public void addResourcePackProfilesToManager(ResourcePackManager manager) {
        LinkedHashSet set = Sets.newLinkedHashSet();
        Iterator iterator = this.resourcePacks.iterator();
        while (iterator.hasNext()) {
            String string = (String)iterator.next();
            ResourcePackProfile resourcePackProfile = manager.getProfile(string);
            if (resourcePackProfile == null && !string.startsWith("file/")) {
                resourcePackProfile = manager.getProfile("file/" + string);
            }
            if (resourcePackProfile == null) {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)string);
                iterator.remove();
                continue;
            }
            if (!resourcePackProfile.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(string)) {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)string);
                iterator.remove();
                continue;
            }
            if (resourcePackProfile.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(string)) {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)string);
                this.incompatibleResourcePacks.remove(string);
                continue;
            }
            set.add(resourcePackProfile.getId());
        }
        manager.setEnabledProfiles((Collection)set);
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    private static List<String> parseList(String content) {
        List list = (List)JsonHelper.deserialize((Gson)GSON, (String)content, (TypeToken)STRING_LIST_TYPE);
        return list != null ? list : Lists.newArrayList();
    }

    public File getOptionsFile() {
        return this.optionsFile;
    }

    public String collectProfiledOptions() {
        ArrayList<Pair> list = new ArrayList<Pair>();
        this.acceptProfiledOptions((OptionVisitor)new /* Unavailable Anonymous Inner Class!! */);
        list.add(Pair.of((Object)"fullscreenResolution", (Object)String.valueOf(this.fullscreenResolution)));
        list.add(Pair.of((Object)"glDebugVerbosity", (Object)this.glDebugVerbosity));
        list.add(Pair.of((Object)"overrideHeight", (Object)this.overrideHeight));
        list.add(Pair.of((Object)"overrideWidth", (Object)this.overrideWidth));
        list.add(Pair.of((Object)"syncChunkWrites", (Object)this.syncChunkWrites));
        list.add(Pair.of((Object)"useNativeTransport", (Object)this.useNativeTransport));
        list.add(Pair.of((Object)"resourcePacks", (Object)this.resourcePacks));
        return list.stream().sorted(Comparator.comparing(Pair::getFirst)).map(option -> (String)option.getFirst() + ": " + String.valueOf(option.getSecond())).collect(Collectors.joining(System.lineSeparator()));
    }

    public void setServerViewDistance(int serverViewDistance) {
        this.serverViewDistance = serverViewDistance;
    }

    public int getClampedViewDistance() {
        return this.serverViewDistance > 0 ? Math.min((Integer)this.viewDistance.getValue(), this.serverViewDistance) : (Integer)this.viewDistance.getValue();
    }

    private static Text getPixelValueText(Text prefix, int value) {
        return Text.translatable((String)"options.pixel_value", (Object[])new Object[]{prefix, value});
    }

    private static Text getPercentValueText(Text prefix, double value) {
        return Text.translatable((String)"options.percent_value", (Object[])new Object[]{prefix, (int)(value * 100.0)});
    }

    public static Text getGenericValueText(Text prefix, Text value) {
        return Text.translatable((String)"options.generic_value", (Object[])new Object[]{prefix, value});
    }

    public static Text getGenericValueText(Text prefix, int value) {
        return GameOptions.getGenericValueText((Text)prefix, (Text)Text.literal((String)Integer.toString(value)));
    }

    public static Text getGenericValueOrOffText(Text prefix, int value) {
        if (value == 0) {
            return GameOptions.getGenericValueText((Text)prefix, (Text)ScreenTexts.OFF);
        }
        return GameOptions.getGenericValueText((Text)prefix, (int)value);
    }

    private static Text getPercentValueOrOffText(Text prefix, double value) {
        if (value == 0.0) {
            return GameOptions.getGenericValueText((Text)prefix, (Text)ScreenTexts.OFF);
        }
        return GameOptions.getPercentValueText((Text)prefix, (double)value);
    }
}

