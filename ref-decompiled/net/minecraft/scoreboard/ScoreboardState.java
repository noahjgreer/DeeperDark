package net.minecraft.scoreboard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.PersistentState;

public class ScoreboardState extends PersistentState {
   public static final String SCOREBOARD_KEY = "scoreboard";
   private final Scoreboard scoreboard;

   public ScoreboardState(Scoreboard scoreboard) {
      this.scoreboard = scoreboard;
   }

   public void unpack(Packed packed) {
      List var10000 = packed.objectives();
      Scoreboard var10001 = this.scoreboard;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::addObjective);
      var10000 = packed.scores();
      var10001 = this.scoreboard;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::addEntry);
      packed.displaySlots().forEach((slot, objectiveName) -> {
         ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(objectiveName);
         this.scoreboard.setObjectiveSlot(slot, scoreboardObjective);
      });
      var10000 = packed.teams();
      var10001 = this.scoreboard;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::addTeam);
   }

   public Packed pack() {
      Map map = new EnumMap(ScoreboardDisplaySlot.class);
      ScoreboardDisplaySlot[] var2 = ScoreboardDisplaySlot.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ScoreboardDisplaySlot scoreboardDisplaySlot = var2[var4];
         ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
         if (scoreboardObjective != null) {
            map.put(scoreboardDisplaySlot, scoreboardObjective.getName());
         }
      }

      return new Packed(this.scoreboard.getObjectives().stream().map(ScoreboardObjective::pack).toList(), this.scoreboard.pack(), map, this.scoreboard.getTeams().stream().map(Team::pack).toList());
   }

   public static record Packed(List objectives, List scores, Map displaySlots, List teams) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(ScoreboardObjective.Packed.CODEC.listOf().optionalFieldOf("Objectives", List.of()).forGetter(Packed::objectives), Scoreboard.PackedEntry.CODEC.listOf().optionalFieldOf("PlayerScores", List.of()).forGetter(Packed::scores), Codec.unboundedMap(ScoreboardDisplaySlot.CODEC, Codec.STRING).optionalFieldOf("DisplaySlots", Map.of()).forGetter(Packed::displaySlots), Team.Packed.CODEC.listOf().optionalFieldOf("Teams", List.of()).forGetter(Packed::teams)).apply(instance, Packed::new);
      });

      public Packed(List list, List list2, Map map, List list3) {
         this.objectives = list;
         this.scores = list2;
         this.displaySlots = map;
         this.teams = list3;
      }

      public List objectives() {
         return this.objectives;
      }

      public List scores() {
         return this.scores;
      }

      public Map displaySlots() {
         return this.displaySlots;
      }

      public List teams() {
         return this.teams;
      }
   }
}
