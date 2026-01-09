package net.minecraft.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.util.Util;

public interface EntityCollisionHandler {
   EntityCollisionHandler DUMMY = new EntityCollisionHandler() {
      public void addEvent(CollisionEvent event) {
      }

      public void addPreCallback(CollisionEvent event, Consumer callback) {
      }

      public void addPostCallback(CollisionEvent event, Consumer callback) {
      }
   };

   void addEvent(CollisionEvent event);

   void addPreCallback(CollisionEvent event, Consumer callback);

   void addPostCallback(CollisionEvent event, Consumer callback);

   public static class Impl implements EntityCollisionHandler {
      private static final CollisionEvent[] ALL_EVENTS = CollisionEvent.values();
      private static final int INVALID_VERSION = -1;
      private final Set activeEvents = EnumSet.noneOf(CollisionEvent.class);
      private final Map preCallbacks = Util.mapEnum(CollisionEvent.class, (value) -> {
         return new ArrayList();
      });
      private final Map postCallbacks = Util.mapEnum(CollisionEvent.class, (value) -> {
         return new ArrayList();
      });
      private final List callbacks = new ArrayList();
      private int version = -1;

      public void updateIfNecessary(int version) {
         if (this.version != version) {
            this.version = version;
            this.update();
         }

      }

      public void runCallbacks(Entity entity) {
         this.update();
         Iterator var2 = this.callbacks.iterator();

         while(var2.hasNext()) {
            Consumer consumer = (Consumer)var2.next();
            if (!entity.isAlive()) {
               break;
            }

            consumer.accept(entity);
         }

         this.callbacks.clear();
         this.version = -1;
      }

      private void update() {
         CollisionEvent[] var1 = ALL_EVENTS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CollisionEvent collisionEvent = var1[var3];
            List list = (List)this.preCallbacks.get(collisionEvent);
            this.callbacks.addAll(list);
            list.clear();
            if (this.activeEvents.remove(collisionEvent)) {
               this.callbacks.add(collisionEvent.getAction());
            }

            List list2 = (List)this.postCallbacks.get(collisionEvent);
            this.callbacks.addAll(list2);
            list2.clear();
         }

      }

      public void addEvent(CollisionEvent event) {
         this.activeEvents.add(event);
      }

      public void addPreCallback(CollisionEvent event, Consumer callback) {
         ((List)this.preCallbacks.get(event)).add(callback);
      }

      public void addPostCallback(CollisionEvent event, Consumer callback) {
         ((List)this.postCallbacks.get(event)).add(callback);
      }
   }
}
