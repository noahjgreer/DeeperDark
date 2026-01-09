package net.minecraft.client.realms.gui.screen.tab;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfirmScreen;
import net.minecraft.client.realms.gui.screen.RealmsInviteScreen;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
class RealmsPlayerTab extends GridScreenTab implements RealmsUpdatableTab {
   static final Logger LOGGER = LogUtils.getLogger();
   static final Text TITLE = Text.translatable("mco.configure.world.players.title");
   static final Text QUESTION_TEXT = Text.translatable("mco.question");
   private static final int field_49462 = 8;
   final RealmsConfigureWorldScreen screen;
   final MinecraftClient client;
   RealmsServer serverData;
   private final InvitedObjectSelectionList playerList;

   RealmsPlayerTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer server) {
      super(TITLE);
      this.screen = screen;
      this.client = client;
      this.serverData = server;
      GridWidget.Adder adder = this.grid.setSpacing(8).createAdder(1);
      this.playerList = (InvitedObjectSelectionList)adder.add(new InvitedObjectSelectionList(screen.width, this.getPlayerListHeight()), Positioner.create().alignTop().alignHorizontalCenter());
      adder.add(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.invite"), (button) -> {
         client.setScreen(new RealmsInviteScreen(screen, server));
      }).build(), Positioner.create().alignBottom().alignHorizontalCenter());
      this.update(server);
   }

   public int getPlayerListHeight() {
      return this.screen.getContentHeight() - 20 - 16;
   }

   public void refreshGrid(ScreenRect tabArea) {
      this.playerList.setDimensions(this.screen.width, this.getPlayerListHeight());
      super.refreshGrid(tabArea);
   }

   public void update(RealmsServer server) {
      this.serverData = server;
      this.playerList.children().clear();
      Iterator var2 = server.players.iterator();

      while(var2.hasNext()) {
         PlayerInfo playerInfo = (PlayerInfo)var2.next();
         this.playerList.children().add(new InvitedObjectSelectionListEntry(playerInfo));
      }

   }

   @Environment(EnvType.CLIENT)
   class InvitedObjectSelectionList extends ElementListWidget {
      private static final int field_49472 = 36;

      public InvitedObjectSelectionList(final int width, final int height) {
         MinecraftClient var10001 = MinecraftClient.getInstance();
         int var10004 = RealmsPlayerTab.this.screen.getHeaderHeight();
         Objects.requireNonNull(RealmsPlayerTab.this.screen.getTextRenderer());
         super(var10001, width, height, var10004, 36, (int)(9.0F * 1.5F));
      }

      protected void renderHeader(DrawContext context, int x, int y) {
         String string = RealmsPlayerTab.this.serverData.players != null ? Integer.toString(RealmsPlayerTab.this.serverData.players.size()) : "0";
         Text text = Text.translatable("mco.configure.world.invited.number", string).formatted(Formatting.UNDERLINE);
         context.drawTextWithShadow(RealmsPlayerTab.this.screen.getTextRenderer(), (Text)text, x + this.getRowWidth() / 2 - RealmsPlayerTab.this.screen.getTextRenderer().getWidth((StringVisitable)text) / 2, y, -1);
      }

      protected void drawMenuListBackground(DrawContext context) {
      }

      protected void drawHeaderAndFooterSeparators(DrawContext context) {
      }

      public int getRowWidth() {
         return 300;
      }
   }

   @Environment(EnvType.CLIENT)
   private class InvitedObjectSelectionListEntry extends ElementListWidget.Entry {
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

      public InvitedObjectSelectionListEntry(final PlayerInfo playerInfo) {
         this.playerInfo = playerInfo;
         int i = RealmsPlayerTab.this.serverData.players.indexOf(this.playerInfo);
         this.opButton = TextIconButtonWidget.builder(NORMAL_TOOLTIP_TEXT, (button) -> {
            this.op(i);
         }, false).texture(MAKE_OPERATOR_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth((StringVisitable)NORMAL_TOOLTIP_TEXT)).narration((textSupplier) -> {
            return ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.getName()), (Text)textSupplier.get(), Text.translatable("narration.cycle_button.usage.focused", OPS_TOOLTIP_TEXT));
         }).build();
         this.deopButton = TextIconButtonWidget.builder(OPS_TOOLTIP_TEXT, (button) -> {
            this.deop(i);
         }, false).texture(REMOVE_OPERATOR_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth((StringVisitable)OPS_TOOLTIP_TEXT)).narration((textSupplier) -> {
            return ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.getName()), (Text)textSupplier.get(), Text.translatable("narration.cycle_button.usage.focused", NORMAL_TOOLTIP_TEXT));
         }).build();
         this.uninviteButton = TextIconButtonWidget.builder(REMOVE_TOOLTIP_TEXT, (button) -> {
            this.uninvite(i);
         }, false).texture(REMOVE_PLAYER_TEXTURE, 8, 7).width(16 + RealmsPlayerTab.this.screen.getTextRenderer().getWidth((StringVisitable)REMOVE_TOOLTIP_TEXT)).narration((textSupplier) -> {
            return ScreenTexts.joinSentences(Text.translatable("mco.invited.player.narration", playerInfo.getName()), (Text)textSupplier.get());
         }).build();
         this.refreshOpButtonsVisibility();
      }

      private void op(int index) {
         UUID uUID = ((PlayerInfo)RealmsPlayerTab.this.serverData.players.get(index)).getUuid();
         RealmsUtil.method_72217((realmsClient) -> {
            return realmsClient.op(RealmsPlayerTab.this.serverData.id, uUID);
         }, (realmsServiceException) -> {
            RealmsPlayerTab.LOGGER.error("Couldn't op the user", realmsServiceException);
         }).thenAcceptAsync((ops) -> {
            this.setOps(ops);
            this.refreshOpButtonsVisibility();
            this.setFocused(this.deopButton);
         }, RealmsPlayerTab.this.client);
      }

      private void deop(int index) {
         UUID uUID = ((PlayerInfo)RealmsPlayerTab.this.serverData.players.get(index)).getUuid();
         RealmsUtil.method_72217((realmsClient) -> {
            return realmsClient.deop(RealmsPlayerTab.this.serverData.id, uUID);
         }, (realmsServiceException) -> {
            RealmsPlayerTab.LOGGER.error("Couldn't deop the user", realmsServiceException);
         }).thenAcceptAsync((ops) -> {
            this.setOps(ops);
            this.refreshOpButtonsVisibility();
            this.setFocused(this.opButton);
         }, RealmsPlayerTab.this.client);
      }

      private void uninvite(int index) {
         if (index >= 0 && index < RealmsPlayerTab.this.serverData.players.size()) {
            PlayerInfo playerInfo = (PlayerInfo)RealmsPlayerTab.this.serverData.players.get(index);
            RealmsConfirmScreen realmsConfirmScreen = new RealmsConfirmScreen((confirmed) -> {
               if (confirmed) {
                  RealmsUtil.method_72216((realmsClient) -> {
                     realmsClient.uninvite(RealmsPlayerTab.this.serverData.id, playerInfo.getUuid());
                  }, (realmsServiceException) -> {
                     RealmsPlayerTab.LOGGER.error("Couldn't uninvite user", realmsServiceException);
                  });
                  RealmsPlayerTab.this.serverData.players.remove(index);
                  RealmsPlayerTab.this.update(RealmsPlayerTab.this.serverData);
               }

               RealmsPlayerTab.this.client.setScreen(RealmsPlayerTab.this.screen);
            }, RealmsPlayerTab.QUESTION_TEXT, Text.translatable("mco.configure.world.uninvite.player", playerInfo.getName()));
            RealmsPlayerTab.this.client.setScreen(realmsConfirmScreen);
         }

      }

      private void setOps(Ops ops) {
         Iterator var2 = RealmsPlayerTab.this.serverData.players.iterator();

         while(var2.hasNext()) {
            PlayerInfo playerInfo = (PlayerInfo)var2.next();
            playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
         }

      }

      private void refreshOpButtonsVisibility() {
         this.opButton.visible = !this.playerInfo.isOperator();
         this.deopButton.visible = !this.opButton.visible;
      }

      private ButtonWidget getOpButton() {
         return this.opButton.visible ? this.opButton : this.deopButton;
      }

      public List children() {
         return ImmutableList.of(this.getOpButton(), this.uninviteButton);
      }

      public List selectableChildren() {
         return ImmutableList.of(this.getOpButton(), this.uninviteButton);
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         int i;
         if (!this.playerInfo.isAccepted()) {
            i = -6250336;
         } else if (this.playerInfo.isOnline()) {
            i = -16711936;
         } else {
            i = -1;
         }

         int j = y + entryHeight / 2 - 16;
         RealmsUtil.drawPlayerHead(context, x, j, 32, this.playerInfo.getUuid());
         int var10000 = y + entryHeight / 2;
         Objects.requireNonNull(RealmsPlayerTab.this.screen.getTextRenderer());
         int k = var10000 - 9 / 2;
         context.drawTextWithShadow(RealmsPlayerTab.this.screen.getTextRenderer(), this.playerInfo.getName(), x + 8 + 32, k, i);
         int l = y + entryHeight / 2 - 10;
         int m = x + entryWidth - this.uninviteButton.getWidth();
         this.uninviteButton.setPosition(m, l);
         this.uninviteButton.render(context, mouseX, mouseY, tickProgress);
         int n = m - this.getOpButton().getWidth() - 8;
         this.opButton.setPosition(n, l);
         this.opButton.render(context, mouseX, mouseY, tickProgress);
         this.deopButton.setPosition(n, l);
         this.deopButton.render(context, mouseX, mouseY, tickProgress);
      }
   }
}
