package net.minecraft.client.world;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.TrackedWaypointHandler;
import net.minecraft.world.waypoint.Waypoint;

@Environment(EnvType.CLIENT)
public class ClientWaypointHandler implements TrackedWaypointHandler {
   private final Map waypoints = new ConcurrentHashMap();

   public void onTrack(TrackedWaypoint trackedWaypoint) {
      this.waypoints.put(trackedWaypoint.getSource(), trackedWaypoint);
   }

   public void onUpdate(TrackedWaypoint trackedWaypoint) {
      ((TrackedWaypoint)this.waypoints.get(trackedWaypoint.getSource())).handleUpdate(trackedWaypoint);
   }

   public void onUntrack(TrackedWaypoint trackedWaypoint) {
      this.waypoints.remove(trackedWaypoint.getSource());
   }

   public boolean hasWaypoint() {
      return !this.waypoints.isEmpty();
   }

   public void forEachWaypoint(Entity receiver, Consumer action) {
      this.waypoints.values().stream().sorted(Comparator.comparingDouble((waypoint) -> {
         return waypoint.squaredDistanceTo(receiver);
      }).reversed()).forEachOrdered(action);
   }

   // $FF: synthetic method
   public void onUntrack(final Waypoint waypoint) {
      this.onUntrack((TrackedWaypoint)waypoint);
   }

   // $FF: synthetic method
   public void onTrack(final Waypoint waypoint) {
      this.onTrack((TrackedWaypoint)waypoint);
   }
}
