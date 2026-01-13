/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.gui.widget.SquareWidgetEntry;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ServerInfo;
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
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListWidget.ServerEntry
extends MultiplayerServerListWidget.Entry
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

    protected MultiplayerServerListWidget.ServerEntry(MultiplayerScreen screen, ServerInfo server) {
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
    boolean isOfSameType(MultiplayerServerListWidget.Entry entry) {
        if (!(entry instanceof MultiplayerServerListWidget.ServerEntry)) return false;
        MultiplayerServerListWidget.ServerEntry serverEntry = (MultiplayerServerListWidget.ServerEntry)entry;
        if (serverEntry.server != this.server) return false;
        return true;
    }
}
