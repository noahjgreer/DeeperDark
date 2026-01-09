package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class Scoreboard {
   public static final String field_47542 = "#";
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Object2ObjectMap objectives = new Object2ObjectOpenHashMap(16, 0.5F);
   private final Reference2ObjectMap objectivesByCriterion = new Reference2ObjectOpenHashMap();
   private final Map scores = new Object2ObjectOpenHashMap(16, 0.5F);
   private final Map objectiveSlots = new EnumMap(ScoreboardDisplaySlot.class);
   private final Object2ObjectMap teams = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap teamsByScoreHolder = new Object2ObjectOpenHashMap();

   @Nullable
   public ScoreboardObjective getNullableObjective(@Nullable String name) {
      return (ScoreboardObjective)this.objectives.get(name);
   }

   public ScoreboardObjective addObjective(String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType, boolean displayAutoUpdate, @Nullable NumberFormat numberFormat) {
      if (this.objectives.containsKey(name)) {
         throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
      } else {
         ScoreboardObjective scoreboardObjective = new ScoreboardObjective(this, name, criterion, displayName, renderType, displayAutoUpdate, numberFormat);
         ((List)this.objectivesByCriterion.computeIfAbsent(criterion, (criterion2) -> {
            return Lists.newArrayList();
         })).add(scoreboardObjective);
         this.objectives.put(name, scoreboardObjective);
         this.updateObjective(scoreboardObjective);
         return scoreboardObjective;
      }
   }

   public final void forEachScore(ScoreboardCriterion criterion, ScoreHolder scoreHolder, Consumer action) {
      ((List)this.objectivesByCriterion.getOrDefault(criterion, Collections.emptyList())).forEach((objective) -> {
         action.accept(this.getOrCreateScore(scoreHolder, objective, true));
      });
   }

   private Scores getScores(String scoreHolderName) {
      return (Scores)this.scores.computeIfAbsent(scoreHolderName, (name) -> {
         return new Scores();
      });
   }

   public ScoreAccess getOrCreateScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
      return this.getOrCreateScore(scoreHolder, objective, false);
   }

   public ScoreAccess getOrCreateScore(final ScoreHolder scoreHolder, final ScoreboardObjective objective, boolean forceWritable) {
      final boolean bl = forceWritable || !objective.getCriterion().isReadOnly();
      Scores scores = this.getScores(scoreHolder.getNameForScoreboard());
      final MutableBoolean mutableBoolean = new MutableBoolean();
      final ScoreboardScore scoreboardScore = scores.getOrCreate(objective, (score) -> {
         mutableBoolean.setTrue();
      });
      return new ScoreAccess() {
         public int getScore() {
            return scoreboardScore.getScore();
         }

         public void setScore(int score) {
            if (!bl) {
               throw new IllegalStateException("Cannot modify read-only score");
            } else {
               boolean blx = mutableBoolean.isTrue();
               if (objective.shouldDisplayAutoUpdate()) {
                  Text text = scoreHolder.getDisplayName();
                  if (text != null && !text.equals(scoreboardScore.getDisplayText())) {
                     scoreboardScore.setDisplayText(text);
                     blx = true;
                  }
               }

               if (score != scoreboardScore.getScore()) {
                  scoreboardScore.setScore(score);
                  blx = true;
               }

               if (blx) {
                  this.update();
               }

            }
         }

         @Nullable
         public Text getDisplayText() {
            return scoreboardScore.getDisplayText();
         }

         public void setDisplayText(@Nullable Text text) {
            if (mutableBoolean.isTrue() || !Objects.equals(text, scoreboardScore.getDisplayText())) {
               scoreboardScore.setDisplayText(text);
               this.update();
            }

         }

         public void setNumberFormat(@Nullable NumberFormat numberFormat) {
            scoreboardScore.setNumberFormat(numberFormat);
            this.update();
         }

         public boolean isLocked() {
            return scoreboardScore.isLocked();
         }

         public void unlock() {
            this.setLocked(false);
         }

         public void lock() {
            this.setLocked(true);
         }

         private void setLocked(boolean locked) {
            scoreboardScore.setLocked(locked);
            if (mutableBoolean.isTrue()) {
               this.update();
            }

            Scoreboard.this.resetScore(scoreHolder, objective);
         }

         private void update() {
            Scoreboard.this.updateScore(scoreHolder, objective, scoreboardScore);
            mutableBoolean.setFalse();
         }
      };
   }

   @Nullable
   public ReadableScoreboardScore getScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
      Scores scores = (Scores)this.scores.get(scoreHolder.getNameForScoreboard());
      return scores != null ? scores.get(objective) : null;
   }

   public Collection getScoreboardEntries(ScoreboardObjective objective) {
      List list = new ArrayList();
      this.scores.forEach((scoreHolderName, scores) -> {
         ScoreboardScore scoreboardScore = scores.get(objective);
         if (scoreboardScore != null) {
            list.add(new ScoreboardEntry(scoreHolderName, scoreboardScore.getScore(), scoreboardScore.getDisplayText(), scoreboardScore.getNumberFormat()));
         }

      });
      return list;
   }

   public Collection getObjectives() {
      return this.objectives.values();
   }

   public Collection getObjectiveNames() {
      return this.objectives.keySet();
   }

   public Collection getKnownScoreHolders() {
      return this.scores.keySet().stream().map(ScoreHolder::fromName).toList();
   }

   public void removeScores(ScoreHolder scoreHolder) {
      Scores scores = (Scores)this.scores.remove(scoreHolder.getNameForScoreboard());
      if (scores != null) {
         this.onScoreHolderRemoved(scoreHolder);
      }

   }

   public void removeScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
      Scores scores = (Scores)this.scores.get(scoreHolder.getNameForScoreboard());
      if (scores != null) {
         boolean bl = scores.remove(objective);
         if (!scores.hasScores()) {
            Scores scores2 = (Scores)this.scores.remove(scoreHolder.getNameForScoreboard());
            if (scores2 != null) {
               this.onScoreHolderRemoved(scoreHolder);
            }
         } else if (bl) {
            this.onScoreRemoved(scoreHolder, objective);
         }
      }

   }

   public Object2IntMap getScoreHolderObjectives(ScoreHolder scoreHolder) {
      Scores scores = (Scores)this.scores.get(scoreHolder.getNameForScoreboard());
      return scores != null ? scores.getScoresAsIntMap() : Object2IntMaps.emptyMap();
   }

   public void removeObjective(ScoreboardObjective objective) {
      this.objectives.remove(objective.getName());
      ScoreboardDisplaySlot[] var2 = ScoreboardDisplaySlot.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ScoreboardDisplaySlot scoreboardDisplaySlot = var2[var4];
         if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
            this.setObjectiveSlot(scoreboardDisplaySlot, (ScoreboardObjective)null);
         }
      }

      List list = (List)this.objectivesByCriterion.get(objective.getCriterion());
      if (list != null) {
         list.remove(objective);
      }

      Iterator var7 = this.scores.values().iterator();

      while(var7.hasNext()) {
         Scores scores = (Scores)var7.next();
         scores.remove(objective);
      }

      this.updateRemovedObjective(objective);
   }

   public void setObjectiveSlot(ScoreboardDisplaySlot slot, @Nullable ScoreboardObjective objective) {
      this.objectiveSlots.put(slot, objective);
   }

   @Nullable
   public ScoreboardObjective getObjectiveForSlot(ScoreboardDisplaySlot slot) {
      return (ScoreboardObjective)this.objectiveSlots.get(slot);
   }

   @Nullable
   public Team getTeam(String name) {
      return (Team)this.teams.get(name);
   }

   public Team addTeam(String name) {
      Team team = this.getTeam(name);
      if (team != null) {
         LOGGER.warn("Requested creation of existing team '{}'", name);
         return team;
      } else {
         team = new Team(this, name);
         this.teams.put(name, team);
         this.updateScoreboardTeamAndPlayers(team);
         return team;
      }
   }

   public void removeTeam(Team team) {
      this.teams.remove(team.getName());
      Iterator var2 = team.getPlayerList().iterator();

      while(var2.hasNext()) {
         String string = (String)var2.next();
         this.teamsByScoreHolder.remove(string);
      }

      this.updateRemovedTeam(team);
   }

   public boolean addScoreHolderToTeam(String scoreHolderName, Team team) {
      if (this.getScoreHolderTeam(scoreHolderName) != null) {
         this.clearTeam(scoreHolderName);
      }

      this.teamsByScoreHolder.put(scoreHolderName, team);
      return team.getPlayerList().add(scoreHolderName);
   }

   public boolean clearTeam(String scoreHolderName) {
      Team team = this.getScoreHolderTeam(scoreHolderName);
      if (team != null) {
         this.removeScoreHolderFromTeam(scoreHolderName, team);
         return true;
      } else {
         return false;
      }
   }

   public void removeScoreHolderFromTeam(String scoreHolderName, Team team) {
      if (this.getScoreHolderTeam(scoreHolderName) != team) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
      } else {
         this.teamsByScoreHolder.remove(scoreHolderName);
         team.getPlayerList().remove(scoreHolderName);
      }
   }

   public Collection getTeamNames() {
      return this.teams.keySet();
   }

   public Collection getTeams() {
      return this.teams.values();
   }

   @Nullable
   public Team getScoreHolderTeam(String scoreHolderName) {
      return (Team)this.teamsByScoreHolder.get(scoreHolderName);
   }

   public void updateObjective(ScoreboardObjective objective) {
   }

   public void updateExistingObjective(ScoreboardObjective objective) {
   }

   public void updateRemovedObjective(ScoreboardObjective objective) {
   }

   protected void updateScore(ScoreHolder scoreHolder, ScoreboardObjective objective, ScoreboardScore score) {
   }

   protected void resetScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
   }

   public void onScoreHolderRemoved(ScoreHolder scoreHolder) {
   }

   public void onScoreRemoved(ScoreHolder scoreHolder, ScoreboardObjective objective) {
   }

   public void updateScoreboardTeamAndPlayers(Team team) {
   }

   public void updateScoreboardTeam(Team team) {
   }

   public void updateRemovedTeam(Team team) {
   }

   public void clearDeadEntity(Entity entity) {
      if (!(entity instanceof PlayerEntity) && !entity.isAlive()) {
         this.removeScores(entity);
         this.clearTeam(entity.getNameForScoreboard());
      }
   }

   protected List pack() {
      return this.scores.entrySet().stream().flatMap((entry) -> {
         String string = (String)entry.getKey();
         return ((Scores)entry.getValue()).getScores().entrySet().stream().map((entryx) -> {
            return new PackedEntry(string, ((ScoreboardObjective)entryx.getKey()).getName(), (ScoreboardScore)entryx.getValue());
         });
      }).toList();
   }

   protected void addEntry(PackedEntry packedEntry) {
      ScoreboardObjective scoreboardObjective = this.getNullableObjective(packedEntry.objective);
      if (scoreboardObjective == null) {
         LOGGER.error("Unknown objective {} for name {}, ignoring", packedEntry.objective, packedEntry.owner);
      } else {
         this.getScores(packedEntry.owner).put(scoreboardObjective, packedEntry.score);
      }
   }

   protected void addTeam(Team.Packed packedTeam) {
      Team team = this.addTeam(packedTeam.name());
      Optional var10000 = packedTeam.displayName();
      Objects.requireNonNull(team);
      var10000.ifPresent(team::setDisplayName);
      var10000 = packedTeam.color();
      Objects.requireNonNull(team);
      var10000.ifPresent(team::setColor);
      team.setFriendlyFireAllowed(packedTeam.allowFriendlyFire());
      team.setShowFriendlyInvisibles(packedTeam.seeFriendlyInvisibles());
      team.setPrefix(packedTeam.memberNamePrefix());
      team.setSuffix(packedTeam.memberNameSuffix());
      team.setNameTagVisibilityRule(packedTeam.nameTagVisibility());
      team.setDeathMessageVisibilityRule(packedTeam.deathMessageVisibility());
      team.setCollisionRule(packedTeam.collisionRule());
      Iterator var3 = packedTeam.players().iterator();

      while(var3.hasNext()) {
         String string = (String)var3.next();
         this.addScoreHolderToTeam(string, team);
      }

   }

   protected void addObjective(ScoreboardObjective.Packed packedObjective) {
      this.addObjective(packedObjective.name(), packedObjective.criteria(), packedObjective.displayName(), packedObjective.renderType(), packedObjective.displayAutoUpdate(), (NumberFormat)packedObjective.numberFormat().orElse((Object)null));
   }

   public static record PackedEntry(String owner, String objective, ScoreboardScore score) {
      final String owner;
      final String objective;
      final ScoreboardScore score;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.fieldOf("Name").forGetter(PackedEntry::owner), Codec.STRING.fieldOf("Objective").forGetter(PackedEntry::objective), ScoreboardScore.CODEC.forGetter(PackedEntry::score)).apply(instance, PackedEntry::new);
      });

      public PackedEntry(String string, String string2, ScoreboardScore scoreboardScore) {
         this.owner = string;
         this.objective = string2;
         this.score = scoreboardScore;
      }

      public String owner() {
         return this.owner;
      }

      public String objective() {
         return this.objective;
      }

      public ScoreboardScore score() {
         return this.score;
      }
   }
}
