/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
class RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry
extends ElementListWidget.Entry<RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry> {
    private static final Text ACCEPT_TEXT = Text.translatable("mco.invites.button.accept");
    private static final Text REJECT_TEXT = Text.translatable("mco.invites.button.reject");
    private static final ButtonTextures ACCEPT_TEXTURE = new ButtonTextures(Identifier.ofVanilla("pending_invite/accept"), Identifier.ofVanilla("pending_invite/accept_highlighted"));
    private static final ButtonTextures REJECT_TEXTURE = new ButtonTextures(Identifier.ofVanilla("pending_invite/reject"), Identifier.ofVanilla("pending_invite/reject_highlighted"));
    private static final int field_62090 = 18;
    private static final int field_62091 = 21;
    private static final int field_32123 = 38;
    private final PendingInvite pendingInvite;
    private final List<ClickableWidget> buttons = new ArrayList<ClickableWidget>();
    private final TextIconButtonWidget acceptButton;
    private final TextIconButtonWidget rejectButton;
    private final TextWidget worldNameText;
    private final TextWidget worldOwnerNameText;
    private final TextWidget dateText;

    RealmsPendingInvitesScreen.PendingInvitationSelectionListEntry(PendingInvite pendingInvite) {
        this.pendingInvite = pendingInvite;
        int i = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.getRowWidth() - 32 - 32 - 42;
        this.worldNameText = new TextWidget(Text.literal(pendingInvite.worldName()), RealmsPendingInvitesScreen.this.textRenderer).setMaxWidth(i);
        this.worldOwnerNameText = new TextWidget(Text.literal(pendingInvite.worldOwnerName()).withColor(-6250336), RealmsPendingInvitesScreen.this.textRenderer).setMaxWidth(i);
        this.dateText = new TextWidget(Texts.withStyle(RealmsUtil.convertToAgePresentation(pendingInvite.date()), Style.EMPTY.withColor(-6250336)), RealmsPendingInvitesScreen.this.textRenderer).setMaxWidth(i);
        ButtonWidget.NarrationSupplier narrationSupplier = this.getNarration(pendingInvite);
        this.acceptButton = TextIconButtonWidget.builder(ACCEPT_TEXT, button -> this.handle(true), false).texture(ACCEPT_TEXTURE, 18, 18).dimension(21, 21).narration(narrationSupplier).useTextAsTooltip().build();
        this.rejectButton = TextIconButtonWidget.builder(REJECT_TEXT, button -> this.handle(false), false).texture(REJECT_TEXTURE, 18, 18).dimension(21, 21).narration(narrationSupplier).useTextAsTooltip().build();
        this.buttons.addAll(List.of(this.acceptButton, this.rejectButton));
    }

    private ButtonWidget.NarrationSupplier getNarration(PendingInvite invite) {
        return textSupplier -> {
            MutableText mutableText = ScreenTexts.joinSentences((Text)textSupplier.get(), Text.literal(invite.worldName()), Text.literal(invite.worldOwnerName()), RealmsUtil.convertToAgePresentation(invite.date()));
            return Text.translatable("narrator.select", mutableText);
        };
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
        int i = this.getContentX();
        int j = this.getContentY();
        int k = i + 38;
        RealmsUtil.drawPlayerHead(context, i, j, 32, this.pendingInvite.worldOwnerUuid());
        this.worldNameText.setPosition(k, j + 1);
        this.worldNameText.renderWidget(context, mouseX, mouseY, i);
        this.worldOwnerNameText.setPosition(k, j + 12);
        this.worldOwnerNameText.renderWidget(context, mouseX, mouseY, i);
        this.dateText.setPosition(k, j + 24);
        this.dateText.renderWidget(context, mouseX, mouseY, i);
        int l = j + this.getContentHeight() / 2 - 10;
        this.acceptButton.setPosition(i + this.getContentWidth() - 16 - 42, l);
        this.acceptButton.render(context, mouseX, mouseY, deltaTicks);
        this.rejectButton.setPosition(i + this.getContentWidth() - 8 - 21, l);
        this.rejectButton.render(context, mouseX, mouseY, deltaTicks);
    }

    private void handle(boolean accepted) {
        String string = this.pendingInvite.invitationId();
        CompletableFuture.supplyAsync(() -> {
            try {
                RealmsClient realmsClient = RealmsClient.create();
                if (accepted) {
                    realmsClient.acceptInvitation(string);
                } else {
                    realmsClient.rejectInvitation(string);
                }
                return true;
            }
            catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't handle invite", (Throwable)realmsServiceException);
                return false;
            }
        }, Util.getIoWorkerExecutor()).thenAcceptAsync(processed -> {
            if (processed.booleanValue()) {
                RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.remove(this);
                RealmsPeriodicCheckers realmsPeriodicCheckers = RealmsPendingInvitesScreen.this.client.getRealmsPeriodicCheckers();
                if (accepted) {
                    realmsPeriodicCheckers.serverList.reset();
                }
                realmsPeriodicCheckers.pendingInvitesCount.reset();
            }
        }, RealmsPendingInvitesScreen.this.executor);
    }
}
