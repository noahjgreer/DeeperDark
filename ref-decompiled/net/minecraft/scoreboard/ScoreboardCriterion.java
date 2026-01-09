package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class ScoreboardCriterion {
   private static final Map SIMPLE_CRITERIA = Maps.newHashMap();
   private static final Map CRITERIA = Maps.newHashMap();
   public static final Codec CODEC;
   public static final ScoreboardCriterion DUMMY;
   public static final ScoreboardCriterion TRIGGER;
   public static final ScoreboardCriterion DEATH_COUNT;
   public static final ScoreboardCriterion PLAYER_KILL_COUNT;
   public static final ScoreboardCriterion TOTAL_KILL_COUNT;
   public static final ScoreboardCriterion HEALTH;
   public static final ScoreboardCriterion FOOD;
   public static final ScoreboardCriterion AIR;
   public static final ScoreboardCriterion ARMOR;
   public static final ScoreboardCriterion XP;
   public static final ScoreboardCriterion LEVEL;
   public static final ScoreboardCriterion[] TEAM_KILLS;
   public static final ScoreboardCriterion[] KILLED_BY_TEAMS;
   private final String name;
   private final boolean readOnly;
   private final RenderType defaultRenderType;

   public static ScoreboardCriterion create(String name, boolean readOnly, RenderType defaultRenderType) {
      ScoreboardCriterion scoreboardCriterion = new ScoreboardCriterion(name, readOnly, defaultRenderType);
      SIMPLE_CRITERIA.put(name, scoreboardCriterion);
      return scoreboardCriterion;
   }

   public static ScoreboardCriterion create(String name) {
      return create(name, false, ScoreboardCriterion.RenderType.INTEGER);
   }

   protected ScoreboardCriterion(String name) {
      this(name, false, ScoreboardCriterion.RenderType.INTEGER);
   }

   protected ScoreboardCriterion(String name, boolean readOnly, RenderType defaultRenderType) {
      this.name = name;
      this.readOnly = readOnly;
      this.defaultRenderType = defaultRenderType;
      CRITERIA.put(name, this);
   }

   public static Set getAllSimpleCriteria() {
      return ImmutableSet.copyOf(SIMPLE_CRITERIA.keySet());
   }

   public static Optional getOrCreateStatCriterion(String name) {
      ScoreboardCriterion scoreboardCriterion = (ScoreboardCriterion)CRITERIA.get(name);
      if (scoreboardCriterion != null) {
         return Optional.of(scoreboardCriterion);
      } else {
         int i = name.indexOf(58);
         return i < 0 ? Optional.empty() : Registries.STAT_TYPE.getOptionalValue(Identifier.splitOn(name.substring(0, i), '.')).flatMap((type) -> {
            return getOrCreateStatCriterion(type, Identifier.splitOn(name.substring(i + 1), '.'));
         });
      }
   }

   private static Optional getOrCreateStatCriterion(StatType statType, Identifier id) {
      Optional var10000 = statType.getRegistry().getOptionalValue(id);
      Objects.requireNonNull(statType);
      return var10000.map(statType::getOrCreateStat);
   }

   public String getName() {
      return this.name;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public RenderType getDefaultRenderType() {
      return this.defaultRenderType;
   }

   static {
      CODEC = Codec.STRING.comapFlatMap((name) -> {
         return (DataResult)getOrCreateStatCriterion(name).map(DataResult::success).orElse(DataResult.error(() -> {
            return "No scoreboard criteria with name: " + name;
         }));
      }, ScoreboardCriterion::getName);
      DUMMY = create("dummy");
      TRIGGER = create("trigger");
      DEATH_COUNT = create("deathCount");
      PLAYER_KILL_COUNT = create("playerKillCount");
      TOTAL_KILL_COUNT = create("totalKillCount");
      HEALTH = create("health", true, ScoreboardCriterion.RenderType.HEARTS);
      FOOD = create("food", true, ScoreboardCriterion.RenderType.INTEGER);
      AIR = create("air", true, ScoreboardCriterion.RenderType.INTEGER);
      ARMOR = create("armor", true, ScoreboardCriterion.RenderType.INTEGER);
      XP = create("xp", true, ScoreboardCriterion.RenderType.INTEGER);
      LEVEL = create("level", true, ScoreboardCriterion.RenderType.INTEGER);
      TEAM_KILLS = new ScoreboardCriterion[]{create("teamkill." + Formatting.BLACK.getName()), create("teamkill." + Formatting.DARK_BLUE.getName()), create("teamkill." + Formatting.DARK_GREEN.getName()), create("teamkill." + Formatting.DARK_AQUA.getName()), create("teamkill." + Formatting.DARK_RED.getName()), create("teamkill." + Formatting.DARK_PURPLE.getName()), create("teamkill." + Formatting.GOLD.getName()), create("teamkill." + Formatting.GRAY.getName()), create("teamkill." + Formatting.DARK_GRAY.getName()), create("teamkill." + Formatting.BLUE.getName()), create("teamkill." + Formatting.GREEN.getName()), create("teamkill." + Formatting.AQUA.getName()), create("teamkill." + Formatting.RED.getName()), create("teamkill." + Formatting.LIGHT_PURPLE.getName()), create("teamkill." + Formatting.YELLOW.getName()), create("teamkill." + Formatting.WHITE.getName())};
      KILLED_BY_TEAMS = new ScoreboardCriterion[]{create("killedByTeam." + Formatting.BLACK.getName()), create("killedByTeam." + Formatting.DARK_BLUE.getName()), create("killedByTeam." + Formatting.DARK_GREEN.getName()), create("killedByTeam." + Formatting.DARK_AQUA.getName()), create("killedByTeam." + Formatting.DARK_RED.getName()), create("killedByTeam." + Formatting.DARK_PURPLE.getName()), create("killedByTeam." + Formatting.GOLD.getName()), create("killedByTeam." + Formatting.GRAY.getName()), create("killedByTeam." + Formatting.DARK_GRAY.getName()), create("killedByTeam." + Formatting.BLUE.getName()), create("killedByTeam." + Formatting.GREEN.getName()), create("killedByTeam." + Formatting.AQUA.getName()), create("killedByTeam." + Formatting.RED.getName()), create("killedByTeam." + Formatting.LIGHT_PURPLE.getName()), create("killedByTeam." + Formatting.YELLOW.getName()), create("killedByTeam." + Formatting.WHITE.getName())};
   }

   public static enum RenderType implements StringIdentifiable {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String name;
      public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(RenderType::values);

      private RenderType(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String asString() {
         return this.name;
      }

      public static RenderType getType(String name) {
         return (RenderType)CODEC.byId(name, (Enum)INTEGER);
      }

      // $FF: synthetic method
      private static RenderType[] method_36799() {
         return new RenderType[]{INTEGER, HEARTS};
      }
   }
}
