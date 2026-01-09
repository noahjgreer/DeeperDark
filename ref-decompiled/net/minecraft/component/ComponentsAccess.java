package net.minecraft.component;

import org.jetbrains.annotations.Nullable;

public interface ComponentsAccess {
   @Nullable
   Object get(ComponentType type);

   default Object getOrDefault(ComponentType type, Object fallback) {
      Object object = this.get(type);
      return object != null ? object : fallback;
   }

   @Nullable
   default Component getTyped(ComponentType type) {
      Object object = this.get(type);
      return object != null ? new Component(type, object) : null;
   }
}
