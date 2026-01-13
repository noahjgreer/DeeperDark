/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.screen.world.SelectWorldScreen
 *  net.minecraft.client.gui.screen.world.WorldListWidget
 *  net.minecraft.client.gui.screen.world.WorldListWidget$Builder
 *  net.minecraft.client.gui.screen.world.WorldListWidget$Entry
 *  net.minecraft.client.gui.screen.world.WorldListWidget$WorldEntry
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.resource.DataConfiguration
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.GameMode
 *  net.minecraft.world.gen.GeneratorOptions
 *  net.minecraft.world.gen.WorldPresets
 *  net.minecraft.world.level.LevelInfo
 *  net.minecraft.world.level.storage.LevelSummary
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SelectWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final GeneratorOptions DEBUG_GENERATOR_OPTIONS = new GeneratorOptions((long)"test1".hashCode(), true, false);
    protected final Screen parent;
    private final ThreePartsLayoutWidget layout;
    private @Nullable ButtonWidget deleteButton;
    private @Nullable ButtonWidget selectButton;
    private @Nullable ButtonWidget editButton;
    private @Nullable ButtonWidget recreateButton;
    protected @Nullable TextFieldWidget searchBox;
    private @Nullable WorldListWidget levelList;

    public SelectWorldScreen(Screen parent) {
        super((Text)Text.translatable((String)"selectWorld.title"));
        Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
        this.layout = new ThreePartsLayoutWidget((Screen)this, 8 + 9 + 8 + 20 + 4, 60);
        this.parent = parent;
    }

    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.title, this.textRenderer));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal().spacing(4));
        if (SharedConstants.WORLD_RECREATE) {
            directionalLayoutWidget2.add((Widget)this.createDebugRecreateButton());
        }
        this.searchBox = (TextFieldWidget)directionalLayoutWidget2.add((Widget)new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, (Text)Text.translatable((String)"selectWorld.search")));
        this.searchBox.setChangedListener(search -> {
            if (this.levelList != null) {
                this.levelList.setSearch(search);
            }
        });
        this.searchBox.setPlaceholder((Text)Text.translatable((String)"gui.selectWorld.search").setStyle(TextFieldWidget.SEARCH_STYLE));
        Consumer<WorldListWidget.WorldEntry> consumer = WorldListWidget.WorldEntry::play;
        this.levelList = (WorldListWidget)this.layout.addBody((Widget)new WorldListWidget.Builder(this.client, (Screen)this).width(this.width).height(this.layout.getContentHeight()).search(this.searchBox.getText()).predecessor(this.levelList).selectionCallback(arg_0 -> this.worldSelected(arg_0)).confirmationCallback(consumer).toWidget());
        this.addButtons(consumer, this.levelList);
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
        this.worldSelected(null);
    }

    private void addButtons(Consumer<WorldListWidget.WorldEntry> playAction, WorldListWidget levelList) {
        GridWidget gridWidget = (GridWidget)this.layout.addFooter((Widget)new GridWidget().setColumnSpacing(8).setRowSpacing(4));
        gridWidget.getMainPositioner().alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(4);
        this.selectButton = (ButtonWidget)adder.add((Widget)ButtonWidget.builder((Text)LevelSummary.SELECT_WORLD_TEXT, button -> levelList.getSelectedAsOptional().ifPresent(playAction)).build(), 2);
        adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectWorld.create"), button -> CreateWorldScreen.show((MinecraftClient)this.client, () -> ((WorldListWidget)levelList).refresh())).build(), 2);
        this.editButton = (ButtonWidget)adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectWorld.edit"), button -> levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::edit)).width(71).build());
        this.deleteButton = (ButtonWidget)adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectWorld.delete"), button -> levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::deleteIfConfirmed)).width(71).build());
        this.recreateButton = (ButtonWidget)adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"selectWorld.recreate"), button -> levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::recreate)).width(71).build());
        adder.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).width(71).build());
    }

    private ButtonWidget createDebugRecreateButton() {
        return ButtonWidget.builder((Text)Text.literal((String)"DEBUG recreate"), button -> {
            try {
                WorldListWidget.WorldEntry worldEntry;
                WorldListWidget.Entry entry;
                String string = "DEBUG world";
                if (this.levelList != null && !this.levelList.children().isEmpty() && (entry = (WorldListWidget.Entry)this.levelList.children().getFirst()) instanceof WorldListWidget.WorldEntry && (worldEntry = (WorldListWidget.WorldEntry)entry).getLevelDisplayName().equals("DEBUG world")) {
                    worldEntry.delete();
                }
                LevelInfo levelInfo = new LevelInfo("DEBUG world", GameMode.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(DataConfiguration.SAFE_MODE.enabledFeatures()), DataConfiguration.SAFE_MODE);
                String string2 = PathUtil.getNextUniqueName((Path)this.client.getLevelStorage().getSavesDirectory(), (String)"DEBUG world", (String)"");
                this.client.createIntegratedServerLoader().createAndStart(string2, levelInfo, DEBUG_GENERATOR_OPTIONS, WorldPresets::createDemoOptions, (Screen)this);
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to recreate the debug world", (Throwable)iOException);
            }
        }).width(72).build();
    }

    protected void refreshWidgetPositions() {
        if (this.levelList != null) {
            this.levelList.position(this.width, this.layout);
        }
        this.layout.refreshPositions();
    }

    protected void setInitialFocus() {
        if (this.searchBox != null) {
            this.setInitialFocus((Element)this.searchBox);
        }
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public void worldSelected(@Nullable LevelSummary levelSummary) {
        if (this.selectButton == null || this.editButton == null || this.recreateButton == null || this.deleteButton == null) {
            return;
        }
        if (levelSummary == null) {
            this.selectButton.setMessage(LevelSummary.SELECT_WORLD_TEXT);
            this.selectButton.active = false;
            this.editButton.active = false;
            this.recreateButton.active = false;
            this.deleteButton.active = false;
        } else {
            this.selectButton.setMessage(levelSummary.getSelectWorldText());
            this.selectButton.active = levelSummary.isSelectable();
            this.editButton.active = levelSummary.isEditable();
            this.recreateButton.active = levelSummary.isRecreatable();
            this.deleteButton.active = levelSummary.isDeletable();
        }
    }

    public void removed() {
        if (this.levelList != null) {
            this.levelList.children().forEach(WorldListWidget.Entry::close);
        }
    }
}

