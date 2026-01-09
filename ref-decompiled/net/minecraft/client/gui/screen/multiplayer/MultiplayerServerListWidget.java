package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListWidget extends AlwaysSelectedEntryListWidget {
   static final Identifier INCOMPATIBLE_TEXTURE = Identifier.ofVanilla("server_list/incompatible");
   static final Identifier UNREACHABLE_TEXTURE = Identifier.ofVanilla("server_list/unreachable");
   static final Identifier PING_1_TEXTURE = Identifier.ofVanilla("server_list/ping_1");
   static final Identifier PING_2_TEXTURE = Identifier.ofVanilla("server_list/ping_2");
   static final Identifier PING_3_TEXTURE = Identifier.ofVanilla("server_list/ping_3");
   static final Identifier PING_4_TEXTURE = Identifier.ofVanilla("server_list/ping_4");
   static final Identifier PING_5_TEXTURE = Identifier.ofVanilla("server_list/ping_5");
   static final Identifier PINGING_1_TEXTURE = Identifier.ofVanilla("server_list/pinging_1");
   static final Identifier PINGING_2_TEXTURE = Identifier.ofVanilla("server_list/pinging_2");
   static final Identifier PINGING_3_TEXTURE = Identifier.ofVanilla("server_list/pinging_3");
   static final Identifier PINGING_4_TEXTURE = Identifier.ofVanilla("server_list/pinging_4");
   static final Identifier PINGING_5_TEXTURE = Identifier.ofVanilla("server_list/pinging_5");
   static final Identifier JOIN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("server_list/join_highlighted");
   static final Identifier JOIN_TEXTURE = Identifier.ofVanilla("server_list/join");
   static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("server_list/move_up_highlighted");
   static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla("server_list/move_up");
   static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("server_list/move_down_highlighted");
   static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla("server_list/move_down");
   static final Logger LOGGER = LogUtils.getLogger();
   static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL;
   static final Text LAN_SCANNING_TEXT;
   static final Text CANNOT_RESOLVE_TEXT;
   static final Text CANNOT_CONNECT_TEXT;
   static final Text INCOMPATIBLE_TEXT;
   static final Text NO_CONNECTION_TEXT;
   static final Text PINGING_TEXT;
   static final Text ONLINE_TEXT;
   private final MultiplayerScreen screen;
   private final List servers = Lists.newArrayList();
   private final Entry scanningEntry = new ScanningEntry();
   private final List lanServers = Lists.newArrayList();

   public MultiplayerServerListWidget(MultiplayerScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
      super(client, width, height, top, bottom);
      this.screen = screen;
   }

   private void updateEntries() {
      this.clearEntries();
      this.servers.forEach((server) -> {
         this.addEntry(server);
      });
      this.addEntry(this.scanningEntry);
      this.lanServers.forEach((lanServer) -> {
         this.addEntry(lanServer);
      });
   }

   public void setSelected(@Nullable Entry entry) {
      super.setSelected(entry);
      this.screen.updateButtonActivationStates();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      Entry entry = (Entry)this.getSelectedOrNull();
      return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
   }

   public void setServers(ServerList servers) {
      this.servers.clear();

      for(int i = 0; i < servers.size(); ++i) {
         this.servers.add(new ServerEntry(this.screen, servers.get(i)));
      }

      this.updateEntries();
   }

   public void setLanServers(List lanServers) {
      int i = lanServers.size() - this.lanServers.size();
      this.lanServers.clear();
      Iterator var3 = lanServers.iterator();

      while(var3.hasNext()) {
         LanServerInfo lanServerInfo = (LanServerInfo)var3.next();
         this.lanServers.add(new LanServerEntry(this.screen, lanServerInfo));
      }

      this.updateEntries();

      for(int j = this.lanServers.size() - i; j < this.lanServers.size(); ++j) {
         LanServerEntry lanServerEntry = (LanServerEntry)this.lanServers.get(j);
         int k = j - this.lanServers.size() + this.children().size();
         int l = this.getRowTop(k);
         int m = this.getRowBottom(k);
         if (m >= this.getY() && l <= this.getBottom()) {
            this.client.getNarratorManager().narrateSystemMessage(Text.translatable("multiplayer.lan.server_found", lanServerEntry.getMotdNarration()));
         }
      }

   }

   public int getRowWidth() {
      return 305;
   }

   public void onRemoved() {
   }

   static {
      SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build());
      LAN_SCANNING_TEXT = Text.translatable("lanServer.scanning");
      CANNOT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
      CANNOT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").withColor(-65536);
      INCOMPATIBLE_TEXT = Text.translatable("multiplayer.status.incompatible");
      NO_CONNECTION_TEXT = Text.translatable("multiplayer.status.no_connection");
      PINGING_TEXT = Text.translatable("multiplayer.status.pinging");
      ONLINE_TEXT = Text.translatable("multiplayer.status.online");
   }

   @Environment(EnvType.CLIENT)
   public static class ScanningEntry extends Entry {
      private final MinecraftClient client = MinecraftClient.getInstance();

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         int var10000 = y + entryHeight / 2;
         Objects.requireNonNull(this.client.textRenderer);
         int i = var10000 - 9 / 2;
         context.drawTextWithShadow(this.client.textRenderer, (Text)MultiplayerServerListWidget.LAN_SCANNING_TEXT, this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth((StringVisitable)MultiplayerServerListWidget.LAN_SCANNING_TEXT) / 2, i, -1);
         String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
         TextRenderer var10001 = this.client.textRenderer;
         int var10003 = this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2;
         Objects.requireNonNull(this.client.textRenderer);
         context.drawTextWithShadow(var10001, string, var10003, i + 9, -8355712);
      }

      public Text getNarration() {
         return MultiplayerServerListWidget.LAN_SCANNING_TEXT;
      }
   }

   @Environment(EnvType.CLIENT)
   public abstract static class Entry extends AlwaysSelectedEntryListWidget.Entry implements AutoCloseable {
      public void close() {
      }
   }

   @Environment(EnvType.CLIENT)
   public class ServerEntry extends Entry {
      private static final int field_32387 = 32;
      private static final int field_32388 = 32;
      private static final int field_47852 = 5;
      private static final int field_47853 = 10;
      private static final int field_47854 = 8;
      private final MultiplayerScreen screen;
      private final MinecraftClient client;
      private final ServerInfo server;
      private final WorldIcon icon;
      @Nullable
      private byte[] favicon;
      private long time;
      @Nullable
      private List playerListSummary;
      @Nullable
      private Identifier statusIconTexture;
      @Nullable
      private Text statusTooltipText;

      protected ServerEntry(final MultiplayerScreen screen, final ServerInfo server) {
         this.screen = screen;
         this.server = server;
         this.client = MinecraftClient.getInstance();
         this.icon = WorldIcon.forServer(this.client.getTextureManager(), server.address);
         this.update();
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         if (this.server.getStatus() == ServerInfo.Status.INITIAL) {
            this.server.setStatus(ServerInfo.Status.PINGING);
            this.server.label = ScreenTexts.EMPTY;
            this.server.playerCountLabel = ScreenTexts.EMPTY;
            MultiplayerServerListWidget.SERVER_PINGER_THREAD_POOL.submit(() -> {
               try {
                  this.screen.getServerListPinger().add(this.server, () -> {
                     this.client.execute(this::saveFile);
                  }, () -> {
                     this.server.setStatus(this.server.protocolVersion == SharedConstants.getGameVersion().protocolVersion() ? ServerInfo.Status.SUCCESSFUL : ServerInfo.Status.INCOMPATIBLE);
                     this.client.execute(this::update);
                  });
               } catch (UnknownHostException var2) {
                  this.server.setStatus(ServerInfo.Status.UNREACHABLE);
                  this.server.label = MultiplayerServerListWidget.CANNOT_RESOLVE_TEXT;
                  this.client.execute(this::update);
               } catch (Exception var3) {
                  this.server.setStatus(ServerInfo.Status.UNREACHABLE);
                  this.server.label = MultiplayerServerListWidget.CANNOT_CONNECT_TEXT;
                  this.client.execute(this::update);
               }

            });
         }

         context.drawTextWithShadow(this.client.textRenderer, (String)this.server.name, x + 32 + 3, y + 1, -1);
         List list = this.client.textRenderer.wrapLines(this.server.label, entryWidth - 32 - 2);

         int i;
         for(i = 0; i < Math.min(list.size(), 2); ++i) {
            TextRenderer var10001 = this.client.textRenderer;
            OrderedText var10002 = (OrderedText)list.get(i);
            int var10003 = x + 32 + 3;
            int var10004 = y + 12;
            Objects.requireNonNull(this.client.textRenderer);
            context.drawTextWithShadow(var10001, var10002, var10003, var10004 + 9 * i, -8355712);
         }

         this.draw(context, x, y, this.icon.getTextureId());
         if (this.server.getStatus() == ServerInfo.Status.PINGING) {
            i = (int)(Util.getMeasuringTimeMs() / 100L + (long)(index * 2) & 7L);
            if (i > 4) {
               i = 8 - i;
            }

            Identifier var19;
            switch (i) {
               case 1:
                  var19 = MultiplayerServerListWidget.PINGING_2_TEXTURE;
                  break;
               case 2:
                  var19 = MultiplayerServerListWidget.PINGING_3_TEXTURE;
                  break;
               case 3:
                  var19 = MultiplayerServerListWidget.PINGING_4_TEXTURE;
                  break;
               case 4:
                  var19 = MultiplayerServerListWidget.PINGING_5_TEXTURE;
                  break;
               default:
                  var19 = MultiplayerServerListWidget.PINGING_1_TEXTURE;
            }

            this.statusIconTexture = var19;
         }

         i = x + entryWidth - 10 - 5;
         if (this.statusIconTexture != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.statusIconTexture, i, y, 10, 8);
         }

         byte[] bs = this.server.getFavicon();
         if (!Arrays.equals(bs, this.favicon)) {
            if (this.uploadFavicon(bs)) {
               this.favicon = bs;
            } else {
               this.server.setFavicon((byte[])null);
               this.saveFile();
            }
         }

         Text text = this.server.getStatus() == ServerInfo.Status.INCOMPATIBLE ? this.server.version.copy().formatted(Formatting.RED) : this.server.playerCountLabel;
         int j = this.client.textRenderer.getWidth((StringVisitable)text);
         int k = i - j - 5;
         context.drawTextWithShadow(this.client.textRenderer, (Text)text, k, y + 1, -8355712);
         if (this.statusTooltipText != null && mouseX >= i && mouseX <= i + 10 && mouseY >= y && mouseY <= y + 8) {
            context.drawTooltip(this.statusTooltipText, mouseX, mouseY);
         } else if (this.playerListSummary != null && mouseX >= k && mouseX <= k + j && mouseY >= y) {
            int var20 = y - 1;
            Objects.requireNonNull(this.client.textRenderer);
            if (mouseY <= var20 + 9) {
               context.drawTooltip(Lists.transform(this.playerListSummary, Text::asOrderedText), mouseX, mouseY);
            }
         }

         if ((Boolean)this.client.options.getTouchscreen().getValue() || hovered) {
            context.fill(x, y, x + 32, y + 32, -1601138544);
            int l = mouseX - x;
            int m = mouseY - y;
            if (this.canConnect()) {
               if (l < 32 && l > 16) {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.JOIN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
               } else {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.JOIN_TEXTURE, x, y, 32, 32);
               }
            }

            if (index > 0) {
               if (l < 16 && m < 16) {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
               } else {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_UP_TEXTURE, x, y, 32, 32);
               }
            }

            if (index < this.screen.getServerList().size() - 1) {
               if (l < 16 && m > 16) {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
               } else {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_DOWN_TEXTURE, x, y, 32, 32);
               }
            }
         }

      }

      private void update() {
         this.playerListSummary = null;
         switch (this.server.getStatus()) {
            case INITIAL:
            case PINGING:
               this.statusIconTexture = MultiplayerServerListWidget.PING_1_TEXTURE;
               this.statusTooltipText = MultiplayerServerListWidget.PINGING_TEXT;
               break;
            case INCOMPATIBLE:
               this.statusIconTexture = MultiplayerServerListWidget.INCOMPATIBLE_TEXTURE;
               this.statusTooltipText = MultiplayerServerListWidget.INCOMPATIBLE_TEXT;
               this.playerListSummary = this.server.playerListSummary;
               break;
            case UNREACHABLE:
               this.statusIconTexture = MultiplayerServerListWidget.UNREACHABLE_TEXTURE;
               this.statusTooltipText = MultiplayerServerListWidget.NO_CONNECTION_TEXT;
               break;
            case SUCCESSFUL:
               if (this.server.ping < 150L) {
                  this.statusIconTexture = MultiplayerServerListWidget.PING_5_TEXTURE;
               } else if (this.server.ping < 300L) {
                  this.statusIconTexture = MultiplayerServerListWidget.PING_4_TEXTURE;
               } else if (this.server.ping < 600L) {
                  this.statusIconTexture = MultiplayerServerListWidget.PING_3_TEXTURE;
               } else if (this.server.ping < 1000L) {
                  this.statusIconTexture = MultiplayerServerListWidget.PING_2_TEXTURE;
               } else {
                  this.statusIconTexture = MultiplayerServerListWidget.PING_1_TEXTURE;
               }

               this.statusTooltipText = Text.translatable("multiplayer.status.ping", this.server.ping);
               this.playerListSummary = this.server.playerListSummary;
         }

      }

      public void saveFile() {
         this.screen.getServerList().saveFile();
      }

      protected void draw(DrawContext context, int x, int y, Identifier textureId) {
         context.drawTexture(RenderPipelines.GUI_TEXTURED, textureId, x, y, 0.0F, 0.0F, 32, 32, 32, 32);
      }

      private boolean canConnect() {
         return true;
      }

      private boolean uploadFavicon(@Nullable byte[] bytes) {
         if (bytes == null) {
            this.icon.destroy();
         } else {
            try {
               this.icon.load(NativeImage.read(bytes));
            } catch (Throwable var3) {
               MultiplayerServerListWidget.LOGGER.error("Invalid icon for server {} ({})", new Object[]{this.server.name, this.server.address, var3});
               return false;
            }
         }

         return true;
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         if (Screen.hasShiftDown()) {
            MultiplayerServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
            int i = multiplayerServerListWidget.children().indexOf(this);
            if (i == -1) {
               return true;
            }

            if (keyCode == 264 && i < this.screen.getServerList().size() - 1 || keyCode == 265 && i > 0) {
               this.swapEntries(i, keyCode == 264 ? i + 1 : i - 1);
               return true;
            }
         }

         return super.keyPressed(keyCode, scanCode, modifiers);
      }

      private void swapEntries(int i, int j) {
         this.screen.getServerList().swapEntries(i, j);
         this.screen.serverListWidget.setServers(this.screen.getServerList());
         Entry entry = (Entry)this.screen.serverListWidget.children().get(j);
         this.screen.serverListWidget.setSelected(entry);
         MultiplayerServerListWidget.this.ensureVisible(entry);
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         double d = mouseX - (double)MultiplayerServerListWidget.this.getRowLeft();
         double e = mouseY - (double)MultiplayerServerListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
         if (d <= 32.0) {
            if (d < 32.0 && d > 16.0 && this.canConnect()) {
               this.screen.select(this);
               this.screen.connect();
               return true;
            }

            int i = this.screen.serverListWidget.children().indexOf(this);
            if (d < 16.0 && e < 16.0 && i > 0) {
               this.swapEntries(i, i - 1);
               return true;
            }

            if (d < 16.0 && e > 16.0 && i < this.screen.getServerList().size() - 1) {
               this.swapEntries(i, i + 1);
               return true;
            }
         }

         this.screen.select(this);
         if (Util.getMeasuringTimeMs() - this.time < 250L) {
            this.screen.connect();
         }

         this.time = Util.getMeasuringTimeMs();
         return super.mouseClicked(mouseX, mouseY, button);
      }

      public ServerInfo getServer() {
         return this.server;
      }

      public Text getNarration() {
         MutableText mutableText = Text.empty();
         mutableText.append((Text)Text.translatable("narrator.select", this.server.name));
         mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
         switch (this.server.getStatus()) {
            case PINGING:
               mutableText.append(MultiplayerServerListWidget.PINGING_TEXT);
               break;
            case INCOMPATIBLE:
               mutableText.append(MultiplayerServerListWidget.INCOMPATIBLE_TEXT);
               mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
               mutableText.append((Text)Text.translatable("multiplayer.status.version.narration", this.server.version));
               mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
               mutableText.append((Text)Text.translatable("multiplayer.status.motd.narration", this.server.label));
               break;
            case UNREACHABLE:
               mutableText.append(MultiplayerServerListWidget.NO_CONNECTION_TEXT);
               break;
            default:
               mutableText.append(MultiplayerServerListWidget.ONLINE_TEXT);
               mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
               mutableText.append((Text)Text.translatable("multiplayer.status.ping.narration", this.server.ping));
               mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
               mutableText.append((Text)Text.translatable("multiplayer.status.motd.narration", this.server.label));
               if (this.server.players != null) {
                  mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                  mutableText.append((Text)Text.translatable("multiplayer.status.player_count.narration", this.server.players.online(), this.server.players.max()));
                  mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                  mutableText.append(Texts.join(this.server.playerListSummary, (Text)Text.literal(", ")));
               }
         }

         return mutableText;
      }

      public void close() {
         this.icon.close();
      }
   }

   @Environment(EnvType.CLIENT)
   public static class LanServerEntry extends Entry {
      private static final int field_32386 = 32;
      private static final Text TITLE_TEXT = Text.translatable("lanServer.title");
      private static final Text HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress");
      private final MultiplayerScreen screen;
      protected final MinecraftClient client;
      protected final LanServerInfo server;
      private long time;

      protected LanServerEntry(MultiplayerScreen screen, LanServerInfo server) {
         this.screen = screen;
         this.server = server;
         this.client = MinecraftClient.getInstance();
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         context.drawTextWithShadow(this.client.textRenderer, (Text)TITLE_TEXT, x + 32 + 3, y + 1, -1);
         context.drawTextWithShadow(this.client.textRenderer, this.server.getMotd(), x + 32 + 3, y + 12, -8355712);
         if (this.client.options.hideServerAddress) {
            context.drawTextWithShadow(this.client.textRenderer, HIDDEN_ADDRESS_TEXT, x + 32 + 3, y + 12 + 11, -13619152);
         } else {
            context.drawTextWithShadow(this.client.textRenderer, this.server.getAddressPort(), x + 32 + 3, y + 12 + 11, -13619152);
         }

      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         this.screen.select(this);
         if (Util.getMeasuringTimeMs() - this.time < 250L) {
            this.screen.connect();
         }

         this.time = Util.getMeasuringTimeMs();
         return super.mouseClicked(mouseX, mouseY, button);
      }

      public LanServerInfo getLanServerEntry() {
         return this.server;
      }

      public Text getNarration() {
         return Text.translatable("narrator.select", this.getMotdNarration());
      }

      public Text getMotdNarration() {
         return Text.empty().append(TITLE_TEXT).append(ScreenTexts.SPACE).append(this.server.getMotd());
      }
   }
}
