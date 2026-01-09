package net.minecraft.server.network;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.UnmodifiableIterator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.GameRules;
import net.minecraft.world.waypoint.ServerWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.WaypointHandler;

public class ServerWaypointHandler implements WaypointHandler {
   private final Set waypoints = new HashSet();
   private final Set players = new HashSet();
   private final Table trackers = HashBasedTable.create();

   public void onTrack(ServerWaypoint serverWaypoint) {
      this.waypoints.add(serverWaypoint);
      Iterator var2 = this.players.iterator();

      while(var2.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
         this.refreshTracking(serverPlayerEntity, serverWaypoint);
      }

   }

   public void onUpdate(ServerWaypoint serverWaypoint) {
      if (this.waypoints.contains(serverWaypoint)) {
         Map map = Tables.transpose(this.trackers).row(serverWaypoint);
         Sets.SetView setView = Sets.difference(this.players, map.keySet());
         UnmodifiableIterator var4 = ImmutableSet.copyOf(map.entrySet()).iterator();

         while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            this.refreshTracking((ServerPlayerEntity)entry.getKey(), serverWaypoint, (ServerWaypoint.WaypointTracker)entry.getValue());
         }

         var4 = setView.iterator();

         while(var4.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
            this.refreshTracking(serverPlayerEntity, serverWaypoint);
         }

      }
   }

   public void onUntrack(ServerWaypoint serverWaypoint) {
      this.trackers.column(serverWaypoint).forEach((player, tracker) -> {
         tracker.untrack();
      });
      Tables.transpose(this.trackers).row(serverWaypoint).clear();
      this.waypoints.remove(serverWaypoint);
   }

   public void addPlayer(ServerPlayerEntity player) {
      this.players.add(player);
      Iterator var2 = this.waypoints.iterator();

      while(var2.hasNext()) {
         ServerWaypoint serverWaypoint = (ServerWaypoint)var2.next();
         this.refreshTracking(player, serverWaypoint);
      }

      if (player.hasWaypoint()) {
         this.onTrack((ServerWaypoint)player);
      }

   }

   public void updatePlayerPos(ServerPlayerEntity player) {
      Map map = this.trackers.row(player);
      Sets.SetView setView = Sets.difference(this.waypoints, map.keySet());
      UnmodifiableIterator var4 = ImmutableSet.copyOf(map.entrySet()).iterator();

      while(var4.hasNext()) {
         Map.Entry entry = (Map.Entry)var4.next();
         this.refreshTracking(player, (ServerWaypoint)entry.getKey(), (ServerWaypoint.WaypointTracker)entry.getValue());
      }

      var4 = setView.iterator();

      while(var4.hasNext()) {
         ServerWaypoint serverWaypoint = (ServerWaypoint)var4.next();
         this.refreshTracking(player, serverWaypoint);
      }

   }

   public void removePlayer(ServerPlayerEntity player) {
      this.trackers.row(player).values().removeIf((tracker) -> {
         tracker.untrack();
         return true;
      });
      this.onUntrack((ServerWaypoint)player);
      this.players.remove(player);
   }

   public void clear() {
      this.trackers.values().forEach(ServerWaypoint.WaypointTracker::untrack);
      this.trackers.clear();
   }

   public void refreshTracking(ServerWaypoint waypoint) {
      Iterator var2 = this.players.iterator();

      while(var2.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
         this.refreshTracking(serverPlayerEntity, waypoint);
      }

   }

   public Set getWaypoints() {
      return this.waypoints;
   }

   private static boolean isLocatorBarEnabled(ServerPlayerEntity player) {
      return player.getWorld().getServer().getGameRules().getBoolean(GameRules.LOCATOR_BAR);
   }

   private void refreshTracking(ServerPlayerEntity player, ServerWaypoint waypoint) {
      if (player != waypoint) {
         if (isLocatorBarEnabled(player)) {
            waypoint.createTracker(player).ifPresentOrElse((tracker) -> {
               this.trackers.put(player, waypoint, tracker);
               tracker.track();
            }, () -> {
               ServerWaypoint.WaypointTracker waypointTracker = (ServerWaypoint.WaypointTracker)this.trackers.remove(player, waypoint);
               if (waypointTracker != null) {
                  waypointTracker.untrack();
               }

            });
         }
      }
   }

   private void refreshTracking(ServerPlayerEntity player, ServerWaypoint waypoint, ServerWaypoint.WaypointTracker tracker) {
      if (player != waypoint) {
         if (isLocatorBarEnabled(player)) {
            if (!tracker.isInvalid()) {
               tracker.update();
            } else {
               waypoint.createTracker(player).ifPresentOrElse((newTracker) -> {
                  newTracker.track();
                  this.trackers.put(player, waypoint, newTracker);
               }, () -> {
                  tracker.untrack();
                  this.trackers.remove(player, waypoint);
               });
            }
         }
      }
   }

   // $FF: synthetic method
   public void onUntrack(final Waypoint waypoint) {
      this.onUntrack((ServerWaypoint)waypoint);
   }

   // $FF: synthetic method
   public void onTrack(final Waypoint waypoint) {
      this.onTrack((ServerWaypoint)waypoint);
   }
}
