package net.minecraft.world.event.listener;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

public interface GameEventDispatcher {
   GameEventDispatcher EMPTY = new GameEventDispatcher() {
      public boolean isEmpty() {
         return true;
      }

      public void addListener(GameEventListener listener) {
      }

      public void removeListener(GameEventListener listener) {
      }

      public boolean dispatch(RegistryEntry event, Vec3d pos, GameEvent.Emitter emitter, DispatchCallback callback) {
         return false;
      }
   };

   boolean isEmpty();

   void addListener(GameEventListener listener);

   void removeListener(GameEventListener listener);

   boolean dispatch(RegistryEntry event, Vec3d pos, GameEvent.Emitter emitter, DispatchCallback callback);

   @FunctionalInterface
   public interface DispatchCallback {
      void visit(GameEventListener listener, Vec3d listenerPos);
   }
}
