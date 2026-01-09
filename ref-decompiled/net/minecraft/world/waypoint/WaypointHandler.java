package net.minecraft.world.waypoint;

public interface WaypointHandler {
   void onTrack(Waypoint waypoint);

   void onUpdate(Waypoint waypoint);

   void onUntrack(Waypoint waypoint);
}
