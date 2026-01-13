/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen;
import net.minecraft.client.realms.gui.screen.RealmsBackupScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.task.RestoreTask;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RealmsBackupScreen.BackupObjectSelectionListEntry
extends ElementListWidget.Entry<RealmsBackupScreen.BackupObjectSelectionListEntry> {
    private static final int field_44525 = 2;
    private final Backup mBackup;
    private @Nullable ButtonWidget restoreButton;
    private @Nullable ButtonWidget changesButton;
    private final List<ClickableWidget> buttons = new ArrayList<ClickableWidget>();

    public RealmsBackupScreen.BackupObjectSelectionListEntry(Backup backup) {
        this.mBackup = backup;
        this.updateChangeList(backup);
        if (!backup.changeList.isEmpty()) {
            this.changesButton = ButtonWidget.builder(CHANGES_TOOLTIP, button -> RealmsBackupScreen.this.client.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.mBackup))).width(8 + RealmsBackupScreen.this.textRenderer.getWidth(CHANGES_TOOLTIP)).narrationSupplier(this::getNarration).build();
            this.buttons.add(this.changesButton);
        }
        if (!RealmsBackupScreen.this.serverData.expired) {
            this.restoreButton = ButtonWidget.builder(RESTORE_TEXT, button -> this.restore()).width(8 + RealmsBackupScreen.this.textRenderer.getWidth(CHANGES_TOOLTIP)).narrationSupplier(this::getNarration).build();
            this.buttons.add(this.restoreButton);
        }
    }

    private MutableText getNarration(Supplier<MutableText> messageSupplier) {
        return ScreenTexts.joinSentences(Text.translatable("mco.backup.narration", FORMATTER.format(this.mBackup.getLastModifiedTime())), messageSupplier.get());
    }

    private void updateChangeList(Backup backup) {
        int i = RealmsBackupScreen.this.backups.indexOf(backup);
        if (i == RealmsBackupScreen.this.backups.size() - 1) {
            return;
        }
        Backup backup2 = RealmsBackupScreen.this.backups.get(i + 1);
        for (String string : backup.metadata.keySet()) {
            if (!string.contains(RealmsBackupScreen.UPLOADED) && backup2.metadata.containsKey(string)) {
                if (backup.metadata.get(string).equals(backup2.metadata.get(string))) continue;
                this.addChange(string);
                continue;
            }
            this.addChange(string);
        }
    }

    private void addChange(String metadataKey) {
        if (metadataKey.contains(RealmsBackupScreen.UPLOADED)) {
            String string = FORMATTER.format(this.mBackup.getLastModifiedTime());
            this.mBackup.changeList.put(metadataKey, string);
            this.mBackup.uploadedVersion = true;
        } else {
            this.mBackup.changeList.put(metadataKey, this.mBackup.metadata.get(metadataKey));
        }
    }

    private void restore() {
        Text text = RealmsUtil.convertToAgePresentation(this.mBackup.lastModifiedDate);
        String string = FORMATTER.format(this.mBackup.getLastModifiedTime());
        MutableText text2 = Text.translatable("mco.configure.world.restore.question.line1", string, text);
        RealmsBackupScreen.this.client.setScreen(RealmsPopups.createContinuableWarningPopup(RealmsBackupScreen.this, text2, popup -> {
            RealmsConfigureWorldScreen realmsConfigureWorldScreen = RealmsBackupScreen.this.parent.getNewScreen();
            RealmsBackupScreen.this.client.setScreen(new RealmsLongRunningMcoTaskScreen(realmsConfigureWorldScreen, new RestoreTask(this.mBackup, RealmsBackupScreen.this.serverData.id, realmsConfigureWorldScreen)));
        }));
    }

    @Override
    public List<? extends Element> children() {
        return this.buttons;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.buttons;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = this.getContentMiddleY();
        int j = i - ((RealmsBackupScreen)RealmsBackupScreen.this).textRenderer.fontHeight - 2;
        int k = i + 2;
        int l = this.mBackup.uploadedVersion ? -8388737 : -1;
        context.drawTextWithShadow(RealmsBackupScreen.this.textRenderer, Text.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentation(this.mBackup.lastModifiedDate)), this.getContentX(), j, l);
        context.drawTextWithShadow(RealmsBackupScreen.this.textRenderer, FORMATTER.format(this.mBackup.getLastModifiedTime()), this.getContentX(), k, -11776948);
        int m = 0;
        int n = this.getContentMiddleY() - 10;
        if (this.restoreButton != null) {
            this.restoreButton.setX(this.getContentRightEnd() - (m += this.restoreButton.getWidth() + 8));
            this.restoreButton.setY(n);
            this.restoreButton.render(context, mouseX, mouseY, deltaTicks);
        }
        if (this.changesButton != null) {
            this.changesButton.setX(this.getContentRightEnd() - (m += this.changesButton.getWidth() + 8));
            this.changesButton.setY(n);
            this.changesButton.render(context, mouseX, mouseY, deltaTicks);
        }
    }
}
