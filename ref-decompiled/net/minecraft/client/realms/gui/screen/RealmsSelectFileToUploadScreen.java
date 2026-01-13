/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.WorldListWidget
 *  net.minecraft.client.gui.screen.world.WorldListWidget$Builder
 *  net.minecraft.client.gui.screen.world.WorldListWidget$WorldEntry
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSelectFileToUploadScreen
 *  net.minecraft.client.realms.gui.screen.RealmsUploadScreen
 *  net.minecraft.client.realms.task.WorldCreationTask
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.world.level.storage.LevelSummary
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsUploadScreen;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Text TITLE = Text.translatable((String)"mco.upload.select.world.title");
    private static final Text LOADING_ERROR_TEXT = Text.translatable((String)"selectWorld.unable_to_load");
    private final @Nullable WorldCreationTask creationTask;
    private final RealmsCreateWorldScreen parent;
    private final long worldId;
    private final int slotId;
    private final ThreePartsLayoutWidget field_62099;
    protected @Nullable TextFieldWidget field_62100;
    private @Nullable WorldListWidget worldSelectionList;
    private @Nullable ButtonWidget uploadButton;

    public RealmsSelectFileToUploadScreen(@Nullable WorldCreationTask creationTask, long worldId, int slotId, RealmsCreateWorldScreen parent) {
        super(TITLE);
        Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
        this.field_62099 = new ThreePartsLayoutWidget((Screen)this, 8 + 9 + 8 + 20 + 4, 33);
        this.creationTask = creationTask;
        this.parent = parent;
        this.worldId = worldId;
        this.slotId = slotId;
    }

    public void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.field_62099.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.title, this.textRenderer));
        this.field_62100 = (TextFieldWidget)directionalLayoutWidget.add((Widget)new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.field_62100, (Text)Text.translatable((String)"selectWorld.search")));
        this.field_62100.setChangedListener(string -> {
            if (this.worldSelectionList != null) {
                this.worldSelectionList.setSearch(string);
            }
        });
        try {
            this.worldSelectionList = (WorldListWidget)this.field_62099.addBody((Widget)new WorldListWidget.Builder(this.client, (Screen)this).width(this.width).height(this.field_62099.getContentHeight()).search(this.field_62100.getText()).predecessor(this.worldSelectionList).uploadWorld().selectionCallback(arg_0 -> this.worldSelected(arg_0)).confirmationCallback(arg_0 -> this.upload(arg_0)).toWidget());
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load level list", (Throwable)exception);
            this.client.setScreen((Screen)new RealmsGenericErrorScreen(LOADING_ERROR_TEXT, Text.of((String)exception.getMessage()), (Screen)this.parent));
            return;
        }
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.field_62099.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.getMainPositioner().alignHorizontalCenter();
        this.uploadButton = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"mco.upload.button.name"), buttonWidget -> this.worldSelectionList.getSelectedAsOptional().ifPresent(arg_0 -> this.upload(arg_0))).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, buttonWidget -> this.close()).build());
        this.worldSelected(null);
        this.field_62099.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        if (this.worldSelectionList != null) {
            this.worldSelectionList.position(this.width, this.field_62099);
        }
        this.field_62099.refreshPositions();
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.field_62100);
    }

    private void worldSelected(@Nullable LevelSummary level) {
        if (this.worldSelectionList != null && this.uploadButton != null) {
            this.uploadButton.active = this.worldSelectionList.getSelectedOrNull() != null;
        }
    }

    private void upload(WorldListWidget.WorldEntry worldEntry) {
        this.client.setScreen((Screen)new RealmsUploadScreen(this.creationTask, this.worldId, this.slotId, this.parent, worldEntry.getLevel()));
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{this.getTitle(), this.narrateLabels()});
    }

    public void close() {
        this.client.setScreen((Screen)this.parent);
    }
}

