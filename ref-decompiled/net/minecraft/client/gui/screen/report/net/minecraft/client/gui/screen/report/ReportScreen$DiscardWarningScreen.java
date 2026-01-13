/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
class ReportScreen.DiscardWarningScreen
extends WarningScreen {
    private static final Text TITLE = Text.translatable("gui.abuseReport.discard.title").formatted(Formatting.BOLD);
    private static final Text MESSAGE = Text.translatable("gui.abuseReport.discard.content");
    private static final Text RETURN_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.return");
    private static final Text DRAFT_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.draft");
    private static final Text DISCARD_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.discard");

    protected ReportScreen.DiscardWarningScreen() {
        super(TITLE, MESSAGE, MESSAGE);
    }

    @Override
    protected LayoutWidget getLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add(ButtonWidget.builder(RETURN_BUTTON_TEXT, button -> this.close()).build());
        directionalLayoutWidget2.add(ButtonWidget.builder(DRAFT_BUTTON_TEXT, button -> {
            ReportScreen.this.saveDraft();
            this.client.setScreen(ReportScreen.this.parent);
        }).build());
        directionalLayoutWidget.add(ButtonWidget.builder(DISCARD_BUTTON_TEXT, button -> {
            ReportScreen.this.resetDraft();
            this.client.setScreen(ReportScreen.this.parent);
        }).build());
        return directionalLayoutWidget;
    }

    @Override
    public void close() {
        this.client.setScreen(ReportScreen.this);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
