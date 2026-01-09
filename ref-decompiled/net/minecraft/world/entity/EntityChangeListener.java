package net.minecraft.world.entity;

import net.minecraft.entity.Entity;

public interface EntityChangeListener {
   EntityChangeListener NONE = new EntityChangeListener() {
      public void updateEntityPosition() {
      }

      public void remove(Entity.RemovalReason reason) {
      }
   };

   void updateEntityPosition();

   void remove(Entity.RemovalReason reason);
}
