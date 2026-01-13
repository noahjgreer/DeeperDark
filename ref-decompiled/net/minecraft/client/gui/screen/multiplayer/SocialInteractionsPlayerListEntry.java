/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.PlayerSkinDrawer
 *  net.minecraft.client.gui.Selectable
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry
 *  net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.ElementListWidget$Entry
 *  net.minecraft.client.network.SocialInteractionsManager
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SocialInteractionsPlayerListEntry
extends ElementListWidget.Entry<SocialInteractionsPlayerListEntry> {
    private static final Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/draft_report");
    private static final Duration TOOLTIP_DELAY = Duration.ofMillis(500L);
    private static final ButtonTextures REPORT_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"social_interactions/report_button"), Identifier.ofVanilla((String)"social_interactions/report_button_disabled"), Identifier.ofVanilla((String)"social_interactions/report_button_highlighted"));
    private static final ButtonTextures MUTE_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"social_interactions/mute_button"), Identifier.ofVanilla((String)"social_interactions/mute_button_highlighted"));
    private static final ButtonTextures UNMUTE_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"social_interactions/unmute_button"), Identifier.ofVanilla((String)"social_interactions/unmute_button_highlighted"));
    private final MinecraftClient client;
    private final List<ClickableWidget> buttons;
    private final UUID uuid;
    private final String name;
    private final Supplier<SkinTextures> skinSupplier;
    private boolean offline;
    private boolean sentMessage;
    private final boolean canSendReports;
    private boolean hasDraftReport;
    private final boolean reportable;
    private @Nullable ButtonWidget hideButton;
    private @Nullable ButtonWidget showButton;
    private @Nullable ButtonWidget reportButton;
    private float timeCounter;
    private static final Text HIDDEN_TEXT = Text.translatable((String)"gui.socialInteractions.status_hidden").formatted(Formatting.ITALIC);
    private static final Text BLOCKED_TEXT = Text.translatable((String)"gui.socialInteractions.status_blocked").formatted(Formatting.ITALIC);
    private static final Text OFFLINE_TEXT = Text.translatable((String)"gui.socialInteractions.status_offline").formatted(Formatting.ITALIC);
    private static final Text HIDDEN_OFFLINE_TEXT = Text.translatable((String)"gui.socialInteractions.status_hidden_offline").formatted(Formatting.ITALIC);
    private static final Text BLOCKED_OFFLINE_TEXT = Text.translatable((String)"gui.socialInteractions.status_blocked_offline").formatted(Formatting.ITALIC);
    private static final Text REPORT_DISABLED_TEXT = Text.translatable((String)"gui.socialInteractions.tooltip.report.disabled");
    private static final Text HIDE_TEXT = Text.translatable((String)"gui.socialInteractions.tooltip.hide");
    private static final Text SHOW_TEXT = Text.translatable((String)"gui.socialInteractions.tooltip.show");
    private static final Text REPORT_TEXT = Text.translatable((String)"gui.socialInteractions.tooltip.report");
    private static final int field_32420 = 24;
    private static final int field_32421 = 4;
    public static final int BLACK_COLOR = ColorHelper.getArgb((int)190, (int)0, (int)0, (int)0);
    private static final int field_32422 = 20;
    public static final int GRAY_COLOR = ColorHelper.getArgb((int)255, (int)74, (int)74, (int)74);
    public static final int DARK_GRAY_COLOR = ColorHelper.getArgb((int)255, (int)48, (int)48, (int)48);
    public static final int WHITE_COLOR = ColorHelper.getArgb((int)255, (int)255, (int)255, (int)255);
    public static final int LIGHT_GRAY_COLOR = ColorHelper.getArgb((int)140, (int)255, (int)255, (int)255);

    public SocialInteractionsPlayerListEntry(MinecraftClient client, SocialInteractionsScreen parent, UUID uuid, String name, Supplier<SkinTextures> skinTexture, boolean reportable) {
        boolean bl2;
        this.client = client;
        this.uuid = uuid;
        this.name = name;
        this.skinSupplier = skinTexture;
        AbuseReportContext abuseReportContext = client.getAbuseReportContext();
        this.canSendReports = abuseReportContext.getSender().canSendReports();
        this.reportable = reportable;
        this.updateHasDraftReport(abuseReportContext);
        MutableText text = Text.translatable((String)"gui.socialInteractions.narration.hide", (Object[])new Object[]{name});
        MutableText text2 = Text.translatable((String)"gui.socialInteractions.narration.show", (Object[])new Object[]{name});
        SocialInteractionsManager socialInteractionsManager = client.getSocialInteractionsManager();
        boolean bl = client.getChatRestriction().allowsChat(client.isInSingleplayer());
        boolean bl3 = bl2 = !client.player.getUuid().equals(uuid);
        if (SharedConstants.SOCIAL_INTERACTIONS || bl2 && bl && !socialInteractionsManager.isPlayerBlocked(uuid)) {
            this.reportButton = new /* Unavailable Anonymous Inner Class!! */;
            this.reportButton.active = this.canSendReports;
            this.reportButton.setTooltip(this.getReportButtonTooltip());
            this.reportButton.setTooltipDelay(TOOLTIP_DELAY);
            this.hideButton = new /* Unavailable Anonymous Inner Class!! */;
            this.hideButton.setTooltip(Tooltip.of((Text)HIDE_TEXT, (Text)text));
            this.hideButton.setTooltipDelay(TOOLTIP_DELAY);
            this.showButton = new /* Unavailable Anonymous Inner Class!! */;
            this.showButton.setTooltip(Tooltip.of((Text)SHOW_TEXT, (Text)text2));
            this.showButton.setTooltipDelay(TOOLTIP_DELAY);
            this.buttons = new ArrayList();
            this.buttons.add(this.hideButton);
            this.buttons.add(this.reportButton);
            this.setShowButtonVisible(socialInteractionsManager.isPlayerHidden(this.uuid));
        } else {
            this.buttons = ImmutableList.of();
        }
    }

    public void updateHasDraftReport(AbuseReportContext context) {
        this.hasDraftReport = context.draftPlayerUuidEquals(this.uuid);
    }

    private Tooltip getReportButtonTooltip() {
        if (!this.canSendReports) {
            return Tooltip.of((Text)REPORT_DISABLED_TEXT);
        }
        return Tooltip.of((Text)REPORT_TEXT, (Text)Text.translatable((String)"gui.socialInteractions.narration.report", (Object[])new Object[]{this.name}));
    }

    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int l;
        int i = this.getContentX() + 4;
        int j = this.getContentY() + (this.getContentHeight() - 24) / 2;
        int k = i + 24 + 4;
        Text text = this.getStatusText();
        if (text == ScreenTexts.EMPTY) {
            context.fill(this.getContentX(), this.getContentY(), this.getContentRightEnd(), this.getContentBottomEnd(), GRAY_COLOR);
            int n = this.getContentY();
            int n2 = this.getContentHeight();
            Objects.requireNonNull(this.client.textRenderer);
            l = n + (n2 - 9) / 2;
        } else {
            context.fill(this.getContentX(), this.getContentY(), this.getContentRightEnd(), this.getContentBottomEnd(), DARK_GRAY_COLOR);
            int n = this.getContentY();
            int n3 = this.getContentHeight();
            Objects.requireNonNull(this.client.textRenderer);
            Objects.requireNonNull(this.client.textRenderer);
            l = n + (n3 - (9 + 9)) / 2;
            context.drawTextWithShadow(this.client.textRenderer, text, k, l + 12, LIGHT_GRAY_COLOR);
        }
        PlayerSkinDrawer.draw((DrawContext)context, (SkinTextures)((SkinTextures)this.skinSupplier.get()), (int)i, (int)j, (int)24);
        context.drawTextWithShadow(this.client.textRenderer, this.name, k, l, WHITE_COLOR);
        if (this.offline) {
            context.fill(i, j, i + 24, j + 24, BLACK_COLOR);
        }
        if (this.hideButton != null && this.showButton != null && this.reportButton != null) {
            float f = this.timeCounter;
            this.hideButton.setX(this.getContentX() + (this.getContentWidth() - this.hideButton.getWidth() - 4) - 20 - 4);
            this.hideButton.setY(this.getContentY() + (this.getContentHeight() - this.hideButton.getHeight()) / 2);
            this.hideButton.render(context, mouseX, mouseY, deltaTicks);
            this.showButton.setX(this.getContentX() + (this.getContentWidth() - this.showButton.getWidth() - 4) - 20 - 4);
            this.showButton.setY(this.getContentY() + (this.getContentHeight() - this.showButton.getHeight()) / 2);
            this.showButton.render(context, mouseX, mouseY, deltaTicks);
            this.reportButton.setX(this.getContentX() + (this.getContentWidth() - this.showButton.getWidth() - 4));
            this.reportButton.setY(this.getContentY() + (this.getContentHeight() - this.showButton.getHeight()) / 2);
            this.reportButton.render(context, mouseX, mouseY, deltaTicks);
            if (f == this.timeCounter) {
                this.timeCounter = 0.0f;
            }
        }
        if (this.hasDraftReport && this.reportButton != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_ICON_TEXTURE, this.reportButton.getX() + 5, this.reportButton.getY() + 1, 15, 15);
        }
    }

    public List<? extends Element> children() {
        return this.buttons;
    }

    public List<? extends Selectable> selectableChildren() {
        return this.buttons;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Supplier<SkinTextures> getSkinSupplier() {
        return this.skinSupplier;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isOffline() {
        return this.offline;
    }

    public void setSentMessage(boolean sentMessage) {
        this.sentMessage = sentMessage;
    }

    public boolean hasSentMessage() {
        return this.sentMessage;
    }

    public boolean isReportable() {
        return this.reportable;
    }

    private void onButtonClick(boolean showButtonVisible, Text chatMessage) {
        this.setShowButtonVisible(showButtonVisible);
        this.client.inGameHud.getChatHud().addMessage(chatMessage);
        this.client.getNarratorManager().narrateSystemImmediately(chatMessage);
    }

    private void setShowButtonVisible(boolean showButtonVisible) {
        this.showButton.visible = showButtonVisible;
        this.hideButton.visible = !showButtonVisible;
        this.buttons.set(0, showButtonVisible ? this.showButton : this.hideButton);
    }

    MutableText getNarrationMessage(MutableText text) {
        Text text2 = this.getStatusText();
        if (text2 == ScreenTexts.EMPTY) {
            return Text.literal((String)this.name).append(", ").append((Text)text);
        }
        return Text.literal((String)this.name).append(", ").append(text2).append(", ").append((Text)text);
    }

    private Text getStatusText() {
        boolean bl = this.client.getSocialInteractionsManager().isPlayerHidden(this.uuid);
        boolean bl2 = this.client.getSocialInteractionsManager().isPlayerBlocked(this.uuid);
        if (bl2 && this.offline) {
            return BLOCKED_OFFLINE_TEXT;
        }
        if (bl && this.offline) {
            return HIDDEN_OFFLINE_TEXT;
        }
        if (bl2) {
            return BLOCKED_TEXT;
        }
        if (bl) {
            return HIDDEN_TEXT;
        }
        if (this.offline) {
            return OFFLINE_TEXT;
        }
        return ScreenTexts.EMPTY;
    }
}

