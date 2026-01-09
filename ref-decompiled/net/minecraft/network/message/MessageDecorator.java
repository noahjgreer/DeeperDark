package net.minecraft.network.message;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface MessageDecorator {
   MessageDecorator NOOP = (sender, message) -> {
      return message;
   };

   Text decorate(@Nullable ServerPlayerEntity sender, Text message);
}
