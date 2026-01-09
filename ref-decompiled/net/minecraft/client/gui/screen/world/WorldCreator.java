package net.minecraft.client.gui.screen.world;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class WorldCreator {
   private static final Text NEW_WORLD_NAME = Text.translatable("selectWorld.newWorld");
   private final List listeners = new ArrayList();
   private String worldName;
   private Mode gameMode;
   private Difficulty difficulty;
   @Nullable
   private Boolean cheatsEnabled;
   private String seed;
   private boolean generateStructures;
   private boolean bonusChestEnabled;
   private final Path savesDirectory;
   private String worldDirectoryName;
   private GeneratorOptionsHolder generatorOptionsHolder;
   private WorldType worldType;
   private final List normalWorldTypes;
   private final List extendedWorldTypes;
   private GameRules gameRules;

   public WorldCreator(Path savesDirectory, GeneratorOptionsHolder generatorOptionsHolder, Optional defaultWorldType, OptionalLong seed) {
      this.worldName = NEW_WORLD_NAME.getString();
      this.gameMode = WorldCreator.Mode.SURVIVAL;
      this.difficulty = Difficulty.NORMAL;
      this.normalWorldTypes = new ArrayList();
      this.extendedWorldTypes = new ArrayList();
      this.savesDirectory = savesDirectory;
      this.generatorOptionsHolder = generatorOptionsHolder;
      this.worldType = new WorldType((RegistryEntry)getWorldPreset(generatorOptionsHolder, defaultWorldType).orElse((Object)null));
      this.updateWorldTypeLists();
      this.seed = seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
      this.generateStructures = generatorOptionsHolder.generatorOptions().shouldGenerateStructures();
      this.bonusChestEnabled = generatorOptionsHolder.generatorOptions().hasBonusChest();
      this.worldDirectoryName = this.toDirectoryName(this.worldName);
      this.gameMode = generatorOptionsHolder.initialWorldCreationOptions().selectedGameMode();
      this.gameRules = new GameRules(generatorOptionsHolder.dataConfiguration().enabledFeatures());
      generatorOptionsHolder.initialWorldCreationOptions().disabledGameRules().forEach((rule) -> {
         ((GameRules.BooleanRule)this.gameRules.get(rule)).set(false, (MinecraftServer)null);
      });
      Optional.ofNullable(generatorOptionsHolder.initialWorldCreationOptions().flatLevelPreset()).flatMap((presetKey) -> {
         return generatorOptionsHolder.getCombinedRegistryManager().getOptional(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET).flatMap((registry) -> {
            return registry.getOptional(presetKey);
         });
      }).map((preset) -> {
         return ((FlatLevelGeneratorPreset)preset.value()).settings();
      }).ifPresent((config) -> {
         this.applyModifier(LevelScreenProvider.createModifier(config));
      });
   }

   public void addListener(Consumer listener) {
      this.listeners.add(listener);
   }

   public void update() {
      boolean bl = this.isBonusChestEnabled();
      if (bl != this.generatorOptionsHolder.generatorOptions().hasBonusChest()) {
         this.generatorOptionsHolder = this.generatorOptionsHolder.apply((options) -> {
            return options.withBonusChest(bl);
         });
      }

      boolean bl2 = this.shouldGenerateStructures();
      if (bl2 != this.generatorOptionsHolder.generatorOptions().shouldGenerateStructures()) {
         this.generatorOptionsHolder = this.generatorOptionsHolder.apply((options) -> {
            return options.withStructures(bl2);
         });
      }

      Iterator var3 = this.listeners.iterator();

      while(var3.hasNext()) {
         Consumer consumer = (Consumer)var3.next();
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
         return PathUtil.getNextUniqueName(this.savesDirectory, !string.isEmpty() ? string : NEW_WORLD_NAME.getString(), "");
      } catch (Exception var5) {
         try {
            return PathUtil.getNextUniqueName(this.savesDirectory, "World", "");
         } catch (IOException var4) {
            throw new RuntimeException("Could not create save folder", var4);
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
      return this.isDebug() ? WorldCreator.Mode.DEBUG : this.gameMode;
   }

   public void setDifficulty(Difficulty difficulty) {
      this.difficulty = difficulty;
      this.update();
   }

   public Difficulty getDifficulty() {
      return this.isHardcore() ? Difficulty.HARD : this.difficulty;
   }

   public boolean isHardcore() {
      return this.getGameMode() == WorldCreator.Mode.HARDCORE;
   }

   public void setCheatsEnabled(boolean cheatsEnabled) {
      this.cheatsEnabled = cheatsEnabled;
      this.update();
   }

   public boolean areCheatsEnabled() {
      if (this.isDebug()) {
         return true;
      } else if (this.isHardcore()) {
         return false;
      } else if (this.cheatsEnabled == null) {
         return this.getGameMode() == WorldCreator.Mode.CREATIVE;
      } else {
         return this.cheatsEnabled;
      }
   }

   public void setSeed(String seed) {
      this.seed = seed;
      this.generatorOptionsHolder = this.generatorOptionsHolder.apply((options) -> {
         return options.withSeed(GeneratorOptions.parseSeed(this.getSeed()));
      });
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
      return this.isDebug() ? false : this.generateStructures;
   }

   public void setBonusChestEnabled(boolean bonusChestEnabled) {
      this.bonusChestEnabled = bonusChestEnabled;
      this.update();
   }

   public boolean isBonusChestEnabled() {
      return !this.isDebug() && !this.isHardcore() ? this.bonusChestEnabled : false;
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
      if (dataConfiguration2.dataPacks().getEnabled().equals(dataConfiguration.dataPacks().getEnabled()) && dataConfiguration2.enabledFeatures().equals(dataConfiguration.enabledFeatures())) {
         this.generatorOptionsHolder = new GeneratorOptionsHolder(this.generatorOptionsHolder.generatorOptions(), this.generatorOptionsHolder.dimensionOptionsRegistry(), this.generatorOptionsHolder.selectedDimensions(), this.generatorOptionsHolder.combinedDynamicRegistries(), this.generatorOptionsHolder.dataPackContents(), dataConfiguration, this.generatorOptionsHolder.initialWorldCreationOptions());
         return true;
      } else {
         return false;
      }
   }

   public boolean isDebug() {
      return this.generatorOptionsHolder.selectedDimensions().isDebug();
   }

   public void setWorldType(WorldType worldType) {
      this.worldType = worldType;
      RegistryEntry registryEntry = worldType.preset();
      if (registryEntry != null) {
         this.applyModifier((registryManager, registryHolder) -> {
            return ((WorldPreset)registryEntry.value()).createDimensionsRegistryHolder();
         });
      }

   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   @Nullable
   public LevelScreenProvider getLevelScreenProvider() {
      RegistryEntry registryEntry = this.getWorldType().preset();
      return registryEntry != null ? (LevelScreenProvider)LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.get(registryEntry.getKey()) : null;
   }

   public List getNormalWorldTypes() {
      return this.normalWorldTypes;
   }

   public List getExtendedWorldTypes() {
      return this.extendedWorldTypes;
   }

   private void updateWorldTypeLists() {
      Registry registry = this.getGeneratorOptionsHolder().getCombinedRegistryManager().getOrThrow(RegistryKeys.WORLD_PRESET);
      this.normalWorldTypes.clear();
      this.normalWorldTypes.addAll((Collection)getWorldPresetList(registry, WorldPresetTags.NORMAL).orElseGet(() -> {
         return registry.streamEntries().map(WorldType::new).toList();
      }));
      this.extendedWorldTypes.clear();
      this.extendedWorldTypes.addAll((Collection)getWorldPresetList(registry, WorldPresetTags.EXTENDED).orElse(this.normalWorldTypes));
      RegistryEntry registryEntry = this.worldType.preset();
      if (registryEntry != null) {
         WorldType worldType = (WorldType)getWorldPreset(this.getGeneratorOptionsHolder(), registryEntry.getKey()).map(WorldType::new).orElse((WorldType)this.normalWorldTypes.getFirst());
         boolean bl = LevelScreenProvider.WORLD_PRESET_TO_SCREEN_PROVIDER.get(registryEntry.getKey()) != null;
         if (bl) {
            this.worldType = worldType;
         } else {
            this.setWorldType(worldType);
         }
      }

   }

   private static Optional getWorldPreset(GeneratorOptionsHolder generatorOptionsHolder, Optional key) {
      return key.flatMap((key2) -> {
         return generatorOptionsHolder.getCombinedRegistryManager().getOrThrow(RegistryKeys.WORLD_PRESET).getOptional(key2);
      });
   }

   private static Optional getWorldPresetList(Registry registry, TagKey tag) {
      return registry.getOptional(tag).map((entryList) -> {
         return entryList.stream().map(WorldType::new).toList();
      }).filter((worldTypeList) -> {
         return !worldTypeList.isEmpty();
      });
   }

   public void setGameRules(GameRules gameRules) {
      this.gameRules = gameRules;
      this.update();
   }

   public GameRules getGameRules() {
      return this.gameRules;
   }

   @Environment(EnvType.CLIENT)
   public static enum Mode {
      SURVIVAL("survival", GameMode.SURVIVAL),
      HARDCORE("hardcore", GameMode.SURVIVAL),
      CREATIVE("creative", GameMode.CREATIVE),
      DEBUG("spectator", GameMode.SPECTATOR);

      public final GameMode defaultGameMode;
      public final Text name;
      private final Text info;

      private Mode(final String name, final GameMode defaultGameMode) {
         this.defaultGameMode = defaultGameMode;
         this.name = Text.translatable("selectWorld.gameMode." + name);
         this.info = Text.translatable("selectWorld.gameMode." + name + ".info");
      }

      public Text getInfo() {
         return this.info;
      }

      // $FF: synthetic method
      private static Mode[] method_36891() {
         return new Mode[]{SURVIVAL, HARDCORE, CREATIVE, DEBUG};
      }
   }

   @Environment(EnvType.CLIENT)
   public static record WorldType(@Nullable RegistryEntry preset) {
      private static final Text CUSTOM_GENERATOR_TEXT = Text.translatable("generator.custom");

      public WorldType(@Nullable RegistryEntry registryEntry) {
         this.preset = registryEntry;
      }

      public Text getName() {
         return (Text)Optional.ofNullable(this.preset).flatMap(RegistryEntry::getKey).map((key) -> {
            return Text.translatable(key.getValue().toTranslationKey("generator"));
         }).orElse(CUSTOM_GENERATOR_TEXT);
      }

      public boolean isAmplified() {
         return Optional.ofNullable(this.preset).flatMap(RegistryEntry::getKey).filter((key) -> {
            return key.equals(WorldPresets.AMPLIFIED);
         }).isPresent();
      }

      @Nullable
      public RegistryEntry preset() {
         return this.preset;
      }
   }
}
