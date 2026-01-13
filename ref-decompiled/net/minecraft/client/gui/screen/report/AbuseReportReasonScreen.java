/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.AbuseReportReasonScreen
 *  net.minecraft.client.gui.screen.report.AbuseReportReasonScreen$ReasonListWidget
 *  net.minecraft.client.gui.screen.report.AbuseReportReasonScreen$ReasonListWidget$ReasonEntry
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.session.report.AbuseReportReason
 *  net.minecraft.client.session.report.AbuseReportType
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Nullables
 *  net.minecraft.util.Urls
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;
import net.minecraft.util.Urls;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AbuseReportReasonScreen
extends Screen {
    private static final Text TITLE_TEXT = Text.translatable((String)"gui.abuseReport.reason.title");
    private static final Text DESCRIPTION_TEXT = Text.translatable((String)"gui.abuseReport.reason.description");
    private static final Text READ_INFO_TEXT = Text.translatable((String)"gui.abuseReport.read_info");
    private static final int field_49546 = 320;
    private static final int field_49547 = 62;
    private static final int TOP_MARGIN = 4;
    private final @Nullable Screen parent;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ReasonListWidget reasonList;
    @Nullable AbuseReportReason reason;
    private final Consumer<AbuseReportReason> reasonConsumer;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    final AbuseReportType reportType;

    public AbuseReportReasonScreen(@Nullable Screen parent, @Nullable AbuseReportReason reason, AbuseReportType reportType, Consumer<AbuseReportReason> reasonConsumer) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.reason = reason;
        this.reasonConsumer = reasonConsumer;
        this.reportType = reportType;
    }

    protected void init() {
        this.layout.addHeader(TITLE_TEXT, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addBody((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        this.reasonList = (ReasonListWidget)directionalLayoutWidget.add((Widget)new ReasonListWidget(this, this.client));
        ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)Nullables.map((Object)this.reason, arg_0 -> ((ReasonListWidget)this.reasonList).getEntry(arg_0));
        this.reasonList.setSelected(reasonEntry);
        directionalLayoutWidget.add((Widget)EmptyWidget.ofHeight((int)this.getHeight()));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)READ_INFO_TEXT, (ButtonWidget.PressAction)ConfirmLinkScreen.opening((Screen)this, (URI)Urls.ABOUT_JAVA_REPORTING)).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> {
            ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
            if (reasonEntry != null) {
                this.reasonConsumer.accept(reasonEntry.getReason());
            }
            this.client.setScreen(this.parent);
        }).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.reasonList != null) {
            this.reasonList.position(this.width, this.getReasonListHeight(), this.layout.getHeaderHeight());
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.fill(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), -16777216);
        context.drawStrokedRectangle(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight(), -1);
        context.drawTextWithShadow(this.textRenderer, DESCRIPTION_TEXT, this.getLeft() + 4, this.getTop() + 4, -1);
        ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
        if (reasonEntry != null) {
            int i = this.getLeft() + 4 + 16;
            int j = this.getRight() - 4;
            int n = this.getTop() + 4;
            Objects.requireNonNull(this.textRenderer);
            int k = n + 9 + 2;
            int l = this.getBottom() - 4;
            int m = j - i;
            int n2 = l - k;
            int o = this.textRenderer.getWrappedLinesHeight((StringVisitable)reasonEntry.reason.getDescription(), m);
            context.drawWrappedTextWithShadow(this.textRenderer, (StringVisitable)reasonEntry.reason.getDescription(), i, k + (n2 - o) / 2, m, -1);
        }
    }

    private int getLeft() {
        return (this.width - 320) / 2;
    }

    private int getRight() {
        return (this.width + 320) / 2;
    }

    private int getTop() {
        return this.getBottom() - this.getHeight();
    }

    private int getBottom() {
        return this.height - this.layout.getFooterHeight() - 4;
    }

    private int getWidth() {
        return 320;
    }

    private int getHeight() {
        return 62;
    }

    int getReasonListHeight() {
        return this.layout.getContentHeight() - this.getHeight() - 8;
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    static /* synthetic */ TextRenderer method_44521(AbuseReportReasonScreen abuseReportReasonScreen) {
        return abuseReportReasonScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44671(AbuseReportReasonScreen abuseReportReasonScreen) {
        return abuseReportReasonScreen.textRenderer;
    }
}

