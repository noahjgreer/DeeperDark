/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.PlayerSkinDrawer
 *  net.minecraft.client.gui.hud.InGameHud
 *  net.minecraft.client.gui.hud.PlayerListHud
 *  net.minecraft.client.gui.hud.PlayerListHud$Heart
 *  net.minecraft.client.gui.hud.PlayerListHud$ScoreDisplayEntry
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.render.entity.PlayerEntityRenderer
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.scoreboard.AbstractTeam
 *  net.minecraft.scoreboard.ReadableScoreboardScore
 *  net.minecraft.scoreboard.ScoreHolder
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.ScoreboardCriterion$RenderType
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.scoreboard.number.NumberFormat
 *  net.minecraft.scoreboard.number.StyledNumberFormat
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Nullables
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.GameMode
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerListHud {
    private static final Identifier PING_UNKNOWN_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/ping_unknown");
    private static final Identifier PING_1_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/ping_1");
    private static final Identifier PING_2_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/ping_2");
    private static final Identifier PING_3_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/ping_3");
    private static final Identifier PING_4_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/ping_4");
    private static final Identifier PING_5_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/ping_5");
    private static final Identifier CONTAINER_HEART_BLINKING_TEXTURE = Identifier.ofVanilla((String)"hud/heart/container_blinking");
    private static final Identifier CONTAINER_HEART_TEXTURE = Identifier.ofVanilla((String)"hud/heart/container");
    private static final Identifier FULL_HEART_BLINKING_TEXTURE = Identifier.ofVanilla((String)"hud/heart/full_blinking");
    private static final Identifier HALF_HEART_BLINKING_TEXTURE = Identifier.ofVanilla((String)"hud/heart/half_blinking");
    private static final Identifier ABSORBING_FULL_HEART_BLINKING_TEXTURE = Identifier.ofVanilla((String)"hud/heart/absorbing_full_blinking");
    private static final Identifier FULL_HEART_TEXTURE = Identifier.ofVanilla((String)"hud/heart/full");
    private static final Identifier ABSORBING_HALF_HEART_BLINKING_TEXTURE = Identifier.ofVanilla((String)"hud/heart/absorbing_half_blinking");
    private static final Identifier HALF_HEART_TEXTURE = Identifier.ofVanilla((String)"hud/heart/half");
    private static final Comparator<PlayerListEntry> ENTRY_ORDERING = Comparator.comparingInt(entry -> -entry.getListOrder()).thenComparingInt(entry -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0).thenComparing(entry -> (String)Nullables.mapOrElse((Object)entry.getScoreboardTeam(), Team::getName, (Object)"")).thenComparing(entry -> entry.getProfile().name(), String::compareToIgnoreCase);
    public static final int MAX_ROWS = 20;
    private final MinecraftClient client;
    private final InGameHud inGameHud;
    private @Nullable Text footer;
    private @Nullable Text header;
    private boolean visible;
    private final Map<UUID, Heart> hearts = new Object2ObjectOpenHashMap();

    public PlayerListHud(MinecraftClient client, InGameHud inGameHud) {
        this.client = client;
        this.inGameHud = inGameHud;
    }

    public Text getPlayerName(PlayerListEntry entry) {
        if (entry.getDisplayName() != null) {
            return this.applyGameModeFormatting(entry, entry.getDisplayName().copy());
        }
        return this.applyGameModeFormatting(entry, Team.decorateName((AbstractTeam)entry.getScoreboardTeam(), (Text)Text.literal((String)entry.getProfile().name())));
    }

    private Text applyGameModeFormatting(PlayerListEntry entry, MutableText name) {
        return entry.getGameMode() == GameMode.SPECTATOR ? name.formatted(Formatting.ITALIC) : name;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.hearts.clear();
            this.visible = visible;
            if (visible) {
                MutableText text = Texts.join((Collection)this.collectPlayerEntries(), (Text)Text.literal((String)", "), arg_0 -> this.getPlayerName(arg_0));
                this.client.getNarratorManager().narrateSystemImmediately((Text)Text.translatable((String)"multiplayer.player.list.narration", (Object[])new Object[]{text}));
            }
        }
    }

    private List<PlayerListEntry> collectPlayerEntries() {
        return this.client.player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(80L).toList();
    }

    public void render(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
        int x;
        int u;
        boolean bl;
        int n;
        int m;
        List list = this.collectPlayerEntries();
        ArrayList<ScoreDisplayEntry> list2 = new ArrayList<ScoreDisplayEntry>(list.size());
        int i = this.client.textRenderer.getWidth(" ");
        int j = 0;
        int k = 0;
        for (PlayerListEntry playerListEntry : list) {
            Text text = this.getPlayerName(playerListEntry);
            j = Math.max(j, this.client.textRenderer.getWidth((StringVisitable)text));
            int l = 0;
            MutableText text2 = null;
            m = 0;
            if (objective != null) {
                ScoreHolder scoreHolder = ScoreHolder.fromProfile((GameProfile)playerListEntry.getProfile());
                ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(scoreHolder, objective);
                if (readableScoreboardScore != null) {
                    l = readableScoreboardScore.getScore();
                }
                if (objective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
                    NumberFormat numberFormat = objective.getNumberFormatOr((NumberFormat)StyledNumberFormat.YELLOW);
                    text2 = ReadableScoreboardScore.getFormattedScore((ReadableScoreboardScore)readableScoreboardScore, (NumberFormat)numberFormat);
                    m = this.client.textRenderer.getWidth((StringVisitable)text2);
                    k = Math.max(k, m > 0 ? i + m : 0);
                }
            }
            list2.add(new ScoreDisplayEntry(text, l, text2, m));
        }
        if (!this.hearts.isEmpty()) {
            Set set = list.stream().map(playerEntry -> playerEntry.getProfile().id()).collect(Collectors.toSet());
            this.hearts.keySet().removeIf(uuid -> !set.contains(uuid));
        }
        int o = n = list.size();
        int p = 1;
        while (o > 20) {
            o = (n + ++p - 1) / p;
        }
        boolean bl2 = bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int q = objective != null ? (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS ? 90 : k) : 0;
        m = Math.min(p * ((bl ? 9 : 0) + j + q + 13), scaledWindowWidth - 50) / p;
        int r = scaledWindowWidth / 2 - (m * p + (p - 1) * 5) / 2;
        int s = 10;
        int t = m * p + (p - 1) * 5;
        List list3 = null;
        if (this.header != null) {
            list3 = this.client.textRenderer.wrapLines((StringVisitable)this.header, scaledWindowWidth - 50);
            for (Object orderedText : list3) {
                t = Math.max(t, this.client.textRenderer.getWidth((OrderedText)orderedText));
            }
        }
        List list4 = null;
        if (this.footer != null) {
            list4 = this.client.textRenderer.wrapLines((StringVisitable)this.footer, scaledWindowWidth - 50);
            for (OrderedText orderedText2 : list4) {
                t = Math.max(t, this.client.textRenderer.getWidth(orderedText2));
            }
        }
        if (list3 != null) {
            int n2 = scaledWindowWidth / 2 - t / 2 - 1;
            int n3 = scaledWindowWidth / 2 + t / 2 + 1;
            int n4 = list3.size();
            Objects.requireNonNull(this.client.textRenderer);
            context.fill(n2, s - 1, n3, s + n4 * 9, Integer.MIN_VALUE);
            for (OrderedText orderedText2 : list3) {
                u = this.client.textRenderer.getWidth(orderedText2);
                context.drawTextWithShadow(this.client.textRenderer, orderedText2, scaledWindowWidth / 2 - u / 2, s, -1);
                Objects.requireNonNull(this.client.textRenderer);
                s += 9;
            }
            ++s;
        }
        context.fill(scaledWindowWidth / 2 - t / 2 - 1, s - 1, scaledWindowWidth / 2 + t / 2 + 1, s + o * 9, Integer.MIN_VALUE);
        int v = this.client.options.getTextBackgroundColor(0x20FFFFFF);
        for (int w = 0; w < n; ++w) {
            int aa;
            int ab;
            u = w / o;
            x = w % o;
            int y = r + u * m + u * 5;
            int z = s + x * 9;
            context.fill(y, z, y + m, z + 8, v);
            if (w >= list.size()) continue;
            PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(w);
            ScoreDisplayEntry scoreDisplayEntry = (ScoreDisplayEntry)list2.get(w);
            GameProfile gameProfile = playerListEntry2.getProfile();
            if (bl) {
                PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.id());
                boolean bl22 = playerEntity != null && PlayerEntityRenderer.shouldFlipUpsideDown((PlayerEntity)playerEntity);
                PlayerSkinDrawer.draw((DrawContext)context, (Identifier)playerListEntry2.getSkinTextures().body().texturePath(), (int)y, (int)z, (int)8, (boolean)playerListEntry2.shouldShowHat(), (boolean)bl22, (int)-1);
                y += 9;
            }
            context.drawTextWithShadow(this.client.textRenderer, scoreDisplayEntry.name, y, z, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
            if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR && (ab = (aa = y + j + 1) + q) - aa > 5) {
                this.renderScoreboardObjective(objective, z, scoreDisplayEntry, aa, ab, gameProfile.id(), context);
            }
            this.renderLatencyIcon(context, m, y - (bl ? 9 : 0), z, playerListEntry2);
        }
        if (list4 != null) {
            s += o * 9 + 1;
            int n5 = scaledWindowWidth / 2 - t / 2 - 1;
            int n6 = scaledWindowWidth / 2 + t / 2 + 1;
            int n7 = list4.size();
            Objects.requireNonNull(this.client.textRenderer);
            context.fill(n5, s - 1, n6, s + n7 * 9, Integer.MIN_VALUE);
            for (OrderedText orderedText3 : list4) {
                x = this.client.textRenderer.getWidth(orderedText3);
                context.drawTextWithShadow(this.client.textRenderer, orderedText3, scaledWindowWidth / 2 - x / 2, s, -1);
                Objects.requireNonNull(this.client.textRenderer);
                s += 9;
            }
        }
    }

    protected void renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry) {
        Identifier identifier = entry.getLatency() < 0 ? PING_UNKNOWN_ICON_TEXTURE : (entry.getLatency() < 150 ? PING_5_ICON_TEXTURE : (entry.getLatency() < 300 ? PING_4_ICON_TEXTURE : (entry.getLatency() < 600 ? PING_3_ICON_TEXTURE : (entry.getLatency() < 1000 ? PING_2_ICON_TEXTURE : PING_1_ICON_TEXTURE))));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x + width - 11, y, 10, 8);
    }

    private void renderScoreboardObjective(ScoreboardObjective objective, int y, ScoreDisplayEntry scoreDisplayEntry, int left, int right, UUID uuid, DrawContext context) {
        if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            this.renderHearts(y, left, right, uuid, context, scoreDisplayEntry.score);
        } else if (scoreDisplayEntry.formattedScore != null) {
            context.drawTextWithShadow(this.client.textRenderer, scoreDisplayEntry.formattedScore, right - scoreDisplayEntry.scoreWidth, y, -1);
        }
    }

    private void renderHearts(int y, int left, int right, UUID uuid, DrawContext context, int score) {
        int l;
        Heart heart = this.hearts.computeIfAbsent(uuid, uuid2 -> new Heart(score));
        heart.tick(score, (long)this.inGameHud.getTicks());
        int i = MathHelper.ceilDiv((int)Math.max(score, heart.getLastScore()), (int)2);
        int j = Math.max(score, Math.max(heart.getLastScore(), 20)) / 2;
        boolean bl = heart.useHighlighted((long)this.inGameHud.getTicks());
        if (i <= 0) {
            return;
        }
        int k = MathHelper.floor((float)Math.min((float)(right - left - 4) / (float)j, 9.0f));
        if (k <= 3) {
            float f = MathHelper.clamp((float)((float)score / 20.0f), (float)0.0f, (float)1.0f);
            int l2 = (int)((1.0f - f) * 255.0f) << 16 | (int)(f * 255.0f) << 8;
            float g = (float)score / 2.0f;
            MutableText text = Text.translatable((String)"multiplayer.player.list.hp", (Object[])new Object[]{Float.valueOf(g)});
            MutableText text2 = right - this.client.textRenderer.getWidth((StringVisitable)text) >= left ? text : Text.literal((String)Float.toString(g));
            context.drawTextWithShadow(this.client.textRenderer, (Text)text2, (right + left - this.client.textRenderer.getWidth((StringVisitable)text2)) / 2, y, ColorHelper.fullAlpha((int)l2));
            return;
        }
        Identifier identifier = bl ? CONTAINER_HEART_BLINKING_TEXTURE : CONTAINER_HEART_TEXTURE;
        for (l = i; l < j; ++l) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, left + l * k, y, 9, 9);
        }
        for (l = 0; l < i; ++l) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, left + l * k, y, 9, 9);
            if (bl) {
                if (l * 2 + 1 < heart.getLastScore()) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FULL_HEART_BLINKING_TEXTURE, left + l * k, y, 9, 9);
                }
                if (l * 2 + 1 == heart.getLastScore()) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HALF_HEART_BLINKING_TEXTURE, left + l * k, y, 9, 9);
                }
            }
            if (l * 2 + 1 < score) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, l >= 10 ? ABSORBING_FULL_HEART_BLINKING_TEXTURE : FULL_HEART_TEXTURE, left + l * k, y, 9, 9);
            }
            if (l * 2 + 1 != score) continue;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, l >= 10 ? ABSORBING_HALF_HEART_BLINKING_TEXTURE : HALF_HEART_TEXTURE, left + l * k, y, 9, 9);
        }
    }

    public void setFooter(@Nullable Text footer) {
        this.footer = footer;
    }

    public void setHeader(@Nullable Text header) {
        this.header = header;
    }

    public void clear() {
        this.header = null;
        this.footer = null;
    }
}

