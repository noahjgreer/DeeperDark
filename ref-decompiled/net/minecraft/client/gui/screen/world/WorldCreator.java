/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.world.LevelScreenProvider
 *  net.minecraft.client.gui.screen.world.WorldCreator
 *  net.minecraft.client.gui.screen.world.WorldCreator$Mode
 *  net.minecraft.client.gui.screen.world.WorldCreator$WorldType
 *  net.minecraft.client.world.GeneratorOptionsHolder
 *  net.minecraft.client.world.GeneratorOptionsHolder$RegistryAwareModifier
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.registry.tag.WorldPresetTags
 *  net.minecraft.resource.DataConfiguration
 *  net.minecraft.text.Text
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.gen.FlatLevelGeneratorPreset
 *  net.minecraft.world.gen.GeneratorOptions
 *  net.minecraft.world.gen.WorldPreset
 *  net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.text.Text;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class WorldCreator {
    private static final Text NEW_WORLD_NAME = Text.translatable((String)"selectWorld.newWorld");
    private final List<Consumer<WorldCreator>> listeners = new ArrayList();
    private String worldName = NEW_WORLD_NAME.getString();
    private Mode gameMode = Mode.SURVIVAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    private @Nullable Boolean cheatsEnabled;
    private String seed;
    private boolean generateStructures;
    private boolean bonusChestEnabled;
    private final Path savesDirectory;
    private String worldDirectoryName;
    private GeneratorOptionsHolder generatorOptionsHolder;
    private WorldType worldType;
    private final List<WorldType> normalWorldTypes = new ArrayList();
    private final List<WorldType> extendedWorldTypes = new ArrayList();
    private GameRules gameRules;

    public WorldCreator(Path savesDirectory, GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> defaultWorldType, OptionalLong seed) {
        this.savesDirectory = savesDirectory;
        this.generatorOptionsHolder = generatorOptionsHolder;
        this.worldType = new WorldType((RegistryEntry)WorldCreator.getWorldPreset((GeneratorOptionsHolder)generatorOptionsHolder, defaultWorldType).orElse(null));
        this.updateWorldTypeLists();
        this.seed = seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
        this.generateStructures = generatorOptionsHolder.generatorOptions().shouldGenerateStructures();
        this.bonusChestEnabled = generatorOptionsHolder.generatorOptions().hasBonusChest();
        this.worldDirectoryName = this.toDirectoryName(this.worldName);
        this.gameMode = generatorOptionsHolder.initialWorldCreationOptions().selectedGameMode();
        this.gameRules = new GameRules(generatorOptionsHolder.dataConfiguration().enabledFeatures());
        this.gameRules.copyFrom(generatorOptionsHolder.initialWorldCreationOptions().gameRuleOverwrites(), null);
        Optional.ofNullable(generatorOptionsHolder.initialWorldCreationOptions().flatLevelPreset()).flatMap(presetKey -> generatorOptionsHolder.getCombinedRegistryManager().getOptional(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET).flatMap(registry -> registry.getOptional(presetKey))).map(preset -> ((FlatLevelGeneratorPreset)preset.value()).settings()).ifPresent(config -> this.applyModifier(LevelScreenProvider.createModifier((FlatChunkGeneratorConfig)config)));
    }

    public void addListener(Consumer<WorldCreator> listener) {
        this.listeners.add(listener);
    }

    public void update() {
        boolean bl2;
        boolean bl = this.isBonusChestEnabled();
        if (bl != this.generatorOptionsHolder.generatorOptions().hasBonusChest()) {
            this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withBonusChest(bl));
        }
        if ((bl2 = this.shouldGenerateStructures()) != this.generatorOptionsHolder.generatorOptions().shouldGenerateStructures()) {
            this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withStructures(bl2));
        }
        for (Consumer consumer : this.listeners) {
            consumer.accept(this);
        }
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
        this.worldDirectoryName = this.toDirectoryName(worldName);
        this.update();
    }

    private String toDirectoryName(String worldName) {
        String string = worldName.trim();
        try {
            return PathUtil.getNextUniqueName((Path)this.savesDirectory, (String)(!string.isEmpty() ? string : NEW_WORLD_NAME.getString()), (String)"");
        }
        catch (Exception exception) {
            try {
                return PathUtil.getNextUniqueName((Path)this.savesDirectory, (String)"World", (String)"");
            }
            catch (IOException iOException) {
                throw new RuntimeException("Could not create save folder", iOException);
            }
        }
    }

    public String getWorldName() {
        return this.worldName;
    }

    public String getWorldDirectoryName() {
        return this.worldDirectoryName;
    }

    public void setGameMode(Mode gameMode) {
        this.gameMode = gameMode;
        this.update();
    }

    public Mode getGameMode() {
        if (this.isDebug()) {
            return Mode.DEBUG;
        }
        return this.gameMode;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.update();
    }

    public Difficulty getDifficulty() {
        if (this.isHardcore()) {
            return Difficulty.HARD;
        }
        return this.difficulty;
    }

    public boolean isHardcore() {
        return this.getGameMode() == Mode.HARDCORE;
    }

    public void setCheatsEnabled(boolean cheatsEnabled) {
        this.cheatsEnabled = cheatsEnabled;
        this.update();
    }

    public boolean areCheatsEnabled() {
        if (this.isDebug()) {
            return true;
        }
        if (this.isHardcore()) {
            return false;
        }
        if (this.cheatsEnabled == null) {
            return this.getGameMode() == Mode.CREATIVE;
        }
        return this.cheatsEnabled;
    }

    public void setSeed(String seed) {
        this.seed = seed;
        this.generatorOptionsHolder = this.generatorOptionsHolder.apply(options -> options.withSeed(GeneratorOptions.parseSeed((String)this.getSeed())));
        this.update();
    }

    public String getSeed() {
        return this.seed;
    }

    public void setGenerateStructures(boolean generateStructures) {
        this.generateStructures = generateStructures;
        this.update();
    }

    public boolean shouldGenerateStructures() {
        if (this.isDebug()) {
            return false;
        }
        return this.generateStructures;
    }

    public void setBonusChestEnabled(boolean bonusChestEnabled) {
        this.bonusChestEnabled = bonusChestEnabled;
        this.update();
    }

    public boolean isBonusChestEnabled() {
        if (this.isDebug() || this.isHardcore()) {
            return false;
        }
        return this.bonusChestEnabled;
    }

    public void setGeneratorOptionsHolder(GeneratorOptionsHolder generatorOptionsHolder) {
        this.generatorOptionsHolder = generatorOptionsHolder;
        this.updateWorldTypeLists();
        this.update();
    }

    public GeneratorOptionsHolder getGeneratorOptionsHolder() {
        return this.generatorOptionsHolder;
    }

    public void applyModifier(GeneratorOptionsHolder.RegistryAwareModifier modifier) {
        this.generatorOptionsHolder = this.generatorOptionsHolder.apply(modifier);
        this.update();
    }

    protected boolean updateDataConfiguration(DataConfiguration dataConfiguration) {
        DataConfiguration dataConfiguration2 = this.generatorOptionsHolder.dataConfiguration();
        if (dataConfiguration2.dataPacks().getEnabled().equals(dataConfiguration.dataPacks().getEnabled()) && dataConfiguration2.enabledFeatures().equals((Object)dataConfiguration.enabledFeatures())) {
            this.generatorOptionsHolder = new GeneratorOptionsHolder(this.generatorOptionsHolder.generatorOptions(), this.generatorOptionsHolder.dimensionOptionsRegistry(), this.generatorOptionsHolder.selectedDimensions(), this.generatorOptionsHolder.combinedDynamicRegistries(), this.generatorOptionsHolder.dataPackContents(), dataConfiguration, this.generatorOptionsHolder.initialWorldCreationOptions());
            return true;
        }
        return false;
    }

    public boolean isDebug() {
        return this.generatorOptionsHolder.selectedDimensions().isDebug();
    }

    public void setWorldType(WorldType worldType) {
        this.worldType = worldType;
        RegistryEntry registryEntry = worldType.preset();
        if (registryEntry != null) {
            this.applyModifier((registryManager, registryHolder) -> ((WorldPreset)registryEntry.value()).createDimensionsRegistryHolder());
        }
    }

    public WorldType getWorldType() {
        return this.worldType;
    }

    public @Nullable LevelScreenProvider getLevelScreenProvider() {
        RegistryEntry registryEntry = this.getWorldType().preset();
        return registryEntry != null ? (LevelScreenProvider)LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.get(registryEntry.getKey()) : null;
    }

    public List<WorldType> getNormalWorldTypes() {
        return this.normalWorldTypes;
    }

    public List<WorldType> getExtendedWorldTypes() {
        return this.extendedWorldTypes;
    }

    private void updateWorldTypeLists() {
        Registry registry = this.getGeneratorOptionsHolder().getCombinedRegistryManager().getOrThrow(RegistryKeys.WORLD_PRESET);
        this.normalWorldTypes.clear();
        this.normalWorldTypes.addAll(WorldCreator.getWorldPresetList((Registry)registry, (TagKey)WorldPresetTags.NORMAL).orElseGet(() -> registry.streamEntries().map(WorldType::new).toList()));
        this.extendedWorldTypes.clear();
        this.extendedWorldTypes.addAll(WorldCreator.getWorldPresetList((Registry)registry, (TagKey)WorldPresetTags.EXTENDED).orElse(this.normalWorldTypes));
        RegistryEntry registryEntry = this.worldType.preset();
        if (registryEntry != null) {
            boolean bl;
            WorldType worldType = WorldCreator.getWorldPreset((GeneratorOptionsHolder)this.getGeneratorOptionsHolder(), (Optional)registryEntry.getKey()).map(WorldType::new).orElse((WorldType)this.normalWorldTypes.getFirst());
            boolean bl2 = bl = LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.get(registryEntry.getKey()) != null;
            if (bl) {
                this.worldType = worldType;
            } else {
                this.setWorldType(worldType);
            }
        }
    }

    private static Optional<RegistryEntry<WorldPreset>> getWorldPreset(GeneratorOptionsHolder generatorOptionsHolder, Optional<RegistryKey<WorldPreset>> key) {
        return key.flatMap(key2 -> generatorOptionsHolder.getCombinedRegistryManager().getOrThrow(RegistryKeys.WORLD_PRESET).getOptional(key2));
    }

    private static Optional<List<WorldType>> getWorldPresetList(Registry<WorldPreset> registry, TagKey<WorldPreset> tag) {
        return registry.getOptional(tag).map(entryList -> entryList.stream().map(WorldType::new).toList()).filter(worldTypeList -> !worldTypeList.isEmpty());
    }

    public void setGameRules(GameRules gameRules) {
        this.gameRules = gameRules;
        this.update();
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }
}

