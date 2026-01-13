/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.client.gui.widget.SquareWidgetEntry;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
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
    static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(LOGGER)).build());
    static final Text LAN_SCANNING_TEXT = Text.translatable("lanServer.scanning");
    static final Text CANNOT_RESOLVE_TEXT = Text.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
    static final Text CANNOT_CONNECT_TEXT = Text.translatable("multiplayer.status.cannot_connect").withColor(-65536);
    static final Text INCOMPATIBLE_TEXT = Text.translatable("multiplayer.status.incompatible");
    static final Text NO_CONNECTION_TEXT = Text.translatable("multiplayer.status.no_connection");
    static final Text PINGING_TEXT = Text.translatable("multiplayer.status.pinging");
    static final Text ONLINE_TEXT = Text.translatable("multiplayer.status.online");
    private final MultiplayerScreen screen;
    private final List<ServerEntry> servers = Lists.newArrayList();
    private final Entry scanningEntry = new ScanningEntry();
    private final List<LanServerEntry> lanServers = Lists.newArrayList();

    public MultiplayerServerListWidget(MultiplayerScreen screen, MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom);
        this.screen = screen;
    }

    private void updateEntries() {
        Entry entry = (Entry)this.getSelectedOrNull();
        ArrayList<ServerEntry> list = new ArrayList<ServerEntry>(this.servers);
        list.add((ServerEntry)this.scanningEntry);
        list.addAll(this.lanServers);
        this.replaceEntries(list);
        if (entry != null) {
            for (Entry entry2 : list) {
                if (!entry2.isOfSameType(entry)) continue;
                this.setSelected(entry2);
                break;
            }
        }
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.screen.updateButtonActivationStates();
    }

    public void setServers(ServerList servers) {
        this.servers.clear();
        for (int i = 0; i < servers.size(); ++i) {
            this.servers.add(new ServerEntry(this.screen, servers.get(i)));
        }
        this.updateEntries();
    }

    public void setLanServers(List<LanServerInfo> lanServers) {
        int i = lanServers.size() - this.lanServers.size();
        this.lanServers.clear();
        for (LanServerInfo lanServerInfo : lanServers) {
            this.lanServers.add(new LanServerEntry(this.screen, lanServerInfo));
        }
        this.updateEntries();
        for (int j = this.lanServers.size() - i; j < this.lanServers.size(); ++j) {
            LanServerEntry lanServerEntry = this.lanServers.get(j);
            int k = j - this.lanServers.size() + this.children().size();
            int l = this.getRowTop(k);
            int m = this.getRowBottom(k);
            if (m < this.getY() || l > this.getBottom()) continue;
            this.client.getNarratorManager().narrateSystemMessage(Text.translatable("multiplayer.lan.server_found", lanServerEntry.getMotdNarration()));
        }
    }

    @Override
    public int getRowWidth() {
        return 305;
    }

    public void onRemoved() {
    }

    @Environment(value=EnvType.CLIENT)
    public static class ScanningEntry
    extends Entry {
        private final MinecraftClient client = MinecraftClient.getInstance();
        private final LoadingWidget loadingWidget;

        public ScanningEntry() {
            this.loadingWidget = new LoadingWidget(this.client.textRenderer, LAN_SCANNING_TEXT);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            this.loadingWidget.setPosition(this.getContentMiddleX() - this.client.textRenderer.getWidth(LAN_SCANNING_TEXT) / 2, this.getContentY());
            this.loadingWidget.render(context, mouseX, mouseY, deltaTicks);
        }

        @Override
        public Text getNarration() {
            return LAN_SCANNING_TEXT;
        }

        @Override
        boolean isOfSameType(Entry entry) {
            return entry instanceof ScanningEntry;
        }

        @Override
        public void connect() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry>
    implements AutoCloseable {
        @Override
        public void close() {
        }

        abstract boolean isOfSameType(Entry var1);

        public abstract void connect();
    }

    @Environment(value=EnvType.CLIENT)
    public class ServerEntry
    extends Entry
    implements SquareWidgetEntry {
        private static final int field_64200 = 32;
        private static final int field_47852 = 5;
        private static final int field_47853 = 10;
        private static final int field_47854 = 8;
        private final MultiplayerScreen screen;
        private final MinecraftClient client;
        private final ServerInfo server;
        private final WorldIcon icon;
        private byte @Nullable [] favicon;
        private @Nullable List<Text> playerListSummary;
        private @Nullable Identifier statusIconTexture;
        private @Nullable Text statusTooltipText;

        protected ServerEntry(MultiplayerScreen screen, ServerInfo server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
            this.icon = WorldIcon.forServer(this.client.getTextureManager(), server.address);
            this.update();
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            byte[] bs;
            int j;
            int i;
            if (this.server.getStatus() == ServerInfo.Status.INITIAL) {
                this.server.setStatus(ServerInfo.Status.PINGING);
                this.server.label = ScreenTexts.EMPTY;
                this.server.playerCountLabel = ScreenTexts.EMPTY;
                SERVER_PINGER_THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getServerListPinger().add(this.server, () -> this.client.execute(this::saveFile), () -> {
                            this.server.setStatus(this.server.protocolVersion == SharedConstants.getGameVersion().protocolVersion() ? ServerInfo.Status.SUCCESSFUL : ServerInfo.Status.INCOMPATIBLE);
                            this.client.execute(this::update);
                        }, NetworkingBackend.remote(this.client.options.shouldUseNativeTransport()));
                    }
                    catch (UnknownHostException unknownHostException) {
                        this.server.setStatus(ServerInfo.Status.UNREACHABLE);
                        this.server.label = CANNOT_RESOLVE_TEXT;
                        this.client.execute(this::update);
                    }
                    catch (Exception exception) {
                        this.server.setStatus(ServerInfo.Status.UNREACHABLE);
                        this.server.label = CANNOT_CONNECT_TEXT;
                        this.client.execute(this::update);
                    }
                });
            }
            context.drawTextWithShadow(this.client.textRenderer, this.server.name, this.getContentX() + 32 + 3, this.getContentY() + 1, -1);
            List<OrderedText> list = this.client.textRenderer.wrapLines(this.server.label, this.getContentWidth() - 32 - 2);
            for (i = 0; i < Math.min(list.size(), 2); ++i) {
                context.drawTextWithShadow(this.client.textRenderer, list.get(i), this.getContentX() + 32 + 3, this.getContentY() + 12 + this.client.textRenderer.fontHeight * i, -8355712);
            }
            this.draw(context, this.getContentX(), this.getContentY(), this.icon.getTextureId());
            i = MultiplayerServerListWidget.this.children().indexOf(this);
            if (this.server.getStatus() == ServerInfo.Status.PINGING) {
                j = (int)(Util.getMeasuringTimeMs() / 100L + (long)(i * 2) & 7L);
                if (j > 4) {
                    j = 8 - j;
                }
                this.statusIconTexture = switch (j) {
                    default -> PINGING_1_TEXTURE;
                    case 1 -> PINGING_2_TEXTURE;
                    case 2 -> PINGING_3_TEXTURE;
                    case 3 -> PINGING_4_TEXTURE;
                    case 4 -> PINGING_5_TEXTURE;
                };
            }
            j = this.getContentRightEnd() - 10 - 5;
            if (this.statusIconTexture != null) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.statusIconTexture, j, this.getContentY(), 10, 8);
            }
            if (!Arrays.equals(bs = this.server.getFavicon(), this.favicon)) {
                if (this.uploadFavicon(bs)) {
                    this.favicon = bs;
                } else {
                    this.server.setFavicon(null);
                    this.saveFile();
                }
            }
            Text text = this.server.getStatus() == ServerInfo.Status.INCOMPATIBLE ? this.server.version.copy().formatted(Formatting.RED) : this.server.playerCountLabel;
            int k = this.client.textRenderer.getWidth(text);
            int l = j - k - 5;
            context.drawTextWithShadow(this.client.textRenderer, text, l, this.getContentY() + 1, -8355712);
            if (this.statusTooltipText != null && mouseX >= j && mouseX <= j + 10 && mouseY >= this.getContentY() && mouseY <= this.getContentY() + 8) {
                context.drawTooltip(this.statusTooltipText, mouseX, mouseY);
            } else if (this.playerListSummary != null && mouseX >= l && mouseX <= l + k && mouseY >= this.getContentY() && mouseY <= this.getContentY() - 1 + this.client.textRenderer.fontHeight) {
                context.drawTooltip(Lists.transform(this.playerListSummary, Text::asOrderedText), mouseX, mouseY);
            }
            if (this.client.options.getTouchscreen().getValue().booleanValue() || hovered) {
                context.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
                int m = mouseX - this.getContentX();
                int n = mouseY - this.getContentY();
                if (this.isRight(m, n, 32)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JOIN_HIGHLIGHTED_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                    MultiplayerServerListWidget.this.setCursor(context);
                } else {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, JOIN_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                }
                if (i > 0) {
                    if (this.isBottomLeft(m, n, 32)) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_UP_HIGHLIGHTED_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                        MultiplayerServerListWidget.this.setCursor(context);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_UP_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                    }
                }
                if (i < this.screen.getServerList().size() - 1) {
                    if (this.isTopLeft(m, n, 32)) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_HIGHLIGHTED_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                        MultiplayerServerListWidget.this.setCursor(context);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                    }
                }
            }
        }

        private void update() {
            this.playerListSummary = null;
            switch (this.server.getStatus()) {
                case INITIAL: 
                case PINGING: {
                    this.statusIconTexture = PING_1_TEXTURE;
                    this.statusTooltipText = PINGING_TEXT;
                    break;
                }
                case INCOMPATIBLE: {
                    this.statusIconTexture = INCOMPATIBLE_TEXTURE;
                    this.statusTooltipText = INCOMPATIBLE_TEXT;
                    this.playerListSummary = this.server.playerListSummary;
                    break;
                }
                case UNREACHABLE: {
                    this.statusIconTexture = UNREACHABLE_TEXTURE;
                    this.statusTooltipText = NO_CONNECTION_TEXT;
                    break;
                }
                case SUCCESSFUL: {
                    this.statusIconTexture = this.server.ping < 150L ? PING_5_TEXTURE : (this.server.ping < 300L ? PING_4_TEXTURE : (this.server.ping < 600L ? PING_3_TEXTURE : (this.server.ping < 1000L ? PING_2_TEXTURE : PING_1_TEXTURE)));
                    this.statusTooltipText = Text.translatable("multiplayer.status.ping", this.server.ping);
                    this.playerListSummary = this.server.playerListSummary;
                }
            }
        }

        public void saveFile() {
            this.screen.getServerList().saveFile();
        }

        protected void draw(DrawContext context, int x, int y, Identifier textureId) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, textureId, x, y, 0.0f, 0.0f, 32, 32, 32, 32);
        }

        private boolean uploadFavicon(byte @Nullable [] bytes) {
            if (bytes == null) {
                this.icon.destroy();
            } else {
                try {
                    this.icon.load(NativeImage.read(bytes));
                }
                catch (Throwable throwable) {
                    LOGGER.error("Invalid icon for server {} ({})", new Object[]{this.server.name, this.server.address, throwable});
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean keyPressed(KeyInput input) {
            if (input.isEnterOrSpace()) {
                this.connect();
                return true;
            }
            if (input.hasShift()) {
                MultiplayerServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
                int i = multiplayerServerListWidget.children().indexOf(this);
                if (i == -1) {
                    return true;
                }
                if (input.isDown() && i < this.screen.getServerList().size() - 1 || input.isUp() && i > 0) {
                    this.swapEntries(i, input.isDown() ? i + 1 : i - 1);
                    return true;
                }
            }
            return super.keyPressed(input);
        }

        @Override
        public void connect() {
            this.screen.connect(this.server);
        }

        private void swapEntries(int i, int j) {
            this.screen.getServerList().swapEntries(i, j);
            this.screen.serverListWidget.swapEntriesOnPositions(i, j);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            int j;
            int i = (int)click.x() - this.getContentX();
            if (this.isRight(i, j = (int)click.y() - this.getContentY(), 32)) {
                this.connect();
                return true;
            }
            int k = this.screen.serverListWidget.children().indexOf(this);
            if (k > 0 && this.isBottomLeft(i, j, 32)) {
                this.swapEntries(k, k - 1);
                return true;
            }
            if (k < this.screen.getServerList().size() - 1 && this.isTopLeft(i, j, 32)) {
                this.swapEntries(k, k + 1);
                return true;
            }
            if (doubled) {
                this.connect();
            }
            return super.mouseClicked(click, doubled);
        }

        public ServerInfo getServer() {
            return this.server;
        }

        @Override
        public Text getNarration() {
            MutableText mutableText = Text.empty();
            mutableText.append(Text.translatable("narrator.select", this.server.name));
            mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
            switch (this.server.getStatus()) {
                case INCOMPATIBLE: {
                    mutableText.append(INCOMPATIBLE_TEXT);
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.version.narration", this.server.version));
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
                    break;
                }
                case UNREACHABLE: {
                    mutableText.append(NO_CONNECTION_TEXT);
                    break;
                }
                case PINGING: {
                    mutableText.append(PINGING_TEXT);
                    break;
                }
                default: {
                    mutableText.append(ONLINE_TEXT);
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.ping.narration", this.server.ping));
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
                    if (this.server.players == null) break;
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.player_count.narration", this.server.players.online(), this.server.players.max()));
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Texts.join(this.server.playerListSummary, Text.literal(", ")));
                }
            }
            return mutableText;
        }

        @Override
        public void close() {
            this.icon.close();
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        boolean isOfSameType(Entry entry) {
            if (!(entry instanceof ServerEntry)) return false;
            ServerEntry serverEntry = (ServerEntry)entry;
            if (serverEntry.server != this.server) return false;
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LanServerEntry
    extends Entry {
        private static final int field_32386 = 32;
        private static final Text TITLE_TEXT = Text.translatable("lanServer.title");
        private static final Text HIDDEN_ADDRESS_TEXT = Text.translatable("selectServer.hiddenAddress");
        private final MultiplayerScreen screen;
        protected final MinecraftClient client;
        protected final LanServerInfo server;

        protected LanServerEntry(MultiplayerScreen screen, LanServerInfo server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawTextWithShadow(this.client.textRenderer, TITLE_TEXT, this.getContentX() + 32 + 3, this.getContentY() + 1, -1);
            context.drawTextWithShadow(this.client.textRenderer, this.server.getMotd(), this.getContentX() + 32 + 3, this.getContentY() + 12, -8355712);
            if (this.client.options.hideServerAddress) {
                context.drawTextWithShadow(this.client.textRenderer, HIDDEN_ADDRESS_TEXT, this.getContentX() + 32 + 3, this.getContentY() + 12 + 11, -8355712);
            } else {
                context.drawTextWithShadow(this.client.textRenderer, this.server.getAddressPort(), this.getContentX() + 32 + 3, this.getContentY() + 12 + 11, -8355712);
            }
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            if (doubled) {
                this.connect();
            }
            return super.mouseClicked(click, doubled);
        }

        @Override
        public boolean keyPressed(KeyInput input) {
            if (input.isEnterOrSpace()) {
                this.connect();
                return true;
            }
            return super.keyPressed(input);
        }

        @Override
        public void connect() {
            this.screen.connect(new ServerInfo(this.server.getMotd(), this.server.getAddressPort(), ServerInfo.ServerType.LAN));
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.getMotdNarration());
        }

        public Text getMotdNarration() {
            return Text.empty().append(TITLE_TEXT).append(ScreenTexts.SPACE).append(this.server.getMotd());
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        boolean isOfSameType(Entry entry) {
            if (!(entry instanceof LanServerEntry)) return false;
            LanServerEntry lanServerEntry = (LanServerEntry)entry;
            if (lanServerEntry.server != this.server) return false;
            return true;
        }
    }
}
