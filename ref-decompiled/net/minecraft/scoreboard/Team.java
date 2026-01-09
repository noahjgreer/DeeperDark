package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class Team extends AbstractTeam {
   private static final int field_31884 = 0;
   private static final int field_31885 = 1;
   private final Scoreboard scoreboard;
   private final String name;
   private final Set playerList = Sets.newHashSet();
   private Text displayName;
   private Text prefix;
   private Text suffix;
   private boolean friendlyFire;
   private boolean showFriendlyInvisibles;
   private AbstractTeam.VisibilityRule nameTagVisibilityRule;
   private AbstractTeam.VisibilityRule deathMessageVisibilityRule;
   private Formatting color;
   private AbstractTeam.CollisionRule collisionRule;
   private final Style nameStyle;

   public Team(Scoreboard scoreboard, String name) {
      this.prefix = ScreenTexts.EMPTY;
      this.suffix = ScreenTexts.EMPTY;
      this.friendlyFire = true;
      this.showFriendlyInvisibles = true;
      this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
      this.deathMessageVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
      this.color = Formatting.RESET;
      this.collisionRule = AbstractTeam.CollisionRule.ALWAYS;
      this.scoreboard = scoreboard;
      this.name = name;
      this.displayName = Text.literal(name);
      this.nameStyle = Style.EMPTY.withInsertion(name).withHoverEvent(new HoverEvent.ShowText(Text.literal(name)));
   }

   public Packed pack() {
      return new Packed(this.name, Optional.of(this.displayName), this.color != Formatting.RESET ? Optional.of(this.color) : Optional.empty(), this.friendlyFire, this.showFriendlyInvisibles, this.prefix, this.suffix, this.nameTagVisibilityRule, this.deathMessageVisibilityRule, this.collisionRule, List.copyOf(this.playerList));
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public String getName() {
      return this.name;
   }

   public Text getDisplayName() {
      return this.displayName;
   }

   public MutableText getFormattedName() {
      MutableText mutableText = Texts.bracketed(this.displayName.copy().fillStyle(this.nameStyle));
      Formatting formatting = this.getColor();
      if (formatting != Formatting.RESET) {
         mutableText.formatted(formatting);
      }

      return mutableText;
   }

   public void setDisplayName(Text displayName) {
      if (displayName == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = displayName;
         this.scoreboard.updateScoreboardTeam(this);
      }
   }

   public void setPrefix(@Nullable Text prefix) {
      this.prefix = prefix == null ? ScreenTexts.EMPTY : prefix;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public Text getPrefix() {
      return this.prefix;
   }

   public void setSuffix(@Nullable Text suffix) {
      this.suffix = suffix == null ? ScreenTexts.EMPTY : suffix;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public Text getSuffix() {
      return this.suffix;
   }

   public Collection getPlayerList() {
      return this.playerList;
   }

   public MutableText decorateName(Text name) {
      MutableText mutableText = Text.empty().append(this.prefix).append(name).append(this.suffix);
      Formatting formatting = this.getColor();
      if (formatting != Formatting.RESET) {
         mutableText.formatted(formatting);
      }

      return mutableText;
   }

   public static MutableText decorateName(@Nullable AbstractTeam team, Text name) {
      return team == null ? name.copy() : team.decorateName(name);
   }

   public boolean isFriendlyFireAllowed() {
      return this.friendlyFire;
   }

   public void setFriendlyFireAllowed(boolean friendlyFire) {
      this.friendlyFire = friendlyFire;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public boolean shouldShowFriendlyInvisibles() {
      return this.showFriendlyInvisibles;
   }

   public void setShowFriendlyInvisibles(boolean showFriendlyInvisible) {
      this.showFriendlyInvisibles = showFriendlyInvisible;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public AbstractTeam.VisibilityRule getNameTagVisibilityRule() {
      return this.nameTagVisibilityRule;
   }

   public AbstractTeam.VisibilityRule getDeathMessageVisibilityRule() {
      return this.deathMessageVisibilityRule;
   }

   public void setNameTagVisibilityRule(AbstractTeam.VisibilityRule nameTagVisibilityRule) {
      this.nameTagVisibilityRule = nameTagVisibilityRule;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public void setDeathMessageVisibilityRule(AbstractTeam.VisibilityRule deathMessageVisibilityRule) {
      this.deathMessageVisibilityRule = deathMessageVisibilityRule;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public AbstractTeam.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(AbstractTeam.CollisionRule collisionRule) {
      this.collisionRule = collisionRule;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public int getFriendlyFlagsBitwise() {
      int i = 0;
      if (this.isFriendlyFireAllowed()) {
         i |= 1;
      }

      if (this.shouldShowFriendlyInvisibles()) {
         i |= 2;
      }

      return i;
   }

   public void setFriendlyFlagsBitwise(int flags) {
      this.setFriendlyFireAllowed((flags & 1) > 0);
      this.setShowFriendlyInvisibles((flags & 2) > 0);
   }

   public void setColor(Formatting color) {
      this.color = color;
      this.scoreboard.updateScoreboardTeam(this);
   }

   public Formatting getColor() {
      return this.color;
   }

   public static record Packed(String name, Optional displayName, Optional color, boolean allowFriendlyFire, boolean seeFriendlyInvisibles, Text memberNamePrefix, Text memberNameSuffix, AbstractTeam.VisibilityRule nameTagVisibility, AbstractTeam.VisibilityRule deathMessageVisibility, AbstractTeam.CollisionRule collisionRule, List players) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("Name").forGetter(Packed::name), TextCodecs.CODEC.optionalFieldOf("DisplayName").forGetter(Packed::displayName), Formatting.COLOR_CODEC.optionalFieldOf("TeamColor").forGetter(Packed::color), Codec.BOOL.optionalFieldOf("AllowFriendlyFire", true).forGetter(Packed::allowFriendlyFire), Codec.BOOL.optionalFieldOf("SeeFriendlyInvisibles", true).forGetter(Packed::seeFriendlyInvisibles), TextCodecs.CODEC.optionalFieldOf("MemberNamePrefix", ScreenTexts.EMPTY).forGetter(Packed::memberNamePrefix), TextCodecs.CODEC.optionalFieldOf("MemberNameSuffix", ScreenTexts.EMPTY).forGetter(Packed::memberNameSuffix), AbstractTeam.VisibilityRule.CODEC.optionalFieldOf("NameTagVisibility", AbstractTeam.VisibilityRule.ALWAYS).forGetter(Packed::nameTagVisibility), AbstractTeam.VisibilityRule.CODEC.optionalFieldOf("DeathMessageVisibility", AbstractTeam.VisibilityRule.ALWAYS).forGetter(Packed::deathMessageVisibility), AbstractTeam.CollisionRule.CODEC.optionalFieldOf("CollisionRule", AbstractTeam.CollisionRule.ALWAYS).forGetter(Packed::collisionRule), Codec.STRING.listOf().optionalFieldOf("Players", List.of()).forGetter(Packed::players)).apply(instance, Packed::new);
      });

      public Packed(String string, Optional optional, Optional optional2, boolean bl, boolean bl2, Text text, Text text2, AbstractTeam.VisibilityRule visibilityRule, AbstractTeam.VisibilityRule visibilityRule2, AbstractTeam.CollisionRule collisionRule, List list) {
         this.name = string;
         this.displayName = optional;
         this.color = optional2;
         this.allowFriendlyFire = bl;
         this.seeFriendlyInvisibles = bl2;
         this.memberNamePrefix = text;
         this.memberNameSuffix = text2;
         this.nameTagVisibility = visibilityRule;
         this.deathMessageVisibility = visibilityRule2;
         this.collisionRule = collisionRule;
         this.players = list;
      }

      public String name() {
         return this.name;
      }

      public Optional displayName() {
         return this.displayName;
      }

      public Optional color() {
         return this.color;
      }

      public boolean allowFriendlyFire() {
         return this.allowFriendlyFire;
      }

      public boolean seeFriendlyInvisibles() {
         return this.seeFriendlyInvisibles;
      }

      public Text memberNamePrefix() {
         return this.memberNamePrefix;
      }

      public Text memberNameSuffix() {
         return this.memberNameSuffix;
      }

      public AbstractTeam.VisibilityRule nameTagVisibility() {
         return this.nameTagVisibility;
      }

      public AbstractTeam.VisibilityRule deathMessageVisibility() {
         return this.deathMessageVisibility;
      }

      public AbstractTeam.CollisionRule collisionRule() {
         return this.collisionRule;
      }

      public List players() {
         return this.players;
      }
   }
}
