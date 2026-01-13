/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen.tab;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.gui.screen.RealmsConfirmScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class RealmsPlayerTab.InvitedObjectSelectionListEntry
extends RealmsPlayerTab.PlayerTabEntry {
    protected static final int field_60252 = 32;
    private static final Text NORMAL_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.normal.tooltip");
    private static final Text OPS_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.ops.tooltip");
    private static final Text REMOVE_TOOLTIP_TEXT = Text.translatable("mco.configure.world.invites.remove.tooltip");
    private static final Identifier MAKE_OPERATOR_TEXTURE = Identifier.ofVanilla("player_list/make_operator");
    private static final Identifier REMOVE_OPERATOR_TEXTURE = Identifier.ofVanilla("player_list/remove_operator");
    private static final Identifier REMOVE_PLAYER_TEXTURE = Identifier.ofVanilla("player_list/remove_player");
    private static final int field_49470 = 8;
    private static final int field_49471 = 7;
    private final PlayerInfo playerInfo;
    private final ButtonWidget uninviteButton;
    private final ButtonWidget opButton;
    private final ButtonWidget deopButton;

    public RealmsPlayerTab.InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
        int i = RealmsPlayerTab.this.serverData.players.indexOf(this.playerInfo);
        this.opButton = TextIconButtonWidget.builder(NORMAL_TOOLTIP_TEXT, button -> this.op(i), false).texture(MAKE_OPERATOR_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth(NORMAL_TOOLTIP_TEXT)).narration(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.name), (Text)textSupplier.get(), Text.translatable("narration.cycle_button.usage.focused", OPS_TOOLTIP_TEXT))).build();
        this.deopButton = TextIconButtonWidget.builder(OPS_TOOLTIP_TEXT, button -> this.deop(i), false).texture(REMOVE_OPERATOR_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth(OPS_TOOLTIP_TEXT)).narration(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.name), (Text)textSupplier.get(), Text.translatable("narration.cycle_button.usage.focused", NORMAL_TOOLTIP_TEXT))).build();
        this.uninviteButton = TextIconButtonWidget.builder(REMOVE_TOOLTIP_TEXT, button -> this.uninvite(i), false).texture(REMOVE_PLAYER_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth(REMOVE_TOOLTIP_TEXT)).narration(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.name), (Text)textSupplier.get())).build();
        this.refreshOpButtonsVisibility();
    }

    private void op(int index) {
        UUID uUID = RealmsPlayerTab.this.serverData.players.get((int)index).uuid;
        RealmsUtil.runAsync(client -> client.op(RealmsPlayerTab.this.serverData.id, uUID), error -> LOGGER.error("Couldn't op the user", (Throwable)error)).thenAcceptAsync(ops -> {
            this.setOps((Ops)ops);
            this.refreshOpButtonsVisibility();
            this.setFocused(this.deopButton);
        }, (Executor)RealmsPlayerTab.this.client);
    }

    private void deop(int index) {
        UUID uUID = RealmsPlayerTab.this.serverData.players.get((int)index).uuid;
        RealmsUtil.runAsync(client -> client.deop(RealmsPlayerTab.this.serverData.id, uUID), error -> LOGGER.error("Couldn't deop the user", (Throwable)error)).thenAcceptAsync(ops -> {
            this.setOps((Ops)ops);
            this.refreshOpButtonsVisibility();
            this.setFocused(this.opButton);
        }, (Executor)RealmsPlayerTab.this.client);
    }

    private void uninvite(int index) {
        if (index >= 0 && index < RealmsPlayerTab.this.serverData.players.size()) {
            PlayerInfo playerInfo = RealmsPlayerTab.this.serverData.players.get(index);
            RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen(confirmed -> {
                if (confirmed) {
                    RealmsUtil.runAsync(client -> client.uninvite(RealmsPlayerTab.this.serverData.id, playerInfo.uuid), error -> LOGGER.error("Couldn't uninvite user", (Throwable)error));
                    RealmsPlayerTab.this.serverData.players.remove(index);
                    RealmsPlayerTab.this.update(RealmsPlayerTab.this.serverData);
                }
                RealmsPlayerTab.this.client.setScreen(RealmsPlayerTab.this.screen);
            }, QUESTION_TEXT, (Text)Text.translatable("mco.configure.world.uninvite.player", playerInfo.name));
            RealmsPlayerTab.this.client.setScreen(realmsConfirmScreen);
        }
    }

    private void setOps(Ops ops) {
        for (PlayerInfo playerInfo : RealmsPlayerTab.this.serverData.players) {
            playerInfo.operator = ops.ops().contains(playerInfo.name);
        }
    }

    private void refreshOpButtonsVisibility() {
        this.opButton.visible = !this.playerInfo.operator;
        this.deopButton.visible = !this.opButton.visible;
    }

    private ButtonWidget getOpButton() {
        if (this.opButton.visible) {
            return this.opButton;
        }
        return this.deopButton;
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of((Object)this.getOpButton(), (Object)this.uninviteButton);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of((Object)this.getOpButton(), (Object)this.uninviteButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = !this.playerInfo.accepted ? -6250336 : (this.playerInfo.online ? -16711936 : -1);
        int j = this.getContentMiddleY() - 16;
        RealmsUtil.drawPlayerHead(context, this.getContentX(), j, 32, this.playerInfo.uuid);
        int k = this.getContentMiddleY() - RealmsPlayerTab.this.textRenderer.fontHeight / 2;
        context.drawTextWithShadow(RealmsPlayerTab.this.textRenderer, this.playerInfo.name, this.getContentX() + 8 + 32, k, i);
        int l = this.getContentMiddleY() - 10;
        int m = this.getContentRightEnd() - this.uninviteButton.getWidth();
        this.uninviteButton.setPosition(m, l);
        this.uninviteButton.render(context, mouseX, mouseY, deltaTicks);
        int n = m - this.getOpButton().getWidth() - 8;
        this.opButton.setPosition(n, l);
        this.opButton.render(context, mouseX, mouseY, deltaTicks);
        this.deopButton.setPosition(n, l);
        this.deopButton.render(context, mouseX, mouseY, deltaTicks);
    }
}
