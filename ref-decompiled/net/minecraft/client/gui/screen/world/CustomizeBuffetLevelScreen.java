/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen
 *  net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen$BuffetBiomesListWidget
 *  net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen$BuffetBiomesListWidget$BuffetBiomeItem
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.world.GeneratorOptionsHolder
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.biome.BiomeKeys
 */
package net.minecraft.client.gui.screen.world;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private static final Text SEARCH_TEXT = Text.translatable((String)"createWorld.customize.buffet.search").fillStyle(TextFieldWidget.SEARCH_STYLE);
    private static final int field_49494 = 3;
    private static final int field_64199 = 15;
    final ThreePartsLayoutWidget layout;
    private final Screen parent;
    private final Consumer<RegistryEntry<Biome>> onDone;
    final Registry<Biome> biomeRegistry;
    private BuffetBiomesListWidget biomeSelectionList;
    RegistryEntry<Biome> biome;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(Screen parent, GeneratorOptionsHolder generatorOptionsHolder, Consumer<RegistryEntry<Biome>> onDone) {
        super((Text)Text.translatable((String)"createWorld.customize.buffet.title"));
        this.parent = parent;
        this.onDone = onDone;
        Objects.requireNonNull(this.textRenderer);
        this.layout = new ThreePartsLayoutWidget((Screen)this, 13 + 9 + 3 + 15, 33);
        this.biomeRegistry = generatorOptionsHolder.getCombinedRegistryManager().getOrThrow(RegistryKeys.BIOME);
        RegistryEntry registryEntry = (RegistryEntry)this.biomeRegistry.getOptional(BiomeKeys.PLAINS).or(() -> this.biomeRegistry.streamEntries().findAny()).orElseThrow();
        this.biome = generatorOptionsHolder.selectedDimensions().getChunkGenerator().getBiomeSource().getBiomes().stream().findFirst().orElse(registryEntry);
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(3));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.getTitle(), this.textRenderer));
        TextFieldWidget textFieldWidget = (TextFieldWidget)directionalLayoutWidget.add((Widget)new TextFieldWidget(this.textRenderer, 200, 15, (Text)Text.empty()));
        BuffetBiomesListWidget buffetBiomesListWidget = new BuffetBiomesListWidget(this);
        textFieldWidget.setPlaceholder(SEARCH_TEXT);
        textFieldWidget.setChangedListener(arg_0 -> ((BuffetBiomesListWidget)buffetBiomesListWidget).onSearch(arg_0));
        this.biomeSelectionList = (BuffetBiomesListWidget)this.layout.addBody((Widget)buffetBiomesListWidget);
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        this.confirmButton = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> {
            this.onDone.accept(this.biome);
            this.close();
        }).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).build());
        this.biomeSelectionList.setSelected((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.children().stream().filter(entry -> Objects.equals(entry.biome, this.biome)).findFirst().orElse(null));
        this.layout.forEachChild(arg_0 -> ((CustomizeBuffetLevelScreen)this).addDrawableChild(arg_0));
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        this.biomeSelectionList.position(this.width, this.layout);
    }

    void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelectedOrNull() != null;
    }
}

