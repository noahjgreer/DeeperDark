package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateType;
import org.jetbrains.annotations.Nullable;

public class ServerScoreboard extends Scoreboard {
   public static final PersistentStateType STATE_TYPE;
   private final MinecraftServer server;
   private final Set syncableObjectives = Sets.newHashSet();
   private final List updateListeners = Lists.newArrayList();

   public ServerScoreboard(MinecraftServer server) {
      this.server = server;
   }

   protected void updateScore(ScoreHolder scoreHolder, ScoreboardObjective objective, ScoreboardScore score) {
      super.updateScore(scoreHolder, objective, score);
      if (this.syncableObjectives.contains(objective)) {
         this.server.getPlayerManager().sendToAll(new ScoreboardScoreUpdateS2CPacket(scoreHolder.getNameForScoreboard(), objective.getName(), score.getScore(), Optional.ofNullable(score.getDisplayText()), Optional.ofNullable(score.getNumberFormat())));
      }

      this.runUpdateListeners();
   }

   protected void resetScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
      super.resetScore(scoreHolder, objective);
      this.runUpdateListeners();
   }

   public void onScoreHolderRemoved(ScoreHolder scoreHolder) {
      super.onScoreHolderRemoved(scoreHolder);
      this.server.getPlayerManager().sendToAll(new ScoreboardScoreResetS2CPacket(scoreHolder.getNameForScoreboard(), (String)null));
      this.runUpdateListeners();
   }

   public void onScoreRemoved(ScoreHolder scoreHolder, ScoreboardObjective objective) {
      super.onScoreRemoved(scoreHolder, objective);
      if (this.syncableObjectives.contains(objective)) {
         this.server.getPlayerManager().sendToAll(new ScoreboardScoreResetS2CPacket(scoreHolder.getNameForScoreboard(), objective.getName()));
      }

      this.runUpdateListeners();
   }

   public void setObjectiveSlot(ScoreboardDisplaySlot slot, @Nullable ScoreboardObjective objective) {
      ScoreboardObjective scoreboardObjective = this.getObjectiveForSlot(slot);
      super.setObjectiveSlot(slot, objective);
      if (scoreboardObjective != objective && scoreboardObjective != null) {
         if (this.countDisplaySlots(scoreboardObjective) > 0) {
            this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
         } else {
            this.stopSyncing(scoreboardObjective);
         }
      }

      if (objective != null) {
         if (this.syncableObjectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
         } else {
            this.startSyncing(objective);
         }
      }

      this.runUpdateListeners();
   }

   public boolean addScoreHolderToTeam(String scoreHolderName, Team team) {
      if (super.addScoreHolderToTeam(scoreHolderName, team)) {
         this.server.getPlayerManager().sendToAll(TeamS2CPacket.changePlayerTeam(team, scoreHolderName, TeamS2CPacket.Operation.ADD));
         this.refreshWaypointTrackingFor(scoreHolderName);
         this.runUpdateListeners();
         return true;
      } else {
         return false;
      }
   }

   public void removeScoreHolderFromTeam(String scoreHolderName, Team team) {
      super.removeScoreHolderFromTeam(scoreHolderName, team);
      this.server.getPlayerManager().sendToAll(TeamS2CPacket.changePlayerTeam(team, scoreHolderName, TeamS2CPacket.Operation.REMOVE));
      this.refreshWaypointTrackingFor(scoreHolderName);
      this.runUpdateListeners();
   }

   public void updateObjective(ScoreboardObjective objective) {
      super.updateObjective(objective);
      this.runUpdateListeners();
   }

   public void updateExistingObjective(ScoreboardObjective objective) {
      super.updateExistingObjective(objective);
      if (this.syncableObjectives.contains(objective)) {
         this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(objective, 2));
      }

      this.runUpdateListeners();
   }

   public void updateRemovedObjective(ScoreboardObjective objective) {
      super.updateRemovedObjective(objective);
      if (this.syncableObjectives.contains(objective)) {
         this.stopSyncing(objective);
      }

      this.runUpdateListeners();
   }

   public void updateScoreboardTeamAndPlayers(Team team) {
      super.updateScoreboardTeamAndPlayers(team);
      this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateTeam(team, true));
      this.runUpdateListeners();
   }

   public void updateScoreboardTeam(Team team) {
      super.updateScoreboardTeam(team);
      this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateTeam(team, false));
      this.refreshWaypointTrackingFor(team);
      this.runUpdateListeners();
   }

   public void updateRemovedTeam(Team team) {
      super.updateRemovedTeam(team);
      this.server.getPlayerManager().sendToAll(TeamS2CPacket.updateRemovedTeam(team));
      this.refreshWaypointTrackingFor(team);
      this.runUpdateListeners();
   }

   public void addUpdateListener(Runnable listener) {
      this.updateListeners.add(listener);
   }

   protected void runUpdateListeners() {
      Iterator var1 = this.updateListeners.iterator();

      while(var1.hasNext()) {
         Runnable runnable = (Runnable)var1.next();
         runnable.run();
      }

   }

   public List createChangePackets(ScoreboardObjective objective) {
      List list = Lists.newArrayList();
      list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 0));
      ScoreboardDisplaySlot[] var3 = ScoreboardDisplaySlot.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ScoreboardDisplaySlot scoreboardDisplaySlot = var3[var5];
         if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
            list.add(new ScoreboardDisplayS2CPacket(scoreboardDisplaySlot, objective));
         }
      }

      Iterator var7 = this.getScoreboardEntries(objective).iterator();

      while(var7.hasNext()) {
         ScoreboardEntry scoreboardEntry = (ScoreboardEntry)var7.next();
         list.add(new ScoreboardScoreUpdateS2CPacket(scoreboardEntry.owner(), objective.getName(), scoreboardEntry.value(), Optional.ofNullable(scoreboardEntry.display()), Optional.ofNullable(scoreboardEntry.numberFormatOverride())));
      }

      return list;
   }

   public void startSyncing(ScoreboardObjective objective) {
      List list = this.createChangePackets(objective);
      Iterator var3 = this.server.getPlayerManager().getPlayerList().iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Packet packet = (Packet)var5.next();
            serverPlayerEntity.networkHandler.sendPacket(packet);
         }
      }

      this.syncableObjectives.add(objective);
   }

   public List createRemovePackets(ScoreboardObjective objective) {
      List list = Lists.newArrayList();
      list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 1));
      ScoreboardDisplaySlot[] var3 = ScoreboardDisplaySlot.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ScoreboardDisplaySlot scoreboardDisplaySlot = var3[var5];
         if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
            list.add(new ScoreboardDisplayS2CPacket(scoreboardDisplaySlot, objective));
         }
      }

      return list;
   }

   public void stopSyncing(ScoreboardObjective objective) {
      List list = this.createRemovePackets(objective);
      Iterator var3 = this.server.getPlayerManager().getPlayerList().iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Packet packet = (Packet)var5.next();
            serverPlayerEntity.networkHandler.sendPacket(packet);
         }
      }

      this.syncableObjectives.remove(objective);
   }

   public int countDisplaySlots(ScoreboardObjective objective) {
      int i = 0;
      ScoreboardDisplaySlot[] var3 = ScoreboardDisplaySlot.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ScoreboardDisplaySlot scoreboardDisplaySlot = var3[var5];
         if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
            ++i;
         }
      }

      return i;
   }

   private ScoreboardState createState() {
      ScoreboardState scoreboardState = new ScoreboardState(this);
      Objects.requireNonNull(scoreboardState);
      this.addUpdateListener(scoreboardState::markDirty);
      return scoreboardState;
   }

   private ScoreboardState unpackState(ScoreboardState.Packed packedState) {
      ScoreboardState scoreboardState = this.createState();
      scoreboardState.unpack(packedState);
      return scoreboardState;
   }

   private void refreshWaypointTrackingFor(String playerName) {
      ServerPlayerEntity serverPlayerEntity = this.server.getPlayerManager().getPlayer(playerName);
      if (serverPlayerEntity != null) {
         ServerWorld var4 = serverPlayerEntity.getWorld();
         if (var4 instanceof ServerWorld) {
            var4.getWaypointHandler().refreshTracking(serverPlayerEntity);
         }
      }

   }

   private void refreshWaypointTrackingFor(Team team) {
      Iterator var2 = this.server.getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld serverWorld = (ServerWorld)var2.next();
         team.getPlayerList().stream().map((playerName) -> {
            return this.server.getPlayerManager().getPlayer(playerName);
         }).filter(Objects::nonNull).forEach((player) -> {
            serverWorld.getWaypointHandler().refreshTracking(player);
         });
      }

   }

   static {
      STATE_TYPE = new PersistentStateType("scoreboard", (context) -> {
         return context.getWorldOrThrow().getScoreboard().createState();
      }, (context) -> {
         ServerScoreboard serverScoreboard = context.getWorldOrThrow().getScoreboard();
         Codec var10000 = ScoreboardState.Packed.CODEC;
         Objects.requireNonNull(serverScoreboard);
         return var10000.xmap(serverScoreboard::unpackState, ScoreboardState::pack);
      }, DataFixTypes.SAVED_DATA_SCOREBOARD);
   }
}
