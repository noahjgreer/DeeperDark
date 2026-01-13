/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.dto.Backup
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.gui.RealmsPopups
 *  net.minecraft.client.realms.gui.screen.RealmsBackupScreen
 *  net.minecraft.client.realms.gui.screen.RealmsBackupScreen$BackupObjectSelectionList
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.task.DownloadTask
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsBackupScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.DownloadTask;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupScreen
extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Text BACKUPS_TEXT = Text.translatable((String)"mco.configure.world.backup");
    static final Text RESTORE_TEXT = Text.translatable((String)"mco.backup.button.restore");
    static final Text CHANGES_TOOLTIP = Text.translatable((String)"mco.backup.changes.tooltip");
    private static final Text NO_BACKUPS_TEXT = Text.translatable((String)"mco.backup.nobackups");
    private static final Text DOWNLOAD_TEXT = Text.translatable((String)"mco.backup.button.download");
    private static final String UPLOADED = "uploaded";
    private static final int field_49447 = 8;
    public static final DateTimeFormatter FORMATTER = Util.getDefaultLocaleFormatter((FormatStyle)FormatStyle.SHORT);
    final RealmsConfigureWorldScreen parent;
    List<Backup> backups = Collections.emptyList();
    // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BackupObjectSelectionList selectionList;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private final int slotId;
    @Nullable ButtonWidget downloadButton;
    final RealmsServer serverData;
    boolean noBackups = false;

    public RealmsBackupScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData, int slotId) {
        super(BACKUPS_TEXT);
        this.parent = parent;
        this.serverData = serverData;
        this.slotId = slotId;
    }

    public void init() {
        this.layout.addHeader(BACKUPS_TEXT, this.textRenderer);
        this.selectionList = (BackupObjectSelectionList)this.layout.addBody((Widget)new BackupObjectSelectionList(this));
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        this.downloadButton = (ButtonWidget)directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)DOWNLOAD_TEXT, button -> this.downloadClicked()).build());
        this.downloadButton.active = false;
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
        this.startBackupFetcher();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.noBackups && this.selectionList != null) {
            int n = this.width / 2 - this.textRenderer.getWidth((StringVisitable)NO_BACKUPS_TEXT) / 2;
            int n2 = this.selectionList.getY() + this.selectionList.getHeight() / 2;
            Objects.requireNonNull(this.textRenderer);
            context.drawTextWithShadow(this.textRenderer, NO_BACKUPS_TEXT, n, n2 - 9 / 2, -1);
        }
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.selectionList != null) {
            this.selectionList.position(this.width, this.layout);
        }
    }

    private void startBackupFetcher() {
        new /* Unavailable Anonymous Inner Class!! */.start();
    }

    public void close() {
        this.client.setScreen((Screen)this.parent);
    }

    private void downloadClicked() {
        this.client.setScreen((Screen)RealmsPopups.createInfoPopup((Screen)this, (Text)Text.translatable((String)"mco.configure.world.restore.download.question.line1"), button -> this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen((Screen)this.parent.getNewScreen(), new LongRunningTask[]{new DownloadTask(this.serverData.id, this.slotId, Objects.requireNonNullElse(this.serverData.name, "") + " (" + ((RealmsSlot)this.serverData.slots.get((Object)Integer.valueOf((int)this.serverData.activeSlot))).options.getSlotName(this.serverData.activeSlot) + ")", (Screen)this)}))));
    }

    static /* synthetic */ MinecraftClient method_25108(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.client;
    }

    static /* synthetic */ TextRenderer method_25115(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_25116(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.textRenderer;
    }

    static /* synthetic */ MinecraftClient method_51237(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.client;
    }

    static /* synthetic */ TextRenderer method_57662(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_57663(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_57664(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.textRenderer;
    }

    static /* synthetic */ MinecraftClient method_57665(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.client;
    }

    static /* synthetic */ MinecraftClient method_57666(RealmsBackupScreen realmsBackupScreen) {
        return realmsBackupScreen.client;
    }
}

