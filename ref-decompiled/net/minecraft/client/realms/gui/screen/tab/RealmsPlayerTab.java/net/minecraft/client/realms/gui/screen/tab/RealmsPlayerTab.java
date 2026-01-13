/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen.tab;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfirmScreen;
import net.minecraft.client.realms.gui.screen.RealmsInviteScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
class RealmsPlayerTab
extends GridScreenTab
implements RealmsUpdatableTab {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Text TITLE = Text.translatable("mco.configure.world.players.title");
    static final Text QUESTION_TEXT = Text.translatable("mco.question");
    private static final int field_49462 = 8;
    final RealmsConfigureWorldScreen screen;
    final MinecraftClient client;
    final TextRenderer textRenderer;
    RealmsServer serverData;
    final InvitedObjectSelectionList playerList;

    RealmsPlayerTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer serverData) {
        super(TITLE);
        this.screen = screen;
        this.client = client;
        this.textRenderer = screen.getTextRenderer();
        this.serverData = serverData;
        GridWidget.Adder adder = this.grid.setSpacing(8).createAdder(1);
        this.playerList = adder.add(new InvitedObjectSelectionList(screen.width, this.getPlayerListHeight()), Positioner.create().alignTop().alignHorizontalCenter());
        adder.add(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.invite"), button -> client.setScreen(new RealmsInviteScreen(screen, serverData))).build(), Positioner.create().alignBottom().alignHorizontalCenter());
        this.update(serverData);
    }

    public int getPlayerListHeight() {
        return this.screen.getContentHeight() - 20 - 16;
    }

    @Override
    public void refreshGrid(ScreenRect tabArea) {
        this.playerList.position(this.screen.width, this.getPlayerListHeight(), this.screen.layout.getHeaderHeight());
        super.refreshGrid(tabArea);
    }

    @Override
    public void update(RealmsServer server) {
        this.serverData = server;
        this.playerList.refreshEntries(server);
    }

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionList
    extends ElementListWidget<PlayerTabEntry> {
        private static final int field_49472 = 36;

        public InvitedObjectSelectionList(int width, int height) {
            super(MinecraftClient.getInstance(), width, height, RealmsPlayerTab.this.screen.getHeaderHeight(), 36);
        }

        void refreshEntries(RealmsServer serverData) {
            this.clearEntries();
            this.addEntries(serverData);
        }

        private void addEntries(RealmsServer serverData) {
            HeaderEntry headerEntry = new HeaderEntry();
            this.addEntry(headerEntry, headerEntry.getHeight(RealmsPlayerTab.this.textRenderer.fontHeight));
            for (InvitedObjectSelectionListEntry invitedObjectSelectionListEntry : serverData.players.stream().map(playerInfo -> new InvitedObjectSelectionListEntry((PlayerInfo)playerInfo)).toList()) {
                this.addEntry(invitedObjectSelectionListEntry);
            }
        }

        @Override
        protected void drawMenuListBackground(DrawContext context) {
        }

        @Override
        protected void drawHeaderAndFooterSeparators(DrawContext context) {
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class HeaderEntry
    extends PlayerTabEntry {
        private String invitedPlayerCount = "";
        private final NarratedMultilineTextWidget textWidget;

        public HeaderEntry() {
            MutableText text = Text.translatable("mco.configure.world.invited.number", "").formatted(Formatting.UNDERLINE);
            this.textWidget = NarratedMultilineTextWidget.builder(text, RealmsPlayerTab.this.textRenderer).alwaysShowBorders(false).backgroundRendering(NarratedMultilineTextWidget.BackgroundRendering.ON_FOCUS).build();
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            String string;
            String string2 = string = RealmsPlayerTab.this.serverData.players != null ? Integer.toString(RealmsPlayerTab.this.serverData.players.size()) : "0";
            if (!string.equals(this.invitedPlayerCount)) {
                this.invitedPlayerCount = string;
                MutableText text = Text.translatable("mco.configure.world.invited.number", string).formatted(Formatting.UNDERLINE);
                this.textWidget.setMessage(text);
            }
            this.textWidget.setPosition(RealmsPlayerTab.this.playerList.getRowLeft() + RealmsPlayerTab.this.playerList.getRowWidth() / 2 - this.textWidget.getWidth() / 2, this.getY() + this.getHeight() / 2 - this.textWidget.getHeight() / 2);
            this.textWidget.render(context, mouseX, mouseY, deltaTicks);
        }

        int getHeight(int innerHeight) {
            return innerHeight + this.textWidget.getMargin() * 2;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.textWidget);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.textWidget);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionListEntry
    extends PlayerTabEntry {
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

        public InvitedObjectSelectionListEntry(PlayerInfo playerInfo) {
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

    @Environment(value=EnvType.CLIENT)
    static abstract class PlayerTabEntry
    extends ElementListWidget.Entry<PlayerTabEntry> {
        PlayerTabEntry() {
        }
    }
}
