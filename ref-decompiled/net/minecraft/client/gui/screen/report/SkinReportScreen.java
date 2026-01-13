/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.AbuseReportReasonScreen
 *  net.minecraft.client.gui.screen.report.ReportScreen
 *  net.minecraft.client.gui.screen.report.SkinReportScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EditBoxWidget
 *  net.minecraft.client.gui.widget.LayoutWidgets
 *  net.minecraft.client.gui.widget.PlayerSkinWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.session.report.AbuseReport$Builder
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.AbuseReportReason
 *  net.minecraft.client.session.report.AbuseReportType
 *  net.minecraft.client.session.report.SkinAbuseReport
 *  net.minecraft.client.session.report.SkinAbuseReport$Builder
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.report;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
import net.minecraft.client.gui.screen.report.ReportScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.PlayerSkinWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.client.session.report.SkinAbuseReport;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SkinReportScreen
extends ReportScreen<SkinAbuseReport.Builder> {
    private static final int SKIN_WIDGET_WIDTH = 85;
    private static final int REASON_BUTTON_AND_COMMENTS_BOX_WIDTH = 178;
    private static final Text TITLE_TEXT = Text.translatable((String)"gui.abuseReport.skin.title");
    private EditBoxWidget commentsBox;
    private ButtonWidget selectReasonButton;

    private SkinReportScreen(Screen parent, AbuseReportContext context, SkinAbuseReport.Builder reportBuilder) {
        super(TITLE_TEXT, parent, context, (AbuseReport.Builder)reportBuilder);
    }

    public SkinReportScreen(Screen parent, AbuseReportContext context, UUID reportedPlayerUuid, Supplier<SkinTextures> skinSupplier) {
        this(parent, context, new SkinAbuseReport.Builder(reportedPlayerUuid, skinSupplier, context.getSender().getLimits()));
    }

    public SkinReportScreen(Screen parent, AbuseReportContext context, SkinAbuseReport report) {
        this(parent, context, new SkinAbuseReport.Builder(report, context.getSender().getLimits()));
    }

    protected void addContent() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
        directionalLayoutWidget.add((Widget)new PlayerSkinWidget(85, 120, this.client.getLoadedEntityModels(), ((SkinAbuseReport)((SkinAbuseReport.Builder)this.reportBuilder).getReport()).getSkinSupplier()));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        this.selectReasonButton = ButtonWidget.builder((Text)SELECT_REASON_TEXT, button -> this.client.setScreen((Screen)new AbuseReportReasonScreen((Screen)this, ((SkinAbuseReport.Builder)this.reportBuilder).getReason(), AbuseReportType.SKIN, reason -> {
            ((SkinAbuseReport.Builder)this.reportBuilder).setReason(reason);
            this.onChange();
        }))).width(178).build();
        directionalLayoutWidget2.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.selectReasonButton, (Text)OBSERVED_WHAT_TEXT));
        Objects.requireNonNull(this.textRenderer);
        this.commentsBox = this.createCommentsBox(178, 9 * 8, comments -> {
            ((SkinAbuseReport.Builder)this.reportBuilder).setOpinionComments(comments);
            this.onChange();
        });
        directionalLayoutWidget2.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.commentsBox, (Text)MORE_COMMENTS_TEXT, positioner -> positioner.marginBottom(12)));
    }

    protected void onChange() {
        AbuseReportReason abuseReportReason = ((SkinAbuseReport.Builder)this.reportBuilder).getReason();
        if (abuseReportReason != null) {
            this.selectReasonButton.setMessage(abuseReportReason.getText());
        } else {
            this.selectReasonButton.setMessage(SELECT_REASON_TEXT);
        }
        super.onChange();
    }

    public boolean mouseReleased(Click click) {
        if (super.mouseReleased(click)) {
            return true;
        }
        return this.commentsBox.mouseReleased(click);
    }
}

