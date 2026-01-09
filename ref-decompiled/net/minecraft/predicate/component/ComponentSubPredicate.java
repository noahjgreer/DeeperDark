package net.minecraft.predicate.component;

import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;

public interface ComponentSubPredicate extends ComponentPredicate {
   default boolean test(ComponentsAccess components) {
      Object object = components.get(this.getComponentType());
      return object != null && this.test(object);
   }

   ComponentType getComponentType();

   boolean test(Object component);
}
