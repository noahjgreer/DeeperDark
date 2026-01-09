package net.minecraft.client.gui.hud;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
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
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
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
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PlayerListHud {
   private static final Identifier PING_UNKNOWN_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_unknown");
   private static final Identifier PING_1_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_1");
   private static final Identifier PING_2_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_2");
   private static final Identifier PING_3_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_3");
   private static final Identifier PING_4_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_4");
   private static final Identifier PING_5_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_5");
   private static final Identifier CONTAINER_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/container_blinking");
   private static final Identifier CONTAINER_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/container");
   private static final Identifier FULL_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/full_blinking");
   private static final Identifier HALF_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/half_blinking");
   private static final Identifier ABSORBING_FULL_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/absorbing_full_blinking");
   private static final Identifier FULL_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/full");
   private static final Identifier ABSORBING_HALF_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/absorbing_half_blinking");
   private static final Identifier HALF_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/half");
   private static final Comparator ENTRY_ORDERING = Comparator.comparingInt((entry) -> {
      return -entry.getListOrder();
   }).thenComparingInt((entry) -> {
      return entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0;
   }).thenComparing((entry) -> {
      return (String)Nullables.mapOrElse(entry.getScoreboardTeam(), Team::getName, "");
   }).thenComparing((entry) -> {
      return entry.getProfile().getName();
   }, String::compareToIgnoreCase);
   public static final int MAX_ROWS = 20;
   private final MinecraftClient client;
   private final InGameHud inGameHud;
   @Nullable
   private Text footer;
   @Nullable
   private Text header;
   private boolean visible;
   private final Map hearts = new Object2ObjectOpenHashMap();

   public PlayerListHud(MinecraftClient client, InGameHud inGameHud) {
      this.client = client;
      this.inGameHud = inGameHud;
   }

   public Text getPlayerName(PlayerListEntry entry) {
      return entry.getDisplayName() != null ? this.applyGameModeFormatting(entry, entry.getDisplayName().copy()) : this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), Text.literal(entry.getProfile().getName())));
   }

   private Text applyGameModeFormatting(PlayerListEntry entry, MutableText name) {
      return entry.getGameMode() == GameMode.SPECTATOR ? name.formatted(Formatting.ITALIC) : name;
   }

   public void setVisible(boolean visible) {
      if (this.visible != visible) {
         this.hearts.clear();
         this.visible = visible;
         if (visible) {
            Text text = Texts.join(this.collectPlayerEntries(), (Text)Text.literal(", "), this::getPlayerName);
            this.client.getNarratorManager().narrateSystemImmediately((Text)Text.translatable("multiplayer.player.list.narration", text));
         }
      }

   }

   private List collectPlayerEntries() {
      return this.client.player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(80L).toList();
   }

   public void render(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
      List list = this.collectPlayerEntries();
      List list2 = new ArrayList(list.size());
      int i = this.client.textRenderer.getWidth(" ");
      int j = 0;
      int k = 0;

      Text text;
      int l;
      MutableText text2;
      int m;
      for(Iterator var10 = list.iterator(); var10.hasNext(); list2.add(new ScoreDisplayEntry(text, l, text2, m))) {
         PlayerListEntry playerListEntry = (PlayerListEntry)var10.next();
         text = this.getPlayerName(playerListEntry);
         j = Math.max(j, this.client.textRenderer.getWidth((StringVisitable)text));
         l = 0;
         text2 = null;
         m = 0;
         if (objective != null) {
            ScoreHolder scoreHolder = ScoreHolder.fromProfile(playerListEntry.getProfile());
            ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(scoreHolder, objective);
            if (readableScoreboardScore != null) {
               l = readableScoreboardScore.getScore();
            }

            if (objective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
               NumberFormat numberFormat = objective.getNumberFormatOr(StyledNumberFormat.YELLOW);
               text2 = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, numberFormat);
               m = this.client.textRenderer.getWidth((StringVisitable)text2);
               k = Math.max(k, m > 0 ? i + m : 0);
            }
         }
      }

      if (!this.hearts.isEmpty()) {
         Set set = (Set)list.stream().map((playerEntry) -> {
            return playerEntry.getProfile().getId();
         }).collect(Collectors.toSet());
         this.hearts.keySet().removeIf((uuid) -> {
            return !set.contains(uuid);
         });
      }

      int n = list.size();
      int o = n;

      int p;
      for(p = 1; o > 20; o = (n + p - 1) / p) {
         ++p;
      }

      boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
      int q;
      if (objective != null) {
         if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            q = 90;
         } else {
            q = k;
         }
      } else {
         q = 0;
      }

      m = Math.min(p * ((bl ? 9 : 0) + j + q + 13), scaledWindowWidth - 50) / p;
      int r = scaledWindowWidth / 2 - (m * p + (p - 1) * 5) / 2;
      int s = 10;
      int t = m * p + (p - 1) * 5;
      List list3 = null;
      if (this.header != null) {
         list3 = this.client.textRenderer.wrapLines(this.header, scaledWindowWidth - 50);

         OrderedText orderedText;
         for(Iterator var20 = list3.iterator(); var20.hasNext(); t = Math.max(t, this.client.textRenderer.getWidth(orderedText))) {
            orderedText = (OrderedText)var20.next();
         }
      }

      List list4 = null;
      OrderedText orderedText2;
      Iterator var42;
      if (this.footer != null) {
         list4 = this.client.textRenderer.wrapLines(this.footer, scaledWindowWidth - 50);

         for(var42 = list4.iterator(); var42.hasNext(); t = Math.max(t, this.client.textRenderer.getWidth(orderedText2))) {
            orderedText2 = (OrderedText)var42.next();
         }
      }

      int var10001;
      int var10002;
      int var10003;
      int var10005;
      int u;
      if (list3 != null) {
         var10001 = scaledWindowWidth / 2 - t / 2 - 1;
         var10002 = s - 1;
         var10003 = scaledWindowWidth / 2 + t / 2 + 1;
         var10005 = list3.size();
         Objects.requireNonNull(this.client.textRenderer);
         context.fill(var10001, var10002, var10003, s + var10005 * 9, Integer.MIN_VALUE);

         for(var42 = list3.iterator(); var42.hasNext(); s += 9) {
            orderedText2 = (OrderedText)var42.next();
            u = this.client.textRenderer.getWidth(orderedText2);
            context.drawTextWithShadow(this.client.textRenderer, (OrderedText)orderedText2, scaledWindowWidth / 2 - u / 2, s, -1);
            Objects.requireNonNull(this.client.textRenderer);
         }

         ++s;
      }

      context.fill(scaledWindowWidth / 2 - t / 2 - 1, s - 1, scaledWindowWidth / 2 + t / 2 + 1, s + o * 9, Integer.MIN_VALUE);
      int v = this.client.options.getTextBackgroundColor(553648127);

      int x;
      for(int w = 0; w < n; ++w) {
         u = w / o;
         x = w % o;
         int y = r + u * m + u * 5;
         int z = s + x * 9;
         context.fill(y, z, y + m, z + 8, v);
         if (w < list.size()) {
            PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(w);
            ScoreDisplayEntry scoreDisplayEntry = (ScoreDisplayEntry)list2.get(w);
            GameProfile gameProfile = playerListEntry2.getProfile();
            if (bl) {
               PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
               boolean bl2 = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
               PlayerSkinDrawer.draw(context, playerListEntry2.getSkinTextures().texture(), y, z, 8, playerListEntry2.shouldShowHat(), bl2, -1);
               y += 9;
            }

            context.drawTextWithShadow(this.client.textRenderer, scoreDisplayEntry.name, y, z, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
            if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
               int aa = y + j + 1;
               int ab = aa + q;
               if (ab - aa > 5) {
                  this.renderScoreboardObjective(objective, z, scoreDisplayEntry, aa, ab, gameProfile.getId(), context);
               }
            }

            this.renderLatencyIcon(context, m, y - (bl ? 9 : 0), z, playerListEntry2);
         }
      }

      if (list4 != null) {
         s += o * 9 + 1;
         var10001 = scaledWindowWidth / 2 - t / 2 - 1;
         var10002 = s - 1;
         var10003 = scaledWindowWidth / 2 + t / 2 + 1;
         var10005 = list4.size();
         Objects.requireNonNull(this.client.textRenderer);
         context.fill(var10001, var10002, var10003, s + var10005 * 9, Integer.MIN_VALUE);

         for(Iterator var45 = list4.iterator(); var45.hasNext(); s += 9) {
            OrderedText orderedText3 = (OrderedText)var45.next();
            x = this.client.textRenderer.getWidth(orderedText3);
            context.drawTextWithShadow(this.client.textRenderer, (OrderedText)orderedText3, scaledWindowWidth / 2 - x / 2, s, -1);
            Objects.requireNonNull(this.client.textRenderer);
         }
      }

   }

   protected void renderLatencyIcon(DrawContext context, int width, int x, int y, PlayerListEntry entry) {
      Identifier identifier;
      if (entry.getLatency() < 0) {
         identifier = PING_UNKNOWN_ICON_TEXTURE;
      } else if (entry.getLatency() < 150) {
         identifier = PING_5_ICON_TEXTURE;
      } else if (entry.getLatency() < 300) {
         identifier = PING_4_ICON_TEXTURE;
      } else if (entry.getLatency() < 600) {
         identifier = PING_3_ICON_TEXTURE;
      } else if (entry.getLatency() < 1000) {
         identifier = PING_2_ICON_TEXTURE;
      } else {
         identifier = PING_1_ICON_TEXTURE;
      }

      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x + width - 11, y, 10, 8);
   }

   private void renderScoreboardObjective(ScoreboardObjective objective, int y, ScoreDisplayEntry scoreDisplayEntry, int left, int right, UUID uuid, DrawContext context) {
      if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
         this.renderHearts(y, left, right, uuid, context, scoreDisplayEntry.score);
      } else if (scoreDisplayEntry.formattedScore != null) {
         context.drawTextWithShadow(this.client.textRenderer, (Text)scoreDisplayEntry.formattedScore, right - scoreDisplayEntry.scoreWidth, y, -1);
      }

   }

   private void renderHearts(int y, int left, int right, UUID uuid, DrawContext context, int score) {
      Heart heart = (Heart)this.hearts.computeIfAbsent(uuid, (uuid2) -> {
         return new Heart(score);
      });
      heart.tick(score, (long)this.inGameHud.getTicks());
      int i = MathHelper.ceilDiv(Math.max(score, heart.getLastScore()), 2);
      int j = Math.max(score, Math.max(heart.getLastScore(), 20)) / 2;
      boolean bl = heart.useHighlighted((long)this.inGameHud.getTicks());
      if (i > 0) {
         int k = MathHelper.floor(Math.min((float)(right - left - 4) / (float)j, 9.0F));
         int l;
         if (k <= 3) {
            float f = MathHelper.clamp((float)score / 20.0F, 0.0F, 1.0F);
            l = (int)((1.0F - f) * 255.0F) << 16 | (int)(f * 255.0F) << 8;
            float g = (float)score / 2.0F;
            Text text = Text.translatable("multiplayer.player.list.hp", g);
            MutableText text2;
            if (right - this.client.textRenderer.getWidth((StringVisitable)text) >= left) {
               text2 = text;
            } else {
               text2 = Text.literal(Float.toString(g));
            }

            context.drawTextWithShadow(this.client.textRenderer, (Text)text2, (right + left - this.client.textRenderer.getWidth((StringVisitable)text2)) / 2, y, ColorHelper.fullAlpha(l));
         } else {
            Identifier identifier = bl ? CONTAINER_HEART_BLINKING_TEXTURE : CONTAINER_HEART_TEXTURE;

            for(l = i; l < j; ++l) {
               context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, left + l * k, y, 9, 9);
            }

            for(l = 0; l < i; ++l) {
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

               if (l * 2 + 1 == score) {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, l >= 10 ? ABSORBING_HALF_HEART_BLINKING_TEXTURE : HALF_HEART_TEXTURE, left + l * k, y, 9, 9);
               }
            }

         }
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

   @Environment(EnvType.CLIENT)
   static record ScoreDisplayEntry(Text name, int score, @Nullable Text formattedScore, int scoreWidth) {
      final Text name;
      final int score;
      @Nullable
      final Text formattedScore;
      final int scoreWidth;

      ScoreDisplayEntry(Text text, int i, @Nullable Text text2, int j) {
         this.name = text;
         this.score = i;
         this.formattedScore = text2;
         this.scoreWidth = j;
      }

      public Text name() {
         return this.name;
      }

      public int score() {
         return this.score;
      }

      @Nullable
      public Text formattedScore() {
         return this.formattedScore;
      }

      public int scoreWidth() {
         return this.scoreWidth;
      }
   }

   @Environment(EnvType.CLIENT)
   static class Heart {
      private static final long COOLDOWN_TICKS = 20L;
      private static final long SCORE_DECREASE_HIGHLIGHT_TICKS = 20L;
      private static final long SCORE_INCREASE_HIGHLIGHT_TICKS = 10L;
      private int score;
      private int lastScore;
      private long lastScoreChangeTick;
      private long highlightEndTick;

      public Heart(int score) {
         this.lastScore = score;
         this.score = score;
      }

      public void tick(int score, long currentTick) {
         if (score != this.score) {
            long l = score < this.score ? 20L : 10L;
            this.highlightEndTick = currentTick + l;
            this.score = score;
            this.lastScoreChangeTick = currentTick;
         }

         if (currentTick - this.lastScoreChangeTick > 20L) {
            this.lastScore = score;
         }

      }

      public int getLastScore() {
         return this.lastScore;
      }

      public boolean useHighlighted(long currentTick) {
         return this.highlightEndTick > currentTick && (this.highlightEndTick - currentTick) % 6L >= 3L;
      }
   }
}
