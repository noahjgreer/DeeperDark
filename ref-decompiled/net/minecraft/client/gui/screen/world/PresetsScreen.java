package net.minecraft.client.gui.screen.world;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
   static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("container/slot");
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int ICON_SIZE = 18;
   private static final int BUTTON_HEIGHT = 20;
   private static final int ICON_BACKGROUND_OFFSET_X = 1;
   private static final int ICON_BACKGROUND_OFFSET_Y = 1;
   private static final int ICON_OFFSET_X = 2;
   private static final int ICON_OFFSET_Y = 2;
   private static final RegistryKey BIOME_KEY;
   public static final Text UNKNOWN_PRESET_TEXT;
   private final CustomizeFlatLevelScreen parent;
   private Text shareText;
   private Text listText;
   private SuperflatPresetsListWidget listWidget;
   private ButtonWidget selectPresetButton;
   TextFieldWidget customPresetField;
   FlatChunkGeneratorConfig config;

   public PresetsScreen(CustomizeFlatLevelScreen parent) {
      super(Text.translatable("createWorld.customize.presets.title"));
      this.parent = parent;
   }

   @Nullable
   private static FlatChunkGeneratorLayer parseLayerString(RegistryEntryLookup blockLookup, String layer, int layerStartHeight) {
      List list = Splitter.on('*').limit(2).splitToList(layer);
      int i;
      String string;
      if (list.size() == 2) {
         string = (String)list.get(1);

         try {
            i = Math.max(Integer.parseInt((String)list.get(0)), 0);
         } catch (NumberFormatException var11) {
            LOGGER.error("Error while parsing flat world string", var11);
            return null;
         }
      } else {
         string = (String)list.get(0);
         i = 1;
      }

      int j = Math.min(layerStartHeight + i, DimensionType.MAX_HEIGHT);
      int k = j - layerStartHeight;

      Optional optional;
      try {
         optional = blockLookup.getOptional(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(string)));
      } catch (Exception var10) {
         LOGGER.error("Error while parsing flat world string", var10);
         return null;
      }

      if (optional.isEmpty()) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", string);
         return null;
      } else {
         return new FlatChunkGeneratorLayer(k, (Block)((RegistryEntry.Reference)optional.get()).value());
      }
   }

   private static List parsePresetLayersString(RegistryEntryLookup blockLookup, String layers) {
      List list = Lists.newArrayList();
      String[] strings = layers.split(",");
      int i = 0;
      String[] var5 = strings;
      int var6 = strings.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String string = var5[var7];
         FlatChunkGeneratorLayer flatChunkGeneratorLayer = parseLayerString(blockLookup, string, i);
         if (flatChunkGeneratorLayer == null) {
            return Collections.emptyList();
         }

         int j = DimensionType.MAX_HEIGHT - i;
         if (j > 0) {
            list.add(flatChunkGeneratorLayer.withMaxThickness(j));
            i += flatChunkGeneratorLayer.getThickness();
         }
      }

      return list;
   }

   public static FlatChunkGeneratorConfig parsePresetString(RegistryEntryLookup blockLookup, RegistryEntryLookup biomeLookup, RegistryEntryLookup structureSetLookup, RegistryEntryLookup placedFeatureLookup, String preset, FlatChunkGeneratorConfig config) {
      Iterator iterator = Splitter.on(';').split(preset).iterator();
      if (!iterator.hasNext()) {
         return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
      } else {
         List list = parsePresetLayersString(blockLookup, (String)iterator.next());
         if (list.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
         } else {
            RegistryEntry.Reference reference = biomeLookup.getOrThrow(BIOME_KEY);
            RegistryEntry registryEntry = reference;
            if (iterator.hasNext()) {
               String string = (String)iterator.next();
               Optional var10000 = Optional.ofNullable(Identifier.tryParse(string)).map((biomeId) -> {
                  return RegistryKey.of(RegistryKeys.BIOME, biomeId);
               });
               Objects.requireNonNull(biomeLookup);
               registryEntry = (RegistryEntry)var10000.flatMap(biomeLookup::getOptional).orElseGet(() -> {
                  LOGGER.warn("Invalid biome: {}", string);
                  return reference;
               });
            }

            return config.with(list, config.getStructureOverrides(), (RegistryEntry)registryEntry);
         }
      }
   }

   static String getGeneratorConfigString(FlatChunkGeneratorConfig config) {
      StringBuilder stringBuilder = new StringBuilder();

      for(int i = 0; i < config.getLayers().size(); ++i) {
         if (i > 0) {
            stringBuilder.append(",");
         }

         stringBuilder.append(config.getLayers().get(i));
      }

      stringBuilder.append(";");
      stringBuilder.append(config.getBiome().getKey().map(RegistryKey::getValue).orElseThrow(() -> {
         return new IllegalStateException("Biome not registered");
      }));
      return stringBuilder.toString();
   }

   protected void init() {
      this.shareText = Text.translatable("createWorld.customize.presets.share");
      this.listText = Text.translatable("createWorld.customize.presets.list");
      this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
      this.customPresetField.setMaxLength(1230);
      GeneratorOptionsHolder generatorOptionsHolder = this.parent.parent.getWorldCreator().getGeneratorOptionsHolder();
      DynamicRegistryManager dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
      FeatureSet featureSet = generatorOptionsHolder.dataConfiguration().enabledFeatures();
      RegistryEntryLookup registryEntryLookup = dynamicRegistryManager.getOrThrow(RegistryKeys.BIOME);
      RegistryEntryLookup registryEntryLookup2 = dynamicRegistryManager.getOrThrow(RegistryKeys.STRUCTURE_SET);
      RegistryEntryLookup registryEntryLookup3 = dynamicRegistryManager.getOrThrow(RegistryKeys.PLACED_FEATURE);
      RegistryEntryLookup registryEntryLookup4 = dynamicRegistryManager.getOrThrow(RegistryKeys.BLOCK).withFeatureFilter(featureSet);
      this.customPresetField.setText(getGeneratorConfigString(this.parent.getConfig()));
      this.config = this.parent.getConfig();
      this.addSelectableChild(this.customPresetField);
      this.listWidget = (SuperflatPresetsListWidget)this.addDrawableChild(new SuperflatPresetsListWidget(dynamicRegistryManager, featureSet));
      this.selectPresetButton = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("createWorld.customize.presets.select"), (buttonWidget) -> {
         FlatChunkGeneratorConfig flatChunkGeneratorConfig = parsePresetString(registryEntryLookup4, registryEntryLookup, registryEntryLookup2, registryEntryLookup3, this.customPresetField.getText(), this.config);
         this.parent.setConfig(flatChunkGeneratorConfig);
         this.client.setScreen(this.parent);
      }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build());
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.client.setScreen(this.parent);
      }).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
      this.updateSelectButton(this.listWidget.getSelectedOrNull() != null);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.listWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public void resize(MinecraftClient client, int width, int height) {
      String string = this.customPresetField.getText();
      this.init(client, width, height);
      this.customPresetField.setText(string);
   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 8, -1);
      context.drawTextWithShadow(this.textRenderer, (Text)this.shareText, 51, 30, -6250336);
      context.drawTextWithShadow(this.textRenderer, (Text)this.listText, 51, 68, -6250336);
      this.customPresetField.render(context, mouseX, mouseY, deltaTicks);
   }

   public void updateSelectButton(boolean hasSelected) {
      this.selectPresetButton.active = hasSelected || this.customPresetField.getText().length() > 1;
   }

   static {
      BIOME_KEY = BiomeKeys.PLAINS;
      UNKNOWN_PRESET_TEXT = Text.translatable("flat_world_preset.unknown");
   }

   @Environment(EnvType.CLIENT)
   private class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget {
      public SuperflatPresetsListWidget(final DynamicRegistryManager dynamicRegistryManager, final FeatureSet featureSet) {
         super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height - 117, 80, 24);
         Iterator var4 = dynamicRegistryManager.getOrThrow(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET).iterateEntries(FlatLevelGeneratorPresetTags.VISIBLE).iterator();

         while(var4.hasNext()) {
            RegistryEntry registryEntry = (RegistryEntry)var4.next();
            Set set = (Set)((FlatLevelGeneratorPreset)registryEntry.value()).settings().getLayers().stream().map((layer) -> {
               return layer.getBlockState().getBlock();
            }).filter((block) -> {
               return !block.isEnabled(featureSet);
            }).collect(Collectors.toSet());
            if (!set.isEmpty()) {
               PresetsScreen.LOGGER.info("Discarding flat world preset {} since it contains experimental blocks {}", registryEntry.getKey().map((key) -> {
                  return key.getValue().toString();
               }).orElse("<unknown>"), set);
            } else {
               this.addEntry(new SuperflatPresetEntry(registryEntry));
            }
         }

      }

      public void setSelected(@Nullable SuperflatPresetEntry superflatPresetEntry) {
         super.setSelected(superflatPresetEntry);
         PresetsScreen.this.updateSelectButton(superflatPresetEntry != null);
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
         } else {
            if (KeyCodes.isToggle(keyCode) && this.getSelectedOrNull() != null) {
               ((SuperflatPresetEntry)this.getSelectedOrNull()).setPreset();
            }

            return false;
         }
      }

      @Environment(EnvType.CLIENT)
      public class SuperflatPresetEntry extends AlwaysSelectedEntryListWidget.Entry {
         private static final Identifier STATS_ICONS_TEXTURE = Identifier.ofVanilla("textures/gui/container/stats_icons.png");
         private final FlatLevelGeneratorPreset preset;
         private final Text text;

         public SuperflatPresetEntry(final RegistryEntry preset) {
            this.preset = (FlatLevelGeneratorPreset)preset.value();
            this.text = (Text)preset.getKey().map((key) -> {
               return Text.translatable(key.getValue().toTranslationKey("flat_world_preset"));
            }).orElse(PresetsScreen.UNKNOWN_PRESET_TEXT);
         }

         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            this.renderIcon(context, x, y, (Item)this.preset.displayItem().value());
            context.drawTextWithShadow(PresetsScreen.this.textRenderer, (Text)this.text, x + 18 + 5, y + 6, -1);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.setPreset();
            return super.mouseClicked(mouseX, mouseY, button);
         }

         void setPreset() {
            SuperflatPresetsListWidget.this.setSelected(this);
            PresetsScreen.this.config = this.preset.settings();
            PresetsScreen.this.customPresetField.setText(PresetsScreen.getGeneratorConfigString(PresetsScreen.this.config));
            PresetsScreen.this.customPresetField.setCursorToStart(false);
         }

         private void renderIcon(DrawContext context, int x, int y, Item iconItem) {
            this.drawIconBackground(context, x + 1, y + 1);
            context.drawItemWithoutEntity(new ItemStack(iconItem), x + 2, y + 2);
         }

         private void drawIconBackground(DrawContext context, int x, int y) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PresetsScreen.SLOT_TEXTURE, x, y, 18, 18);
         }

         public Text getNarration() {
            return Text.translatable("narrator.select", this.text);
         }
      }
   }
}
