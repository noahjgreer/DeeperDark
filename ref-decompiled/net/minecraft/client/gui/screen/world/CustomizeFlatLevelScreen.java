/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen
 *  net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen$SuperflatLayersListWidget
 *  net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen$SuperflatLayersListWidget$SuperflatLayerEntry
 *  net.minecraft.client.gui.screen.world.PresetsScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.world.PresetsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeFlatLevelScreen
extends Screen {
    private static final Text TITLE = Text.translatable((String)"createWorld.customize.flat.title");
    static final Identifier SLOT_TEXTURE = Identifier.ofVanilla((String)"container/slot");
    private static final int ICON_SIZE = 18;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ICON_BACKGROUND_OFFSET_X = 1;
    private static final int ICON_BACKGROUND_OFFSET_Y = 1;
    private static final int ICON_OFFSET_X = 2;
    private static final int ICON_OFFSET_Y = 2;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this, 33, 64);
    protected final CreateWorldScreen parent;
    private final Consumer<FlatChunkGeneratorConfig> configConsumer;
    FlatChunkGeneratorConfig config;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable SuperflatLayersListWidget layers;
    private @Nullable ButtonWidget widgetButtonRemoveLayer;

    public CustomizeFlatLevelScreen(CreateWorldScreen parent, Consumer<FlatChunkGeneratorConfig> configConsumer, FlatChunkGeneratorConfig config) {
        super(TITLE);
        this.parent = parent;
        this.configConsumer = configConsumer;
        this.config = config;
    }

    public FlatChunkGeneratorConfig getConfig() {
        return this.config;
    }

    public void setConfig(FlatChunkGeneratorConfig config) {
        this.config = config;
        if (this.layers != null) {
            this.layers.updateLayers();
            this.updateRemoveLayerButton();
        }
    }

    protected void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        this.layers = (SuperflatLayersListWidget)this.layout.addBody((Widget)new SuperflatLayersListWidget(this));
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        DirectionalLayoutWidget directionalLayoutWidget3 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        this.widgetButtonRemoveLayer = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"createWorld.customize.flat.removeLayer"), button -> {
            EntryListWidget.Entry entry;
            if (this.layers != null && (entry = this.layers.getSelectedOrNull()) instanceof SuperflatLayersListWidget.SuperflatLayerEntry) {
                SuperflatLayersListWidget.SuperflatLayerEntry superflatLayerEntry = (SuperflatLayersListWidget.SuperflatLayerEntry)entry;
                this.layers.removeLayer(superflatLayerEntry);
            }
        }).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"createWorld.customize.presets"), button -> {
            this.client.setScreen((Screen)new PresetsScreen(this));
            this.config.updateLayerBlocks();
            this.updateRemoveLayerButton();
        }).build());
        directionalLayoutWidget3.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> {
            this.configConsumer.accept(this.config);
            this.close();
            this.config.updateLayerBlocks();
        }).build());
        directionalLayoutWidget3.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> {
            this.close();
            this.config.updateLayerBlocks();
        }).build());
        this.config.updateLayerBlocks();
        this.updateRemoveLayerButton();
        this.layout.forEachChild(arg_0 -> ((CustomizeFlatLevelScreen)this).addDrawableChild(arg_0));
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        if (this.layers != null) {
            this.layers.position(this.width, this.layout);
        }
        this.layout.refreshPositions();
    }

    void updateRemoveLayerButton() {
        if (this.widgetButtonRemoveLayer != null) {
            this.widgetButtonRemoveLayer.active = this.hasLayerSelected();
        }
    }

    private boolean hasLayerSelected() {
        return this.layers != null && this.layers.getSelectedOrNull() instanceof SuperflatLayersListWidget.SuperflatLayerEntry;
    }

    public void close() {
        this.client.setScreen((Screen)this.parent);
    }
}

