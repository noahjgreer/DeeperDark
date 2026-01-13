/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen
 *  net.minecraft.client.gui.screen.world.PresetsScreen
 *  net.minecraft.client.gui.screen.world.PresetsScreen$SuperflatPresetsListWidget
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.world.GeneratorOptionsHolder
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryEntryLookup
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.RegistryWrapper$Impl
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.structure.StructureSet
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.biome.BiomeKeys
 *  net.minecraft.world.dimension.DimensionType
 *  net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
 *  net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer
 *  net.minecraft.world.gen.feature.PlacedFeature
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.world.PresetsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.structure.StructureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PresetsScreen
extends Screen {
    static final Identifier SLOT_TEXTURE = Identifier.ofVanilla((String)"container/slot");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int ICON_SIZE = 18;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ICON_BACKGROUND_OFFSET_X = 1;
    private static final int ICON_BACKGROUND_OFFSET_Y = 1;
    private static final int ICON_OFFSET_X = 2;
    private static final int ICON_OFFSET_Y = 2;
    private static final RegistryKey<Biome> BIOME_KEY = BiomeKeys.PLAINS;
    public static final Text UNKNOWN_PRESET_TEXT = Text.translatable((String)"flat_world_preset.unknown");
    private final CustomizeFlatLevelScreen parent;
    private Text shareText;
    private Text listText;
    private SuperflatPresetsListWidget listWidget;
    private ButtonWidget selectPresetButton;
    TextFieldWidget customPresetField;
    FlatChunkGeneratorConfig config;

    public PresetsScreen(CustomizeFlatLevelScreen parent) {
        super((Text)Text.translatable((String)"createWorld.customize.presets.title"));
        this.parent = parent;
    }

    private static @Nullable FlatChunkGeneratorLayer parseLayerString(RegistryEntryLookup<Block> blockLookup, String layer, int layerStartHeight) {
        Optional optional;
        int i;
        String string;
        List list = Splitter.on((char)'*').limit(2).splitToList((CharSequence)layer);
        if (list.size() == 2) {
            string = (String)list.get(1);
            try {
                i = Math.max(Integer.parseInt((String)list.get(0)), 0);
            }
            catch (NumberFormatException numberFormatException) {
                LOGGER.error("Error while parsing flat world string", (Throwable)numberFormatException);
                return null;
            }
        } else {
            string = (String)list.get(0);
            i = 1;
        }
        int j = Math.min(layerStartHeight + i, DimensionType.MAX_HEIGHT);
        int k = j - layerStartHeight;
        try {
            optional = blockLookup.getOptional(RegistryKey.of((RegistryKey)RegistryKeys.BLOCK, (Identifier)Identifier.of((String)string)));
        }
        catch (Exception exception) {
            LOGGER.error("Error while parsing flat world string", (Throwable)exception);
            return null;
        }
        if (optional.isEmpty()) {
            LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)string);
            return null;
        }
        return new FlatChunkGeneratorLayer(k, (Block)((RegistryEntry.Reference)optional.get()).value());
    }

    private static List<FlatChunkGeneratorLayer> parsePresetLayersString(RegistryEntryLookup<Block> blockLookup, String layers) {
        ArrayList list = Lists.newArrayList();
        String[] strings = layers.split(",");
        int i = 0;
        for (String string : strings) {
            FlatChunkGeneratorLayer flatChunkGeneratorLayer = PresetsScreen.parseLayerString(blockLookup, (String)string, (int)i);
            if (flatChunkGeneratorLayer == null) {
                return Collections.emptyList();
            }
            int j = DimensionType.MAX_HEIGHT - i;
            if (j <= 0) continue;
            list.add(flatChunkGeneratorLayer.withMaxThickness(j));
            i += flatChunkGeneratorLayer.getThickness();
        }
        return list;
    }

    public static FlatChunkGeneratorConfig parsePresetString(RegistryEntryLookup<Block> blockLookup, RegistryEntryLookup<Biome> biomeLookup, RegistryEntryLookup<StructureSet> structureSetLookup, RegistryEntryLookup<PlacedFeature> placedFeatureLookup, String preset, FlatChunkGeneratorConfig config) {
        RegistryEntry.Reference reference;
        Iterator iterator = Splitter.on((char)';').split((CharSequence)preset).iterator();
        if (!iterator.hasNext()) {
            return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
        }
        List list = PresetsScreen.parsePresetLayersString(blockLookup, (String)((String)iterator.next()));
        if (list.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig(biomeLookup, structureSetLookup, placedFeatureLookup);
        }
        RegistryEntry.Reference registryEntry = reference = biomeLookup.getOrThrow(BIOME_KEY);
        if (iterator.hasNext()) {
            String string = (String)iterator.next();
            registryEntry = (RegistryEntry)Optional.ofNullable(Identifier.tryParse((String)string)).map(biomeId -> RegistryKey.of((RegistryKey)RegistryKeys.BIOME, (Identifier)biomeId)).flatMap(arg_0 -> biomeLookup.getOptional(arg_0)).orElseGet(() -> {
                LOGGER.warn("Invalid biome: {}", (Object)string);
                return reference;
            });
        }
        return config.with(list, config.getStructureOverrides(), (RegistryEntry)registryEntry);
    }

    static String getGeneratorConfigString(FlatChunkGeneratorConfig config) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < config.getLayers().size(); ++i) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(config.getLayers().get(i));
        }
        stringBuilder.append(";");
        stringBuilder.append(config.getBiome().getKey().map(RegistryKey::getValue).orElseThrow(() -> new IllegalStateException("Biome not registered")));
        return stringBuilder.toString();
    }

    protected void init() {
        this.shareText = Text.translatable((String)"createWorld.customize.presets.share");
        this.listText = Text.translatable((String)"createWorld.customize.presets.list");
        this.customPresetField = new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, this.shareText);
        this.customPresetField.setMaxLength(1230);
        GeneratorOptionsHolder generatorOptionsHolder = this.parent.parent.getWorldCreator().getGeneratorOptionsHolder();
        DynamicRegistryManager.Immutable dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
        FeatureSet featureSet = generatorOptionsHolder.dataConfiguration().enabledFeatures();
        Registry registryEntryLookup = dynamicRegistryManager.getOrThrow(RegistryKeys.BIOME);
        Registry registryEntryLookup2 = dynamicRegistryManager.getOrThrow(RegistryKeys.STRUCTURE_SET);
        Registry registryEntryLookup3 = dynamicRegistryManager.getOrThrow(RegistryKeys.PLACED_FEATURE);
        RegistryWrapper.Impl registryEntryLookup4 = dynamicRegistryManager.getOrThrow(RegistryKeys.BLOCK).withFeatureFilter(featureSet);
        this.customPresetField.setText(PresetsScreen.getGeneratorConfigString((FlatChunkGeneratorConfig)this.parent.getConfig()));
        this.config = this.parent.getConfig();
        this.addSelectableChild((Element)this.customPresetField);
        this.listWidget = (SuperflatPresetsListWidget)this.addDrawableChild((Element)new SuperflatPresetsListWidget(this, (DynamicRegistryManager)dynamicRegistryManager, featureSet));
        this.selectPresetButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"createWorld.customize.presets.select"), arg_0 -> this.method_19847((RegistryEntryLookup)registryEntryLookup4, (RegistryEntryLookup)registryEntryLookup, (RegistryEntryLookup)registryEntryLookup2, (RegistryEntryLookup)registryEntryLookup3, arg_0)).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.client.setScreen((Screen)this.parent)).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.updateSelectButton(this.listWidget.getSelectedOrNull() != null);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return this.listWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void resize(int width, int height) {
        String string = this.customPresetField.getText();
        this.init(width, height);
        this.customPresetField.setText(string);
    }

    public void close() {
        this.client.setScreen((Screen)this.parent);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, -1);
        context.drawTextWithShadow(this.textRenderer, this.shareText, 51, 30, -6250336);
        context.drawTextWithShadow(this.textRenderer, this.listText, 51, 68, -6250336);
        this.customPresetField.render(context, mouseX, mouseY, deltaTicks);
    }

    public void updateSelectButton(boolean hasSelected) {
        this.selectPresetButton.active = hasSelected || this.customPresetField.getText().length() > 1;
    }

    private /* synthetic */ void method_19847(RegistryEntryLookup registryEntryLookup, RegistryEntryLookup registryEntryLookup2, RegistryEntryLookup registryEntryLookup3, RegistryEntryLookup registryEntryLookup4, ButtonWidget buttonWidget) {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = PresetsScreen.parsePresetString((RegistryEntryLookup)registryEntryLookup, (RegistryEntryLookup)registryEntryLookup2, (RegistryEntryLookup)registryEntryLookup3, (RegistryEntryLookup)registryEntryLookup4, (String)this.customPresetField.getText(), (FlatChunkGeneratorConfig)this.config);
        this.parent.setConfig(flatChunkGeneratorConfig);
        this.client.setScreen((Screen)this.parent);
    }
}

