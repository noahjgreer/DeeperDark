/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.dto.Backup
 *  net.minecraft.client.realms.dto.RealmsServer$WorldType
 *  net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen
 *  net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen$BackupInfoList
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.GameMode
 */
package net.minecraft.client.realms.gui.screen;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSlotOptionsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupInfoScreen
extends RealmsScreen {
    private static final Text TITLE = Text.translatable((String)"mco.backup.info.title");
    private static final Text UNKNOWN = Text.translatable((String)"mco.backup.unknown");
    private final Screen parent;
    final Backup backup;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private BackupInfoList backupInfoList;

    public RealmsBackupInfoScreen(Screen parent, Backup backup) {
        super(TITLE);
        this.parent = parent;
        this.backup = backup;
    }

    public void init() {
        this.layout.addHeader(TITLE, this.textRenderer);
        this.backupInfoList = (BackupInfoList)this.layout.addBody((Widget)new BackupInfoList(this, this.client));
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).build());
        this.refreshWidgetPositions();
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
    }

    protected void refreshWidgetPositions() {
        this.backupInfoList.position(this.width, this.layout);
        this.layout.refreshPositions();
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    Text getValueText(String key, String value) {
        String string = key.toLowerCase(Locale.ROOT);
        if (string.contains("game") && string.contains("mode")) {
            return this.getGameModeText(value);
        }
        if (string.contains("game") && string.contains("difficulty")) {
            return this.getDifficultyText(value);
        }
        if (key.equals("world_type")) {
            return this.getWorldTypeText(value);
        }
        return Text.literal((String)value);
    }

    private Text getDifficultyText(String value) {
        try {
            return ((Difficulty)RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(value))).getTranslatableName();
        }
        catch (Exception exception) {
            return UNKNOWN;
        }
    }

    private Text getGameModeText(String value) {
        try {
            return ((GameMode)RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(value))).getSimpleTranslatableName();
        }
        catch (Exception exception) {
            return UNKNOWN;
        }
    }

    private Text getWorldTypeText(String type) {
        try {
            return RealmsServer.WorldType.valueOf((String)type.toUpperCase(Locale.ROOT)).getText();
        }
        catch (Exception exception) {
            return RealmsServer.WorldType.UNKNOWN.getText();
        }
    }

    static /* synthetic */ TextRenderer method_29335(RealmsBackupInfoScreen realmsBackupInfoScreen) {
        return realmsBackupInfoScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_51885(RealmsBackupInfoScreen realmsBackupInfoScreen) {
        return realmsBackupInfoScreen.textRenderer;
    }
}

