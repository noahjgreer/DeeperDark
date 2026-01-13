/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.MessageScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen
 *  net.minecraft.client.gui.screen.pack.PackScreen
 *  net.minecraft.client.gui.screen.world.CreateWorldCallback
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen$GameTab
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen$MoreTab
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen$WorldTab
 *  net.minecraft.client.gui.screen.world.ExperimentsScreen
 *  net.minecraft.client.gui.screen.world.InitialWorldOptions
 *  net.minecraft.client.gui.screen.world.WorldCreationSettings
 *  net.minecraft.client.gui.screen.world.WorldCreator
 *  net.minecraft.client.gui.screen.world.WorldCreator$Mode
 *  net.minecraft.client.gui.tab.Tab
 *  net.minecraft.client.gui.tab.TabManager
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TabNavigationWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.toast.SystemToast
 *  net.minecraft.client.world.GeneratorOptionsFactory
 *  net.minecraft.client.world.GeneratorOptionsHolder
 *  net.minecraft.command.permission.LeveledPermissionPredicate
 *  net.minecraft.command.permission.PermissionPredicate
 *  net.minecraft.registry.CombinedDynamicRegistries
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.RegistryOps
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.ServerDynamicRegistryType
 *  net.minecraft.resource.DataConfiguration
 *  net.minecraft.resource.DataPackSettings
 *  net.minecraft.resource.ResourcePackManager
 *  net.minecraft.resource.ResourcePackProvider
 *  net.minecraft.resource.VanillaDataPackProvider
 *  net.minecraft.resource.featuretoggle.FeatureFlags
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.SaveLoading
 *  net.minecraft.server.SaveLoading$DataPacks
 *  net.minecraft.server.SaveLoading$LoadContext
 *  net.minecraft.server.SaveLoading$LoadContextSupplierContext
 *  net.minecraft.server.SaveLoading$ServerConfig
 *  net.minecraft.server.command.CommandManager$RegistrationEnvironment
 *  net.minecraft.server.integrated.IntegratedServerLoader
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.WorldSavePath
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.util.path.SymlinkFinder
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.GameMode
 *  net.minecraft.world.SaveProperties
 *  net.minecraft.world.dimension.DimensionOptionsRegistryHolder
 *  net.minecraft.world.dimension.DimensionOptionsRegistryHolder$DimensionsConfig
 *  net.minecraft.world.gen.FlatLevelGeneratorPresets
 *  net.minecraft.world.gen.GeneratorOptions
 *  net.minecraft.world.gen.WorldPreset
 *  net.minecraft.world.gen.WorldPresets
 *  net.minecraft.world.level.LevelInfo
 *  net.minecraft.world.level.LevelProperties
 *  net.minecraft.world.level.LevelProperties$SpecialProperty
 *  net.minecraft.world.level.WorldGenSettings
 *  net.minecraft.world.level.storage.LevelStorage$Session
 *  net.minecraft.world.rule.GameRules
 *  net.minecraft.world.rule.ServerGameRules$Builder
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.CreateWorldCallback;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.ExperimentsScreen;
import net.minecraft.client.gui.screen.world.InitialWorldOptions;
import net.minecraft.client.gui.screen.world.WorldCreationSettings;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.GeneratorOptionsFactory;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.command.permission.LeveledPermissionPredicate;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.path.SymlinkFinder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.rule.ServerGameRules;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
    private static final int field_42165 = 1;
    private static final int field_42166 = 210;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TEMP_DIR_PREFIX = "mcworld-";
    static final Text GAME_MODE_TEXT = Text.translatable((String)"selectWorld.gameMode");
    static final Text ENTER_NAME_TEXT = Text.translatable((String)"selectWorld.enterName");
    static final Text EXPERIMENTS_TEXT = Text.translatable((String)"selectWorld.experiments");
    static final Text ALLOW_COMMANDS_INFO_TEXT = Text.translatable((String)"selectWorld.allowCommands.info");
    private static final Text PREPARING_TEXT = Text.translatable((String)"createWorld.preparing");
    private static final int field_42170 = 10;
    private static final int field_42171 = 8;
    public static final Identifier TAB_HEADER_BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"textures/gui/tab_header_background.png");
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    final WorldCreator worldCreator;
    private final TabManager tabManager = new TabManager(clickableWidget -> {
        ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(clickableWidget);
    }, child -> this.remove(child));
    private boolean recreated;
    private final SymlinkFinder symlinkFinder;
    private final CreateWorldCallback callback;
    private final Runnable onClosed;
    private @Nullable Path dataPackTempDir;
    private @Nullable ResourcePackManager packManager;
    private @Nullable TabNavigationWidget tabNavigation;

    public static void show(MinecraftClient client, Runnable onClosed) {
        CreateWorldScreen.show((MinecraftClient)client, (Runnable)onClosed, (screen, combinedDynamicRegistries, levelProperties, dataPackTempDir) -> screen.startServer(combinedDynamicRegistries, (SaveProperties)levelProperties));
    }

    public static void show(MinecraftClient client, Runnable onClosed, CreateWorldCallback callback) {
        GeneratorOptionsFactory generatorOptionsFactory = (dataPackContents, dynamicRegistries, settings) -> new GeneratorOptionsHolder(settings.worldGenSettings(), dynamicRegistries, dataPackContents, settings.dataConfiguration());
        Function<SaveLoading.LoadContextSupplierContext, WorldGenSettings> function = context -> new WorldGenSettings(GeneratorOptions.createRandom(), WorldPresets.createDemoOptions((RegistryWrapper.WrapperLookup)context.worldGenRegistryManager()));
        CreateWorldScreen.show((MinecraftClient)client, (Runnable)onClosed, function, (GeneratorOptionsFactory)generatorOptionsFactory, (RegistryKey)WorldPresets.DEFAULT, (CreateWorldCallback)callback);
    }

    public static void showTestWorld(MinecraftClient client, Runnable onClosed) {
        GeneratorOptionsFactory generatorOptionsFactory = (dataPackContents, dynamicRegistries, settings) -> new GeneratorOptionsHolder(settings.worldGenSettings().generatorOptions(), settings.worldGenSettings().dimensionOptionsRegistryHolder(), dynamicRegistries, dataPackContents, settings.dataConfiguration(), new InitialWorldOptions(WorldCreator.Mode.CREATIVE, new ServerGameRules.Builder().put(GameRules.ADVANCE_TIME, (Object)false).put(GameRules.ADVANCE_WEATHER, (Object)false).put(GameRules.DO_MOB_SPAWNING, (Object)false).build(), FlatLevelGeneratorPresets.REDSTONE_READY));
        Function<SaveLoading.LoadContextSupplierContext, WorldGenSettings> function = context -> new WorldGenSettings(GeneratorOptions.createTestWorld(), WorldPresets.createTestOptions((RegistryWrapper.WrapperLookup)context.worldGenRegistryManager()));
        CreateWorldScreen.show((MinecraftClient)client, (Runnable)onClosed, function, (GeneratorOptionsFactory)generatorOptionsFactory, (RegistryKey)WorldPresets.FLAT, (screen, combinedDynamicRegistries, levelProperties, dataPackTempDir) -> screen.startServer(combinedDynamicRegistries, (SaveProperties)levelProperties));
    }

    private static void show(MinecraftClient client, Runnable onClosed, Function<SaveLoading.LoadContextSupplierContext, WorldGenSettings> settingsSupplier, GeneratorOptionsFactory generatorOptionsFactory, RegistryKey<WorldPreset> presetKey, CreateWorldCallback callback) {
        CreateWorldScreen.showMessage((MinecraftClient)client, (Text)PREPARING_TEXT);
        ResourcePackManager resourcePackManager = new ResourcePackManager(new ResourcePackProvider[]{new VanillaDataPackProvider(client.getSymlinkFinder())});
        DataConfiguration dataConfiguration = SharedConstants.isDevelopment ? new DataConfiguration(new DataPackSettings(List.of("vanilla", "tests"), List.of()), FeatureFlags.DEFAULT_ENABLED_FEATURES) : DataConfiguration.SAFE_MODE;
        SaveLoading.ServerConfig serverConfig = CreateWorldScreen.createServerConfig((ResourcePackManager)resourcePackManager, (DataConfiguration)dataConfiguration);
        CompletableFuture completableFuture = SaveLoading.load((SaveLoading.ServerConfig)serverConfig, context -> new SaveLoading.LoadContext((Object)new WorldCreationSettings((WorldGenSettings)settingsSupplier.apply(context), context.dataConfiguration()), context.dimensionsRegistryManager()), (resourceManager, dataPackContents, dynamicRegistries, settings) -> {
            resourceManager.close();
            return generatorOptionsFactory.apply(dataPackContents, dynamicRegistries, settings);
        }, (Executor)Util.getMainWorkerExecutor(), (Executor)client);
        client.runTasks(completableFuture::isDone);
        client.setScreen((Screen)new CreateWorldScreen(client, onClosed, (GeneratorOptionsHolder)completableFuture.join(), Optional.of(presetKey), OptionalLong.empty(), callback));
    }

    public static CreateWorldScreen create(MinecraftClient client, Runnable onClosed, LevelInfo levelInfo, GeneratorOptionsHolder generatorOptionsHolder, @Nullable Path dataPackTempDir2) {
        CreateWorldScreen createWorldScreen = new CreateWorldScreen(client, onClosed, generatorOptionsHolder, WorldPresets.getWorldPreset((DimensionOptionsRegistryHolder)generatorOptionsHolder.selectedDimensions()), OptionalLong.of(generatorOptionsHolder.generatorOptions().getSeed()), (screen, combinedDynamicRegistries, levelProperties, dataPackTempDir) -> screen.startServer(combinedDynamicRegistries, (SaveProperties)levelProperties));
        createWorldScreen.recreated = true;
        createWorldScreen.worldCreator.setWorldName(levelInfo.getLevelName());
        createWorldScreen.worldCreator.setCheatsEnabled(levelInfo.areCommandsAllowed());
        createWorldScreen.worldCreator.setDifficulty(levelInfo.getDifficulty());
        createWorldScreen.worldCreator.getGameRules().copyFrom(levelInfo.getGameRules(), null);
        if (levelInfo.isHardcore()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.HARDCORE);
        } else if (levelInfo.getGameMode().isSurvivalLike()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.SURVIVAL);
        } else if (levelInfo.getGameMode().isCreative()) {
            createWorldScreen.worldCreator.setGameMode(WorldCreator.Mode.CREATIVE);
        }
        createWorldScreen.dataPackTempDir = dataPackTempDir2;
        return createWorldScreen;
    }

    private CreateWorldScreen(MinecraftClient client, Runnable onClosed, GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> defaultWorldType, OptionalLong seed, CreateWorldCallback callback) {
        super((Text)Text.translatable((String)"selectWorld.create"));
        this.onClosed = onClosed;
        this.symlinkFinder = client.getSymlinkFinder();
        this.callback = callback;
        this.worldCreator = new WorldCreator(client.getLevelStorage().getSavesDirectory(), generatorOptionsHolder, defaultWorldType, seed);
    }

    public WorldCreator getWorldCreator() {
        return this.worldCreator;
    }

    protected void init() {
        this.tabNavigation = TabNavigationWidget.builder((TabManager)this.tabManager, (int)this.width).tabs(new Tab[]{new GameTab(this), new WorldTab(this), new MoreTab(this)}).build();
        this.addDrawableChild((Element)this.tabNavigation);
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectWorld.create"), button -> this.createLevel()).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.onCloseScreen()).build());
        this.layout.forEachChild(child -> {
            child.setNavigationOrder(1);
            this.addDrawableChild((Element)child);
        });
        this.tabNavigation.selectTab(0, false);
        this.worldCreator.update();
        this.refreshWidgetPositions();
    }

    protected void setInitialFocus() {
    }

    public void refreshWidgetPositions() {
        if (this.tabNavigation == null) {
            return;
        }
        this.tabNavigation.setWidth(this.width);
        this.tabNavigation.init();
        int i = this.tabNavigation.getNavigationFocus().getBottom();
        ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - this.layout.getFooterHeight() - i);
        this.tabManager.setTabArea(screenRect);
        this.layout.setHeaderHeight(i);
        this.layout.refreshPositions();
    }

    private static void showMessage(MinecraftClient client, Text text) {
        client.setScreenAndRender((Screen)new MessageScreen(text));
    }

    private void createLevel() {
        GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
        DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = generatorOptionsHolder.selectedDimensions().toConfig(generatorOptionsHolder.dimensionOptionsRegistry());
        CombinedDynamicRegistries combinedDynamicRegistries = generatorOptionsHolder.combinedDynamicRegistries().with((Object)ServerDynamicRegistryType.DIMENSIONS, new DynamicRegistryManager.Immutable[]{dimensionsConfig.toDynamicRegistryManager()});
        Lifecycle lifecycle = FeatureFlags.isNotVanilla((FeatureSet)generatorOptionsHolder.dataConfiguration().enabledFeatures()) ? Lifecycle.experimental() : Lifecycle.stable();
        Lifecycle lifecycle2 = combinedDynamicRegistries.getCombinedRegistryManager().getLifecycle();
        Lifecycle lifecycle3 = lifecycle2.add(lifecycle);
        boolean bl = !this.recreated && lifecycle2 == Lifecycle.stable();
        LevelInfo levelInfo = this.createLevelInfo(dimensionsConfig.specialWorldProperty() == LevelProperties.SpecialProperty.DEBUG);
        LevelProperties levelProperties = new LevelProperties(levelInfo, this.worldCreator.getGeneratorOptionsHolder().generatorOptions(), dimensionsConfig.specialWorldProperty(), lifecycle3);
        IntegratedServerLoader.tryLoad((MinecraftClient)this.client, (CreateWorldScreen)this, (Lifecycle)lifecycle3, () -> this.createAndClearTempDir(combinedDynamicRegistries, levelProperties), (boolean)bl);
    }

    private void createAndClearTempDir(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, LevelProperties levelProperties) {
        boolean bl = this.callback.create(this, combinedDynamicRegistries, levelProperties, this.dataPackTempDir);
        this.clearDataPackTempDir();
        if (!bl) {
            this.onCloseScreen();
        }
    }

    private boolean startServer(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, SaveProperties saveProperties) {
        String string = this.worldCreator.getWorldDirectoryName();
        GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
        CreateWorldScreen.showMessage((MinecraftClient)this.client, (Text)PREPARING_TEXT);
        Optional optional = CreateWorldScreen.createSession((MinecraftClient)this.client, (String)string, (Path)this.dataPackTempDir);
        if (optional.isEmpty()) {
            SystemToast.addPackCopyFailure((MinecraftClient)this.client, (String)string);
            return false;
        }
        this.client.createIntegratedServerLoader().startNewWorld((LevelStorage.Session)optional.get(), generatorOptionsHolder.dataPackContents(), combinedDynamicRegistries, saveProperties);
        return true;
    }

    private LevelInfo createLevelInfo(boolean debugWorld) {
        String string = this.worldCreator.getWorldName().trim();
        if (debugWorld) {
            GameRules gameRules = new GameRules(DataConfiguration.SAFE_MODE.enabledFeatures());
            gameRules.setValue(GameRules.ADVANCE_TIME, (Object)false, null);
            return new LevelInfo(string, GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, gameRules, DataConfiguration.SAFE_MODE);
        }
        return new LevelInfo(string, this.worldCreator.getGameMode().defaultGameMode, this.worldCreator.isHardcore(), this.worldCreator.getDifficulty(), this.worldCreator.areCheatsEnabled(), this.worldCreator.getGameRules(), this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
    }

    public boolean keyPressed(KeyInput input) {
        if (this.tabNavigation.keyPressed(input)) {
            return true;
        }
        if (super.keyPressed(input)) {
            return true;
        }
        if (input.isEnter()) {
            this.createLevel();
            return true;
        }
        return false;
    }

    public void close() {
        this.onCloseScreen();
    }

    public void onCloseScreen() {
        this.onClosed.run();
        this.clearDataPackTempDir();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR_TEXTURE, 0, this.height - this.layout.getFooterHeight() - 2, 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    protected void renderDarkening(DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TAB_HEADER_BACKGROUND_TEXTURE, 0, 0, 0.0f, 0.0f, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderDarkening(context, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    private @Nullable Path getOrCreateDataPackTempDir() {
        if (this.dataPackTempDir == null) {
            try {
                this.dataPackTempDir = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to create temporary dir", (Throwable)iOException);
                SystemToast.addPackCopyFailure((MinecraftClient)this.client, (String)this.worldCreator.getWorldDirectoryName());
                this.onCloseScreen();
            }
        }
        return this.dataPackTempDir;
    }

    void openExperimentsScreen(DataConfiguration dataConfiguration) {
        Pair pair = this.getScannedPack(dataConfiguration);
        if (pair != null) {
            this.client.setScreen((Screen)new ExperimentsScreen((Screen)this, (ResourcePackManager)pair.getSecond(), resourcePackManager -> this.applyDataPacks(resourcePackManager, false, arg_0 -> this.openExperimentsScreen(arg_0))));
        }
    }

    void openPackScreen(DataConfiguration dataConfiguration) {
        Pair pair = this.getScannedPack(dataConfiguration);
        if (pair != null) {
            this.client.setScreen((Screen)new PackScreen((ResourcePackManager)pair.getSecond(), resourcePackManager -> this.applyDataPacks(resourcePackManager, true, arg_0 -> this.openPackScreen(arg_0)), (Path)pair.getFirst(), (Text)Text.translatable((String)"dataPack.title")));
        }
    }

    private void applyDataPacks(ResourcePackManager dataPackManager, boolean fromPackScreen, Consumer<DataConfiguration> configurationSetter) {
        List list2;
        ImmutableList list = ImmutableList.copyOf((Collection)dataPackManager.getEnabledIds());
        DataConfiguration dataConfiguration = new DataConfiguration(new DataPackSettings((List)list, list2 = (List)dataPackManager.getIds().stream().filter(arg_0 -> CreateWorldScreen.method_29983((List)list, arg_0)).collect(ImmutableList.toImmutableList())), this.worldCreator.getGeneratorOptionsHolder().dataConfiguration().enabledFeatures());
        if (this.worldCreator.updateDataConfiguration(dataConfiguration)) {
            this.client.setScreen((Screen)this);
            return;
        }
        FeatureSet featureSet = dataPackManager.getRequestedFeatures();
        if (FeatureFlags.isNotVanilla((FeatureSet)featureSet) && fromPackScreen) {
            this.client.setScreen((Screen)new ExperimentalWarningScreen(dataPackManager.getEnabledProfiles(), confirmed -> {
                if (confirmed) {
                    this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
                } else {
                    configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
                }
            }));
        } else {
            this.validateDataPacks(dataPackManager, dataConfiguration, configurationSetter);
        }
    }

    private void validateDataPacks(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration, Consumer<DataConfiguration> configurationSetter) {
        this.client.setScreenAndRender((Screen)new MessageScreen((Text)Text.translatable((String)"dataPack.validation.working")));
        SaveLoading.ServerConfig serverConfig = CreateWorldScreen.createServerConfig((ResourcePackManager)dataPackManager, (DataConfiguration)dataConfiguration);
        ((CompletableFuture)((CompletableFuture)SaveLoading.load((SaveLoading.ServerConfig)serverConfig, context -> {
            if (context.worldGenRegistryManager().getOrThrow(RegistryKeys.WORLD_PRESET).streamEntries().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one world preset to continue");
            }
            if (context.worldGenRegistryManager().getOrThrow(RegistryKeys.BIOME).streamEntries().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one biome continue");
            }
            GeneratorOptionsHolder generatorOptionsHolder = this.worldCreator.getGeneratorOptionsHolder();
            RegistryOps dynamicOps = generatorOptionsHolder.getCombinedRegistryManager().getOps((DynamicOps)JsonOps.INSTANCE);
            DataResult dataResult = WorldGenSettings.encode((DynamicOps)dynamicOps, (GeneratorOptions)generatorOptionsHolder.generatorOptions(), (DimensionOptionsRegistryHolder)generatorOptionsHolder.selectedDimensions()).setLifecycle(Lifecycle.stable());
            RegistryOps dynamicOps2 = context.worldGenRegistryManager().getOps((DynamicOps)JsonOps.INSTANCE);
            WorldGenSettings worldGenSettings = (WorldGenSettings)dataResult.flatMap(arg_0 -> CreateWorldScreen.method_45682((DynamicOps)dynamicOps2, arg_0)).getOrThrow(error -> new IllegalStateException("Error parsing worldgen settings after loading data packs: " + error));
            return new SaveLoading.LoadContext((Object)new WorldCreationSettings(worldGenSettings, context.dataConfiguration()), context.dimensionsRegistryManager());
        }, (resourceManager, dataPackContents, combinedDynamicRegistries, context) -> {
            resourceManager.close();
            return new GeneratorOptionsHolder(context.worldGenSettings(), combinedDynamicRegistries, dataPackContents, context.dataConfiguration());
        }, (Executor)Util.getMainWorkerExecutor(), (Executor)this.client).thenApply(generatorOptionsHolder -> {
            generatorOptionsHolder.initializeIndexedFeaturesLists();
            return generatorOptionsHolder;
        })).thenAcceptAsync(arg_0 -> ((WorldCreator)this.worldCreator).setGeneratorOptionsHolder(arg_0), (Executor)this.client)).handleAsync((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.warn("Failed to validate datapack", throwable);
                this.client.setScreen((Screen)new ConfirmScreen(confirmed -> {
                    if (confirmed) {
                        configurationSetter.accept(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
                    } else {
                        configurationSetter.accept(DataConfiguration.SAFE_MODE);
                    }
                }, (Text)Text.translatable((String)"dataPack.validation.failed"), ScreenTexts.EMPTY, (Text)Text.translatable((String)"dataPack.validation.back"), (Text)Text.translatable((String)"dataPack.validation.reset")));
            } else {
                this.client.setScreen((Screen)this);
            }
            return null;
        }, (Executor)this.client);
    }

    private static SaveLoading.ServerConfig createServerConfig(ResourcePackManager dataPackManager, DataConfiguration dataConfiguration) {
        SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(dataPackManager, dataConfiguration, false, true);
        return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, (PermissionPredicate)LeveledPermissionPredicate.GAMEMASTERS);
    }

    private void clearDataPackTempDir() {
        if (this.dataPackTempDir != null && Files.exists(this.dataPackTempDir, new LinkOption[0])) {
            try (Stream<Path> stream = Files.walk(this.dataPackTempDir, new FileVisitOption[0]);){
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to remove temporary file {}", path, (Object)iOException);
                    }
                });
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to list temporary dir {}", (Object)this.dataPackTempDir);
            }
        }
        this.dataPackTempDir = null;
    }

    private static void copyDataPack(Path srcFolder, Path destFolder, Path dataPackFile) {
        try {
            Util.relativeCopy((Path)srcFolder, (Path)destFolder, (Path)dataPackFile);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", (Object)dataPackFile, (Object)destFolder);
            throw new UncheckedIOException(iOException);
        }
    }

    /*
     * WARNING - bad return control flow
     */
    private static Optional<LevelStorage.Session> createSession(MinecraftClient client, String worldDirectoryName, @Nullable Path dataPackTempDir) {
        Optional<LevelStorage.Session> optional;
        block12: {
            LevelStorage.Session session;
            block11: {
                session = client.getLevelStorage().createSessionWithoutSymlinkCheck(worldDirectoryName);
                if (dataPackTempDir != null) break block11;
                return Optional.of(session);
            }
            Stream<Path> stream = Files.walk(dataPackTempDir, new FileVisitOption[0]);
            try {
                Path path2 = session.getDirectory(WorldSavePath.DATAPACKS);
                PathUtil.createDirectories((Path)path2);
                stream.filter(path -> !path.equals(dataPackTempDir)).forEach(path -> CreateWorldScreen.copyDataPack((Path)dataPackTempDir, (Path)path2, (Path)path));
                optional = Optional.of(session);
                if (stream == null) break block12;
            }
            catch (Throwable throwable) {
                try {
                    try {
                        if (stream != null) {
                            try {
                                stream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException | UncheckedIOException exception) {
                        LOGGER.warn("Failed to copy datapacks to world {}", (Object)worldDirectoryName, (Object)exception);
                        session.close();
                    }
                }
                catch (IOException | UncheckedIOException exception2) {
                    LOGGER.warn("Failed to create access for {}", (Object)worldDirectoryName, (Object)exception2);
                }
            }
            stream.close();
        }
        return optional;
        return Optional.empty();
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    public static @Nullable Path copyDataPack(Path srcFolder, MinecraftClient client) {
        @Nullable MutableObject mutableObject = new MutableObject();
        try (Stream<Path> stream = Files.walk(srcFolder, new FileVisitOption[0]);){
            stream.filter(dataPackFile -> !dataPackFile.equals(srcFolder)).forEach(dataPackFile -> {
                Path path2 = (Path)mutableObject.get();
                if (path2 == null) {
                    try {
                        path2 = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to create temporary dir");
                        throw new UncheckedIOException(iOException);
                    }
                    mutableObject.setValue((Object)path2);
                }
                CreateWorldScreen.copyDataPack((Path)srcFolder, (Path)path2, (Path)dataPackFile);
            });
        }
        catch (IOException | UncheckedIOException exception) {
            LOGGER.warn("Failed to copy datapacks from world {}", (Object)srcFolder, (Object)exception);
            SystemToast.addPackCopyFailure((MinecraftClient)client, (String)srcFolder.toString());
            return null;
        }
        return (Path)mutableObject.get();
    }

    private @Nullable Pair<Path, ResourcePackManager> getScannedPack(DataConfiguration dataConfiguration) {
        Path path = this.getOrCreateDataPackTempDir();
        if (path != null) {
            if (this.packManager == null) {
                this.packManager = VanillaDataPackProvider.createManager((Path)path, (SymlinkFinder)this.symlinkFinder);
                this.packManager.scanPacks();
            }
            this.packManager.setEnabledProfiles((Collection)dataConfiguration.dataPacks().getEnabled());
            return Pair.of((Object)path, (Object)this.packManager);
        }
        return null;
    }

    private static /* synthetic */ DataResult method_45682(DynamicOps dynamicOps, JsonElement json) {
        return WorldGenSettings.CODEC.parse(dynamicOps, (Object)json);
    }

    private static /* synthetic */ boolean method_29983(List list, String name) {
        return !list.contains(name);
    }

    static /* synthetic */ TextRenderer method_48646(CreateWorldScreen createWorldScreen) {
        return createWorldScreen.textRenderer;
    }

    static /* synthetic */ void method_48649(CreateWorldScreen createWorldScreen, Element element) {
        createWorldScreen.setInitialFocus(element);
    }

    static /* synthetic */ TextRenderer method_48647(CreateWorldScreen createWorldScreen) {
        return createWorldScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_48651(CreateWorldScreen createWorldScreen) {
        return createWorldScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_48652(CreateWorldScreen createWorldScreen) {
        return createWorldScreen.textRenderer;
    }

    static /* synthetic */ MinecraftClient method_48653(CreateWorldScreen createWorldScreen) {
        return createWorldScreen.client;
    }

    static /* synthetic */ MinecraftClient method_48655(CreateWorldScreen createWorldScreen) {
        return createWorldScreen.client;
    }

    static /* synthetic */ MinecraftClient method_48656(CreateWorldScreen createWorldScreen) {
        return createWorldScreen.client;
    }
}

