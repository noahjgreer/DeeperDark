package net.minecraft.component;

import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public interface ComponentHolder extends ComponentsAccess {
   ComponentMap getComponents();

   @Nullable
   default Object get(ComponentType type) {
      return this.getComponents().get(type);
   }

   default Stream streamAll(Class valueClass) {
      return this.getComponents().stream().map(Component::value).filter((value) -> {
         return valueClass.isAssignableFrom(value.getClass());
      }).map((value) -> {
         return value;
      });
   }

   default Object getOrDefault(ComponentType type, Object fallback) {
      return this.getComponents().getOrDefault(type, fallback);
   }

   default boolean contains(ComponentType type) {
      return this.getComponents().contains(type);
   }
}
