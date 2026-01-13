/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry
 *  net.minecraft.client.gui.screen.report.AbuseReportTypeScreen
 *  net.minecraft.client.gui.screen.report.ChatReportScreen
 *  net.minecraft.client.gui.screen.report.SkinReportScreen
 *  net.minecraft.client.gui.screen.report.UsernameReportScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.screen.report.ChatReportScreen;
import net.minecraft.client.gui.screen.report.SkinReportScreen;
import net.minecraft.client.gui.screen.report.UsernameReportScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class AbuseReportTypeScreen
extends Screen {
    private static final Text TITLE_TEXT = Text.translatable((String)"gui.abuseReport.title");
    private static final Text MESSAGE_TEXT = Text.translatable((String)"gui.abuseReport.message");
    private static final Text CHAT_TYPE_TEXT = Text.translatable((String)"gui.abuseReport.type.chat");
    private static final Text SKIN_TYPE_TEXT = Text.translatable((String)"gui.abuseReport.type.skin");
    private static final Text NAME_TYPE_TEXT = Text.translatable((String)"gui.abuseReport.type.name");
    private static final int field_46046 = 6;
    private final Screen parent;
    private final AbuseReportContext context;
    private final SocialInteractionsPlayerListEntry selectedPlayer;
    private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(6);

    public AbuseReportTypeScreen(Screen parent, AbuseReportContext context, SocialInteractionsPlayerListEntry selectedPlayer) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.context = context;
        this.selectedPlayer = selectedPlayer;
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), MESSAGE_TEXT});
    }

    protected void init() {
        this.layout.getMainPositioner().alignHorizontalCenter();
        this.layout.add((Widget)new TextWidget(this.title, this.textRenderer), this.layout.copyPositioner().marginBottom(6));
        this.layout.add((Widget)new MultilineTextWidget(MESSAGE_TEXT, this.textRenderer).setCentered(true), this.layout.copyPositioner().marginBottom(6));
        ButtonWidget buttonWidget = (ButtonWidget)this.layout.add((Widget)ButtonWidget.builder((Text)CHAT_TYPE_TEXT, button -> this.client.setScreen((Screen)new ChatReportScreen(this.parent, this.context, this.selectedPlayer.getUuid()))).build());
        if (!this.selectedPlayer.isReportable()) {
            buttonWidget.active = false;
            buttonWidget.setTooltip(Tooltip.of((Text)Text.translatable((String)"gui.socialInteractions.tooltip.report.not_reportable")));
        } else if (!this.selectedPlayer.hasSentMessage()) {
            buttonWidget.active = false;
            buttonWidget.setTooltip(Tooltip.of((Text)Text.translatable((String)"gui.socialInteractions.tooltip.report.no_messages", (Object[])new Object[]{this.selectedPlayer.getName()})));
        }
        this.layout.add((Widget)ButtonWidget.builder((Text)SKIN_TYPE_TEXT, button -> this.client.setScreen((Screen)new SkinReportScreen(this.parent, this.context, this.selectedPlayer.getUuid(), this.selectedPlayer.getSkinSupplier()))).build());
        this.layout.add((Widget)ButtonWidget.builder((Text)NAME_TYPE_TEXT, button -> this.client.setScreen((Screen)new UsernameReportScreen(this.parent, this.context, this.selectedPlayer.getUuid(), this.selectedPlayer.getName()))).build());
        this.layout.add((Widget)EmptyWidget.ofHeight((int)20));
        this.layout.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    public void close() {
        this.client.setScreen(this.parent);
    }
}

