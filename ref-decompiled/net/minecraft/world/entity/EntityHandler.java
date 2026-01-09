package net.minecraft.world.entity;

public interface EntityHandler {
   void create(Object entity);

   void destroy(Object entity);

   void startTicking(Object entity);

   void stopTicking(Object entity);

   void startTracking(Object entity);

   void stopTracking(Object entity);

   void updateLoadStatus(Object entity);
}
