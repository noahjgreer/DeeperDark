package net.minecraft.loot;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;

public interface ContainerComponentModifier {
   ComponentType getComponentType();

   Object getDefault();

   Object apply(Object component, Stream contents);

   Stream stream(Object component);

   default void apply(ItemStack stack, Object component, Stream contents) {
      Object object = stack.getOrDefault(this.getComponentType(), component);
      Object object2 = this.apply(object, contents);
      stack.set(this.getComponentType(), object2);
   }

   default void apply(ItemStack stack, Stream contents) {
      this.apply(stack, this.getDefault(), contents);
   }

   default void apply(ItemStack stack, UnaryOperator contentsOperator) {
      Object object = stack.get(this.getComponentType());
      if (object != null) {
         UnaryOperator unaryOperator = (contentStack) -> {
            if (contentStack.isEmpty()) {
               return contentStack;
            } else {
               ItemStack itemStack = (ItemStack)contentsOperator.apply(contentStack);
               itemStack.capCount(itemStack.getMaxCount());
               return itemStack;
            }
         };
         this.apply(stack, this.stream(object).map(unaryOperator));
      }

   }
}
